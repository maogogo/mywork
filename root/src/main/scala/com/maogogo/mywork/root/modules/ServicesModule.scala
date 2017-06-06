package com.maogogo.mywork.root.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules._
import com.maogogo.mywork.thrift._
import com.google.inject.{ Provides, Singleton }
import javax.inject.{ Inject, Named }
import com.typesafe.config.Config
import scala.collection.JavaConversions._
import com.maogogo.mywork.root.service.RootServiceImpl

trait ServicesModule extends TwitterModule with ConfigModule with RedisClusterModule {

  override def configure: Unit = {
    bindSingleton[EngineService.FutureIface].toInstance(zookClient[EngineService.FutureIface]("engine"))
    bindSingleton[MetaService.FutureIface].toInstance(zookClient[MetaService.FutureIface]("meta"))
    bindSingleton[RootService.FutureIface].to[RootServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"root" -> injector.instance[RootService.FutureIface]
  )

  //  @Provides @Singleton @Named("MergerServers")
  //  def provideServers(@Inject() config: Config): Seq[MergerService.FutureIface] = {
  //    config.getObject("rpc.client.mergers").map { t =>
  //      provideClient[MergerService.FutureIface](t._2.unwrapped().toString)
  //    }.toSeq
  //  }

}

object ServicesModule extends ServicesModule