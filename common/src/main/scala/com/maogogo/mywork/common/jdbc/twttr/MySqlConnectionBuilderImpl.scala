package com.maogogo.mywork.common.jdbc.twttr

import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import com.twitter.util.Future
import com.maogogo.mywork.thrift.{ Row, Cell }
import com.twitter.finagle.mysql.{ Row ⇒ TRow }
import javax.inject.Inject
import com.maogogo.mywork.thrift.ThriftAnyVal

class MySqlConnectionBuilderImpl @Inject() (pool: MySqlDataSourcePool) extends ConnectionBuilder {

  def prepare[T](sql: String, params: Option[Seq[String]])(fallback: Row ⇒ Option[T]): Future[Seq[T]] = {
    pool.client.prepare(sql)().map(resultSet(r ⇒ fallback(rowToRow(r))))
  }

  def rowToRow(row: TRow): Row = {
    Row(row.fields.map { f ⇒
      Cell(ThriftAnyVal.StringVal(row(f.name).toString), f.name)
    })
  }

  def statement[T](sql: String)(fallback: Row ⇒ Option[T]): Future[Seq[T]] = {
    ???
  }

}