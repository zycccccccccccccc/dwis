SELECT
  temp + '---' + CONVERT(VARCHAR, cast_date) AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM (
SELECT
  CASE
    WHEN ladle_temp <= 1550 THEN '1550以下'
    WHEN ladle_temp >= 1551 AND ladle_temp <= 1560 THEN '1551-1560'
    WHEN ladle_temp >= 1561 AND ladle_temp <= 1570 THEN '1561-1570'
    WHEN ladle_temp >= 1571 AND ladle_temp <= 1580 THEN '1571-1580'
    WHEN ladle_temp >= 1581 AND ladle_temp <= 1595 THEN '1581-1595'
    ELSE '1596以上'
  END AS temp,
  confirmed_scrap,
  cast_date,
  wheel_serial,
  scrap_code
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND scrap_code IS NOT NULL AND scrap_code <> ''
) a
GROUP BY
	temp,
  cast_date,
	scrap_code
ORDER BY
	temp,
  cast_date,
  sconf_sum DESC
