/*
 *
 *                                No Copyright
 *                      
 */

package com.xite.api

import com.xite.api.model.ShortUrl
import org.scalacheck.{ Arbitrary, Gen }

import scala.collection.immutable.List

package object db {
  val genToken: Gen[String]       = Gen.oneOf(List("ABC", "DEF", "HIJ"))
  val genOriginalUrl: Gen[String] = Gen.oneOf(List("MNO", "PQR", "STU"))

  implicit val arbitraryToken: Arbitrary[String] = Arbitrary(genToken)

  val genShortUrl: Gen[ShortUrl] = for {
    id <- genToken
    ts <- genOriginalUrl
  } yield ShortUrl(
    shortUrl = id,
    originalUrl = ts
  )

  implicit val arbitraryShortUrl: Arbitrary[ShortUrl]        = Arbitrary(genShortUrl)
  val genShortUrls: Gen[List[ShortUrl]]                      = Gen.nonEmptyListOf(genShortUrl)
  implicit val arbitraryShortUrls: Arbitrary[List[ShortUrl]] = Arbitrary(genShortUrls)
}
