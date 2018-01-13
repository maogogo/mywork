package com.maogogo.mywork.backend.modules

import com.maogogo.mywork.common.inject.MainModule
import com.typesafe.config.Config
import com.twitter.inject.Injector
import com.twitter.scrooge.ThriftService
import com.maogogo.mywork.common.inject.HttpMainModule
import com.maogogo.mywork.backend.hello.HelloEndpoints
import com.twitter.finagle.Service
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Response
import com.twitter.util.Future
import com.maogogo.mywork.thrift._
import io.finch.Output
import com.twitter.finagle.http.Status

class ServicesModule(implicit val config: Config) extends HttpMainModule {

  def injectModule: Unit = {
    install(new TestModule)
    //bindSingleton[RootService.MethodPerEndpoint].toInstance(zookClient[RootService.MethodPerEndpoint]("root"))
    bindSingleton[EngineService.MethodPerEndpoint].toInstance(zookClient[EngineService.MethodPerEndpoint]("engine"))
  }

  def endpoints(injector: Injector) = {
    (injector.instance[HelloEndpoints].endpoints).handle {
      case ex: Exception ⇒
        //Ok("123")
        println("eeee==>>>" + ex)
        io.finch.Output.failure(ex, Status(500))
      //throw new ServiceException("sssssssssssssss")
    }
  }

}