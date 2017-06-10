package com.maogogo.mywork.common.cache

import org.slf4j.LoggerFactory
import com.twitter.util.Future

trait KeySerializer[K, KS] {
  def serialize(realm: String, k: K): KS
}

trait ValueSerializer[V, VS] {
  def serialize(v: V): VS
  def deserialize(vs: VS): V
}

trait Cacher[K, KS, V, VS] {
  val realm: String
  val ttl: Int
  val ks: KeySerializer[K, KS]
  val vs: ValueSerializer[V, VS]
  val accesser: CacheAccesser[KS, VS]

  lazy val log = LoggerFactory.getLogger(this.getClass)

  def get(key: K) = accesser.read(ks.serialize(realm, key)).map(_.map(vs.deserialize))
  def getSeq(key: K) = accesser.readSeq(ks.serialize(realm, key)).map(_.map(vs.deserialize))

  def getOrElse(key: K)(fallback: => Future[Option[V]]): Future[Option[V]] = {
    for {
      cachedOption <- get(key)
      cacheMissed = cachedOption.isEmpty
      _ = if (cacheMissed) log.info(s"Cache missed for key: ${key}") else log.info(s"Cache found for key: ${key}")
      fallbackOption <- if (cacheMissed) fallback else Future.value(cachedOption)
      recache = cacheMissed && fallbackOption.isDefined
      _ <- if (recache) put(key, fallbackOption.get) else Future.Unit
    } yield fallbackOption
  }

  def put(key: K, value: V) = accesser.write(ks.serialize(realm, key), vs.serialize(value), ttl)
  def put(key: K, value: V, ttl: Long) = accesser.write(ks.serialize(realm, key), vs.serialize(value), ttl)
  //def delete(key: K) = accesser.delete(ks.serialize(realm, key))
  def zincBy(key: K, member: V) = accesser.zincBy(ks.serialize(realm, key), vs.serialize(member))

}