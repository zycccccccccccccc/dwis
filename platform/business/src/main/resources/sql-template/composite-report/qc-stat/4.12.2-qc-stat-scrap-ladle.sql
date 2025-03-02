WITH t1 AS (
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
),
     t2 AS (
         SELECT
             t1.ladle_record_key,t1.ladle_id
         FROM
             t1
         GROUP BY
             t1.ladle_record_key,t1.ladle_id
     )
SELECT
          t2.ladle_record_key,
          pour_record.wheel_serial,
          pour_record.record_created,
          pour_record.in_pit_date_time,
          1 AS xh,
          pour_record.pour_d_t
FROM
    pour_record
        INNER JOIN t2
                   ON pour_record.ladle_id = t2.ladle_id
ORDER BY
    pour_record.record_created,
    pour_record.in_pit_date_time
