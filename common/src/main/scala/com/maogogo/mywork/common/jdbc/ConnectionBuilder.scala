package com.maogogo.mywork.common.jdbc

import com.maogogo.mywork.common._
import javax.inject.{ Inject, Named }
import com.maogogo.mywork.common.modules.DataSourcePool
import com.twitter.finagle.Mysql
import com.twitter.finagle.client.DefaultPool
import com.twitter.util.Duration
import com.typesafe.config.Config
import com.maogogo.mywork.common.utils.ThreeDesUtil
import com.twitter.util.Futures
import com.twitter.util.Future
import com.twitter.util.Await

trait DataSourceBuilder { self ⇒

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

class ConnectionBuilder @Inject() (config: Config) extends DataSourceBuilder { //self =>

  lazy val shardId: Int = config.getInt("shard.id")
  lazy val testSQL = "select 1"

  private def getConnections: Seq[DataSourcePool] = {
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

  def client(masterOrSlave: Int = 0, partitionRandomIndex: Int = 0): TransactionsClient = {

    val connSize = getConnections.size

    val partitionIndex = masterOrSlave match {
      case 0 ⇒
        shardId match {
          case 0 ⇒ new scala.util.Random().nextInt(connSize)
          case _ ⇒ (shardId + partitionRandomIndex) % connSize
        }
      case 1 ⇒ 0
      case _ ⇒ new scala.util.Random().nextInt(connSize - 1) + 1
    }

    val pool = getConnections(partitionIndex)
    val poolSize = pool.clients.length

    //随机获取连接
    val serialIndex = new scala.util.Random().nextInt(poolSize)

    val connectionIndex = (pool.testing match {
      case true ⇒
        try Await.result(pool.clients(serialIndex).select(testSQL) { _("1").asInt }.map(_.headOption))
        catch {
          case t: Throwable ⇒ None
        }
      case _ ⇒ Option(serialIndex)
    }) match {
      case Some(index) if index != -1 ⇒ index
      case _ ⇒ getConnectionIndex(poolSize, serialIndex)
    }
    pool.clients(connectionIndex)
  }

  private[this] def getConnectionIndex(size: Int, num: Int): Int = {
    size match {
      //size > 1
      case x if x == (num + 1) && num > 0 ⇒ num - 1
      //size == 1
      case x if x == (num + 1) && num == 0 ⇒ -1
      case _ ⇒ num + 1
    }
  }
}
