WITH t AS (
SELECT
  CONVERT(varchar(100), cast_date, 111) AS cast_date,
  SUBSTRING(wheel_serial, 5, 1) AS xh,
  model_id,
  CASE WHEN scrap_code IN :scrapCode THEN 1 ELSE 0 END AS is_scrap,
  CASE WHEN pre <> 0 OR scrap_code <> '' THEN 1 ELSE 0 END AS is_pre
FROM
  v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
), d AS (
 SELECT
   CONVERT (VARCHAR (100),dateadd(d, number, :beginDate), 111) AS cast_date
 FROM master..spt_values n
 WHERE n.type = 'p'
   AND n.number <= DATEDIFF(d, :beginDate, :endDate)
 )

SELECT
  d.cast_date,
  model_id,
  SUM(is_scrap) AS scrap
FROM t RIGHT JOIN d ON t.cast_date = d.cast_date
GROUP BY
  d.cast_date,
  model_id
ORDER BY
  d.cast_date,
  model_id
