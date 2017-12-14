package com.hejinonline.mywork.common.jdbc

import javax.sql.DataSource
import java.sql.ResultSet
import com.twitter.util.Future
import com.maogogo.mywork.thrift._

trait SqlSession { self =>

  val dataSource: DataSource

  def list(sql: String): Future[Seq[Seq[String]]] = {
    val conn = dataSource.getConnection
    val stmt = conn.createStatement
    val rows = stmt.executeQuery(sql).list
    stmt.close
    conn.close
    Future(rows)
  }

  def list(sql: String, params: Option[Seq[String]] = None): Future[Seq[Seq[String]]] = {
    val conn = dataSource.getConnection
    val pstmt = conn.prepareStatement(sql)

    params.map(_.zipWithIndex.map {
      case (x, index) => pstmt.setString(index + 1, x)
    })

    val rows = pstmt.executeQuery.list
    pstmt.close
    conn.close
    Future(rows)
  }

  def listRow(sql: String, params: Option[Seq[String]] = None): Future[Seq[Row]] = {
    val conn = dataSource.getConnection
    val stmt = conn.createStatement
    val rows = stmt.executeQuery(sql).listRow
    stmt.close
    conn.close
    Future(rows)
  }

  def single(sql: String): Future[Seq[String]] = {
    val conn = dataSource.getConnection
    val stmt = conn.createStatement
    val rows = stmt.executeQuery(sql).single
    stmt.close
    conn.close
    Future(rows)
  }

  def singleRow(sql: String): Future[Row] = {
    val conn = dataSource.getConnection
    val stmt = conn.createStatement
    val rows = stmt.executeQuery(sql).singleRow
    stmt.close
    conn.close
    Future(rows)
  }

  private[this] implicit class RichResultSet(v: ResultSet) {

    def single: Seq[String] = {
      v.next match {
        case true => toSeq(v)
        case _ => Seq.empty
      }
    }

    def singleRow: Row = {
      v.next match {
        case true => toRow(v)
        case _ => Row()
      }
    }

    def list: Seq[Seq[String]] = {
      val list = scala.collection.mutable.MutableList.empty[Seq[String]]
      while (v.next) {
        list += toSeq(v)
      }
      list
    }

    def listRow: Seq[Row] = {
      val list = scala.collection.mutable.MutableList.empty[Row]
      while (v.next) {
        list += toRow(v)
      }
      list
    }

    private def toRow: PartialFunction[ResultSet, Row] = {
      case rs =>
        val cols = rs.getMetaData.getColumnCount

        Row(Seq.range(0, cols).map { index =>
          Cell(rs.getMetaData.getColumnLabel(index + 1), rs.getString(index + 1))
        })
    }

    private def toSeq: PartialFunction[ResultSet, Seq[String]] = {
      case rs =>
        val cols = rs.getMetaData.getColumnCount
        Seq.range(0, cols).map(index => rs.getString(index + 1))
    }

  }

}