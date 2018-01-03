package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._

//
//import com.maogogo.mywork.thrift._
//import org.slf4j.LoggerFactory
//
trait SqlEngining {

  val req: ReportReq
  val tabProp: TableProperties

  //lazy val properties = tabProp.properties

  lazy val groupingProps = req.grouping.getOrElse(Seq.empty).map(binding2Property)
  //lazy val selectingProps = req.selecting.map(f)

  def hasSelecting: Boolean = req.selecting.isDefined

  def sqlTemplate(tableName: String, selecting: String, filtering: Option[String] = None,
    grouping: Option[String] = None, having: Option[String] = None) = {
    s"""
  SELECT ${selecting}
  FROM ${tableName}
  ${filtering.getOrElse("")}
  ${grouping.getOrElse("")}
  ${having.getOrElse("")}
"""
  }

  def binding2Property: PartialFunction[PropertyBinding, Property] = {
    case binding ⇒
      val propOption = tabProp.properties.find(_.id == binding.propertyId)
      //TODO(Toan) assert
      propOption.get.copy(values = binding.propertyValues)
  }

}

case class TableGroup(table: Table, grouping: Seq[Property], allSelecting: Seq[Property],
  inUseSelecting: Seq[Property], filtering: Seq[Property], propGroup: PropertyGroup) {

  def createListingColumns: Seq[String] = {

    //grouping.map(f)

    Seq.empty
  }
}
