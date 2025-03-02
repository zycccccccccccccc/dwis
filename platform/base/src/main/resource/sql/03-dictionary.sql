
EXEC p_drop_table 'dictionary';
CREATE TABLE dictionary (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  table_name varchar(50) NOT NULL,
  columns varchar(1024) DEFAULT NULL,
  memo varchar(50) DEFAULT NULL
);
GO

EXEC add_table_desc 'dictionary', '字典表'
EXEC add_column_desc 'dictionary', 'id', 'ID'
EXEC add_column_desc 'dictionary', 'table_name', '表名'
EXEC add_column_desc 'dictionary', 'columns', '字段信息'
EXEC add_column_desc 'dictionary', 'memo', '字典表说明'
GO


EXEC p_drop_table 'location';
CREATE TABLE location (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  location_id VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'location', '模块定义表'
EXEC add_column_desc 'location', 'id', 'ID'
EXEC add_column_desc 'location', 'location_id', '模块ID'
EXEC add_column_desc 'location', 'explain', '中文释义'
EXEC add_column_desc 'location', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'location', 'create_time', '创建时间'
EXEC add_column_desc 'location', 'memo', '备注'
GO


EXEC p_drop_table 'delay_code';
CREATE TABLE delay_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  explain_en VARCHAR(256),
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'delay_code', '延误代码表'
EXEC add_column_desc 'delay_code', 'id', 'ID'
EXEC add_column_desc 'delay_code', 'code', '代码'
EXEC add_column_desc 'delay_code', 'explain', '中文释义'
EXEC add_column_desc 'delay_code', 'explain_en', '英文释义'
EXEC add_column_desc 'delay_code', 'location', '模块'
EXEC add_column_desc 'delay_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'delay_code', 'create_time', '创建时间'
EXEC add_column_desc 'delay_code', 'memo', '备注'
GO


EXEC p_drop_table 'heat_code';
CREATE TABLE heat_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  code_type VARCHAR(50) NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'heat_code', '热处理代码表'
EXEC add_column_desc 'heat_code', 'id', 'ID'
EXEC add_column_desc 'heat_code', 'code', '代码'
EXEC add_column_desc 'heat_code', 'explain', '中文释义'
EXEC add_column_desc 'heat_code', 'code_type', '代码分类'
EXEC add_column_desc 'heat_code', 'location', '模块'
EXEC add_column_desc 'heat_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'heat_code', 'create_time', '创建时间'
EXEC add_column_desc 'heat_code', 'memo', '备注'
GO


EXEC p_drop_table 'hold_code';
CREATE TABLE hold_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  code_type VARCHAR(50) NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'hold_code', '保留代码表'
EXEC add_column_desc 'hold_code', 'id', 'ID'
EXEC add_column_desc 'hold_code', 'code', '代码'
EXEC add_column_desc 'hold_code', 'explain', '中文释义'
EXEC add_column_desc 'hold_code', 'code_type', '代码分类'
EXEC add_column_desc 'hold_code', 'location', '模块'
EXEC add_column_desc 'hold_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'hold_code', 'create_time', '创建时间'
EXEC add_column_desc 'hold_code', 'memo', '备注'
GO


EXEC p_drop_table 'machine_code';
CREATE TABLE machine_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  machine_id INT NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'machine_code', '机床号表'
EXEC add_column_desc 'machine_code', 'id', 'ID'
EXEC add_column_desc 'machine_code', 'machine_id', '机床号'
EXEC add_column_desc 'machine_code', 'location', '模块'
EXEC add_column_desc 'machine_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'machine_code', 'create_time', '创建时间'
EXEC add_column_desc 'machine_code', 'memo', '备注'
GO


EXEC p_drop_table 'machining_code';
CREATE TABLE machining_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  [procedure] VARCHAR(32) NOT NULL,
  [parameter] VARCHAR(32) NOT NULL,
  machining_code VARCHAR(32) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'machining_code', '机加工代码表'
EXEC add_column_desc 'machining_code', 'id', 'ID'
EXEC add_column_desc 'machining_code', 'procedure', '工序'
EXEC add_column_desc 'machining_code', 'parameter', '参数'
EXEC add_column_desc 'machining_code', 'machining_code', '机加工代码'
EXEC add_column_desc 'machining_code', 'explain', '中文释义'
EXEC add_column_desc 'machining_code', 'location', '模块'
EXEC add_column_desc 'machining_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'machining_code', 'create_time', '创建时间'
EXEC add_column_desc 'machining_code', 'memo', '备注'
GO


EXEC p_drop_table 'rework_code';
CREATE TABLE rework_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  code_type VARCHAR(50) NOT NULL,
  single_wheel_type INT DEFAULT 1 NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL,
  rework_flag VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'rework_code', '返工代码表'
EXEC add_column_desc 'rework_code', 'id', 'ID'
EXEC add_column_desc 'rework_code', 'code', '代码'
EXEC add_column_desc 'rework_code', 'explain', '中文释义'
EXEC add_column_desc 'rework_code', 'code_type', '代码分类'
EXEC add_column_desc 'rework_code', 'single_wheel_type', '单轮查询分类 1-外观, 2-尺寸, 3-磁探, 4-超探'
EXEC add_column_desc 'rework_code', 'location', '模块'
EXEC add_column_desc 'rework_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'rework_code', 'create_time', '创建时间'
EXEC add_column_desc 'rework_code', 'memo', '备注'
EXEC add_column_desc 'rework_code', 'rework_flag', '返工标记 F-ZZ 铸造返修,F-JJ 机加基面返修,F-JT 机加踏面返修,F-JK 机加镗孔返修,F-JW 机加外辐板返修,D 打磨'
GO


EXEC p_drop_table 'scrap_code';
CREATE TABLE scrap_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  code_type VARCHAR(50) NOT NULL,
  single_wheel_type INT DEFAULT 1 NULL,
  is_count INT DEFAULT 0 NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'scrap_code', '废品代码表'
EXEC add_column_desc 'scrap_code', 'id', 'ID'
EXEC add_column_desc 'scrap_code', 'code', '代码'
EXEC add_column_desc 'scrap_code', 'explain', '中文释义'
EXEC add_column_desc 'scrap_code', 'code_type', '代码分类'
EXEC add_column_desc 'scrap_code', 'single_wheel_type', '单轮查询分类 1-外观, 2-尺寸, 3-磁探, 4-超探'
EXEC add_column_desc 'scrap_code', 'is_count', '是否需要统计，1-需要，0-不需要'
EXEC add_column_desc 'scrap_code', 'location', '模块'
EXEC add_column_desc 'scrap_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'scrap_code', 'create_time', '创建时间'
EXEC add_column_desc 'scrap_code', 'memo', '备注'
GO


EXEC p_drop_table 'test_code';
CREATE TABLE test_code (
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  code_type VARCHAR(50) NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'test_code', '实验代码表'
EXEC add_column_desc 'test_code', 'id', 'ID'
EXEC add_column_desc 'test_code', 'code', '代码'
EXEC add_column_desc 'test_code', 'explain', '中文释义'
EXEC add_column_desc 'test_code', 'code_type', '代码分类'
EXEC add_column_desc 'test_code', 'location', '模块'
EXEC add_column_desc 'test_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'test_code', 'create_time', '创建时间'
EXEC add_column_desc 'test_code', 'memo', '备注'
GO


EXEC p_drop_table 'cihen_code';
CREATE TABLE cihen_code
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  code_type VARCHAR(50) NULL,
  location VARCHAR(256) NOT NULL,
  memo VARCHAR(50) NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'cihen_code', '磁痕代码表'

EXEC add_column_desc 'cihen_code', 'id', '主键'
EXEC add_column_desc 'cihen_code', 'code', '磁痕代码'
EXEC add_column_desc 'cihen_code', 'explain', '中文释义'
EXEC add_column_desc 'cihen_code', 'code_type', '代码分类'
EXEC add_column_desc 'cihen_code', 'location', '模块'
EXEC add_column_desc 'cihen_code', 'memo', '备注'
EXEC add_column_desc 'cihen_code', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'cihen_code', 'create_time', '创建时间'

GO


EXEC p_drop_table 'scrap_reason_code';
CREATE TABLE scrap_reason_code
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  scrap_code VARCHAR(32) NOT NULL,
  scrap_reason_code VARCHAR(32) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  location VARCHAR(256) NOT NULL,
  memo VARCHAR(50) NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'scrap_reason_code', '废品分析原因代码表'
EXEC add_column_desc 'scrap_reason_code', 'id', '主键'
EXEC add_column_desc 'scrap_reason_code', 'scrap_code', '废品代码'
EXEC add_column_desc 'scrap_reason_code', 'scrap_reason_code', '废品原因代码'
EXEC add_column_desc 'scrap_reason_code', 'explain', '中文释义'
EXEC add_column_desc 'scrap_reason_code', 'location', '模块'
EXEC add_column_desc 'scrap_reason_code', 'memo', '备注'
EXEC add_column_desc 'scrap_reason_code', 'enabled', '是否有效'
EXEC add_column_desc 'scrap_reason_code', 'create_time', '创建时间'

GO


EXEC p_drop_table 'test_wheel';
CREATE TABLE test_wheel
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  wheel_serial VARCHAR(32) NOT NULL,
  design VARCHAR(32) NOT NULL,
  standard VARCHAR(4096) NOT NULL,
  location VARCHAR(32) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);

GO

EXEC add_table_desc 'test_wheel', '质检试验轮记录表'
EXEC add_column_desc 'test_wheel', 'id', 'ID'
EXEC add_column_desc 'test_wheel', 'wheel_serial', '轮号'
EXEC add_column_desc 'test_wheel', 'design', '轮型'
EXEC add_column_desc 'test_wheel', 'standard', '标准范围'
EXEC add_column_desc 'test_wheel', 'location', '模块'
EXEC add_column_desc 'test_wheel', 'enabled', '是否启用 1-启用, 0-禁用'
EXEC add_column_desc 'test_wheel', 'create_time', '记录生成时间'
EXEC add_column_desc 'test_wheel', 'memo', '备注'

GO


EXEC p_drop_table 'case_unpack_time_ctl';
CREATE TABLE case_unpack_time_ctl
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  type_kxsj VARCHAR(5) NOT NULL,
  temp_min INT NOT NULL,
  temp_max INT NOT NULL,
  fminite INT NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'case_unpack_time_ctl', '开箱时间表'
EXEC add_column_desc 'case_unpack_time_ctl', 'id', 'ID'
EXEC add_column_desc 'case_unpack_time_ctl', 'type_kxsj', '开箱类型'
EXEC add_column_desc 'case_unpack_time_ctl', 'temp_min', '最小温度'
EXEC add_column_desc 'case_unpack_time_ctl', 'temp_max', '最大温度'
EXEC add_column_desc 'case_unpack_time_ctl', 'fminite', '开箱延迟分钟数'
EXEC add_column_desc 'case_unpack_time_ctl', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'case_unpack_time_ctl', 'create_time', '创建时间'
EXEC add_column_desc 'case_unpack_time_ctl', 'memo', '备注'

GO


EXEC p_drop_table 'chemical_control';
CREATE TABLE chemical_control
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  design VARCHAR(32) NOT NULL,
  element_abbr VARCHAR(32) NOT NULL,
  cin_value_min DECIMAL(6,3) NOT NULL,
  cin_value_max DECIMAL(6,3) NOT NULL,
  cst_value_min DECIMAL(6,3) NOT NULL,
  cst_value_max DECIMAL(6,3) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);

