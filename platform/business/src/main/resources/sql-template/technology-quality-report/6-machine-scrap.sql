WITH a AS (
SELECT
  m.wheel_serial,
  m.ope_d_t,
  m.j_s2 AS s2,
  m.machine_no,
  m.operator
FROM
  j_machine_record m
  INNER JOIN wheel_record w ON w.wheel_serial = m.wheel_serial
  INNER JOIN ladle_record l ON l.id = w.ladle_id
  INNER JOIN heat_record h ON h.id = l.heat_record_id
WHERE h.cast_date >= :beginDate
  AND h.cast_date <= :endDate
  AND m.rework_code IN :reworkCode
UNION ALL
SELECT
  m.wheel_serial,
  m.ope_d_t,
  m.t_s2 AS s2,
  m.machine_no,
  m.operator
FROM
  t_machine_record m
  INNER JOIN wheel_record w ON w.wheel_serial = m.wheel_serial
  INNER JOIN ladle_record l ON l.id = w.ladle_id
  INNER JOIN heat_record h ON h.id = l.heat_record_id
WHERE h.cast_date >= :beginDate
  AND h.cast_date <= :endDate
  AND m.rework_code IN :reworkCode
UNION ALL
SELECT
  m.wheel_serial,
  m.ope_d_t,
  m.k_s2 AS s2,
  m.machine_no,
  m.operator
FROM
  k_machine_record m
  INNER JOIN wheel_record w ON w.wheel_serial = m.wheel_serial
  INNER JOIN ladle_record l ON l.id = w.ladle_id
  INNER JOIN heat_record h ON h.id = l.heat_record_id
WHERE h.cast_date >= :beginDate
  AND h.cast_date <= :endDate
  AND m.rework_code IN :reworkCode
)

SELECT
  a.wheel_serial,
  CONVERT(varchar(100), a.ope_d_t, 23) AS ope_d_t,
  a.s2,
  a.machine_no,
  a.operator,
  ISNULL(w.scrap_code, '') AS scrap_code,
  w.confirmed_scrap,
  w.finished
FROM a
INNER JOIN wheel_record w on a.wheel_serial = w.wheel_serial
order by
  a.wheel_serial,
  a.ope_d_t;
