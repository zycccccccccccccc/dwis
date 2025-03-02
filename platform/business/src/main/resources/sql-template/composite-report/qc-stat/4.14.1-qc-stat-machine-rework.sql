WITH t1 AS(
    SELECT
       t_machine_record.wheel_serial,
       t_machine_record.rework_code,
    t_machine_record.t_s1,
    t_machine_record.t_s2
    FROM
        t_machine_record
    WHERE
      t_machine_record.ope_d_t >= :beginDate
    AND
    t_machine_record.ope_d_t < :endDate
    AND
    t_machine_record.rework_code IN :reworkCode
  GROUP BY
     t_machine_record.wheel_serial,
       t_machine_record.rework_code,
    t_machine_record.t_s1,
    t_machine_record.t_s2
),
 t2 AS(
         SELECT
          t1.rework_code,
     COUNT(t1.wheel_serial) AS rework_amount
         FROM t1
         GROUP BY
          t1.rework_code
),

s1 AS (
  SELECT
       t_machine_record.wheel_serial
    FROM
        t_machine_record
     WHERE
        t_machine_record.ope_d_t >= :beginDate
    AND
        t_machine_record.ope_d_t < :endDate
   AND
       t_machine_record.rework_code IN :reworkCode
  GROUP BY
     t_machine_record.wheel_serial
),


s2 AS (
    SELECT
    SUM(CASE WHEN wheel_record.finished = 1 THEN 1 ELSE 0 END) AS finished_amount,
    SUM(CASE WHEN wheel_record.confirmed_scrap = 1 THEN 1 ELSE 0 END) AS scrap_amount
FROM
    wheel_record 
  INNER JOIN s1 ON wheel_record.wheel_serial = s1.wheel_serial
)

SELECT
    t2.rework_code,
    t2.rework_amount,
   s2.finished_amount,
  s2.scrap_amount
FROM t2, s2