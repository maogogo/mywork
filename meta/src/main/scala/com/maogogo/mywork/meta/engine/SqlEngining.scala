package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

//
//import com.maogogo.mywork.thrift._
//import org.slf4j.LoggerFactory
//
trait SqlEngining {

  val builder: SqlEngineBuilder
  def packing: Seq[QuerySql]

}

//object SqlEngining extends SqlEngining

case class SqlTable(table: Table, groupings: Seq[Property], filterings: Seq[Property], group: Option[PropertyGroup]) {

  lazy val tableName = table.tableName + group.flatMap(_.tableEx).getOrElse("")

  lazy val allFilters = groupings ++ filterings

  lazy val commonGrouping = groupings.filter(SqlTemplate.filterCommonGrouping)

  def createListingSql: String = {
    SqlTemplate(tableName, getListingColumn, getListingAdapter)
  }

  def createJoinOn(index: Int): String = {
    commonGrouping.map {
      case prop ⇒
        s"${SqlTemplate.asName(index)}.${prop.cellLabel}=${SqlTemplate.asName(index + 1)}.${prop.cellLabel}"
    } mkString (" AND ")
  }

  def getListingColumn: String =
    groupings.map(SqlTemplate.toListingColumn).distinct.filterNot(_.isEmpty).mkString(", ")

  def getParams: Option[Seq[String]] = {

    ???
  }

  def getListingParams: Seq[String] =
    allFilters.filter(SqlTemplate.filterListingAdaper).map(_.values).flatten.flatten

  def getAggregateParams: Seq[String] =
    allFilters.filter(SqlTemplate.filterAggregateAdaper).map(_.values).flatten.flatten

  def getListingAdapter: Option[String] = {
    Option(allFilters.filter(SqlTemplate.filterListingAdaper).map(_.cellFiltering.getOrElse("")).distinct.filterNot(_.isEmpty).mkString(" AND "))
  }

  def getAggregateAdaper: Option[String] = {
    Option(allFilters.filter(SqlTemplate.filterAggregateAdaper).map(_.cellFiltering.getOrElse("")).distinct.filterNot(_.isEmpty).mkString(" AND "))
  }

}

//  val Log = LoggerFactory.getLogger(getClass)
//
//  val req: ReportReq
//  val tabProp: TableProperties
//  //维度
//  lazy val groupingProps = req.grouping.getOrElse(Seq.empty).map(binding2Property)
//  //指标
//  lazy val selectingProps = req.selecting.getOrElse(Seq.empty).map(PropertyBinding(_)).map(binding2Property)
//  //条件
//  lazy val filteringProps = req.filtering.getOrElse(Seq.empty).map(binding2Property)
//  //符合
//  lazy val combiningProps = selectingProps.filter(_.propertyType == PropertyType.Combining).map {
//    prop ⇒
//      prop.relateIds match {
//        case Some(relateIds) if relateIds.nonEmpty ⇒
//          Log.info(s"Property[${prop.id}] has relate ids ${relateIds}")
//          relateIds.map((_, Option(prop.id))).map(findProperty)
//        case _ ⇒
//          Log.error(s"can not found relate ids for property[${prop.id}]")
//          throw new ServiceException(s"can not found relate ids for property[${prop.id}]")
//      }
//  }
//
//  /**
//   * 有没有指标
//   */
//  def hasSelecting: Boolean = req.selecting.isDefined
//
//  def sqlTemplate(tableName: String, selecting: String, filtering: Option[String] = None,
//    grouping: Option[String] = None, having: Option[String] = None) = {
//    s"""
//  SELECT ${selecting}
//  FROM ${tableName}
//  ${filtering.getOrElse("")}
//  ${grouping.getOrElse("")}
//  ${having.getOrElse("")}
//"""
//  }
//
//  def binding2Property: PartialFunction[PropertyBinding, Property] = {
//    case binding ⇒
//      val prop = findProperty(binding.propertyId, None)
//      prop.copy(values = binding.propertyValues)
//  }
//
//  def findProperty: PartialFunction[(String, Option[String]), Property] = {
//    case (id, parentId) ⇒
//      val propOption = tabProp.properties.find(_.id == id)
//      if (propOption.isEmpty)
//        throw new ServiceException(s"can not found property by id[${id}]")
//      propOption.get.copy(parentId = parentId)
//  }
//
//}
//
//case class TableGroup(table: Table, grouping: Seq[Property], allSelecting: Seq[Property],
//  inUseSelecting: Seq[Property], filtering: Seq[Property], propGroup: PropertyGroup) {
//
//  def createListingColumns: Seq[String] = {
//
//    //grouping.map(f)
//
//    Seq.empty
//  }
//}
//
//case class

