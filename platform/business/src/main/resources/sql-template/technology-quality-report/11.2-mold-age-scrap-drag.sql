WITH t1 AS (
SELECT
  s.jet_time,
  p.wheel_serial,
  ROUND((DATEPART(SECOND, s.mold_age) + DATEPART(MINUTE, s.mold_age) * 60 + DATEPART(HOUR, s.mold_age) * 3600)/60.0, 0) AS mold_age
FROM
  sand_jet_record s
  INNER JOIN pour_record p ON (s.wheel_serial = p.wheel_serial)
WHERE
  s.status <> 1
  AND s.mold_age IS NOT NULL
  AND s.graphite = p.drag_no
  AND (s.wheel_serial IS NOT NULL OR s.wheel_serial != '')
  AND (s.line_no = 1 OR s.line_no = 2)
  AND CONVERT(VARCHAR(10),s.jet_time,120) >= :beginDate
  AND CONVERT(VARCHAR(10),s.jet_time,120) <= :endDate
  <#if design??>
     AND p.design IN :design
  </#if>
),

t2 AS (
SELECT
  t1.wheel_serial,
  MAX(t1.jet_time) AS jet_time
FROM
  t1
GROUP BY
  t1.wheel_serial
),

t3 AS (
SELECT
  CONVERT(VARCHAR(10),t1.jet_time,120) AS jet_date,
  t1.wheel_serial,
  mold_age
FROM
  t1
  INNER JOIN t2 ON (t1.wheel_serial = t2.wheel_serial AND t2.jet_time = t1.jet_time)
),

t4 AS (
SELECT
  t3.jet_date,
  w.wheel_serial,
  w.design,
  (CASE WHEN t3.mold_age < 20 THEN '1'
	    WHEN t3.mold_age >= 20 AND t3.mold_age < 40 THEN '2'
		WHEN t3.mold_age >= 40 AND t3.mold_age < 60 THEN '3'
		WHEN t3.mold_age >= 60 AND t3.mold_age <= 90 THEN '4'
		WHEN t3.mold_age > 90 THEN '5' END) AS 'mold_age',
  w.scrap_code,
  w.confirmed_scrap
FROM
  wheel_record w
  INNER JOIN t3 ON w.wheel_serial = t3.wheel_serial
),

t5 AS (
SELECT
  design,
  jet_date,
  mold_age,
  COUNT(wheel_serial) AS wheel_sum,
  SUM(CASE WHEN confirmed_scrap = 1 THEN 1 ELSE 0 END) AS scrap_sum,
  SUM(CASE WHEN confirmed_scrap = 1 AND scrap_code IN ('9AS', '9B', '9CS', '88S', '23S') THEN 1 ELSE 0 END) AS stat_scrap_sum,
  SUM (CASE WHEN scrap_code= '9AS' THEN 1 ELSE 0 END) AS scrap_9AS,
  SUM (CASE WHEN scrap_code= '9B' THEN 1 ELSE 0 END) AS scrap_9B,
  SUM (CASE WHEN scrap_code= '9CS' THEN 1 ELSE 0 END) AS scrap_9CS,
  SUM (CASE WHEN scrap_code= '88S' THEN 1 ELSE 0 END) AS scrap_88S,
  SUM (CASE WHEN scrap_code= '23S' THEN 1 ELSE 0 END) AS scrap_23S
FROM
  t4
GROUP BY
  design,
  jet_date,
  mold_age
),

t6 AS (
SELECT
  t5.*
FROM
  t5
UNION ALL
SELECT
  design,
  jet_date,
  'total' AS mold_age,
  SUM(wheel_sum) AS wheel_sum,
  SUM(scrap_sum) AS scrap_sum,
  SUM(stat_scrap_sum) AS stat_scrap_sum,
  SUM (scrap_9AS) AS scrap_9AS,
  SUM (scrap_9B) AS scrap_9B,
  SUM (scrap_9CS) AS scrap_9CS,
  SUM (scrap_88S) AS scrap_88S,
  SUM (scrap_23S) AS scrap_23S
FROM
  t5
GROUP BY
  design,
  jet_date
UNION ALL
SELECT
  design,
  'total' AS jet_date,
  'total' AS mold_age,
  SUM(wheel_sum) AS wheel_sum,
  SUM(scrap_sum) AS scrap_sum,
  SUM(stat_scrap_sum) AS stat_scrap_sum,
  SUM (scrap_9AS) AS scrap_9AS,
  SUM (scrap_9B) AS scrap_9B,
  SUM (scrap_9CS) AS scrap_9CS,
  SUM (scrap_88S) AS scrap_88S,
  SUM (scrap_23S) AS scrap_23S
FROM
  t5
GROUP BY
  design
UNION ALL
SELECT
  'total' AS design,
  'total' AS jet_date,
  'total' AS mold_age,
  SUM(wheel_sum) AS wheel_sum,
  SUM(scrap_sum) AS scrap_sum,
  SUM(stat_scrap_sum) AS stat_scrap_sum,
  SUM (scrap_9AS) AS scrap_9AS,
  SUM (scrap_9B) AS scrap_9B,
  SUM (scrap_9CS) AS scrap_9CS,
  SUM (scrap_88S) AS scrap_88S,
  SUM (scrap_23S) AS scrap_23S
FROM
  t5
)

SELECT
  *
FROM (
  SELECT
	t6.*,
	dbo.percentage2(scrap_sum, wheel_sum) AS scrap_rate,
	dbo.percentage2(stat_scrap_sum, scrap_sum) AS stat_scrap_rate,
	dbo.percentage2(scrap_9AS, scrap_sum) AS scrap_9AS_rate,
	dbo.percentage2(scrap_9B, scrap_sum) AS scrap_9B_rate,
	dbo.percentage2(scrap_9CS, scrap_sum) AS scrap_9CS_rate,
	dbo.percentage2(scrap_88S, scrap_sum) AS scrap_88S_rate,
	dbo.percentage2(scrap_23S, scrap_sum) AS scrap_23S_rate
  FROM
	t6
) t
ORDER BY
  design,
  jet_date,
  mold_age