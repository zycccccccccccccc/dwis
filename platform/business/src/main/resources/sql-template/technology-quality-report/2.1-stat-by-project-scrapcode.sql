SELECT
  ISNULL(SUM(CASE WHEN scrap_code IS NOT NULL AND scrap_code <> '' THEN 1 ELSE 0 END), 0) AS cast_total,
  SUM (confirmed_scrap) AS pre_insp
FROM v_heat_ladle_wheel
WHERE	cast_date >= :beginDate
	AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
<#if scrapCode??>
  AND scrap_code IN :scrapCode
</#if>
