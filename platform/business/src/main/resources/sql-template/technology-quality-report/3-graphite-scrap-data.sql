WITH a AS (
SELECT
<#if type = 1>
  drag_no AS graphite_no,
<#else>
  cope_no AS graphite_no,
</#if>
  w.scrap_code
FROM heat_record h
INNER JOIN ladle_record l ON h.id = l.heat_record_id
INNER JOIN pour_record p ON l.id = p.ladle_id
INNER JOIN wheel_record w ON p.wheel_serial = w.wheel_serial
WHERE h.cast_date >= :beginDate
  AND h.cast_date <= :endDate
<#if design??>
  AND w.design IN :design
</#if>
  AND w.scrap_code IS NOT NULL AND w.scrap_code <> ''
  AND 0 <> :type
)
SELECT
  graphite_no AS stat_key,
  graphite_no,
  scrap_code,
  SUM (1) AS cnt
FROM a
WHERE 0 <> :type
GROUP BY
  graphite_no,
  scrap_code
ORDER BY
  graphite_no,
  cnt DESC
