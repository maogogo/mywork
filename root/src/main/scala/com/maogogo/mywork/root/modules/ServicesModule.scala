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
import com.github.racc.tscg.TypesafeConfigModule
import com.github.racc.tscg.TypesafeConfig

class ServicesModule(implicit config: Config) extends TwitterModule with BaseModule {

  override def configure: Unit = {

    install(TypesafeConfigModule.fromConfigWithPackage(config, ""))
    bindSingleton[Config].toInstance(config)
    
    //bindSingleton[EngineService.MethodPerEndpoint].toInstance(zookClient[EngineService.MethodPerEndpoint]("engine"))
    //bindSingleton[MetaService.MethodPerEndpoint].toInstance(zookClient[MetaService.MethodPerEndpoint]("meta"))
    bindSingleton[RootService.MethodPerEndpoint].to[RootServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"root" -> injector.instance[RootService.MethodPerEndpoint])

  //MergerService.MethodPerEndpoint
  @Provides @Singleton @Named("MergerServers")
  def provideServers(@Inject()@TypesafeConfig("rpc.client.mergers") mergers: java.util.Map[String, String]) = {
    provideClients[MergerService.MethodPerEndpoint](mergers.toSeq)
  }

}

//object ServicesModule extends ServicesModule