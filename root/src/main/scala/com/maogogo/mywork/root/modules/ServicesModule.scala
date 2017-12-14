package com.maogogo.mywork.root.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules._
import com.maogogo.mywork.thrift._
import com.google.inject.{ Provides, Singleton }
import javax.inject.{ Inject, Named }
import com.typesafe.config.Config
import scala.collection.JavaConversions._
import com.maogogo.mywork.root.service.RootServiceImpl
import scala.collection.JavaConverters._

trait ServicesModule extends TwitterModule with BaseConfigModule {

  override def configure: Unit = {
    bindSingleton[EngineService.MethodPerEndpoint].toInstance(zookClient[EngineService.MethodPerEndpoint]("engine"))
    bindSingleton[MetaService.MethodPerEndpoint].toInstance(zookClient[MetaService.MethodPerEndpoint]("meta"))
    bindSingleton[RootService.MethodPerEndpoint].to[RootServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"root" -> injector.instance[RootService.MethodPerEndpoint])

  @Provides @Singleton @Named("MergerServers")
  def provideServers(@Inject() config: Config): Seq[MergerService.MethodPerEndpoint] = {
    config.getObject(s"${RPC.rpcClientPrefix}mergers").map {
      case (k, v) =>
        Log.info(s"get MergerService @ ${k}")
        provideClient[MergerService.MethodPerEndpoint](v.unwrapped.toString)
    }.toSeq
  }

}

object ServicesModule extends ServicesModule