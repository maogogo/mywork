package com.maogogo.mywork.merger.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules._
import org.slf4j.LoggerFactory
import com.maogogo.mywork.thrift._
import com.google.inject.{ Provides, Singleton }
import javax.inject.{ Inject, Named }
import com.typesafe.config.Config
import scala.collection.JavaConversions._
import com.twitter.finagle.ThriftMux
import com.maogogo.mywork.merger.service.MergerServiceImpl
import com.maogogo.mywork.common.dispatch.LeafingDispatchImpl
import com.github.racc.tscg.TypesafeConfig

class ServicesModule(implicit config: Config) extends TwitterModule with BaseModule {

  override def configure: Unit = {
    bindSingleton[LeafService.MethodPerEndpoint].to[LeafingDispatchImpl]
    bindSingleton[MergerService.MethodPerEndpoint].to[MergerServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"merger" -> injector.instance[MergerService.MethodPerEndpoint])

  @Provides @Singleton @Named("LeafServers")
  def provideServers(@Inject()@TypesafeConfig("rpc.client.leafs") leafs: java.util.Map[String, String]) = {
    provideClients[LeafService.MethodPerEndpoint](leafs.toSeq)
  }

}

