import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.17" % Test
  lazy val scalaCheck =
    "org.scalatestplus" %% "scalacheck-1-17" % "3.2.17.0" % Test
  lazy val mockito = "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % Test
  lazy val catsCore = "org.typelevel" %% "cats-core" % "2.10.0"

  lazy val aws = "software.amazon.awssdk" % "rds" % "2.23.7"
}
