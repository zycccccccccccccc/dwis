WITH t1 AS (
SELECT
  design,
  CONVERT(varchar(100), cast_date, 111) AS cast_date,
  COUNT(1) AS cnt,
  SUM(CASE WHEN pre > 0 OR scrap_code <> '' THEN 1 ELSE 0 END) AS pres,
  SUM(CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap,
  SUM (finished) AS dock,
  SUM(CASE WHEN tape_size >= 844 AND wheel_w = 137 AND finished = 1 THEN 1 ELSE 0 END) AS big_tape,
  SUM(CASE WHEN (tape_size < 844 OR wheel_w < 137) AND finished = 1 THEN 1 ELSE 0 END) AS small_tape,
  SUM(CASE WHEN balance_s = 'E3' AND finished = 1 THEN 1 ELSE 0 END) AS e3,
  SUM(confirmed_scrap) AS confirmed_scrap
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  design,
  cast_date
),

t2 AS (
SELECT
  design,
  cast_date,
  cnt,
  pres,
  scrap,
  dbo.percentage(scrap, pres) + '%' AS pres_scrap,
  dock,
  big_tape,
  small_tape,
  e3,
  confirmed_scrap,
  dbo.percentage(confirmed_scrap, dock + confirmed_scrap) + '%' AS sconf_dock_and_sconf,
  cnt - confirmed_scrap - dock AS stock
FROM t1
)

SELECT * FROM (
  SELECT *
  FROM t2
  UNION ALL
  SELECT
    design,
    'total' AS cast_date,
    SUM(cnt) AS cnt,
    SUM(pres) AS pres,
    SUM(scrap) AS scrap,
    dbo.percentage(SUM(scrap), SUM(pres)) + '%' AS pres_scrap,
    SUM(dock) AS dock,
    SUM(big_tape) AS big_tape,
    SUM(small_tape) AS small_tape,
    SUM(e3) AS e3,
    SUM(confirmed_scrap) AS confirmed_scrap,
    dbo.percentage(SUM(confirmed_scrap), SUM(dock) + SUM(confirmed_scrap)) + '%' AS sconf_dock_and_sconf,
    SUM(stock) AS stock
  FROM t2
  GROUP BY
    design
  UNION ALL
  SELECT
    'total' AS design,
    'total' AS cast_date,
    SUM(cnt) AS cnt,
    SUM(pres) AS pres,
    SUM(scrap) AS scrap,
    dbo.percentage(SUM(scrap), SUM(pres)) + '%' AS pres_scrap,
    SUM(dock) AS dock,
    SUM(big_tape) AS big_tape,
    SUM(small_tape) AS small_tape,
    SUM(e3) AS e3,
    SUM(confirmed_scrap) AS confirmed_scrap,
    dbo.percentage(SUM(confirmed_scrap), SUM(dock) + SUM(confirmed_scrap)) + '%' AS sconf_dock_and_sconf,
    SUM(stock) AS stock
  FROM t2
) t
ORDER BY
  design,
  cast_date
