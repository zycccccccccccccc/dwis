SELECT * FROM (
  SELECT
    '基面' AS 工序,
    j_machine_record. machine_no AS 机床号,
    j_machine_record. operator AS 操作工号,
    dbo.getShortDateTime(j_machine_record.ope_d_t) AS 操作时间,
    rework_code AS '返工',
    CONVERT(VARCHAR(10), j_s1) AS s1,
    CONVERT(VARCHAR(10), j_s2) AS s2,
    CASE j_machine_record.is_check WHEN 1 THEN '是' ELSE '否' END AS 是否抽检复选框,
    CASE is_inspec_check WHEN 1 THEN '是' ELSE '否' END AS 工长抽检复选框,
    f AS 数据1,
    CONVERT(VARCHAR(10), d2_dia) AS 数据2,
    CONVERT(VARCHAR(10), d2_cir) AS 数据3,
    CASE calibra_wheel.is_check WHEN 1 THEN '是' ELSE '否' END AS 是否量具检查
  FROM j_machine_record
  LEFT JOIN calibra_wheel ON j_machine_record.cali_wheel_id = calibra_wheel.id
  WHERE j_machine_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '踏面' AS 工序,
    machine_no AS 机床号,
    operator AS 操作工号,
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS '返工',
    CONVERT(VARCHAR(10), t_s1) AS s1,
    CONVERT(VARCHAR(10), t_s2) AS s2,
    CASE is_check WHEN 1 THEN '是' ELSE '否' END AS 是否抽检复选框,
    CASE is_inspec_check WHEN 1 THEN '是' ELSE '否' END AS 工长抽检复选框,
    CONVERT(VARCHAR(10), flange_tread_profile) AS 数据1,
    CONVERT(VARCHAR(10), t_chamfer) AS 数据2,
    CONVERT(VARCHAR(10), rolling_circle_dia) AS 数据3,
    CASE is_measure_check WHEN 1 THEN '是' ELSE '否' END AS 是否量具检查
  FROM t_machine_record
  WHERE t_machine_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '镗孔' AS 工序,
    machine_no AS 机床号,
    operator AS 操作工号,
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS '返工',
    CONVERT(VARCHAR(10), k_s1) AS s1,
    CONVERT(VARCHAR(10), k_s2) AS s2,
    CASE is_check WHEN 1 THEN '是' ELSE '否' END AS 是否抽检复选框,
    CASE is_inspec_check WHEN 1 THEN '是' ELSE '否' END AS 工长抽检复选框,
    CONVERT(VARCHAR(10), concentricity) AS 数据1,
    CONVERT(VARCHAR(10), bore_dia) AS 数据2,
    CASE location WHEN '1' THEN '左工位' ELSE '右工位' END AS 数据3,
    CASE is_measure_check WHEN 1 THEN '是' ELSE '否' END AS 是否量具检查
  FROM k_machine_record
  WHERE k_machine_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '外辐板' AS 工序,
    machine_no AS 机床号,
    operator AS 操作工号,
    dbo.getShortDateTime(ope_d_t) AS 操作时间,
    rework_code AS '返工',
    CONVERT(VARCHAR(10), w_s1) AS s1,
    CONVERT(VARCHAR(10), w_s2) AS s2,
    '否' AS 是否抽检复选框,
    CASE is_inspec_check WHEN 1 THEN '是' ELSE '否' END AS 工长抽检复选框,
    CONVERT(VARCHAR(10), hub_exradius) AS 数据1,
    CONVERT(VARCHAR(10), plate_thickness) AS 数据2,
    CONVERT(VARCHAR(10), rim_thickness) AS 数据3,
    CASE is_measure_check WHEN 1 THEN '是' ELSE '否' END AS 是否量具检查
  FROM w_machine_record
  WHERE w_machine_record.wheel_serial = :wheelSerial
  UNION ALL
  SELECT
    '去重' AS 工序,
    q_machine_record.machine_no AS 机床号,
    q_machine_record.operator AS 操作工号,
    dbo.getShortDateTime(q_machine_record.ope_d_t) AS 操作时间,
    hold_code AS '返工',
    '' AS s1,
    '' AS s2,
    '否' AS 是否抽检复选框,
    CASE is_inspec_check WHEN 1 THEN '是' ELSE '否' END AS 工长抽检复选框,
    CONVERT(VARCHAR(10), chuck1) AS 数据1,
    CONVERT(VARCHAR(10), pad1) AS 数据2,
    CONVERT(VARCHAR(10), deviation) AS 数据3,
    CASE calibra_wheel.is_check WHEN 1 THEN '是' ELSE '否' END AS 是否量具检查
  FROM q_machine_record
  LEFT JOIN calibra_wheel ON q_machine_record.cali_wheel_id = calibra_wheel.id
  WHERE q_machine_record.wheel_serial = :wheelSerial
) t
ORDER BY 操作时间