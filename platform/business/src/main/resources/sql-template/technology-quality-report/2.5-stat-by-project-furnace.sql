WITH stat AS (
SELECT
  ISNULL(furnace_id, '') AS minor_title,
  CONVERT(VARCHAR, cast_date) AS sub_title,
  COUNT(wheel_serial) AS cast_total,
  SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
  SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap_sum,
	SUM (confirmed_scrap) AS sconf_sum,
  SUM (finished) AS to_dock
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  furnace_id,
  cast_date
)

SELECT 
  minor_title + '---' + sub_title AS stat_key,
  minor_title,
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
	minor_title,
  'total' AS cast_date,
  SUM(cast_total) AS cast_total,
  SUM(pre_insp) AS pre_insp,
  SUM(scrap_sum) AS scrap_sum,
  SUM(sconf_sum) AS sconf_sum,
  SUM(to_dock) AS to_dock
FROM stat
GROUP BY minor_title
) total
ORDER BY minor_title, sub_title