
WITH shipped AS (
SELECT
  cast_date,
  COUNT(1) AS shipped
FROM (
  SELECT
    CONVERT(VARCHAR(10), shipped_date, 120) AS cast_date
  FROM wheel_record w
  JOIN train_no t ON w.shipped_no = t.shipped_no
  WHERE shipped_date >= :beginDate
    AND shipped_date < :endDate
  <#if design??>
    AND design IN :design
  </#if>
) t
GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(shipped, 0) AS shipped
FROM shipped
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(shipped), 0)
FROM shipped
ORDER BY cast_date
