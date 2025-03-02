SELECT
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
    final_check_record.rework_code
