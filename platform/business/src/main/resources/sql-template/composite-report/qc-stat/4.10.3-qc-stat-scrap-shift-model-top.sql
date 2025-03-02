WITH s1 AS (
    SELECT
        heat_record.model_id,
        wheel_record.wheel_serial
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
)
SELECT
    model_id AS inspector_id,
    COUNT(s1.wheel_serial) AS amount
FROM s1
GROUP BY
    s1.model_id
