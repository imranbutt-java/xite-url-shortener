/*
 *
 *                                No Copyright
 *
 */

package com.xite.api

import cats.effect._
import cats.implicits._
import com.typesafe.config._
import com.xite.api.routes._
import com.xite.api.config._
import com.xite.api.db._
import doobie._
import eu.timepit.refined.auto._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import pureconfig._

import scala.io.StdIn

object Server extends IOApp {

  @SuppressWarnings(
    Array(
      "org.wartremover.warts.Any",
      "scalafix:DisableSyntax.null"
    )
  )
  def run(args: List[String]): IO[ExitCode] = {
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val program = for {
      (apiConfig, dbConfig) <- IO {
        val cfg = ConfigFactory.load(getClass().getClassLoader())
        // TODO Think about alternatives to `Throw`.
        (
          loadConfigOrThrow[ApiConfig](cfg, "api"),
          loadConfigOrThrow[DatabaseConfig](cfg, "database")
        )
      }
      ms <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
      tx = Transactor
        .fromDriverManager[IO](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass)
      repo      = new DoobieRepository(tx)
      urlRoutes = new UrlRoute(repo)
      httpApp   = Router("/" -> urlRoutes.routes).orNotFound
      server    = BlazeServerBuilder[IO].bindHttp(apiConfig.port, apiConfig.host).withHttpApp(httpApp)
      fiber     = server.resource.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)
    } yield fiber
    program.attempt.unsafeRunSync match {
      case Left(e) =>
        IO {
          println("*** An error occured! ***")
          if (e ne null) {
            println(e.getMessage)
          }
          ExitCode.Error
        }
      case Right(r) => r
    }
  }
}
