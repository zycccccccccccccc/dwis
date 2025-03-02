WITH s1 AS (
    SELECT
        wheel_serial
    FROM
        wheel_record
WHERE
            stock_date >= :beginDate
      AND
            stock_date <= :endDate
)
SELECT
    COUNT(machine_record.wheel_serial) AS amount,
    t_machine_record.inspector_id
FROM
    machine_record
        INNER JOIN s1
                   ON s1.wheel_serial = machine_record.wheel_serial
        INNER JOIN
    t_machine_record
    ON machine_record.t_id = t_machine_record.id
GROUP BY
    t_machine_record.inspector_id
