package com.maogogo.mywork.common.jdbc.druid

import javax.inject.Inject
import com.alibaba.druid.pool.DruidDataSource
import java.sql.Connection

class DruidDataSourcePool @Inject() (ds: DruidDataSource) {

  def client: Connection = ds.getConnection

}