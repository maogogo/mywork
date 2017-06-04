package com.maogogo.mywork.common.cache

import com.maogogo.mywork.common._
import javax.inject.Inject
import com.redis.cluster._
import com.redis.serialization.Parse
import com.twitter.util.Future
import com.redis._

final class RedisBinaryCacheAccesser @Inject() (client: RedisCluster) extends CacheAccesser[Bytes, Bytes] {

  implicit val parseByteArray = Parse[Bytes](x => x)

  def read(ks: Bytes): Future[Option[Bytes]] = {
    Future.value(client.get(ks))
  }

  def readSeq(ks: Bytes): Future[Seq[Bytes]] = {
    Future.value(client.lrange(ks, 0, -1).map(_.flatten).getOrElse(Seq.empty))
  }

  def write(ks: Bytes, vs: Seq[Bytes]): Future[Any] = {
    Future.value(client.set(ks, vs))
  }

  def write(ks: Bytes, vs: Bytes, ttl: Long): Future[Any] = {
    Future.value(client.set(ks, vs, true, Seconds(ttl)))
  }

  def zincBy(ks: Bytes, vs: Bytes): Future[Any] = {
    Future.value(client.zincrby(ks, 1.0, vs))
  }

}