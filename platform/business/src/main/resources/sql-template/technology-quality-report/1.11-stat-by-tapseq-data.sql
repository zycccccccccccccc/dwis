BEGIN
DECLARE @beginTime datetime
DECLARE @endTime datetime

SET @beginTime = (
SELECT MIN(record_created) AS beginTime
FROM v_heat_ladle_wheel
WHERE cast_date = :beginDate
<#if beginTap??>
  AND tap_seq = :beginTap
</#if>
)

SET @endTime = (
SELECT MAX(record_created) AS endTime
FROM v_heat_ladle_wheel
WHERE cast_date = :endDate
<#if endTap??>
  AND tap_seq = :endTap
</#if>
);

SELECT
  CONVERT(VARCHAR, cast_date) + '---' + RIGHT('000' + CONVERT(VARCHAR, tap_seq), 4) AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM v_heat_ladle_wheel
WHERE record_created >= @beginTime
  AND record_created <= @endTime
<#if design??>
  AND design IN :design
</#if>
  AND scrap_code IS NOT NULL AND scrap_code <> ''
GROUP BY
  cast_date,
  tap_seq,
  scrap_code
ORDER BY cast_date, sconf_sum DESC
END
