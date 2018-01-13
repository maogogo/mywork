package com.maogogo.mywork.common.jdbc

import com.maogogo.mywork.common._
import com.twitter.util.Future
import java.sql.ResultSet
import java.sql.Connection
import java.sql.Statement
import com.maogogo.mywork.thrift._

trait SQL {

  def single[T](sql: String)(fallback: Row ⇒ Option[T])(implicit builder: ConnectionBuilder): Future[Option[T]] = {
    builder.statement(sql)(fallback).map(_.headOption)
  }

  def single[T](sql: String, params: Option[Seq[String]] = None)(fallback: Row ⇒ Option[T])(implicit builder: ConnectionBuilder): Future[Option[T]] = {
    builder.prepare(sql, params)(fallback).map(_.headOption)
  }

  def list[T](sql: String)(fallback: Row ⇒ Option[T])(implicit builder: ConnectionBuilder): Future[Seq[T]] = {
    builder.statement(sql)(fallback)
  }

  def list[T](sql: String, params: Option[Seq[String]] = None)(fallback: Row ⇒ Option[T])(implicit builder: ConnectionBuilder): Future[Seq[T]] = {
    builder.prepare(sql)(fallback)
  }

  //  private[this] def close(conn: Connection, stmt: Statement, rs: ResultSet): Unit = {
  //    try {
  //      if (conn != null) conn.close
  //      if (stmt != null) stmt.close
  //      if (rs != null) rs.close
  //    } catch {
  //      case e: Exception ⇒
  //        println(e)
  //    }
  //  }
}

object SQL extends SQL