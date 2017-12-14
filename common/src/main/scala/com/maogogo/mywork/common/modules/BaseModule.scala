package com.maogogo.mywork.common.modules

import com.twitter.finagle.ThriftMux
import com.typesafe.config.Config
import com.twitter.scrooge.ThriftService

trait BaseModule { self =>
  //  lazy val Log = LoggerFactory.getLogger(getClass)
  //
  def provideServices(injector: com.twitter.inject.Injector): Map[String, ThriftService]
  //
  def services(injector: com.twitter.inject.Injector)(implicit config: Config) = {
    val rand = new scala.util.Random
    //val config = injector.instance[Config]
    //val filter = new HandlerFilter[com.twitter.finagle.mux.Request, com.twitter.finagle.mux.Response]
    provideServices(injector).map { kv =>
      val (name, service) = kv
      val label = s"${RPC.serverPrefix}${name}"
      val anounce = s"zk!${config.getString(label)}!" + Math.abs(rand.nextInt)
      ThriftMux.server.serveIface(s":*", service).announce(anounce)
    }.toSeq
  }
  //
  def zookClient[T: Manifest](s: String)(implicit config: Config) = {
    provideClient(config.getString(s"${RPC.clientPrefix}${s}"))
  }

  def provideClient[T: Manifest](path: String) = ThriftMux.client.newIface[T](s"zk2!${path}")

  def provideClients[T: Manifest](paths: Seq[(String, String)]): Seq[T] =
    paths.toSeq.map {
      case (_, v) => provideClient[T](v)
    }

}