GO

EXEC add_table_desc 'chemical_control', '化学成分控制表'
EXEC add_column_desc 'chemical_control', 'id', 'ID'
EXEC add_column_desc 'chemical_control', 'design', '轮型'
EXEC add_column_desc 'chemical_control', 'element_abbr', '元素简写'
EXEC add_column_desc 'chemical_control', 'cin_value_min', '内控化学成分最小值'
EXEC add_column_desc 'chemical_control', 'cin_value_max', '内控化学成分最大值'
EXEC add_column_desc 'chemical_control', 'cst_value_min', '标准化学成分最小值'
EXEC add_column_desc 'chemical_control', 'cst_value_max', '标准化学成分最大值'
EXEC add_column_desc 'chemical_control', 'enabled', '是否启用: 1-启用, 0-禁用'
EXEC add_column_desc 'chemical_control', 'create_time', '记录生成时间'
EXEC add_column_desc 'chemical_control', 'memo', '备注'
GO

EXEC p_drop_table 'operating_time_ctr';
CREATE TABLE operating_time_ctr
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  dep VARCHAR(32) NOT NULL,
  operating_time INT NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'operating_time_ctr', '操作时间控制表'
EXEC add_column_desc 'operating_time_ctr', 'id', 'ID'
EXEC add_column_desc 'operating_time_ctr', 'dep', '部门'
EXEC add_column_desc 'operating_time_ctr', 'operating_time', '时间（分钟）'
EXEC add_column_desc 'operating_time_ctr', 'enabled', '是否启用: 1-启用, 0-禁用'
EXEC add_column_desc 'operating_time_ctr', 'create_time', '创建日期时间'
EXEC add_column_desc 'operating_time_ctr', 'memo', '备注'
GO


EXEC p_drop_table 'hardness_control';
CREATE TABLE hardness_control
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  design VARCHAR(32) NOT NULL,
  hardness_min INT NOT NULL,
  hardness_max INT NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'hardness_control', '硬度值控制表'
EXEC add_column_desc 'hardness_control', 'id', 'ID'
EXEC add_column_desc 'hardness_control', 'design', '轮型'
EXEC add_column_desc 'hardness_control', 'hardness_min', '硬度最小值'
EXEC add_column_desc 'hardness_control', 'hardness_max', '硬度最大值'
EXEC add_column_desc 'hardness_control', 'enabled', '是否启用 1-启用, 0-禁用'
EXEC add_column_desc 'hardness_control', 'create_time', '记录生成时间'
EXEC add_column_desc 'hardness_control', 'memo', '备注'
GO


EXEC p_drop_table 'bore_size';
CREATE TABLE bore_size
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  design VARCHAR(32) NOT NULL,
  size_parameter INT NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'bore_size', '轴孔尺寸表'
EXEC add_column_desc 'bore_size', 'id', 'ID'
EXEC add_column_desc 'bore_size', 'design', '轮型'
EXEC add_column_desc 'bore_size', 'size_parameter', '轴孔尺寸'
EXEC add_column_desc 'bore_size', 'enabled', '是否启用 1-启用, 0-禁用'
EXEC add_column_desc 'bore_size', 'create_time', '记录生成时间'
EXEC add_column_desc 'bore_size', 'memo', '备注'

GO


EXEC p_drop_table 'wheel_width_size';
CREATE TABLE wheel_width_size
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  design VARCHAR(32) NOT NULL,
  size_parameter DECIMAL(6,1) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'wheel_width_size', '轮辋尺寸表'
EXEC add_column_desc 'wheel_width_size', 'id', 'ID'
EXEC add_column_desc 'wheel_width_size', 'design', '轮型'
EXEC add_column_desc 'wheel_width_size', 'size_parameter', '轮辋尺寸'
EXEC add_column_desc 'wheel_width_size', 'enabled', '是否启用 1-启用, 0-禁用'
EXEC add_column_desc 'wheel_width_size', 'create_time', '记录生成时间'
EXEC add_column_desc 'wheel_width_size', 'memo', '备注'

GO


EXEC p_drop_table 'tape';
CREATE TABLE tape
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  design VARCHAR(32) NOT NULL,
  tapesize DECIMAL(6,1) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);

GO

EXEC add_table_desc 'tape', '带尺尺寸表'

EXEC add_column_desc 'tape', 'id', '主键'
EXEC add_column_desc 'tape', 'design', '轮型'
EXEC add_column_desc 'tape', 'tapesize', '带尺尺寸'
EXEC add_column_desc 'tape', 'enabled', '是否启用 1-启用, 0-禁用'
EXEC add_column_desc 'tape', 'create_time', '记录生成时间'
EXEC add_column_desc 'tape', 'memo', '备注'

GO


EXEC p_drop_table 'pits';
CREATE TABLE pits (
    id INT IDENTITY NOT NULL PRIMARY KEY,
    pit_no INT NOT NULL,
    memo VARCHAR(256),
    enabled INT DEFAULT 1 NOT NULL,
    create_time DATETIME NOT NULL
);

EXEC add_table_desc 'pits', '桶号表'
EXEC add_column_desc 'pits', 'id', 'ID'
EXEC add_column_desc 'pits', 'pit_no', '筒号'
EXEC add_column_desc 'pits', 'memo', '备注'
EXEC add_column_desc 'pits', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'pits', 'create_time', '创建时间'

GO


EXEC p_drop_table 'furnace_patching_table';
CREATE TABLE furnace_patching_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  patching_name VARCHAR(32) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'furnace_patching_table', '喷补料补炉位置表'
EXEC add_column_desc 'furnace_patching_table', 'id', 'ID'
EXEC add_column_desc 'furnace_patching_table', 'patching_name', '喷补料补炉位置'
EXEC add_column_desc 'furnace_patching_table', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'furnace_patching_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'furnace_ramming_table';
CREATE TABLE furnace_ramming_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  ramming_name VARCHAR(32) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'furnace_ramming_table', '打结料补炉位置表'
EXEC add_column_desc 'furnace_ramming_table', 'id', 'ID'
EXEC add_column_desc 'furnace_ramming_table', 'ramming_name', '喷补料补炉位置'
EXEC add_column_desc 'furnace_ramming_table', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'furnace_ramming_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'furnace_status_table';
CREATE TABLE furnace_status_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  fur_status_name VARCHAR(32) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL
);

GO

EXEC add_table_desc 'furnace_status_table', '炉况情况表'
EXEC add_column_desc 'furnace_status_table', 'id', 'ID'
EXEC add_column_desc 'furnace_status_table', 'fur_status_name', '炉况'
EXEC add_column_desc 'furnace_status_table', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'furnace_status_table', 'create_time', '创建时间'

GO


EXEC p_drop_table 'addition_position_table';
CREATE TABLE addition_position_table
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  addition_position_name VARCHAR(32) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);

GO

EXEC add_table_desc 'addition_position_table', '添加剂位置表'
EXEC add_column_desc 'addition_position_table', 'id', 'ID'
EXEC add_column_desc 'addition_position_table', 'addition_position_name', '位置'
EXEC add_column_desc 'addition_position_table', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'addition_position_table', 'create_time', '创建时间'
EXEC add_column_desc 'addition_position_table', 'memo', '备注'

GO


EXEC p_drop_table 'print_file';
CREATE TABLE print_file
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  code VARCHAR(50) NOT NULL,
  explain VARCHAR(256) NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);
GO

EXEC add_table_desc 'print_file', '打印文档代码表'
EXEC add_column_desc 'print_file', 'id', 'ID'
EXEC add_column_desc 'print_file', 'code', '代码'
EXEC add_column_desc 'print_file', 'explain', '中文释义'
EXEC add_column_desc 'print_file', 'location', '模块'
EXEC add_column_desc 'print_file', 'enabled', '是否可用 1-可用，0-不可用'
EXEC add_column_desc 'print_file', 'create_time', '创建时间'
EXEC add_column_desc 'print_file', 'memo', '备注'
GO


EXEC p_drop_table 'product_type';
CREATE TABLE product_type
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  dep_id VARCHAR(512) NOT NULL,
  explain VARCHAR(32) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(32) NULL,
);

EXEC add_table_desc 'product_type', '产品类型表'
EXEC add_column_desc 'product_type', 'id', 'ID'
EXEC add_column_desc 'product_type', 'name', '产品名称'
EXEC add_column_desc 'product_type', 'dep_id', '部门'
EXEC add_column_desc 'product_type', 'explain', '中文释义'
EXEC add_column_desc 'product_type', 'enabled', '是否可用 1-可用, 0-不可用'
EXEC add_column_desc 'product_type', 'create_time', '创建时间'
EXEC add_column_desc 'product_type', 'memo', '备注'

GO


EXEC p_drop_table 'manufacturer';
CREATE TABLE manufacturer
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  product_type_id VARCHAR(512) NOT NULL,
  location VARCHAR(256) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  disabled_time DATETIME NULL,
  memo VARCHAR(32) NULL,
);

EXEC add_table_desc 'manufacturer', '供应商表'
EXEC add_column_desc 'manufacturer', 'id', 'ID'
EXEC add_column_desc 'manufacturer', 'name', '供应商名称'
EXEC add_column_desc 'manufacturer', 'product_type_id', '产品类型'
EXEC add_column_desc 'manufacturer', 'location', '模块'
EXEC add_column_desc 'manufacturer', 'enabled', '是否可用 1-可用, 0-不可用'
EXEC add_column_desc 'manufacturer', 'create_time', '创建时间'
EXEC add_column_desc 'manufacturer', 'disabled_time', '禁用时间'
EXEC add_column_desc 'manufacturer', 'memo', '备注'

GO


EXEC p_drop_table 'department';
CREATE TABLE department
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  dep_name VARCHAR(32) NULL,
  dep_key VARCHAR(32) NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(256) NULL
);

create unique index uk_department_dep_name on department(dep_name);
create unique index uk_department_dep_key on department(dep_key);

EXEC add_table_desc 'department', '部门表'
EXEC add_column_desc 'department', 'id', 'ID'
EXEC add_column_desc 'department', 'dep_name', '部门名称'
EXEC add_column_desc 'department', 'dep_key', '部门关键字'
EXEC add_column_desc 'department', 'enabled', '是否启用 1-启用, 0-禁用'
EXEC add_column_desc 'department', 'create_time', '创建时间'
EXEC add_column_desc 'department', 'memo', '备注'

GO


EXEC p_drop_table 'customer';
CREATE TABLE customer
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  customer_id VARCHAR(32) NOT NULL,
  customer_name VARCHAR(128) NOT NULL,
  train_code VARCHAR(32) NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);

create unique index uk_customer_id on customer(customer_id);
create unique index uk_customer_name on customer(customer_name);
GO

