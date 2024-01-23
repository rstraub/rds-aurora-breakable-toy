package nl.codecraftr.scala.kata

import pureconfig._
import pureconfig.generic.auto._

case class DbConfig(
    host: String,
    port: Int,
    username: String,
    password: String,
    database: String
)
