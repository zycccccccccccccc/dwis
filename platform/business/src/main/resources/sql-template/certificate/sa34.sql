SELECT
    wheel_record.wheel_serial,
    wheel_record.grind_depth,
    wheel_record.shipped_no
FROM
    wheel_record
WHERE
    wheel_record.grind_depth IS NOT NULL
    AND wheel_record.shipped_no = :shippedNo
