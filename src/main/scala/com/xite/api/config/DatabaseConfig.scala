/*
 *
 *                                No Copyright
 *
 */

package com.xite.api.config

import com.xite.api.{ DatabaseLogin, DatabasePassword, DatabaseUrl, NonEmptyString }
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The configuration for our database connection.
  *
  * @param driver The class name of the driver to use.
  * @param url    The JDBC connection url (driver specific).
  * @param user   The username for the database connection.
  * @param pass   The password for the database connection.
  */
final case class DatabaseConfig(
    driver: NonEmptyString,
    url: DatabaseUrl,
    user: DatabaseLogin,
    pass: DatabasePassword
)

object DatabaseConfig {

  implicit val configReader: ConfigReader[DatabaseConfig] = deriveReader[DatabaseConfig]

}
