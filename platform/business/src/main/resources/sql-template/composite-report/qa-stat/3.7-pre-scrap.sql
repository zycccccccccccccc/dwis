WITH t0 AS (
SELECT DISTINCT
  CONVERT(varchar(100), ope_d_t, 111) AS cast_date,
  w.design,
  w.scrap_code,
  w.wheel_serial
FROM wheel_record w
LEFT JOIN pre_check_record p ON p.wheel_serial = w.wheel_serial
WHERE ope_d_t >= :beginDate
  AND ope_d_t <= :endDate
<#if design??>
  AND w.design IN :design
</#if>
),


t1 AS (
SELECT
  cast_date,
  design,
  COUNT(1) AS pre,
  SUM(CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap
FROM t0
GROUP BY
  cast_date,
  design
)

SELECT * FROM (
SELECT
  cast_date,
  design,
  pre,
  scrap,
  dbo.percentage(scrap, pre) AS pct
FROM t1
UNION ALL
SELECT
  cast_date,
  'total' AS design,
  SUM(pre) AS pre,
  SUM(scrap) AS scrap,
  dbo.percentage(SUM(scrap), SUM(pre)) AS pct
FROM t1
GROUP BY cast_date
UNION ALL
SELECT
  'total' AS cast_date,
  'total' AS design,
  SUM(pre) AS pre,
  SUM(scrap) AS scrap,
  dbo.percentage(SUM(scrap), SUM(pre)) AS pct
FROM t1
) t
ORDER BY cast_date, design
