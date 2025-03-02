WITH a AS (
SELECT
  model_id,
  scrap_code,
  SUM (CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS cnt,
  SUM (confirmed_scrap) AS sconf_sum
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
  AND scrap_code <> ''
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  model_id,
  scrap_code
),
b AS (
SELECT
  model_id,
  SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp
FROM v_heat_ladle_wheel
WHERE cast_date >= :beginDate
  AND cast_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  model_id
)

SELECT
  a.model_id AS stat_key,
  scrap_code,
  cnt,
  dbo.percentage(cnt, b.pre_insp) AS cnt_pre,
  sconf_sum
FROM a JOIN b ON a.model_id = b.model_id
ORDER BY sconf_sum DESC





