SELECT
    balance_record.wheel_serial,
    balance_record.design,
    balance_record.rework_code,
    balance_record.scrap_code,
    wheel_record.rework_code AS current_rework_code,
    wheel_record.scrap_code AS current_scrap_code,
    wheel_record.confirmed_scrap,
    balance_inspector_id,
    ope_d_t
FROM
    balance_record
        INNER JOIN wheel_record ON balance_record.wheel_serial = wheel_record.wheel_serial
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
  AND (balance_record.rework_code !='' OR balance_record.scrap_code != '')
<#if shift??>
  AND ${shift}
</#if>
ORDER BY
    balance_record.wheel_serial,ope_d_t
