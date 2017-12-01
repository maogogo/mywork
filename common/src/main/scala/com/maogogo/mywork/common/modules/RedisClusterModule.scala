//package com.maogogo.mywork.common.modules
//
//import com.google.inject.{ Provides, Singleton }
//import javax.inject.Inject
//import com.typesafe.config.Config
//import com.redis.cluster._
//
//trait RedisClusterModule { self =>
//
//  @Provides @Singleton
//  def provideRedisCient(@Inject() config: Config): RedisCluster = {
//
//    val hosts = config.getString("redis.host").split(",")
//    val posts = config.getString("redis.port").split(",")
//
//    val auth = config.getString("redis.auth") match {
//      case x if !x.isEmpty => Some(x)
//      case _ => None
//    }
//
//    val nodes = hosts.zip(posts).zipWithIndex.map { kv =>
//      ClusterNode(s"node${kv._2}", kv._1._1, kv._1._2.toInt, 0, 8, auth, 0)
//    }.toSeq
//
//    new RedisCluster(nodes: _*) {
//      val keyTag = Some(RegexKeyTag)
//    }
//  }
//
//}