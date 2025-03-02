SELECT wheel_record.wheel_serial
FROM wheel_record
INNER JOIN sample_wheel_record
ON wheel_record.wheel_serial = sample_wheel_record.wheel_serial
WHERE wheel_record.shipped_no = :shippedNo
