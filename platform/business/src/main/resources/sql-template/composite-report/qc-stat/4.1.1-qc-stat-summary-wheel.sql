SELECT
    design.internal,wheel_record.design,COUNT(pre_check_record.wheel_serial) AS amount,
    pre_check_record.rework_code,pre_check_record.scrap_code
FROM
    pre_check_record
        INNER JOIN wheel_record ON pre_check_record.wheel_serial = wheel_record.wheel_serial
        INNER JOIN design ON wheel_record.design = design.design
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
    design.internal,
    wheel_record.design,
    pre_check_record.rework_code,
    pre_check_record.scrap_code
ORDER BY CASE WHEN (pre_check_record.rework_code = '' AND pre_check_record.scrap_code ='') THEN 0
    WHEN (pre_check_record.rework_code = '' AND pre_check_record.scrap_code !='') THEN 1
    WHEN (pre_check_record.rework_code != '' AND pre_check_record.scrap_code ='') THEN 2
    ELSE 3 END,amount DESC
