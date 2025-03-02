
WITH pre_insp AS (
SELECT
  cast_date,
  COUNT(1) AS pre_insp
FROM (
  SELECT
    CONVERT(VARCHAR(10), last_pre, 120) AS cast_date
  FROM wheel_record
  WHERE last_pre >= :beginDate
    AND last_pre < :endDate
  <#if design??>
    AND design IN :design
  </#if>
 ) t
  GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(pre_insp, 0) AS pre_insp
FROM pre_insp
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(pre_insp), 0)
FROM pre_insp
ORDER BY cast_date
