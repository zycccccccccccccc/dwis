
WITH to_dock AS (
SELECT
  cast_date,
  COUNT(1) AS to_dock
FROM (
  SELECT
    CONVERT(VARCHAR(10), stock_date, 120) AS cast_date
  FROM wheel_record
  WHERE stock_date >= :beginDate
    AND stock_date < :endDate
    AND finished = 1
  <#if design??>
    AND design IN :design
  </#if>
) t
GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(to_dock, 0) AS to_dock
FROM to_dock
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(to_dock), 0)
FROM to_dock
ORDER BY cast_date
