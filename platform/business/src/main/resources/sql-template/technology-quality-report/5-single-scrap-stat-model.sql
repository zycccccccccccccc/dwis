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
)

SELECT * FROM (
  SELECT
    '' AS major_title,
    '' AS minor_title,
  	COUNT(1) AS cast_total,
    SUM(is_scrap) AS scrap_sum,
    SUM(is_pre) AS pre_insp,
    dbo.percentage(SUM(is_scrap), SUM(is_pre)) + '%' AS scrap_pre
  FROM t
  UNION ALL
  SELECT
    cast_date AS major_title,
    '' AS minor_title,
  	COUNT(1) AS cast_total,
    SUM(is_scrap) AS scrap_sum,
    SUM(is_pre) AS pre_insp,
    dbo.percentage(SUM(is_scrap), SUM(is_pre)) + '%' AS scrap_pre
  FROM t
  GROUP BY
    cast_date
  UNION ALL
  SELECT
    cast_date AS major_title,
    model_id AS minor_title,
  	COUNT(1) AS cast_total,
    SUM(is_scrap) AS scrap_sum,
    SUM(is_pre) AS pre_insp,
    dbo.percentage(SUM(is_scrap), SUM(is_pre)) + '%' AS scrap_pre
  FROM t
  GROUP BY
    cast_date,
    model_id
) u
ORDER BY major_title, minor_title