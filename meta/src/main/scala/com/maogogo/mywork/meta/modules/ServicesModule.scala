package com.maogogo.mywork.meta.modules

import com.maogogo.mywork.common.inject.MainModule
import com.maogogo.mywork.meta.engine.EngineServiceImpl
import com.maogogo.mywork.meta.service.MetaServiceDao
import com.maogogo.mywork.meta.service.MetaServiceImpl
import com.maogogo.mywork.thrift._
import com.typesafe.config.Config
import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import javax.inject.Inject
import com.github.racc.tscg.TypesafeConfig
import com.google.inject.{ Provides, Singleton }
import javax.inject.Named
import com.maogogo.mywork.common.modules.DataSourceModule

class ServicesModule(implicit val config: Config) extends MainModule { //with DataSourceModule {

  override def injectModule: Unit = {
    //bindSingleton[MetaServiceDao]
    bindSingleton[ConnectionBuilder]

    bindSingleton[EngineService.MethodPerEndpoint].to[EngineServiceImpl]
    bindSingleton[MetaService.MethodPerEndpoint].to[MetaServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"meta" -> injector.instance[MetaService.MethodPerEndpoint],
    s"engine" -> injector.instance[EngineService.MethodPerEndpoint])

}