package com.maogogo.mywork.merger.modules

import scala.collection.JavaConversions._

import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.common.inject.MainModule
import com.typesafe.config.Config

import javax.inject.Named
import com.maogogo.mywork.thrift._
import javax.inject.Inject
import com.github.racc.tscg.TypesafeConfig

class ServicesModule(implicit val config: Config) extends MainModule { //extends TwitterModule with BaseModule {

  override def injectModule: Unit = {
    //    bindSingleton[LeafService.MethodPerEndpoint].to[LeafingDispatchImpl]
    //    bindSingleton[MergerService.MethodPerEndpoint].to[MergerServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"merger" -> injector.instance[MergerService.MethodPerEndpoint])

  @Provides @Singleton @Named("LeafServers")
  def provideServers(@Inject()@TypesafeConfig("rpc.client.leafs") leafs: java.util.Map[String, String]) = {
    provideClients[LeafService.MethodPerEndpoint](leafs.toSeq)
  }

}

