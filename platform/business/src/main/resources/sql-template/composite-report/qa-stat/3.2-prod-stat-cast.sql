WITH whl_cast AS (
SELECT
  CONVERT(VARCHAR(10), cast_date, 120) AS cast_date,
  COUNT(wheel_serial) AS whl_cast
FROM v_heat_ladle_pour_wheel
WHERE cast_date >= :beginDate
  AND cast_date < :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(whl_cast, 0) AS whl_cast
FROM whl_cast
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(whl_cast), 0)
FROM whl_cast
ORDER BY cast_date
