SELECT
  CONVERT(VARCHAR, cast_date) + '---' + xh AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM (
SELECT
  cast_date,
  SUBSTRING(wheel_serial, 5, 1) AS xh,
  confirmed_scrap,
  scrap_code
FROM v_heat_ladle_wheel
WHERE	cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND scrap_code IS NOT NULL AND scrap_code <> ''
) a
GROUP BY
  cast_date,
  xh,
  scrap_code
ORDER BY cast_date, sconf_sum DESC