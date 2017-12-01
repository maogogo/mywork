package com.maogogo.mywork.leaf.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules._
import org.slf4j.LoggerFactory
import com.maogogo.mywork.thrift._
import com.google.inject.{ Provides, Singleton }
import javax.inject.{ Inject, Named }
import com.typesafe.config.Config

trait ServicesModule extends TwitterModule with BaseConfigModule with DataSourceModule {
  //  lazy val log = LoggerFactory.getLogger(this.getClass)

  override def configure: Unit = {
    //    bindSingleton[MetaService.MethodPerEndpoint].toInstance(zookClient[MetaService.MethodPerEndpoint]("meta"))
    //    bindSingleton[MetaDataConfig]
    //    bindSingleton[LeafServiceDao]
    //    bindSingleton[LeafService.MethodPerEndpoint].to[LeafServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"leaf" -> injector.instance[LeafService.MethodPerEndpoint])

}

object ServicesModule extends ServicesModule