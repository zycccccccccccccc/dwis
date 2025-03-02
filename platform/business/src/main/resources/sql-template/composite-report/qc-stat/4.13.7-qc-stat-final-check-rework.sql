SELECT
    CONVERT(VARCHAR (10), pour_record.cast_date, 120) AS ope_d_t,
    COUNT(final_check_record.wheel_serial) AS final_check_times
FROM
    final_check_record
INNER JOIN
    pour_record ON final_check_record.wheel_serial = pour_record.wheel_serial
WHERE
        pour_record.cast_date >= :beginDate
  AND
        pour_record.cast_date <= :endDate
GROUP BY
    CONVERT(VARCHAR (10), pour_record.cast_date, 120)
ORDER BY CONVERT(VARCHAR (10), pour_record.cast_date, 120)
