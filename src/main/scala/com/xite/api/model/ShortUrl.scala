/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api.model

import io.circe._
import io.circe.generic.semiauto._

/* imransarwar created on 17/11/2020*/
final case class ShortUrl(shortUrl: String, originalUrl: String)

object ShortUrl {
  implicit val decode: Decoder[ShortUrl] = deriveDecoder[ShortUrl]
  implicit val encode: Encoder[ShortUrl] = deriveEncoder[ShortUrl]

  /**
    * Try to create a Short Url from the given list of database rows.
    *
    * @param rows The database rows describing a short url.
    * @return An option to the successfully created ShortUrl.
    */
  def fromDatabase(rows: Seq[(String, String)]): Option[ShortUrl] =
    for {
      (token, orig) <- rows.headOption
      u = ShortUrl(shortUrl = s"http://127.0.0.1:53248/shorturl/${token}", originalUrl = orig)
    } yield u
}
