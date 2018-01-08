package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

class SqlEngineBuilder(req: ReportReq, tabProp: TableProperties) {

  val Log = LoggerFactory.getLogger(getClass)

  lazy val table = tabProp.table

  private[this] val groupingProps = req.grouping.getOrElse(Seq.empty).map(binding2Property)
  //指标
  lazy val selectingProps = req.selecting.getOrElse(Seq.empty).map(PropertyBinding(_)).map(binding2Property)
  //条件
  lazy val filteringProps = req.filtering.getOrElse(Seq.empty).map(binding2Property)
  //符合
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

  //lazy val commonGroupingProps = groupingProps.filter(SqlTemplate.filterCommonGrouping)

  lazy val isListing: Boolean = (req.isListing || req.selecting.isEmpty)
  private[this] val allProperties: Seq[Property] = getAllGroupings ++ selectingProps

  lazy val headers = allProperties.map(toCellHeader)

  private[this] def toCellHeader: PartialFunction[Property, CellHeader] = {
    case prop ⇒
      val rowSpan = Option(1)
      val parentSpan = Option(0)
      CellHeader(prop.label, prop.parentId.getOrElse(""), None, prop.cellIndex, rowSpan, rowSpan, parentSpan, parentSpan)
  }

  def getAllGroupings: Seq[Property] = {
    groupingProps.size match {
      case 0 ⇒ tabProp.properties.filter(p ⇒ p.propertyType == PropertyType.Grouping || p.propertyType == PropertyType.CommonGrouping)
      case _ ⇒ groupingProps
    }
  }

  def createListingLabel(prefix: Option[String] = None): String = {
    allProperties.map(SqlTemplate.toAggregateColumn(prefix)).distinct.filterNot(_.isEmpty).mkString(", ")
  }

  def createAggregateLabel(group: PropertyGroup): String = {
    (allProperties.map { prop ⇒
      prop.propertyType match {
        case PropertyType.Grouping | PropertyType.CommonGrouping ⇒
          SqlTemplate.toAggregateColumn(None)(prop)
        case PropertyType.Selecting | PropertyType.Combining ⇒
          if (prop.propertyGroup == group) SqlTemplate.toAggregateColumn(None)(prop) else "0"
        case _ ⇒ ""
      }
    }).distinct.filterNot(_.isEmpty).mkString(", ")

  }

  def getGroupingColumn: String = {
    groupingProps.map(_.cellLabel).distinct.filterNot(_.isEmpty).mkString(", ")
  }

  private[this] def binding2Property: PartialFunction[PropertyBinding, Property] = {
    case binding ⇒
      val prop = findProperty(binding.propertyId, None)
      prop.copy(values = binding.propertyValues)
  }

  /**
   * input  : id, Option(parentId)
   * output : Property
   */
  private[this] def findProperty: PartialFunction[(String, Option[String]), Property] = {
    case (id, parentId) ⇒
      val propOption = tabProp.properties.find(_.id == id)
      if (propOption.isEmpty)
        throw new ServiceException(s"can not found property by id[${id}]")
      propOption.get.copy(parentId = parentId)
  }
}