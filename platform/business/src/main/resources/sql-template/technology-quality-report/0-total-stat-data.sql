BEGIN
DECLARE @beginTime datetime
DECLARE @endTime datetime

SET @beginTime = (
SELECT MIN(record_created) AS beginTime
FROM v_heat_ladle_pour_wheel
WHERE cast_date = :beginDate
<#if beginTap??>
  AND tap_seq = :beginTap
</#if>
)

SET @endTime = (
SELECT MAX(record_created) AS endTime
FROM v_heat_ladle_pour_wheel
WHERE cast_date = :endDate
<#if endTap??>
  AND tap_seq = :endTap
</#if>
)

SELECT TOP(20)
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM v_heat_ladle_pour_wheel
WHERE record_created >= @beginTime
  AND record_created <= @endTime
<#if design??>
  AND design IN :design
</#if>
<#if ladleNo??>
  AND ladle_no = :ladleNo
</#if>
<#if testCode??>
  AND test_code IN :testCode
</#if>
<#if scrapCode??>
  AND scrap_code IN :scrapCode
</#if>
  AND scrap_code IS NOT NULL AND scrap_code <> ''
GROUP BY scrap_code
ORDER BY SUM (confirmed_scrap) DESC
END
