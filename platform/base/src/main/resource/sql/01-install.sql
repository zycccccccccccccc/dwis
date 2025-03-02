
-- 简化的创建表注释存储过程
IF EXISTS(SELECT TOP 1 * FROM sysobjects WHERE id = OBJECT_ID(N'add_table_desc') AND xtype = 'P')
DROP PROCEDURE add_table_desc;
GO

CREATE PROCEDURE add_table_desc
  @tableName VARCHAR(200),
  @description VARCHAR(1000)
AS
  EXEC sp_addextendedproperty 'MS_Description', @description, 'schema', 'dbo', 'table', @tableName;
GO


-- 简化的创建列注释存储过程
IF EXISTS(SELECT TOP 1 * FROM sysobjects WHERE id = OBJECT_ID(N'add_column_desc') AND xtype = 'P')
DROP PROCEDURE add_column_desc;
GO

CREATE PROCEDURE add_column_desc
  @tableName VARCHAR(200),
  @columnName VARCHAR(200),
  @description VARCHAR(1000)
AS
  EXEC sp_addextendedproperty 'MS_Description', @description, 'schema', 'dbo', 'table', @tableName, 'column', @columnName;
GO


-- 简化的删除表的存储过程
IF EXISTS(SELECT TOP 1 * FROM sysobjects WHERE id = OBJECT_ID(N'p_drop_table') AND xtype = 'P')
  DROP PROCEDURE p_drop_table;
GO

CREATE PROCEDURE p_drop_table
  @objectName VARCHAR(200)
AS
BEGIN
DECLARE @sqlstr VARCHAR(200);
  SET @sqlstr = 'DROP TABLE ' + @objectName;
  IF EXISTS(SELECT TOP 1 * FROM sysobjects WHERE id = OBJECT_ID(@objectName) AND xtype = 'U')
    EXEC (@sqlstr);
END;
GO


-- 简化的删除视图的存储过程
IF EXISTS(SELECT TOP 1 * FROM sysobjects WHERE id = OBJECT_ID(N'p_drop_view') AND xtype = 'P')
  DROP PROCEDURE p_drop_view;
GO

CREATE PROCEDURE p_drop_view
  @objectName VARCHAR(200)
AS
BEGIN
DECLARE @sqlstr VARCHAR(200);
  SET @sqlstr = 'DROP VIEW ' + @objectName;
  IF EXISTS(SELECT TOP 1 * FROM sysobjects WHERE id = OBJECT_ID(@objectName) AND xtype = 'V')
    EXEC (@sqlstr);
END;
GO

EXEC p_drop_table 'account';
CREATE TABLE account (
  id VARCHAR(32) NOT NULL PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(256) NOT NULL,
  status TINYINT NOT NULL,
  nickname VARCHAR(50),
  mobile VARCHAR(11),
  email VARCHAR(50),
  avatar text,
  photo_url VARCHAR(256),
  login_status TINYINT NULL DEFAULT NULL,
  location VARCHAR(2048) DEFAULT 'all' NOT NULL,
  team_leader_id VARCHAR(32),
  dep_id INT,
  station_id INT,
  memo VARCHAR(32),
  create_time DATETIME,
  enabled TINYINT NOT NULL,
  is_leader TINYINT NOT NULL
);
create unique index uk_account_username on account(username);
GO

EXEC add_table_desc 'account', '账号表'
EXEC add_column_desc 'account', 'id', 'ID'
EXEC add_column_desc 'account', 'username', '用户名(工号)'
EXEC add_column_desc 'account', 'password', '密码'
EXEC add_column_desc 'account', 'status', '状态'
EXEC add_column_desc 'account', 'nickname', '姓名'
EXEC add_column_desc 'account', 'mobile', '手机号'
EXEC add_column_desc 'account', 'email', '邮箱'
EXEC add_column_desc 'account', 'avatar', '头像'
EXEC add_column_desc 'account', 'photo_url', '头像url'
EXEC add_column_desc 'account', 'login_status', '登录状态'
EXEC add_column_desc 'account', 'location', '模块'
EXEC add_column_desc 'account', 'team_leader_id', '工长工号'
EXEC add_column_desc 'account', 'dep_id', '部门ID'
EXEC add_column_desc 'account', 'station_id', '岗位ID'
EXEC add_column_desc 'account', 'memo', '备注'
EXEC add_column_desc 'account', 'create_time', '创建时间'


EXEC p_drop_table 'oauth_client_details';
CREATE TABLE oauth_client_details (
  client_id                 VARCHAR(256)        NOT NULL PRIMARY KEY,
  resource_ids              VARCHAR(256),
  client_secret             VARCHAR(256),
  scope                   VARCHAR(256),
  authorized_grant_types    VARCHAR(256),
  web_server_redirect_uri   VARCHAR(256),
  authorities               VARCHAR(256),
  access_token_validity     int,
  refresh_token_validity    int,
  additional_information    VARCHAR(4096),
  autoapprove               VARCHAR(256)
);


EXEC p_drop_table 'authority';
CREATE TABLE authority (
  id VARCHAR(32) NOT NULL PRIMARY KEY,
  authority_name VARCHAR(50) NOT NULL,
  descritpion VARCHAR(100) DEFAULT NULL,
  parent_id VARCHAR(32) DEFAULT NULL,
);

EXEC add_table_desc 'authority', '权限表'
EXEC add_column_desc 'authority', 'id', 'ID'
EXEC add_column_desc 'authority', 'authority_name', '权限名称'
EXEC add_column_desc 'authority', 'descritpion', '权限描述'
EXEC add_column_desc 'authority', 'parent_id', '父权限名称'


EXEC p_drop_table 'role';
CREATE TABLE role (
  id VARCHAR(32) NOT NULL PRIMARY KEY,
  role_name VARCHAR(50) NOT NULL ,
  remark VARCHAR(100) DEFAULT NULL
);

EXEC add_table_desc 'role', '角色表'
EXEC add_column_desc 'role', 'id', 'ID'
EXEC add_column_desc 'role', 'role_name', '角色名称'
EXEC add_column_desc 'role', 'remark', '角色中文描述'


EXEC p_drop_table 'account_role';
CREATE TABLE account_role (
  id VARCHAR(32) NOT NULL PRIMARY KEY,
  account_id VARCHAR(32) NOT NULL ,
  role_id VARCHAR(32) NOT NULL
);

EXEC add_table_desc 'account_role', '账号角色关系表'
EXEC add_column_desc 'account_role', 'id', 'ID'
EXEC add_column_desc 'account_role', 'account_id', '账号ID'
EXEC add_column_desc 'account_role', 'role_id', '角色ID'


EXEC p_drop_table 'role_authority';
CREATE TABLE role_authority (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  role_id VARCHAR(32) NOT NULL,
  authority_id VARCHAR(32) NOT NULL
);

EXEC add_table_desc 'role_authority', '角色权限关系表'
EXEC add_column_desc 'role_authority', 'role_id', '角色ID'
EXEC add_column_desc 'role_authority', 'authority_id', '权限ID'



EXEC p_drop_table 'operation_log';
CREATE TABLE operation_log (
  id                        INT        IDENTITY NOT NULL PRIMARY KEY,
  username                  VARCHAR(64)             NULL,
  operation_name            VARCHAR(64)         NOT NULL,
  uri                       VARCHAR(128)        NOT NULL,
  parameter                 VARCHAR(256)            NULL,
  http_method               VARCHAR(16)             NULL,
  request_body              VARCHAR(2048)           NULL,
  error_code                int                     NULL,
  error_desc                VARCHAR(128)            NULL,
  http_status               int                     NULL,
  operation_type            VARCHAR(32)             NULL,
  operation_time            DATETIME            NOT NULL,
  consuming                 bigint              NOT NULL,
  old_value                 VARCHAR(MAX)           NULL,
  new_value                 VARCHAR(MAX)           NULL
);

EXEC add_table_desc 'operation_log', '操作日志表'
EXEC add_column_desc 'operation_log', 'id', 'ID'
EXEC add_column_desc 'operation_log', 'username', '操作用户名'
EXEC add_column_desc 'operation_log', 'operation_name', '操作名称'
EXEC add_column_desc 'operation_log', 'uri', 'URI'
EXEC add_column_desc 'operation_log', 'parameter', '参数'
EXEC add_column_desc 'operation_log', 'http_method', 'HTTP方法'
EXEC add_column_desc 'operation_log', 'request_body', '请求消息体'
EXEC add_column_desc 'operation_log', 'error_code', '错误码'
EXEC add_column_desc 'operation_log', 'error_desc', '错误描述'
EXEC add_column_desc 'operation_log', 'http_status', 'HTTP错误码'
EXEC add_column_desc 'operation_log', 'operation_type', '操作类型'
EXEC add_column_desc 'operation_log', 'operation_time', '操作时间'
EXEC add_column_desc 'operation_log', 'consuming', '耗时'
EXEC add_column_desc 'operation_log', 'old_value', '旧记录值'
EXEC add_column_desc 'operation_log', 'new_value', '新记录值'

GO



EXEC p_drop_table 'design';
CREATE TABLE design (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  design VARCHAR(32) NOT NULL,
  base_design VARCHAR(32) NOT NULL,
  type_kxsj VARCHAR(32) NOT NULL,
  steel_class VARCHAR(10) NOT NULL,
  balance_check TINYINT,
  internal TINYINT NOT NULL,
  drawing_no VARCHAR(20),
  approbation_no VARCHAR(256),
  weight DECIMAL(8,2),
  spec VARCHAR(32),
  transfer_record_no VARCHAR(32),
  enabled TINYINT NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(256) NULL
);

create unique index uk_design_design on design(design);
GO

EXEC add_table_desc 'design', '轮型表'
EXEC add_column_desc 'design', 'id', 'ID'
EXEC add_column_desc 'design', 'design', '轮型'
EXEC add_column_desc 'design', 'base_design', '基础轮型'
EXEC add_column_desc 'design', 'type_kxsj', '开箱类型'
EXEC add_column_desc 'design', 'steel_class', '轮型类型，有4种级别：A，B，C，其它'
EXEC add_column_desc 'design', 'balance_check', '是否做平衡检测 1-是, 0-否'
EXEC add_column_desc 'design', 'internal', '是否是国内轮型 1-是, 0-否, 2-未知'
EXEC add_column_desc 'design', 'drawing_no', '图纸编号'
EXEC add_column_desc 'design', 'approbation_no', '报批文号'
EXEC add_column_desc 'design', 'weight', '轮重'
EXEC add_column_desc 'design', 'spec', '执行标准'
EXEC add_column_desc 'design', 'transfer_record_no', '移交记录编号'
EXEC add_column_desc 'design', 'enabled', '是否启用 1-启用, 0-禁用'
EXEC add_column_desc 'design', 'create_time', '创建时间'
EXEC add_column_desc 'design', 'memo', '备注'

GO


EXEC p_drop_table 'graphite_firm';
CREATE TABLE graphite_firm
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  graphite_key VARCHAR(32) NOT NULL,
  diameter INT NOT NULL,
  graphite_ope_id VARCHAR(32) NOT NULL,
  manufacturer_id INT NOT NULL,
  manufacturer_name VARCHAR(32) NOT NULL,
  receive_date DATE NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  scrap_date DATE NULL,
  status INT NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL
);

create unique index uk_graphite_firm_graphite_key on graphite_firm(graphite_key);
GO

EXEC add_table_desc 'graphite_firm', '原始石墨表'
EXEC add_column_desc 'graphite_firm', 'id', 'ID'
EXEC add_column_desc 'graphite_firm', 'graphite_key', '原始石墨号'
EXEC add_column_desc 'graphite_firm', 'diameter', '直径，单位mm'
EXEC add_column_desc 'graphite_firm', 'graphite_ope_id', '石墨员工号'
EXEC add_column_desc 'graphite_firm', 'manufacturer_id', '供应商ID'
EXEC add_column_desc 'graphite_firm', 'manufacturer_name', '供应商名称'
EXEC add_column_desc 'graphite_firm', 'receive_date', '接收日期'
EXEC add_column_desc 'graphite_firm', 'scrap_code', '报废代码'
EXEC add_column_desc 'graphite_firm', 'scrap_date', '报废日期'
EXEC add_column_desc 'graphite_firm', 'status', '状态: 0-接收, 1-使用, 2-报废'
EXEC add_column_desc 'graphite_firm', 'create_time', '创建时间'
EXEC add_column_desc 'graphite_firm', 'memo', '备注'

GO


EXEC p_drop_table 'graphite';
CREATE TABLE graphite
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  graphite VARCHAR(32) NOT NULL,
  graphite_key VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  cd INT NOT NULL,
  process_date DATE NULL,
  gr_id INT NULL,
  repair_times INT NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  height INT NULL,
  redesign_times INT NULL,
  up_times INT NULL,
  down_times INT NULL,
  status INT NULL,
  create_time DATETIME NULL,
  memo VARCHAR(32) NULL
);

EXEC add_table_desc 'graphite', '石墨表'
EXEC add_column_desc 'graphite', 'id', 'ID'
EXEC add_column_desc 'graphite', 'graphite', '石墨号'
EXEC add_column_desc 'graphite', 'graphite_key', '原始石墨号'
EXEC add_column_desc 'graphite', 'design', '轮型'
EXEC add_column_desc 'graphite', 'cd', '上下箱 0-上箱, 1-下箱'
EXEC add_column_desc 'graphite', 'process_date', '最后操作日期'
EXEC add_column_desc 'graphite', 'gr_id', '最后操作记录ID'
EXEC add_column_desc 'graphite', 'repair_times', '返工次数'
EXEC add_column_desc 'graphite', 'scrap_code', '报废代码'
EXEC add_column_desc 'graphite', 'height', '高度, 单位: mm'
EXEC add_column_desc 'graphite', 'redesign_times', '改变轮型次数'
EXEC add_column_desc 'graphite', 'up_times', '上线次数'
EXEC add_column_desc 'graphite', 'down_times', '下线次数'
EXEC add_column_desc 'graphite', 'status', '状态 0-接收未加工, 1-加工后, 2-上线, 3-下线, 4-改石墨号, 5-返修, 6-报废'
EXEC add_column_desc 'graphite', 'create_time', '创建时间'
EXEC add_column_desc 'graphite', 'memo', '备注'

GO


EXEC p_drop_table 'graphite_record';
CREATE TABLE graphite_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  graphite_key VARCHAR(32) NOT NULL,
  graphite VARCHAR(32) NULL,
  re_graphite VARCHAR(32) NULL,
  design VARCHAR(32) NULL,
  re_design VARCHAR(32) NULL,
  cd INT NOT NULL,
  graphite_ope_id VARCHAR(32) NULL,
  process_id VARCHAR(32) NULL,
  height INT NULL,
  process_size INT NULL,
  process_date DATE NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  status INT NULL,
  create_time DATETIME NULL,
  memo VARCHAR(32) NULL
);

EXEC add_table_desc 'graphite_record', '石墨加工记录'
EXEC add_column_desc 'graphite_record', 'id', 'ID'
EXEC add_column_desc 'graphite_record', 'graphite_key', '原始石墨号'
EXEC add_column_desc 'graphite_record', 'graphite', '石墨号'
EXEC add_column_desc 'graphite_record', 're_graphite', '原石墨号'
EXEC add_column_desc 'graphite_record', 'design', '轮型'
EXEC add_column_desc 'graphite_record', 're_design', '原轮型'
EXEC add_column_desc 'graphite_record', 'cd', '上下箱 0-上箱, 1-下箱'
EXEC add_column_desc 'graphite_record', 'graphite_ope_id', '石墨工长号'
EXEC add_column_desc 'graphite_record', 'process_id', '操作工号'
EXEC add_column_desc 'graphite_record', 'height', '高度, 单位: mm'
EXEC add_column_desc 'graphite_record', 'process_size', '加工量, 单位: mm'
EXEC add_column_desc 'graphite_record', 'process_date', '处理日期'
EXEC add_column_desc 'graphite_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'graphite_record', 'rework_code', '返工代码'
EXEC add_column_desc 'graphite_record', 'status', '状态 0-接收未加工, 1-加工后, 2-上线, 3-下线, 4-改石墨号, 5-返修, 6-报废'
EXEC add_column_desc 'graphite_record', 'create_time', '创建时间'
EXEC add_column_desc 'graphite_record', 'memo', '备注'

GO


EXEC p_drop_table 'heat_record';
CREATE TABLE heat_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  heat_record_key VARCHAR(32),
  cast_date DATE,
  furnace_no INT,
  heat_seq INT,
  tap_seq INT,
  pourleader_id VARCHAR(32),
  pourdirect_id VARCHAR(32),
  model_id VARCHAR(32),
  pour_id VARCHAR(32),
  modi_id VARCHAR(32),
  furnace_id VARCHAR(32),
  bag_no VARCHAR(32),
  ladle_no VARCHAR(32),
  bottom_temp DECIMAL(8,3),
  out_steel_temp DECIMAL(8,3),
  scrap_num INT,
  delay_code VARCHAR(32) DEFAULT '' NOT NULL,
  cycletime_conveyor DECIMAL(8,2),
  record_created DATETIME,
  memo VARCHAR(32) NULL
);

create unique index uk_heat_record_heat_record_key on heat_record(heat_record_key);
create index ix_heat_record_heat_furnace_no on heat_record(furnace_no);
GO

