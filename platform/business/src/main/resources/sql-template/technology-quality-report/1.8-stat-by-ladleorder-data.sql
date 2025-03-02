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
  pour_order - min_ladle_order + 1 AS ladle_order
FROM (
  SELECT ladle_id, MIN(pour_order) AS min_ladle_order
  FROM a
  GROUP BY ladle_id
) c JOIN a
ON a.ladle_id = c.ladle_id
)
SELECT
  RIGHT('000' + ladle_order, 4) AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM (
SELECT
  CONVERT(VARCHAR, ladle_order) AS ladle_order,
  confirmed_scrap,
  scrap_code
FROM b
  WHERE scrap_code IS NOT NULL AND scrap_code <> ''
) a
GROUP BY
  ladle_order,
  scrap_code
ORDER BY
  stat_key,
  sconf_sum DESC