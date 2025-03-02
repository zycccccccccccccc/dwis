WITH a AS (
SELECT
  CONVERT(VARCHAR, cast_date) AS cast_date,
  COUNT(wheel_serial) AS cast_total,
  SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
  SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap_sum,
	SUM (confirmed_scrap) AS sconf_sum,
  SUM (finished) AS to_dock
FROM v_heat_ladle_wheel
WHERE	cast_date >= :beginDate
	AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY cast_date
)

SELECT
  SUBSTRING(cast_date, 1, 7) AS cast_date,
  SUM (sconf_sum) AS sconf_sum,
  dbo.percentage1(SUM (sconf_sum), SUM (to_dock + sconf_sum) * 100, 4) AS sconf_dock_and_sconf
FROM a
GROUP BY SUBSTRING(cast_date, 1, 7)
ORDER BY cast_date
