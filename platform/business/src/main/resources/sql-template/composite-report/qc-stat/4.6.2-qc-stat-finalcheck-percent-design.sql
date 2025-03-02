WITH a AS (
    SELECT CONVERT(VARCHAR (10), ope_d_t, 120) AS                                       ope_d_t,
           design,
           cope_inspector_id,
           COUNT(wheel_serial) AS                                                       amount,
           SUM(CASE WHEN rework_code = '' THEN 1 ELSE 0 END) AS                         no_rework_amount,
           SUM(CASE WHEN scrap_code = '' THEN 1 ELSE 0 END) AS                          no_scrap_amount,
           SUM(
                   CASE WHEN rework_code = '' AND scrap_code = '' THEN 1 ELSE 0 END) AS no_rework_scrap_amount
    FROM final_check_record
    WHERE ts = 1
      AND ope_d_t >= :beginDate
      AND ope_d_t < :endDate
<#if shift??>
    AND ${shift}
</#if>
    GROUP BY CONVERT(VARCHAR (10), ope_d_t, 120),
             design,
             cope_inspector_id
)

SELECT a.*,dbo.percentage(no_rework_amount,amount) AS no_rework_percent,
       dbo.percentage(no_rework_scrap_amount,amount) AS no_rework_scrap_percent,
       dbo.percentage(no_scrap_amount,amount) AS no_scrap_percent,
       amount - no_rework_scrap_amount AS other_amount,
       dbo.percentage((amount - no_rework_amount),amount) AS other_percent
FROM a
ORDER BY
    ope_d_t ASC
