package com.maogogo.mywork.common.jdbc

import com.twitter.finagle.mysql._
import java.util.TimeZone
import java.util.Date
import com.maogogo.mywork.thrift.{ Row => TRow, _ }
//import com.maogogo.mywork.webapp.thrift.SearchPhrase

trait Dao { self =>

  implicit def anyToParameter(p: Option[Any]): Parameter = Parameter.unsafeWrap(p.getOrElse(null))
  implicit def seqToParameter(ps: Seq[Any]): Parameter = ps.map(Parameter.unsafeWrap)

  type Bytes = Array[Byte]

  def result[T](convert: Row => Option[T]): PartialFunction[Any, Option[T]] = {
    case rs: ResultSet => rs.rows.headOption.flatMap(convert)
    case _ => None
  }

  def resultSet[T](convert: Row => Option[T]): PartialFunction[Any, Seq[T]] = {
    case rs: ResultSet => rs.rows.map(convert).flatten
    case _ => Seq.empty[T]
  }

  def executeUpdate(r: Result): Int = r match {
    case OK(rows, _, _, _, _) => rows.toInt
    case _ => -1
  }

  def rowToArray(row: Row): Option[Seq[String]] = {
    Option(row.fields.map { f =>
      row(f.name).asString
    })
  }

  def rowToCells(row: Row): Option[Seq[Cell]] = {
    Option(
      row.fields.map { f =>
        Cell(f.name, row(f.name).asString)
      })
  }

  def rowToInt: PartialFunction[Row, Option[Int]] = {
    case row: Row =>
      row.fields.headOption.map { field => row(field.name).asInt }
  }

  implicit class RichValueOption(v: Option[Value]) {

    lazy val timestampValueLocal = new TimestampValue(TimeZone.getDefault(), TimeZone.getDefault())

    def asOptionInt[T](method: Int => T) = as[Int, T]({
      case IntValue(v) => Option(method(v))
      case ByteValue(v) => Option(method(v))
      case LongValue(v) => Option(method(v.toInt))
      case StringValue(v) => Option(method(v match {
        case "" => 0
        case _ => v.toInt
      }))
    })

    def asOptionInt = asOptionInt[Int](x => x)

    def asInt = asOptionInt[Int](x => x).getOrElse(0)

    def asOptionDouble[T](method: Double => T) = as[Double, T]({
      case IntValue(v) => Option(method(v.toDouble))
      case LongValue(v) => Option(method(v.toDouble))
      case DoubleValue(v) => Option(method(v))
      case StringValue(v) => Option(method(v.toDouble))
    })
    def asOptionDouble = asOptionDouble[Double](x => x)
    def asDouble = asOptionDouble[Double](x => x).getOrElse(0.0)

    def asOptionBool[T](method: Boolean => T) = as[Boolean, T]({
      case LongValue(v) => Option(method(v == 1))
      case IntValue(v) => Option(method(v == 1))
      case StringValue(v) => Option(method(v == "1"))
      case RawValue(_type, _, true, bytes) => Option(method(new String(bytes) == 1))
    })

    def asOptionBool = asOptionBool[Boolean](x => x)

    def asBool = asOptionBool[Boolean](x => x).getOrElse(false)

    def asOptionLong[T](method: Long => T) = as[Long, T]({
      case LongValue(v) => Option(method(v))
      case IntValue(v) => Option(method(v))
      case StringValue(v) => Option(method(v match {
        case "" => 0
        case _ => v.toLong
      }))
    })

    def asOptionLong = asOptionLong[Long](x => x)

    def asLong = asOptionLong[Long](x => x).getOrElse(0l)

    def asTimestamp[T](method: Long => T) = as[Long, T]({
      case timestampValueLocal(v) => Option(method(v.getTime))
    })

    def asTimestamp = asTimestamp[Long](x => x)

    def asOptionString[T](method: String => T) = as[String, T]({
      case StringValue(v) => Option(method(v))
      case LongValue(v) => Option(method(v.toString))
      case IntValue(v) => Option(method(v.toString))
      case DoubleValue(v) => Option(method(v.toString))
      case BigDecimalValue(v) => Option(method(v.toString))
      //TODO (Toan) 这里是日期的时候有问题
      case RawValue(_, _, true, bytes) => Option(method(new String(bytes)))
      case _ => None
    })

    def asOptionString = asOptionString[String](x => x)

    def asString = asOptionString[String](x => x).getOrElse("")

    def asDate[T](method: Date => T) = as[Date, T]({ case DateValue(v) => Option(method(v)) })

    def asDate = asDate[Date](x => x)

    def asBytes[T](method: Bytes => T) = as[Bytes, T]({ case RawValue(_, _, _, v) => Option(method(v)) })

    def asBytes = asBytes[Bytes](x => x)

    private[this] def as[V, T](pf: PartialFunction[Any, Option[T]]): Option[T] = (v map (pf orElse notMatch)).flatten

    private def notMatch[T](): PartialFunction[Value, Option[T]] = {
      case NullValue => None
      case x =>
        throw new IllegalArgumentException(s"参数错误 - ${x}")
    }
  }

  //  case class PhraseQuery(searchPhrase: SearchPhrase, fields: Seq[String], recordStatus: Boolean = true) {
  //
  //    val status = recordStatus match {
  //      case true => " record_status='1'"
  //      case _ => ""
  //    }
  //
  //    def getCondition: String = {
  //      searchPhrase.searchPhrase match {
  //        case Some(s) if s.nonEmpty => fields.map(x => s"${x} like ?").mkString(" where ", " or ", "") + s" and ${status}"
  //        case _ => if (status == "") "" else s" where ${status}"
  //      }
  //    }
  //
  //    def getParams: Seq[Parameter] = {
  //      searchPhrase.searchPhrase match {
  //        case Some(s) if s.nonEmpty =>
  //          val param = s"%${searchPhrase.searchPhrase.getOrElse("").trim}%"
  //          fields.indices.map(x => Option(param)).map(anyToParameter)
  //        case _ => Seq.empty
  //      }
  //
  //    }
  //
  //    def getValues: Seq[String] = {
  //      searchPhrase.searchPhrase match {
  //        case Some(s) if s.nonEmpty =>
  //          val param = s"%${searchPhrase.searchPhrase.getOrElse("").trim}%"
  //          fields.indices.map(x => param)
  //        case _ => Seq.empty
  //      }
  //    }
  //
  //    def getLimit: String = {
  //      if (searchPhrase.offset < 0 || searchPhrase.limit < 0) "" else s"limit ${(searchPhrase.offset - 1) * searchPhrase.limit}, ${searchPhrase.limit}"
  //    }
  //
  //  }

}

