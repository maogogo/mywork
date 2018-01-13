package com.maogogo.mywork.common.jdbc

import com.maogogo.mywork.common._
import com.twitter.finagle.mysql.{ Row ⇒ TRow, _ }
import java.util.TimeZone
import java.util.Date
import com.maogogo.mywork.thrift.{ Row, Cell }
import com.twitter.finagle.mysql.transport.MysqlBuf
import java.sql.Timestamp
import java.util.Calendar
import com.maogogo.mywork.common.utils.DateUtil

package object twttr {

  implicit def anyToParameter(p: Option[Any]): Parameter = Parameter.unsafeWrap(p.getOrElse(null))
  implicit def seqToParameter(ps: Seq[Any]): Parameter = ps.map(Parameter.unsafeWrap)

  implicit def rowToRow(row: TRow): Row = {
    row.fields.map { f =>
      
      //f.name
      
      f.fieldType
    }
    ???
  }

  private[this] object Zero extends Timestamp(0) {
    override val getTime = 0L
    override val toString = "0000-00-00 00:00:00"
  }

  def result[T](convert: TRow ⇒ Option[T]): PartialFunction[Any, Option[T]] = {
    case rs: ResultSet ⇒ rs.rows.headOption.flatMap(convert)
    case _ ⇒ None
  }

  def resultSet[T](convert: TRow ⇒ Option[T]): PartialFunction[Any, Seq[T]] = {
    case rs: ResultSet ⇒ rs.rows.map(convert).flatten
    case _ ⇒ Seq.empty[T]
  }

  def executeUpdate(r: Result): Int = r match {
    case OK(rows, _, _, _, _) ⇒ rows.toInt
    case _ ⇒ -1
  }

  def rowToInt: PartialFunction[TRow, Option[Int]] = {
    case row: TRow ⇒
      row.fields.headOption.map { field ⇒ row(field.name).asInt }
  }

  trait SDate
  case object SDateType extends SDate
  case object STimestampType extends SDate

  implicit class RichValueOption(v: Option[Value]) {

    lazy val timestampValueLocal = new TimestampValue(TimeZone.getDefault(), TimeZone.getDefault())

    def asOptionInt[T](method: Int ⇒ T) = as[Int, T]({
      case IntValue(v) ⇒ Option(method(v))
      case ByteValue(v) ⇒ Option(method(v))
      case LongValue(v) ⇒ Option(method(v.toInt))
      case StringValue(v) ⇒ Option(method(v match {
        case "" ⇒ 0
        case _ ⇒ v.toInt
      }))
    })

    def asOptionInt = asOptionInt[Int](x ⇒ x)

    def asInt = asOptionInt[Int](x ⇒ x).getOrElse(0)

    def asOptionDouble[T](method: Double ⇒ T) = as[Double, T]({
      case IntValue(v) ⇒ Option(method(v.toDouble))
      case LongValue(v) ⇒ Option(method(v.toDouble))
      case DoubleValue(v) ⇒ Option(method(v))
      case StringValue(v) ⇒ Option(method(v.toDouble))
    })
    def asOptionDouble = asOptionDouble[Double](x ⇒ x)
    def asDouble = asOptionDouble[Double](x ⇒ x).getOrElse(0.0)

    def asOptionBool[T](method: Boolean ⇒ T) = as[Boolean, T]({
      case LongValue(v) ⇒ Option(method(v == 1))
      case IntValue(v) ⇒ Option(method(v == 1))
      case StringValue(v) ⇒ Option(method(v == "1"))
      case RawValue(_type, _, true, bytes) ⇒ Option(method(new String(bytes) == "1"))
    })

    def asOptionBool = asOptionBool[Boolean](x ⇒ x)

    def asBool = asOptionBool[Boolean](x ⇒ x).getOrElse(false)

    def asOptionLong[T](method: Long ⇒ T) = as[Long, T]({
      case LongValue(v) ⇒ Option(method(v))
      case IntValue(v) ⇒ Option(method(v))
      case StringValue(v) ⇒ Option(method(v match {
        case "" ⇒ 0
        case _ ⇒ v.toLong
      }))
    })

    def asOptionLong = asOptionLong[Long](x ⇒ x)

    def asLong = asOptionLong[Long](x ⇒ x).getOrElse(0l)

    def asTimestamp[T](method: Long ⇒ T) = as[Long, T]({
      case timestampValueLocal(v) ⇒ Option(method(v.getTime))
    })

    def asTimestamp = asTimestamp[Long](x ⇒ x)

    def asOptionString[T](method: String ⇒ T) = as[String, T]({
      case StringValue(v) ⇒ Option(method(v))
      case LongValue(v) ⇒ Option(method(v.toString))
      case IntValue(v) ⇒ Option(method(v.toString))
      case DoubleValue(v) ⇒ Option(method(v.toString))
      case BigDecimalValue(v) ⇒ Option(method(v.toString))
      //TODO (Toan) 这里是日期的时候有问题
      case RawValue(t, _, _, bytes) if t == Type.Date || t == Type.DateTime || t == Type.DateTime ⇒
        implicit val fmt: SDate = t match {
          case Type.Date ⇒ SDateType
          case _ ⇒ STimestampType
        }
        val time = fromBytes(bytes)
        Option(method(time))
      case RawValue(_, cs, _, bytes) ⇒
        Option(method(new String(bytes, Charset(cs))))
      case _ ⇒ None
    })

    def asOptionString = asOptionString[String](x ⇒ x)

    def asString = asOptionString[String](x ⇒ x).getOrElse("")

    def asDate[T](method: Date ⇒ T) = as[Date, T]({ case DateValue(v) ⇒ Option(method(v)) })

    def asDate = asDate[Date](x ⇒ x)

    def asBytes[T](method: Bytes ⇒ T) = as[Bytes, T]({ case RawValue(_, _, _, v) ⇒ Option(method(v)) })

    def asBytes = asBytes[Bytes](x ⇒ x)

    private[this] def as[V, T](pf: PartialFunction[Any, Option[T]]): Option[T] = (v flatMap (pf orElse notMatch)) //.flatten

    private def notMatch[T](): PartialFunction[Value, Option[T]] = {
      case NullValue ⇒ None
      case x ⇒
        throw new IllegalArgumentException(s"参数错误 - ${x}")
    }

    private[this] def fromBytes(bytes: Array[Byte])(implicit d: SDate): String = {

      if (bytes.isEmpty) "0000-00-00 00:00:00" else {
        val br = MysqlBuf.reader(bytes)
        try {
          val ymd = (if (br.remaining >= 4) {
            s"${br.readUnsignedShortLE}-${br.readUnsignedByte}-${br.readUnsignedByte()}"
          } else { "0000-00-00" })
          d match {
            case STimestampType ⇒
              val hms = if (br.remaining >= 3) {
                s"${br.readUnsignedByte}:${br.readUnsignedByte}:${br.readUnsignedByte}"
              } else { "00:00:00" }
              s"${ymd} ${hms}"
            case SDateType ⇒ ymd
            case _ ⇒ throw new IllegalArgumentException(s"can not found Date type")
          }
        } finally br.close()
      }
    }
  }

}