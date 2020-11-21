/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api.db

import cats.effect.Effect
import cats.implicits._
import com.xite.api.model.ShortUrl
import fs2.Stream

import scala.collection.immutable.Seq

/* imransarwar created on 20/11/2020*/
class TestRepository[F[_]: Effect](data: Seq[ShortUrl]) extends Repository[F] {
  override def loadShortUrl(token: String): F[Seq[(String, String)]] =
    data.find(_.shortUrl === token) match {
      case None    => Seq.empty.pure[F]
      case Some(p) => Seq((p.shortUrl, p.originalUrl)).pure[F]
    }

  override def findOriginalUrl(originalUrl: String): F[Seq[(String, String)]] =
    data.find(_.originalUrl === originalUrl) match {
      case None    => Seq.empty.pure[F]
      case Some(p) => Seq((p.shortUrl, p.originalUrl)).pure[F]
    }

  override def loadUrls(): Stream[F, (String, String)] = {
    val rows = data.flatMap(p => Seq((p.shortUrl, p.originalUrl)))
    Stream.emits(rows)
  }

  override def saveShortUrl(p: ShortUrl): F[Int] =
    data.find(_.shortUrl === p.shortUrl).fold(0.pure[F])(_ => 1.pure[F])
}
