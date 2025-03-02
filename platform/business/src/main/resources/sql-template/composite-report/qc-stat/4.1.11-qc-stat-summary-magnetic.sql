SELECT
    magnetic_record.wheel_serial,
    magnetic_record.design,
    magnetic_record.scrap_code,
    magnetic_record.rework_code,
    magnetic_record.ts
FROM
    magnetic_record
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
<#if shift??>
  AND ${shift}
</#if>
<#if staffId??>
  AND magnetic_record.inspector_id = :staffId
</#if>
