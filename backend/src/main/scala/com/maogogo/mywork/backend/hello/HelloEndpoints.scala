package com.maogogo.mywork.backend.hello

import com.google.inject.{ Provides, Singleton }
import io.finch._
import javax.inject.Inject
import com.maogogo.mywork.thrift.RootService

@Provides @Singleton
class HelloEndpoints @Inject() (client: RootService.MethodPerEndpoint) {

  def endpoints = hello

  val hello: Endpoint[String] = get("hello") {
    client.hi("Toan").map(Ok)
    //    Ok("hello")
  }

}