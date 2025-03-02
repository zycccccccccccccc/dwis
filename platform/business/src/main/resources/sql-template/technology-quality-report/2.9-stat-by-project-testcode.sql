SELECT
  COUNT(wheel_serial) AS cast_total,
  ISNULL(SUM(CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END), 0) AS pre_insp,
  ISNULL(SUM(finished), 0) AS to_dock
FROM v_heat_ladle_wheel
WHERE	cast_date >= :beginDate
	AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND test_code IN :testCode
  AND scrap_code IS NOT NULL AND scrap_code <> ''
