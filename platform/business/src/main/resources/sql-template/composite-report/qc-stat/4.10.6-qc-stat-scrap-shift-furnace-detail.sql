WITH t1 AS (
    SELECT
        Heat_Record.pourleader_id AS inspector_id,
        wheel_record.wheel_serial,
        wheel_record.scrap_code
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
             t1.scrap_code
         FROM
             t1
         GROUP BY
             t1.inspector_id,
             t1.scrap_code
     ),
     s1 AS (
         SELECT
             pourleader_id AS inspector_id,
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
         GROUP BY pourleader_id
)
SELECT
    t2.inspector_id,
    t2.scrap_code,
    t2.scrap_amount,
    s1.amount,dbo.percentage(t2.scrap_amount,t2.scrap_amount + s1.amount) AS scrap_percent
FROM t2 INNER JOIN s1 ON t2.inspector_id = s1.inspector_id