EXEC add_table_desc 'heat_record', '炉信息'
EXEC add_column_desc 'heat_record', 'id', 'ID'
EXEC add_column_desc 'heat_record', 'heat_record_key', '大包关键字'
EXEC add_column_desc 'heat_record', 'cast_date', '浇注日期'
EXEC add_column_desc 'heat_record', 'furnace_no', '电炉号'
EXEC add_column_desc 'heat_record', 'heat_seq', '炉次'
EXEC add_column_desc 'heat_record', 'tap_seq', '出钢号'
EXEC add_column_desc 'heat_record', 'pourleader_id', '浇注工长'
EXEC add_column_desc 'heat_record', 'pourdirect_id', '浇注指导'
EXEC add_column_desc 'heat_record', 'model_id', '造型工长号'
EXEC add_column_desc 'heat_record', 'pour_id', '浇注工号'
EXEC add_column_desc 'heat_record', 'modi_id', '修包工号'
EXEC add_column_desc 'heat_record', 'furnace_id', '炉长工号'
EXEC add_column_desc 'heat_record', 'bag_no', '大包编号'
EXEC add_column_desc 'heat_record', 'ladle_no', '底注包号'
EXEC add_column_desc 'heat_record', 'bottom_temp', '底注包温度'
EXEC add_column_desc 'heat_record', 'out_steel_temp', '放钢温度'
EXEC add_column_desc 'heat_record', 'scrap_num', '废型数量'
EXEC add_column_desc 'heat_record', 'delay_code', '延时代码'
EXEC add_column_desc 'heat_record', 'cycletime_conveyor', '驱动梁节拍'
EXEC add_column_desc 'heat_record', 'record_created', '创建时间'
EXEC add_column_desc 'heat_record', 'memo', '备注'

GO


EXEC p_drop_table 'ladle_record';
CREATE TABLE ladle_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  heat_record_id INT,
  ladle_record_key VARCHAR(32),
  ladle_seq INT,
  ladle_temp DECIMAL(8,3),
  ladle_interval TIME,
  record_created DATETIME
);

create index ix_ladle_record_heat_record_id on ladle_record(heat_record_id);
-- create unique index uk_ladle_record on ladle_record(heat_record_id, ladle_seq);
GO

EXEC add_table_desc 'ladle_record', '小包底注'
EXEC add_column_desc 'ladle_record', 'id', 'ID'
EXEC add_column_desc 'ladle_record', 'heat_record_id', '炉信息ID'
EXEC add_column_desc 'ladle_record', 'ladle_record_key', '小包关键字'
EXEC add_column_desc 'ladle_record', 'ladle_seq', '小包序号'
EXEC add_column_desc 'ladle_record', 'ladle_temp', '浇注温度'
EXEC add_column_desc 'ladle_record', 'ladle_interval', '倒包时间'
EXEC add_column_desc 'ladle_record', 'record_created', '记录生成时间'

GO


EXEC p_drop_table 'pour_record';
CREATE TABLE pour_record
(
    pour_id         INT IDENTITY NOT NULL PRIMARY KEY,
    ladle_id        int NULL,
    cast_date       date NULL,
    design          varchar(50) NOT NULL,
    wheel_serial    varchar(50) NOT NULL,
    drag_no         varchar(50) NULL,
    cope_no         varchar(50) NULL,
    pour_time       SMALLDATETIME NULL,
    pour_d_t        DATETIME NULL,
    open_time_cal   SMALLDATETIME NULL,
    open_time_act   SMALLDATETIME NULL,
    pit_seq         int NULL,
    in_pit_date_time     DATETIME NULL,
    special_ultra   smallint NULL,
    test_code       varchar(50) DEFAULT '' NOT NULL,
    scrap_code      varchar(50) DEFAULT '' NOT NULL,
    xray_req        smallint NULL,
    core_setter_id1 varchar(50) NULL,
    drag_scrap      smallint NULL,
    cope_scrap      smallint NULL,
    bz              smallint NULL,
    ordinal_n       int NULL,
    batch_no        varchar(50) NULL,
    record_created  DATETIME NOT NULL
);

create index ix_pour_record_ladle_id on pour_record(ladle_id);
create index ix_pour_record_wheel_serial on pour_record(wheel_serial);
GO

EXEC add_table_desc 'pour_record', '浇注表'
EXEC add_column_desc 'pour_record', 'pour_id', 'ID'
EXEC add_column_desc 'pour_record', 'ladle_id', '小包ID'
EXEC add_column_desc 'pour_record', 'cast_date', '浇注日期'
EXEC add_column_desc 'pour_record', 'design', '轮型'
EXEC add_column_desc 'pour_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'pour_record', 'drag_no', '下箱号'
EXEC add_column_desc 'pour_record', 'cope_no', '上箱号'
EXEC add_column_desc 'pour_record', 'pour_time', '浇注时间'
EXEC add_column_desc 'pour_record', 'pour_d_t', '浇铸日期+时间'
EXEC add_column_desc 'pour_record', 'open_time_cal', '计算开箱时间'
EXEC add_column_desc 'pour_record', 'open_time_act', '实际开箱时间'
EXEC add_column_desc 'pour_record', 'pit_seq', '缓冷桶序列号'
EXEC add_column_desc 'pour_record', 'in_pit_date_time', '入桶日期时间'
EXEC add_column_desc 'pour_record', 'special_ultra', '是否特超 1-是, 0-否'
EXEC add_column_desc 'pour_record', 'test_code', '试验代码'
EXEC add_column_desc 'pour_record', 'scrap_code', '废品代码'
EXEC add_column_desc 'pour_record', 'xray_req', '是否X光紧急开箱 1-是, 0-否'
EXEC add_column_desc 'pour_record', 'core_setter_id1', '下芯工号'
EXEC add_column_desc 'pour_record', 'drag_scrap', '下箱废标志'
EXEC add_column_desc 'pour_record', 'cope_scrap', '上箱废标志'
EXEC add_column_desc 'pour_record', 'bz', '浇注状态 0-已下芯, 1-浇注未提交, 2-浇注已提交, 3-入桶未提交, 4-入桶已提交'
EXEC add_column_desc 'pour_record', 'ordinal_n', '小包浇注轮的序号'
EXEC add_column_desc 'pour_record', 'batch_no', '批次'
EXEC add_column_desc 'pour_record', 'record_created', '记录生成日期+时间'

GO


EXEC p_drop_table 'pit_records';
CREATE TABLE pit_records
(
    pit_seq INT IDENTITY NOT NULL PRIMARY KEY,
    pit_no int NULL,
    in_pit_d_t DATETIME NULL,
    out_pit_d_t_cal DATETIME NULL,
    out_pit_d_t_act DATETIME NULL,
    crane_in_id varchar(50) NULL,
    crane_out_id varchar(50) NULL,
    open_id varchar(50) NULL,
    record_created DATETIME NULL
);

EXEC add_table_desc 'pit_records', '进桶记录表'
EXEC add_column_desc 'pit_records', 'pit_seq', 'ID'
EXEC add_column_desc 'pit_records', 'pit_no', '桶号'
EXEC add_column_desc 'pit_records', 'in_pit_d_t', '入桶日期时间'
EXEC add_column_desc 'pit_records', 'out_pit_d_t_cal', '计算出桶日期时间'
EXEC add_column_desc 'pit_records', 'out_pit_d_t_act', '实际出桶日期时间'
EXEC add_column_desc 'pit_records', 'crane_in_id', '进桶天车工号'
EXEC add_column_desc 'pit_records', 'crane_out_id', '出桶天车工号'
EXEC add_column_desc 'pit_records', 'open_id', '开箱工号'
EXEC add_column_desc 'pit_records', 'record_created', '记录创建时间'
GO


EXEC p_drop_table 'furnace_tap_table';
CREATE TABLE furnace_tap_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  cast_date DATE NOT NULL,
  furnace_no INT NOT NULL,
  furnace_seq INT NOT NULL,
  tap_no INT NOT NULL,
  charge_tank_no VARCHAR(32) NULL,
  gaffer_id VARCHAR(32) NOT NULL,
  fs_id VARCHAR(32) NOT NULL,
  gmr_id VARCHAR(32) NOT NULL,
  furnace_key VARCHAR(32) NOT NULL,
  mtotal_weight DECIMAL(6,2) NULL,
  emeter_reading DECIMAL(12,2) NULL,
  thistime_econsumption DECIMAL(12,2) NULL,
  o2_flow INT NULL,
  thistime_o2_use_quantity INT NULL,
  first_poweron_time DATETIME NULL,
  tap_time DATETIME NULL,
  tap_duration TIME NULL,
  tap_temp INT NULL,
  electrode_use_quantity INT NULL,
  electrode_broken_quantity INT NULL,
  plug_use_quantity INT NULL,
  plug_broken_quantity INT NULL,
  fbottom_contion VARCHAR(32) NULL,
  fwall_contion VARCHAR(32) NULL,
  froof_contion VARCHAR(32) NULL,
  tapping_spout_contion VARCHAR(32) NULL,
  fbottom_usage INT NULL,
  fwall_usage INT NULL,
  froof_usage INT NULL,
  tapping_spout_usage INT NULL,
  patching_position VARCHAR(32) NULL,
  patching_amount INT NULL,
  ramming_position VARCHAR(32) NULL,
  ramming_amount INT NULL,
  status INT NOT NULL,
  delayed_code VARCHAR(32) DEFAULT '' NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL,
);

create index ix_furnace_tap_furnace_no on furnace_tap_table(furnace_no);
GO

EXEC add_table_desc 'furnace_tap_table', '熔炼信息主表'
EXEC add_column_desc 'furnace_tap_table', 'id', 'ID'
EXEC add_column_desc 'furnace_tap_table', 'cast_date', '浇注日期'
EXEC add_column_desc 'furnace_tap_table', 'furnace_no', '电炉号'
EXEC add_column_desc 'furnace_tap_table', 'furnace_seq', '炉次'
EXEC add_column_desc 'furnace_tap_table', 'tap_no', '出钢号'
EXEC add_column_desc 'furnace_tap_table', 'charge_tank_no', '料罐号'
EXEC add_column_desc 'furnace_tap_table', 'gaffer_id', '工长'
EXEC add_column_desc 'furnace_tap_table', 'fs_id', '炉长'
EXEC add_column_desc 'furnace_tap_table', 'gmr_id', '备料工'
EXEC add_column_desc 'furnace_tap_table', 'furnace_key', '电炉关键字'
EXEC add_column_desc 'furnace_tap_table', 'mtotal_weight', '加料合计'
EXEC add_column_desc 'furnace_tap_table', 'emeter_reading', '放钢后电表读数'
EXEC add_column_desc 'furnace_tap_table', 'thistime_econsumption', '本次用电量'
EXEC add_column_desc 'furnace_tap_table', 'o2_flow', '本次氧气流量数'
EXEC add_column_desc 'furnace_tap_table', 'thistime_o2_use_quantity', '本次氧气用量'
EXEC add_column_desc 'furnace_tap_table', 'first_poweron_time', '第一次送电时间'
EXEC add_column_desc 'furnace_tap_table', 'tap_time', '出钢时间'
EXEC add_column_desc 'furnace_tap_table', 'tap_duration', '放钢用时'
EXEC add_column_desc 'furnace_tap_table', 'tap_temp', '放钢温度'
EXEC add_column_desc 'furnace_tap_table', 'electrode_use_quantity', '本月电极使用数量'
EXEC add_column_desc 'furnace_tap_table', 'electrode_broken_quantity', '本月电极损坏数量'
EXEC add_column_desc 'furnace_tap_table', 'plug_use_quantity', '本月电极接头使用数量'
EXEC add_column_desc 'furnace_tap_table', 'plug_broken_quantity', '本月电极损坏使用数量'
EXEC add_column_desc 'furnace_tap_table', 'fbottom_contion', '炉底状况'
EXEC add_column_desc 'furnace_tap_table', 'fwall_contion', '炉墙状况'
EXEC add_column_desc 'furnace_tap_table', 'froof_contion', '炉盖状况'
EXEC add_column_desc 'furnace_tap_table', 'tapping_spout_contion', '出钢槽状况'
EXEC add_column_desc 'furnace_tap_table', 'fbottom_usage', '炉底使用次数'
EXEC add_column_desc 'furnace_tap_table', 'fwall_usage', '炉墙使用次数'
EXEC add_column_desc 'furnace_tap_table', 'froof_usage', '炉盖使用次数'
EXEC add_column_desc 'furnace_tap_table', 'tapping_spout_usage', '出钢槽使用次数'
EXEC add_column_desc 'furnace_tap_table', 'patching_position', '喷补料补炉位置'
EXEC add_column_desc 'furnace_tap_table', 'patching_amount', '喷补料数量'
EXEC add_column_desc 'furnace_tap_table', 'ramming_position', '打结料补炉位置'
EXEC add_column_desc 'furnace_tap_table', 'ramming_amount', '打结料数量'
EXEC add_column_desc 'furnace_tap_table', 'status', '状态 1-保存, 2-提交'
EXEC add_column_desc 'furnace_tap_table', 'delayed_code', '延时代码'
EXEC add_column_desc 'furnace_tap_table', 'create_time', '创建时间'
EXEC add_column_desc 'furnace_tap_table', 'memo', '备注'

GO


EXEC p_drop_table 'charge_material_table';
CREATE TABLE charge_material_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  furnace_tap_id INT NOT NULL,
  times INT NOT NULL,
  charge_time DATETIME NOT NULL,
  poweron_time DATETIME NOT NULL,
  purchase_wheel_weight DECIMAL(6,2) NULL,
  wheel_returns_weight DECIMAL(6,2) NULL,
  rail_weight DECIMAL(6,2) NULL,
  bobs_and_heads_weight DECIMAL(6,2) NULL,
  rough_returns_weight DECIMAL(6,2) NULL,
  wheel_tyre_weight DECIMAL(6,2) NULL,
  turnning_weight DECIMAL(6,2) NULL,
  hammer_weight DECIMAL(6,2) NULL,
  guard_rail_buckle_weight DECIMAL(6,2) NULL,
  steel_board_weight DECIMAL(6,2) NULL,
  coupler_weight DECIMAL(6,2) NULL,
  hboard_weight DECIMAL(6,2) NULL,
  mtotal_weight DECIMAL(6,2) NULL,
  create_time DATETIME NOT NULL
);

create index ix_charge_material_furnace_id on charge_material_table(furnace_tap_id);
GO

EXEC add_table_desc 'charge_material_table', '加料表'
EXEC add_column_desc 'charge_material_table', 'id', 'ID'
EXEC add_column_desc 'charge_material_table', 'furnace_tap_id', '熔炼信息主表ID'
EXEC add_column_desc 'charge_material_table', 'times', '添加次数'
EXEC add_column_desc 'charge_material_table', 'charge_time', '加料时间'
EXEC add_column_desc 'charge_material_table', 'poweron_time', '送电时间'
EXEC add_column_desc 'charge_material_table', 'purchase_wheel_weight', '外购车轮'
EXEC add_column_desc 'charge_material_table', 'wheel_returns_weight', '自产车轮'
EXEC add_column_desc 'charge_material_table', 'rail_weight', '道轨'
EXEC add_column_desc 'charge_material_table', 'bobs_and_heads_weight', '冒口及补贴'
EXEC add_column_desc 'charge_material_table', 'rough_returns_weight', '粗回炉料'
EXEC add_column_desc 'charge_material_table', 'wheel_tyre_weight', '轮箍（轴）'
EXEC add_column_desc 'charge_material_table', 'turnning_weight', '钢屑'
EXEC add_column_desc 'charge_material_table', 'hammer_weight', '镐边、大沿铁'
EXEC add_column_desc 'charge_material_table', 'guard_rail_buckle_weight', '护栏扣'
EXEC add_column_desc 'charge_material_table', 'steel_board_weight', '钢板、角钢、槽钢'
EXEC add_column_desc 'charge_material_table', 'coupler_weight', '车钩、侧架、连接杆'
EXEC add_column_desc 'charge_material_table', 'hboard_weight', '道板、工字板'
EXEC add_column_desc 'charge_material_table', 'mtotal_weight', '合计'
EXEC add_column_desc 'charge_material_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'addition_material_table';
CREATE TABLE addition_material_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  furnace_tap_id INT NOT NULL,
  ap_id INT NOT NULL,
  times INT NOT NULL,
  lime_weight DECIMAL(6,2) NULL,
  lronore_weight DECIMAL(6,2) NULL,
  coke_weight DECIMAL(6,2) NULL,
  lance_quantity DECIMAL(6,2) NULL,
  carbon_weight DECIMAL(6,2) NULL,
  simn_weight DECIMAL(6,2) NULL,
  fesi_weight DECIMAL(6,2) NULL,
  fluorite_weight DECIMAL(6,2) NULL,
  hottop_weight DECIMAL(6,2) NULL,
  thermo_quantity DECIMAL(6,2) NULL,
  fe_weight DECIMAL(6,2) NULL,
  hold1_weight DECIMAL(6,2) NULL,
  hold2_weight DECIMAL(6,2) NULL,
  hold3_weight DECIMAL(6,2) NULL,
  create_time DATETIME NOT NULL
);

create index ix_addition_material_furnace_id on addition_material_table(furnace_tap_id);
GO

EXEC add_table_desc 'addition_material_table', '添加剂表'
EXEC add_column_desc 'addition_material_table', 'id', 'ID'
EXEC add_column_desc 'addition_material_table', 'furnace_tap_id', '熔炼信息主表ID'
EXEC add_column_desc 'addition_material_table', 'ap_id', '添加剂位置表ID'
EXEC add_column_desc 'addition_material_table', 'times', '添加次数'
EXEC add_column_desc 'addition_material_table', 'lime_weight', '石灰'
EXEC add_column_desc 'addition_material_table', 'lronore_weight', '铁矿石'
EXEC add_column_desc 'addition_material_table', 'coke_weight', '焦炭'
EXEC add_column_desc 'addition_material_table', 'lance_quantity', '吹氧管'
EXEC add_column_desc 'addition_material_table', 'carbon_weight', '碳粉'
EXEC add_column_desc 'addition_material_table', 'simn_weight', '硅锰铁'
EXEC add_column_desc 'addition_material_table', 'fesi_weight', '硅铁'
EXEC add_column_desc 'addition_material_table', 'fluorite_weight', '萤石'
EXEC add_column_desc 'addition_material_table', 'hottop_weight', '覆盖剂'
EXEC add_column_desc 'addition_material_table', 'thermo_quantity', '热电偶'
EXEC add_column_desc 'addition_material_table', 'fe_weight', '生铁'
EXEC add_column_desc 'addition_material_table', 'hold1_weight', '预留1'
EXEC add_column_desc 'addition_material_table', 'hold2_weight', '预留2'
EXEC add_column_desc 'addition_material_table', 'hold3_weight', '预留3'
EXEC add_column_desc 'addition_material_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'o2blowing_table';
CREATE TABLE o2blowing_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  furnace_tap_id INT NOT NULL,
  times INT NOT NULL,
  pressure VARCHAR(32) NOT NULL,
  time_begin DATETIME NOT NULL,
  time_end DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_o2blowing_furnace_id on o2blowing_table(furnace_tap_id);
