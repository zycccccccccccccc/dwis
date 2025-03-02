WITH a AS (
    SELECT CONVERT(VARCHAR (10), ope_d_t, 120) AS                                       ope_d_t,
           design,
           COUNT(wheel_serial) AS                                                       amount,
           balance_inspector_id,
           SUM(CASE WHEN(balance_v > 0 AND balance_v <= 125) THEN 1 ELSE 0 END) AS qualified_amount
    FROM balance_record
    WHERE ts = 1
      AND balance_v > 0
      AND ope_d_t >= :beginDate
      AND ope_d_t < :endDate
<#if design??>
    AND design IN :design
</#if>
    GROUP BY CONVERT(VARCHAR (10), ope_d_t, 120),
            design,
            balance_inspector_id
)

SELECT a.*,dbo.percentage(qualified_amount,amount) AS qualified_percent
FROM a
ORDER BY
    ope_d_t ASC
