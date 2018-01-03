package com.maogogo.mywork.common.inject

import com.maogogo.mywork.common.modules.BaseModule
import com.twitter.inject.TwitterModule
import com.typesafe.config.Config
import com.github.racc.tscg.TypesafeConfigModule

trait MainModule extends TwitterModule with BaseModule {

  val config: Config

  override def configure: Unit = {
    install(TypesafeConfigModule.fromConfigWithPackage(config, ""))
    bindSingleton[Config].toInstance(config)

    injectModule
  }

  def injectModule: Unit

}