GO

EXEC add_table_desc 'o2blowing_table', '吹氧信息表'
EXEC add_column_desc 'o2blowing_table', 'id', 'ID'
EXEC add_column_desc 'o2blowing_table', 'furnace_tap_id', '熔炼信息主表ID'
EXEC add_column_desc 'o2blowing_table', 'times', '吹氧次数'
EXEC add_column_desc 'o2blowing_table', 'pressure', '氧气压力'
EXEC add_column_desc 'o2blowing_table', 'time_begin', '吹氧开始时间'
EXEC add_column_desc 'o2blowing_table', 'time_end', '吹氧结束时间'
EXEC add_column_desc 'o2blowing_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'dipelectrode_table';
CREATE TABLE dipelectrode_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  furnace_tap_id INT NOT NULL,
  times INT NOT NULL,
  time_begin DATETIME NOT NULL,
  time_end DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_dipelectrode_furnace_id on dipelectrode_table(furnace_tap_id);
GO

EXEC add_table_desc 'dipelectrode_table', '浸电极信息表'
EXEC add_column_desc 'dipelectrode_table', 'id', 'ID'
EXEC add_column_desc 'dipelectrode_table', 'furnace_tap_id', '熔炼信息主表ID'
EXEC add_column_desc 'dipelectrode_table', 'times', '添加次数'
EXEC add_column_desc 'dipelectrode_table', 'time_begin', '开始时间'
EXEC add_column_desc 'dipelectrode_table', 'time_end', '结束时间'
EXEC add_column_desc 'dipelectrode_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'volt_change_table';
CREATE TABLE volt_change_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  furnace_tap_id INT NOT NULL,
  times INT NOT NULL,
  volt INT NOT NULL,
  time_begin DATETIME NOT NULL,
  time_end DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_volt_change_furnace_id on volt_change_table(furnace_tap_id);
GO

EXEC add_table_desc 'volt_change_table', '电压变化信息表'
EXEC add_column_desc 'volt_change_table', 'id', 'ID'
EXEC add_column_desc 'volt_change_table', 'furnace_tap_id', '熔炼信息主表ID'
EXEC add_column_desc 'volt_change_table', 'times', '次数'
EXEC add_column_desc 'volt_change_table', 'volt', '电压'
EXEC add_column_desc 'volt_change_table', 'time_begin', '开始时间'
EXEC add_column_desc 'volt_change_table', 'time_end', '结束时间'
EXEC add_column_desc 'volt_change_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'tempmeasure_table';
CREATE TABLE tempmeasure_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  furnace_tap_id INT NOT NULL,
  times INT NOT NULL,
  temperature VARCHAR(32) NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_tempmeasure_furnace_id on tempmeasure_table(furnace_tap_id);
GO

EXEC add_table_desc 'tempmeasure_table', '测温信息表'
EXEC add_column_desc 'tempmeasure_table', 'id', 'ID'
EXEC add_column_desc 'tempmeasure_table', 'furnace_tap_id', '熔炼信息主表ID'
EXEC add_column_desc 'tempmeasure_table', 'times', '次数'
EXEC add_column_desc 'tempmeasure_table', 'temperature', '温度'
EXEC add_column_desc 'tempmeasure_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'wheel_record';
CREATE TABLE wheel_record(
  wheel_id INT IDENTITY NOT NULL PRIMARY KEY,
  ladle_id int NULL,
  design varchar(50) NOT NULL,
  wheel_serial varchar(50) NOT NULL,
  heat_id int NULL,
  heat_times int DEFAULT(0) NOT NULL,
  heat_code varchar(50) DEFAULT '' NOT NULL,
  hold_code varchar(50) DEFAULT '' NOT NULL,
  rework_code varchar(50) DEFAULT '' NOT NULL,
  test_code varchar(50) DEFAULT '' NOT NULL,
  xray_req tinyint DEFAULT 0 NOT NULL,
  special_ultra tinyint DEFAULT 0 NOT NULL,
  scrap_code varchar(50) DEFAULT '' NOT NULL,
  confirmed_scrap tinyint DEFAULT 0 NOT NULL,
  scrap_date date NULL,
  brin_req tinyint NULL,
  brinnel_reading int NULL,
  bore_size INT NULL,
  tape_size DECIMAL(6,1) NULL,
  wheel_w INT NULL,
  balance_flag int NULL,
  balance_v int NULL,
  balance_a int NULL,
  balance_s varchar(50) NULL,
  pre_id int NULL,
  pre int DEFAULT 0 NOT NULL,
  final_id int NULL,
  final int DEFAULT 0 NOT NULL,
  ultra_id int NULL,
  ultra int DEFAULT 0 NOT NULL,
  mt_id int NULL,
  mt int DEFAULT 0 NOT NULL,
  special_mt tinyint DEFAULT 0 NOT NULL,
  balance_id int NULL,
  balance int DEFAULT 0 NOT NULL,
  last_pre DATETIME NULL,
  last_final DATETIME NULL,
  last_ultra DATETIME NULL,
  last_mt DATETIME NULL,
  last_balance DATETIME NULL,
  finished tinyint DEFAULT 0 NOT NULL,
  barcode_id int NULL,
  last_barcode DATETIME NULL,
  barcode int DEFAULT 0 NOT NULL,
  x_finished_id int NULL,
  re_weight_id int NULL,
  k_finished_id int NULL,
  fin_recall_id int NULL,
  stock_recall_id int NULL,
  out_recall_id int NULL,
  stock_date date NULL,
  hfs varchar(50) NULL,
  check_code varchar(50) DEFAULT '' NOT NULL,
  shipped_no varchar(50) NULL,
  xh varchar(50) NULL,
  pre_date DATETIME NULL,
  shelf_number varchar(50) NULL,
  grind_depth DECIMAL(3,1) NULL,
  cihen_code varchar(50) DEFAULT '' NOT NULL,
  n_grind varchar(50) NULL,
  out_sourcing_dep varchar(50) NULL,
  finish_print tinyint DEFAULT 0 NULL,
  mec_serial varchar(50) NULL,
  mec_confirm tinyint DEFAULT 0 NOT NULL,
  column2 varchar(50) NULL,
  column3 varchar(50) NULL,
  column4 varchar(50) NULL,
  column5 varchar(50) NULL
);

create unique index uk_wheel_record_wheel_serial on wheel_record(wheel_serial);
create index ix_wheel_record_ladle_id on wheel_record(ladle_id);
GO

EXEC add_table_desc 'wheel_record', '车轮记录表'
EXEC add_column_desc 'wheel_record', 'wheel_id', 'ID'
EXEC add_column_desc 'wheel_record', 'ladle_id', '小包记录id'
EXEC add_column_desc 'wheel_record', 'design', '轮型'
EXEC add_column_desc 'wheel_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'wheel_record', 'heat_id', '热处理记录id'
EXEC add_column_desc 'wheel_record', 'heat_times', '热处理次数'
EXEC add_column_desc 'wheel_record', 'heat_code', '热处理代码'
EXEC add_column_desc 'wheel_record', 'hold_code', '去重代码'
EXEC add_column_desc 'wheel_record', 'rework_code', '返工代码'
EXEC add_column_desc 'wheel_record', 'test_code', '试验代码'
EXEC add_column_desc 'wheel_record', 'xray_req', '是否X光 1-是, 0-否'
EXEC add_column_desc 'wheel_record', 'special_ultra', '特超 1-是, 0-否'
EXEC add_column_desc 'wheel_record', 'scrap_code', '废品代码'
EXEC add_column_desc 'wheel_record', 'confirmed_scrap', '是否报废 1-是, 0-否'
EXEC add_column_desc 'wheel_record', 'scrap_date', '报废日期'
EXEC add_column_desc 'wheel_record', 'brin_req', '需要硬度检测 1-是, 0-否'
EXEC add_column_desc 'wheel_record', 'brinnel_reading', '硬度值'
EXEC add_column_desc 'wheel_record', 'bore_size', '轴孔尺寸'
EXEC add_column_desc 'wheel_record', 'tape_size', '带尺尺寸'
EXEC add_column_desc 'wheel_record', 'wheel_w', '轮辋'
EXEC add_column_desc 'wheel_record', 'balance_flag', '一次平衡'
EXEC add_column_desc 'wheel_record', 'balance_v', '平衡值'
EXEC add_column_desc 'wheel_record', 'balance_a', '平衡角度'
EXEC add_column_desc 'wheel_record', 'balance_s', '平衡标识'
EXEC add_column_desc 'wheel_record', 'pre_id', '最后一次预检记录id'
EXEC add_column_desc 'wheel_record', 'pre', '预检次数'
EXEC add_column_desc 'wheel_record', 'final_id', '最后一次终检记录id'
EXEC add_column_desc 'wheel_record', 'final', '终检次数'
EXEC add_column_desc 'wheel_record', 'ultra_id', '最后一次超探记录id'
EXEC add_column_desc 'wheel_record', 'ultra', '超探次数'
EXEC add_column_desc 'wheel_record', 'mt_id', '最后一次磁探记录id'
EXEC add_column_desc 'wheel_record', 'mt', '磁探次数'
EXEC add_column_desc 'wheel_record', 'special_mt', '特别磁探检查 1-是, 0-否'
EXEC add_column_desc 'wheel_record', 'balance_id', '最后一次平衡记录id'
EXEC add_column_desc 'wheel_record', 'balance', '平衡次数'
EXEC add_column_desc 'wheel_record', 'last_pre', '最后预检日期时间'
EXEC add_column_desc 'wheel_record', 'last_final', '最后终检日期时间'
EXEC add_column_desc 'wheel_record', 'last_ultra', '最后超探日期时间'
EXEC add_column_desc 'wheel_record', 'last_mt', '最后磁探日期时间'
EXEC add_column_desc 'wheel_record', 'last_balance', '最后平衡机日期时间'
EXEC add_column_desc 'wheel_record', 'finished', '是否成品 1-是, 0-否'
EXEC add_column_desc 'wheel_record', 'barcode_id', '最后一次条码记录id'
EXEC add_column_desc 'wheel_record', 'last_barcode', '最后打条码日期时间'
EXEC add_column_desc 'wheel_record', 'barcode', '打条码次数'
EXEC add_column_desc 'wheel_record', 'x_finished_id', 'X光成品'
EXEC add_column_desc 'wheel_record', 're_weight_id', '去重成品'
EXEC add_column_desc 'wheel_record', 'k_finished_id', '镗孔成品'
EXEC add_column_desc 'wheel_record', 'fin_recall_id', '成品纠回'
EXEC add_column_desc 'wheel_record', 'stock_recall_id', '入库纠回'
EXEC add_column_desc 'wheel_record', 'out_recall_id', '返厂纠回'
EXEC add_column_desc 'wheel_record', 'stock_date', '入库日期'
EXEC add_column_desc 'wheel_record', 'hfs', '核辐射指数'
EXEC add_column_desc 'wheel_record', 'check_code', '验收编号'
EXEC add_column_desc 'wheel_record', 'shipped_no', '运单号（合格证号）'
EXEC add_column_desc 'wheel_record', 'xh', '检查线号（终检开始）'
EXEC add_column_desc 'wheel_record', 'pre_date', '第一次预检日期'
EXEC add_column_desc 'wheel_record', 'shelf_number', '架子（串）号'
EXEC add_column_desc 'wheel_record', 'grind_depth', '打磨深度'
EXEC add_column_desc 'wheel_record', 'cihen_code', '磁痕代码'
EXEC add_column_desc 'wheel_record', 'n_grind', '预打磨'
EXEC add_column_desc 'wheel_record', 'out_sourcing_dep', '委外'
EXEC add_column_desc 'wheel_record', 'finish_print', '是否成品打印 1-是, 0-否'
EXEC add_column_desc 'wheel_record', 'mec_serial', '机械性能批次号'
EXEC add_column_desc 'wheel_record', 'mec_confirm', '是否机械性能检测'
EXEC add_column_desc 'wheel_record', 'column2', '预留字段'
EXEC add_column_desc 'wheel_record', 'column3', '预留字段'
EXEC add_column_desc 'wheel_record', 'column4', '预留字段'
EXEC add_column_desc 'wheel_record', 'column5', '预留字段'
GO


EXEC p_drop_table 'heat';
CREATE TABLE heat(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    h_id int NULL,
    l_id int NULL,
    wheel_serial_1 varchar(50) NULL,
    wheel_serial_2 varchar(50) NULL,
    design_1 varchar(50) NULL,
    design_2 varchar(50) NULL,
    test_code_1 varchar(50) DEFAULT '' NOT NULL,
    test_code_2 varchar(50) DEFAULT '' NOT NULL,
    heat_code_1 varchar(50) DEFAULT '' NOT NULL,
    heat_code_2 varchar(50) DEFAULT '' NOT NULL,
    hi_heat_in_date date NULL,
    hi_heat_in_time SMALLDATETIME NULL,
    hi_heat_in_shift int NULL,
    hi_heat_in_id varchar(50) NULL,
    hi_heat_in_operator varchar(50) NULL,
    hi_heat_out_date date NULL,
    hi_heat_out_time SMALLDATETIME NULL,
    hi_heat_out_shift int NULL,
    hi_heat_out_id varchar(50) NULL,
    hi_heat_out_operator varchar(50) NULL,
    heat_line tinyint NULL,
    xh int NULL,
    cut_id varchar(50) NULL,
    low_heat_in_date date NULL,
    low_heat_in_time SMALLDATETIME NULL,
    low_heat_in_shift int NULL,
    low_heat_in_id varchar(50) NULL,
    low_heat_in_operator varchar(50) NULL,
    low_heat_out_date date NULL,
    low_heat_out_time SMALLDATETIME NULL,
    low_heat_out_shift int NULL,
    low_heat_out_id varchar(50) NULL,
    low_heat_out_operator varchar(50) NULL,
    mec_serial varchar(50) NULL,
    create_date_time datetime NULL
);

EXEC add_table_desc 'heat', '热处理明细表'
EXEC add_column_desc 'heat', 'id', 'ID'
EXEC add_column_desc 'heat', 'h_id', '环形炉开班记录表ID'
EXEC add_column_desc 'heat', 'l_id', '回火炉开班记录表ID'
EXEC add_column_desc 'heat', 'wheel_serial_1', '一号轮轮号'
EXEC add_column_desc 'heat', 'wheel_serial_2', '二号轮轮号'
EXEC add_column_desc 'heat', 'design_1', '轮型1'
EXEC add_column_desc 'heat', 'design_2', '轮型2'
EXEC add_column_desc 'heat', 'test_code_1', '试验1'
EXEC add_column_desc 'heat', 'test_code_2', '试验2'
EXEC add_column_desc 'heat', 'heat_code_1', '代码1'
EXEC add_column_desc 'heat', 'heat_code_2', '代码2'
EXEC add_column_desc 'heat', 'hi_heat_in_date', '高温进炉日期'
EXEC add_column_desc 'heat', 'hi_heat_in_time', '高温进炉时间'
EXEC add_column_desc 'heat', 'hi_heat_in_shift', '高温进炉班次'
EXEC add_column_desc 'heat', 'hi_heat_in_id', '高温进炉工长'
EXEC add_column_desc 'heat', 'hi_heat_in_operator', '高温进炉操作工'
EXEC add_column_desc 'heat', 'hi_heat_out_date', '高温出炉日期'
EXEC add_column_desc 'heat', 'hi_heat_out_time', '高温出炉时间'
EXEC add_column_desc 'heat', 'hi_heat_out_shift', '高温出炉班次'
EXEC add_column_desc 'heat', 'hi_heat_out_id', '高温出炉工长'
EXEC add_column_desc 'heat', 'hi_heat_out_operator', '高温出炉操作工'
EXEC add_column_desc 'heat', 'heat_line', '线号'
EXEC add_column_desc 'heat', 'xh', '序号'
EXEC add_column_desc 'heat', 'cut_id', '切割工工号'
EXEC add_column_desc 'heat', 'low_heat_in_date', '低温进炉日期'
EXEC add_column_desc 'heat', 'low_heat_in_time', '低温进炉时间'
EXEC add_column_desc 'heat', 'low_heat_in_shift', '低温进炉班次'
EXEC add_column_desc 'heat', 'low_heat_in_id', '低温进炉工长'
EXEC add_column_desc 'heat', 'low_heat_in_operator', '低温进炉操作工'
EXEC add_column_desc 'heat', 'low_heat_out_date', '低温出炉日期'
EXEC add_column_desc 'heat', 'low_heat_out_time', '低温出炉时间'
EXEC add_column_desc 'heat', 'low_heat_out_shift', '低温出炉班次'
EXEC add_column_desc 'heat', 'low_heat_out_id', '低温出炉工长'
EXEC add_column_desc 'heat', 'low_heat_out_operator', '低温出炉操作工'
EXEC add_column_desc 'heat', 'mec_serial', '机械性能批次号'
EXEC add_column_desc 'heat', 'create_date_time', '记录生成时间'



EXEC p_drop_table 'release_record';
CREATE TABLE release_record
(
    log_id INT IDENTITY NOT NULL PRIMARY KEY,
    inspector_id VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    ope_d_t DATETIME NULL,
    rework_code VARCHAR(32) NULL,
    create_date DATETIME NULL
);

