package com.maogogo.mywork.backend.modules

import com.twitter.inject.TwitterModule
import com.maogogo.mywork.backend.hello.HelloServiceDao
import com.google.inject.AbstractModule
import com.maogogo.mywork.backend.hello.HelloServiceDao
import com.google.inject.Provides
import com.google.inject.Singleton
import com.typesafe.config.Config
import javax.inject.Inject
import com.github.racc.tscg.TypesafeConfig

class TestModule extends AbstractModule {

  override def configure(): Unit = {
    //bind[HelloServiceDao].to(classOf[HelloServiceDao])
    //bind(classOf[HelloServiceDao]) //.to(classOf[HelloServiceDao]) //.in(classOf[Singleton])
  }

  @Provides @Singleton
  def aa(@TypesafeConfig("http.port") p: String): HelloServiceDao = {
    new HelloServiceDao(p)
  }

  //  override def configure(): Unit = {
  ////    bind[HelloServiceDao]
  //
  //
  //    //bind(HelloServiceDao).to(HelloServiceDao.class);
  //    //bind[HelloServiceDao]
  //  }

}

object TestModule extends TestModule {

}