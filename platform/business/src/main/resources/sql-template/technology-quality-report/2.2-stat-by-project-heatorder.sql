WITH a AS (
SELECT
  row_number() OVER (ORDER BY record_created) AS pour_order, *
FROM v_heat_ladle_pour_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
), b AS (
SELECT
  a.*,
  pour_order - min_heat_order + 1 AS heat_order
FROM (
  SELECT heat_record_key, MIN(pour_order) AS min_heat_order
  FROM a
  GROUP BY heat_record_key
) c JOIN a
ON a.heat_record_key = c.heat_record_key
), stat AS (
SELECT
  ISNULL(pourleader_id, '') AS minor_title,
  CONVERT(VARCHAR, heat_order) AS sub_title,
  COUNT(1) AS cast_total,
  SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
  SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap_sum,
	SUM (confirmed_scrap) AS sconf_sum,
  SUM (finished) AS to_dock
FROM b
GROUP BY
	pourleader_id,
  heat_order
)

SELECT
  minor_title + '---' + RIGHT('000' + sub_title, 4) AS stat_key,
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
	'total' AS sub_title,
  SUM(cast_total) AS cast_total,
  SUM(pre_insp) AS pre_insp,
  SUM(scrap_sum) AS scrap_sum,
  SUM(sconf_sum) AS sconf_sum,
  SUM(to_dock) AS to_dock
FROM stat
GROUP BY minor_title
) total
ORDER BY stat_key
