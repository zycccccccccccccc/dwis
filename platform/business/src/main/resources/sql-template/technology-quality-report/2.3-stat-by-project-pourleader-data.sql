SELECT
  ISNULL(pourleader_id, '') + '---' + CONVERT(VARCHAR, cast_date) AS stat_key,
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
  pourleader_id,
  cast_date,
  scrap_code
ORDER BY
  pourleader_id,
  cast_date,
  sconf_sum DESC
