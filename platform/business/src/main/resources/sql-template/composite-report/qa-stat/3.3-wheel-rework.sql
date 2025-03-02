WITH pre_temp AS (
SELECT DISTINCT
  cast_date,
  rework_code,
  p.wheel_serial
FROM pre_check_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
),

pre_rework1 AS (
SELECT
  SUM(CASE WHEN rework_code <> '' THEN 1 ELSE 0 END) AS prew,
  CONVERT(varchar(100), cast_date, 111) AS cast_date,
  SUM(CASE WHEN rework_code IN('4E', '4R') THEN 1 ELSE 0 END) AS p4,
  SUM(CASE WHEN rework_code IN('5E', '5R') THEN 1 ELSE 0 END) AS p5,
  SUM(CASE WHEN rework_code IN('6E', '6R') THEN 1 ELSE 0 END) AS p6,
  SUM(CASE WHEN rework_code = '7R' THEN 1 ELSE 0 END) AS p7,
  SUM(CASE WHEN rework_code LIKE 'H%' THEN 1 ELSE 0 END) AS ph
FROM pre_temp
GROUP BY
  cast_date
),

pre_rework2 AS (
SELECT
  CONVERT(varchar(100), cast_date, 111) AS cast_date,
  SUM(CASE WHEN pre > 0 OR scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
  COUNT(1) AS cast_total,
  SUM(CASE WHEN pre = 1 AND final = 1 AND ultra = 1 AND balance = 1 AND finished = 1 THEN 1 ELSE 0 END) AS good1,
  SUM(CASE WHEN pre = 1 THEN 1 ELSE 0 END) AS pre1,
  SUM(CASE WHEN pre = 2 THEN 1 ELSE 0 END) AS pre2,
  SUM(CASE WHEN pre = 3 THEN 1 ELSE 0 END) AS pre3,
  SUM(CASE WHEN pre > 3 OR pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre4,
  SUM(CASE WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre5,
  SUM(CASE WHEN final = 1 THEN 1 ELSE 0 END) AS fin1,
  SUM(CASE WHEN final = 2 THEN 1 ELSE 0 END) AS fin2,
  SUM(CASE WHEN final > 2 THEN 1 ELSE 0 END) AS fin3
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  cast_date
),

fin_temp AS (
SELECT DISTINCT
  cast_date,
  rework_code,
  p.wheel_serial
FROM final_check_record r
JOIN pour_record p ON r.wheel_serial = p.wheel_serial
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
),

fin_rework AS (
SELECT
  CONVERT(varchar(100), cast_date, 111) AS cast_date,
  SUM(CASE WHEN rework_code <> '' THEN 1 ELSE 0 END) AS frew,
  SUM(CASE WHEN rework_code IN('4E', '4R') THEN 1 ELSE 0 END) AS f4,
  SUM(CASE WHEN rework_code IN('5E', '5R') THEN 1 ELSE 0 END) AS f5,
  SUM(CASE WHEN rework_code IN('6E', '6R') THEN 1 ELSE 0 END) AS f6,
  SUM(CASE WHEN rework_code = '7R' THEN 1 ELSE 0 END) AS f7,
  SUM(CASE WHEN rework_code LIKE 'H%' THEN 1 ELSE 0 END) AS fh
FROM fin_temp
GROUP BY
  cast_date
)
SELECT * FROM (
SELECT
  pre_rework2.cast_date,
  cast_total,
  pre_insp,
  good1,
  dbo.percentage(good1, cast_total) AS good1_pct,
  pre1,
  dbo.percentage(pre1, cast_total) AS pre1_pct,
  pre2,
  dbo.percentage(pre2, cast_total) AS pre2_pct,
  pre3,
  dbo.percentage(pre3, cast_total) AS pre3_pct,
  pre4,
  dbo.percentage(pre4, cast_total) AS pre4_pct,
  fin1,
  dbo.percentage(fin1, cast_total) AS fin1_pct,
  fin2,
  dbo.percentage(fin2, cast_total) AS fin2_pct,
  fin3,
  dbo.percentage(fin3, cast_total) AS fin3_pct,

  prew,
  dbo.percentage(prew, cast_total) AS prew_pct,
  p4,
  dbo.percentage(p4, cast_total) AS p4_pct,
  p5,
  dbo.percentage(p5, cast_total) AS p5_pct,
  p6,
  dbo.percentage(p6, cast_total) AS p6_pct,
  p7,
  dbo.percentage(p7, cast_total) AS p7_pct,
  ph,
  dbo.percentage(ph, cast_total) AS ph_pct,
  prew - p4 - p5 - p6 - p7 - ph AS poth,
  dbo.percentage(prew - p4 - p5 - p6 - p7 - ph, cast_total) AS poth_pct,

  frew,
  dbo.percentage(frew, cast_total) AS frew_pct,
  f4,
  dbo.percentage(f4, cast_total) AS f4_pct,
  f5,
  dbo.percentage(f5, cast_total) AS f5_pct,
  f6,
  dbo.percentage(f6, cast_total) AS f6_pct,
  f7,
  dbo.percentage(f7, cast_total) AS f7_pct,
  fh,
  dbo.percentage(fh, cast_total) AS fh_pct,
  frew - f4 - f5 - f6 - f7 - fh AS foth,
  dbo.percentage(frew - f4 - f5 - f6 - f7 - fh, cast_total) AS foth_pct
FROM pre_rework2
LEFT JOIN pre_rework1 ON pre_rework1.cast_date = pre_rework2.cast_date
LEFT JOIN fin_rework ON pre_rework2.cast_date = fin_rework.cast_date
UNION ALL
SELECT
  'total' AS cast_date,
  SUM(cast_total) AS cast_total,
  SUM(pre_insp) AS pre_insp,
  SUM(good1) AS good1,
  dbo.percentage(SUM(good1), SUM(cast_total)) AS good1_pct,
  SUM(pre1) AS pre1,
  dbo.percentage(SUM(pre1), SUM(cast_total)) AS pre1_pct,
  SUM(pre2) AS pre2,
  dbo.percentage(SUM(pre2), SUM(cast_total)) AS pre2_pct,
  SUM(pre3) AS pre3,
  dbo.percentage(SUM(pre3), SUM(cast_total)) AS pre3_pct,
  SUM(pre4) AS pre4,
  dbo.percentage(SUM(pre4), SUM(cast_total)) AS pre4_pct,
  SUM(fin1) AS fin1,
  dbo.percentage(SUM(fin1), SUM(cast_total)) AS fin1_pct,
  SUM(fin2) AS fin2,
  dbo.percentage(SUM(fin2), SUM(cast_total)) AS fin2_pct,
  SUM(fin3) AS fin3,
  dbo.percentage(SUM(fin3), SUM(cast_total)) AS fin3_pct,

  SUM(prew) AS prew,
  dbo.percentage(SUM(prew), SUM(cast_total)) AS prew_pct,
  SUM(p4) AS p4,
  dbo.percentage(SUM(p4), SUM(cast_total)) AS p4_pct,
  SUM(p5) AS p5,
  dbo.percentage(SUM(p5), SUM(cast_total)) AS p5_pct,
  SUM(p6) AS p6,
  dbo.percentage(SUM(p6), SUM(cast_total)) AS p6_pct,
  SUM(p7) AS p7,
  dbo.percentage(SUM(p7), SUM(cast_total)) AS p7_pct,
  SUM(ph) AS ph,
  dbo.percentage(SUM(ph), SUM(cast_total)) AS ph_pct,
  SUM(prew - p4 - p5 - p6 - p7 - ph) AS poth,
  dbo.percentage(SUM(prew - p4 - p5 - p6 - p7 - ph), SUM(cast_total)) AS poth_pct,

  SUM(frew) AS frew,
  dbo.percentage(SUM(frew), SUM(cast_total)) AS frew_pct,
  SUM(f4) AS f4,
  dbo.percentage(SUM(f4), SUM(cast_total)) AS f4_pct,
  SUM(f5) AS f5,
  dbo.percentage(SUM(f5), SUM(cast_total)) AS f5_pct,
  SUM(f6) AS f6,
  dbo.percentage(SUM(f6), SUM(cast_total)) AS f6_pct,
  SUM(f7) AS f7,
  dbo.percentage(SUM(f7), SUM(cast_total)) AS f7_pct,
  SUM(fh) AS fh,
  dbo.percentage(SUM(fh), SUM(cast_total)) AS fh_pct,
  SUM(frew - f4 - f5 - f6 - f7 - fh) AS foth,
  dbo.percentage(SUM(frew - f4 - f5 - f6 - f7 - fh), SUM(cast_total)) AS foth_pct
FROM pre_rework2
LEFT JOIN pre_rework1 ON pre_rework1.cast_date = pre_rework2.cast_date
LEFT JOIN fin_rework ON pre_rework2.cast_date = fin_rework.cast_date
) total
ORDER BY cast_date
