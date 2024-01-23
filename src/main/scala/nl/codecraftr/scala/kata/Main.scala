package nl.codecraftr.scala.kata

import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.HikariConfig
import doobie._
import doobie.hikari._
import doobie.implicits._
import doobie.implicits.javasql._
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import software.amazon.awssdk.auth.credentials.{
  AwsBasicCredentials,
  StaticCredentialsProvider
}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.rds.RdsClient

object Main extends IOApp with StrictLogging {
  private lazy val pooledTransactor = {
    for {
      dbConfig <- Resource.eval(IO(ConfigSource.default.loadOrThrow[DbConfig]))
      hikariConfig <- Resource.pure {
        val config = new HikariConfig()
        config.setDriverClassName("org.postgresql.Driver")
        config.setJdbcUrl(
          s"jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}"
        )
        config.setUsername(dbConfig.username)
        config.setPassword(dbConfig.password)
        config
      }
      xa <- HikariTransactor.fromHikariConfig[IO](hikariConfig)
    } yield xa
  }

  def run(args: List[String]): IO[ExitCode] = {
    val accessKeyId = args.head
    val accessKeySecret = args.last

    for {
      _ <- IO(logger.info("Starting RDS"))
      rdsClient = createRdsClient(accessKeyId, accessKeySecret)
      _ <- IO(describeClusters(rdsClient))
      _ <- IO(logger.info("Finished RDS"))
      _ <- IO(logger.info("Starting JDBC"))
      _ <- crud()
      _ <- IO(logger.info("Finished JDBC"))
      _ <- IO(rdsClient.close())
    } yield ExitCode.Success
  }

  private def createRdsClient(accessKeyId: String, accessKeySecret: String) =
    RdsClient.builder
      .region(Region.EU_WEST_1)
      .credentialsProvider(
        StaticCredentialsProvider.create(
          AwsBasicCredentials.create(
            accessKeyId,
            accessKeySecret
          )
        )
      )
      .build

  private def describeClusters(client: RdsClient): Unit = {
    val response = client.describeDBClusters()
    response
      .dbClusters()
      .forEach(cluster => logger.info(cluster.dbClusterIdentifier()))
  }

  private def crud() = {
    val drop = sql"drop table if exists todos".update.run

    val create =
      sql"create table if not exists todos (description TEXT NOT NULL, created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP)".update.run

    val insert =
      Update[Todo]("insert into todos (created_at, description) values (?, ?)")
        .updateMany(Todos.todos)

    pooledTransactor.use { xa =>
      val setup = for {
        _ <- drop.transact(xa)
        _ <- create.transact(xa)
        _ <- insert.transact(xa)
      } yield ()

      val select =
        sql"select created_at, description from todos order by created_at desc"
          .query[Todo]
          .stream
          .transact(xa)

      val output = select
        .evalTap { record =>
          IO(logger.info(record.toString))
        }
        .compile
        .drain

      for {
        _ <- setup
        _ <- output
      } yield ExitCode.Success
    }
  }
}
