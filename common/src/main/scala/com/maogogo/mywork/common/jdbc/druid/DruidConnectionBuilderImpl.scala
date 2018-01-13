package com.maogogo.mywork.common.jdbc.druid

import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import com.maogogo.mywork.common.jdbc.druid._
import com.maogogo.mywork.thrift.Row
import com.twitter.util.Future
import javax.inject.Inject
import com.alibaba.druid.pool.DruidDataSource
import java.sql._

class DruidConnectionBuilderImpl @Inject() (ds: DruidDataSource) extends ConnectionBuilder {

  def prepare[T](sql: String, params: Option[Seq[String]])(fallback: Row ⇒ Option[T]): Future[Seq[T]] = {
    val conn = getConnection
    val pstmt = conn.prepareStatement(sql)
    params.map {
      _.zipWithIndex.foreach {
        case (p, index) ⇒ pstmt.setString(index + 1, p)
      }
    }
    val rs = pstmt.executeQuery
    val result = rs.resultSet { r ⇒ fallback(r) }
    close(conn, pstmt, rs)
    Future.value(result)
  }

  def statement[T](sql: String)(fallback: Row ⇒ Option[T]): Future[Seq[T]] = {
    val conn = getConnection
    val stmt = conn.createStatement
    val rs = stmt.executeQuery(sql)
    val result = rs.resultSet { r ⇒ fallback(r) }
    close(conn, stmt, rs)
    Future.value(result)
  }

  private[this] def getConnection: Connection = ds.getConnection

  private[this] def close(conn: Connection, stmt: Statement, rs: ResultSet): Unit = {
    try {
      if (conn != null) conn.close
      if (stmt != null) stmt.close
      if (rs != null) rs.close
    } catch {
      case e: Exception ⇒
    }
  }

}