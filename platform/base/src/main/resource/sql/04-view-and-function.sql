
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


EXEC p_drop_view 'v_account_authority';
GO

CREATE VIEW v_account_authority
AS
SELECT a.id AS uid, username, role_name, authority_id, authority_name
FROM account a LEFT JOIN account_role ar ON ar.account_id = a.id
LEFT JOIN role r ON r.id = ar.role_id
LEFT JOIN role_authority ra ON ra.role_id = ar.role_id
LEFT JOIN authority au ON au.id = ra.authority_id;
GO


EXEC p_drop_view 'v_authority_leaf';
GO

CREATE VIEW v_authority_leaf
AS
SELECT a.id, a.authority_name, a.descritpion, r.id AS role_id
FROM role r, role_authority ra, authority a
WHERE r.id = ra.role_id AND ra.authority_id = a.id
  AND NOT EXISTS (SELECT aa.id FROM authority aa WHERE aa.parent_id = a.id);
GO


-- 分组查询最大原始石墨号
EXEC p_drop_view 'v_max_graphite_firm';
GO

CREATE VIEW v_max_graphite_firm
AS
SELECT left(graphite_key, 2) graphitePre, max(graphite_key) maxGraphite
FROM graphite_firm
GROUP BY left(graphite_key, 2);
GO


-- 分组查询最大石墨号
EXEC p_drop_view 'max_graphite';
GO

CREATE VIEW max_graphite
AS
SELECT left(graphite, 2) graphitePre, max(graphite) maxGraphite, cd
FROM graphite
GROUP BY left(graphite, 2), cd;
GO


-- 分组查询最大石墨号
EXEC p_drop_view 'v_graphite_scrap';
GO

CREATE VIEW v_graphite_scrap
AS
SELECT f.graphite_key, g.graphite, g.design, f.scrap_code, f.scrap_date
FROM graphite_firm f JOIN graphite g ON f.graphite_key = g.graphite_key
WHERE g.status = 6;
GO


-- 石墨加工查询
EXEC p_drop_view 'v_graphite_process';
GO

CREATE VIEW v_graphite_process
AS
SELECT g.graphite_key, graphite, design, height, g.process_date 
FROM graphite g JOIN ( 
  SELECT graphite_key, max(process_date) AS process_date FROM graphite GROUP BY graphite_key 
) p ON g.graphite_key = p.graphite_key and g.process_date = p.process_date;


-- 浇注延时统计
EXEC p_drop_view 'v_pour_delay_stat';
GO

CREATE VIEW v_pour_delay_stat
AS
SELECT h.id heat_record_id,
  h.cast_date pour_date,
  h.heat_seq,
  isnull(isnull(min(p.pour_d_t), min(l.record_created)), h.record_created) pour_begin_time,
  isnull(isnull(max(p.pour_d_t), max(l.record_created)), h.record_created) pour_end_time,
	datediff(minute, 
	         isnull(isnull(min(p.pour_d_t), min(l.record_created)), h.record_created), 
	         isnull(isnull(max(p.pour_d_t), max(l.record_created)), h.record_created)
					) pour_duration,
  count(0) pour_wheel_num
FROM heat_record h
LEFT JOIN ladle_record l ON h.id = l.heat_record_id
LEFT JOIN pour_record p ON l.id = p.ladle_id
GROUP BY h.id, h.cast_date, h.heat_seq, h.record_created;
GO


-- 表信息视图，用于字典表模块
EXEC p_drop_view 'v_table_info';
GO

CREATE VIEW v_table_info
AS
SELECT t.name AS table_name, c.name AS column_name, ep.value AS column_desc, c.is_nullable, st.[name] AS column_type, ep.minor_id AS column_id
  FROM sys.tables AS t
  JOIN sys.columns AS c ON t.object_id = c.object_id
  LEFT JOIN sys.extended_properties AS ep ON ep.major_id = c.object_id AND ep.minor_id = c.column_id
  JOIN sys.types AS st ON c.system_type_id = st.system_type_id
GO


-- 原材料视图
EXEC p_drop_view 'v_material_record';
GO

CREATE VIEW v_material_record
AS
SELECT
  m.id,
  m.material_name,
  m.manufacturer_id,
  m.dept,
  m.batch_no,
  m.parameter1,
  m.parameter2,
  m.parameter3,
  m.parameter4,
  m.parameter5,
  m.parameter6,
  m.parameter7,
  m.parameter8,
  m.operator,
  m.status,
  m.start_time,
  m.suspend_time,
  m.stop_time,
  m.create_time,
  m.material_id,
  mf.name AS manufacturer_name
