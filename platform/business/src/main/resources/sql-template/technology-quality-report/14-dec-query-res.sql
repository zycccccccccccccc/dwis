WITH t AS(
SELECT
  h.id,
  h.heat_record_key,
  MAX(c.c) AS p_max,
  MIN(c.c) AS p_min
FROM
  heat_record h
  INNER JOIN chemistry_detail c ON h.id = c.heat_record_id
WHERE
  CONVERT(VARCHAR(10),h.cast_date,120) >= :beginDate
  AND CONVERT(VARCHAR(10),h.cast_date,120) <= :endDate
  AND c.sample_no LIKE '%-P%'
  <#if pourLeaderId??>
    AND h.pourleader_id = :pourLeaderId
  </#if>
  <#if furNo??>
    AND h.furnace_no = :furNo
  </#if>
  <#if furnaceId??>
    AND h.furnace_id = :furnaceId
  </#if>
GROUP BY
  h.id, h.heat_record_key
)

SELECT
  t.heat_record_key AS heatSeq,
  t.p_max AS meltedPure,
  (t.p_max - t.p_min) AS decarbon
FROM
  t
ORDER BY
  t.id ASC
