/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api.db

import com.xite.api.model.ShortUrl

import scala.collection.immutable.Seq

/**
  * A base class for our database repository.
  *
  * @tparam F A higher kinded type which wraps the actual return value.
  */
trait Repository[F[_]] {

  /**
    * Load a ShortUrl from the database repository.
    *
    * @param token The short url token of ShortUrl
    * @return Short url and originial url is returned
    */
  def loadShortUrl(token: String): F[Seq[(String, String)]]

  /**
    * Load a ShortUrl from the database repository.
    *
    * @param originalUrl The original url of Short Url
    * @return Short url and originial url is returned
    */
  def findOriginalUrl(originalUrl: String): F[Seq[(String, String)]]

  /**
    * Save the given url with short url in the database.
    *
    * @param su A ShortUrl to be saved.
    * @return It returns if record saved or not
    */
  def saveShortUrl(su: ShortUrl): F[Int]
}
