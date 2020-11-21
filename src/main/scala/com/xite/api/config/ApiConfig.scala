/*
 *
 *                                No Copyright
 *
 */

package com.xite.api.config

import com.xite.api.{ NonEmptyString, PortNumber }
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The configuration for our HTTP API.
  *
  * @param host The hostname or ip address on which the service shall listen.
  * @param port The port number on which the service shall listen.
  */
final case class ApiConfig(host: NonEmptyString, port: PortNumber)

object ApiConfig {

  implicit val configReader: ConfigReader[ApiConfig] = deriveReader[ApiConfig]

}
