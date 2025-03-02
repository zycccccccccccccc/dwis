SELECT
    SUM(CASE WHEN final_check_record.rework_code IN ('H1','H2','H3','H4','H5') THEN 1 ELSE 0 END) AS h1,
    SUM (CASE WHEN final_check_record.rework_code= 'H6' THEN 1 ELSE 0 END) AS h6,
    SUM (CASE WHEN final_check_record.rework_code= 'TR' THEN 1 ELSE 0 END) AS tir
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
