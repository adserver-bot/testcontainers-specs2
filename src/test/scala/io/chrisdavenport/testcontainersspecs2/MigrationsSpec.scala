package io.chrisdavenport.testcontainersspecs2

import cats.effect._
import org.specs2.mutable.Specification
import org.flywaydb.core.Flyway
// import io.chrisdavenport.testcontainersspecs2.{ ForAllTestContainer, PostgresqlMultipleDatabases }

class MigrationsSpec extends Specification with ForAllTestContainer {

  implicit val CS: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)

  private[this] val multiple = new PostgresqlMultipleDatabases(
    name = "christopherdavenport/postgres-multi-db:10.3",
    exposedPort = 5432,
    dbName = dbName,
    dbUserName = dbUserName,
    dbPassword = dbPassword
  )
  // IMPORTANT: MUST BE LAZY VAL
  override lazy val container = multiple.container

  lazy val driverName = "org.postgresql.Driver"
  lazy val dbUserName = "user"
  lazy val dbPassword = "password"
  lazy val dbName = "db"
  lazy val jdbcUrl = multiple.jdbcUrl

  "Migrations should run Correctly" in {
    IO {
      Flyway
        .configure()
        .dataSource(jdbcUrl, dbUserName, dbPassword)
        .load()
        .migrate
    }.attempt
      .map(_.isRight)
      .unsafeRunSync() must_=== (true)
  }

}