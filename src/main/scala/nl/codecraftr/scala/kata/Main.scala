package nl.codecraftr.scala.kata

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO}
import doobie._
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
import com.typesafe.scalalogging.StrictLogging

object Main extends App with StrictLogging {
  private def run(accessKeyId: String, accessKeySecret: String): Unit = {
    val rdsClient = createRdsClient(accessKeyId, accessKeySecret)

    describeClusters(rdsClient)

    val dbConfig = ConfigSource.default.loadOrThrow[DbConfig]
    crud(dbConfig).unsafeRunSync()

    rdsClient.close()
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

  private def crud(dbConfig: DbConfig) = {
    val xa = Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver", // driver classname
      url =
        s"jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}", // connect URL (driver-specific)
      user = dbConfig.username, // user
      password = dbConfig.password, // password
      logHandler = None
    )

    val drop = sql"drop table if exists todos".update.run

    val create =
      sql"create table if not exists todos (description TEXT NOT NULL, created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP)".update.run

    val insert =
      Update[Todo]("insert into todos (created_at, description) values (?, ?)")
        .updateMany(Todos.todos)

    val setup = for {
      _ <- drop.transact(xa)
      _ <- create.transact(xa)
      _ <- insert.transact(xa)
    } yield ()

    val select =
      sql"select created_at, description from todos"
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

  run(args(0), args(1))
}
