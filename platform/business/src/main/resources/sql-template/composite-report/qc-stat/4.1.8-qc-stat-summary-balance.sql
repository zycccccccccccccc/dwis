SELECT
    balance_record.xh,
    balance_record.rework_code,
    balance_record.hold_code,
    balance_record.scrap_code,
    SUM (CASE WHEN(balance_record.balance_s= 'E3') THEN 1 ELSE 0 END ) AS e3,
    SUM (CASE WHEN(balance_record.balance_s= 'MC') THEN 1 ELSE 0 END ) AS mc,
    COUNT(balance_record.wheel_serial) AS amount
FROM
    balance_record
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
<#if shift??>
  AND ${shift}
</#if>
<#if staffId??>
  AND balance_record.inspector_id = :staffId
</#if>
GROUP BY
    balance_record.xh,
    balance_record.rework_code,
    balance_record.scrap_code,
    balance_record.hold_code
ORDER BY amount DESC
