WITH t1 AS(
    SELECT
        wheel_serial,
        scrap_code
    FROM
        wheel_record
    WHERE
            scrap_code IN :scrapCode
      AND
            scrap_date >= :beginDate
      AND
            scrap_date <= :endDate
)
SELECT
    COUNT(machine_record.wheel_serial) AS scrap_amount,
    j_machine_record.operator,
    j_machine_record.inspector_id,
    t1.scrap_code,
    'jmachine' AS type
FROM
    machine_record
        INNER JOIN t1 ON t1.wheel_serial = machine_record.wheel_serial
        INNER JOIN j_machine_record ON machine_record.j_id = j_machine_record.id
GROUP BY
    j_machine_record.inspector_id,
    j_machine_record.operator,
    t1.scrap_code
UNION ALL
SELECT
    COUNT(machine_record.wheel_serial) AS scrap_amount,
    t_machine_record.operator,
    t_machine_record.inspector_id,
    t1.scrap_code,
    'tmachine' AS type
FROM
    machine_record
        INNER JOIN t1 ON t1.wheel_serial = machine_record.wheel_serial
        INNER JOIN t_machine_record ON machine_record.t_id = t_machine_record.id
GROUP BY
    t_machine_record.inspector_id,
    t_machine_record.operator,
    t1.scrap_code
UNION ALL
SELECT
    COUNT(machine_record.wheel_serial) AS scrap_amount,
    k_machine_record.operator,
    k_machine_record.inspector_id,
    t1.scrap_code,
    'kmachine' AS type
FROM
    machine_record
        INNER JOIN t1 ON t1.wheel_serial = machine_record.wheel_serial
        INNER JOIN k_machine_record ON machine_record.k_id = k_machine_record.id
GROUP BY
    k_machine_record.inspector_id,
    k_machine_record.operator,
    t1.scrap_code
UNION ALL
SELECT
    COUNT(machine_record.wheel_serial) AS scrap_amount,
    w_machine_record.operator,
    w_machine_record.inspector_id,
    t1.scrap_code,
    'wmachine' AS type
FROM
    machine_record
        INNER JOIN t1 ON t1.wheel_serial = machine_record.wheel_serial
        INNER JOIN w_machine_record ON machine_record.w_id = w_machine_record.id
GROUP BY
    w_machine_record.inspector_id,
    w_machine_record.operator,
    t1.scrap_code
