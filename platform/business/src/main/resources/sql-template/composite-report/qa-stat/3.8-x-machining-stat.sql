WITH t1 AS (
SELECT
  design,
  CONVERT(varchar(100), cast_date, 111) AS cast_date,
  COUNT(1) AS cnt,
  SUM(CASE WHEN pre > 0 THEN 1 ELSE 0 END) AS machining,
  SUM(confirmed_scrap) AS scrap
FROM
  v_heat_ladle_wheel
WHERE xray_req = 1
  AND confirmed_scrap = 0
  AND finished = 0
GROUP BY
  design,
  cast_date
)

SELECT * FROM (
SELECT
  design,
  cast_date,
  cnt,
  machining,
  scrap
FROM t1
UNION ALL
SELECT
  design,
  'total',
  SUM(cnt) AS cnt,
  SUM(machining) AS machining,
  SUM(scrap) AS scrap
FROM t1
GROUP BY
  design
UNION ALL
SELECT
  'total',
  'total',
  SUM(cnt) AS cnt,
  SUM(machining) AS machining,
  SUM(scrap) AS scrap
FROM t1
) t
ORDER BY
  design,
  cast_date
