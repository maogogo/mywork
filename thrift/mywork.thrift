namespace java com.maogogo.mywork.thrift
#@namespace scala com.maogogo.mywork.thrift

typedef string SHARD_ID

#struct
struct Cell {
  1: string cell_label
  2: string cell_value
  3: optional string cell_type
}

struct Row {
  1: list<Cell> cells
}

enum Relating {
  AND = 1
  OR = 2
}

enum JoinOuter {
  LEFT = 1
  RIGHT = 2
  INNER = 3
  OUTER = 4
}

enum Sorted {
  ASC = 1
  DESC = 2
}

enum Aggregated {
  COUNT = 1
  SUM = 2
  AVG = 3
  MAX = 4
  MIN = 5
}

struct Paging {
  1: i32 offset
  2: i32 limit
}

struct Selecting {
  1: string cell_label
  2: optional Aggregated aggregated
}

enum Judgment {
  EQ = 1
  GT = 2
  GE = 3
  LT = 4
  LE = 5
  NE = 6
  LK = 7
  IN = 8
}

struct Filtering {
  1: string cell_label
  2: string cell_value
  3: optional Judgment judgment 
  3: optional Relating relating
}

struct FilterBinding {
  1: list<Filtering> filerings
  2: Relating relating
}

struct Sorting {
  1: string cell_label
  2: optional Sorted sorted
}

struct QueryBinding {
  1: optional list<Selecting> selectings	#select 字段
  2: optional list<FilterBinding> filter_bindings	#where条件
  3: optional list<string> groupings	#分组
  4: optional list<Sorting> sortings	#排序
  5: optional list<string> havings	#暂不支持
}

struct RootQueryReq {
  1: string table_name
  2: optional string alias
  3: optional string db_source
  4: optional QueryBinding query_binding
  5: optional JoinOuter join_outer
  6: optional Paging paging
}

struct RootUnionQueryReq {
  1: list<RootQueryReq> queries
  2: optional QueryBinding query_binding
}

enum Stutus {
  SUCCESS = 1
  ERROR = 9 
}

struct Running {
  1: SHARD_ID shard_id
  2: Stutus status
  3: i64 total_record
  4: i64 timeout
  5: optional string memo
}

struct RootQueryResp {
  1: list<Row> rows
  2: i64 total_record
  3: optional map<SHARD_ID, Running> running_map
}

enum StructType {
  BOOL = 1
  VARCHAR = 2
  VARCHAR2 = 3
  CHAR = 4
  DATE = 5
  INT = 6
  FLOAT = 7
}

struct StructField {
  1: string label
  2: optional StructType struct_type
  3: bool is_primary_key = false
  4: bool is_null_able = true
  5: optional i32 precision
  6: optional i32 scale
}

struct RootExecuteReq {
  1: string table_name
  2: optional string db_source
  3: optional list<StructField> struct_fields
  4: optional string chartset
}

struct RootExecuteResp {
  1: i64 total_record
}

struct RootInsertReq {
  1: string table_name 
  2: list<StructField> struct_fields
  3: optional string db_source
}

struct RootUpdateReq {
  1: string table_name
  2: list<StructField> struct_fields
  3: list<StructField> key_struct_fields
  4: optional string db_source
}

struct RootDeleteReq {
  1: string table_name
  2: list<StructField> key_struct_fields
  3: optional string db_source
}

struct ExecuteUpdateReq {
  1: string sql
  2: optional list<string> params
  3: optional string db_source
  4: optional string chartset
}

struct ExecuteQueryReq {
  1: string sql
  2: optional list<string> params
  3: optional i32 grouing_columns
  4: optional string db_source
}

service RootService {
  RootExecuteResp execute(RootExecuteReq req)
  RootExecuteResp insert(RootInsertReq req)
  RootExecuteResp update(RootUpdateReq req)
  RootExecuteResp delete(RootDeleteReq req)
  RootQueryResp selectOne(RootQueryReq req)
  RootQueryResp selectUnion(RootUnionQueryReq req)
  RootExecuteResp executeUpdate(ExecuteUpdateReq req)
  RootQueryResp executeQuery(ExecuteQueryReq req)
}

struct MergerExecuteReq {
  1: string sql
  2: optional list<string> params
  3: optional string db_source
  4: optional string chartset
}

struct MergerExecuteResp {
  1: i64 total_record
  2: optional map<SHARD_ID, Running> running_map
}

struct MergerQueryReq {
  1: string sql
  2: optional list<string> params
  3: optional i32 grouing_columns
  4: optional string db_source
}

struct MergerQueryResp {
  1: list<Row> rows
  2: i64 total_record
  3: optional map<SHARD_ID, Running> running_map
}

service MergerService {
  MergerExecuteResp executeUpdate(ExecuteUpdateReq req)
  MergerQueryResp executeQuery(ExecuteQueryReq req)
}
