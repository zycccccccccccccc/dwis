SELECT
    ultra_record.wheel_serial,
    ultra_record.design,
    ultra_record.rework_code,
    ultra_record.scrap_code,
    wheel_record.rework_code AS current_rework_code,
    wheel_record.scrap_code AS current_scrap_code,
    wheel_record.confirmed_scrap,
    mag_drag_inspector_id AS drag_inspector_id,
    mag_cope_inspector_id AS cope_inspector_id,
    ope_d_t
FROM
    ultra_record
        INNER JOIN wheel_record ON ultra_record.wheel_serial = wheel_record.wheel_serial
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
  AND (ultra_record.rework_code !='' OR ultra_record.scrap_code != '')
<#if shift??>
  AND ${shift}
</#if>
ORDER BY
    ultra_record.wheel_serial,ope_d_t
