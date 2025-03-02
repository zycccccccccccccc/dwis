
WITH tl_ht AS (
SELECT
  cast_date,
  COUNT(1) * 2 AS tl_ht
FROM (
  SELECT
    CONVERT(VARCHAR(10), low_heat_out_date, 120) AS cast_date
  FROM heat
  WHERE low_heat_out_date >= :beginDate
    AND low_heat_out_date < :endDate
  <#if design??>
    AND design IN :design
  </#if>
) t
GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(tl_ht, 0) AS tl_ht
FROM tl_ht
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(tl_ht), 0)
FROM tl_ht
ORDER BY cast_date
