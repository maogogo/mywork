package com.maogogo.mywork.merger

import com.maogogo.mywork.common.inject.MainServer
import com.maogogo.mywork.merger.modules.ServicesModule
import com.twitter.util.Await
import com.twitter.util.TimeoutException

object Main extends MainServer {

  lazy val servicesModule = new ServicesModule

  override def injectServer: Unit = {
    val services = servicesModule.services(injector)
    Await.all(services: _*)
  }

  override val logo = """
      __  ___                         
     /  |/  /__  _________ ____  _____
    / /|_/ / _ \/ ___/ __ `/ _ \/ ___/
   / /  / /  __/ /  / /_/ /  __/ /    
  /_/  /_/\___/_/   \__, /\___/_/     
                   /____/             """

}