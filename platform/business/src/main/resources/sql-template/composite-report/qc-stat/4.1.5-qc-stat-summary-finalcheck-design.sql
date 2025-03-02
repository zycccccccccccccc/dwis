SELECT
    CASE WHEN final_check_record.xh = '02' THEN '02' ELSE '01' END AS xh,
    CASE WHEN wheel_record.design = 'cj33' THEN 'cj33' WHEN wheel_record.design LIKE 'sa34%' THEN 'sa34'
        ELSE wheel_record.design END AS design,
    final_check_record.rework_code,
    final_check_record.scrap_code,
    COUNT(final_check_record.wheel_serial) AS amount
FROM
    final_check_record
        INNER JOIN wheel_record ON final_check_record.wheel_serial = wheel_record.wheel_serial
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
<#if shift??>
  AND ${shift}
</#if>
<#if staffId??>
  AND final_check_record.inspector_id = :staffId
</#if>
GROUP BY
    final_check_record.xh,
    CASE WHEN wheel_record.design = 'cj33' THEN 'cj33' WHEN wheel_record.design LIKE 'sa34%' THEN 'sa34' ELSE wheel_record.design END,
	final_check_record.rework_code,
	final_check_record.scrap_code
ORDER BY CASE WHEN (final_check_record.rework_code = '' AND final_check_record.scrap_code ='') THEN 0
WHEN (final_check_record.rework_code = '' AND final_check_record.scrap_code !='') THEN 1
WHEN (final_check_record.rework_code != '' AND final_check_record.scrap_code ='') THEN 2
ELSE 3 END,amount DESC
