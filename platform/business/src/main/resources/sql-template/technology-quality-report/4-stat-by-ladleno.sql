WITH a AS (
SELECT
  row_number() OVER (ORDER BY record_created) AS pour_order, *
FROM v_heat_ladle_pour_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND ladle_no = :ladleNo
), b AS (
SELECT
  a.*,
  pour_order - min_pour_order + 1 AS ladle_order
FROM (
  SELECT ladle_id, MIN(pour_order) AS min_pour_order
  FROM a
  GROUP BY ladle_id
) c JOIN a
ON a.ladle_id = c.ladle_id
), stat AS (
SELECT
  CONVERT(VARCHAR, ladle_order) AS sub_title,
  COUNT(1) AS cast_total,
  SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
  SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap_sum,
	SUM (confirmed_scrap) AS sconf_sum,
  SUM (finished) AS to_dock
FROM b
GROUP BY
  ladle_order
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
