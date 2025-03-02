WITH t1 AS (
SELECT
  CONVERT(varchar(100), cast_date, 111) AS cast_date,
  design,
  wheel_serial
FROM
  v_heat_ladle_wheel
WHERE finished = 0
  AND confirmed_scrap = 0
)

SELECT * FROM (
SELECT
  cast_date,
  design,
  COUNT(1) AS cnt
FROM t1
GROUP BY
  cast_date,
  design
UNION ALL
SELECT
  cast_date,
  'total',
  COUNT(1) AS cnt
FROM t1
GROUP BY
  cast_date
) t
ORDER BY
  cast_date
