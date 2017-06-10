package com.maogogo.mywork.common.modules

import com.maogogo.mywork.common._
import com.google.inject.{ Provides, Singleton }
import javax.inject.Named
import com.typesafe.config.Config
import com.maogogo.mywork.common.utils.ThreeDesUtil
import com.twitter.finagle.client.DefaultPool
import com.twitter.finagle.Mysql
import com.twitter.util.Duration

trait DataSourceModule { self =>

  private[this] val namespace = "mysql"

  @Provides @Singleton @Named("connections")
  def getConnections(implicit config: Config): Seq[DataSourcePool] = {

    val partitions = config.getInt(s"${namespace}.partitions")
    val username = config.getString(s"${namespace}.username")
    val encrypt = config.getBoolean(s"${namespace}.encrypt")

    val password = encrypt match {
      case true => ThreeDesUtil.decrypt(config.getString(s"${namespace}.password"))
      case _ => config.getString(s"${namespace}.password")
    }

    val hosts = config.getString(s"${namespace}.host").split(",")
    val database = config.getString(s"${namespace}.database")
    val pool = config.getInt(s"${namespace}.pool")
    val testing = config.getBoolean(s"${namespace}.testing")

    hosts.toSeq.map { host =>
      val clients = ((0 until partitions) map { i =>
        getConnection(host, username, password, database, pool)
      })

      DataSourcePool(partitions, testing, clients)
    }

  }

  private[this] def getConnection(host: String, username: String, password: String, database: String, pool: Int): TransactionsClient = {
    val client = Mysql.client
      .withCredentials(username, password)
      .configured(DefaultPool.Param(
        low = pool,
        high = Int.MaxValue,
        idleTime = Duration.Top,
        bufferSize = 0,
        maxWaiters = Int.MaxValue
      ))

    client.withDatabase(database).newRichClient(host)
  }

}

case class DataSourcePool(
  partitions: Int,
  testing: Boolean = false,
  clients: Seq[TransactionsClient]
)