FROM material_record m LEFT JOIN manufacturer mf
ON m.manufacturer_id = mf.id;
GO


-- 二维码校验结果视图
EXEC p_drop_view 'v_audit_result';
GO

CREATE VIEW v_audit_result
AS
SELECT
  a.id,
  a.audit_batch,
  a.wheel_serial,
  a.c116 AS audit_tape_size,
  a.c123 AS internal_design,
  a.c133 AS internal_bore_size,
  a.c114 AS external_design,
  a.c120 AS external_bore_size,
  w.design,
  w.tape_size,
  w.bore_size,
  w.finished
FROM audit_detail a JOIN wheel_record w
ON w.wheel_serial = a.wheel_serial;
GO


-- 通知视图
EXEC p_drop_view 'v_notification';
GO

CREATE VIEW v_notification
AS
SELECT n.id, n.title, ns.read_status, n.create_time, ns.account_id
FROM notification n JOIN notify_status ns ON n.id = ns.notify_id
WHERE notify_type = 1;
GO


-- 技术文档视图
EXEC p_drop_view 'v_technical_document';
GO

CREATE VIEW v_technical_document
AS
SELECT t.id, t.title, ns.read_status, t.create_time, ns.account_id
FROM technical_document t JOIN notify_status ns ON t.id = ns.notify_id
WHERE notify_type = 2;
GO


-- 合格证视图
EXEC p_drop_view 'v_certificate';
GO

CREATE VIEW v_certificate
AS
SELECT
  wheel_record.wheel_id AS id,
  ladle_record.ladle_record_key AS ladle_record_key,
  wheel_record.wheel_serial,
  ISNULL(wheel_record.shelf_number, '') AS shelf_number,
  wheel_record.design,
  ISNULL(wheel_record.wheel_w, 0) AS wheel_w,
  train_no.shipped_date,
  train_no.shipped_no,
  ISNULL(wheel_record.tape_size, 0) AS tape_size,
  customer.customer_name,
  train_no.train_no,
  ISNULL(wheel_record.balance_s, '') AS balance_s,
  ISNULL(wheel_record.brinnel_reading, 0) AS brinnel_reading,
  ISNULL(design.drawing_no, '') AS drawing_no,
  ISNULL(design.approbation_no, '') AS approbation_no,
  ISNULL(design.spec, '') AS spec,
  ISNULL(design.transfer_record_no, '') AS transfer_record_no,
  design.steel_class,
  ISNULL(chemistry_detail.C, 0) AS C,
  ISNULL(chemistry_detail.Mn, 0) AS Mn,
  ISNULL(chemistry_detail.P, 0) AS P,
  ISNULL(chemistry_detail.S, 0) AS S,
  ISNULL(chemistry_detail.Si, 0) AS Si,
  ISNULL(chemistry_detail.Cr, 0) AS Cr,
  ISNULL(chemistry_detail.Ni, 0) AS Ni,
  ISNULL(chemistry_detail.Mo, 0) AS Mo,
  ISNULL(chemistry_detail.Cu, 0) AS Cu,
  ISNULL(chemistry_detail.Nb, 0) AS Nb,
  ISNULL(chemistry_detail.V, 0)  AS V,
  ISNULL(chemistry_detail.Ti, 0) AS Ti,
  ISNULL(chemistry_detail.Al, 0) AS Al,
  ISNULL(chemistry_detail.B, 0) AS H,
  wheel_record.check_code,
  ISNULL(pour_record.batch_no, '') AS batch_no
FROM customer
INNER JOIN train_no ON customer.customer_id = train_no.customer_id
INNER JOIN wheel_record ON train_no.shipped_no = wheel_record.shipped_no
INNER JOIN design ON design.design = wheel_record.design
INNER JOIN chemistry_detail  ON chemistry_detail.ladle_id = wheel_record.ladle_id
INNER JOIN pour_record ON wheel_record.wheel_serial = pour_record.wheel_serial
INNER JOIN ladle_record ON wheel_record.ladle_id = ladle_record.id;


-- 账号视图
EXEC p_drop_view 'v_account_detail';
GO

CREATE VIEW v_account_detail
AS
SELECT
  a.id,
  a.username,
  a.password,
  a.status,
  a.nickname,
  a.mobile,
  a.email,
  a.avatar,
  a.photo_url,
  a.login_status,
  a.location,
  a.team_leader_id,
  a.dep_id,
  d.dep_name,
  a.station_id,
  s.station_name,
  a.memo,
  a.create_time,
  a.enabled,
  a.is_leader
