SELECT
    wheel_record.design,COUNT(pre_check_record.wheel_serial) AS amount,pre_check_record.scrap_code
FROM
    pre_check_record
        INNER JOIN wheel_record ON pre_check_record.wheel_serial = wheel_record.wheel_serial
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
<#if shift??>
  AND ${shift}
</#if>
<#if staffId??>
  AND pre_check_record.inspector_id = :staffId
</#if>
GROUP BY
    wheel_record.design,
    pre_check_record.scrap_code
HAVING
    pre_check_record.scrap_code IN ('69','56D','56A','56C','9B','45')
