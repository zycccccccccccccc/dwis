WITH t AS (
	SELECT
	  operator,
	  wheel_serial
	FROM
	  j_machine_record
	WHERE
	  CONVERT(VARCHAR(10),ope_d_t,120) >=  :beginDate
	  AND
	  CONVERT(VARCHAR(10),ope_d_t,120) <= :endDate
	  AND
	  j_s2 = 6
	GROUP BY
	  operator,
	  wheel_serial
),

t1 AS (
    SELECT
      operator,
      COUNT(wheel_serial) AS j6_count
    FROM
      t
    GROUP BY
      operator
),

t2 AS (
	SELECT
	  j.operator,
	  SUM(CASE WHEN w.scrap_code = '88S' AND w.confirmed_scrap = 1 AND j.j_s2 = 69 THEN 1 ELSE 0 END) AS sconf8s,
	  SUM(CASE WHEN w.scrap_code = '88S' AND w.confirmed_scrap = 1 AND j.j_s2 = 6 THEN 1 ELSE 0 END) AS j6_sconf8s
	FROM
	  j_machine_record j
	  INNER JOIN wheel_record w ON j.wheel_serial = w.wheel_serial
	WHERE
	  CONVERT(VARCHAR(10),j.ope_d_t,120) >= :beginDate
	  AND
	  CONVERT(VARCHAR(10),j.ope_d_t,120) <= :endDate
	GROUP BY
	  j.operator
),

t4 AS (
	SELECT
	  operator,
	  wheel_serial,
	  MIN ( ope_d_t ) AS min_opt
	FROM
	  j_machine_record
	WHERE
	  CONVERT(VARCHAR(10),ope_d_t,120) >= :beginDate
	  AND
	  CONVERT(VARCHAR(10),ope_d_t,120) <= :endDate
	  AND
	  j_s2 = '69'
	GROUP BY
	  operator,
	  wheel_serial
),

t5 AS (
	SELECT
	  j.operator,
	  COUNT(j.wheel_serial) AS j_machine_count
	FROM
	  j_machine_record j
	  INNER JOIN t4 ON t4.wheel_serial = j.wheel_serial AND t4.min_opt = j.ope_d_t
	GROUP BY
	  j.operator
)


SELECT
  t5.operator,
  t5.j_machine_count,
  t1.j6_count,
  CONVERT(VARCHAR, CONVERT(FLOAT,CONVERT(DECIMAL(10, 2), 100.0 * t1.j6_count / t5.j_machine_count))) AS j6_j_machine_count,
  t2.sconf8s,
  t2.j6_sconf8s,
  (t2.sconf8s - t2.j6_sconf8s) AS miss6_count,
  CONVERT(VARCHAR,CONVERT(FLOAT,CONVERT(DECIMAL(10, 2), 100.0 * (t2.sconf8s - t2.j6_sconf8s) / t5.j_machine_count))) AS miss6_j_machine_count
FROM
  t5
  LEFT JOIN t2 ON t2.operator = t5.operator
  LEFT JOIN t1 ON t1.operator = t5.operator
GROUP BY
  t5.operator,
  t5.j_machine_count,
  t1.j6_count,
  t2.sconf8s,
  t2.j6_sconf8s
ORDER BY
  t5.j_machine_count