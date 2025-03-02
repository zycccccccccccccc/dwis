WITH t1 AS (
SELECT
  SUBSTRING(CONVERT(varchar(100), last_barcode, 23), 1, 7) AS dd,
  SUM (finished) AS dock,
  SUM(CASE WHEN balance_s = 'E3' AND finished = 1 THEN 1 ELSE 0 END) AS e3,
  SUM(CASE WHEN tape_size >= 844 AND wheel_w = 137 AND finished = 1 THEN 1 ELSE 0 END) AS tape_over_840,
  SUM(CASE WHEN (tape_size < 844 OR wheel_w < 137) AND finished = 1 THEN 1 ELSE 0 END) AS tape_under_840
FROM wheel_record
WHERE last_barcode >= :beginDate
  AND last_barcode < :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  SUBSTRING(CONVERT(varchar(100), last_barcode, 23), 1, 7)
),

t2 AS (
SELECT
  SUBSTRING(CONVERT(varchar(100), scrap_date, 23), 1, 7) AS dd,
  COUNT(1) AS confirmed_scrap
FROM wheel_record
WHERE scrap_date >= :beginDate
  AND scrap_date < :endDate
<#if design??>
  AND design IN :design
</#if>
  AND confirmed_scrap = 1
GROUP BY
  SUBSTRING(CONVERT(varchar(100), scrap_date, 23), 1, 7)
),

t3 AS (
SELECT
  SUBSTRING(CONVERT(varchar(100), stock_date, 23), 1, 7) AS dd,
  COUNT(1) AS stock
FROM wheel_record
WHERE stock_date >= :beginDate
  AND stock_date < :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  SUBSTRING(CONVERT(varchar(100), stock_date, 23), 1, 7)
),

t4 AS (
SELECT
  SUBSTRING(CONVERT(varchar(100), cast_date, 23), 1, 7) AS dd,
  COUNT(1) AS cnt,
  SUM(CASE WHEN pre <> 0 OR scrap_code <> '' THEN 1 ELSE 0 END) AS pres,
  SUM(CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date < :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  SUBSTRING(CONVERT(varchar(100), cast_date, 23), 1, 7)
),

t5 AS (
SELECT
  SUBSTRING(CONVERT(varchar(100), shipped_date, 23), 1, 7) AS dd,
  SUM (w.finished) AS dock,
  SUM(CASE WHEN w.tape_size >= 844 AND w.wheel_w = 137 AND w.finished = 1 THEN 1 ELSE 0 END) AS tape_over_840,
  SUM(CASE WHEN (w.tape_size < 844 OR w.wheel_w < 137) AND w.finished = 1 THEN 1 ELSE 0 END) AS tape_under_840,
  SUM(CASE WHEN balance_s = 'E3' THEN 1 ELSE 0 END) AS e3
FROM train_no t
JOIN wheel_record w ON t.shipped_no = w.shipped_no
WHERE t.shipped_date >= :beginDate
  AND t.shipped_date < :endDate
<#if design??>
  AND design IN :design
</#if>
  AND w.shipped_no IS NOT NULL
GROUP BY
  SUBSTRING(CONVERT(varchar(100), shipped_date, 23), 1, 7)
),

t0 AS (
SELECT
  t1.dd,
  ISNULL(t4.cnt, 0) AS cnt,
  ISNULL(t4.pres, 0) AS pres,
  ISNULL(t4.scrap, 0) AS scrap,
  ISNULL(t1.dock, 0) AS finish_dock,
  ISNULL(t1.tape_over_840, 0) AS finish_tape_over_840,
  ISNULL(t1.tape_under_840, 0) AS finish_tape_under_840,
  ISNULL(t1.e3, 0) AS finish_e3,
  ISNULL(t3.stock, 0) AS stock,
  ISNULL(t5.dock, 0) AS ship_dock,
  ISNULL(t5.tape_over_840, 0) AS ship_tape_over_840,
  ISNULL(t5.tape_under_840, 0) AS ship_tape_under_840,
  ISNULL(t5.e3, 0) AS ship_e3,
  ISNULL(t2.confirmed_scrap, 0) AS confirmed_scrap
FROM t1
LEFT JOIN t2 ON t1.dd = t2.dd
LEFT JOIN t3 ON t1.dd = t3.dd
LEFT JOIN t4 ON t1.dd = t4.dd
LEFT JOIN t5 ON t1.dd = t5.dd
)

SELECT * FROM (
  SELECT
    dd,
    cnt,
    pres,
    scrap,
    dbo.percentage(t0.scrap, t0.pres) + '%' AS pres_scrap,
    finish_dock,
    finish_tape_over_840,
    finish_tape_under_840,
    finish_e3,
    stock,
    ship_dock,
    ship_tape_over_840,
    ship_tape_under_840,
    ship_e3,
    confirmed_scrap
  FROM t0
  UNION ALL
  SELECT
    'total' AS dd,
    SUM(t0.cnt) AS cnt,
    SUM(t0.pres) AS pres,
    SUM(t0.scrap) AS scrap,
    dbo.percentage(SUM(scrap), SUM(pres)) + '%' AS pres_scrap,
    SUM(t0.finish_dock) AS finish_dock,
    SUM(t0.finish_tape_over_840) AS finish_tape_over_840,
    SUM(t0.finish_tape_under_840) AS finish_tape_under_840,
    SUM(t0.finish_e3) AS finish_e3,
    SUM(t0.stock) AS stock,
    SUM(t0.ship_dock) AS ship_dock,
    SUM(t0.ship_tape_over_840) AS ship_tape_over_840,
    SUM(t0.ship_tape_under_840) AS ship_tape_under_840,
    SUM(t0.ship_e3) AS ship_e3,
    SUM(t0.confirmed_scrap) AS confirmed_scrap
  FROM t0
) t
ORDER BY
  dd
