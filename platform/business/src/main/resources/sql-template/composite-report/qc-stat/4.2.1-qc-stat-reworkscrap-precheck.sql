SELECT
    pre_check_record.wheel_serial,
    pre_check_record.design,
    pre_check_record.rework_code,
    pre_check_record.scrap_code,
    wheel_record.rework_code AS current_rework_code,
    wheel_record.scrap_code AS current_scrap_code,
    wheel_record.confirmed_scrap,
    drag_inspector_id,
    cope_inspector_id,
    ope_d_t
FROM
    pre_check_record
        INNER JOIN wheel_record ON pre_check_record.wheel_serial = wheel_record.wheel_serial
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
  AND (pre_check_record.rework_code !='' OR pre_check_record.scrap_code != '')
<#if shift??>
  AND ${shift}
</#if>
ORDER BY
    pre_check_record.wheel_serial,ope_d_t
