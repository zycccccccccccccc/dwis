SELECT
        wheel_record.scrap_code,
        wheel_record.ladle_id,
        ladle_record.ladle_record_key,
        wheel_record.wheel_serial,
        wheel_record.design,
        heat_record.cast_date,
        wheel_record.confirmed_scrap,
        ladle_record.ladle_temp AS pour_temp
    FROM heat_record
             INNER JOIN
         (ladle_record INNER JOIN wheel_record ON ladle_record.id = wheel_record.ladle_id)
         ON heat_record.id = ladle_record.heat_record_id
    WHERE
            heat_record.cast_date >= :beginDate
      AND
            heat_record.cast_date <= :endDate
      AND
            wheel_record.scrap_code IN :scrapCode
      AND
            wheel_record.confirmed_scrap = 1
