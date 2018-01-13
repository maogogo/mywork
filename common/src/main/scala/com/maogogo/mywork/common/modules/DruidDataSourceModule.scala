package com.maogogo.mywork.common.modules

import com.google.inject.AbstractModule
import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import com.maogogo.mywork.common.jdbc.druid.DruidConnectionBuilderImpl
import com.alibaba.druid.pool.DruidDataSource
import com.typesafe.config.Config
import javax.inject.Inject
import com.google.inject.Provides
import com.google.inject.Singleton

class DruidDataSourceModule extends AbstractModule {

  override def configure: Unit = {
    bind(classOf[ConnectionBuilder]).to(classOf[DruidConnectionBuilderImpl])
  }

  @Provides @Singleton
  def provideDruidDataSource(@Inject config: Config): DruidDataSource = {

    val driver = config.getString("jdbc.driver")
    val url = config.getString("jdbc.url")
    val username = config.getString("jdbc.username")
    val password = config.getString("jdbc.password")
    val encrypt = config.getBoolean("jdbc.encrypt")

    val dataSource = new DruidDataSource
    dataSource.setDriverClassName(driver)
    dataSource.setUrl(url)
    dataSource.setUsername(username)
    dataSource.setPassword(password)

    dataSource.setInitialSize(5)
    dataSource.setMinIdle(1)
    dataSource.setMaxActive(10)

    dataSource
  }

}