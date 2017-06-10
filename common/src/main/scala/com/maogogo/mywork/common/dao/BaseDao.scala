package com.maogogo.mywork.common.dao

import com.maogogo.mywork.common._
import com.twitter.finagle.mysql._
import java.util.TimeZone
import java.util.Date
import java.sql.Timestamp
import com.twitter.finagle.mysql.transport.MysqlBuf
import java.util.Calendar

trait BaseDao { self =>

  implicit def optionToParameter(p: Option[Any]): Parameter = Parameter.unsafeWrap(p.getOrElse(null))

  def result[T](convert: Row => Option[T]): PartialFunction[Any, Option[T]] = {
    case rs: ResultSet => rs.rows.headOption.flatMap(convert)
    case _ => None
  }

  def resultSet[T](convert: Row => Option[T]): PartialFunction[Any, Seq[T]] = {
    case rs: ResultSet => rs.rows.map(convert).flatten
    case _ => Seq.empty[T]
  }

  def executeCount: PartialFunction[Any, Int] = {
    case rs: ResultSet =>
      rs.rows.headOption.flatMap { row =>
        row.fields.headOption.map { field => row(field.name).asInt }
      }.getOrElse(-1)
    case _ => -1
  }

  def executeUpdate(r: Result): Int = r match {
    case OK(rows, _, _, _, _) => rows.toInt
    case _ => -1
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
      case RawValue(_, _, true, bytes) => Option(method(new String(bytes)))
      case _ => None
    })

    def asOptionString = asOptionString[String](x => x)

    def asString = asOptionString[String](x => x).getOrElse("")

    def asDate[T](method: Date => T) = as[Date, T]({ case DateValue(v) => Option(method(v)) })

    def asDate = asDate[Date](x => x)

    def asDatetime[T](method: Date => T) = as[Long, T]({
      case DateValue(v) => Option(method(v))
      case x: Timestamp =>
        Option(method(x))
      case RawValue(_, charset, true, bytes) =>
        val br = MysqlBuf.reader(bytes)

        val year = br.readUnsignedShortLE()
        val month = br.readUnsignedByte()
        val day = br.readUnsignedByte()
        val cal = Calendar.getInstance
        cal.set(year, month - 1, day)

        Option(method(cal.getTime))
    })

    def asOptionDatetime = asDatetime[Long](x => x.getTime)

    def asDatetime = asOptionDatetime.getOrElse(-1l)

    def asBytes[T](method: Bytes => T) = as[Bytes, T]({ case RawValue(_, _, _, v) => Option(method(v)) })

    def asBytes = asBytes[Bytes](x => x)

    private[this] def as[V, T](pf: PartialFunction[Any, Option[T]]): Option[T] = (v map (pf orElse notMatch)).flatten

    private def notMatch[T](): PartialFunction[Value, Option[T]] = {
      case NullValue => None
      case x =>
        throw new IllegalArgumentException(s"参数错误 - ${x}")
    }
  }

}