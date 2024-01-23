package nl.codecraftr.scala.kata

import software.amazon.awssdk.auth.credentials.{
  AwsBasicCredentials,
  AwsCredentialsProvider,
  DefaultCredentialsProvider,
  StaticCredentialsProvider
}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.rds.RdsClient

import java.sql.DriverManager
import java.util.UUID

object Main extends App {
  private def run(accessKeyId: String, accessKeySecret: String): Unit = {
    val rdsClient = RdsClient.builder
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

    describeClusters(rdsClient)
    insertTodos()
    rdsClient.close()
  }

  private def describeClusters(client: RdsClient): Unit = {
    val response = client.describeDBClusters()
    response
      .dbClusters()
      .forEach(cluster => println(cluster.dbClusterIdentifier()))
  }

  private def insertTodos(): Unit = {
    val conn = DriverManager.getConnection(
      "jdbc:postgresql://aurora-demo.cluster-cncgooey2wlj.eu-west-1.rds.amazonaws.com:5432/postgres",
      "postgres",
      "password"
    )

    // Create table
    val statement = conn.createStatement
    val createSql =
      "CREATE TABLE IF NOT EXISTS todos (id SERIAL PRIMARY KEY, content VARCHAR(80))"
    statement.executeUpdate(createSql)

    // Insert data
    val preparedStatement =
      conn.prepareStatement("INSERT INTO todos (content) VALUES (?)")
    val content: String = "" + UUID.randomUUID
    preparedStatement.setString(1, content)
    preparedStatement.executeUpdate

    // Read data
    val read: String = "SELECT  count(*) as count FROM todos"
    val resultSet = statement.executeQuery(read)
    while (resultSet.next) {
      val count: String = resultSet.getString("count")
      println("Total Records: " + count)
    }
  }

  run(args(0), args(1))
}
