package com.maogogo.mywork.common.jdbc

import java.sql.ResultSet
import com.maogogo.mywork.thrift.{ Cell, Row }
import com.twitter.finagle.Thrift
import com.maogogo.mywork.thrift.ThriftAnyVal
import java.sql.Types

package object druid {

  implicit def resultToRow(rs: ResultSet): Row = {
    val cols = rs.getMetaData.getColumnCount
    Row(Seq.range(1, cols + 1).map { index ⇒
      val colName = Option(rs.getMetaData.getColumnName(index)) match {
        case Some(name) if name.nonEmpty ⇒ name
        case _ ⇒ rs.getMetaData.getColumnLabel(index)
      }
      val colType = rs.getMetaData.getColumnType(index)
      (index, colName, colType)
    }.map {
      case (index, cellLabel, cellType) ⇒
        val cellVal = cellType match {
          case Types.BOOLEAN ⇒ ThriftAnyVal.BoolVal(rs.getBoolean(index))
          case Types.INTEGER ⇒ ThriftAnyVal.IntVal(rs.getInt(index))
          case Types.BIGINT ⇒ ThriftAnyVal.LongVal(rs.getLong(index))
          case x if x == Types.FLOAT || x == Types.DOUBLE ⇒ ThriftAnyVal.DoubleVal(rs.getDouble(index))
          case _ ⇒ ThriftAnyVal.StringVal(rs.getString(index))
        }
        Cell(cellVal, cellLabel)
    })
  }

  implicit class RichResultSet(rs: ResultSet) {

    private[this] def toStream: Stream[ResultSet] = {
      new Iterator[ResultSet] {
        def hasNext = rs.next()
        def next() = rs
      } toStream
    }

    def result[T](f: ResultSet ⇒ Option[T]): Option[T] = toStream.map(f).headOption.flatten

    def resultSet[T](f: ResultSet ⇒ Option[T]): Seq[T] = toStream.map(f).flatten

  }

}