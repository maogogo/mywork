package com.maogogo.mywork.rest.hello

import io.finch._
import javax.inject.Inject
import com.maogogo.mywork.thrift._

class HelloEndpoints @Inject() (service: RootService.FutureIface) {

  def endpoints = hello

  val hello: Endpoint[String] = get("hello") {

    //    2: optional list<PropertyBinding> selecting
    //  3: optional list<PropertyBinding> grouping
    //  4: optional list<PropertyBinding> filtering
    //  5: optional Paging paging
    //  6: optional MasterOrSlave master_or_slave

    val selecting = Some(Seq(PropertyBinding("201")))
    val grouping = Some(Seq(PropertyBinding("101", Some(Seq("1111", "2222")))))
    service.queryReport(RootQueryReq("2", selecting, grouping))
    Ok("Hello")
  }
}