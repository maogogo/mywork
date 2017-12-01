package com.maogogo.mywork.common.cache

import com.maogogo.mywork.common._
import com.twitter.util.Future

trait CacheAccesser[KS, VS] {
  def write(ks: KS, vs: VS, ttl: Long): Future[Any]
  def read(ks: KS): Future[Option[VS]]
  //def delete(ks: KS): Future[Any]
  def readSeq(ks: KS): Future[Seq[VS]]
  def zincBy(ks: KS, vs: VS): Future[Any]
}

class SimpleStringBytesCacher[T](val realm: String, val ttl: Int)(
  implicit
  val ks: KeySerializer[String, Bytes],
  implicit val vs: ValueSerializer[T, Bytes],
  implicit val accesser: CacheAccesser[Bytes, Bytes]) extends Cacher[String, Bytes, T, Bytes] {
}

final class Murmurhash128StringBytesKeySerializer extends KeySerializer[String, Bytes] {
  import com.google.common.hash.Hashing
  import com.google.common.base.Charsets
  def serialize(r: String, k: String) =
    Hashing.murmur3_128().hashString(s"${r}@${k}", Charsets.UTF_8).asBytes
}