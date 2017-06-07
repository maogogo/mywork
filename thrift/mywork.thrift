namespace java com.maogogo.mywork.thrift
#@namespace scala com.maogogo.mywork.thrift

typedef string SHARD_ID
typedef string TABLE_ID
typedef string PROPERTY_ID

enum ErrorCode {
  META_ERROR = 1
  ROOT_ERROR = 2
  MERGER_ERROR = 3
  LEAF_ERROR = 4
  SYS_ERROR = 9
}

exception ServiceException {
  1: string error
  2: optional ErrorCode code
}

#struct
struct Cell {
  1: string cell_label
  2: string cell_value
  3: optional string cell_type
}

struct Row {
  1: list<Cell> cells
}

struct CellHeader {
  1: string label
  2: optional string parent_label
  3: i32 cell_index
  4: optional i32 row_span
  5: optional i32 col_span
  6: optional i32 parent_row_span
  7: optional i32 parent_col_span
}

struct MasterOrSlave {
  1: i32 master_or_slave = 0
  2: optional i32 partition_random_index
}

struct Paging {
  1: i32 offset
  2: i32 limit
  3: optional map<SHARD_ID, i32>  skips
}

struct ServiceWatcher {
  1: i64 timeout
  2: i64 total_rows
  3: bool success
  4: optional string memo
}

struct Table {
  1: TABLE_ID id
  2: string db_table_name
  3: optional bool is_listing
  4: optional string db_schema
  5: optional string force_index
}

struct QuerySql {
  1: string sql
  2: optional string count_sql
  3: optional list<string> params
  4: i32 grouping_columns
  5: list<CellHeader> headers
}

struct PropertyBinding {
  1: PROPERTY_ID property_id
  2: optional list<string> property_values
}

enum PropertyType {
  SELECTING = 1
  GROUPING = 2
  FILTERING = 3
  COMBINING = 4
  UNKNOWN = 9
}

struct PropertySupport {
  1: optional list<string> values
  2: optional bool is_using = false
  3: optional string parent_id
}

struct Property {
  1:  PROPERTY_ID id
  2:  string label
  3:  optional string parent_label
  4:  PropertyType property_type
  5:  string cell_column
  6:  string cell_label
  7:  i32 cell_index = 1000
  8:  optional string cell_expression			#只对清单查询有效
  9:  optional string aggregation_method
  10: optional string cell_filters
  11: optional string having_filters
  12: optional string value_display_format
  13: optional string value_display_key
  14: optional string table_ex
  15: optional string unique_columns
  16: optional string level_columns
  17: bool is_fixed_show = false
  18: optional string formula_script
  19: optional string relate_ids
  20: optional PropertySupport support
}

struct TableProperty {
  1: Table table
  2: list<Property> properties
}

struct TablePropertyData {
  1: list<TableProperty> table_properties
}

struct RootQueryReq {
  1: TABLE_ID table_id
  2: optional list<PropertyBinding> selecting
  3: optional list<PropertyBinding> grouping
  4: optional list<PropertyBinding> filtering
  5: optional Paging paging
  6: optional MasterOrSlave master_or_slave
}

struct RootQueryResp {
  1: string query_id
  2: list<CellHeader> cell_headers
  3: list<Row> rows
  4: optional list<Cell> summary_row
}

service MetaService {
  string getRandomCache() throws (ServiceException e)
  i32 getRandomPartition() throws (ServiceException e)
}

service EngineService {
  list<QuerySql> engining(RootQueryReq req) throws (ServiceException e)
}

service RootService {
  RootQueryResp queryReport(RootQueryReq req) throws (ServiceException e)
}

struct MergerQueryReq {
  1: string sql
  2: optional list<string> params
  3: i32 grouping_columns
  4: optional MasterOrSlave master_or_slave
}

struct MergerQueryResp {
  1: list<Row> rows
  2: map<SHARD_ID, ServiceWatcher> watchers
}

service MergerService {
  MergerQueryResp queryReport(MergerQueryReq req) throws (ServiceException e)
}

service LeafService extends MergerService {
}

