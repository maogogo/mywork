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
  2: PROPERTY_ID property_id
  3: optional string parent_label
  4: i32 cell_index
  5: optional i32 row_span
  6: optional i32 col_span
  7: optional i32 parent_row_span
  8: optional i32 parent_col_span
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

struct Property {
  1:  PROPERTY_ID id
  2:  string label
  3:  PropertyType property_type 								#字段类型
  4:  i32 cell_index = 9999										#排序
  5:  string cell_column											#列名
  6:  string cell_label											#虚拟列名
  7:  optional string cell_filtering								#过滤条件
  9:  optional string selecting_filtering						#指标固定条件
  10: optional string cell_value									#指标 配合过滤条件使用
  11: optional string aggregation_method 						#汇聚方法
  12: optional list<string> grouping_columns						#分组字段
  13: optional list<string> having_columns						#过滤分组
  14: optional string hfilter_id
  15: optional PropertyHaving property_having
  16: optional string value_display_format
  17: optional string formula_script
  18: optional list<string> relate_ids							#关联字段
  19: optional string table_ex									#并联表
  20: optional list<PropertyExpression> property_expressions		#字段转换
  21: optional string table_cell_column							# 所在表对应的where条件
  22: optional string table_cell_filtering						# 所在表对应的字段
  23: optional list<string> values								# 维度 where条件 参数
  24: optional string parent_id									# 复合指标有用
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

struct QuerySql {
  1: string sql
  2: optional string count_sql
  3: optional list<string> params
  4: i32 grouping_columns
  5: list<CellHeader> headers
}