SELECT TOP(20)
  scrap_code,
  COUNT(1) AS cnt,
  SUM (confirmed_scrap) AS sconf_sum
FROM v_heat_ladle_pour_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND scrap_code IS NOT NULL AND scrap_code <> ''
GROUP BY scrap_code
ORDER BY cnt DESC
