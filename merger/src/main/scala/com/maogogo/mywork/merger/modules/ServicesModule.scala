package com.maogogo.mywork.merger.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules.ConfigModule
import org.slf4j.LoggerFactory
import com.maogogo.mywork.thrift._
import com.google.inject.{ Provides, Singleton }
import javax.inject.{ Inject, Named }
import com.typesafe.config.Config
import scala.collection.JavaConversions._
import com.twitter.finagle.ThriftMux
import com.maogogo.mywork.merger.service.MergerServiceImpl
import com.maogogo.mywork.common.dispatch.LeafingDispatchImpl

trait ServicesModule extends TwitterModule with ConfigModule {

  lazy val log = LoggerFactory.getLogger(this.getClass)

  def zookClient(s: String) = s"zk2!${s}"

  override def configure: Unit = {
    bindSingleton[LeafService.FutureIface].to[LeafingDispatchImpl]
    bindSingleton[MergerService.FutureIface].to[MergerServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"merger" -> injector.instance[MergerService.FutureIface])

  @Provides @Singleton @Named("LeafServers")
  def provideServers(@Inject() config: Config): Seq[LeafService.FutureIface] = {
    config.getObject(s"${RpcClientPrefix}leafs").map { t =>
      log.info(s"get LeafService @ ${t._2.render}")
      ThriftMux.client.newIface[LeafService.FutureIface](zookClient(t._2.unwrapped().toString()))
    }.toSeq
  }

}

object ServicesModule extends ServicesModule