FROM account a
LEFT JOIN department d ON a.dep_id = d.id
LEFT JOIN station s ON a.station_id = s.id;

GO


-- 炉、小包、车轮视图
EXEC p_drop_view 'v_heat_ladle_wheel';
GO

CREATE VIEW v_heat_ladle_wheel
AS
SELECT
  h.cast_date,
  h.pourleader_id,
  h.model_id,
  h.modi_id,
  h.furnace_id,
  h.furnace_no,
  h.tap_seq,
  l.ladle_temp,
  l.ladle_seq,
  w.wheel_serial,
  w.design,
  w.test_code,
  w.scrap_code,
  w.pre,
  w.final,
  w.ultra,
  w.balance,
  w.finished,
  w.confirmed_scrap,
  w.xray_req,
  w.scrap_date,
  w.tape_size,
  w.wheel_w,
  w.balance_s
FROM heat_record h
INNER JOIN ladle_record l ON h.id = l.heat_record_id
INNER JOIN wheel_record w ON l.id = w.ladle_id;


-- 炉、小包、浇注、车轮视图
EXEC p_drop_view 'v_heat_ladle_pour_wheel';
GO

CREATE VIEW v_heat_ladle_pour_wheel
AS
SELECT
  h.cast_date,
  h.heat_record_key,
  h.pourleader_id,
  h.model_id,
  h.modi_id,
  h.furnace_id,
  h.tap_seq,
  h.furnace_no,
  h.ladle_no,
  p.ladle_id,
  p.in_pit_date_time,
  p.drag_no,
  p.cope_no,
  p.pour_d_t,
  l.ladle_temp,
  l.ladle_seq,
  w.wheel_serial,
  w.design,
  w.test_code,
  w.scrap_code,
  w.pre,
  w.finished,
  w.confirmed_scrap,
  p.record_created
FROM heat_record h
INNER JOIN ladle_record l ON h.id = l.heat_record_id
INNER JOIN pour_record p ON l.id = p.ladle_id
INNER JOIN wheel_record w ON p.wheel_serial = w.wheel_serial;
GO


-- DATETIME转成短日期格式
IF OBJECT_ID('getShortDateTime') IS NOT NULL
  DROP FUNCTION getShortDateTime
GO
CREATE FUNCTION getShortDateTime(@dt DATETIME)
RETURNS VARCHAR(20)
AS
BEGIN
  DECLARE @newTime VARCHAR(20)
  SET @newTime = CONVERT(VARCHAR(100), @dt, 11) + ' ' + SUBSTRING(CONVERT(VARCHAR(100), @dt, 24), 1, 5)
  RETURN @newTime
END
GO


-- SMALLDATETIME转成短日期格式
IF OBJECT_ID('getShortDateTime1') IS NOT NULL
  DROP FUNCTION getShortDateTime1
GO
CREATE FUNCTION getShortDateTime1(@dt DATE, @tm SMALLDATETIME)
RETURNS VARCHAR(20)
AS
BEGIN
  DECLARE @newTime VARCHAR(20)
  SET @newTime = CONVERT(VARCHAR(100), @dt, 11) + ' ' + SUBSTRING(CONVERT(VARCHAR(100), @tm, 24), 1, 5)
  RETURN @newTime
END
GO


-- 两个整数相除计算出百分比
-- dividend：被除数，divider：除数
IF OBJECT_ID('percentage') IS NOT NULL
  DROP FUNCTION percentage
GO
CREATE FUNCTION percentage(@dividend INTEGER, @divider INTEGER)
RETURNS VARCHAR(20)
AS
BEGIN
  DECLARE @pct VARCHAR(20)
  IF (@divider = 0) 
    RETURN '0'
  SET @pct = CONVERT(VARCHAR, CONVERT(FLOAT, CONVERT(DECIMAL(10,2), 100.0 * @dividend / @divider)))
  RETURN @pct
END
GO


-- 两个整数相除计算出百分比
-- dividend：被除数，divider：除数，prec：精度
IF OBJECT_ID('percentage1') IS NOT NULL
  DROP FUNCTION percentage1
GO
CREATE FUNCTION percentage1(@dividend INTEGER, @divider INTEGER, @prec INTEGER)
RETURNS VARCHAR(20)
AS
BEGIN
  DECLARE @pct VARCHAR(20)
  IF (@divider = 0) 
    RETURN '0'
  SET @pct = CONVERT(VARCHAR, ROUND(CONVERT(FLOAT, 100.0 * @dividend / @divider), 4))
  RETURN @pct
END
GO
