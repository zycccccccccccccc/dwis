SELECT
    cihen_record.wheel_serial,
    cihen_record.grind_time,
    cihen_record.cope_cihen_sum,
    cihen_record.cope_sandholes,
    cihen_record.drag_cihen_sum,
    cihen_record.drag_sandholes,
    cihen_record.ts
FROM
    cihen_record
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
<#if shift??>
  AND ${shift}
  <#if staffId??>
    AND cihen_record.inspector_id = :staffId
  </#if>
</#if>
