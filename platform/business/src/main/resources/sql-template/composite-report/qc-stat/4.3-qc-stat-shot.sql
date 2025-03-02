SELECT
    wheel_record.last_pre,
    wheel_record.wheel_serial,
    wheel_record.scrap_code,
    wheel_record.shipped_no,
    wheel_record.finished,
    wheel_record.last_balance
FROM
    wheel_record
WHERE
        last_pre >= :beginDate
  AND
        last_pre < :endDate
ORDER BY
    last_pre
