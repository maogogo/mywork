package com.maogogo.mywork.backend

import com.maogogo.mywork.common.inject.MainServer
import com.maogogo.mywork.backend.modules.ServicesModule
import com.twitter.finagle.Http
import com.twitter.util.StorageUnit
import io.finch._
import com.maogogo.mywork.backend.filters.ExceptionFilter
import com.twitter.finagle.http.Response

object Main extends MainServer {

  override val servicesModule = new ServicesModule

  override def injectServer: Unit = {

    //servicesModule.endpoints(injector)

    val endpoints = servicesModule.endpoints(injector).toService

    val filters = new ExceptionFilter

    val server = Http.server.withMaxRequestSize(StorageUnit.fromMegabytes(100))
      .serve(s":${config.getInt("http.port")}", filters andThen endpoints)

    onExit {
      server.close()
    }

  }

  override val logo: String = """
      ____             __   ______          __
     / __ )____ ______/ /__/ ____/___  ____/ /
    / __  / __ `/ ___/ //_/ __/ / __ \/ __  / 
   / /_/ / /_/ / /__/ ,< / /___/ / / / /_/ /  
  /_____/\__,_/\___/_/|_/_____/_/ /_/\__,_/   """

}