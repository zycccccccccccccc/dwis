
SELECT
  smr.create_time AS mold_date,
  smr.sand_breathability AS mea_value,
  mp.min_value,
  mp.max_value
FROM
  sand_mix_record smr
  INNER JOIN mold_params mp ON smr.sand_breathability_limits = mp.id
WHERE
  smr.sand_breathability IS NOT NULL
  AND CONVERT(VARCHAR(10),DATEADD(hour, -8, smr.create_time),120) >= :beginDate
  AND CONVERT(VARCHAR(10),DATEADD(hour, -8, smr.create_time),120) <= :endDate
  AND smr.line_no = :lineNo
  <#if inspectorId??>
       AND smr.inspector_id = :inspectorId
  </#if>
   <#if operatorId??>
       AND smr.operator_id = :operatorId
   </#if>
ORDER BY
  smr.create_time ASC