EXEC add_table_desc 'release_record', '专员检查放行表'

EXEC add_column_desc 'release_record', 'log_id', '主键'
EXEC add_column_desc 'release_record', 'inspector_id', '检查员工号'
EXEC add_column_desc 'release_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'release_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'release_record', 'rework_code', '返工代码'
EXEC add_column_desc 'release_record', 'create_date', '记录生成日期时间'

GO


EXEC p_drop_table 'inspection_record';
CREATE TABLE inspection_record
(
    log_id INT IDENTITY NOT NULL PRIMARY KEY,
    inspector_id VARCHAR(32) NULL,
    leader_id VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    rework_code VARCHAR(32) DEFAULT '' NOT NULL,
    hold_code VARCHAR(32) DEFAULT '' NOT NULL,
    test_code VARCHAR(32) DEFAULT '' NOT NULL,
    scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
    heat_code VARCHAR(32) DEFAULT '' NOT NULL,
    brin_req INT NULL,
    special_ultra INT NULL,
    xray_req INT NULL,
    xray_result VARCHAR(32) NULL,
    scrap_result VARCHAR(32) NULL,
    special_mt INT NULL,
    ts INT NULL,
    ope_d_t DATETIME NULL,
    create_date DATETIME NULL
);

GO

EXEC add_table_desc 'inspection_record', '质检操作记录表'

EXEC add_column_desc 'inspection_record', 'log_id', '主键'
EXEC add_column_desc 'inspection_record', 'inspector_id', '操作工号'
EXEC add_column_desc 'inspection_record', 'leader_id', '当班工长号'
EXEC add_column_desc 'inspection_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'inspection_record', 'rework_code', '返工代码'
EXEC add_column_desc 'inspection_record', 'hold_code', '保留代码'
EXEC add_column_desc 'inspection_record', 'test_code', '试验代码'
EXEC add_column_desc 'inspection_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'inspection_record', 'heat_code', '热处理代码'
EXEC add_column_desc 'inspection_record', 'brin_req', '是否布式硬度检查'
EXEC add_column_desc 'inspection_record', 'special_ultra', '是否特别超探检查'
EXEC add_column_desc 'inspection_record', 'xray_req', '是否X光检查'
EXEC add_column_desc 'inspection_record', 'xray_result', 'X光结果'
EXEC add_column_desc 'inspection_record', 'scrap_result', '报废原因'
EXEC add_column_desc 'inspection_record', 'special_mt', '是否特别磁探检查'
EXEC add_column_desc 'inspection_record', 'ts', '操作次数'
EXEC add_column_desc 'inspection_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'inspection_record', 'create_date', '记录生成日期时间'

GO


EXEC p_drop_table 'scrap_record';
CREATE TABLE scrap_record
(
    log_id INT IDENTITY NOT NULL PRIMARY KEY,
    inspector_id VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    design VARCHAR(32) NULL,
    former_scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
    scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
    confirmed_scrap INT NULL,
    ope_d_t DATETIME NULL,
    create_date DATETIME NULL
);

GO

EXEC add_table_desc 'scrap_record', '报废&纠回操作记录表'

EXEC add_column_desc 'scrap_record', 'log_id', '主键'
EXEC add_column_desc 'scrap_record', 'inspector_id', '操作工号'
EXEC add_column_desc 'scrap_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'scrap_record', 'design', '轮型'
EXEC add_column_desc 'scrap_record', 'former_scrap_code', '前报废代码'
EXEC add_column_desc 'scrap_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'scrap_record', 'confirmed_scrap', '是否确认报废 1-是, 0-否'
EXEC add_column_desc 'scrap_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'scrap_record', 'create_date', '记录生成日期时间'

GO


EXEC p_drop_table 'correct_wheel_record';
CREATE TABLE correct_wheel_record
(
    log_id INT IDENTITY NOT NULL PRIMARY KEY,
    inspector_id VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    recall_type INT NULL,
    hold_code VARCHAR(32) DEFAULT '' NOT NULL,
    rework_code VARCHAR(32) DEFAULT '' NOT NULL,
    scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
    cihen_code VARCHAR(32) DEFAULT '' NOT NULL,
    former_check_code VARCHAR(32) NULL,
    former_stock_date DATE NULL,
    former_shipped_no VARCHAR(32) NULL,
    confirmed_scrap INT NULL,
    scrap_date DATE NULL,
    memo VARCHAR(32) NULL,
    ope_d_t DATETIME NULL,
    create_date DATETIME NULL
);

GO

EXEC add_table_desc 'correct_wheel_record', '纠轮操作记录表'

EXEC add_column_desc 'correct_wheel_record', 'log_id', '主键'
EXEC add_column_desc 'correct_wheel_record', 'inspector_id', '操作工号'
EXEC add_column_desc 'correct_wheel_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'correct_wheel_record', 'recall_type', '纠回分类'
EXEC add_column_desc 'correct_wheel_record', 'hold_code', '保留代码'
EXEC add_column_desc 'correct_wheel_record', 'rework_code', '返工代码'
EXEC add_column_desc 'correct_wheel_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'correct_wheel_record', 'cihen_code', '磁痕代码'
EXEC add_column_desc 'correct_wheel_record', 'former_check_code', '前验收编号'
EXEC add_column_desc 'correct_wheel_record', 'former_stock_date', '前入库日期'
EXEC add_column_desc 'correct_wheel_record', 'former_shipped_no', '前合格证号'
EXEC add_column_desc 'correct_wheel_record', 'confirmed_scrap', '是否确认报废 1-是, 0-否'
EXEC add_column_desc 'correct_wheel_record', 'scrap_date', '报废日期'
EXEC add_column_desc 'correct_wheel_record', 'memo', '说明'
EXEC add_column_desc 'correct_wheel_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'correct_wheel_record', 'create_date', '记录生成日期时间'

GO


EXEC p_drop_table 'cihen_record';
CREATE TABLE cihen_record
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    drag_inspector_id VARCHAR(32) NULL,
    cope_inspector_id VARCHAR(32) NULL,
    inspector_id VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    cihen_code VARCHAR(32) DEFAULT '' NOT NULL,
    fore_cihen_code VARCHAR(32) DEFAULT '' NOT NULL,
    scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
    grind_time INT NULL,
    cope_cihen_sum INT NULL,
    cope_sandholes INT NULL,
    drag_cihen_sum INT NULL,
    drag_sandholes INT NULL,
    ts INT NULL,
    ope_d_t DATETIME NULL,
    create_date DATETIME NULL
);

GO

EXEC add_table_desc 'cihen_record', '磁痕检测记录表'

EXEC add_column_desc 'cihen_record', 'id', '主键'
EXEC add_column_desc 'cihen_record', 'drag_inspector_id', '下箱检查员工号'
EXEC add_column_desc 'cihen_record', 'cope_inspector_id', '上箱检查员工号'
EXEC add_column_desc 'cihen_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'cihen_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'cihen_record', 'cihen_code', '磁痕代码'
EXEC add_column_desc 'cihen_record', 'fore_cihen_code', '前磁痕代码'
EXEC add_column_desc 'cihen_record', 'scrap_code', '废品代码'
EXEC add_column_desc 'cihen_record', 'grind_time', '打磨时间（分钟）'
EXEC add_column_desc 'cihen_record', 'cope_cihen_sum', '上箱磁痕数'
EXEC add_column_desc 'cihen_record', 'cope_sandholes', '上箱砂眼数'
EXEC add_column_desc 'cihen_record', 'drag_cihen_sum', '下箱磁痕数'
EXEC add_column_desc 'cihen_record', 'drag_sandholes', '下箱砂眼数'
EXEC add_column_desc 'cihen_record', 'ts', '第几次终检'
EXEC add_column_desc 'cihen_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'cihen_record', 'create_date', '记录生成日期时间'

GO

EXEC p_drop_table 'cihen_record_pre';
CREATE TABLE cihen_record_pre
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    inspector_id VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
    cope_sandholes INT NULL,
    drag_sandholes INT NULL,
    ts INT NULL,
    ope_d_t DATETIME NULL,
    create_date DATETIME NULL
);

GO

EXEC add_table_desc 'cihen_record_pre', '磁痕外观检查记录表'

EXEC add_column_desc 'cihen_record_pre', 'id', '主键'
EXEC add_column_desc 'cihen_record_pre', 'inspector_id', '检查员工号'
EXEC add_column_desc 'cihen_record_pre', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'cihen_record_pre', 'scrap_code', '废品代码'
EXEC add_column_desc 'cihen_record_pre', 'cope_sandholes', '上箱砂眼数'
EXEC add_column_desc 'cihen_record_pre', 'drag_sandholes', '下箱砂眼数'
EXEC add_column_desc 'cihen_record_pre', 'ts', '第几次磁痕外观检查'
EXEC add_column_desc 'cihen_record_pre', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'cihen_record_pre', 'create_date', '记录生成日期时间'

GO


EXEC p_drop_table 'cold_wheel';
CREATE TABLE cold_wheel
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    inspector_id VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    cast_date date NULL,
    tap_seq INT NULL,
    pit_no INT NULL,
    pit_seq INT NULL,
    ope_d_t DATETIME NULL,
    create_date DATETIME NULL
);

GO

EXEC add_table_desc 'cold_wheel', '冷割车轮记录表'

EXEC add_column_desc 'cold_wheel', 'id', '主键'
EXEC add_column_desc 'cold_wheel', 'inspector_id', '检查员工号'
EXEC add_column_desc 'cold_wheel', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'cold_wheel', 'cast_date', '浇注日期'
EXEC add_column_desc 'cold_wheel', 'tap_seq', '出钢号'
EXEC add_column_desc 'cold_wheel', 'pit_no', '桶号'
EXEC add_column_desc 'cold_wheel', 'pit_seq', '缓冷桶序列号'
EXEC add_column_desc 'cold_wheel', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'cold_wheel', 'create_date', '记录生成日期时间'

GO

EXEC p_drop_table 'chemistry_detail';
CREATE TABLE chemistry_detail
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  heat_record_id INT NULL,
  ladle_id INT NULL,
  lab_id INT NULL,
  furnace_seq VARCHAR(32) NULL,
  sample_no VARCHAR(32) NULL,
  c DECIMAL(10,3) NULL,
  si DECIMAL(10,3) NULL,
  mn DECIMAL(10,3) NULL,
  p DECIMAL(10,3) NULL,
  s DECIMAL(10,3) NULL,
  cr DECIMAL(10,3) NULL,
  ni DECIMAL(10,3) NULL,
  w DECIMAL(10,3) NULL,
  v DECIMAL(10,3) NULL,
  mo DECIMAL(10,3) NULL,
  ti DECIMAL(10,3) NULL,
  cu DECIMAL(10,3) NULL,
  al DECIMAL(10,3) NULL,
  b DECIMAL(10,3) NULL,
  co DECIMAL(10,3) NULL,
  sn DECIMAL(10,3) NULL,
  pb DECIMAL(10,3) NULL,
  [as] DECIMAL(10,3) NULL,
  sb DECIMAL(10,3) NULL,
  bi DECIMAL(10,3) NULL,
  nb DECIMAL(10,3) NULL,
  ca DECIMAL(10,3) NULL,
  mg DECIMAL(10,3) NULL,
  ce DECIMAL(10,3) NULL,
  n DECIMAL(10,3) NULL,
  zr DECIMAL(10,3) NULL,
  bs DECIMAL(10,3) NULL,
  ns DECIMAL(10,3) NULL,
  nt DECIMAL(10,3) NULL,
  fe DECIMAL(10,3) NULL,
  opre_id VARCHAR(32) NULL,
  seq_no_repeat INT NULL,
  create_date DATETIME NULL
);

-- create unique index uk_chemistry_detail_lab_id on chemistry_detail(lab_id);
create index ix_chemistry_detail_furnace_seq on chemistry_detail(furnace_seq);
GO

EXEC add_table_desc 'chemistry_detail', '服务器化学成分表'

EXEC add_column_desc 'chemistry_detail', 'id', '序号'
EXEC add_column_desc 'chemistry_detail', 'heat_record_id', '大包id（heat_record）'
EXEC add_column_desc 'chemistry_detail', 'ladle_id', '小包id(Ladle_record)'
EXEC add_column_desc 'chemistry_detail', 'lab_id', '化验室本地数据库id'
EXEC add_column_desc 'chemistry_detail', 'furnace_seq', '炉号'
EXEC add_column_desc 'chemistry_detail', 'sample_no', '样号'
EXEC add_column_desc 'chemistry_detail', 'c', '碳'
EXEC add_column_desc 'chemistry_detail', 'si', '硅'
EXEC add_column_desc 'chemistry_detail', 'mn', '锰'
EXEC add_column_desc 'chemistry_detail', 'p', '磷'
EXEC add_column_desc 'chemistry_detail', 's', '硫'
EXEC add_column_desc 'chemistry_detail', 'cr', '铬'
EXEC add_column_desc 'chemistry_detail', 'ni', '镍'
EXEC add_column_desc 'chemistry_detail', 'w', '钨'
EXEC add_column_desc 'chemistry_detail', 'v', '矾'
EXEC add_column_desc 'chemistry_detail', 'mo', '钼'
EXEC add_column_desc 'chemistry_detail', 'ti', '钛'
EXEC add_column_desc 'chemistry_detail', 'cu', '铜'
EXEC add_column_desc 'chemistry_detail', 'al', '铝'
EXEC add_column_desc 'chemistry_detail', 'b', '硼'
EXEC add_column_desc 'chemistry_detail', 'co', '钴'
EXEC add_column_desc 'chemistry_detail', 'sn', '锡'
EXEC add_column_desc 'chemistry_detail', 'pb', '铅'
EXEC add_column_desc 'chemistry_detail', 'as', '砷'
EXEC add_column_desc 'chemistry_detail', 'sb', '锑'
EXEC add_column_desc 'chemistry_detail', 'bi', '铋'
EXEC add_column_desc 'chemistry_detail', 'nb', '铌'
EXEC add_column_desc 'chemistry_detail', 'ca', '钙'
EXEC add_column_desc 'chemistry_detail', 'mg', '镁'
EXEC add_column_desc 'chemistry_detail', 'ce', '铈'
EXEC add_column_desc 'chemistry_detail', 'n', '氮'
EXEC add_column_desc 'chemistry_detail', 'zr', '锆'
EXEC add_column_desc 'chemistry_detail', 'bs', 'null'
EXEC add_column_desc 'chemistry_detail', 'ns', 'null'
EXEC add_column_desc 'chemistry_detail', 'nt', 'null'
EXEC add_column_desc 'chemistry_detail', 'fe', '铁'
EXEC add_column_desc 'chemistry_detail', 'opre_id', '操作工号'
EXEC add_column_desc 'chemistry_detail', 'seq_no_repeat', '炉号和样号是否重复 1-重复, 2-没有重复'
EXEC add_column_desc 'chemistry_detail', 'create_date', '记录生成日期时间'

GO


EXEC p_drop_table 'pre_check_record';
CREATE TABLE pre_check_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  drag_inspector_id VARCHAR(32) NOT NULL,
  cope_inspector_id VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  test_code VARCHAR(32) DEFAULT '' NOT NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  heat_code VARCHAR(32) DEFAULT '' NOT NULL,
  ope_d_t DATETIME NOT NULL,
  brin_req INT DEFAULT 0 NULL,
  ts INT DEFAULT 0 NOT NULL,
  grind_depth DECIMAL(3,1) NULL,
  create_time DATETIME NOT NULL
);

create index ix_pre_check_wheel_serial on pre_check_record(wheel_serial);

GO

EXEC add_table_desc 'pre_check_record', '预检记录表'
EXEC add_column_desc 'pre_check_record', 'id', 'ID'
EXEC add_column_desc 'pre_check_record', 'drag_inspector_id', '下箱操作工号'
EXEC add_column_desc 'pre_check_record', 'cope_inspector_id', '上箱操作工号'
EXEC add_column_desc 'pre_check_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'pre_check_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'pre_check_record', 'design', '轮型'
EXEC add_column_desc 'pre_check_record', 'test_code', '试验代码'
EXEC add_column_desc 'pre_check_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'pre_check_record', 'rework_code', '返工代码'
EXEC add_column_desc 'pre_check_record', 'heat_code', '热处理代码'
EXEC add_column_desc 'pre_check_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'pre_check_record', 'brin_req', '需要硬度检测 1-是, 0-否'
EXEC add_column_desc 'pre_check_record', 'ts', '第几次预检'
EXEC add_column_desc 'pre_check_record', 'grind_depth', '打磨深度'
EXEC add_column_desc 'pre_check_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'final_check_record';
CREATE TABLE final_check_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  drag_inspector_id VARCHAR(32) NOT NULL,
  cope_inspector_id VARCHAR(32) NOT NULL,
  tape_inspector_id VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  xh VARCHAR(32) NOT NULL,
  bore_size INT NOT NULL,
  wheel_w INT NOT NULL,
  test_code VARCHAR(32) DEFAULT '' NOT NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  heat_code VARCHAR(32) DEFAULT '' NOT NULL,
  hold_code VARCHAR(32) DEFAULT '' NOT NULL,
  ope_d_t datetime NOT NULL,
  brin_req INT DEFAULT 0 NOT NULL,
  brinnel_reading INT NOT NULL,
  cihen_code VARCHAR(32) NULL,
  ts INT DEFAULT 0 NOT NULL,
  grind_depth DECIMAL(3,1) NULL,
  ngrind VARCHAR(32) DEFAULT 0 NOT NULL,
  hub_thickness DECIMAL(5,1) NOT NULL,
  rim_parallelism DECIMAL(5,1) NOT NULL,
  flange_tread_profile INT DEFAULT 1 NOT NULL,
  create_time datetime NOT NULL
);

