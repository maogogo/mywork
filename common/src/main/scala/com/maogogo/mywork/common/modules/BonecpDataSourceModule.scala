package com.maogogo.mywork.common.modules

trait BonecpDataSourceModule { self =>

  //  @Provides @Singleton
  //  def provideDataSource(@Inject() config: Config): DataSource = {
  //    Class.forName("com.facebook.presto.jdbc.PrestoDriver")
  //    val ds = new BoneCPDataSource
  //    ds.setJdbcUrl("jdbc:presto://192.168.0.200:9900/hive/default")
  //    ds.setUsername(System.getProperty("user.name"))
  //    ds.setPassword(null)
  //    ds.setLazyInit(true)
  //    ds.setPartitionCount(4)
  //    ds.setAcquireIncrement(4)
  //    ds.setMinConnectionsPerPartition(10)
  //    ds.setMaxConnectionsPerPartition(150)
  //    ds
  //  }

}