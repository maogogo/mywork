namespace java com.maogogo.mywork.thrift
#@namespace scala com.maogogo.mywork.thrift

include "common.thrift"

struct ReportReq {
  1: common.TABLE_ID table_id
  2: optional list<common.PropertyBinding> selecting
  3: optional list<common.PropertyBinding> grouping
  4: optional list<common.PropertyBinding> filtering
  5: optional common.Paging paging
  6: optional common.MasterOrSlave master_or_slave
}

struct ReportResp {
  1: string query_id
  2: list<common.CellHeader> cell_headers
  3: list<common.Row> rows
  4: optional list<common.Cell> summary_row
  5: map<common.SHARD_ID, common.ServiceWatcher> watchers
}

struct ExecuteResp {
  1: string query_id
  2: list<common.CellHeader> cell_headers
  3: map<common.SHARD_ID, common.ServiceWatcher> watchers
}

service MetaService {
  string getRandomCache() throws (common.ServiceException e)
  i32 getRandomPartition() throws (common.ServiceException e)
}

service EngineService {
  list<common.QuerySql> toQuerySql(ReportReq req) throws (common.ServiceException e)
}

service RootService {
  ReportResp queryReport(ReportReq req) throws (common.ServiceException e)
  ReportResp executeReport(common.QuerySql req) throws (common.ServiceException e)
  
  ExecuteResp queryToStaging(ReportReq req) throws (common.ServiceException e)
  ExecuteResp executeToStaging(common.QuerySql req) throws (common.ServiceException e)
}

struct MergerQueryReq {
  1: common.QuerySql query_sql
}

struct MergerQueryResp {
  1: list<common.Row> rows
  2: map<common.SHARD_ID, common.ServiceWatcher> watchers
}

service MergerService {
  MergerQueryResp queryReport(MergerQueryReq req) throws (common.ServiceException e)
}

service LeafService extends MergerService {
}