create index ix_final_check_wheel_serial on final_check_record(wheel_serial);
GO

EXEC add_table_desc 'final_check_record', '终检记录表'
EXEC add_column_desc 'final_check_record', 'id', '主键'
EXEC add_column_desc 'final_check_record', 'drag_inspector_id', '下箱检查员工号'
EXEC add_column_desc 'final_check_record', 'cope_inspector_id', '上箱检查员工号'
EXEC add_column_desc 'final_check_record', 'tape_inspector_id', '带尺检查员工号'
EXEC add_column_desc 'final_check_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'final_check_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'final_check_record', 'design', '轮型'
EXEC add_column_desc 'final_check_record', 'xh', '检查线号'
EXEC add_column_desc 'final_check_record', 'bore_size', '轴孔尺寸'
EXEC add_column_desc 'final_check_record', 'wheel_w', '轮辋尺寸'
EXEC add_column_desc 'final_check_record', 'test_code', '试验代码'
EXEC add_column_desc 'final_check_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'final_check_record', 'rework_code', '返工代码'
EXEC add_column_desc 'final_check_record', 'heat_code', '热处理代码'
EXEC add_column_desc 'final_check_record', 'hold_code', '保留代码'
EXEC add_column_desc 'final_check_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'final_check_record', 'brin_req', '是否硬度检测 1-是, 0-否'
EXEC add_column_desc 'final_check_record', 'brinnel_reading', '布氏硬度值'
EXEC add_column_desc 'final_check_record', 'cihen_code', '磁痕代码'
EXEC add_column_desc 'final_check_record', 'ts', '第几次终检'
EXEC add_column_desc 'final_check_record', 'grind_depth', '打磨深度'
EXEC add_column_desc 'final_check_record', 'ngrind', '不打磨车轮'
EXEC add_column_desc 'final_check_record', 'hub_thickness', '轮毂壁厚差'
EXEC add_column_desc 'final_check_record', 'rim_parallelism', '内外侧辋面平行度'
EXEC add_column_desc 'final_check_record', 'flange_tread_profile', '踏面外形 1-合格, 0-不合格'
EXEC add_column_desc 'final_check_record', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'hbtest_record';
CREATE TABLE hbtest_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  test_date DATE NOT NULL,
  hb_no INT NOT NULL,
  test_block_no VARCHAR(32) NOT NULL,
  stand_value INT NOT NULL,
  test_result INT NOT NULL,
  indenta_dia DECIMAL(6,2) NOT NULL,
  m_indenta_dia DECIMAL(6,2) NOT NULL,
  dev_indenta_dia DECIMAL(6,2) NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_hbtest_inspector_shift on hbtest_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'hbtest_record', '布氏硬度机试验记录表'
EXEC add_column_desc 'hbtest_record', 'id', 'ID'
EXEC add_column_desc 'hbtest_record', 'test_date', '试验日期'
EXEC add_column_desc 'hbtest_record', 'hb_no', '布氏硬度机编号'
EXEC add_column_desc 'hbtest_record', 'test_block_no', '试块编号'
EXEC add_column_desc 'hbtest_record', 'stand_value', '试块标准值'
EXEC add_column_desc 'hbtest_record', 'test_result', '试块试验值'
EXEC add_column_desc 'hbtest_record', 'indenta_dia', '试块试验值压痕直径'
EXEC add_column_desc 'hbtest_record', 'm_indenta_dia', '读数显微镜检查的压痕直径'
EXEC add_column_desc 'hbtest_record', 'dev_indenta_dia', '压痕直径差'
EXEC add_column_desc 'hbtest_record', 'ts', '试验次数'
EXEC add_column_desc 'hbtest_record', 'operator', '操作工号'
EXEC add_column_desc 'hbtest_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'hbtest_record', 'shift_no', '班次'
EXEC add_column_desc 'hbtest_record', 'is_inspec_check', '是否工长确认 1-是, 0-否'
EXEC add_column_desc 'hbtest_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'hbtest_record', 'create_time', '记录生成日期'

GO



EXEC p_drop_table 'threehb_record';
CREATE TABLE threehb_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  wheel_serial VARCHAR(32) NOT NULL,
  brinnel1 INT NOT NULL,
  brinnel2 INT NOT NULL,
  brinnel3 INT NOT NULL,
  difference INT NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  re_test INT NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_threehb_inspector_shift on threehb_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'threehb_record', '轮辋三点硬度检测记录表'

EXEC add_column_desc 'threehb_record', 'id', 'ID'
EXEC add_column_desc 'threehb_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'threehb_record', 'brinnel1', '外侧硬度1'
EXEC add_column_desc 'threehb_record', 'brinnel2', '外侧硬度2'
EXEC add_column_desc 'threehb_record', 'brinnel3', '外侧硬度3'
EXEC add_column_desc 'threehb_record', 'difference', '偏差值'
EXEC add_column_desc 'threehb_record', 'ts', '试验次数'
EXEC add_column_desc 'threehb_record', 're_test', '硬度复试'
EXEC add_column_desc 'threehb_record', 'operator', '操作工号'
EXEC add_column_desc 'threehb_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'threehb_record', 'shift_no', '班次'
EXEC add_column_desc 'threehb_record', 'is_inspec_check', '是否工长确认 1-是, 0-否'
EXEC add_column_desc 'threehb_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'threehb_record', 'create_time', '记录生成日期'
GO


EXEC p_drop_table 'tround_record';
CREATE TABLE tround_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  wheel_serial VARCHAR(32) NOT NULL,
  pro INT NOT NULL,
  brinnel1 DECIMAL(5,1) NOT NULL,
  brinnel2 DECIMAL(5,1) NOT NULL,
  brinnel3 DECIMAL(5,1) NOT NULL,
  brinnel4 DECIMAL(5,1) NOT NULL,
  round_differ DECIMAL(5,1) NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_tround_inspector_shift on tround_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'tround_record', '踏面机床首件圆度记录表'

EXEC add_column_desc 'tround_record', 'id', 'ID'
EXEC add_column_desc 'tround_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'tround_record', 'pro', '机床号'
EXEC add_column_desc 'tround_record', 'brinnel1', '轮径1'
EXEC add_column_desc 'tround_record', 'brinnel2', '轮径2'
EXEC add_column_desc 'tround_record', 'brinnel3', '轮径3'
EXEC add_column_desc 'tround_record', 'brinnel4', '轮径4'
EXEC add_column_desc 'tround_record', 'round_differ', '圆度轮径差'
EXEC add_column_desc 'tround_record', 'ts', '试验次数'
EXEC add_column_desc 'tround_record', 'operator', '操作工号'
EXEC add_column_desc 'tround_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'tround_record', 'shift_no', '班次'
EXEC add_column_desc 'tround_record', 'is_inspec_check', '是否工长确认 1-是, 0-否'
EXEC add_column_desc 'tround_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'tround_record', 'create_time', '记录生成日期'

GO


EXEC p_drop_table 'wheel_dev_record';
CREATE TABLE wheel_dev_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  distance VARCHAR(32) NOT NULL,
  rim_dev INT NOT NULL,
  hub_back INT NOT NULL,
  hub_front INT NOT NULL,
  side_back INT NOT NULL,
  side_front INT NOT NULL,
  front_rim INT NOT NULL,
  back_rim INT NOT NULL,
  diff_rim INT NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_wheel_dev_inspector_shift on wheel_dev_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'wheel_dev_record', '车轮尺寸偏差记录表'
EXEC add_column_desc 'wheel_dev_record', 'id', 'ID'
EXEC add_column_desc 'wheel_dev_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'wheel_dev_record', 'design', '轮型'
EXEC add_column_desc 'wheel_dev_record', 'distance', '内侧毂辋距'
EXEC add_column_desc 'wheel_dev_record', 'rim_dev', '轮辋厚度差'
EXEC add_column_desc 'wheel_dev_record', 'hub_back', '轮毂外径内侧'
EXEC add_column_desc 'wheel_dev_record', 'hub_front', '轮毂外径外侧'
EXEC add_column_desc 'wheel_dev_record', 'side_back', '同侧轮毂壁厚差内侧'
EXEC add_column_desc 'wheel_dev_record', 'side_front', '同侧轮毂壁厚差外侧'
EXEC add_column_desc 'wheel_dev_record', 'front_rim', '轮辋外侧内径'
EXEC add_column_desc 'wheel_dev_record', 'back_rim', '轮辋内侧内径'
EXEC add_column_desc 'wheel_dev_record', 'diff_rim', '轮辋内外侧内径差'
EXEC add_column_desc 'wheel_dev_record', 'ts', '试验次数'
EXEC add_column_desc 'wheel_dev_record', 'operator', '操作工号'
EXEC add_column_desc 'wheel_dev_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'wheel_dev_record', 'shift_no', '班次'
EXEC add_column_desc 'wheel_dev_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'wheel_dev_record', 'create_time', '记录生成日期'
GO


EXEC p_drop_table 'tape_testing_record';
CREATE TABLE tape_testing_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  test_date DATE NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  stand_tape_size VARCHAR(32) NOT NULL,
  tape_size DECIMAL(6,1) NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_ultra_wheel_serial on tape_testing_record(wheel_serial);
GO

EXEC add_table_desc 'tape_testing_record', '带尺检测记录表'
EXEC add_column_desc 'tape_testing_record', 'id', 'ID'
EXEC add_column_desc 'tape_testing_record', 'test_date', '试验日期'
EXEC add_column_desc 'tape_testing_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'tape_testing_record', 'design', '轮型'
EXEC add_column_desc 'tape_testing_record', 'stand_tape_size', '标准轮带尺'
EXEC add_column_desc 'tape_testing_record', 'tape_size', '带尺'
EXEC add_column_desc 'tape_testing_record', 'ts', '试验次数'
EXEC add_column_desc 'tape_testing_record', 'operator', '操作工号'
EXEC add_column_desc 'tape_testing_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'tape_testing_record', 'shift_no', '班次'
EXEC add_column_desc 'tape_testing_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'tape_testing_record', 'create_time', '记录生成日期'
GO


EXEC p_drop_table 'ultra_record';
CREATE TABLE ultra_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  ultra_inspector_id VARCHAR(32) NOT NULL,
  mag_cope_inspector_id VARCHAR(32) NOT NULL,
  mag_drag_inspector_id VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  xh VARCHAR(32) NOT NULL,
  tape_size DECIMAL(6,1) NOT NULL,
  test_code VARCHAR(32) DEFAULT '' NOT NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  heat_code VARCHAR(32) DEFAULT '' NOT NULL,
  balance_s VARCHAR(32) DEFAULT '' NOT NULL,
  hold_code VARCHAR(32) DEFAULT '' NOT NULL,
  hfs VARCHAR(32) NOT NULL,
  ope_d_t DATETIME NOT NULL,
  special_mt INT DEFAULT 0 NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  remanence VARCHAR(32) NOT NULL,
  xray_req INT DEFAULT 0 NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_ultra_wheel_serial on ultra_record(wheel_serial);
GO

EXEC add_table_desc 'ultra_record', '超探记录表'
EXEC add_column_desc 'ultra_record', 'id', 'ID'
EXEC add_column_desc 'ultra_record', 'ultra_inspector_id', '超探检查员工号'
EXEC add_column_desc 'ultra_record', 'mag_cope_inspector_id', '磁探检查员工号(上)'
EXEC add_column_desc 'ultra_record', 'mag_drag_inspector_id', '磁探检查员工号(下)'
EXEC add_column_desc 'ultra_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'ultra_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'ultra_record', 'design', '轮型'
EXEC add_column_desc 'ultra_record', 'xh', '线号'
EXEC add_column_desc 'ultra_record', 'tape_size', '带尺尺寸'
EXEC add_column_desc 'ultra_record', 'test_code', '试验代码'
EXEC add_column_desc 'ultra_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'ultra_record', 'rework_code', '返工代码'
EXEC add_column_desc 'ultra_record', 'heat_code', '热处理代码'
EXEC add_column_desc 'ultra_record', 'balance_s', '静平衡代码'
EXEC add_column_desc 'ultra_record', 'hold_code', '保留代码'
EXEC add_column_desc 'ultra_record', 'hfs', '核辐射'
EXEC add_column_desc 'ultra_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'ultra_record', 'special_mt', '是否冷割车轮 1-是, 0-否'
EXEC add_column_desc 'ultra_record', 'ts', '第几次超探'
EXEC add_column_desc 'ultra_record', 'remanence', '剩磁'
EXEC add_column_desc 'ultra_record', 'xray_req', 'X-光检查'
EXEC add_column_desc 'ultra_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'magnetic_record';
CREATE TABLE magnetic_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  mag_cope_inspector_id VARCHAR(32) NOT NULL,
  mag_drag_inspector_id VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_magnetic_wheel_serial on magnetic_record(wheel_serial);
GO

EXEC add_table_desc 'magnetic_record', '磁探记录表'
EXEC add_column_desc 'magnetic_record', 'id', 'ID'
EXEC add_column_desc 'magnetic_record', 'mag_cope_inspector_id', '磁探上箱员工号'
EXEC add_column_desc 'magnetic_record', 'mag_drag_inspector_id', '磁探下箱员工号'
EXEC add_column_desc 'magnetic_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'magnetic_record', 'ts', '磁探次数'
EXEC add_column_desc 'magnetic_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'magnetic_record', 'design', '轮型'
EXEC add_column_desc 'magnetic_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'magnetic_record', 'rework_code', '返工代码'
EXEC add_column_desc 'magnetic_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'magnetic_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'balance_record';
CREATE TABLE balance_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  balance_inspector_id VARCHAR(32) NOT NULL,
  mark_inspector_id VARCHAR(32) NOT NULL,
  stick_inspector_id VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  xh VARCHAR(32) NULL,
  test_code VARCHAR(32) DEFAULT '' NOT NULL,
  scrap_code VARCHAR(32) DEFAULT '' NOT NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  hold_code VARCHAR(32) DEFAULT '' NOT NULL,
  heat_code VARCHAR(32) DEFAULT '' NOT NULL,
  balance_s VARCHAR(32) DEFAULT '' NOT NULL,
  balance_v INT NULL,
  balance_a INT NULL,
  ope_d_t DATETIME NOT NULL,
  special_mt INT DEFAULT 0 NOT NULL,
  brin_req INT DEFAULT 0 NOT NULL,
  xray_req INT DEFAULT 0 NOT NULL,
  ts INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_balance_wheel_serial on balance_record(wheel_serial);
GO

EXEC add_table_desc 'balance_record', '平衡机记录表'
EXEC add_column_desc 'balance_record', 'id', 'ID'
EXEC add_column_desc 'balance_record', 'balance_inspector_id', ' 平衡机员工号'
EXEC add_column_desc 'balance_record', 'mark_inspector_id', ' 打号机员工号'
EXEC add_column_desc 'balance_record', 'stick_inspector_id', '贴条码员工号'
EXEC add_column_desc 'balance_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'balance_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'balance_record', 'design', '轮型'
EXEC add_column_desc 'balance_record', 'xh', '线号'
EXEC add_column_desc 'balance_record', 'test_code', '试验代码'
EXEC add_column_desc 'balance_record', 'scrap_code', '报废代码'
EXEC add_column_desc 'balance_record', 'rework_code', '返工代码'
EXEC add_column_desc 'balance_record', 'hold_code', '保留代码'
EXEC add_column_desc 'balance_record', 'heat_code', '热处理代码'
EXEC add_column_desc 'balance_record', 'balance_s', '静平衡代码、标注'
EXEC add_column_desc 'balance_record', 'balance_v', '不平衡量'
EXEC add_column_desc 'balance_record', 'balance_a', '不平衡角度'
EXEC add_column_desc 'balance_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'balance_record', 'special_mt', '是否冷割车轮 1-是, 0-否'
EXEC add_column_desc 'balance_record', 'brin_req', '是否硬度检测 1-是, 0-否'
EXEC add_column_desc 'balance_record', 'xray_req', '是否X光检测 1-是, 0-否'
EXEC add_column_desc 'balance_record', 'ts', '第几次平衡机'
EXEC add_column_desc 'balance_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'barcode_print_record';
CREATE TABLE barcode_print_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  barcode_inspector_id VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  xh VARCHAR(32) NOT NULL,
  finished INT DEFAULT 0 NOT NULL,
  finish_print INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  reprint_code INT DEFAULT 0 NULL,
  ts INT NULL,
  create_time DATETIME NOT NULL
);

create index ix_barcode_print_wheel_serial on barcode_print_record(wheel_serial);
GO

EXEC add_table_desc 'barcode_print_record', '条码打印记录表'
EXEC add_column_desc 'barcode_print_record', 'id', 'ID'
EXEC add_column_desc 'barcode_print_record', 'barcode_inspector_id', ' 打条码员工号'
EXEC add_column_desc 'barcode_print_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'barcode_print_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'barcode_print_record', 'design', '轮型'
EXEC add_column_desc 'barcode_print_record', 'xh', '线号'
EXEC add_column_desc 'barcode_print_record', 'finished', '是否成品 1-是, 0-否'
EXEC add_column_desc 'barcode_print_record', 'finish_print', '是否成品打印 1-是, 0-否'
EXEC add_column_desc 'barcode_print_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'barcode_print_record', 'reprint_code', '是否补打标识 1-是, 0-否'
EXEC add_column_desc 'barcode_print_record', 'ts', '第几次打印'
EXEC add_column_desc 'barcode_print_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'ut_test_record';
CREATE TABLE ut_test_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  eq_model VARCHAR(32) NOT NULL,
  eq_no VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  probe_check INT DEFAULT 0 NOT NULL,
  room_temp VARCHAR(32) NOT NULL,
  t_db INT NULL,
  j1 INT NULL,
  j2 INT NULL,
  j3 INT NULL,
  j4 INT NULL,
  j5 INT NULL,
  j6 INT NULL,
  j7 INT NULL,
  b_db INT NULL,
  z1 INT NULL,
  z2 INT NULL,
  z3 INT NULL,
  z4 INT NULL,
  z5 INT NULL,
  wheel_check INT DEFAULT 0 NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
);

