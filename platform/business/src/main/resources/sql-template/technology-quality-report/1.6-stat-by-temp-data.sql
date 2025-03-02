SELECT
  CONVERT(VARCHAR, cast_date) + '---' + temp AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM (
SELECT
  cast_date,
  wheel_serial,
  CASE
    WHEN ladle_temp <= 1550 THEN '1550以下'
    WHEN ladle_temp >= 1551 AND ladle_temp <= 1560 THEN '1551-1560'
    WHEN ladle_temp >= 1561 AND ladle_temp <= 1570 THEN '1561-1570'
    WHEN ladle_temp >= 1571 AND ladle_temp <= 1580 THEN '1571-1580'
    WHEN ladle_temp >= 1581 AND ladle_temp <= 1595 THEN '1581-1595'
    ELSE '1596以上'
  END AS temp,
  confirmed_scrap,
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
  cast_date,
	temp,
	scrap_code
ORDER BY
  cast_date,
	temp,
	sconf_sum DESC


