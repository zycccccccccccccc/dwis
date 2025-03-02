SELECT
    CONVERT(VARCHAR (10), pour_record.cast_date, 120) AS ope_d_t,
    final_check_record.rework_code,
    COUNT(final_check_record.wheel_serial) AS amount
FROM
    final_check_record
INNER JOIN
    pour_record ON final_check_record.wheel_serial = pour_record.wheel_serial
WHERE
        pour_record.cast_date >= :beginDate
  AND
        pour_record.cast_date <= :endDate
  AND
        final_check_record.rework_code IN :reworkCode
GROUP BY
    CONVERT(VARCHAR (10), pour_record.cast_date, 120),
    final_check_record.rework_code
ORDER BY
    CONVERT(VARCHAR (10), pour_record.cast_date, 120),
    final_check_record.rework_code