EXEC add_table_desc 'customer', '客户表'
EXEC add_column_desc 'customer', 'id', 'ID'
EXEC add_column_desc 'customer', 'customer_id', '客户代号'
EXEC add_column_desc 'customer', 'customer_name', '客户名称'
EXEC add_column_desc 'customer', 'train_code', '铁路代号'
EXEC add_column_desc 'customer', 'enabled', '是否启用 1-是, 0-否'
EXEC add_column_desc 'customer', 'create_time', '记录生成日期时间'
EXEC add_column_desc 'customer', 'memo', '备注'

GO


EXEC p_drop_table 'station';
CREATE TABLE station
(
  id INT IDENTITY NOT NULL PRIMARY KEY,
  station_name VARCHAR(128) NOT NULL,
  enabled INT DEFAULT 1 NOT NULL,
  create_time DATETIME NOT NULL,
  memo VARCHAR(50) NULL
);

create unique index uk_station_name on station(station_name);
GO

EXEC add_table_desc 'station', '岗位表'
EXEC add_column_desc 'station', 'id', 'ID'
EXEC add_column_desc 'station', 'station_name', '岗位名称'
EXEC add_column_desc 'station', 'enabled', '是否启用 1-是, 0-否'
EXEC add_column_desc 'station', 'create_time', '记录生成日期时间'
EXEC add_column_desc 'station', 'memo', '备注'

GO

