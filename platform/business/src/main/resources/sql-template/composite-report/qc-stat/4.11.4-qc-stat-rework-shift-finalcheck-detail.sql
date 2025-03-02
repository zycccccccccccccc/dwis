WITH t1 AS(
    SELECT
        inspection_record.wheel_serial,
        inspection_record.rework_code
    FROM
        inspection_record
    WHERE
            ope_d_t > :beginDate
      AND
            ope_d_t <= :endDate
    <#if reworkCode??>
        AND rework_code IN :reworkCode
    </#if>
    UNION ALL
    SELECT
        final_check_record.wheel_serial,
        final_check_record.rework_code
    FROM
        final_check_record
    WHERE
            ope_d_t > :beginDate
      AND
            ope_d_t <= :endDate
    <#if reworkCode??>
        AND rework_code IN :reworkCode
    </#if>
    UNION ALL
    SELECT
        ultra_record.wheel_serial,
        ultra_record.rework_code
    FROM
        ultra_record
    WHERE
            ope_d_t > :beginDate
      AND
            ope_d_t <= :endDate
    <#if reworkCode??>
        AND rework_code IN :reworkCode
    </#if>
    UNION ALL
    SELECT
        balance_record.wheel_serial,
        balance_record.rework_code
    FROM
        balance_record
    WHERE
            ope_d_t > :beginDate
      AND
            ope_d_t <= :endDate
    <#if reworkCode??>
        AND rework_code IN :reworkCode
    </#if>
    UNION ALL
    SELECT
        pre_check_record.wheel_serial,
        pre_check_record.rework_code
    FROM
        pre_check_record
    WHERE
            ope_d_t > :beginDate
      AND
            ope_d_t <= :endDate
    <#if reworkCode??>
        AND rework_code IN :reworkCode
    </#if>
    UNION ALL
    SELECT
        correct_wheel_record.wheel_serial,
        correct_wheel_record.rework_code
    FROM
        correct_wheel_record
    WHERE
            ope_d_t > :beginDate
      AND
            ope_d_t <= :endDate
    <#if reworkCode??>
        AND rework_code IN :reworkCode
    </#if>
    UNION ALL
    SELECT
        magnetic_record.wheel_serial,
        magnetic_record.rework_code
    FROM
        magnetic_record
    WHERE
            ope_d_t > :beginDate
      AND
            ope_d_t <= :endDate
    <#if reworkCode??>
        AND rework_code IN :reworkCode
    </#if>
),
     s1 AS(
         SELECT
             final_check_record.inspector_id,
             Count(final_check_record.wheel_serial) AS pre_amount,
             SUM(CASE WHEN wheel_record.confirmed_scrap = 1 THEN 1 ELSE 0 END) AS scrap_amount,
             SUM(CASE WHEN wheel_record.finished = 1 THEN 1 ELSE 0 END) AS amount
         FROM final_check_record
                  INNER JOIN
              wheel_record
              ON final_check_record.wheel_serial = wheel_record.wheel_serial
         WHERE
                 ope_d_t > :beginDate
           AND
                 ope_d_t <= :endDate
         GROUP BY
             final_check_record.inspector_id
     )
SELECT
    final_check_record.inspector_id,
    t1.rework_code,
    COUNT(t1.Wheel_Serial) AS scrap_amount,
    dbo.percentage(COUNT(t1.wheel_serial),s1.pre_amount) AS scrap_percent
FROM
    t1
        INNER JOIN final_check_record ON t1.wheel_serial = final_check_record.wheel_serial
        INNER JOIN s1 ON final_check_record.inspector_id = s1.inspector_id
WHERE
        final_check_record.ts = 1
GROUP BY
    final_check_record.inspector_id,
    t1.rework_code,s1.pre_amount
ORDER BY scrap_amount DESC
