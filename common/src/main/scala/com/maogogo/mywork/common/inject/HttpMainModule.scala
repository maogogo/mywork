package com.maogogo.mywork.common.inject

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.common.modules.BaseModule
import com.typesafe.config.Config
import com.github.racc.tscg.TypesafeConfigModule
import com.google.inject.{ Provides, Singleton }
import com.twitter.inject.Injector
import com.twitter.scrooge.ThriftService
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.Service
import com.twitter.finagle.http._

trait HttpMainModule extends MainModule {

  @Provides @Singleton
  def provideCorsFilter: Cors.HttpFilter = {
    val policy: Cors.Policy = Cors.Policy(
      allowsOrigin = _ ⇒ Some("*"),
      allowsMethods = _ ⇒ Some(Seq("GET", "POST", "PUT", "DELETE", "OPTOPNS")),
      allowsHeaders = _ ⇒ Some(Seq("Accept", "Content-Type", "Authorization", "Access-Control-Allow-Origin")) //"Access-Control-Allow-Headers": "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
    )
    new Cors.HttpFilter(policy)
  }

  def provideServices(injector: Injector): Map[String, ThriftService] = {
    ???
  }

}