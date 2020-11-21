/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api.routes

import cats._
import cats.effect._
import com.xite.api.BaseSpec
import com.xite.api.db._
import com.xite.api.model.ShortUrl
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router
import com.xite.api.db._

import scala.collection.immutable.Seq

/* imransarwar created on 20/11/2020*/
final class UrlRouteTest extends BaseSpec {
  implicit def decodeShortUrl: EntityDecoder[IO, ShortUrl]                   = jsonOf
  implicit def decodeShortUrls: EntityDecoder[IO, List[ShortUrl]]            = jsonOf
  implicit def encodeShortUrl[A[_]: Applicative]: EntityEncoder[A, ShortUrl] = jsonEncoderOf

  private val emptyRepository: Repository[IO] = new TestRepository[IO](Seq.empty)

  "ProductRoutes" when {
    "GET /shorturl/ID" when {
      "short url does not exist" must {
        val expectedStatusCode = Status.NotFound

        s"return $expectedStatusCode" in {
          forAll("shortUrl") { token: String =>
            Uri.fromString("/shorturl/" + token) match {
              case Left(_) => fail("Could not generate valid URI!")
              case Right(u) =>
                def service: HttpRoutes[IO] =
                  Router("/" -> new UrlRoute(emptyRepository).routes)
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.GET, uri = u)
                )
                val result = response.unsafeRunSync
                result.status must be(expectedStatusCode)
                result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }
      }
      "url exists" must {
        val expectedStatusCode = Status.PermanentRedirect

        s"return $expectedStatusCode and the short url" in {
          forAll("shortUrl") { p: ShortUrl =>
            Uri.fromString("/shorturl/" + p.shortUrl) match {
              case Left(_) => fail("Could not generate valid URI!")
              case Right(u) =>
                val repo: Repository[IO] = new TestRepository[IO](Seq(p))
                def service: HttpRoutes[IO] =
                  Router("/" -> new UrlRoute(repo).routes)
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.GET, uri = u)
                )
                val result = response.unsafeRunSync
                result.status must be(expectedStatusCode)
            }
          }
        }
      }
      "GET /shorturls" when {
        "no urls exist" must {
          val expectedStatusCode = Status.Ok

          s"return $expectedStatusCode and an empty list" in {
            def service: HttpRoutes[IO] =
              Router("/" -> new UrlRoute(emptyRepository).routes)
            val response: IO[Response[IO]] = service.orNotFound.run(
              Request(method = Method.GET, uri = uri"/shorturls")
            )
            val result = response.unsafeRunSync
            result.status must be(expectedStatusCode)
            result.as[List[ShortUrl]].unsafeRunSync mustEqual List.empty[ShortUrl]
          }
        }

        "urls exist" must {
          val expectedStatusCode = Status.Ok

          s"return $expectedStatusCode and a list of short urls" in {
            forAll("shortUrl") { ps: List[ShortUrl] =>
              val repo: Repository[IO] = new TestRepository[IO](ps)
              def service: HttpRoutes[IO] =
                Router("/" -> new UrlRoute(repo).routes)
              val response: IO[Response[IO]] = service.orNotFound.run(
                Request(method = Method.GET, uri = Uri.uri("/shorturls"))
              )
              val result = response.unsafeRunSync
              result.status must be(expectedStatusCode)
//              result.as[List[ShortUrl]].map(x => x.map(y => ShortUrl(y.shortUrl.split("/").last, y.originalUrl))).unsafeRunSync mustEqual ps
            }
          }
        }
      }
    }
  }
}