create index ix_ut_test_inspector_shift on ut_test_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'ut_test_record', '超探开班试验记录表'
EXEC add_column_desc 'ut_test_record', 'id', 'ID'
EXEC add_column_desc 'ut_test_record', 'eq_model', '设备型号'
EXEC add_column_desc 'ut_test_record', 'eq_no', '设备编号'
EXEC add_column_desc 'ut_test_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'ut_test_record', 'design', '轮型'
EXEC add_column_desc 'ut_test_record', 'probe_check', '探头是否清理 1-是, 0-否'
EXEC add_column_desc 'ut_test_record', 'room_temp', '探房温度'
EXEC add_column_desc 'ut_test_record', 't_db', '踏面dB值'
EXEC add_column_desc 'ut_test_record', 'j1', '踏面J1'
EXEC add_column_desc 'ut_test_record', 'j2', '踏面J2'
EXEC add_column_desc 'ut_test_record', 'j3', '踏面J3'
EXEC add_column_desc 'ut_test_record', 'j4', '踏面J4'
EXEC add_column_desc 'ut_test_record', 'j5', '踏面J5'
EXEC add_column_desc 'ut_test_record', 'j6', '踏面J6'
EXEC add_column_desc 'ut_test_record', 'j7', '踏面J7'
EXEC add_column_desc 'ut_test_record', 'b_db', '内侧面dB值'
EXEC add_column_desc 'ut_test_record', 'z1', '内侧面Z1(B1)'
EXEC add_column_desc 'ut_test_record', 'z2', '内侧面Z2(B2)'
EXEC add_column_desc 'ut_test_record', 'z3', '内侧面Z3(B3)'
EXEC add_column_desc 'ut_test_record', 'z4', '内侧面Z4'
EXEC add_column_desc 'ut_test_record', 'z5', '内侧面Z5'
EXEC add_column_desc 'ut_test_record', 'wheel_check', '是否抽检轮 1-是, 0-否'
EXEC add_column_desc 'ut_test_record', 'ts', '试验次数'
EXEC add_column_desc 'ut_test_record', 'operator', '操作工号'
EXEC add_column_desc 'ut_test_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'ut_test_record', 'shift_no', '班次'
EXEC add_column_desc 'ut_test_record', 'is_inspec_check', '是否工长确认 1-是, 0-否'
EXEC add_column_desc 'ut_test_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'ut_test_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'mt_test_record';
CREATE TABLE mt_test_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  eq_model VARCHAR(32) NOT NULL,
  eq_no VARCHAR(32) NOT NULL,
  room_temp VARCHAR(32) NULL,
  solution_pre VARCHAR(32) NULL,
  solution_amount INT NULL,
  magn_amount INT NULL,
  disp_amount INT NULL,
  defo_amount INT NULL,
  solution_density DECIMAL(6,2) NULL,
  magn_current DECIMAL(6,1) NULL,
  light_cope_left INT NULL,
  light_cope_right INT NULL,
  light_tread INT NULL,
  light_drag_left INT NULL,
  light_drag_right INT NULL,
  white_light DECIMAL(6,1) NULL,
  striprev_up INT NULL,
  striprev_down INT NULL,
  remanence_intensity INT NULL,
  batchno_mt VARCHAR(32) NULL,
  ts INT DEFAULT 0 NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
);

create index ix_mt_test_inspector_shift on mt_test_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'mt_test_record', '磁探开班试验记录表'
EXEC add_column_desc 'mt_test_record', 'id', 'ID'
EXEC add_column_desc 'mt_test_record', 'eq_model', '设备型号'
EXEC add_column_desc 'mt_test_record', 'eq_no', '设备编号'
EXEC add_column_desc 'mt_test_record', 'room_temp', '探房温度'
EXEC add_column_desc 'mt_test_record', 'solution_pre', '磁悬液配制'
EXEC add_column_desc 'mt_test_record', 'solution_amount', '配液量'
EXEC add_column_desc 'mt_test_record', 'magn_amount', '荧光磁粉加入量'
EXEC add_column_desc 'mt_test_record', 'disp_amount', '分散剂加入量'
EXEC add_column_desc 'mt_test_record', 'defo_amount', '消泡剂加入量'
EXEC add_column_desc 'mt_test_record', 'solution_density', '磁悬液浓度'
EXEC add_column_desc 'mt_test_record', 'magn_current', '磁化电流'
EXEC add_column_desc 'mt_test_record', 'light_cope_left', '紫光灯强度上箱左'
EXEC add_column_desc 'mt_test_record', 'light_cope_right', '紫光灯强度上箱右'
EXEC add_column_desc 'mt_test_record', 'light_tread', '紫光灯强度踏面'
EXEC add_column_desc 'mt_test_record', 'light_drag_left', '紫光灯强度下箱左'
EXEC add_column_desc 'mt_test_record', 'light_drag_right', '紫光灯强度下箱右'
EXEC add_column_desc 'mt_test_record', 'white_light', '白光强度'
EXEC add_column_desc 'mt_test_record', 'striprev_up', '试片显示上 1-是, 0-否'
EXEC add_column_desc 'mt_test_record', 'striprev_down', '试片显示下 1-是, 0-否'
EXEC add_column_desc 'mt_test_record', 'remanence_intensity', '剩磁强度'
EXEC add_column_desc 'mt_test_record', 'batchno_mt', '荧光磁粉批次号'
EXEC add_column_desc 'mt_test_record', 'ts', '试验次数'
EXEC add_column_desc 'mt_test_record', 'operator', '操作工号'
EXEC add_column_desc 'mt_test_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'mt_test_record', 'shift_no', '班次'
EXEC add_column_desc 'mt_test_record', 'is_inspec_check', '是否工长确认 1-是, 0-否'
EXEC add_column_desc 'mt_test_record', 'ope_d_t', '探伤日期时间'
EXEC add_column_desc 'mt_test_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'material_record';
CREATE TABLE material_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  material_name VARCHAR(32) NOT NULL,
  manufacturer_id INT NOT NULL,
  dept INT NOT NULL,
  batch_no VARCHAR(32) NOT NULL,
  parameter1 VARCHAR(32) NULL,
  parameter2 VARCHAR(32) NULL,
  parameter3 VARCHAR(32) NULL,
  parameter4 VARCHAR(32) NULL,
  parameter5 VARCHAR(32) NULL,
  parameter6 VARCHAR(32) NULL,
  parameter7 VARCHAR(32) NULL,
  parameter8 VARCHAR(32) NULL,
  operator VARCHAR(32) NOT NULL,
  status INT DEFAULT 0 NOT NULL,
  start_time DATETIME NULL,
  suspend_time DATETIME NULL,
  stop_time DATETIME NULL,
  create_time DATETIME NOT NULL,
  material_id INT NULL
);

GO

EXEC add_table_desc 'material_record', '原材料管理记录表'
EXEC add_column_desc 'material_record', 'id', 'ID'
EXEC add_column_desc 'material_record', 'material_name', '原材料名称'
EXEC add_column_desc 'material_record', 'manufacturer_id', '供应商id'
EXEC add_column_desc 'material_record', 'dept', '使用部门'
EXEC add_column_desc 'material_record', 'batch_no', '批次号'
EXEC add_column_desc 'material_record', 'parameter1', '参数1'
EXEC add_column_desc 'material_record', 'parameter2', '参数2'
EXEC add_column_desc 'material_record', 'parameter3', '参数3'
EXEC add_column_desc 'material_record', 'parameter4', '参数4'
EXEC add_column_desc 'material_record', 'parameter5', '参数5'
EXEC add_column_desc 'material_record', 'parameter6', '参数6'
EXEC add_column_desc 'material_record', 'parameter7', '参数7'
EXEC add_column_desc 'material_record', 'parameter8', '参数8'
EXEC add_column_desc 'material_record', 'operator', '操作工号'
EXEC add_column_desc 'material_record', 'status', '状态 0-新创建, 1-在用, 2-暂停, 3-结束'
EXEC add_column_desc 'material_record', 'start_time', '原材料开始使用日期时间'
EXEC add_column_desc 'material_record', 'suspend_time', '原材料暂停使用日期时间'
EXEC add_column_desc 'material_record', 'stop_time', '原材料停止使用日期时间'
EXEC add_column_desc 'material_record', 'create_time', '记录生成时间'
EXEC add_column_desc 'material_record', 'material_id', '原材料名称ID'

GO



EXEC p_drop_table 'balance_test_record';
CREATE TABLE balance_test_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  balance_v180 DECIMAL(5,1) NOT NULL,
  balance_a180 INT NOT NULL,
  balance_v270 DECIMAL(5,1) NOT NULL,
  balance_a270 INT NOT NULL,
  ts INT DEFAULT 0 NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_balance_test_inspector_shift on balance_test_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'balance_test_record', '平衡机开班试验记录表'
EXEC add_column_desc 'balance_test_record', 'id', 'ID'
EXEC add_column_desc 'balance_test_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'balance_test_record', 'design', '轮型'
EXEC add_column_desc 'balance_test_record', 'balance_v180', '180°不平衡量'
EXEC add_column_desc 'balance_test_record', 'balance_a180', '180°不平衡值对应角度'
EXEC add_column_desc 'balance_test_record', 'balance_v270', '270°不平衡量'
EXEC add_column_desc 'balance_test_record', 'balance_a270', '270°不平衡值对应角度'
EXEC add_column_desc 'balance_test_record', 'ts', '试验次数'
EXEC add_column_desc 'balance_test_record', 'operator', '操作工号'
EXEC add_column_desc 'balance_test_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'balance_test_record', 'shift_no', '班次'
EXEC add_column_desc 'balance_test_record', 'is_inspec_check', '是否工长确认 1-是, 0-否'
EXEC add_column_desc 'balance_test_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'balance_test_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'shot_test_record';
CREATE TABLE shot_test_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  shift_no VARCHAR(32) NOT NULL,
  peening_time INT NOT NULL,
  intensity_front DECIMAL(6,2) NOT NULL,
  intensity_back DECIMAL(6,2) NOT NULL,
  coverage_front INT NOT NULL,
  coverage_back INT NOT NULL,
  sieve_no INT NOT NULL,
  shot_type VARCHAR(32) NOT NULL,
  amount_onsieve INT NOT NULL,
  shotpeener_no VARCHAR(32) NOT NULL,
  batch_no VARCHAR(32) NULL,
  ts INT DEFAULT 0 NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_shot_test_inspector_shift on shot_test_record(inspector_id, shift_no);
GO

EXEC add_table_desc 'shot_test_record', '抛丸试验记录表'
EXEC add_column_desc 'shot_test_record', 'id', 'ID'
EXEC add_column_desc 'shot_test_record', 'operator', '操作工号'
EXEC add_column_desc 'shot_test_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'shot_test_record', 'shift_no', '班次'
EXEC add_column_desc 'shot_test_record', 'peening_time', '抛丸时间'
EXEC add_column_desc 'shot_test_record', 'intensity_front', '抛丸强度外侧'
EXEC add_column_desc 'shot_test_record', 'intensity_back', '抛丸强度内侧'
EXEC add_column_desc 'shot_test_record', 'coverage_front', '覆盖率外侧'
EXEC add_column_desc 'shot_test_record', 'coverage_back', '覆盖率内侧'
EXEC add_column_desc 'shot_test_record', 'sieve_no', '筛号14'
EXEC add_column_desc 'shot_test_record', 'shot_type', '钢丸规格'
EXEC add_column_desc 'shot_test_record', 'amount_onsieve', '筛上留量'
EXEC add_column_desc 'shot_test_record', 'shotpeener_no', '抛丸机号'
EXEC add_column_desc 'shot_test_record', 'batch_no', '批次号'
EXEC add_column_desc 'shot_test_record', 'ts', '试验次数'
EXEC add_column_desc 'shot_test_record', 'is_inspec_check', '是否工长确认 1-是, 0-否'
EXEC add_column_desc 'shot_test_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'shot_test_record', 'create_time', '记录生成时间'

GO


EXEC p_drop_table 'mec_property';
CREATE TABLE mec_property
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    wheel_serial VARCHAR(32) NULL,
    inspector_id VARCHAR(32) NULL,
    design VARCHAR(32) NULL,
    test_date DATE NULL,
    test_no VARCHAR(32) NULL,
    report_date DATE NULL,
    retest INT NULL,
    pour_batch1 DATE NULL,
    pour_batch2 DATE NULL,
    residual_stress DECIMAL(10,1) NULL,
    tensile INT NULL,
    elongation DECIMAL(10,1) NULL,
    hardness_location1 INT NULL,
    hardness_location2 INT NULL,
    hardness_location3 INT NULL,
    impact_avg DECIMAL(10,1) NULL,
    impact_min DECIMAL(10,1) NULL,
    impact_location1 DECIMAL(10,1) NULL,
    impact_location2 DECIMAL(10,1) NULL,
    impact_location3 DECIMAL(10,1) NULL,
    ope_d_t DATETIME NULL,
    create_date DATETIME NULL
);

GO

EXEC add_table_desc 'mec_property', '机械性能记录表'

EXEC add_column_desc 'mec_property', 'id', '主键'
EXEC add_column_desc 'mec_property', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'mec_property', 'inspector_id', '检查员工号'
EXEC add_column_desc 'mec_property', 'design', '轮型'
EXEC add_column_desc 'mec_property', 'test_date', '试验日期'
EXEC add_column_desc 'mec_property', 'test_no', '试验编号'
EXEC add_column_desc 'mec_property', 'report_date', '报告日期'
EXEC add_column_desc 'mec_property', 'retest', '是否复试 1-是, 0-否'
EXEC add_column_desc 'mec_property', 'pour_batch1', '浇注批次1'
EXEC add_column_desc 'mec_property', 'pour_batch2', '浇注批次2'
EXEC add_column_desc 'mec_property', 'residual_stress', '残余应力'
EXEC add_column_desc 'mec_property', 'tensile', '抗拉强度'
EXEC add_column_desc 'mec_property', 'elongation', '延伸率'
EXEC add_column_desc 'mec_property', 'hardness_location1', '断面硬度1'
EXEC add_column_desc 'mec_property', 'hardness_location2', '断面硬度2'
EXEC add_column_desc 'mec_property', 'hardness_location3', '断面硬度3'
EXEC add_column_desc 'mec_property', 'impact_avg', '平均冲击功'
EXEC add_column_desc 'mec_property', 'impact_min', '最低冲击功'
EXEC add_column_desc 'mec_property', 'impact_location1', '冲击功1'
EXEC add_column_desc 'mec_property', 'impact_location2', '冲击功2'
EXEC add_column_desc 'mec_property', 'impact_location3', '冲击功3'
EXEC add_column_desc 'mec_property', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'mec_property', 'create_date', '记录生成日期时间'

GO

EXEC p_drop_table 'mec_record';
CREATE TABLE mec_record
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    wheel_serial VARCHAR(32) NOT NULL,
    operator VARCHAR(32) NOT NULL,
    mec_serial VARCHAR(50) NOT NULL,
    status INT NOT NULL,
    create_date DATETIME NOT NULL
);

GO

EXEC add_table_desc 'mec_record', '机械性能批次号记录表'

EXEC add_column_desc 'mec_record', 'id', '主键'
EXEC add_column_desc 'mec_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'mec_record', 'operator', '操作工号'
EXEC add_column_desc 'mec_record', 'mec_serial', '机械性能批次号'
EXEC add_column_desc 'mec_record', 'status', '状态、1性能抽检、2性能纠回、3性能放行'
EXEC add_column_desc 'mec_record', 'create_date', '记录生成日期时间'

GO


EXEC p_drop_table 'machine_record';
CREATE TABLE machine_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  j_s1 INT NULL,
  j_s2 INT NULL,
  t_s1 DECIMAL(8,1) NULL,
  t_s2 INT NULL,
  k_s1 INT NULL,
  k_s2 INT NULL,
  w_s1 INT NULL,
  w_s2 INT NULL,
  j_id INT NULL,
  j_id_last INT NULL,
  j_id_re INT NULL,
  j_counts INT DEFAULT 0 NOT NULL,
  t_id INT NULL,
  t_id_last INT NULL,
  t_id_re INT NULL,
  t_counts INT DEFAULT 0 NOT NULL,
  k_id INT NULL,
  k_id_last INT NULL,
  k_id_re INT NULL,
  k_counts INT DEFAULT 0 NOT NULL,
  w_id INT NULL,
  w_id_last INT NULL,
  w_id_re INT NULL,
  w_counts INT DEFAULT 0 NOT NULL,
  q_id INT NULL,
  q_counts INT DEFAULT 0 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL
);

create unique index uk_machine_record_wheel_serial on machine_record(wheel_serial);
GO