TRUNCATE TABLE dictionary;
INSERT INTO dictionary (table_name, columns, memo) VALUES ('delay_code', '[]', '延误代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('heat_code', '[]', '热处理代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('hold_code', '[]', '保留代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('machine_code', '[]', '机床号表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('machining_code', '[]', '机加工代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('rework_code', '[{"prop":"single_wheel_type","label":"单轮查询分类","type":"select","options":[{"value":1,"label":"外观"},{"value":2,"label":"尺寸"},{"value":3,"label":"磁探"},{"value":4,"label":"超探"}]},{"prop":"rework_flag","label":"返工标记"}]', '返工代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('scrap_code', '[{"prop":"is_count","label":"是否需要统计","type":"select","options":[{"value":1,"label":"是"},{"value":0,"label":"否"}]},{"prop":"single_wheel_type","label":"单轮查询分类","type":"select","options":[{"value":1,"label":"外观"},{"value":2,"label":"尺寸"},{"value":3,"label":"磁探"},{"value":4,"label":"超探"}]}]', '废品代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('test_code', '[]', '实验代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('case_unpack_time_ctl', '[{"prop":"temp_min","validate":"digit"},{"prop":"temp_max","validate":"digit"},{"prop":"fminite","validate":"number"}]', '开箱时间表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('chemical_control', '[]', '化学成分控制表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('operating_time_ctr', '[{"prop":"operating_time","validate":"number"}]', '操作时间控制表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('hardness_control', '[{"prop":"hardness_min","validate":"number"},{"prop":"hardness_max","validate":"number"}]', '硬度值控制表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('bore_size', '[{"prop":"size_parameter","validate":"digit"}]', '轴孔尺寸表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('wheel_width_size', '[{"prop":"size_parameter","validate":"digit"}]', '轮辋尺寸表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('test_wheel', '[]', '质检试验轮记录表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('tape', '[{"prop":"tapesize","validate":"digit"}]', '带尺尺寸表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('cihen_code', '[]', '磁痕代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('pits', '[{"prop":"pit_no","validate":"number"}]', '桶号表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('furnace_patching_table', '[]', '喷补料补炉位置表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('furnace_ramming_table', '[]', '打结料补炉位置表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('furnace_status_table', '[]', '炉况情况表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('addition_position_table', '[]', '添加剂位置表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('scrap_reason_code', '[]', '废品分析原因代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('print_file', '[]', '打印文档代码表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('product_type', '[]', '产品类型表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('manufacturer', '[]', '供应商表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('department', '[]', '部门表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('customer', '[]', '客户表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('station', '[]', '岗位表');
INSERT INTO dictionary (table_name, columns, memo) VALUES ('design', '[{"prop":"steel_class","label":"轮型类型","type":"select","options":[{"value":"A","label":"A"},{"value":"B","label":"B"},{"value":"C","label":"C"},{"value":"其他","label":"其他"}]},{"prop":"balance_check","label":"是否做平衡检测","type":"select","options":[{"value":0,"label":"否"},{"value":1,"label":"是"}]},{"prop":"internal","label":"是否是国内轮型","type":"select","options":[{"value":0,"label":"否"},{"value":1,"label":"是"},{"value":2,"label":"未知"}]},{"prop":"weight","validate":"digit"}]', '轮型表');


DELETE FROM location;
INSERT INTO location (location_id, explain, create_time) VALUES ('design','轮型业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('graphite','石墨业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('graphitepour','石墨浇注', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('graphitescrap','石墨报废', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('core','下芯业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('pour','浇注业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('pit','开箱入桶出桶业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('heat','高温热处理业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('low','低温热处理业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('precheck','预检业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('lab','化验室业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('furnace','熔炼业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('finalcheck','终检业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('finalchecktest','终检开班检测业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('ultra','超探业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('ultratest','超探开班检测业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('magnetic','磁探业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('magnetictest','磁探开班检测业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('balance','平衡机业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('balancetest','平衡机开班检测业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('shottest','抛丸开班检测业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('material','原材料业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('inspectionsample','质检工长抽验业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('jmachine','基面加工业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('tmachine','踏面加工业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('kmachine','镗孔加工业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('qmachine','去重加工业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('wmachine','外辐板加工业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('machinesample','机加工长抽验业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('releaserecord','放行业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('inspectionrecord','质检操作记录业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('scraprecord','车轮报废和纠回业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('finishcorrect','成品纠回业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('stockcorrect','入库纠回业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('returncorrect','返厂纠回业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('xray','X光录入', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('xraycheck','X光检查', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('cihen','磁痕检测业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('chemistrydetail','化学成分超标查询业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('coldwheel','冷割车轮业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('performance','性能录入汇总业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('stock','入库车轮业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('batchprint','批量打印条码业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('scrapanalysis','废品分析业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('wheelinstock','CJ33车轮入库业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('samplewheel','抽检车轮录入业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('transport','X光发运/去重发运/镗孔发运业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('rawwheel','毛坯车轮录入业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('rawwheelprint','毛坯车轮打印业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('auditdetail','二维码校验业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('ship','车轮发运业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('printserial','打印串号业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('printfile','打印电子文档', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('certificate','打印合格证业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('helement','氢元素录入业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('contract','合同管理业务', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('servicemodify','数据修改业务', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('precheckpercent','预检一次通过率', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('finalcheckpercent','终检一次通过率', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('balancepercent','一次平衡率', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('singlewheel','单轮查询', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('compositequery','综合查询', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('datamodify','数据修改', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('techquality','技术质量查询', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('controlledrecord','受控记录导出', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('reworkpercent','返工代码一次通过率', '2021-08-08 08:00:00');

INSERT INTO location (location_id, explain, create_time) VALUES ('depnotify','部门通知', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('techdoc','技术文件', '2021-08-08 08:00:00');
INSERT INTO location (location_id, explain, create_time) VALUES ('mecrecord','机械性能检测', '2021-08-08 08:00:00');

SET IDENTITY_INSERT delay_code ON
TRUNCATE TABLE delay_code;
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('1', 'T1', '插座故障', 'Socket Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('2', 'T2', '开新包延时', 'Change New T-Pot', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('3', 'T3', '与底注包有关的故障', 'Bpl Issue', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('4', 'T4', '等试样', 'Waiting For Chemistry', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('5', 'T5', '茶壶包卡子故障', 'T-Pot Clamp', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('6', 'T6', '新砌茶壶包', 'New T-Pot', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('7', 'T7', '清理茶壶包', 'Clean T-Pot', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('8', 'T8', '茶壶包电机故障', 'T-Pot Motor Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('9', 'T9', '处理茶壶包罩子', 'T-Pot Cover', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('10', 'T10', '茶壶包穿', 'T-Pot Run-Out', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('11', 'G1', '烤石墨', 'Graphite Pre-Heat', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('12', 'G2', '吊石墨下线', 'Take Off Graphite', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('13', 'G3', '石墨跑火', 'Steel Run-Out From Graphite', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('14', 'G4', '调整石墨', 'Graphite Adjustment', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('15', 'M1', '1线转向机故障。', 'Line 1 Turntable', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('16', 'M2', '2线转向机故障。', 'Line 2 Turntable', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('17', 'M3', '3线转向机故障。', 'Line 3 Turntable', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('18', 'M4', '4线转向机故障。', 'Line 4 Turntable', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('19', 'M5', '更换模样', 'Pattern Change', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('20', 'M6', '清洗射砂筒', 'Magazine Cleaning', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('21', 'M7', '过废型', 'Scrap Moulds', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('22', 'M8', '1线翻箱机故障', 'Line 1 Flask Roll Over', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('23', 'M9', '2线翻箱机故障', 'Line 2 Flask Roll Over', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('24', 'M10', '3线翻箱机故障', 'Line 3 Flask Roll Over', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('25', 'M11', '4线翻箱机故障', 'Line 4 Flask Roll Over', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('26', 'M12', '1线辊道故障', 'Line 1 Conveyor', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('27', 'M13', '2线辊道故障', 'Line 2 Conveyor', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('28', 'M14', '3线辊道故障', 'Line 3 Conveyor', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('29', 'M15', '4线辊道故障', 'Line 4 Conveyor', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('30', 'M16', '送砂设备故障', 'Sand Blower Failure ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('31', 'M17', '上箱模样小吊故障', 'Cope Line Pattern Hoist', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('32', 'M18', '下箱模样小吊故障', 'Drag Line Pattern Hoist', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('33', 'M19', '造型1线遂道窑故障', 'Line 1 Graphite Pre-Heat ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('34', 'M20', '造型2线遂道窑故障', 'Line 2 Graphite Pre-Heat', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('35', 'M21', '造型3线遂道窑故障', 'Line 3 Graphite Pre-Heat', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('36', 'M22', '造型4线遂道窑故障', 'Line 4 Graphite Pre-Heat', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('37', 'M23', '1线射砂机射砂头更换隔模垫', 'L1 Blow Head Gasket Change', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('38', 'M24', '2线射砂机射砂头更换隔模垫', 'L2 Blow Head Gasket Change', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('39', 'M25', '3线射砂机射砂头更换隔模垫', 'L3 Blow Head Gasket Change', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('40', 'M26', '4线射砂机射砂头更换隔模垫', 'L4 Blow Head Gasket Change', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('41', 'M27', '1线射砂机故障', 'L1 Moulding Machine Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('42', 'M28', '2线射砂机故障', 'L2 Moulding Machine Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('43', 'M29', '3线射砂机故障', 'L3 Moulding Machine Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('44', 'M30', '4线射砂机故障', 'L4 Moulding Machine Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('45', 'M31', '合箱机故障', 'Cope On Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('46', 'M32', '1线终喷故障', 'L1 Final Spray', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('47', 'M33', '2线终喷故障', 'L2 Final Spray', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('48', 'M34', '3线终喷故障', 'L3 Final Spray', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('49', 'M35', '4线终喷故障', 'L4 Final Spray', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('50', 'M36', '1线混砂机故障', 'L1 Sand Mixer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('51', 'M37', '2线混砂机故障', 'L2 Sand Mixer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('52', 'M38', '3线混砂机故障', 'L3 Sand Mixer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('53', 'M39', '4线混砂机故障', 'L4 Sand Mixer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('54', 'M40', '造型线空压故障', 'Mould Line Air Supply', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'U');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('55', 'M41', '预喷故障', 'Pre-Spray', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('56', 'M42', '开箱故障', 'Cope Off Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('57', 'M43', '雨淋芯掉芯', 'Gate Core Dropped Out', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('58', 'M44', '水玻璃问题', 'Sodium Silicate Issue', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('59', 'M45', '补刷喷枪堵', 'Additional Coating Spray Gun Problem', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('60', 'M46', '八区辊道故障', 'Cope-Off Conveyor ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('61', 'M47', '下中芯轨道故障', 'Center Core Setting Conveyor ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('62', 'M48', '调整射砂机', 'Mold Blower Adjustment ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('63', 'M49', '涂料泵故障', 'Coating Pump ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'U');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('64', 'M50', '砂型不足', 'Insufficient Molds', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('65', 'M51', '开箱撞冒口处故障', 'Bob-Knocking Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('66', 'M52', '涂料管道故障', 'Coating Pipe ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('67', 'M53', '上箱砂罐故障', 'Cope Line Sand Hopper ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('68', 'M54', '开箱取轮机故障', 'Wheel-Pick Up Machine', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('69', 'M55', '合箱机前转向机故障', 'Turntable  Before Mold Closing ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('70', 'M56', '推轮机故障', 'Wheel Pusher', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('71', 'M57', '送砂除尘系统故障', 'Fine Sand Extractio', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('72', 'M58', '金型故障', 'Pattern Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('73', 'M59', '造型线PLC故障', 'Molding Plc ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('74', 'M60', '造型线通信故障', 'Molding Line Tele-Communicatio', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('75', 'M61', '合箱机前辊道故障', 'conveyor  before mold closing ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('76', 'M62', '制芯叉车故障', 'core making forklift truck', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('77', 'M63', '造型天车故障', '20T Crane Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('78', 'M64', '开箱翻轮机故障', 'cope-off rollover ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('79', 'M65', '开箱转向机故障', 'cope-off turntable ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('80', 'M66', '造型线液压站故障', 'Molding hydraulic station failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('81', 'P1', '测温枪故障', 'Pouring Temp Gun Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('82', 'P2', '驱动梁故障', 'Walking Beam Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('83', 'P3', '按工艺要求对设备进行定检（如调夹紧站等）', 'Planned Maintenance Pouring', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('84', 'P4', '半门吊故障', '#1 Semi-Gantry Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('85', 'P5', '浇注站辊道故障', 'Conveyor Fail', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('86', 'P6', '吊浇注坑钢渣', 'Clean Slag From Pit', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('87', 'P7', '生产延误', 'Production Loss', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('88', 'P8', '浇注站转向机故障', 'Turntable Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('89', 'P9', '清理驱动梁废钢', 'Clean Slag On Driving Beam', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('90', 'P10', '浇注站摄像机故障', 'Pouring Station Camera', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('91', 'P11', '浇注站石墨跑火', 'Steel Splash From Graphite', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('92', 'P12', '浇注站石墨型骑上辊道', 'Graphite Shift On Conveyor', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('93', 'P13', '浇注站送样机故障', 'Pouring Station Sample Machine', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('94', 'P14', '浇注站翻转机故障', 'Pouring Station Flask Roll Over', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('95', 'P15', '底注包塞杆断', 'BPL Stop Rod Breakage', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('96', 'P16', '浇注站液压系统故障', 'Pouring Station Hydraulic Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('97', 'P17', '高温等待', 'High Temperature Waiting', 'pour,furnace', '1', '2021-10-11 08:51:45.000', NULL);
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('98', 'E1', '料罐未拉开。', 'Scrap Bucket', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('99', 'E2', '等炉前放钢', 'Waiting For Tapping', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('100', 'E3', '过跨电平车故障', 'Cross-Bay Cable Car ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('101', 'E4', '高温停炉', 'High Temperature Shutdow', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('102', 'E5', '处理/清理电炉炉门，炉底，炉坡，出钢眼', 'Furnace Lining', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('103', 'E6', '处理炉内钢水化学成份延时', 'Melt Chemistry', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('104', 'E7', '40T天车故障', '40T Crane Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('105', 'E8', '钢水温度高镇静', 'Steel Temp Too High', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('106', 'E9', '除尘器故障', 'Fume Extract', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('107', 'E10', '接电极影响', 'Electrodes', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('108', 'E11', '1号电炉变压器故障', '#1 Transformer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('109', 'E12', '2号电炉变压器故障', '#2 Transformer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('110', 'E13', '3号电炉变压器故障', '#3 Transformer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('111', 'E14', '4号电炉变压器故障', '#4 Transformer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('112', 'E15', '1号电炉故障', '#1 Eaf Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('113', 'E16', '2号电炉故障', '#2 Eaf Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('114', 'E17', '3号电炉故障', '#3 Eaf Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('115', 'E18', '4号电炉故障', '#4 Eaf Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('116', 'E19', '试样不合格，重新取样', 'Test Sample No Good', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('117', 'E20', '除尘差，影响视线', 'Fumes And Dust ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('118', 'E21', '试验室取样设备故障/风动取样机故障', 'Lampson Tube Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('119', 'E22', '20t天车故障', '20T Scrap Crane Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('120', 'E23', '电炉炉壁漏水', 'Furnace Body Cooling Water Leakage  ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('121', 'E24', '与炉墙相关的延误', 'Lining Issue', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('122', 'E25', '废钢电子称故障', 'Scrap Scale Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('123', 'E26', '电炉液压站故障', 'Eaf Hydraulic Unit ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('124', 'E27', '冷炉延时', 'Cold Furnace Cycle Time Increase ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('125', 'E28', '喷补机故障', 'Patching Anit Dow', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('126', 'E29', '测温枪故障', 'Temperature Gun Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('127', 'E30', '处理钢坨子', 'Slag Handling', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('128', 'E31', '电炉跨高压故障', 'High voltage across the furnace failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('129', 'E32', '50T天车故障', '50T Crane Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('130', 'E33', '电炉试样切割机故障', 'EAF smple burner failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('131', 'E34', '化验室设备故障', 'LAB Equipment Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('132', 'S1', '清孔机故障', 'Reamer Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('133', 'S2', '等缓冷桶', 'Short Of Pits ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('134', 'H1', '缓冷桶处天车故障', 'Bridge Crane Soak Pit', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('135', 'H2', '回火炉故障', 'Tempering Furnace Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('136', 'H3', '热轮抛丸机故障', 'Hot Wheel Cleaner ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('137', 'H4', '环型炉故障', 'Rotary Furnace ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('138', 'H5', '热处理辊道故障', 'Ht Conveyor', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('139', 'H6', '切割机故障', 'Bob Burner', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('140', 'H7', '热处理返修', 'Heat Treatment Refurb', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('141', 'A1', 'AC-1大链转向机故障', 'Ac1 Turntable Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('142', 'A2', 'AC-2大链转向机故障', 'Ac2 Turntable Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('143', 'A3', 'AC-3大链转向机故障', 'Ac3 Turntable Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('144', 'A4', 'AC-1大链故障', 'Ac1 Chain Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('145', 'A5', 'AC-2大链故障', 'Ac2 Chain Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('146', 'A6', 'AC-3大链故障', 'Ac3 Chain Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('147', 'A7', 'A7', 'A7', 'pour,furnace', '1', '2021-10-11 08:51:45.000', NULL);
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('148', 'O1', '检修高压', 'High Voltge Pm', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('149', 'O2', '多种轮型影响。', 'Delay Caused By Multiple Wheel Operatio', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'A');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('150', 'O3', '工艺试验', 'Process Test', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'P');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('151', 'O4', '6＃变压器故障', '#6 Tranformer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('152', 'O5', '1＃变压器故障', '#1 Tranformer', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('153', 'O6', '两台炉生产', '2 EAF''s Running', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('154', 'O7', '动能影响', 'Utility ', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'U');
INSERT INTO delay_code (id, code, explain, explain_en, location, enabled, create_time, memo) VALUES ('155', 'E35', '备料天车故障', 'Mat Crane Failure', 'pour,furnace', '1', '2021-10-11 08:51:45.000', 'E');
SET IDENTITY_INSERT delay_code OFF


SET IDENTITY_INSERT heat_code ON
TRUNCATE TABLE heat_code;
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('1', 'ARHT', '第一次热处理保留', 'Hold', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('2', 'BBOB', '第二次热处理保留', 'Hold', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('3', 'HBD', '硬度值低于下限保留', 'Hold', 'finalcheck', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('4', 'HBD1', '硬度值低于下限放行', 'Release', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('5', 'HBG', '第一次硬度值高于上限保留', 'Hold', 'finalcheck', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('6', 'HBG1', '第一次硬度值高于上限放行', 'Release', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('7', 'HBG2', '第二次硬度值高于上限放行', 'Release', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('8', 'HBGG', '第二次硬度值高于上限保留', 'Hold', 'finalcheck', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('9', 'NRHT', '第二次热处理放行', 'Release', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('10', 'OBOB', '第三次热处理放行', 'Release', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
INSERT INTO heat_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('11', 'SC', '第三次热处理不合格', 'Hold', 'heat,low', '1', '2021-10-08 10:59:18.000', '2016-03 2016-5-27');
SET IDENTITY_INSERT heat_code OFF


SET IDENTITY_INSERT hold_code ON
TRUNCATE TABLE hold_code;
INSERT INTO hold_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('1', 'Q1', '去重扣留码', 'Hold', 'balance,balancetest,correctwheel', '1', '2021-10-08 11:08:34.000', '2002.11.20 2016-03 2016-5-27');
INSERT INTO hold_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('2', 'Q2', '二次去重扣留码', 'Hold', 'balance,balancetest,correctwheel', '1', '2021-10-08 11:08:34.000', '2004.6.16 2016-03 2016-5-27');
INSERT INTO hold_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('3', 'Q3', '220gm以上的去重', 'Hold', 'balance,balancetest,correctwheel', '1', '2021-10-08 11:08:34.000', '2005.8.16 2016-03 2016-5-27');
INSERT INTO hold_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('4', 'Q4', '去重', 'Hold', 'balance,balancetest,correctwheel', '1', '2021-10-08 11:08:34.000', '2007-9-4  07-15 2016-03 2016-5-27');
SET IDENTITY_INSERT hold_code OFF


SET IDENTITY_INSERT machine_code ON
TRUNCATE TABLE machine_code;
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('1', '00', 'jmachine', '1', '2021-10-08 11:21:35.000', 'J');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('2', '01', 'jmachine', '1', '2021-10-08 11:21:35.000', 'J');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('3', '02', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('4', '03', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('5', '04', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('6', '05', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('7', '06', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('8', '07', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('9', '08', 'kmachine', '1', '2021-10-08 11:21:35.000', 'K');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('10', '09', 'kmachine', '1', '2021-10-08 11:21:35.000', 'K');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('11', '10', 'kmachine', '1', '2021-10-08 11:21:35.000', 'K');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('12', '11', 'jmachine', '1', '2021-10-08 11:21:35.000', 'J');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('13', '12', 'jmachine', '1', '2021-10-08 11:21:35.000', 'J');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('14', '13', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('15', '14', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('16', '15', 'tmachine,wmachine', '1', '2021-10-08 11:21:35.000', 'T,W');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('17', '16', 'kmachine', '1', '2021-10-08 11:21:35.000', 'K');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('18', '17', '17', '1', '2021-10-08 11:21:35.000', NULL);
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('19', '18', '18', '1', '2021-10-08 11:21:35.000', NULL);
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('20', '19', '19', '1', '2021-10-08 11:21:35.000', NULL);
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('21', '20', '20', '1', '2021-10-08 11:21:35.000', NULL);
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('22', '21', 'qmachine', '1', '2021-10-08 11:21:35.000', 'Q');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('23', '22', 'qmachine', '0', '2021-10-08 11:21:35.000', 'Q');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('24', '23', 'qmachine,kmachine', '1', '2021-10-08 11:21:35.000', 'Q,T');
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('25', '24', '24', '1', '2021-10-08 11:21:35.000', NULL);
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('26', '25', '25', '1', '2021-10-08 11:21:35.000', NULL);
INSERT INTO machine_code (id, machine_id, location, enabled, create_time, memo) VALUES ('27', '26', '26', '1', '2021-10-08 11:21:35.000', NULL);
SET IDENTITY_INSERT machine_code OFF


TRUNCATE TABLE machining_code;
SET IDENTITY_INSERT machining_code ON
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(1 , 'J', 'S2', '69', '第一次加工的车轮（毛轮）', 'jmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(2 , 'J', 'S2', '691', '第二次加工湖东车轮', 'jmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(3 , 'J', 'S2', '45', '返修车轮', 'jmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(4 , 'J', 'S2', '6', '第二次加工的车轮', 'jmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(5 , 'J', 'S2', '9', '基圆有飞边的车轮', 'jmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(6 , 'T', 'S2', '138', '第一次加工的车轮（毛轮）', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(7 , 'T', 'S2', '51', '改带尺', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(8 , 'T', 'S2', '52', '修轮毂外侧面', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(9 , 'T', 'S2', '53', '修轮辋外侧面', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(10, 'T', 'S2', '54', '既改带尺又修平面', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(11, 'T', 'S2', '55', '设备原因返修外轮毂面或外轮辋面或踏面', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(12, 'T', 'S2', '56', '操作原因返修外轮毂面或外轮辋面或踏面', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(13, 'T', 'S2', '8', '高冒口车轮', 'tmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(14, 'WF', 'S2', '70', '第一次加工的车轮（毛轮）', 'wmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(15, 'WF', 'S2', '701', '外幅板返修', 'wmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(16, 'WF', 'S2', '0', '', 'wmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(17, 'K', 'S2', '40', '第一次加工轴孔（粗加工）', 'kmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(18, 'K', 'S2', '44', '精加工轴孔', 'kmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(19, 'K', 'S2', '43', '加工偏孔轮', 'kmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(20, 'K', 'S2', '46', '第二次大孔轮', 'kmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(21, 'K', 'S2', '30', '加蓬磨耗槽加工', 'kmachine', '2021-11-24 08:00:00');
INSERT INTO machining_code (id, [procedure], [parameter], machining_code, explain, location, create_time) VALUES(22, 'WF', 'S2', '47', '幅板返修', 'wmachine', '2021-11-24 08:00:00');
SET IDENTITY_INSERT machining_code OFF


SET IDENTITY_INSERT rework_code ON
TRUNCATE TABLE rework_code;INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (1, '12A', '辐板外侧面气孔待返打磨', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (2, '12D', '辐板内侧面气孔待返打磨', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (3, '23', '轮缘/踏面孔洞待返加工', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (4, '2A', '铸造面(黑皮)', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (5, '2R', '先加工后打磨', '1', NULL, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', NULL, '');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (6, '2T', '改带尺', '1', 2, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '尺寸', 'F-JT');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (7, '3E', '内侧面辐板幅条状凸起', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (8, '3R', '外侧面辐板幅条状凸起', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (9, '44A', '火焰烧伤待返加工', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JK');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (10, '4E', '辐板内侧面粘砂', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (11, '4EH', '字号粘砂', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (12, '4R', '辐板内侧面粘砂', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (13, '58', '轮辋偏差错箱', '1', NULL, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 0, '1900-01-19 11:27:13.000', NULL, '');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (14, '59', '轮辋偏差(机加工)待返加工', '1', 2, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '尺寸', 'F-JT');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (15, '5E', '辐板内侧面砂眼', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (16, '5R', '辐板外侧面砂眼', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (17, '65', '冷机械损伤（磕碰伤）', '1', NULL, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', NULL, 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (18, '67C', '轴孔环形裂纹待返加工', '1', 3, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '磁探', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (19, '67F', '轮辋内侧面裂纹待返加工', '1', 3, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '磁探', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (20, '67H', '轮毂轴向裂纹待返加工', '1', 3, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '磁探', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (21, '67R', '轮辋外侧面裂纹待返加工', '1', 3, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '磁探', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (22, '68', '踏面裂纹待返加工', '1', 3, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '磁探', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (23, '6E', '辐板内侧面水纹', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (24, '6R', '辐板外侧面水纹', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (25, '7R', '车轮重新打磨', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'D');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (26, '88', '轮辋内侧面孔洞待返加工', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (27, '8C', '超探报废待二次复查', '1', 4, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '超探', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (28, '8E', '内侧辐板磁痕数量为1-5处', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (29, '8E1', '内侧辐板磁痕数量为6-10处', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 0, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (30, '8E2', '内侧辐板磁痕数量为10处以上', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 0, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (31, '8ER', '内外侧均有磁痕，并且内外侧辐板磁痕数量均为1-5处', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (32, '8ER1', '内外侧均有磁痕，并且至少有一单侧辐板磁痕数量为6-10处', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 0, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (33, '8ER2', '内外侧均有磁痕，并且至少有一单侧辐板磁痕数量为10处以上', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 0, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (34, '8R', '外侧辐板磁痕数量为1-5处', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (35, '8R1', '外侧辐板磁痕数量为6-10处', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 0, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (36, '8R2', '外侧辐板磁痕数量为10处以上', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 0, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (37, '9A', '轮毂杂物待返加工', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (38, '9C', '轮辋外侧面杂物待返加工', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (39, '9D', '轴孔加工表面夹砂（夹杂）', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-ZZ');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (40, '9R', '抛丸', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', '');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (41, 'CT1', '8C分析(加工面粗糙)', '1', 4, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '超探', 'F-JT');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (42, 'H1', '返加工轮毂外侧面(加工原因)', 'JT', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JT-T');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (43, 'H2', '返加工轮辋外侧面(加工原因)', 'JT', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JT-T');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (44, 'H3', '返加工踏面(加工原因)', 'JT', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JT-T');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (45, 'H4', '返加工轮辋内侧面(加工原因)', 'JJ', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JJ-T');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (46, 'H5', '返加工轮毂内侧面(加工原因)', 'JJ', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JJ-T');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (47, 'H6', '返加工轴孔(加工原因)', 'JK', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JK-T');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (48, 'H7', '待加工大孔', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JK');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (49, 'H8', '踏面加工后未镗孔', '1', 1, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '外观', 'F-JK');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (50, 'R6', '倒角不合格返修', '1', 2, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '尺寸', 'F-JT');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (51, 'TR', '(TIR)超差', 'JT', 2, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '尺寸', 'F-JT-T');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (52, 'TW', '带尺不一致', '1', 2, 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetic,balance,balancetest,inspectionrecord,releaserecord,finishcorrect,stockcorrect,returncorrect', 1, '1900-01-19 11:27:13.000', '尺寸', 'F-JT');
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (53, 'G0', '小止口', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (54, 'G1', '改静平衡', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (55, 'G10', '踏面损伤', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (56, 'G11', '轮辋面损伤', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (57, 'G12', '25t', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (58, 'G13', '8改6字头', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (59, 'G14', '新6字头', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (60, 'G15', '减重修改', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (61, 'G16', '挂砂槽损伤', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (62, 'G17', '25T修改减重', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (63, 'G18', 'CA--30', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (64, 'G19', '8字头石墨改CJ33', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (65, 'G2', '轮缘损伤', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (66, 'G20', '6字头石墨改CJ33', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (67, 'G21', '新制CJ33', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (68, 'G22', '改减重', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (69, 'G23', '新制IR33', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (70, 'G24', '6字头石墨改IR33', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (71, 'G26', '浇轮67R多', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (72, 'G27', '震轮多', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (73, 'G28', 'HDZD改輻板', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (74, 'G29', '穿钢', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (75, 'G3', '止口损伤', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (76, 'G30', '倒浇石墨', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (77, 'G31', 'C轮俢改HDZD', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (78, 'G32', 'B轮俢改HDZD', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (79, 'G33', '新制HDZD', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (80, 'G34', '改冒口型', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (81, 'G35', '3０T轴重', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (82, 'G36', '改轮辋面', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (83, 'G37', '改Cp33', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (84, 'G38', 'IR33改南非', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (85, 'G39', '改輻板', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (86, 'G4', '改止口', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (87, 'G40', '新 制南非', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (88, 'G41', '改 18排气孔', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (89, 'G42', '蒙古轮', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (90, 'G43', '底面损伤', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (91, 'G44', ' 改 轮缘', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (92, 'G45', '粘接石墨', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (93, 'G46', 'CG33新 制', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (94, 'G47', 'CJ33改CG33', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (95, 'G48', '新 制BJST', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (96, 'G49', '蒙古轮改BJST', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (97, 'G5', '新制静平衡', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (98, 'G51', '改踏面', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (99, 'G52', '尺寸检查不合格', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (100, 'G53', '尺寸检查合格', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (101, 'G54', '改型加工', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (102, 'G55', '新制', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (103, 'G56', '止口磨损加工', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (104, 'G57', '正常磨损加工', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (105, 'G58', '缺陷修理加工', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (106, 'G59', '补焊缺陷', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (107, 'G6', '氧化', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (108, 'G8', '减重', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
INSERT INTO rework_code (id, code, explain, code_type, single_wheel_type, location, enabled, create_time, memo, rework_flag) VALUES (109, 'G9', '减重静平衡', '1', NULL, 'graphite', 1, '1900-01-19 11:27:13.000', NULL, NULL);
SET IDENTITY_INSERT rework_code OFF


SET IDENTITY_INSERT scrap_code ON
TRUNCATE TABLE scrap_code;
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('1', '1', '坏上箱', '报废', '1', '0', 'pour,pit', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('2', '100', '上箱穿钢', '报废', '1', '0', 'pour,pit', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('3', '110', '铁包砂', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('4', '12AS', '辐板外侧面气孔', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('5', '12DS', '辐板内侧面气孔', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('6', '2', '坏下箱', '报废', '1', '0', 'pour,pit', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('7', '23S', '轮缘/踏面孔洞', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('8', '25', '跑火', '浇铸', '1', '0', 'pour,pit', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('9', '2AS', '铸造面（黒皮）', '废模', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('10', '3', '雨淋芯散落', '报废', '1', '0', 'pour,pit', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('11', '30', '空心轮辋(孔)', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('12', '32', '轮毂撕裂', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('13', '4', '雨淋芯裂纹', '报废', '1', '0', 'pour,pit', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('14', '41', '芯子破碎', '浇铸', '1', '0', 'pour,pit', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('15', '42', '浮芯粘连', '浇铸', '1', '0', 'pour,pit', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('16', '44', '空心轮毂', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('17', '44AS', '火焰烧伤', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('18', '45', '水纹(辐板)', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('19', '5', '所有其他原因', '报废', '1', '0', 'pour,pit', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('20', '50', '喷涂不匀', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('21', '55', '浇注不足', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('22', '56A', '环状缺陷', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('23', '56C', '上箱掉砂', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('24', '56D', '下箱掉砂', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('25', '58S', '轮辋偏差-铸型移动', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('26', '59S', '轮辋偏差(机加工)', '报废', '2', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('27', '6', '公司试验', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('28', '60', '硬度不合格', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('29', '65', '热处理不合格及机械损伤', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('30', '65A', '热机械损伤', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('31', '65B', '冷机械损伤', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('32', '66', '机加工废品', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('33', '66A', '轮辋厚度差超标', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', 'GY-BJ-017-17 2017-3-6');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('34', '66B', '轮毂壁厚差超标', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', 'GY-BJ-017-17 2017-3-6');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('35', '66C', '由于加工原因引起的其它废品', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', 'GY-BJ-017-17 2017-3-6');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('36', '66W', '外幅板加工废', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('37', '67CS', '轴孔环状裂纹', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('38', '67FS', '轮辋内侧面裂纹', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('39', '67HS', '轮毂轴向裂纹', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('40', '67P', '辐板裂纹', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('41', '67RS', '轮辋外侧面裂纹', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('42', '68S', '踏面裂纹', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('43', '69', '机械故障(中断浇铸、辊道移动)', '浇铸', '1', '0', 'pit,inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('44', '7', 'X-光废', '废模', '1', '0', 'inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('45', '70', '浇注温度不够', '废模', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('46', '77', '成分出格', '废模', '1', '0', 'inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('47', '8', '提前出缓冷桶', '废模', '1', '0', 'inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('48', '88S', '轮辋内侧孔面洞', '废模', '4', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('49', '8B', '磁痕报废', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '0', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('50', '8BE', '内侧辐板磁痕', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('51', '8BR', '外侧辐板磁痕', '报废', '3', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('52', '8CS', '超探报废', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('53', '8H', '超探轮毂', '报废', '4', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('54', '90', '辐板薄', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('55', '91', '冒口粘连', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('56', '9AS', '轮毂杂物', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('57', '9B', '辐板杂物', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('58', '9CS', '轮辋外侧面杂物', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('59', '9DS', '轴孔加工表面夹砂（夹杂）', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2020-12-17');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('60', '9S', '结瘤', '报废', '1', '0', 'precheck,finalcheck,finalchecktest,ultra,ultratest,magnetic,magnetictest,balance,balancetest,rawwheel,rawwheelprint,inspectionrecord,scraprecord,correctwheel', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('61', 'QS', '去重废品', '报废', '2', '0', 'inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('62', 'R1', '预检前(失踪)', '其它', '1', '0', 'inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('63', 'R2', '预检后(失踪)', '其它', '1', '0', 'inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2016-03 2016-5-27');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('64', 'Z', '考核统计使用（null）', '其它', '1', '0', 'other', '0', '2021-10-09 15:11:49.000', '2020-3-13');
INSERT INTO scrap_code (id, code, explain, code_type, single_wheel_type, is_count, location, enabled, create_time, memo) VALUES ('65', 'SCS', '第三次热处理不合格报废', '报废', '1', '0', 'inspectionrecord,scraprecord', '1', '2021-10-09 15:11:49.000', '2021-10-9');
SET IDENTITY_INSERT scrap_code OFF


SET IDENTITY_INSERT test_code ON
TRUNCATE TABLE test_code;
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('258', 'C107', '3/3/05 刘斌通知', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', ' 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('259', 'C108', '试验南非轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-15   2005-5-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('260', 'C109', '试验南非轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-16   2005-6-6 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('261', 'C113', '降低67R试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-26   2005-7-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('262', 'C114', '局部加厚HDZC砂衬', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-36  2005--8-25 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('263', 'C115', '倒浇HDZC840', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-43 2005-10-10 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('264', 'C117', '毛坯字母铸反', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-33 2005-11-25 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('265', 'C118', 'CO2树脂试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-47 2005-11-30 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('266', 'C119', '贝氏体车轮', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '06-01 2006-1-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('267', 'C120', '薄轮缘车轮 朱彪', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2006-2-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('268', 'C121', '试验印度轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2006-6-2 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('269', 'C122', '试验新钢轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2006-6-9 05-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('270', 'C123', '刘满军', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '06-06-17 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('271', 'C124', '试验HEZD840车轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '06-07-13  06-23 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('272', 'C125', '辅加挠场试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '06-8-4  06-30 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('273', 'C126', '大秦试验车轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '07-001  2007-1-3 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('274', 'C127', '扣留2/1/07的车轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '07-03  2007-2-7 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('275', 'C128', '试验C级轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2007-2-12 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('276', 'C130', '试验HDZD840车轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2007-7-30  07-20 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('277', 'C131', '孙延东', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '07-9-12  07-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('278', 'C132', '刘斌', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-1-24  07-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('279', 'C133', '孙延东', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-3-2  08-03 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('280', 'C136', '刘斌', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-8-6  扣轮 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('281', 'C139', '刘斌', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-8-13  扣轮 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('282', 'C141', '扣留8/17/2008_1_1222_1', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2008-8-18 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('283', 'C145', '30T轴重试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-2-20 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('284', 'C146', '扣留1/2/2009_4_1', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-1-3 邱风斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('285', 'C147', '扣留1/2/2009_4_57_1', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-1-9 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('286', 'C148', '扣留6/5/2009_1_580_3  4', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-6-15 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('287', 'C149', '扣留6/22/2009_2_677', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-6-22 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('288', 'C150', '扣留6/22/2009_1_654', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-6-22 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('289', 'C151', '多孔雨林芯', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-6-25 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('290', 'C152', '哈尔滨薄轮缘', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-7-14 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('291', 'C153', '扣留7/20/2009_1_731', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-7-22 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('292', 'C154', '扣留8/21/2009_3_896', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-8-24 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('293', 'C156', '扣留9/10/2009_3_1002', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-9-15 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('294', 'C157', '扣留9/13/2009_4_997', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-9-16 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('295', 'C158', '扣留济南返回车轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-9-26 高志 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('296', 'C159', '扣留12/11/2009_4_57_1', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-12-12 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('297', 'C160', '除渣剂试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-3-26 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('298', 'C161', '扣留4/27/2010_4_524_8', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-4-29 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('299', 'C162', '扣留5/26/2010_3_680_16', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-5-28 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('300', 'C163', '孙延东', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '10-09-01 2010-17 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('301', 'C164', '扣留9/6/2010_3_1186', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-9-10 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('302', 'C165', '孙延东', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '11-18-01 2010-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('303', 'C166', '孙延东', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '11-18-01 2010-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('304', 'C167', '雨淋芯子试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-11-25 2010-29 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('305', 'C169', '孙延东', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '12/16/2010 2010-32 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('306', 'C170', '孙延东', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '1/10/2011 2011-01 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('307', 'C171', '雨淋芯子试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-1-17 2011-03 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('308', 'C172', '30T轴重', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-2-9 2011-07 刘彦磊 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('309', 'C174', '4-264-L4铝偏低', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-2-28 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('310', 'C175', '孙延东', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '1/15/2011 2011-13 2019-9-10 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('311', 'C179', '增加沙衬厚度', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-6-22 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('312', 'C182', '修理模样', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-12-26孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('313', 'C183', '蒙古车轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2012-4-12孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('314', 'C186', '跟踪南非车轮', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2012-4-12孙延东? 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('315', 'C188', '扣留6/25/2013_4_642', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2013-7-3 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('316', 'C190', '扣留8/26/2013_4_847', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2013-8-29 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('317', 'C191', '扣留9/26/2013', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2013-9-26 张志 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('318', 'C193', '扣留11/20/2013_2_1511', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2013-11-20 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('319', 'C194', '扣留1/14/2014_3_46', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-1-18 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('320', 'C197', '扣留5/25/2014_1_588', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'GY-BJ-32-2014 2014-6-9 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('321', 'C198', '扣留终喷涂料试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'GY-BJ-61-2014 2014-10-29 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('322', 'C2', '轴孔偏', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-4-16 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('323', 'C201', '8C扣', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-12-10 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('324', 'C202', 'PT试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-12-13 聂继英 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('325', 'C203', '12/14/14浇注', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '12/22/14 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('326', 'C205', '树脂砂造型试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2015-6-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('327', 'C206', 'CJ33磁痕试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2/6/2015 2015-05 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('328', 'C208', 'CJ33树脂试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2015-22 2015-8-26 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('329', 'C211', '该炉8C废品较多', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-012-2020 2020-1-13');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('330', 'C212', '840HEZD-C级钢试验', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2015-34  2015-12-22 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('331', 'C214', '840HDZD-C级钢试验', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2016-01 2016-1-22 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('332', 'C219', '扣留2016-6-13-4-485', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-47-2016 2016-6-17');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('333', 'C224', '钢水过滤网实验', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2017-16 2017-7-10 刘斌 2017-16 2017-7-10');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('334', 'C227', '车轮扣留', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-004-18 2018-1-8 2018-1-8');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('335', 'C228', 'E轮字号位置更改', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-05 2018-05 2018-2-5');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('336', 'C230', '8CS扣留', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-055-2018 2018-3-22');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('337', 'C232', '扣留18-7-22-4-917', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-160-2018 2018-7-23');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('338', 'C233', '浇注C级钢HDZD840', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-06 2018-8-24');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('339', 'C234', '降低轮毂加工量', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-40 2018-10-12');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('340', 'C235', '控制C级钢硬度试验', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-43 2018-10-29');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('341', 'C236', 'HBG车轮重新热处理', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-008-19 2019-1-15');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('342', 'C239', '暂扣20200428-1016镗床加工车轮', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-074-20 2020-4-29');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('343', 'C240', '暂扣20200429-23床加工车轮', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-075-20 2020-4-30');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('344', 'C241', '暂扣20210108-1床加工车轮', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-001-21 2021-1-9');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('345', 'C242', 'C242', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-001-21 2021-3-6');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('346', 'C28', '工艺控制试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'Process Control Test 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('347', 'C30', '热处理过调车轮', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-179-2018 2018-8-15');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('348', 'C70', '局部更改25T轴重', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '3/6/03  试03-09 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('349', 'C72', '降低冒口砂衬厚度', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2003.4.15试 03-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('350', 'C74', '生产CH-36 CJ36', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '6/23/03 试 03-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('351', 'C80', '试HEZC840', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2003.10.16 试03-28 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('352', 'C83', 'HEZC840每包后二个', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2003.10.18 工02-28 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('353', 'C84', '增冒口砂衬厚度', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2003.11.26 试03-37 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('354', 'C90', '840HDZC试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-21  2004-4-28 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('355', 'C92', 'CG-30试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-29  2004-5-26 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('356', 'C96', 'IR-33试验', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-55  2004-9-24 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('357', 'C98', '降低浇注温度', 'Hold', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-64  2004-11-16 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('358', 'CXD', 'CJ33轮临时扣留', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '07-6-10 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('359', 'P0', '大飞边车轮', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-042-18 2018-3-15 2018-3');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('360', 'P100', '降低浇注温度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-73  2004-12-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('361', 'P101', '整体塞棒试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-74  2004-12-21 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('362', 'P102', '生产印度轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-34  2004-12-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('363', 'P103', '降低底注包烘烤温度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-02  2005-1-5 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('364', 'P104', '冷对芯杆袖砖试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-1  2005-1-14 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('365', 'P105', '整体塞棒投入批量使用', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-05  2005-1-25 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('366', 'P106', '试验金属模样', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-06  2005-3-1 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('367', 'P110', '取消粉煤灰试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-19 2005-6-20 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('368', 'P111', '取消粉煤灰水玻璃试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-20 2005-6-21 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('369', 'P113', '降低67R试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-26   2005-7-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('370', 'P114', '局部加厚HDZC砂衬', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-36  2005--8-25 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('371', 'P115', '倒浇HDZC840', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-43 2005-10-10 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('372', 'P116', '打硬度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-46 2005-11-25 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('373', 'P117', '毛坯字母铸反', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-33 2005-11-25 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('374', 'P118', 'CO2树脂试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '05-47 2005-11-30 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('375', 'P12', '工艺控制试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'Process Control Test 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('376', 'P120', '薄轮缘车轮 朱彪', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2006-2-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('377', 'P122', '试验新钢轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2006-6-9 05-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('378', 'P123', '刘满军 时兴东', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '06-06-17 08-1-22 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('379', 'P124', '试验HEZD840车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '06-07-24  06-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('380', 'P125', '辅加挠场试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '06-8-4 06-30 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('381', 'P126', '大秦试验车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-04  2008-2-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('382', 'P127', '释放2/1/07的车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '07-03-06 07-05 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('383', 'P129', '浇注42个车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2007-7-19  07-18 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('384', 'P130', '试验HDZD840车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '07-22 2007-8-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('385', 'P131', '孙延东', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '07-9-12  07-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('386', 'P132', '刘斌', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-1-24  07-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('387', 'P133', '孙延东', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-3-2  08-03 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('388', 'P134', 'P134', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-04 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('389', 'P135', '制芯试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-09 08-8-3 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('390', 'P136', '刘斌', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-8-6 扣轮放行 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('391', 'P137', '制芯涂料试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-10 08-8-8 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('392', 'P138', '中芯存放试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-11 08-8-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('393', 'P139', '刘斌', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-8-13 扣轮放行 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('394', 'P140', '济南返厂', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2008-8-13  高志 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('395', 'P142', '梁晓武', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-8-19  08-12 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('396', 'P143', '840HEZD-C', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2008-8-19 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('397', 'P144', '中芯涂料', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2008-8-22 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('398', 'P145', '30T轴重试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-2-20 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('399', 'P146', '扣留1/2/2009_4_1', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-1-3 邱风斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('400', 'P147', '扣留1/2/2009_4_57_1', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2008-1-9 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('401', 'P149', '扣留6/22/2009_2_677', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-9-4 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('402', 'P150', '放行6/22/2009_1_654', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-6-29 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('403', 'P151', '多孔雨林芯', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2009-8-17 孙延东 2016-03 2016-5-27 2019-11-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('404', 'P152', '哈尔滨薄轮缘', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-7-14 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('405', 'P153', '扣留7/20/2009_1_731', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-7-22 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('406', 'P154', '扣留8/21/2009_3_896', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-8-24 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('407', 'P155', '挑浮芯', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-8-27 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('408', 'P156', '扣留9/10/2009_3_1002', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-9-15 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('409', 'P157', '扣留9/13/2009_4_997', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-9-16 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('410', 'P159', '扣留12/11/2009_4_57_1', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2009-12-12 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('411', 'P160', '除渣剂试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-3-26 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('412', 'P163', '孙延东', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '10-09-01 2010-17 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('413', 'P166', '孙延东', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '11-18-01 2010-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('414', 'P167', '雨淋芯子试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-11-25 2010-29 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('415', 'P168', '粉煤灰加水泥', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2010-12-8 2010-31 张志 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('416', 'P169', '孙延东', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '12/16/2010 2010-32 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('417', 'P170', '孙延东', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '1/10/2011 2011-01 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('418', 'P171', '雨淋芯子试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-1-17 2011-03 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('419', 'P172', '30T轴重', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-2-9 2011-07 刘彦磊 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('420', 'P173', '刘斌', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '11-2-14  2011-10 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('421', 'P174', '4-264-L4铝偏低', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-2-28 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('422', 'P175', '孙延东南非车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '1/15/2011 2011-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('423', 'P176', '孙延东', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '1/15/2011 2011-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('424', 'P177', '平止口石墨', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '7-BJ-85-11刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('425', 'P178', 'HDZD雨淋芯子试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-6-20 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('426', 'P179', '增加沙衬厚度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-6-22 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('427', 'P180', '刘斌', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '11-8-26  2011-30 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('428', 'P181', '试验北海沙', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '11-9-15  2011-32 果吾江 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('429', 'P182', '修理模样', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-12-26孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('430', 'P183', '蒙古车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2013-1-18孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('431', 'P184', '跟踪车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2012-8-2 2012-03 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('432', 'P185', '跟踪南非车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2012-10-26 2012-12 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('433', 'P186', '跟踪南非车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2012-11-2 2012-22 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('434', 'P187', '跟踪南非车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2012-11-6 2012-24 孙延东 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('435', 'P188', '扣留6/25/2013_4_642', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2013-7-3 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('436', 'P189', '刘斌', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '13-07-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('437', 'P190', '扣留8/26/2013_4_847', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2013-8-29 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('438', 'P191', '试验石墨', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2013-9-26 张志 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('439', 'P193', '扣留11/20/2013_2_1511', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2013-11-20 梁晓武 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('440', 'P194', '扣留1/14/2014_3_46', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-1-18 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('441', 'P195', '凉石磨试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '邱风斌 2014-5-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('442', 'P196', '模样排气孔扩大', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-06 5/30/14 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('443', 'P197', '扣留5/25/2014_1_588', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'GY-BJ-32-2014 2014-6-9 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('444', 'P198', '扣留终喷涂料试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'GY-BJ-61-2014 2014-10-29 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('445', 'P199', '六西格玛试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-11-4 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('446', 'P2', '时兴东 轴孔偏', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '08-1-22 2010-4-16 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('447', 'P200', '试验石墨', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-12-9 张志 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('448', 'P201', '南非超探放行', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-12-30 聂继英 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('449', 'P202', 'PT试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2014-12-16 聂继英 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('450', 'P203', '12/14/14浇注', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '1/4/15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('451', 'P204', '终喷涂料试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2015-01 15-1-8 2016-03 2016-5-27 20181227开');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('452', 'P205', '树脂砂造型试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2015-02  2015-6-1 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('453', 'P206', 'CJ33磁痕试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2/6/2015 2015-05 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('454', 'P207', '津巴布韦', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '4/20/15 2015-07 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('455', 'P208', 'CJ33树脂试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2015-22 2015-8-26 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('456', 'P209', '雨淋芯加装过滤网', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2015-26  2015-9-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('457', 'P210', '天津返回CJ33车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2015-11-06 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('458', 'P212', '840HEZD-C级钢试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2015-34  2015-12-22 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('459', 'P213', '生产CG33', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2016-01 2016-1-22 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('460', 'P214', '840HDZD-C级钢试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2016-01 2016-1-22 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('461', 'P215', 'PAK热处理试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2016-02 2016-3-11 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('462', 'P216', '巴基斯坦PAK车轮试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2016-03 2016-3-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('463', 'P217', '32个镗孔超标轮', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2016-5-17 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('464', 'P218', '通辽石英砂实验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2016-11 2016-5-27 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('465', 'P219', '放行"9/23/2017_1_1628_2或_1"', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-114-2017 2017-10-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('466', 'P220', 'HDZD加大中芯直径试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2017-02 ');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('467', 'P221', '涂料试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', ' 2017-5-22');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('468', 'P222', 'HEZD加大中芯直径试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2017-14 2017-6-8 2017-6-8');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('469', 'P223', '碗式混沙机试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2017-15 2017-6-21 2017-6-22');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('470', 'P224', '钢水过滤网实验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2017-16 2017-7-10 刘斌 2017-16 2017-7-10');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('471', 'P225', '降低喷砂更改下石墨型', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', ' 2017-20 2017-8-14');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('472', 'P226', '通辽芯砂试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2017-21 2017-8-22');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('473', 'P227', '车轮扣留后放行', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-010-18 2018-1-15 2018-1-15');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('474', 'P228', 'E轮字号位置更改', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-05 2018-05 2018-2-5');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('475', 'P23', '生产CE-28', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '03-12-17工03-33 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('476', 'P230', '8CS放行', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-055-2018 2018-3-22');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('477', 'P231', '襄阳锆英粉试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-04 2018-6-22');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('478', 'P233', '浇注C级钢HDZD840', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-06 2018-8-24');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('479', 'P234', '降低轮毂加工量', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '2018-40 2018-10-12');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('480', 'P235', '控制C级钢硬度试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-267-2018 2018-11-30');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('481', 'P236', 'HBG车轮重新热处理', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-022-19 2019-2-15');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('482', 'P237', 'SA34-C孔径165', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', ' 2020-3-11');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('483', 'P238', '加蓬车轮GEZ', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-066-20 2020-4-15');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('484', 'P239', '复检同轴度合格放行', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-085-20 2020-5-13');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('485', 'P241', '释放20210108-1床加工车轮', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-001-21 2021-1-11');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('486', 'P28', '工艺控制试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'Process Control Test 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('487', 'P29', '定位加工', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', ' 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('488', 'P51', '减重车轮', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'Process Control Test 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('489', 'P52', 'CJ33样品 7/17/02', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', 'Process Control Test 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('490', 'P63', '吹氩试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', ' 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('491', 'P70', '25T轴重', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '3/6/03  03-09 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('492', 'P72', '降低冒口砂衬厚度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '4/15/03 试03-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('493', 'P74', '生产CH-36 CJ36', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '6/23/03  工03-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('494', 'P77', '平止口石墨7-BJ-85-11', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2011-6-16 刘斌 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('495', 'P80', '试HEZC840', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2003.10.16 试03-28 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('496', 'P82', '廊坊水玻璃试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', 'GY-BJ-148-2017 2017-12');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('497', 'P84', '增加冒口砂衬厚度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2003.11.26 试03-37 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('498', 'P85', '生产840TZR', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2003.12.22工03-34 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('499', 'P86', '插铝试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2004-1-16 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('500', 'P87', '试HDZC840', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '工试04-08 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('501', 'P88', '试HEZB840', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2004.4.9 工艺04-15 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('502', 'P89', 'P89', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', ' 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('503', 'P90', '840HDZC试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-21  2004-4-28 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('504', 'P91', 'E轮用B轮中芯', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-25  2004-5-13 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('505', 'P92', 'CG-30试验', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', '04-29  2004-5-26 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('506', 'P93', '冷中芯试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '工试04-30  2004-5-31 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('507', 'P94', '提前开箱', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2004-7-12 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('508', 'P95', '底注插铝试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '2004-9-13(04-51) 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('509', 'P96', 'IR-33试验', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-55  2004-9-24 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('510', 'P97', '低温钢水的处理', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-29  2004-11-08 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('511', 'P98', '降低浇注温度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-64  2004-11-16 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('512', 'P99', '降低底注包温度', 'Release', 'pour,inspectionrecord', '0', '2021-10-11 09:23:23.000', '04-68  2004-11-24 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('513', 'Y0', '验收员返工释放', 'Release', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', ' 2016-03 2016-5-27');
INSERT INTO test_code (id, code, explain, code_type, location, enabled, create_time, memo) VALUES ('514', 'Y1', '验收员返工扣留', 'Hold', 'pour,inspectionrecord', '1', '2021-10-11 09:23:23.000', ' 2016-03 2016-5-27');
SET IDENTITY_INSERT test_code OFF


SET IDENTITY_INSERT cihen_code ON
TRUNCATE TABLE cihen_code;
INSERT INTO cihen_code (id, code, explain, code_type, location, memo, enabled, create_time) VALUES ('1', 'OK', '磁痕检测合格', NULL, 'cihen,correctwheel', '1', '1', '2021-10-11 08:38:39.000');
INSERT INTO cihen_code (id, code, explain, code_type, location, memo, enabled, create_time) VALUES ('2', 'XPW', '需抛丸', NULL, 'cihen,correctwheel', '1', '1', '2021-10-11 08:38:39.000');
SET IDENTITY_INSERT cihen_code OFF


SET IDENTITY_INSERT test_wheel ON
TRUNCATE TABLE test_wheel
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('0', '1103130280', 'HEZD840/HDZD840', 'HEZD840/HDZD840', 'balancetest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('1', '2003225300', 'HEZD840/HDZD840', 'B1—B3 均>=90% J3-J6 均>=90% 门槛高度20%', 'ultratest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('2', '2003225300', 'HFZ915/HEZE', 'B1—B3 均>=90% J3-J6 均>=90% 门槛高度20%', 'ultratest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('3', '0311217141', 'SA34', 'Z1-Z5均>=100% J2-J5 均>=100% 门槛高度8%（侧面）15%（踏面）', 'ultratest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('4', '0103117471', 'CJ33', 'Z1-Z4均>=100% J1-J7 均>=100% 门槛高度15%', 'ultratest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('5', '0407207083', 'IR33', 'B1-B3均>=100% J2-J6 均>=100% 门槛高度15%', 'ultratest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('6', '1603217116', 'PAK', 'Z1-Z5均>=100% J2-J6 均>=100% 门槛高度15%', 'ultratest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('7', '0009124036', 'HEZD840/IR33', '845.50mm', 'finalchecktest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('8', '0902124070', 'HFZ915/HEZE', '917.00mm', 'finalchecktest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('9', '2012112258', 'SA34', '872.00mm', 'finalchecktest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('10', '0406118253', 'CJ33', '美标160', 'finalchecktest', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO test_wheel (id, wheel_serial, design, standard, location, enabled, create_time, memo) VALUES ('11', '1603217056', 'PAK', '949.00mm', 'finalchecktest', '1', '2021-10-14 10:00:00', NULL);
SET IDENTITY_INSERT test_wheel OFF


TRUNCATE TABLE case_unpack_time_ctl;
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1513,1524,38, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1524,1536,39, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1536,1547,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1547,1558,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1558,1569,42, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1569,1580,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1580,1591,44, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('A',1591,1602,45, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1536,1547,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1547,1558,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1558,1569,42, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1569,1580,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1580,1591,44, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1591,1602,46, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1602,1613,47, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1613,1624,48, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1624,1635,49, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('B',1635,9999,50, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1536,1547,37, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1547,1558,38, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1558,1569,39, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1569,1580,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1580,1591,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1591,1602,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1602,1613,44, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1613,1624,45, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1624,1635,46, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('C',1635,9999,47, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1513,1524,31, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1524,1536,32, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1536,1547,33, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1547,1558,34, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1558,1569,35, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1569,1580,36, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1580,1591,37, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1591,1602,38, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1602,1613,39, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1613,1624,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('D',1624,9999,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1513,1524,34, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1524,1536,35, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1536,1547,36, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1547,1558,37, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1558,1569,38, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1569,1580,39, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1580,1591,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1591,1602,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1602,1613,42, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('E',1613,9999,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1513,1524,39, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1524,1536,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1536,1547,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1547,1558,42, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1558,1569,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1569,1580,44, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1580,1591,45, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1591,1602,46, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('F',1602,9999,47, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1513,1524,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1524,1536,42, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1536,1547,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1547,1558,44, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1558,1569,45, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1569,1580,46, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1580,1591,47, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1591,1602,48, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('G',1602,9999,49, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1536,1547,36, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1547,1558,38, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1558,1569,39, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1569,1580,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1580,1591,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1591,1602,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1602,1613,44, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1613,1624,45, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1624,1635,46, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('H',1635,9999,47, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1513,1524,40, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1524,1536,41, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1536,1547,42, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1547,1558,43, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1558,1569,44, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1569,1580,45, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1580,1591,46, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1591,1602,47, '2021-08-08 08:00:00');
INSERT INTO case_unpack_time_ctl (type_kxsj, temp_min, temp_max, fminite, create_time) VALUES ('I',1602,9999,48, '2021-08-08 08:00:00');


TRUNCATE TABLE operating_time_ctr;
INSERT INTO operating_time_ctr (dep, operating_time, create_time) VALUES ('QA',480 , '2021-08-08 08:00:00');
INSERT INTO operating_time_ctr (dep, operating_time, create_time) VALUES ('Machine',480 , '2021-08-08 08:00:00');


TRUNCATE TABLE furnace_patching_table;
INSERT INTO furnace_patching_table (patching_name, create_time) VALUES ('渣线', '2021-08-08 08:00:00');
INSERT INTO furnace_patching_table (patching_name, create_time) VALUES ('炉坡', '2021-08-08 08:00:00');
INSERT INTO furnace_patching_table (patching_name, create_time) VALUES ('渣线炉坡', '2021-08-08 08:00:00');
INSERT INTO furnace_patching_table (patching_name, create_time) VALUES ('炉底', '2021-08-08 08:00:00');
INSERT INTO furnace_patching_table (patching_name, create_time) VALUES ('渣线炉底', '2021-08-08 08:00:00');
INSERT INTO furnace_patching_table (patching_name, create_time) VALUES ('炉底炉坡', '2021-08-08 08:00:00');
INSERT INTO furnace_patching_table (patching_name, create_time) VALUES ('渣线炉坡炉底', '2021-08-08 08:00:00');


TRUNCATE TABLE furnace_ramming_table;
INSERT INTO furnace_ramming_table (ramming_name, create_time) VALUES ('炉坡', '2021-08-08 08:00:00');
INSERT INTO furnace_ramming_table (ramming_name, create_time) VALUES ('炉底', '2021-08-08 08:00:00');
INSERT INTO furnace_ramming_table (ramming_name, create_time) VALUES ('炉底炉坡', '2021-08-08 08:00:00');


TRUNCATE TABLE furnace_status_table;
INSERT INTO furnace_status_table (fur_status_name, create_time) VALUES ('正常', '2021-08-08 08:00:00');
INSERT INTO furnace_status_table (fur_status_name, create_time) VALUES ('良好', '2021-08-08 08:00:00');
INSERT INTO furnace_status_table (fur_status_name, create_time) VALUES ('差', '2021-08-08 08:00:00');


TRUNCATE TABLE addition_position_table;
INSERT INTO addition_position_table (addition_position_name, create_time) VALUES ('料中', '2021-08-08 08:00:00');
INSERT INTO addition_position_table (addition_position_name, create_time) VALUES ('炉底', '2021-08-08 08:00:00');
INSERT INTO addition_position_table (addition_position_name, create_time) VALUES ('炉中', '2021-08-08 08:00:00');
INSERT INTO addition_position_table (addition_position_name, create_time) VALUES ('茶壶包', '2021-08-08 08:00:00');

SET IDENTITY_INSERT print_file ON
TRUNCATE TABLE print_file
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('0', 'EF1', '验收', 'printfile', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('1', 'EF2', '湖东', 'printfile', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('2', 'EF3', '国内', 'printfile,printserial', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('3', 'EF4', '国铁', 'printfile', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('4', 'CE1', '国内合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('5', 'CE2', 'IR33合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('6', 'CE3', 'PAK950合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('7', 'CE4', 'CJ33合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('8', 'CE5', 'SA34合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('9', 'CE6', 'GEZ合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('10', 'CE7', 'TZR合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('11', 'CE8', '湘潭小带尺合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('12', 'CE9', '株洲合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('13', 'CE10', '毛坯合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('14', 'CE11', '已热处理毛坯合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('15', 'CE12', 'MW957合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('16', 'CE13', '薄轮缘合格证', 'certificate', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('17', 'SA', '南非', 'printserial', '1', '2021-10-14 10:00:00', NULL);
INSERT INTO print_file (id, code, explain, location, enabled, create_time, memo) VALUES ('18', 'USA', '美国', 'printserial', '1', '2021-10-14 10:00:00', NULL);

SET IDENTITY_INSERT print_file OFF


TRUNCATE TABLE department;
SET IDENTITY_INSERT department ON
INSERT INTO department (id, dep_name, dep_key, create_time) VALUES (1, '根部门', 'ROOT', '2021-08-08 08:00:00');
INSERT INTO department (id, dep_name, dep_key, create_time) VALUES (2, '熔炼', 'FUSION', '2021-08-08 08:00:00');
INSERT INTO department (id, dep_name, dep_key, create_time) VALUES (3, '造型', 'MODEL', '2021-08-08 08:00:00');
INSERT INTO department (id, dep_name, dep_key, create_time) VALUES (4, '热处理', 'HEAT', '2021-08-08 08:00:00');
INSERT INTO department (id, dep_name, dep_key, create_time) VALUES (5, '机加', 'MA', '2021-08-08 08:00:00');
INSERT INTO department (id, dep_name, dep_key, create_time) VALUES (6, '质检', 'QA', '2021-08-08 08:00:00');
INSERT INTO department (id, dep_name, dep_key, create_time) VALUES (7, '石墨', 'GRAPHITE', '2021-08-08 08:00:00');
SET IDENTITY_INSERT department OFF


TRUNCATE TABLE station;
SET IDENTITY_INSERT station ON
INSERT INTO station (id, station_name, create_time) VALUES (1, '下芯工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (2, '浇注工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (3, '浇注工长', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (4, '浇注指导', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (5, '造型工长', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (6, '修包工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (7, '电炉炉长', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (8, '切割工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (9, '热处理工长', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (10, '进/出炉操作工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (11, '开箱工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (12, '天车工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (13, '石墨工长', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (14, '石墨操作工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (15, '备料工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (16, '质检工长', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (17, '质检操作工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (18, '机加工长', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (19, '机加操作工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (20, '发运操作工', '2021-08-08 08:00:00');
INSERT INTO station (id, station_name, create_time) VALUES (21, '技术质量工程师', '2021-08-08 08:00:00');
SET IDENTITY_INSERT station OFF
