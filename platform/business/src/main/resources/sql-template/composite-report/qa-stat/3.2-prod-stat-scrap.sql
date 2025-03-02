
WITH fin_scrap AS (
SELECT
  cast_date,
  COUNT(1) AS fin_scrap
FROM (
  SELECT
    CONVERT(VARCHAR(10), scrap_date, 120) AS cast_date
  FROM wheel_record
  WHERE scrap_date >= :beginDate
    AND scrap_date < :endDate
    AND confirmed_scrap = 1
  <#if design??>
    AND design IN :design
  </#if>
) t
GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(fin_scrap, 0) AS fin_scrap
FROM fin_scrap
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(fin_scrap), 0)
FROM fin_scrap
ORDER BY cast_date
