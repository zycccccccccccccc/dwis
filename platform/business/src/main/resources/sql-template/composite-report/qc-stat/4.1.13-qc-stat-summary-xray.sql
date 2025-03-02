SELECT
    wheel_record.wheel_serial,
    wheel_record.design,
    inspection_record.xray_result AS xray,
    wheel_record.scrap_code,
    wheel_record.confirmed_scrap,
    wheel_record.scrap_date
FROM
    inspection_record
        INNER JOIN wheel_record ON inspection_record.wheel_serial = wheel_record.wheel_serial
WHERE
        ope_d_t >= :beginDate
  AND
        ope_d_t < :endDate
  AND
        inspection_record.xray_result IS NOT NULL
