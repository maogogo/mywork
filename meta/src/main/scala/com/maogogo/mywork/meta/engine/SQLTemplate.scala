package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._

trait SQLTemplate {

  /**
   * 主要是清单字段
   * TODO(Toan)这里的label需要处理
   */
  def toListingColumn: PartialFunction[Property, String] = {
    case prop ⇒
      prop.propertyType match {
        case PropertyType.Selecting ⇒
          prop.cellFiltering match {
            case Some(filtering) if filtering.nonEmpty ⇒
              //TODO(Toan) cellValue match case
              s"(case when ${filtering} then ${prop.cellValue.getOrElse("")} else 0 end)"
            case _ ⇒ prop.cellColumn
          }
        case x if x == PropertyType.Grouping || x == PropertyType.CommonGrouping || x == PropertyType.Filtering ⇒
          prop.propertyExpressions match {
            case Some(expressions) if expressions.nonEmpty ⇒
              val whenEx = expressions.foldLeft("") { (s, e) ⇒
                s"when ${e.cellExpression} then ${e.label} "
              }
              s"(case ${whenEx} end) as '${prop.cellLabel}'"
            case _ ⇒ prop.cellColumn
          }
        case PropertyType.Combining ⇒ ""
        case _ ⇒ ""
      }
  }

}

object SQLTemplate extends SQLTemplate