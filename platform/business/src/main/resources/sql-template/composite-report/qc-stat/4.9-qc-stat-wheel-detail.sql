SELECT
    heat_record.heat_record_key,
    heat_record.tap_seq,
    ladle_record.ladle_seq,
    wheel_record.wheel_serial,
    CONVERT(VARCHAR(5),pour_record.open_time_act,108) AS open_time_act,
    wheel_record.scrap_code,
    wheel_record.confirmed_scrap,
    wheel_record.scrap_date,
    wheel_record.pre,
    wheel_record.final AS final_times,
    wheel_record.ultra,
    wheel_record.balance,
    wheel_record.finished,
    wheel_record.mec_serial
FROM heat_record
         INNER JOIN
     (ladle_record INNER JOIN wheel_record ON ladle_record.id = wheel_record.ladle_id)
     ON heat_record.id = ladle_record.heat_record_id
         INNER JOIN pour_record ON wheel_record.wheel_serial = pour_record.wheel_serial
WHERE
        heat_record.cast_date = :beginDate
ORDER BY
    heat_record.tap_seq ASC,
    ladle_record.ladle_seq ASC,
    pour_record.record_created ASC
