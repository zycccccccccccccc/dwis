WITH stat AS (
SELECT
<#if type = 1>
  drag_no
<#else>
  cope_no
</#if>
  AS sub_title,
  COUNT(1) AS cast_total,
  SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
  SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap_sum
FROM v_heat_ladle_pour_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
  AND 0 <> :type
GROUP BY
<#if type = 1>
  drag_no
<#else>
  cope_no
</#if>
)

SELECT
  sub_title AS stat_key,
  sub_title,
  cast_total,
  pre_insp,
  scrap_sum
FROM (
SELECT
  sub_title,
  cast_total,
  pre_insp,
  scrap_sum
FROM stat
UNION ALL
SELECT
  'total' AS sub_title,
  SUM(cast_total) AS cast_total,
  SUM(pre_insp) AS pre_insp,
  SUM(scrap_sum) AS scrap_sum
FROM stat
) total
ORDER BY sub_title