package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

class SqlEngineBuilder(req: ReportReq, tabProp: TableProperties) {

  val Log = LoggerFactory.getLogger(getClass)

  lazy val table = tabProp.table

  lazy val allProps = tabProp.properties

  lazy val groupingProps = req.grouping.getOrElse(Seq.empty).map(binding2Property)
  //指标
  lazy val selectingProps = req.selecting.getOrElse(Seq.empty).map(PropertyBinding(_)).map(binding2Property)
  //条件
  lazy val filteringProps = req.filtering.getOrElse(Seq.empty).map(binding2Property)
  //符合
  lazy val combiningProps = selectingProps.filter(_.propertyType == PropertyType.Combining).map {
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

  lazy val commonGroupingProps = groupingProps.filter(SqlTemplate.filterCommonGrouping)

  lazy val headers = {
    //    groupingProps.fil
    (commonGroupingProps ++ groupingProps.filterNot(SqlTemplate.filterCommonGrouping)).map { prop ⇒
      CellHeader(prop.label, prop.parentId.getOrElse(""), None, prop.cellIndex, Option(1), Option(1), Option(0), Option(0))
    }
  }

  def createListingLabel(prefix: Option[String] = None): String = {
    val commonGroupingIds = commonGroupingProps.map(_.id)
    val otherGroupingProps = groupingProps.filterNot(x ⇒ commonGroupingIds.contains(x.id)).map(SqlTemplate.toLabel)
    (commonGroupingProps.map(SqlTemplate.toLabel(prefix)) ++ otherGroupingProps).mkString(", ")
  }

  def binding2Property: PartialFunction[PropertyBinding, Property] = {
    case binding ⇒
      val prop = findProperty(binding.propertyId, None)
      prop.copy(values = binding.propertyValues)
  }

  /**
   * input  : id, Option(parentId)
   * output : Property
   */
  def findProperty: PartialFunction[(String, Option[String]), Property] = {
    case (id, parentId) ⇒
      val propOption = allProps.find(_.id == id)
      if (propOption.isEmpty)
        throw new ServiceException(s"can not found property by id[${id}]")
      propOption.get.copy(parentId = parentId)
  }
}