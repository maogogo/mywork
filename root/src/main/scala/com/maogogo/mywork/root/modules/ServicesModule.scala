package com.maogogo.mywork.root.modules

import scala.collection.JavaConversions._

import com.github.racc.tscg.TypesafeConfig
import com.github.racc.tscg.TypesafeConfigModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.maogogo.mywork.common.inject.MainModule
import com.maogogo.mywork.root.service.RootServiceImpl
import com.maogogo.mywork.thrift._
import com.typesafe.config.Config

import javax.inject.Inject
import javax.inject.Named

class ServicesModule(implicit val config: Config) extends MainModule {

  override def injectModule: Unit = {
    bindSingleton[RootService.MethodPerEndpoint].to[RootServiceImpl]
    bindSingleton[MetaService.MethodPerEndpoint].toInstance(zookClient[MetaService.MethodPerEndpoint]("meta"))
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"root" -> injector.instance[RootService.MethodPerEndpoint])

  @Provides @Singleton @Named("MergerServers")
  def provideServers(@Inject()@TypesafeConfig("rpc.client.mergers") mergers: java.util.Map[String, String]) = {
    provideClients[MergerService.MethodPerEndpoint](mergers.toSeq)
  }

}
