package com.maogogo.mywork.common.utils

import com.maogogo.mywork.common._
import com.google.common.hash.Hashing
import com.google.common.base.Charsets

object HashUtil {

  def hashing(bs: Bytes, str: Option[String]): String = {
    val _str = str match {
      case Some(x) => Hashing.murmur3_32.hashString(x, Charsets.UTF_8)
      case _ => ""
    }
    Hashing.murmur3_128().hashBytes(bs).toString + _str
  }

  def hashing(bs: Array[Byte]): String = hashing(bs, None)

}