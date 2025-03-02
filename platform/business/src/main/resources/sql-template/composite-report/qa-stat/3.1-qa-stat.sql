WITH t1 AS (
SELECT
  COUNT(wheel_serial) AS cast_total,
  ISNULL(SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END), 0) AS pre_insp,
  ISNULL(SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END), 0) AS scrap_sum,
  ISNULL(SUM (confirmed_scrap), 0) AS sconf_sum,
  ISNULL(SUM (finished), 0) AS to_dock
FROM v_heat_ladle_pour_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
)

SELECT
  t1.cast_total,
  t1.pre_insp,
  dbo.percentage(t1.pre_insp, t1.cast_total) AS pre_cast,
  t1.scrap_sum,
  dbo.percentage(t1.scrap_sum, t1.pre_insp) AS scrap_pre,
  t1.sconf_sum,
  dbo.percentage(t1.sconf_sum, t1.pre_insp) AS sconf_pre,
  t1.to_dock,
  dbo.percentage(t1.to_dock, t1.pre_insp) AS to_dock_pre,
  dbo.percentage(t1.to_dock, (t1.to_dock + t1.sconf_sum)) AS to_dock_dock_and_sconf,
  dbo.percentage(t1.scrap_sum, (t1.to_dock + t1.sconf_sum)) AS scrap_dock_and_sconf,
  dbo.percentage(t1.sconf_sum, (t1.to_dock + t1.sconf_sum)) AS sconf_dock_and_sconf,
  dbo.percentage(t1.scrap_sum, (t1.to_dock + t1.scrap_sum)) AS scrap_dock_and_scrap,
  dbo.percentage(t1.to_dock, (t1.to_dock + t1.scrap_sum)) AS to_dock_dock_and_scrap
FROM t1