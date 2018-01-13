package com.maogogo.mywork.meta.engine

import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

class ListingSqlEngining(implicit val builder: SqlEngineBuilder) extends SqlEngining {

  lazy val Log = LoggerFactory.getLogger(getClass)

  def packing: Seq[QueryReq] = {
    // grouping 分组
    val sqlTables = createSqlTable
    // 创建子查询
    val tempTable = createTempTable(sqlTables)
    // 前缀
    val prefix = if (sqlTables.size == 1) None else Option("A.")
    // 创建SQL
    val sql = SqlTemplate(tempTable, builder.createListingLabel(prefix), getAdapter(sqlTables))
    Log.info(s"sql => \n ${sql}")

    println("builder.getAllGroupings.size ==>>" + builder.getAllGroupings.size)

    println("builder.headers ==>>\n" + builder.headers)

    println("params ===>>>> " + getParams(sqlTables))

    //TODO(Toan) 这里缺少 sort 和 limit
    Seq(QueryReq(sql, None, getParams(sqlTables), builder.getAllGroupings.size, builder.headers))
  }

  /**
   * 创建子查询关联表
   */
  def createTempTable(sqlTables: Seq[SqlTable]): String = {
    sqlTables.zipWithIndex.foldLeft("") { (fragment, sqlTableWithIndex) ⇒
      val (sqlTable, index) = sqlTableWithIndex
      //生成临时表
      val tempSql = s"(${sqlTable.createListingSql}) ${SqlTemplate.asName(index)}"
      fragment match {
        case x if x.nonEmpty ⇒ s"${x} INNER JOIN ${tempSql} ON ${sqlTable.createJoinOn(index)}"
        case _ ⇒ tempSql
      }
    }
  }

  /**
   * 获取参数
   * 分为两层获取
   */
  def getParams(sqlTables: Seq[SqlTable]): Option[Seq[String]] =
    Option(sqlTables.flatMap(_.getListingParams) ++ sqlTables.flatMap(_.getAggregateParams))

  /**
   * 获取where条件(这里是第二层的where条件)
   * TODO(Toan)如果这里有重复的条件有问题, 这里 把 distinct 去掉了 ,要是有重复的时候会有问题
   */
  def getAdapter(sqlTables: Seq[SqlTable]): Option[String] =
    Option(sqlTables.flatMap(_.getAggregateAdaper).filterNot(_.isEmpty).mkString(" AND "))

  /**
   * 创建SqlTable
   */
  def createSqlTable: Seq[SqlTable] = {
    //通用维度字段
    val commonGrouping = builder.getAllGroupings.filter(SqlTemplate.filterCommonGrouping)
    // 维度分组
    val propGroups = builder.getAllGroupings.filterNot(SqlTemplate.filterCommonGrouping)
      .groupBy(_.propertyGroup).toSeq.sortBy(_._1.tableEx.getOrElse("")).map {
        case (group, props) ⇒
          SqlTable(builder.table, commonGrouping ++ props, builder.filteringProps, Option(group))
      }

    propGroups.size match {
      case 0 ⇒ Seq(SqlTable(builder.table, builder.getAllGroupings, builder.filteringProps, None))
      case _ ⇒ propGroups
    }
  }
}
