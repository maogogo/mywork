package com.maogogo.mywork.backend.hello

import com.google.inject.{ Provides, Singleton }
import io.finch._
import javax.inject.Inject
import com.maogogo.mywork.thrift._

@Provides @Singleton
class HelloEndpoints @Inject() (client: EngineService.MethodPerEndpoint, dao: HelloServiceDao) {

  def endpoints = hello

  val hello: Endpoint[String] = get("hello") {

    //    val selectings = Seq("s31") //, "s2", "s3")
    //    val groupings = Seq(PropertyBinding("g1", Option(Seq("11", "22")))) //+: Seq("g4", "g5").map(PropertyBinding(_))
    //
    //    val req = ReportReq("table_2", Option(selectings), Option(groupings), None, false, None, None)

    //    client.toQuerySql(req).map { x ⇒
    //      printHeader(x.headOption.map(_.headers))
    //      println(s"x ==>> \n ${x}")
    //      Ok(x.size.toString())
    //    }

    //    handle {
    //      case e: Throwable ⇒
    //        println("e==>>" + e)
    //        e.printStackTrace()
    //        Ok("aabbcc")
    //    }

    //client.hi("Toan").map(Ok)
    Ok(dao.aa)
  }

  def printHeader(headerOption: Option[Seq[CellHeader]]) = {
    headerOption match {
      case Some(headers) ⇒
        headers.foreach { header ⇒
          print(header.label + s"(${header.formulaScript.getOrElse("")})|\t")
        }
      case _ ⇒ println("no headers")
    }
  }

}