EXEC add_table_desc 'machine_record', '机加工状态表'
EXEC add_column_desc 'machine_record', 'id', 'ID'
EXEC add_column_desc 'machine_record', 'wheel_serial', '车轮号'
EXEC add_column_desc 'machine_record', 'design', '轮型'
EXEC add_column_desc 'machine_record', 'j_s1', '基面S1参数'
EXEC add_column_desc 'machine_record', 'j_s2', '基面S2参数'
EXEC add_column_desc 'machine_record', 't_s1', '踏面S1参数'
EXEC add_column_desc 'machine_record', 't_s2', '踏面S2参数'
EXEC add_column_desc 'machine_record', 'k_s1', '镗孔S1参数'
EXEC add_column_desc 'machine_record', 'k_s2', '镗孔S2参数'
EXEC add_column_desc 'machine_record', 'w_s1', '外辐板S1参数'
EXEC add_column_desc 'machine_record', 'w_s2', '外辐板S2参数'
EXEC add_column_desc 'machine_record', 'j_id', '基面日志表id（第一次）'
EXEC add_column_desc 'machine_record', 'j_id_last', '基面日志表id（最后一次）'
EXEC add_column_desc 'machine_record', 'j_id_re', '基面日志表id（返修）'
EXEC add_column_desc 'machine_record', 'j_counts', '基面加工次数'
EXEC add_column_desc 'machine_record', 't_id', '踏面日志表id（第一次）'
EXEC add_column_desc 'machine_record', 't_id_last', '踏面日志表id（最后一次）'
EXEC add_column_desc 'machine_record', 't_id_re', '踏面日志表（返修）'
EXEC add_column_desc 'machine_record', 't_counts', '踏面加工次数'
EXEC add_column_desc 'machine_record', 'k_id', '镗孔日志表id（第一次）'
EXEC add_column_desc 'machine_record', 'k_id_last', '镗孔日志表id（最后一次）'
EXEC add_column_desc 'machine_record', 'k_id_re', '镗孔日志表id（返修）'
EXEC add_column_desc 'machine_record', 'k_counts', '镗孔加工次数'
EXEC add_column_desc 'machine_record', 'w_id', '外辐板日志表id（第一次）'
EXEC add_column_desc 'machine_record', 'w_id_last', '外辐板日志表id（最后一次）'
EXEC add_column_desc 'machine_record', 'w_id_re', '外辐板日志表id（返修）'
EXEC add_column_desc 'machine_record', 'w_counts', '外辐板加工次数'
EXEC add_column_desc 'machine_record', 'q_id', '去重日志表id'
EXEC add_column_desc 'machine_record', 'q_counts', '去重加工次数'
EXEC add_column_desc 'machine_record', 'create_time', '创建时间'
EXEC add_column_desc 'machine_record', 'memo', '备注'

GO


EXEC p_drop_table 'calibra_wheel';
CREATE TABLE calibra_wheel
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  machine_no INT NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  claw1 DECIMAL(8,2) NULL,
  claw1_sym DECIMAL(8,2) NULL,
  claw2 DECIMAL(8,2) NULL,
  claw3 DECIMAL(8,2) NULL,
  rest1 DECIMAL(8,2) NULL,
  rest2 DECIMAL(8,2) NULL,
  rest3 DECIMAL(8,2) NULL,
  is_check INT DEFAULT 0 NOT NULL,
  machine_count INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_calibra_wheel_machine_no on calibra_wheel(machine_no);
GO

EXEC add_table_desc 'calibra_wheel', '标准轮检测表'
EXEC add_column_desc 'calibra_wheel', 'id', 'ID'
EXEC add_column_desc 'calibra_wheel', 'machine_no', '车床号'
EXEC add_column_desc 'calibra_wheel', 'operator', '操作工号'
EXEC add_column_desc 'calibra_wheel', 'inspector_id', '当班工长号'
EXEC add_column_desc 'calibra_wheel', 'claw1', '卡爪偏差1'
EXEC add_column_desc 'calibra_wheel', 'claw1_sym', '卡爪偏差1对称'
EXEC add_column_desc 'calibra_wheel', 'claw2', '卡爪偏差2'
EXEC add_column_desc 'calibra_wheel', 'claw3', '卡爪偏差3'
EXEC add_column_desc 'calibra_wheel', 'rest1', '支承块偏差1'
EXEC add_column_desc 'calibra_wheel', 'rest2', '支承块偏差2'
EXEC add_column_desc 'calibra_wheel', 'rest3', '支承块偏差3'
EXEC add_column_desc 'calibra_wheel', 'is_check', '是否量具检查 1-是, 0-否'
EXEC add_column_desc 'calibra_wheel', 'machine_count', '加工数量'
EXEC add_column_desc 'calibra_wheel', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'calibra_wheel', 'create_time', '创建时间'

GO


EXEC p_drop_table 'j_machine_record';
CREATE TABLE j_machine_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  cali_wheel_id INT NOT NULL,
  machine_no INT NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  j_s1 INT NOT NULL,
  j_s2 INT NOT NULL,
  f DECIMAL(8,2) NULL,
  d2_dia DECIMAL(8,2) NULL,
  d2_cir DECIMAL(8,2) NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  is_check INT DEFAULT 0 NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL
);

create index ix_j_machine_record_machine_no on j_machine_record(machine_no);
GO

EXEC add_table_desc 'j_machine_record', '基面加工日志表'
EXEC add_column_desc 'j_machine_record', 'id', 'ID'
EXEC add_column_desc 'j_machine_record', 'cali_wheel_id', '标准轮ID'
EXEC add_column_desc 'j_machine_record', 'machine_no', '车床号'
EXEC add_column_desc 'j_machine_record', 'operator', '基面操作工号'
EXEC add_column_desc 'j_machine_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'j_machine_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'j_machine_record', 'j_s1', '基面S1参数'
EXEC add_column_desc 'j_machine_record', 'j_s2', '基面S2参数'
EXEC add_column_desc 'j_machine_record', 'f', 'F参数'
EXEC add_column_desc 'j_machine_record', 'd2_dia', 'D2直径'
EXEC add_column_desc 'j_machine_record', 'd2_cir', 'D2圆度'
EXEC add_column_desc 'j_machine_record', 'rework_code', '返工代码'
EXEC add_column_desc 'j_machine_record', 'is_check', '是否抽检 1-是, 0-否'
EXEC add_column_desc 'j_machine_record', 'is_inspec_check', '是否工长抽检 1-是, 0-否'
EXEC add_column_desc 'j_machine_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'j_machine_record', 'create_time', '创建时间'
EXEC add_column_desc 'j_machine_record', 'memo', '备注'

GO


EXEC p_drop_table 't_machine_record';
CREATE TABLE t_machine_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  machine_no INT NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  t_s1 DECIMAL(8,1) NOT NULL,
  t_s2 INT NOT NULL,
  rim_width DECIMAL(8,1) NULL,
  hub_length DECIMAL(8,1) NULL,
  flange_tread_profile INT NULL,
  rolling_circle_dia DECIMAL(8,1) NULL,
  t_chamfer INT NULL,
  rimdev1 DECIMAL(8,2) NULL,
  rimdev2 DECIMAL(8,2) NULL,
  rimdev3 DECIMAL(8,2) NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  is_rolling_dia_check INT DEFAULT 0 NOT NULL,
  is_check INT DEFAULT 0 NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  is_measure_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time SMALLDATETIME NOT NULL,
  memo VARCHAR(32) NULL
);

create index ix_t_machine_record_machine_no on t_machine_record(machine_no);
GO

EXEC add_table_desc 't_machine_record', '踏面加工日志表'
EXEC add_column_desc 't_machine_record', 'id', 'ID'
EXEC add_column_desc 't_machine_record', 'machine_no', '车床号'
EXEC add_column_desc 't_machine_record', 'operator', '踏面操作工号'
EXEC add_column_desc 't_machine_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 't_machine_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 't_machine_record', 't_s1', '踏面S1参数'
EXEC add_column_desc 't_machine_record', 't_s2', '踏面S2参数'
EXEC add_column_desc 't_machine_record', 'rim_width', '轮辋宽度'
EXEC add_column_desc 't_machine_record', 'hub_length', '轮毂长度'
EXEC add_column_desc 't_machine_record', 'flange_tread_profile', '轮缘踏面外形'
EXEC add_column_desc 't_machine_record', 'rolling_circle_dia', '滚动圆直径'
EXEC add_column_desc 't_machine_record', 't_chamfer', '踏面倒角'
EXEC add_column_desc 't_machine_record', 'rimdev1', '轮辋偏差1爪'
EXEC add_column_desc 't_machine_record', 'rimdev2', '轮辋偏差2爪'
EXEC add_column_desc 't_machine_record', 'rimdev3', '轮辋偏差3爪'
EXEC add_column_desc 't_machine_record', 'rework_code', '返工代码'
EXEC add_column_desc 't_machine_record', 'is_rolling_dia_check', '是否滚动圆直径抽检 1-是, 0-否'
EXEC add_column_desc 't_machine_record', 'is_check', '是否抽检 1-是, 0-否'
EXEC add_column_desc 't_machine_record', 'is_inspec_check', '是否工长抽检 1-是, 0-否'
EXEC add_column_desc 't_machine_record', 'is_measure_check', '是否量具检查 1-是, 0-否'
EXEC add_column_desc 't_machine_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 't_machine_record', 'create_time', '创建时间'
EXEC add_column_desc 't_machine_record', 'memo', '备注'

GO


EXEC p_drop_table 'k_machine_record';
CREATE TABLE k_machine_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  machine_no INT NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  location VARCHAR(32) NOT NULL,
  k_s1 INT NOT NULL,
  k_s2 INT NOT NULL,
  concentricity DECIMAL(8,2) NULL,
  bore_dia DECIMAL(8,1) NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  is_check INT DEFAULT 0 NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  is_measure_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL
);

create index ix_k_machine_record_machine_no on k_machine_record(machine_no);
GO

EXEC add_table_desc 'k_machine_record', '镗孔加工日志表'
EXEC add_column_desc 'k_machine_record', 'id', 'ID'
EXEC add_column_desc 'k_machine_record', 'machine_no', '车床号'
EXEC add_column_desc 'k_machine_record', 'operator', '镗孔操作工号'
EXEC add_column_desc 'k_machine_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'k_machine_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'k_machine_record', 'location', '工位'
EXEC add_column_desc 'k_machine_record', 'k_s1', '镗孔S1参数'
EXEC add_column_desc 'k_machine_record', 'k_s2', '镗孔S2参数'
EXEC add_column_desc 'k_machine_record', 'concentricity', '轮毂孔对踏面同轴度'
EXEC add_column_desc 'k_machine_record', 'bore_dia', '轮毂孔径'
EXEC add_column_desc 'k_machine_record', 'rework_code', '返工代码'
EXEC add_column_desc 'k_machine_record', 'is_check', '是否抽检'
EXEC add_column_desc 'k_machine_record', 'is_inspec_check', '是否工长抽检 1-是, 0-否'
EXEC add_column_desc 'k_machine_record', 'is_measure_check', '是否量具检查 1-是, 0-否'
EXEC add_column_desc 'k_machine_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'k_machine_record', 'create_time', '创建时间'
EXEC add_column_desc 'k_machine_record', 'memo', '备注'

GO


EXEC p_drop_table 'q_machine_record';
CREATE TABLE q_machine_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  cali_wheel_id INT NOT NULL,
  machine_no INT NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  balance_v INT NULL,
  balance_a INT NULL,
  chuck1 DECIMAL(8,2) NULL,
  pad1 DECIMAL(8,2) NULL,
  deviation DECIMAL(8,2) NULL,
  hold_code VARCHAR(32) DEFAULT '' NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL
);

create index ix_q_machine_record_machine_no on q_machine_record(machine_no);
GO

EXEC add_table_desc 'q_machine_record', '去重加工日志表'
EXEC add_column_desc 'q_machine_record', 'id', 'ID'
EXEC add_column_desc 'q_machine_record', 'cali_wheel_id', '外键'
EXEC add_column_desc 'q_machine_record', 'machine_no', '车床号'
EXEC add_column_desc 'q_machine_record', 'operator', '去重操作工号'
EXEC add_column_desc 'q_machine_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'q_machine_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'q_machine_record', 'balance_v', '原g.m'
EXEC add_column_desc 'q_machine_record', 'balance_a', '角度'
EXEC add_column_desc 'q_machine_record', 'chuck1', '1爪'
EXEC add_column_desc 'q_machine_record', 'pad1', '1爪对称'
EXEC add_column_desc 'q_machine_record', 'deviation', '偏差值'
EXEC add_column_desc 'q_machine_record', 'hold_code', '保留代码'
EXEC add_column_desc 'q_machine_record', 'is_inspec_check', '是否工长抽检 1-是, 0-否'
EXEC add_column_desc 'q_machine_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'q_machine_record', 'create_time', '创建时间'
EXEC add_column_desc 'q_machine_record', 'memo', '备注'

GO


EXEC p_drop_table 'w_machine_record';
CREATE TABLE w_machine_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  machine_no INT NOT NULL,
  operator VARCHAR(32) NOT NULL,
  inspector_id VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  w_s1 INT NOT NULL,
  w_s2 INT NOT NULL,
  hub_exradius DECIMAL(8,2) NOT NULL,
  plate_thickness DECIMAL(8,2) NOT NULL,
  rim_thickness DECIMAL(8,2) NULL,
  machined_step INT NOT NULL,
  rework_code VARCHAR(32) DEFAULT '' NOT NULL,
  is_measure_check INT DEFAULT 0 NOT NULL,
  is_inspec_check INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL
);

create index ix_w_machine_record_machine_no on w_machine_record(machine_no);
GO

EXEC add_table_desc 'w_machine_record', '外幅板加工日志表'
EXEC add_column_desc 'w_machine_record', 'id', 'ID'
EXEC add_column_desc 'w_machine_record', 'machine_no', '车床号'
EXEC add_column_desc 'w_machine_record', 'operator', '外幅板操作工号'
EXEC add_column_desc 'w_machine_record', 'inspector_id', '当班工长号'
EXEC add_column_desc 'w_machine_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'w_machine_record', 'w_s1', '外幅板S1参数'
EXEC add_column_desc 'w_machine_record', 'w_s2', '外幅板S2参数'
EXEC add_column_desc 'w_machine_record', 'hub_exradius', '轮毂外径'
EXEC add_column_desc 'w_machine_record', 'plate_thickness', '幅板厚度'
EXEC add_column_desc 'w_machine_record', 'rim_thickness', '轮辋厚度'
EXEC add_column_desc 'w_machine_record', 'machined_step', '接刀台阶'
EXEC add_column_desc 'w_machine_record', 'rework_code', '返工代码'
EXEC add_column_desc 'w_machine_record', 'is_measure_check', '是否量具检查 1-是, 0-否'
EXEC add_column_desc 'w_machine_record', 'is_inspec_check', '是否工长抽检 1-是, 0-否'
EXEC add_column_desc 'w_machine_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'w_machine_record', 'create_time', '创建时间'
EXEC add_column_desc 'w_machine_record', 'memo', '备注'

GO

EXEC p_drop_table 'sample_wheel_record';
CREATE TABLE sample_wheel_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  inspector_id VARCHAR(32) NULL,
  wheel_serial VARCHAR(32) NULL,
  design VARCHAR(32) NULL,
  check_code VARCHAR(32) NULL,
  ope_d_t DATETIME NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'sample_wheel_record', '抽检车轮记录表'

EXEC add_column_desc 'sample_wheel_record', 'id', '主键'
EXEC add_column_desc 'sample_wheel_record', 'inspector_id', ' 检查员工号'
EXEC add_column_desc 'sample_wheel_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'sample_wheel_record', 'design', '轮型'
EXEC add_column_desc 'sample_wheel_record', 'check_code', '验收编号'
EXEC add_column_desc 'sample_wheel_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'sample_wheel_record', 'create_time', '记录生成时间'

GO



EXEC p_drop_table 'audit_detail';
CREATE TABLE audit_detail
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  audit_batch VARCHAR(32) NOT NULL,
  operator VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  [user] VARCHAR(32) NULL,
  cph VARCHAR(32) NULL,
  company_code VARCHAR(32) NULL,
  id_number VARCHAR(32) NULL,
  c103 VARCHAR(32) NULL,
  c104 VARCHAR(32) NULL,
  c105 VARCHAR(32) NULL,
  c106 VARCHAR(32) NULL,
  c107 VARCHAR(32) NULL,
  c108 VARCHAR(32) NULL,
  c109 VARCHAR(32) NULL,
  c110 VARCHAR(32) NULL,
  c111 VARCHAR(32) NULL,
  c112 VARCHAR(32) NULL,
  c113 VARCHAR(32) NULL,
  c114 VARCHAR(32) NULL,
  c115 VARCHAR(32) NULL,
  c116 VARCHAR(32) NULL,
  c117 VARCHAR(32) NULL,
  c118 VARCHAR(32) NULL,
  c119 VARCHAR(32) NULL,
  c120 VARCHAR(32) NULL,
  c121 VARCHAR(32) NULL,
  c122 VARCHAR(32) NULL,
  c123 VARCHAR(32) NULL,
  c126 VARCHAR(32) NULL,
  c130 VARCHAR(32) NULL,
  c131 VARCHAR(32) NULL,
  c133 VARCHAR(32) NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

create index ix_audit_detail_batch on audit_detail(audit_batch);
create index ix_audit_detail_wheel_serial on audit_detail(wheel_serial);
GO

EXEC add_table_desc 'audit_detail', '二维码校验记录表'

