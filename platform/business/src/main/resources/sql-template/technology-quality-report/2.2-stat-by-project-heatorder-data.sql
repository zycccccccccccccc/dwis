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
)
SELECT
  CONVERT(VARCHAR, ISNULL(pourleader_id, '')) + '---' + RIGHT('000' + heat_order, 4) AS stat_key,
  scrap_code,
  SUM (confirmed_scrap) AS sconf_sum
FROM (
SELECT
  pourleader_id,
  CONVERT(VARCHAR, heat_order) AS heat_order,
  confirmed_scrap,
  scrap_code
FROM b
  WHERE scrap_code IS NOT NULL AND scrap_code <> ''
) a
GROUP BY
  pourleader_id,
  heat_order,
  scrap_code
ORDER BY
  stat_key,
  sconf_sum DESC