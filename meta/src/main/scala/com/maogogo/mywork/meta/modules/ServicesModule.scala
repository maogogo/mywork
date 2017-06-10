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
import com.maogogo.mywork.meta.dao.MetaServiceDao
import com.maogogo.mywork.common.cache._
import com.maogogo.mywork.meta.dao.MetaServiceCacheData

trait ServicesModule extends TwitterModule with ConfigModule with DataSourceModule with RedisClusterModule {

  override def configure: Unit = {
    bindSingleton[RedisBinaryCacheAccesser]

    bindSingleton[MetaServiceDao]
    bindSingleton[MetaServiceCacheData]

    bindSingleton[EngineService.FutureIface].to[EngineServiceImpl]
    bindSingleton[MetaService.FutureIface].to[MetaServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"meta" -> injector.instance[MetaService.FutureIface],
    s"engine" -> injector.instance[EngineService.FutureIface]
  )

}

object ServicesModule extends ServicesModule