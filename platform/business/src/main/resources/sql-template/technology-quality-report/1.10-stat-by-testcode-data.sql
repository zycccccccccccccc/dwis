SELECT
  CONVERT(VARCHAR, cast_date) + '---' + test_code + '---' + ISNULL(pourleader_id, '') AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND test_code IN :testCode
  AND scrap_code IS NOT NULL AND scrap_code <> ''
GROUP BY
  cast_date,
  test_code,
  pourleader_id,
  scrap_code
ORDER BY
  cast_date,
  test_code,
  pourleader_id,
  sconf_sum DESC
