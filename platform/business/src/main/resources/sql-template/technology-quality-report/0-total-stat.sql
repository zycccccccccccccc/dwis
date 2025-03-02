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

SELECT
  cast_total,
  pre_insp,
  dbo.percentage(pre_insp, cast_total) AS pre_cast,
  scrap_sum,
  dbo.percentage(scrap_sum, pre_insp) AS scrap_pre,
  sconf_sum,
  dbo.percentage(sconf_sum, pre_insp) AS sconf_pre,
  to_dock,
  dbo.percentage(to_dock, pre_insp) AS to_dock_pre,
  dbo.percentage(to_dock, (to_dock + sconf_sum)) AS to_dock_dock_and_sconf,
  dbo.percentage(scrap_sum, (to_dock + sconf_sum)) AS scrap_dock_and_sconf,
  dbo.percentage(sconf_sum, (to_dock + sconf_sum)) AS sconf_dock_and_sconf
FROM (
SELECT
  COUNT(wheel_serial) AS cast_total,
  ISNULL(SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END), 0) AS pre_insp,
  ISNULL(SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END), 0) AS scrap_sum,
  ISNULL(SUM (confirmed_scrap), 0) AS sconf_sum,
  ISNULL(SUM (finished), 0) AS to_dock
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
) a
END
