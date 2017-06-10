package com.maogogo.mywork.common.dispatch

import com.maogogo.mywork.thrift._
import org.apache.commons.lang3.math.NumberUtils

trait SimpleMergering {

  def mergeMergerReportResp(q: MergerQueryReq, r1: MergerQueryResp, r2: MergerQueryResp): MergerQueryResp = {
    val allRows = r1.rows ++ r2.rows
    val groupingColumns = q.groupingColumns
    val watchers = r1.watchers ++ r2.watchers

    //1、先排序
    //2、合并数据或者数据去重  // TODO(litao) 考虑一下数据去重
    val aggregatedRows = groupAndAggregateRows(allRows, groupingColumns)

    MergerQueryResp(aggregatedRows, watchers)
  }

  def groupAndAggregateRows(rows: Seq[Row], n: Int): Seq[Row] = {

    rows.groupBy(_.cells.take(n))
      .map { t =>
        val (group, rows) = t
        val aggregated = rows.transpose { _.cells.drop(n) }.map { _.reduce(toCell(_, _)) }

        Row(group ++ aggregated)
      }.toSeq
  }

  def toCell(c1: Cell, c2: Cell) =
    c1.copy(cellValue = (NumberUtils.toDouble(c1.cellValue) + NumberUtils.toDouble(c2.cellValue)).toString)

}

object SimpleMergering extends SimpleMergering