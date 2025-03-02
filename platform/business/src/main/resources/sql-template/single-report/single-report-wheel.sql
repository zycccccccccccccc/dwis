
SELECT
  w.wheel_serial AS 轮号,
  w.design AS 轮型,
  CONVERT(VARCHAR, w.bore_size) AS 轮毂孔径,
  CONVERT(VARCHAR, w.tape_size) AS 滚动圆直径,
  w.wheel_w AS 轮辋宽度,
  w.brinnel_reading AS 硬度值,
  CASE w.finished WHEN 1 THEN CONVERT(VARCHAR, d.hub_length) ELSE '' END AS 轮毂长度,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 抛丸,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 轮缘和踏面外形,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 超磁探,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 剩磁检验,
  w.balance_s AS 平衡标识,
  w.balance_v AS 静不平衡值,
  w.balance_a AS 静不平衡角度,
  w.heat_code AS 热处理代码,
  w.hold_code AS 保留代码,
  w.test_code AS 试验代码,
  CASE r.single_wheel_type WHEN 1 THEN w.rework_code ELSE '' END AS 外观返工代码,
  CASE r.single_wheel_type WHEN 2 THEN w.rework_code ELSE '' END AS 尺寸返工代码,
  CASE r.single_wheel_type WHEN 4 THEN w.rework_code ELSE '' END AS 超探返工代码,
  CASE r.single_wheel_type WHEN 3 THEN w.rework_code ELSE '' END AS 磁探返工代码,
  CASE s.single_wheel_type WHEN 1 THEN w.scrap_code ELSE '' END AS 外观废品代码,
  CASE s.single_wheel_type WHEN 2 THEN w.scrap_code ELSE '' END AS 尺寸废品代码,
  CASE s.single_wheel_type WHEN 4 THEN w.scrap_code ELSE '' END AS 超探废品代码,
  CASE s.single_wheel_type WHEN 3 THEN w.scrap_code ELSE '' END AS 磁探废品代码,
  CASE
    WHEN w.x_finished_id IS NULL
    AND w.re_weight_id IS NULL
    AND w.k_finished_id IS NULL
    AND w.finished = 1
  THEN '是' ELSE '否' END AS 是否线上成品,
  CASE
    WHEN w.x_finished_id IS NOT NULL
    AND w.finished = 1
  THEN '是' ELSE '否' END AS 是否X光成品,
  CASE
    WHEN w.re_weight_id IS NOT NULL
    AND w.finished = 1
  THEN '是' ELSE '否' END AS 是否去重成品,
  CASE
    WHEN w.k_finished_id IS NOT NULL
    AND w.finished = 1
  THEN '是' ELSE '否' END AS 是否镗孔成品,
  CASE w.confirmed_scrap WHEN 1 THEN '是' ELSE '否' END AS 是否确废,
  CONVERT(VARCHAR(8), w.scrap_date, 11) AS 报废日期,
  CASE w.xray_req WHEN 1 THEN '是' ELSE '否' END AS 是否X光,
  dbo.getShortDateTime(w.last_pre) AS 最后预检时间,
  dbo.getShortDateTime(w.last_final) AS 最后终检时间,
  dbo.getShortDateTime(w.last_ultra) AS 最后超探时间,
  dbo.getShortDateTime(w.last_balance) AS 最后平衡机时间,
  dbo.getShortDateTime(w.last_barcode) AS 最后打条码时间,
  REVERSE(SUBSTRING(REVERSE(w.mec_serial), CHARINDEX('-', REVERSE(w.mec_serial)) + 1, 50)) AS 机械性能批次号,
  CASE SUBSTRING(w.mec_serial, 1, 1) WHEN 'P' THEN '合格' ELSE '' END AS 机械性能,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 表面质量,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 辐板厚度及外形,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 热处理均匀性,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 轮辋外侧内径,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 轮辋内侧内径,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 轮辋厚度差,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 轮辋内外侧内径差,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 轮毂外径,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 同侧轮毂壁厚差,
  CASE w.finished WHEN 1 THEN '合格' ELSE '' END AS 内侧毂辋距
FROM wheel_record w
INNER JOIN design d ON w.design = d.design
LEFT JOIN scrap_code s ON w.scrap_code = s.code
LEFT JOIN rework_code r ON w.rework_code = r.code
WHERE w.wheel_serial = :wheelSerial
