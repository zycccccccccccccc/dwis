SELECT
  w.design,
  COUNT(w.wheel_serial) AS amount
FROM
  wheel_record w
INNER JOIN transport_record t ON t.id = w.x_finished_id
WHERE
  w.finished = 1
  AND t.ope_type = 81
  AND t.ope_d_t >= :beginDate
  AND t.ope_d_t < :endDate
  <#if shift??>
     AND ${shift}
  </#if>
  <#if staffId??>
     AND pre_check_record.inspector_id = :staffId
  </#if>
GROUP BY
  w.design
ORDER BY
  amount DESC
