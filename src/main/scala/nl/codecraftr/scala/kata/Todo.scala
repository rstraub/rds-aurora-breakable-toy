package nl.codecraftr.scala.kata

import java.sql.Timestamp
import java.time.LocalDateTime

case class Todo(
    createdAt: Timestamp,
    description: String
)

object Todos {
  lazy val todos: List[Todo] = List(
    Todo(Timestamp.valueOf(LocalDateTime.now()), "Implement the kata"),
    Todo(
      Timestamp.valueOf(LocalDateTime.now().plusDays(1)),
      "Write a blog post about the kata"
    ),
    Todo(
      Timestamp.valueOf(LocalDateTime.now().plusDays(2)),
      "Publish the blog post"
    )
  )
}
