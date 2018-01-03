package com.maogogo.mywork.leaf

import com.maogogo.mywork.common.inject.MainServer
import com.maogogo.mywork.leaf.modules.ServicesModule
import com.twitter.util.Await

object Main extends MainServer {

  lazy val servicesModule = new ServicesModule

  def injectServer: Unit = {
    val services = servicesModule.services(injector)
    Await.all(services: _*)
  }

  val logo = """
      __    _________    ______
     / /   / ____/   |  / ____/
    / /   / __/ / /| | / /_    
   / /___/ /___/ ___ |/ __/    
  /_____/_____/_/  |_/_/       
                               """

}