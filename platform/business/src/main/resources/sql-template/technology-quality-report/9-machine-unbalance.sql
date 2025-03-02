
WITH a AS (
  SELECT
    DISTINCT wheel_serial
  FROM
    q_machine_record
  WHERE ope_d_t >= :beginDate
    AND ope_d_t < :endDate
    AND hold_code IN :holdCode
)

SELECT * FROM (
SELECT
  m.wheel_serial,
  machine_no,
  m.operator,
  CONVERT(varchar(100), m.j_s2) AS s2,
  CONVERT(varchar(100), m.j_s1) AS s1,
  '' AS hold_code,
  SUBSTRING(CONVERT(varchar(100), m.ope_d_t, 20), 1, 16) AS ope_d_t
FROM j_machine_record m
JOIN a ON m.wheel_serial = a.wheel_serial
UNION ALL
SELECT
  m.wheel_serial,
  machine_no,
  m.operator,
  CONVERT(varchar(100), m.t_s2) AS s2,
  CONVERT(varchar(100), m.t_s1) AS s1,
  '' AS hold_code,
  SUBSTRING(CONVERT(varchar(100), m.ope_d_t, 20), 1, 16) AS ope_d_t
FROM t_machine_record m
JOIN a ON m.wheel_serial = a.wheel_serial
UNION ALL
SELECT
  m.wheel_serial,
  machine_no,
  m.operator,
  CONVERT(varchar(100), m.k_s2) AS s2,
  CONVERT(varchar(100), m.k_s1) AS s1,
  '' AS hold_code,
  SUBSTRING(CONVERT(varchar(100), m.ope_d_t, 20), 1, 16) AS ope_d_t
FROM k_machine_record m
JOIN a ON m.wheel_serial = a.wheel_serial
UNION ALL
SELECT
  m.wheel_serial,
  machine_no,
  m.operator,
  '' AS s2,
  '' AS s1,
  hold_code,
  SUBSTRING(CONVERT(varchar(100), m.ope_d_t, 20), 1, 16) AS ope_d_t
FROM q_machine_record m
JOIN a ON m.wheel_serial = a.wheel_serial
UNION ALL
SELECT
  m.wheel_serial,
  machine_no,
  m.operator,
  CONVERT(varchar(100), m.w_s2) AS s2,
  CONVERT(varchar(100), m.w_s1) AS s1,
  '' AS hold_code,
  SUBSTRING(CONVERT(varchar(100), m.ope_d_t, 20), 1, 16) AS ope_d_t
FROM w_machine_record m
JOIN a ON m.wheel_serial = a.wheel_serial
) t
ORDER BY
  wheel_serial,
	machine_no,
	ope_d_t,
	operator
