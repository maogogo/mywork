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

union ThriftAnyVal {
  1: i32 int_val
  2: i64 long_val
  3: string string_val
  4: bool bool_val
  6: double double_val
}

struct Cell {
  1: ThriftAnyVal cell_value
  2: string cell_label
}

struct Row {
  1: list<Cell> cells
}

struct CellHeader {
  1: string label
  2: PROPERTY_ID property_id
  3: string cell_label
  4: optional string value_display_format
  5: optional string formula_script
  6: optional list<i32> offset
}

struct PropertyBinding {
  1: PROPERTY_ID property_id
  2: optional list<string> property_values
}

enum PropertyType {
  GROUPING 			= 1
  SELECTING 			= 2
  UNKNOWN 			= 9
}

struct PropertyExpression {
  1: string id
  2: PROPERTY_ID property_id
  3: string label
  4: string cell_expression
}

struct PropertyHaving {
  1: string id
  2: string label
  3: string hfilter_column
  4: string hfilter_method
  5: string hfilter_symbol
  6: string hfilter_value
}

struct PropertyGroup {
  1: optional string table_ex
  2: optional string selecting_filtering
  3: optional list<string> grouping_columns						# 分组字段
  4: optional list<string> unique_columns						# 去重字段
  5: optional PropertyHaving property_having
}

struct Property {
  1:  PROPERTY_ID id
  2:  string label
  3:  PropertyType property_type 								# 字段类型
  4:  bool is_special = false									# 特殊性
  5:  i32 cell_index = 9999										# 排序
  6:  string cell_column											# 列名
  7:  string cell_label											# 虚拟列名
  8:  optional string cell_filtering								# 过滤条件
  9:  optional string cell_value									# 指标 配合过滤条件使用
  10: optional string aggregation_method 						# 汇聚方法
  11: optional string value_display_format
  12: optional string formula_script
  13: optional list<string> relate_ids							# 关联字段
  14: optional list<PropertyExpression> property_expressions		# 字段转换
  15: optional list<string> values								# 维度 where条件 参数
  16: optional PROPERTY_ID parent_id								# 复合指标有用
  17: PropertyGroup property_group
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
  2: string label
  3: string table_name
  4: optional string db_schema
  5: bool is_listing = false
  6: optional string system_id
}

struct TableProperties {
  1: Table table
  2: list<Property> properties
}

struct QueryReq {
  1: string sql
  2: optional string count_sql
  3: optional list<string> params
  4: i32 grouping_columns
  5: list<CellHeader> headers
  6: i32 data_skips = 0
}

struct QueryResp {
  1: list<Row> rows
  2: map<SHARD_ID, ServiceWatcher> watchers
}
