WITH t AS (
  SELECT
    j.wheel_serial,
    j.rework_code
  FROM
    j_machine_record j
    INNER JOIN rework_code r ON j.rework_code = r.code
  WHERE
    CONVERT(VARCHAR(10),j.ope_d_t,120) >= :beginDate
    AND
    CONVERT(VARCHAR(10),j.ope_d_t,120) <= :endDate
    AND
    (j.rework_code !='' AND j.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND j.j_s2 NOT IN (69, 691, 6, 9))
  UNION
  SELECT
    t.wheel_serial,
    t.rework_code
  FROM
    t_machine_record t
    INNER JOIN rework_code r ON t.rework_code = r.code
  WHERE
    CONVERT(VARCHAR(10),t.ope_d_t,120) >= :beginDate
    AND
    CONVERT(VARCHAR(10),t.ope_d_t,120) <= :endDate
    AND
    (t.rework_code !='' AND t.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND t.t_s2 NOT IN (138, 8))
  UNION
  SELECT
    k.wheel_serial,
    k.rework_code
  FROM
    k_machine_record k
    INNER JOIN rework_code r ON k.rework_code = r.code
  WHERE
    CONVERT(VARCHAR(10),k.ope_d_t,120) >= :beginDate
    AND
    CONVERT(VARCHAR(10),k.ope_d_t,120) <= :endDate
    AND
    (k.rework_code !='' AND k.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND k.k_s2 NOT IN (30, 40))
  UNION
  SELECT
    w.wheel_serial,
    w.rework_code
  FROM
    w_machine_record w
    INNER JOIN rework_code r ON w.rework_code = r.code
  WHERE
    CONVERT(VARCHAR(10),w.ope_d_t,120) >= :beginDate
    AND
    CONVERT(VARCHAR(10),w.ope_d_t,120) <= :endDate
    AND
    (w.rework_code !='' AND w.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND w.w_s2 != 70)
)

SELECT
  t.wheel_serial,
  t.rework_code,
  w.design
  FROM
    t
    INNER JOIN wheel_record w ON t.wheel_serial = w.wheel_serial