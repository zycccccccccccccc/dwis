WITH a AS (
    SELECT COUNT(pre_check_record.wheel_serial) AS counts,
           CONVERT(VARCHAR(10),MIN(ope_d_t),120) AS ope_d_t,
           pre_check_record.wheel_serial,
           t1.design,
           pre_check_record.rework_code
    FROM
        pre_check_record
            INNER JOIN (
            SELECT
                pre_check_record.wheel_serial,
                pre_check_record.rework_code,
                wheel_record.pre_date,
                wheel_record.design
            FROM
                pre_check_record
                    INNER JOIN wheel_record ON pre_check_record.wheel_serial = wheel_record.wheel_serial
            WHERE
                    pre_check_record.ope_d_t = wheel_record.pre_date
                           AND pre_check_record.rework_code IN :reworkCode
                           AND wheel_record.pre_date >= :beginDate
                           AND wheel_record.pre_date < :endDate) AS t1 ON pre_check_record.wheel_serial = t1.wheel_serial
    GROUP BY
        pre_check_record.wheel_serial,
        t1.design,
        pre_check_record.rework_code
    HAVING
            pre_check_record.rework_code IN :reworkCode
       AND MIN(pre_check_record.ope_d_t) >= :beginDate
       AND MIN(pre_check_record.ope_d_t) < :endDate
)

SELECT
    a.ope_d_t,
    a.design,
    a.rework_code,
    COUNT(a.wheel_serial) AS amount,
    SUM(CASE WHEN a.counts = 1 THEN 1 ELSE 0 END) AS passed_amount,
    dbo.percentage(SUM(CASE WHEN a.counts = 1 THEN 1 ELSE 0 END),COUNT(a.wheel_serial)) AS passed_percent
FROM a
GROUP BY
    a.ope_d_t,
    a.design,
    a.rework_code
