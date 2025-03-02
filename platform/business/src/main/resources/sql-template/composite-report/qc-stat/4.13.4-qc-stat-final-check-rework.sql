SELECT
    CONVERT(VARCHAR (10), ope_d_t, 120) AS ope_d_t,
    SUBSTRING(final_check_record.wheel_serial,5,1) AS xh,
    COUNT(final_check_record.wheel_serial) AS final_check_times
FROM
    final_check_record
WHERE
        final_check_record.ope_d_t >= :beginDate
  AND
        final_check_record.ope_d_t <= :endDate
GROUP BY
    CONVERT(VARCHAR (10), ope_d_t, 120),
    SUBSTRING(final_check_record.wheel_serial,5,1)
ORDER BY
    CONVERT(VARCHAR (10), ope_d_t, 120),
    SUBSTRING(final_check_record.wheel_serial,5,1)
