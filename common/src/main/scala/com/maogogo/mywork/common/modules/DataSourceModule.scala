package com.maogogo.mywork.common.modules

import com.google.inject.Provides
import com.google.inject.Singleton
import com.maogogo.mywork.common.TransactionsClient
import com.maogogo.mywork.common.utils.ThreeDesUtil
import com.twitter.finagle.Mysql
import com.twitter.finagle.client.DefaultPool
import com.twitter.util.Duration
import com.typesafe.config.Config

import javax.inject.Named
import javax.inject.Inject

trait DataSourceModule { self ⇒

  @Provides @Singleton @Named("shardId")
  def provideShardId(implicit config: Config): Int = config.getInt("shard.id")

  @Provides @Singleton @Named("connections")
  def getConnections(implicit config: Config): Seq[DataSourcePool] = {

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
