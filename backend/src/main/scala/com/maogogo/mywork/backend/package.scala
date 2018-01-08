package com.maogogo.mywork

import io.finch.internal._
import com.twitter.finagle.http._
import com.twitter.io._
import io.finch._
import org.json4s._
import org.json4s.ext._
import org.json4s.jackson.Serialization._
import org.json4s.jackson.JsonMethods._
import com.twitter.util.Try

package object backend {
  implicit val formats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  //  tr: ToResponse.Aux[A, Application.Json],
  //    tre: ToResponse.Aux[Exception, Application.Json]

  implicit def decode[A: Manifest]: Decode.Json[A] =
    Decode.instance[A, Application.Json]((buf, cs) ⇒ Try(parse(buf.asString(cs), false).extract[A]))
  //
  implicit def encodeJson[A <: AnyRef]: Encode.Json[A] = {
    //  ToResponse.Aux[A, Application.Json] = {
    //    ToResponse.instance[A, Application.Json] { (a, cs) ⇒
    //      val resp = Response(Status.Ok)
    //      resp.contentType = "application/json"
    //      resp.content = Buf.Utf8(a)
    //      //resp.content
    //      resp
    //    }
    Encode.instance[A, Application.Json]((a, _) ⇒ {
      println("aaa ==>>" + a)
      Buf.Utf8(a)
    })
  }

  //
  private[this] implicit def writeJson[A <: AnyRef](a: A): String = {
    a match {
      case Error.NotPresent(e) ⇒
        wrappedError(400, s"exception#${e.description}")
      case Error.NotParsed(e, _, c) ⇒
        wrappedError(400, s"exception#${e.description}#${c.getMessage}")
      case Error.NotValid(e, _) ⇒
        wrappedError(400, s"exception#${e.description}")
      case e: Throwable ⇒
        e.printStackTrace
        wrappedError(error = e.getMessage)
      case x ⇒

        println("xx===ee ==>>" + x)
        val cleaned = Extraction.decompose(x).removeField {
          case JField("_passthroughFields", _) ⇒ true
          case _ ⇒ false
        }
        write(Wrapped(removeHeadAndTail(cleaned)))
    }
  }
  //
  private[this] def wrappedError(status: Int = 500, error: String): String = {
    write(ErrorWrapped(error, status))
  }
  //
  private[this] def removeHeadAndTail(input: JValue): JValue = {
    input match {
      case JObject(List(("tail" | "head", rest))) ⇒ removeHeadAndTail(rest)
      case x ⇒ x
    }
  }
}

case class Wrapped[T](data: T, status: Int = 200)
case class ErrorWrapped(error: String, status: Int = 200)