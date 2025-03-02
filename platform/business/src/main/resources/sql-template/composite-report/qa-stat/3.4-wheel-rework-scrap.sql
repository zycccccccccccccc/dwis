WITH t1 AS (
SELECT DISTINCT
  p.wheel_serial,
  rework_code
FROM inspection_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND rework_code <> ''
UNION
SELECT DISTINCT
  p.wheel_serial,
  rework_code
FROM ultra_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND rework_code <> ''
UNION
SELECT DISTINCT
  p.wheel_serial,
  rework_code
FROM balance_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND rework_code <> ''
UNION
SELECT DISTINCT
  p.wheel_serial,
  rework_code
FROM pre_check_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND rework_code <> ''
UNION
SELECT DISTINCT
  p.wheel_serial,
  rework_code
FROM final_check_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND rework_code <> ''
UNION
SELECT DISTINCT
  p.wheel_serial,
  rework_code
FROM correct_wheel_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND rework_code <> ''
UNION
SELECT DISTINCT
  p.wheel_serial,
  rework_code
FROM magnetic_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND rework_code <> ''
),

t2 AS (
SELECT
  CONVERT(varchar(100), v.cast_date, 111) AS cast_date,
  COUNT(1) AS cast_total,
  SUM(CASE WHEN pre > 0 OR scrap_code <> '' THEN 1 ELSE 0 END) AS pre,
  SUM(finished) AS dock
FROM v_heat_ladle_wheel v
WHERE 1 = 1
<#if design??>
  AND design IN :design
</#if>
GROUP BY v.cast_date
),

t3 AS (
SELECT
  CONVERT(varchar(100), v.cast_date, 111) AS cast_date,
  SUM(CASE WHEN rework_code = '9A' THEN 1 ELSE 0 END) AS rework_9a,
  SUM(CASE WHEN rework_code = '9C' THEN 1 ELSE 0 END) AS rework_9c,
  SUM(CASE WHEN rework_code = '23' THEN 1 ELSE 0 END) AS rework_23,
  SUM(CASE WHEN rework_code = '88' THEN 1 ELSE 0 END) AS rework_88,
  SUM(CASE WHEN SUBSTRING(rework_code, 1, 2) = '12' THEN 1 ELSE 0 END) AS rework_12,
  SUM(CASE WHEN rework_code = '67R' THEN 1 ELSE 0 END) AS rework_67r,
  SUM(CASE WHEN rework_code = '67F' THEN 1 ELSE 0 END) AS rework_67f,
  SUM(CASE WHEN rework_code = '67H' THEN 1 ELSE 0 END) AS rework_67h,
  SUM(CASE WHEN rework_code = '44A' THEN 1 ELSE 0 END) AS rework_44a,
  SUM(CASE WHEN rework_code = '58' THEN 1 ELSE 0 END) AS rework_58,
  SUM(CASE WHEN rework_code = '59' THEN 1 ELSE 0 END) AS rework_59,
  SUM(CASE WHEN rework_code = '8C' THEN 1 ELSE 0 END) AS rework_8c,
  SUM(CASE WHEN rework_code = '67C' THEN 1 ELSE 0 END) AS rework_67c,
  SUM(CASE WHEN rework_code = '2A' THEN 1 ELSE 0 END) AS rework_2a,
  SUM(CASE WHEN rework_code = 'H1' THEN 1 ELSE 0 END) AS rework_h1,
  SUM(CASE WHEN rework_code = 'H2' THEN 1 ELSE 0 END) AS rework_h2,
  SUM(CASE WHEN rework_code = 'H3' THEN 1 ELSE 0 END) AS rework_h3,
  SUM(CASE WHEN rework_code = 'H4' THEN 1 ELSE 0 END) AS rework_h4,
  SUM(CASE WHEN rework_code = 'H5' THEN 1 ELSE 0 END) AS rework_h5,
  SUM(CASE WHEN rework_code = 'H6' THEN 1 ELSE 0 END) AS rework_h6
FROM v_heat_ladle_wheel v
JOIN t1 t ON t.wheel_serial = v.wheel_serial
WHERE 1 = 1
<#if design??>
  AND design IN :design
</#if>
<#if reworkCode??>
  AND rework_code IN :reworkCode
</#if>
GROUP BY v.cast_date
)

SELECT * FROM (
SELECT
  t2.cast_date,
  cast_total,
  pre,
  dock,
  rework_9a,
  rework_9c,
  rework_23,
  rework_88,
  rework_12,
  rework_67r,
  rework_67f,
  rework_67h,
  rework_44a,
  rework_58,
  rework_59,
  rework_8c,
  rework_67c,
  rework_2a,
  rework_h1,
  rework_h2,
  rework_h3,
  rework_h4,
  rework_h5,
  rework_h6,
  ( rework_9a + rework_9c + rework_23 + rework_88 + rework_12
  + rework_67r + rework_67f + rework_67h + rework_44a + rework_58
  + rework_59 + rework_8c + rework_67c + rework_2a + rework_h1
  + rework_h2 + rework_h3 + rework_h4 + rework_h5 + rework_h6 ) AS rework_total
FROM t2
JOIN t3 ON t2.cast_date = t3.cast_date
UNION ALL
SELECT
  'total' AS cast_date,
  SUM(cast_total) AS cast_total,
  SUM(pre) AS pre,
  SUM(dock) AS dock,
  SUM(rework_9a) AS rework_9a,
  SUM(rework_9c) AS rework_9c,
  SUM(rework_23) AS rework_23,
  SUM(rework_88) AS rework_88,
  SUM(rework_12) AS rework_12,
  SUM(rework_67r) AS rework_67r,
  SUM(rework_67f) AS rework_67f,
  SUM(rework_67h) AS rework_67h,
  SUM(rework_44a) AS rework_44a,
  SUM(rework_58) AS rework_58,
  SUM(rework_59) AS rework_59,
  SUM(rework_8c) AS rework_8c,
  SUM(rework_67c) AS rework_67c,
  SUM(rework_2a) AS rework_2a,
  SUM(rework_h1) AS rework_h1,
  SUM(rework_h2) AS rework_h2,
  SUM(rework_h3) AS rework_h3,
  SUM(rework_h4) AS rework_h4,
  SUM(rework_h5) AS rework_h5,
  SUM(rework_h6) AS rework_h6,
  SUM(rework_9a + rework_9c + rework_23 + rework_88 + rework_12
  + rework_67r + rework_67f + rework_67h + rework_44a + rework_58
  + rework_59 + rework_8c + rework_67c + rework_2a + rework_h1
  + rework_h2 + rework_h3 + rework_h4 + rework_h5 + rework_h6) AS rework_total
FROM t2
JOIN t3 ON t2.cast_date = t3.cast_date
) t
ORDER BY cast_date
