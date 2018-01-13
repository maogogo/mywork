package com.maogogo.mywork.meta.modules

import com.maogogo.mywork.common.inject.MainModule
import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import com.maogogo.mywork.meta.service._
import com.maogogo.mywork.thrift._
import com.typesafe.config.Config
import com.maogogo.mywork.common.modules.MySqlDataSourceModule

class ServicesModule(implicit val config: Config) extends MainModule {

  override def injectModule: Unit = {

    install(new MySqlDataSourceModule)

    bindSingleton[EngineService.MethodPerEndpoint].to[EngineServiceImpl]
    bindSingleton[MetaService.MethodPerEndpoint].to[MetaServiceImpl]

  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"meta" -> injector.instance[MetaService.MethodPerEndpoint],
    s"engine" -> injector.instance[EngineService.MethodPerEndpoint])

}