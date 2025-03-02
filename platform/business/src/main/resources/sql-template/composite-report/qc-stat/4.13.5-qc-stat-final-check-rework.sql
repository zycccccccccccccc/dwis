SELECT
    CONVERT(VARCHAR (10), ope_d_t, 120) AS ope_d_t,
    SUBSTRING(final_check_record.wheel_serial,5,1) AS xh,
    final_check_record.rework_code,
    COUNT(final_check_record.wheel_serial) AS amount
FROM
    final_check_record
WHERE
        final_check_record.ope_d_t >= :beginDate
  AND
        final_check_record.ope_d_t <= :endDate
  AND
        final_check_record.rework_code IN :reworkCode
GROUP BY
    CONVERT(VARCHAR (10), ope_d_t, 120),
    SUBSTRING(final_check_record.wheel_serial,5,1),
    final_check_record.rework_code
