package com.maogogo.mywork.common.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import com.maogogo.mywork.common.jdbc.druid.DruidConnectionBuilderImpl
import com.maogogo.mywork.common.utils.ThreeDesUtil
import com.twitter.finagle.Mysql
import com.twitter.finagle.client.DefaultPool
import com.twitter.util.Duration
import com.typesafe.config.Config
import com.maogogo.mywork.common.jdbc._

import javax.inject.Inject
import javax.inject.Named
import com.maogogo.mywork.common.jdbc.twttr.MySqlDataSourcePool

class MySqlDataSourceModule extends AbstractModule { self ⇒

  override def configure: Unit = {
    bind(classOf[MySqlDataSourcePool]).in(classOf[Singleton])
    bind(classOf[ConnectionBuilder]).to(classOf[DruidConnectionBuilderImpl]).in(classOf[Singleton])
  }

  @Provides @Singleton @Named("shardId")
  def provideShardId(@Inject config: Config): Int = config.getInt("shard.id")

  @Provides @Singleton @Named("connections")
  def getConnections(@Inject config: Config): Seq[DataSourcePool] = {

    val host = config.getString("mysql.host")
    val database = config.getString("mysql.database")
    val pool = config.getInt("mysql.pool")
    val testing = config.getBoolean("mysql.testing")
    val username = config.getString("mysql.username")
    val passwordHash = config.getString("mysql.password")
    val encrypt = config.getBoolean("mysql.encrypt")
    val partitions = config.getInt("mysql.partitions")

    val password = encrypt match {
      case true ⇒ ThreeDesUtil.decrypt(passwordHash)
      case _ ⇒ passwordHash
    }

    host.split(",").toSeq.map { host ⇒
      val clients = ((0 until partitions) map { i ⇒
        getConnection(host, username, password, database, pool)
      })
      DataSourcePool(partitions, testing, clients)
    }

  }

  def getConnection(host: String, username: String, password: String, database: String, pool: Int): TransactionsClient = {
    val client = Mysql.client
      .withCredentials(username, password)
      .configured(DefaultPool.Param(
        low = pool,
        high = Int.MaxValue,
        idleTime = Duration.Top,
        bufferSize = 0,
        maxWaiters = Int.MaxValue))

    client.withDatabase(database).newRichClient(host)
  }

}

case class DataSourcePool(
  partitions: Int,
  testing: Boolean = false,
  clients: Seq[TransactionsClient])
