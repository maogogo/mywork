package com.maogogo.mywork

import io.finch.internal._
import com.twitter.finagle.http._
import com.twitter.io._
import io.finch._
import org.json4s._
import org.json4s.ext._
import org.json4s.jackson.Serialization._

package object rest {

  implicit val formats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  implicit def encodeJson[A <: AnyRef]: ToResponse.Aux[A, Application.Json] =
    ToResponse.instance[A, Application.Json] { (a, cs) =>
      val resp = Response(Status.Ok)
      resp.contentType = "application/json"
      resp.content = Buf.Utf8(a)
      resp
    }

  private[this] implicit def writeJson[A <: AnyRef](a: A): String = {
    a match {
      case Error.NotPresent(e) =>
        wrappedError(400, s"exception#${e.description}")
      case Error.NotParsed(e, _, c) =>
        wrappedError(400, s"exception#${e.description}#${c.getMessage}")
      case Error.NotValid(e, _) =>
        wrappedError(400, s"exception#${e.description}")
      case e: Throwable =>
        wrappedError(error = e.getMessage)
      case x =>
        val cleaned = Extraction.decompose(x).removeField {
          case JField("_passthroughFields", _) => true
          case _ => false
        }
        write(Wrapped(removeHeadAndTail(cleaned)))
    }
  }

  private[this] def wrappedError(status: Int = 500, error: String): String = {
    write(Wrapped("", status, Some(error)))
  }

  private[this] def removeHeadAndTail(input: JValue): JValue = {
    input match {
      case JObject(List(("tail" | "head", rest))) => removeHeadAndTail(rest)
      case x => x
    }
  }

}

case class Wrapped[T](data: T, status: Int = 200, error: Option[String] = None)
