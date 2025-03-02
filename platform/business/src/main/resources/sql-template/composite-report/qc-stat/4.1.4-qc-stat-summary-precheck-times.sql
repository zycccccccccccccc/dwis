SELECT
    pre_check_record.wheel_serial,COUNT(pre_check_record.wheel_serial) AS times
FROM
    pre_check_record
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
    pre_check_record.wheel_serial
HAVING
    COUNT ( pre_check_record.wheel_serial ) > 1
ORDER BY times DESC

