SELECT * FROM (
  SELECT
    '放行' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    '' AS 报废代码,
    '否' AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM release_record
  WHERE release_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '质检' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    heat_code AS 热处理代码,
    test_code AS 试验代码,
    hold_code AS 保留代码,
    scrap_code AS 报废代码,
    '否' AS 确废,
    CASE brin_req WHEN 1 THEN '是' ELSE '否' END AS 需硬度检测,
    CASE xray_req WHEN 1 THEN '是' ELSE '否' END AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM inspection_record
  WHERE inspection_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '报废' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    '' AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    scrap_code AS 报废代码,
    CASE confirmed_scrap WHEN 1 THEN '是' ELSE '否' END AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM scrap_record
  WHERE scrap_record.confirmed_scrap = 1 AND scrap_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '报废纠回' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    '' AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    scrap_code AS 报废代码,
    CASE confirmed_scrap WHEN 1 THEN '是' ELSE '否' END AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM scrap_record
  WHERE scrap_record.confirmed_scrap = 0 AND scrap_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '终检' AS 工序,
    xh AS 线号,
    tape_inspector_id AS 质检工号,
    cope_inspector_id AS '上箱',
    drag_inspector_id AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    heat_code AS 热处理代码,
    test_code AS 试验代码,
    hold_code AS 保留代码,
    scrap_code AS 报废代码,
    '否' AS 确废,
    CASE brin_req WHEN 1 THEN '是' ELSE '否' END AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM final_check_record
  WHERE final_check_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '超探' AS 工序,
    xh AS 线号,
    ultra_inspector_id AS 质检工号,
    mag_cope_inspector_id AS '上箱',
    mag_drag_inspector_id AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    heat_code AS 热处理代码,
    test_code AS 试验代码,
    hold_code AS 保留代码,
    scrap_code AS 报废代码,
    '否' AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    CASE balance_s WHEN 'E3' THEN '是' ELSE '' END AS e3
  FROM ultra_record
  WHERE ultra_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '平衡机' AS 工序,
    xh AS 线号,
    balance_inspector_id AS 质检工号,
    mark_inspector_id AS '上箱',
    stick_inspector_id AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    heat_code AS 热处理代码,
    test_code AS 试验代码,
    hold_code AS 保留代码,
    scrap_code AS 报废代码,
    '否' AS 确废,
    CASE brin_req WHEN 1 THEN '是' ELSE '否' END AS 需硬度检测,
    CASE xray_req WHEN 1 THEN '是' ELSE '否' END AS x光,
    ISNULL(CONVERT(VARCHAR(10),balance_v), '') + '/' + ISNULL(CONVERT(VARCHAR(10),balance_a), '') AS 静不平衡,
    CASE balance_s WHEN 'E3' THEN '是' ELSE '' END AS e3
  FROM balance_record
  WHERE balance_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '预检' AS 工序,
    '' AS 线号,
    '' AS 质检工号,
    cope_inspector_id AS '上箱',
    drag_inspector_id AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    heat_code AS 热处理代码,
    test_code AS 试验代码,
    '' AS 保留代码,
    scrap_code AS 报废代码,
    '否' AS 确废,
    CASE brin_req WHEN 1 THEN '是' ELSE '否' END AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM pre_check_record
  WHERE pre_check_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '成品纠回' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    hold_code AS 保留代码,
    scrap_code AS 报废代码,
    CASE confirmed_scrap WHEN 1 THEN '是' ELSE '否' END AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
   '' AS 静不平衡,
    '' AS e3
  FROM correct_wheel_record
  WHERE correct_wheel_record.recall_type = 1 AND correct_wheel_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '入库纠回' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    hold_code AS 保留代码,
    scrap_code AS 报废代码,
    CASE confirmed_scrap WHEN 1 THEN '是' ELSE '否' END AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM correct_wheel_record
  WHERE correct_wheel_record.recall_type = 2 AND correct_wheel_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '返厂纠回' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    hold_code AS 保留代码,
    scrap_code AS 报废代码,
    CASE confirmed_scrap WHEN 1 THEN '是' ELSE '否' END AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM correct_wheel_record
  WHERE correct_wheel_record.recall_type = 3 AND correct_wheel_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '磁探' AS 工序,
    '' AS 线号,
    '' AS 质检工号,
    mag_cope_inspector_id AS '上箱',
    mag_drag_inspector_id AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    scrap_code AS 报废代码,
    '否' AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM magnetic_record
  WHERE magnetic_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '条码打印' AS 工序,
    '' AS 线号,
    barcode_inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    '' AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    '' AS 报废代码,
    '否' AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    '' AS e3
  FROM barcode_print_record
  WHERE barcode_print_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    'X光发运' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    '' AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    '' AS 报废代码,
    '否' AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    CASE balance_s WHEN 'E3' THEN '是' ELSE '' END AS e3
  FROM transport_record
  WHERE transport_record.ope_type = 81 AND transport_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '去重发运' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    '' AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    '' AS 报废代码,
    '否' AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    CASE balance_s WHEN 'E3' THEN '是' ELSE '' END AS e3
  FROM transport_record
  WHERE transport_record.ope_type = 82 AND transport_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '镗孔发运' AS 工序,
    '' AS 线号,
    inspector_id AS 质检工号,
    '' AS '上箱',
    '' AS '下箱',
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    '' AS 返工代码,
    '' AS 热处理代码,
    '' AS 试验代码,
    '' AS 保留代码,
    '' AS 报废代码,
    '否' AS 确废,
    '否' AS 需硬度检测,
    '否' AS x光,
    '' AS 静不平衡,
    CASE balance_s WHEN 'E3' THEN '是' ELSE '' END AS e3
  FROM transport_record
  WHERE transport_record.ope_type = 203 AND transport_record.wheel_serial = :wheelSerial
) t
ORDER BY 操作时间
