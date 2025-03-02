SELECT
  CONVERT(VARCHAR, cast_date) + '---' + CONVERT(VARCHAR, ladle_seq) AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND scrap_code IS NOT NULL AND scrap_code <> ''
GROUP BY
  cast_date,
  ladle_seq,
  scrap_code
ORDER BY cast_date, sconf_sum DESC
