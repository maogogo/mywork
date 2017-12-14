package com.maogogo.mywork.meta.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules._
import com.maogogo.mywork.thrift._
import com.google.inject.{ Provides, Singleton }
import javax.inject.{ Inject, Named }
import com.typesafe.config.Config
import scala.collection.JavaConversions._
import com.maogogo.mywork.meta.engine.EngineServiceImpl
import com.maogogo.mywork.meta.service.MetaServiceImpl
import com.maogogo.mywork.common.cache._
import com.maogogo.mywork.meta.service.MetaServiceDao

trait ServicesModule extends TwitterModule with BaseConfigModule with DataSourceModule {

  override def configure: Unit = {
    bindSingleton[MetaServiceDao]
    //bindSingleton[MetaServiceCacheData]

    bindSingleton[EngineService.MethodPerEndpoint].to[EngineServiceImpl]
    bindSingleton[MetaService.MethodPerEndpoint].to[MetaServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"meta" -> injector.instance[MetaService.MethodPerEndpoint],
    s"engine" -> injector.instance[EngineService.MethodPerEndpoint])

}

object ServicesModule extends ServicesModule