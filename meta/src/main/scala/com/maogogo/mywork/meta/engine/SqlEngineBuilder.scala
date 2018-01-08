package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

class SqlEngineBuilder(req: ReportReq, tabProp: TableProperties) {

  val Log = LoggerFactory.getLogger(getClass)

  lazy val table = tabProp.table

  private[this] val groupingProps = req.grouping.getOrElse(Seq.empty).map(binding2Property)
  //指标
  private[this] val selectingProps = req.selecting.getOrElse(Seq.empty).map(PropertyBinding(_)).map(binding2Property)
  //条件
  lazy val filteringProps = req.filtering.getOrElse(Seq.empty).map(binding2Property)
  //符合
  lazy val combiningProps = selectingProps.filter(_.isSpecial).flatMap {
    prop ⇒
      prop.relateIds match {
        case Some(relateIds) if relateIds.nonEmpty ⇒
          Log.info(s"Property[${prop.id}] has relate ids ${relateIds}")
          relateIds.map((_, Option(prop.id))).map(findProperty)
        case _ ⇒
          Log.error(s"can not found relate ids for property[${prop.id}]")
          throw new ServiceException(s"can not found relate ids for property[${prop.id}]")
      }
  }

  //所有的指标(已经对复合指标做了处理)
  lazy val allSelectings = selectingProps.filterNot(_.isSpecial) ++ combiningProps

  lazy val isListing: Boolean = (req.isListing || req.selecting.isEmpty)

  //这里是全的property
  private[this] val allProperties: Seq[Property] = getAllGroupings ++ selectingProps ++ combiningProps

  lazy val headers = (getAllGroupings ++ selectingProps).map(toCellHeader)

  private[this] def toCellHeader: PartialFunction[Property, CellHeader] = {
    case prop ⇒
      CellHeader(prop.label, prop.cellLabel, prop.cellIndex)
  }

  def getAllGroupings: Seq[Property] = {
    (groupingProps.size == 0 && selectingProps.size == 0) match {
      case true ⇒ tabProp.properties.filter(_.propertyType == PropertyType.Grouping)
      case _ ⇒ groupingProps
    }
  }

  def createListingLabel(prefix: Option[String] = None): String = {
    allProperties.map(SqlTemplate.toAggregateColumn(prefix)).distinct.filterNot(_.isEmpty).mkString(", ")
  }

  def createAggregateLabel(group: PropertyGroup): String = {
    (allProperties.distinct.map { prop ⇒
      prop.propertyType match {
        case PropertyType.Grouping ⇒ SqlTemplate.toAggregateColumn(None)(prop)
        case PropertyType.Selecting ⇒
          if (prop.propertyGroup == group) SqlTemplate.toAggregateColumn(None)(prop) else "0"
        case _ ⇒ ""
      }
    }).filterNot(_.isEmpty).mkString(", ")

  }

  def getGroupingColumn: String = {
    groupingProps.distinct.map(_.cellLabel).filterNot(_.isEmpty).mkString(", ")
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