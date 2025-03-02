WITH t1 AS (
SELECT
  scrap_code,
  COUNT(1) AS scrap,
  SUM(confirmed_scrap) AS confirmed_scrap
FROM
  v_heat_ladle_wheel
WHERE scrap_date >= :beginDate
  AND scrap_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY scrap_code
),

 t2 AS (
 SELECT
   SUM(scrap) AS scrap_sum
 FROM
   t1
 )

SELECT * FROM (
SELECT
  scrap_code,
  scrap,
  dbo.percentage(scrap, t2.scrap_sum) + '%' AS scrap_pct,
  confirmed_scrap
FROM t1 JOIN t2 ON 1 = 1
UNION ALL
SELECT
  'total' AS scrap_code,
  SUM(scrap) AS scrap,
  '100%' AS scrap_pct,
  SUM(confirmed_scrap) AS confirmed_scrap
FROM t1
) t
ORDER BY scrap DESC
