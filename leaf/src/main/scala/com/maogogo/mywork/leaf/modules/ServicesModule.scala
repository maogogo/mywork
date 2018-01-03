package com.maogogo.mywork.leaf.modules

import com.maogogo.mywork.common.inject.MainModule
import com.maogogo.mywork.thrift.LeafService
import com.typesafe.config.Config

class ServicesModule(implicit val config: Config) extends MainModule { //extends TwitterModule with BaseModule with DataSourceModule {
  //  lazy val log = LoggerFactory.getLogger(this.getClass)

  override def injectModule: Unit = {
    //    bindSingleton[MetaService.MethodPerEndpoint].toInstance(zookClient[MetaService.MethodPerEndpoint]("meta"))
    //    bindSingleton[MetaDataConfig]
    //    bindSingleton[LeafServiceDao]
    //    bindSingleton[LeafService.MethodPerEndpoint].to[LeafServiceImpl]
  }

  override def provideServices(injector: com.twitter.inject.Injector) = Map(
    s"leaf" -> injector.instance[LeafService.MethodPerEndpoint])

}
