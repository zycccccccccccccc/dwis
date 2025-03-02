WITH t1 AS (
    SELECT
        heat_record.model_id AS inspector_id,
        wheel_record.wheel_serial,
        wheel_record.scrap_code,
        SUBSTRING(wheel_record.wheel_serial,5,1) AS xh
    FROM
        wheel_record
            INNER JOIN ladle_record
                       ON wheel_record.ladle_id = ladle_record.id
            INNER JOIN heat_record
                       ON ladle_record.heat_record_id = heat_record.id
    WHERE
            scrap_code IN :scrapCode
      AND
            scrap_date >= :beginDate
      AND
            scrap_date <= :endDate
),
     t2 AS (
         SELECT
             COUNT(t1.wheel_serial) AS scrap_amount,
             t1.inspector_id,
             t1.scrap_code,
             t1.xh
         FROM
             t1
         GROUP BY
             t1.inspector_id,
             t1.scrap_code,
             t1.xh
     ),
     s1 AS (
         SELECT
             heat_record.model_id,
             COUNT(wheel_record.wheel_serial) AS amount
         FROM
             wheel_record
                 INNER JOIN ladle_record
                            ON wheel_record.ladle_id = ladle_record.id
                 INNER JOIN heat_record
                            ON ladle_record.heat_record_id = heat_record.id
         WHERE
                 stock_date >= :beginDate
           AND
                 stock_date <= :endDate
         GROUP BY model_id
)
SELECT
    t2.inspector_id,
    t2.scrap_code,
    t2.xh,
    t2.scrap_amount,
    s1.amount,dbo.percentage(t2.scrap_amount,t2.scrap_amount + s1.amount) AS scrap_percent
FROM t2 INNER JOIN s1 ON t2.inspector_id = s1.model_id
