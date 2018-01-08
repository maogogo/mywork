package com.maogogo.mywork.backend.hello

import com.google.inject.{ Provides, Singleton }
import io.finch._
import javax.inject.Inject
import com.maogogo.mywork.thrift._

@Provides @Singleton
class HelloEndpoints @Inject() (client: EngineService.MethodPerEndpoint) {

  def endpoints = hello

  val hello: Endpoint[String] = get("hello") {

    val selectings = Seq("s1", "s2")
    val groupings = PropertyBinding("g1", Option(Seq("11", "22"))) +: Seq("g4", "g5").map(PropertyBinding(_))

    val req = ReportReq("table_1", Option(selectings), Option(groupings), None, false, None, None)

    client.toQuerySql(req).map { x ⇒
      println(s"x ==>> \n ${x}")
      Ok(x.size.toString())
    }

    //    handle {
    //      case e: Throwable ⇒
    //        println("e==>>" + e)
    //        e.printStackTrace()
    //        Ok("aabbcc")
    //    }

    //client.hi("Toan").map(Ok)
    //    Ok("hello")
  }

}