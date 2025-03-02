WITH a AS (
SELECT
  tap_seq,
  CONVERT(VARCHAR, cast_date, 111) AS cast_date,
  SUBSTRING(CONVERT(VARCHAR, pour_d_t, 24), 1, 5) pour_d_t,
  CASE WHEN pre > 0 OR scrap_code <> '' THEN 1 ELSE 0 END AS cast_count,
  CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END AS scrap_count
FROM v_heat_ladle_pour_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
),

data AS (
SELECT
  tap_seq,
  cast_date,
  CONCAT(MIN(pour_d_t), '-', MAX(pour_d_t), ' ', dbo.percentage(SUM(scrap_count), SUM(cast_count)), '%') AS bb1
FROM a
GROUP BY
  cast_date,
  tap_seq
),

seq AS (
SELECT
  number AS tap_seq
FROM
  master..spt_values
WHERE type = 'p' AND number BETWEEN 1 AND 30
),

dt AS (
SELECT
  DISTINCT cast_date
FROM
  data
),

dt_seq AS (
  SELECT *
  FROM dt FULL JOIN seq ON 1=1
)

SELECT
  ds.tap_seq,
  ds.cast_date,
  bb1
FROM dt_seq ds
LEFT JOIN data d
ON ds.tap_seq = d.tap_seq AND ds.cast_date = d.cast_date
ORDER BY
  ds.tap_seq,
  ds.cast_date
