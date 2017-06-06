package com.maogogo.mywork.rest.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules.ConfigModule
import com.maogogo.mywork.thrift.RootService
import com.google.inject.{ Provides, Singleton }
import com.twitter.finagle.http.filter.Cors
import com.maogogo.mywork.rest.hello.HelloEndpoints

trait ServicesModule extends TwitterModule with ConfigModule {

  override def configure: Unit = {
    bindSingleton[RootService.FutureIface].toInstance(zookClient[RootService.FutureIface]("root"))
  }

  def endpoints(injector: com.twitter.inject.Injector) = {
    injector.instance[HelloEndpoints].endpoints
  }

  def filters(injector: com.twitter.inject.Injector) = {
    injector.instance[Cors.HttpFilter]
  }

  @Provides @Singleton
  def provideCorsFilter: Cors.HttpFilter = {
    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin = _ => Some("*"),
      allowsMethods = _ => Some(Seq("GET", "POST", "PUT", "DELETE", "OPTOPNS")),
      allowsHeaders = _ => Some(Seq("Accept", "Content-Type", "Authorization", "Access-Control-Allow-Origin")) //"Access-Control-Allow-Headers": "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    )
    new Cors.HttpFilter(policy)
  }

}

object ServicesModule extends ServicesModule