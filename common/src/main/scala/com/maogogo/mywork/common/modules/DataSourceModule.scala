package com.maogogo.mywork.common.modules

import com.maogogo.mywork.common._
import com.google.inject.{ Provides, Singleton }
import javax.inject.Named
import com.typesafe.config.Config
import com.maogogo.mywork.common.utils.ThreeDesUtil
import com.twitter.finagle.client.DefaultPool
import com.twitter.finagle.Mysql
import com.twitter.util.Duration
import javax.inject.Inject
import com.github.racc.tscg.TypesafeConfig

trait DataSourceModule { self =>

  private[this] val namespace = "mysql"

  @Provides @Singleton @Named("connections")
  def getConnections(
    @Inject()@TypesafeConfig("mysql.host") host: String,
    @TypesafeConfig("mysql.database") database: String,
    @TypesafeConfig("mysql.pool") pool: Int,
    @TypesafeConfig("mysql.testing") testing: Boolean,
    @TypesafeConfig("mysql.username") username: String,
    @TypesafeConfig("mysql.password") passwordHash: String,
    @TypesafeConfig("mysql.partitions") partitions: Int,
    @TypesafeConfig("mysql.encrypt") encrypt: Boolean): Seq[DataSourcePool] = {

    val password = encrypt match {
      case true => ThreeDesUtil.decrypt(passwordHash)
      case _ => passwordHash
    }

    host.split(",").toSeq.map { host =>
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
        maxWaiters = Int.MaxValue))

    client.withDatabase(database).newRichClient(host)
  }

}

case class DataSourcePool(
  partitions: Int,
  testing: Boolean = false,
  clients: Seq[TransactionsClient])
