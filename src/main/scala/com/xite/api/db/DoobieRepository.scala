/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api.db

import cats.effect.Sync
import com.xite.api.model.ShortUrl
import com.xite.api.util.UrlHashing
import doobie.Transactor
import doobie.implicits._

import scala.collection.immutable._

/* imransarwar created on 18/11/2020*/
final class DoobieRepository[F[_]: Sync](tx: Transactor[F]) extends Repository[F] {

  /**
    * Load a ShortUrl from the database repository.
    *
    * @param token The short url token of the ShortUrl
    * @return Short url and originial url is returned
    */
  override def loadShortUrl(token: String): F[Seq[(String, String)]] =
    sql"""SELECT short_url, original_url
          FROM urls
          WHERE short_url = $token"""
      .query[(String, String)]
      .to[Seq]
      .transact(tx)

  /**
    * Load a ShortUrl from the database repository.
    *
    * @param originalUrl The original Url of the ShortUrl.
    * @return Short url and originial url is returned
    */
  override def findOriginalUrl(originalUrl: String): F[Seq[(String, String)]] =
    sql"""SELECT short_url, original_url
          FROM urls
          WHERE original_url = $originalUrl"""
      .query[(String, String)]
      .to[Seq]
      .transact(tx)

  /**
    * Save the given ShortUrl in the database.
    *
    * @param su A ShortUrl to be saved.
    * @return The number of affected database rows urls
    */
  override def saveShortUrl(su: ShortUrl): F[Int] = {
    val hashedToken = UrlHashing.MurmurHash.hash(su.originalUrl)
    sql"INSERT INTO urls (short_url, original_url) VALUES (${hashedToken}, ${su.originalUrl})".update.run
      .transact(tx)
  }
}
