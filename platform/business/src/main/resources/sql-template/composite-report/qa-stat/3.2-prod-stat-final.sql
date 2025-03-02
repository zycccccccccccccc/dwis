
WITH final_insp AS (
SELECT
  cast_date,
  COUNT(1) AS final_insp
FROM (
  SELECT
    CONVERT(VARCHAR(10), last_final, 120) AS cast_date
  FROM wheel_record
  WHERE last_final >= :beginDate
    AND last_final < :endDate
  <#if design??>
    AND design IN :design
  </#if>
  ) t
  GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(final_insp, 0) AS final_insp
FROM final_insp
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(final_insp), 0)
FROM final_insp
ORDER BY cast_date
