
WITH tl_in_pit AS (
SELECT
  cast_date,
  COUNT(1) AS tl_in_pit
FROM (
  SELECT
    CONVERT(VARCHAR(10), in_pit_date_time, 120) AS cast_date
  FROM v_heat_ladle_pour_wheel
  WHERE in_pit_date_time >= :beginDate
    AND in_pit_date_time < :endDate
  <#if design??>
    AND design IN :design
  </#if>
) t
GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(tl_in_pit, 0) AS tl_in_pit
FROM tl_in_pit
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(tl_in_pit), 0)
FROM tl_in_pit
ORDER BY cast_date
