WITH t AS (
	SELECT
	  CONVERT(VARCHAR(10),ope_d_t,120) AS ope_d_t,
	  wheel_serial
	FROM
	  j_machine_record
	WHERE
	  CONVERT(VARCHAR(10),ope_d_t,120) >= :beginDate
	  AND
	  CONVERT(VARCHAR(10),ope_d_t,120) <= :endDate
	  AND
	  j_s2 = 6
	GROUP BY
	  CONVERT (VARCHAR(10),ope_d_t,120),
	  wheel_serial
),

t1 AS (
    SELECT
      ope_d_t,
      COUNT(wheel_serial) AS j6_count
    FROM
      t
    GROUP BY
      ope_d_t
),

t2 AS (
	SELECT
	  CONVERT(VARCHAR(10),j.ope_d_t,120) AS ope_d_t,
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
	  CONVERT(VARCHAR(10),j.ope_d_t,120)
),

t4 AS (
	SELECT
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
	  wheel_serial
),

t5 AS (
	SELECT
	  CONVERT(VARCHAR(10),j.ope_d_t,120) AS ope_d_t,
	  COUNT(j.wheel_serial) AS j_machine_count
	FROM
	  j_machine_record j
	  INNER JOIN t4 ON t4.wheel_serial = j.wheel_serial AND t4.min_opt = j.ope_d_t
	GROUP BY
	  CONVERT(VARCHAR(10),j.ope_d_t,120)
)

SELECT
  t5.ope_d_t,
  t5.j_machine_count,
  t1.j6_count,
  CONVERT(VARCHAR,CONVERT(FLOAT,CONVERT(DECIMAL(10, 2), 100.0 * t1.j6_count / t5.j_machine_count))) AS j6_j_machine_count,
  t2.sconf8s,
  t2.j6_sconf8s,
  (t2.sconf8s - t2.j6_sconf8s) AS miss6_count,
  CONVERT(VARCHAR,CONVERT(FLOAT,CONVERT(DECIMAL(10, 2), 100.0 * (t2.sconf8s - t2.j6_sconf8s) / t5.j_machine_count))) AS miss6_j_machine_count
FROM
  t5
  LEFT JOIN t2 ON t2.ope_d_t = t5.ope_d_t
  LEFT JOIN t1 ON t1.ope_d_t = t5.ope_d_t
GROUP BY
  t5.ope_d_t,
  t5.j_machine_count,
  t1.j6_count,
  t2.sconf8s,
  t2.j6_sconf8s
ORDER BY
  t5.ope_d_t