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
     ),
     s5 AS (
         SELECT
             COUNT(s4.wheel_serial) AS amount,
             s4.inspector_id
         FROM
             s4
         GROUP BY
             s4.inspector_id
     ),
     t1 AS (
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
     ),
     t2 AS (
         SELECT
             inspection_record.wheel_serial,
             inspection_record.leader_id AS inspector_id,
             inspection_record.ope_d_t,
             t1.scrap_code
         FROM
             inspection_record
                 INNER JOIN
             t1 ON t1.wheel_serial = inspection_record.wheel_serial
         UNION ALL
         SELECT
             final_check_record.wheel_serial,
             final_check_record.inspector_id,
             final_check_record.ope_d_t,
             t1.scrap_code
         FROM
             final_check_record
                 INNER JOIN
             t1 ON t1.wheel_serial = final_check_record.wheel_serial
         UNION ALL
         SELECT
             ultra_record.wheel_serial,
             ultra_record.inspector_id,
             ultra_record.ope_d_t,
             t1.scrap_code
         FROM
             ultra_record
                 INNER JOIN
             t1 ON t1.wheel_serial = ultra_record.wheel_serial
         UNION ALL
         SELECT
             balance_record.wheel_serial,
             balance_record.inspector_id,
             balance_record. ope_d_t,
             t1.scrap_code
         FROM
             balance_record
                 INNER JOIN
             t1 ON t1.wheel_serial = balance_record.wheel_serial
         UNION ALL
         SELECT
             pre_check_record.wheel_serial,
             pre_check_record.inspector_id,
             pre_check_record. ope_d_t,
             t1.scrap_code
         FROM
             pre_check_record
                 INNER JOIN
             t1 ON t1.wheel_serial = pre_check_record.wheel_serial
         UNION ALL
         SELECT
             magnetic_record.wheel_serial,
             magnetic_record.inspector_id,
             magnetic_record. ope_d_t,
             t1.scrap_code
         FROM
             magnetic_record
                 INNER JOIN
             t1 ON t1.wheel_serial = magnetic_record.wheel_serial
     ),
     t3 AS (
         Select
             t2.wheel_serial,
             MAX(t2.ope_d_t) AS ope_d_t
         FROM
             t2
         GROUP BY
             t2.wheel_serial

     ),
     t4 AS (
         SELECT
             t3.wheel_serial,
             t2.inspector_id,
             t2.scrap_code
         FROM
             t2 INNER JOIN t3 ON t2.wheel_serial = t3.wheel_serial
         WHERE
                 t2.ope_d_t = t3.ope_d_t
     ),
     t5 AS (
         SELECT
             COUNT(t4.wheel_serial) AS scrap_amount,
             t4.inspector_id,
             t4.scrap_code
         FROM
             t4
         GROUP BY
             t4.inspector_id,
             t4.scrap_code
     )
SELECT
    t5.scrap_amount,
    t5.inspector_id,
    t5.scrap_code,
    s5.amount,dbo.percentage(t5.scrap_amount,t5.scrap_amount+s5.amount) as scrap_percent
FROM
    t5 INNER JOIN s5 ON s5.inspector_id = t5.inspector_id