EXEC add_column_desc 'audit_detail', 'id', '主键'
EXEC add_column_desc 'audit_detail', 'audit_batch', '批次号'
EXEC add_column_desc 'audit_detail', 'operator', '操作工号'
EXEC add_column_desc 'audit_detail', 'wheel_serial', '车轮号'
EXEC add_column_desc 'audit_detail', 'user', 'user'
EXEC add_column_desc 'audit_detail', 'cph', 'cph'
EXEC add_column_desc 'audit_detail', 'company_code', 'companyCode'
EXEC add_column_desc 'audit_detail', 'id_number', 'idNumber'
EXEC add_column_desc 'audit_detail', 'c103', 'C103'
EXEC add_column_desc 'audit_detail', 'c104', 'C104'
EXEC add_column_desc 'audit_detail', 'c105', 'C105'
EXEC add_column_desc 'audit_detail', 'c106', 'C106'
EXEC add_column_desc 'audit_detail', 'c107', 'C107'
EXEC add_column_desc 'audit_detail', 'c108', 'C108'
EXEC add_column_desc 'audit_detail', 'c109', 'C109'
EXEC add_column_desc 'audit_detail', 'c110', 'C110'
EXEC add_column_desc 'audit_detail', 'c111', 'C111'
EXEC add_column_desc 'audit_detail', 'c112', 'C112'
EXEC add_column_desc 'audit_detail', 'c113', 'C113'
EXEC add_column_desc 'audit_detail', 'c114', 'C114'
EXEC add_column_desc 'audit_detail', 'c115', 'C115'
EXEC add_column_desc 'audit_detail', 'c116', 'C116'
EXEC add_column_desc 'audit_detail', 'c117', 'C117'
EXEC add_column_desc 'audit_detail', 'c118', 'C118'
EXEC add_column_desc 'audit_detail', 'c119', 'C119'
EXEC add_column_desc 'audit_detail', 'c120', 'C120'
EXEC add_column_desc 'audit_detail', 'c121', 'C121'
EXEC add_column_desc 'audit_detail', 'c122', 'C122'
EXEC add_column_desc 'audit_detail', 'c123', 'C123'
EXEC add_column_desc 'audit_detail', 'c126', 'C126'
EXEC add_column_desc 'audit_detail', 'c130', 'C130'
EXEC add_column_desc 'audit_detail', 'c131', 'C131'
EXEC add_column_desc 'audit_detail', 'c133', 'C133'
EXEC add_column_desc 'audit_detail', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'audit_detail', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'raw_wheel_print_record';
CREATE TABLE raw_wheel_print_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  operator VARCHAR(32) NOT NULL,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  bore_size INT NULL,
  finished INT DEFAULT 0 NOT NULL,
  scrap_code VARCHAR(32) NULL,
  ts INT DEFAULT 0 NOT NULL,
  ope_d_t DATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'raw_wheel_print_record', '毛坯车轮成品打印记录表'
EXEC add_column_desc 'raw_wheel_print_record', 'id', 'ID'
EXEC add_column_desc 'raw_wheel_print_record', 'operator', ' 检查员工号'
EXEC add_column_desc 'raw_wheel_print_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'raw_wheel_print_record', 'design', '轮型'
EXEC add_column_desc 'raw_wheel_print_record', 'bore_size', '轴孔尺寸'
EXEC add_column_desc 'raw_wheel_print_record', 'finished', '是否成品 1-是, 0-否'
EXEC add_column_desc 'raw_wheel_print_record', 'scrap_code', '废品代码'
EXEC add_column_desc 'raw_wheel_print_record', 'ts', '次数'
EXEC add_column_desc 'raw_wheel_print_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'raw_wheel_print_record', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'scrap_reason_record';
CREATE TABLE scrap_reason_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  inspector_id VARCHAR(32) NULL,
  wheel_serial VARCHAR(32) NULL,
  design VARCHAR(32) NULL,
  scrap_code VARCHAR(32) NULL,
  scrap_reason_code VARCHAR(32) NULL,
  ope_d_t DATETIME NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'scrap_reason_record', '废品原因分析记录表'
EXEC add_column_desc 'scrap_reason_record', 'id', '主键'
EXEC add_column_desc 'scrap_reason_record', 'inspector_id', '操作工号'
EXEC add_column_desc 'scrap_reason_record', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'scrap_reason_record', 'design', '轮型'
EXEC add_column_desc 'scrap_reason_record', 'scrap_code', '废品代码'
EXEC add_column_desc 'scrap_reason_record', 'scrap_reason_code', '废品原因代码'
EXEC add_column_desc 'scrap_reason_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'scrap_reason_record', 'create_time', '创建时间'

GO


EXEC p_drop_table 'hi_heat_prework_record';
CREATE TABLE hi_heat_prework_record
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  h_date DATE NOT NULL,
  furno INT NOT NULL,
  shift INT NOT NULL,
  supervisor VARCHAR(32) NOT NULL,
  operator VARCHAR(32) NOT NULL,
  target_temp VARCHAR(32) NOT NULL,
  actual_cycle TIME NOT NULL,
  time_checked SMALLDATETIME NOT NULL,
  tread_quench_delay TIME NOT NULL,
  tread_time_checked SMALLDATETIME NOT NULL,
  spray_time TIME NOT NULL,
  spray_time_checked SMALLDATETIME NOT NULL,
  water_pressure DECIMAL(8,3) NOT NULL,
  water_pressure_time_checked SMALLDATETIME NOT NULL,
  water_temp INT NOT NULL,
  water_temp_time_checked SMALLDATETIME NOT NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'hi_heat_prework_record', '热处理环形炉开班信息'
EXEC add_column_desc 'hi_heat_prework_record', 'id', 'ID'
EXEC add_column_desc 'hi_heat_prework_record', 'h_date', '日期'
EXEC add_column_desc 'hi_heat_prework_record', 'furno', '炉号'
EXEC add_column_desc 'hi_heat_prework_record', 'shift', '班次'
EXEC add_column_desc 'hi_heat_prework_record', 'supervisor', '当班工长'
EXEC add_column_desc 'hi_heat_prework_record', 'operator', '操作工'
EXEC add_column_desc 'hi_heat_prework_record', 'target_temp', '目标温度'
EXEC add_column_desc 'hi_heat_prework_record', 'actual_cycle', '实测周期'
EXEC add_column_desc 'hi_heat_prework_record', 'time_checked', '检测时间'
EXEC add_column_desc 'hi_heat_prework_record', 'tread_quench_delay', '踏面淬火延时'
EXEC add_column_desc 'hi_heat_prework_record', 'tread_time_checked', '淬火检测时间'
EXEC add_column_desc 'hi_heat_prework_record', 'spray_time', '喷水时间'
EXEC add_column_desc 'hi_heat_prework_record', 'spray_time_checked', '喷水检测时间'
EXEC add_column_desc 'hi_heat_prework_record', 'water_pressure', '水压'
EXEC add_column_desc 'hi_heat_prework_record', 'water_pressure_time_checked', '水压检测时间'
EXEC add_column_desc 'hi_heat_prework_record', 'water_temp', '水温'
EXEC add_column_desc 'hi_heat_prework_record', 'water_temp_time_checked', '水温检测时间'
EXEC add_column_desc 'hi_heat_prework_record', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'low_heat_prework_record';
CREATE TABLE low_heat_prework_record
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    l_date DATE NOT NULL,
    shift INT NOT NULL,
    supervisor VARCHAR(32) NOT NULL,
    operator VARCHAR(32) NOT NULL,
    target_temp VARCHAR(32) NOT NULL,
    actual_cycle TIME NOT NULL,
    time_checked SMALLDATETIME NOT NULL,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'low_heat_prework_record', '热处理回火炉开班信息'
EXEC add_column_desc 'low_heat_prework_record', 'id', 'ID'
EXEC add_column_desc 'low_heat_prework_record', 'l_date', '日期'
EXEC add_column_desc 'low_heat_prework_record', 'shift', '班次'
EXEC add_column_desc 'low_heat_prework_record', 'supervisor', '当班工长'
EXEC add_column_desc 'low_heat_prework_record', 'operator', '操作工'
EXEC add_column_desc 'low_heat_prework_record', 'target_temp', '目标温度'
EXEC add_column_desc 'low_heat_prework_record', 'actual_cycle', '实测周期'
EXEC add_column_desc 'low_heat_prework_record', 'time_checked', '检测时间'
EXEC add_column_desc 'low_heat_prework_record', 'create_time', '记录生成日期时间'

GO

EXEC p_drop_table 'train_no';
CREATE TABLE train_no
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  train_no VARCHAR(32) NOT NULL,
  shipped_no VARCHAR(32) NOT NULL,
  shipped_id VARCHAR(32) NOT NULL,
  con_opeid VARCHAR(32) NOT NULL,
  con_finshed INT DEFAULT 0 NOT NULL,
  shipped_date DATE NOT NULL,
  shipped_sum INT NOT NULL,
  customer_id VARCHAR(32) NOT NULL,
  c_id INT NULL,
  create_time DATETIME NOT NULL
);

create unique index uk_train_no_shipped_no on train_no(shipped_no);
GO

EXEC add_table_desc 'train_no', '发运车皮号记录表'
EXEC add_column_desc 'train_no', 'id', 'ID'
EXEC add_column_desc 'train_no', 'train_no', '车皮号'
EXEC add_column_desc 'train_no', 'shipped_no', '合格证号'
EXEC add_column_desc 'train_no', 'shipped_id', '发运员工号'
EXEC add_column_desc 'train_no', 'con_opeid', '打印合格证操作工号'
EXEC add_column_desc 'train_no', 'con_finshed', '合同是否已执行 1-是, 0-否'
EXEC add_column_desc 'train_no', 'shipped_date', '发运日期'
EXEC add_column_desc 'train_no', 'shipped_sum', '发运数量'
EXEC add_column_desc 'train_no', 'customer_id', '客户代号'
EXEC add_column_desc 'train_no', 'c_id', '合同ID'
EXEC add_column_desc 'train_no', 'create_time', '记录生成日期时间'

GO



EXEC p_drop_table 'transport_record';
CREATE TABLE transport_record
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    inspector_id VARCHAR(32) NULL,
    ope_type INT NULL,
    wheel_serial VARCHAR(32) NULL,
    design VARCHAR(32) NULL,
    bore_size VARCHAR(32) NULL,
    balance_s VARCHAR(32) NULL,
    ope_d_t DATETIME NULL,
    ts INT NULL,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'transport_record', '发运记录表'

EXEC add_column_desc 'transport_record', 'id', '主键'
EXEC add_column_desc 'transport_record', 'inspector_id', '检查员工号'
EXEC add_column_desc 'transport_record', 'ope_type', '操作分类'
EXEC add_column_desc 'transport_record', 'wheel_serial', '轮号'
EXEC add_column_desc 'transport_record', 'design', '轮型'
EXEC add_column_desc 'transport_record', 'bore_size', '轴孔尺寸'
EXEC add_column_desc 'transport_record', 'balance_s', '平衡标识'
EXEC add_column_desc 'transport_record', 'ope_d_t', '操作日期时间'
EXEC add_column_desc 'transport_record', 'ts', '第几次操作'
EXEC add_column_desc 'transport_record', 'create_time', '创建时间'

GO

EXEC p_drop_table 'ship_temp';
CREATE TABLE ship_temp
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    p_id VARCHAR(32) NULL,
    hgz VARCHAR(32) NULL,
    wheel_serial VARCHAR(32) NULL,
    serial_no INT NULL,
    hgz_serial_no INT NULL,
    shelf_no VARCHAR(32) NULL,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'ship_temp', '发运临时表'

EXEC add_column_desc 'ship_temp', 'id', '主键'
EXEC add_column_desc 'ship_temp', 'p_id', '工号'
EXEC add_column_desc 'ship_temp', 'hgz', '合格证号'
EXEC add_column_desc 'ship_temp', 'wheel_serial', '车轮序列号'
EXEC add_column_desc 'ship_temp', 'serial_no', '连续编号'
EXEC add_column_desc 'ship_temp', 'hgz_serial_no', '相同合格证的排序号'
EXEC add_column_desc 'ship_temp', 'shelf_no', '串号'
EXEC add_column_desc 'ship_temp', 'create_time', '创建时间'

GO

EXEC p_drop_table 'contract_record';
CREATE TABLE contract_record
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    contract_no VARCHAR(32) NOT NULL,
    design VARCHAR(32) NOT NULL,
    customer_id VARCHAR(32) NOT NULL,
    operator VARCHAR(32) NOT NULL,
    contract_sum INT NOT NULL,
    shipped_sum INT DEFAULT 0 NOT NULL,
    surplus_sum INT DEFAULT 0 NOT NULL,
    enabled INT DEFAULT 1 NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'contract_record', '合同管理记录表'
EXEC add_column_desc 'contract_record', 'id', 'ID'
EXEC add_column_desc 'contract_record', 'contract_no', '合同编号'
EXEC add_column_desc 'contract_record', 'design', '轮型'
EXEC add_column_desc 'contract_record', 'customer_id', '收货单位ID'
EXEC add_column_desc 'contract_record', 'operator', '操作工号'
EXEC add_column_desc 'contract_record', 'contract_sum', '合同数量'
EXEC add_column_desc 'contract_record', 'shipped_sum', '发运数量'
EXEC add_column_desc 'contract_record', 'surplus_sum', '剩余数量'
EXEC add_column_desc 'contract_record', 'enabled', '是否启用 1-是, 0-否'
EXEC add_column_desc 'contract_record', 'start_date', '合同启用日期'
EXEC add_column_desc 'contract_record', 'end_date', '合同停用日期'
EXEC add_column_desc 'contract_record', 'create_time', '记录生成日期时间'

GO

EXEC p_drop_table 'h_element_record';
CREATE TABLE h_element_record
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    fid INT NOT NULL,
    ope_id VARCHAR(32) NOT NULL,
    cast_date DATE NOT NULL,
    furnace_no INT NOT NULL,
    heat_seq INT NOT NULL,
    tap_seq INT NOT NULL,
    h DECIMAL(6,2) NOT NULL,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'h_element_record', '氢元素录入记录表'
EXEC add_column_desc 'h_element_record', 'id', 'ID'
EXEC add_column_desc 'h_element_record', 'fid', '外键'
EXEC add_column_desc 'h_element_record', 'ope_id', '操作员工号'
EXEC add_column_desc 'h_element_record', 'cast_date', '浇注日期'
EXEC add_column_desc 'h_element_record', 'furnace_no', '电炉号'
EXEC add_column_desc 'h_element_record', 'heat_seq', '炉次'
EXEC add_column_desc 'h_element_record', 'tap_seq', '出钢号'
EXEC add_column_desc 'h_element_record', 'h', '氢元素值'
EXEC add_column_desc 'h_element_record', 'create_time', '记录生成日期时间'

GO

EXEC p_drop_table 'turn_picture';
CREATE TABLE turn_picture
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    filename VARCHAR(256) NOT NULL,
    content TEXT,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'turn_picture', '轮播图表'
EXEC add_column_desc 'turn_picture', 'id', 'ID'
EXEC add_column_desc 'turn_picture', 'filename', '文件名'
EXEC add_column_desc 'turn_picture', 'content', '图片内容'
EXEC add_column_desc 'turn_picture', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'notification';
CREATE TABLE notification
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    title NVARCHAR(128) NOT NULL,
    author VARCHAR(32) NOT NULL,
    department VARCHAR(1024) NOT NULL,
    content NTEXT NOT NULL,
    publish_status INT NOT NULL,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'notification', '通知信息表'
EXEC add_column_desc 'notification', 'id', 'ID'
EXEC add_column_desc 'notification', 'title', '通知标题'
EXEC add_column_desc 'notification', 'author', '通知作者'
EXEC add_column_desc 'notification', 'department', '通知部门'
EXEC add_column_desc 'notification', 'content', '通知内容'
EXEC add_column_desc 'notification', 'publish_status', '状态 1-未发布, 2-已发布'
EXEC add_column_desc 'notification', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'technical_document';
CREATE TABLE technical_document
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    title NVARCHAR(128) NOT NULL,
    author VARCHAR(32) NOT NULL,
    department VARCHAR(1024) NOT NULL,
    filename VARCHAR(256) NOT NULL,
    content varbinary(max) NULL,
    publish_status INT NOT NULL,
    create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'technical_document', '技术文件表'
EXEC add_column_desc 'technical_document', 'id', 'ID'
EXEC add_column_desc 'technical_document', 'title', '技术文件标题'
EXEC add_column_desc 'technical_document', 'author', '技术文件作者'
EXEC add_column_desc 'technical_document', 'department', '可浏览的部门'
EXEC add_column_desc 'technical_document', 'filename', '文件名'
EXEC add_column_desc 'technical_document', 'content', '文件内容'
EXEC add_column_desc 'technical_document', 'publish_status', '状态 1-未发布, 2-已发布'
EXEC add_column_desc 'technical_document', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'notify_status';
CREATE TABLE notify_status
(
    id INT IDENTITY NOT NULL PRIMARY KEY,
    notify_id INT NOT NULL,
    notify_type INT NOT NULL,
    account_id VARCHAR(32) NOT NULL,
    read_status INT NOT NULL,
    create_time DATETIME NOT NULL
);

create unique index uk_notify_status_id_type_account on notify_status(notify_id, notify_type, account_id);
GO

EXEC add_table_desc 'notify_status', '通知状态表'
EXEC add_column_desc 'notify_status', 'id', 'ID'
EXEC add_column_desc 'notify_status', 'notify_id', '通知表或技术文件表ID'
EXEC add_column_desc 'notify_status', 'notify_type', '通知类型 1-通知, 2-技术文件'
EXEC add_column_desc 'notify_status', 'account_id', '账号ID'
EXEC add_column_desc 'notify_status', 'read_status', '阅读状态 1-未读, 2-已读'
EXEC add_column_desc 'notify_status', 'create_time', '记录生成日期时间'

GO


EXEC p_drop_table 'roll_tip';
CREATE TABLE roll_tip
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  tip VARCHAR(1024)
);
GO

EXEC add_table_desc 'roll_tip', '滚动提示表'
EXEC add_column_desc 'roll_tip', 'tip', '滚动提示内容'

GO


EXEC p_drop_table 'login_user_info';
CREATE TABLE login_user_info
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  username VARCHAR(32) NOT NULL,
  ip VARCHAR(128) NOT NULL,
  last_active_time DATETIME NOT NULL
);
GO
create unique index uk_login_user_info_username on login_user_info(username);
GO

EXEC add_table_desc 'login_user_info', '登录用户信息表'
EXEC add_column_desc 'login_user_info', 'id', 'ID'
EXEC add_column_desc 'login_user_info', 'username', '用户名'
EXEC add_column_desc 'login_user_info', 'ip', '用户IP'
EXEC add_column_desc 'login_user_info', 'last_active_time', '最后操作时间'

GO



EXEC p_drop_table 'lab_keepalive';
CREATE TABLE lab_keepalive
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  last_keepalive_time BIGINT
);
GO

EXEC add_table_desc 'lab_keepalive', '化验室同步状态表'
EXEC add_column_desc 'lab_keepalive', 'last_keepalive_time', '化验室程序最后同步时间'

GO
