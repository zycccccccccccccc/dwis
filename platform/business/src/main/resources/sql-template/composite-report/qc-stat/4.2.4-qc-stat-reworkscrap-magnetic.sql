SELECT
    magnetic_record.wheel_serial,
    magnetic_record.design,
    magnetic_record.rework_code,
    magnetic_record.scrap_code,
    wheel_record.rework_code AS current_rework_code,
    wheel_record.scrap_code AS current_scrap_code,
    wheel_record.confirmed_scrap,
    mag_drag_inspector_id AS drag_inspector_id,
    mag_cope_inspector_id AS cope_inspector_id,
    ope_d_t
FROM
    magnetic_record
        INNER JOIN wheel_record ON magnetic_record.wheel_serial = wheel_record.wheel_serial
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
  AND (magnetic_record.rework_code !='' OR magnetic_record.scrap_code != '')
<#if shift??>
  AND ${shift}
</#if>
ORDER BY
    magnetic_record.wheel_serial,ope_d_t
