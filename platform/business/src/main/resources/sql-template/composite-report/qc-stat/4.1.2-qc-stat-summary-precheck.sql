SELECT t1.design,t1.amount AS times,COUNT(t1.wheel_serial) AS amount FROM
(SELECT
     wheel_record.design,pre_check_record.wheel_serial,
     COUNT(pre_check_record.wheel_serial) AS amount
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
     pre_check_record.wheel_serial) t1
GROUP BY t1.design,t1.amount
ORDER BY t1.design DESC,times
