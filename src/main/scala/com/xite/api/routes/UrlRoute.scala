/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api.routes

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import com.xite.api.db.Repository
import com.xite.api.model.ShortUrl
import fs2.Stream
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.{io, _}
import org.http4s.headers.Location

/* imransarwar created on 18/11/2020*/
class UrlRoute[F[_]: Sync](repo: Repository[F]) extends Http4sDsl[F] {
  implicit def decodeShortUrl: EntityDecoder[F, ShortUrl]                    = jsonOf
  implicit def encodeShortUrl[A[_]: Applicative]: EntityEncoder[A, ShortUrl] = jsonEncoderOf

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "shorturl" / id =>
      for {
        rows <- repo.loadShortUrl(id)
        resp <- ShortUrl.fromDatabase(rows).fold(NotFound())(p => PermanentRedirect(Location(Uri(path=s"${p.originalUrl}"))))
      } yield resp
    case GET -> Root / "shorturls" =>
      val prefix = Stream.eval("[".pure[F])
      val suffix = Stream.eval("]".pure[F])
      val ps = repo.loadUrls
        .groupAdjacentBy(_._1)
        .map {
          case (id, rows) => ShortUrl.fromDatabase(rows.toList)
        }
        .collect {
          case Some(p) => p
        }
        .map(_.asJson.noSpaces)
        .intersperse(",")
      @SuppressWarnings(Array("org.wartremover.warts.Any"))
      val result: Stream[F, String] = prefix ++ ps ++ suffix
      Ok(result)
    case req @ POST -> Root / "shorturl" =>
      req
        .as[ShortUrl]
        .flatMap { p =>
          for {
            cnt <- repo.saveShortUrl(p)
            res <- cnt match {
              case 0 => InternalServerError()
              case _ => {
                for {
                  x    <- repo.findOriginalUrl(p.originalUrl)
                  resp <- ShortUrl.fromDatabase(x).fold(NotFound())(p => Ok(p))
                } yield resp
              }
            }
          } yield res
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }
  }

}
