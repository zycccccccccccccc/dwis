WITH t1 AS (
SELECT
  design,
  CONVERT(varchar(100), stock_date, 111) + ' ' + check_code AS cast_date,
  SUM (finished) AS dock,
  SUM(CASE WHEN tape_size >= 844 AND wheel_w = 137 AND finished = 1 THEN 1 ELSE 0 END) AS tape_over_840,
  SUM(CASE WHEN balance_s = 'E3' THEN 1 ELSE 0 END) AS e3,
  SUM(CASE WHEN wheel_w = 135 THEN 1 ELSE 0 END) AS wheel_135
FROM wheel_record
WHERE stock_date >= :beginDate
  AND stock_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  design,
  CONVERT(varchar(100), stock_date, 111) + ' ' + check_code
),

t2 AS (
SELECT
  design,
  cast_date,
  dock,
  tape_over_840,
  e3,
  dock - tape_over_840 AS tape_other,
  wheel_135
FROM t1
)

SELECT * FROM (
  SELECT *
  FROM t2
  UNION ALL
  SELECT
    design,
    'total' AS cast_date,
    SUM(dock) AS dock,
    SUM(tape_over_840) AS tape_over_840,
    SUM(e3) AS e3,
    SUM(tape_other) AS tape_other,
    SUM(wheel_135) AS wheel_135
  FROM t2
  GROUP BY
    design
  UNION ALL
  SELECT
    'total' AS design,
    'total' AS cast_date,
    SUM(dock) AS dock,
    SUM(tape_over_840) AS tape_over_840,
    SUM(e3) AS e3,
    SUM(tape_other) AS tape_other,
    SUM(wheel_135) AS wheel_135
  FROM t2
) t
ORDER BY
  design,
  cast_date
