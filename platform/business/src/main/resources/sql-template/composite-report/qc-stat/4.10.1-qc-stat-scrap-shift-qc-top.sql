WITH s1 AS (
    SELECT
        wheel_serial
    FROM
        wheel_record
    WHERE
            stock_date >= :beginDate
      AND
            stock_date <= :endDate
    ),
     s2 AS (
         SELECT
             balance_record.wheel_serial,
             balance_record.inspector_id,
             balance_record.ope_d_t
         FROM
             balance_record
                 INNER JOIN
             s1 ON s1.wheel_serial = balance_record.wheel_serial
         UNION ALL
         SELECT
             transport_record.wheel_serial,
             account.team_leader_id AS inspector_id,
             transport_record.ope_d_t
         FROM
             transport_record
                 INNER JOIN account
                            ON account.username = transport_record.inspector_id
                 INNER JOIN s1
                            ON s1.wheel_serial = transport_record.wheel_serial
     ),
     s3 AS (
         SELECT
             s2.wheel_serial,
             MAX(s2.ope_d_t) AS ope_d_t
         FROM
             s2
         GROUP BY
             s2.wheel_serial
     ),
     s4 AS (
         SELECT
             s3.wheel_serial,
             s2.inspector_id
         FROM
             s2 INNER JOIN s3
                           ON s2.wheel_serial = s3.wheel_serial
         WHERE
                 s2.ope_d_t = s3.ope_d_t
     )
SELECT
    COUNT(s4.wheel_serial) AS amount,
    s4.inspector_id
FROM
    s4
        GROUP BY
    s4.inspector_id
