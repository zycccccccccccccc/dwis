WITH t1 AS (
SELECT
  scrap_date,
  COUNT(wheel_serial) AS sconf_sum
FROM wheel_record
WHERE scrap_date >= :beginDate
  AND scrap_date <= :endDate
  AND confirmed_scrap = 1
<#if design??>
  AND design IN :design
</#if>
GROUP BY scrap_date
),

t2 AS (
SELECT
  stock_date,
  COUNT(wheel_serial) AS to_stock
FROM wheel_record
WHERE stock_date >= :beginDate
  AND stock_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY stock_date
),

t3 AS (
SELECT
  scrap_date AS product_date
FROM t1
UNION
SELECT
  stock_date AS product_date
FROM t2
)

SELECT
  t3.product_date,
  dbo.percentage( t1.sconf_sum, (t1.sconf_sum + t2.to_stock)) AS sconf_stock_and_sconf,
  t1.sconf_sum,
  t2.to_stock
FROM t3
LEFT JOIN t1 ON t1.scrap_date = t3.product_date
LEFT JOIN t2 ON t2.stock_date = t3.product_date
ORDER BY t3.product_date


