package com.maogogo.mywork.common.dao

import com.maogogo.mywork.common._
import com.maogogo.mywork.common.modules.DataSourcePool
import org.slf4j.LoggerFactory
import com.twitter.util.{ Future, Futures }

trait ConnectionBuilder extends BaseDao { self =>

  val connections: Seq[DataSourcePool] //多个数据库连接池
  val shardId: Int //当前设备编号(主从 穿插分配)
  lazy val log = LoggerFactory.getLogger(getClass)
  lazy val testSQL = "select 1"
  //var partitionRandomIndex: Int = -1

  def build[T](fallback: TransactionsClient => Future[T]): Future[T] = build(0, 0)(fallback)

  /**
   * masterOrSlave: 0:随机分配, 1: master, 2: slave
   * partition:
   */
  def build[T](masterOrSlave: Int = 0, partitionRandomIndex: Int = 0)(fallback: TransactionsClient => Future[T]): Future[T] = {

    // 计算获取主从连接池
    val partitionIndex = masterOrSlave match {
      case 0 =>
        shardId match {
          case 0 => new scala.util.Random().nextInt(connections.size)
          case _ => (shardId + partitionRandomIndex) % connections.size
          //connections.size % shardId + new scala.util.Random().nextInt(2)
        }
      case 1 => 0
      case _ => new scala.util.Random().nextInt(connections.size - 1) + 1
    }

    val pool = connections(partitionIndex)

    //随机获取连接
    val serialIndex = new scala.util.Random().nextInt(pool.clients.size)

    Futures.flatten(for {
      i <- pool.testing match {
        case true => pool.clients(serialIndex).select(testSQL) { _("1").asInt } handle {
          case t: Throwable =>
            log.error(s"sql[${testSQL}] detect failed cause: ", t)
            Seq(-1)
        }
        case _ =>
          log.info(s"mysql is testing false, using [partiton: ${partitionIndex}, serialIndex: ${serialIndex}]")
          Future.value(Seq(serialIndex))
      }
      j = i.headOption match {
        case Some(x) if x != -1 => x
        case _ => getConnectionIndex(pool.clients.size, serialIndex)
      }
    } yield fallback(pool.clients(j)))

  }

  private[this] def getConnectionIndex(size: Int, num: Int): Int = {
    size match {
      //size > 1
      case x if x == (num + 1) && num > 0 => num - 1
      //size == 1
      case x if x == (num + 1) && num == 0 => -1
      case _ => num + 1
    }
  }

}