WITH t1 AS(
    SELECT
       t_machine_record.wheel_serial,
       t_machine_record.rework_code,
    t_machine_record.t_s1,
    t_machine_record.t_s2,
    t_machine_record.ope_d_t
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
    t_machine_record.t_s2,
    t_machine_record.ope_d_t
),
 t2 AS(
         SELECT
      CONVERT(VARCHAR (10), t1.ope_d_t, 120) AS ope_d_t,
          t1.rework_code,
     COUNT(t1.wheel_serial) AS rework_amount
         FROM t1
         GROUP BY
      CONVERT(VARCHAR (10), t1.ope_d_t, 120) AS ope_d_t,
          t1.rework_code
),

s1 AS (
  SELECT
      CONVERT(VARCHAR (10), t_machine_record.ope_d_t, 120) AS ope_d_t,
       t_machine_record.wheel_serial,
    t_machine_record.rework_code
    FROM
        t_machine_record
  INNER JOIN machine_record on t_machine_record.id = machine_record.t_id_last
     WHERE
        t_machine_record.ope_d_t >= :beginDate
    AND
        t_machine_record.ope_d_t < :endDate
  AND
        t_machine_record.rework_code IN :reworkCode
  GROUP BY
     CONVERT(VARCHAR (10), t_machine_record.ope_d_t, 120) AS ope_d_t,
     t_machine_record.wheel_serial,
    t_machine_record.rework_code
),

s2 AS (
    SELECT
  s1.ope_d_t,
  s1.rework_code,
    SUM(CASE WHEN wheel_record.finished = 1 THEN 1 ELSE 0 END) AS finished_amount,
    SUM(CASE WHEN wheel_record.confirmed_scrap = 1 THEN 1 ELSE 0 END) AS scrap_amount
FROM
    wheel_record 
  INNER JOIN s1 ON wheel_record.wheel_serial = s1.wheel_serial
  GROUP BY
  s1.ope_d_t,
  s2.rework_code
)

SELECT
    t2.ope_d_t,
    t2.rework_code,
    t2.rework_amount,
   s2.finished_amount,
  s2.scrap_amount
FROM t2
INNER JOIN s2 ON t2.ope_d_t = s2.ope_d_t AND t2.rework_code = s2.rework_code
GROUP BY
    t2.ope_d_t,
    t2.rework_code,
    t2.rework_amount,
   s2.finished_amount,
  s2.scrap_amount
  
  