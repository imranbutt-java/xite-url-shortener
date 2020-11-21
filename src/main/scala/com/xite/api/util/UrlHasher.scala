/*
 *
 *                                No Copyright
 *
 */

package com.xite.api.util

import scala.util.Random
import scala.util.hashing.MurmurHash3

trait UrlHashing {
  def hash(value: String): String
}

object UrlHashing {
  implicit val RandomBase36: UrlHashing = (_: String) =>
    Integer.toString(new Random().nextInt(Integer.MAX_VALUE), 36)

  implicit val MurmurHash: UrlHashing = (value: String) =>
    Integer.toString(MurmurHash3.stringHash(value), 36)
}
