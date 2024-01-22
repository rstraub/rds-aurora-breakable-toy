package nl.codecraftr.scala.kata

object Main extends App {
  def hello(name: String): String = name match {
    case "dev" => "Hello, creator!"
    case _     => "Hello, world!"
  }

  println(hello("dev"))
}
