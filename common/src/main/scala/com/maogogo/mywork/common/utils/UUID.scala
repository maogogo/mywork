package com.maogogo.mywork.common.utils

import com.maogogo.mywork.common._
import com.google.common.hash.Hashing
import com.google.common.base.Charsets

object UUID {

  def uuid(): String = {
    Hashing.murmur3_32.hashString(java.util.UUID.randomUUID().toString, Charsets.UTF_8) + "-" +
      Hashing.murmur3_32.hashString(java.util.UUID.randomUUID().toString, Charsets.UTF_8)
  }

  def simpleId: String = Hashing.murmur3_32.hashString(java.util.UUID.randomUUID().toString, Charsets.UTF_8).toString()

  def hashing(bytes: Bytes): String = {
    val s = Hashing.murmur3_128.hashBytes(bytes).asLong()
    if (s < 0) (s * -1).toString() else s.toString()
  }

}