/*
 *
 *                                No Copyright
 *
 */

package com.xite.api.db

import cats.effect.IO
import com.xite.api.{ DatabaseLogin, DatabasePassword, DatabaseUrl }
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway

/**
  * An implementation of the database migrator using Flyway and IO.
  */
final class FlywayDatabaseMigrator extends DatabaseMigrator[IO] {

  /**
    * Apply pending migrations to the database.
    *
    * @param url  A JDBC database connection url.
    * @param user The login name for the connection.
    * @param pass The password for the connection.
    * @return The number of applied migrations.
    */
  override def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): IO[Int] =
    IO {
      val flyway: Flyway =
        Flyway.configure().baselineOnMigrate(true).dataSource(url, user, pass).load()
      flyway.migrate()
    }

}
