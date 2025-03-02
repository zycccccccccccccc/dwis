
SELECT
  psr.create_time AS mold_date,
  psr.thickness AS mea_value,
  mp.min_value,
  mp.max_value
FROM
  pre_spray_record psr
  INNER JOIN mold_pre_shift_record mpsr ON psr.pre_shift_id = mpsr.id
  INNER JOIN mold_params mp ON psr.thickness_limits = mp.id
WHERE
  CONVERT(VARCHAR(10),mpsr.mold_date,120) >= :beginDate
  AND CONVERT(VARCHAR(10),mpsr.mold_date,120) <= :endDate
  AND psr.line_no = :lineNo
  <#if inspectorId??>
       AND mpsr.inspector_id = :inspectorId
  </#if>
   <#if operatorId??>
       AND psr.operator_id = :operatorId
   </#if>
ORDER BY
  psr.create_time ASC