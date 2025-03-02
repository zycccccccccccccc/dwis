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

WITH stat AS (
SELECT
  CONVERT(VARCHAR, tap_seq) AS sub_title,
  COUNT(wheel_serial) AS cast_total,
  SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
  SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap_sum,
	SUM (confirmed_scrap) AS sconf_sum,
  SUM (finished) AS to_dock
FROM v_heat_ladle_wheel
WHERE record_created >= @beginTime
  AND record_created <= @endTime
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  tap_seq
)

SELECT 
  RIGHT('000' + sub_title, 4) AS stat_key,
  sub_title,
  cast_total,
  pre_insp,
  dbo.percentage(pre_insp, cast_total) AS pre_cast,
  scrap_sum,
  dbo.percentage(scrap_sum, pre_insp) AS scrap_pre,
  sconf_sum,
  dbo.percentage(sconf_sum, pre_insp) AS sconf_pre,
  to_dock,
  dbo.percentage(to_dock, pre_insp) AS to_dock_pre,
  dbo.percentage(sconf_sum, (to_dock + sconf_sum)) AS sconf_dock_and_sconf
FROM (
SELECT
  *
FROM stat
UNION ALL
SELECT
  'total' AS sub_title,
  SUM(cast_total) AS cast_total,
  SUM(pre_insp) AS pre_insp,
  SUM(scrap_sum) AS scrap_sum,
  SUM(sconf_sum) AS sconf_sum,
  SUM(to_dock) AS to_dock
FROM stat
) total
ORDER BY stat_key
END