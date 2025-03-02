WITH t AS (
	SELECT
	  k.wheel_serial,
	  k.operator
	FROM
	  k_machine_record k
	  INNER JOIN machine_record m ON k.id = m.k_id
	WHERE
	  CONVERT(VARCHAR(10),k.ope_d_t,120) >= :beginDate
      AND
      CONVERT(VARCHAR(10),k.ope_d_t,120) <= :endDate
),

t1 AS (
	SELECT
	  t.wheel_serial,
	  MIN(f.id) AS min_id
	FROM
	  t
	  INNER JOIN final_check_record f ON t.wheel_serial = f.wheel_serial
	  INNER JOIN rework_code r ON f.rework_code = r.code
	WHERE
	  r.rework_flag = 'F-JK-T'
    GROUP BY
	  t.wheel_serial
),

t2 AS (
	SELECT
	  a.team_leader_id,
	  t.operator,
	  f.wheel_serial,
	  f.rework_code
	FROM
	  t1
	  INNER JOIN final_check_record f ON f.id = t1.min_id
	  INNER JOIN t ON t.wheel_serial = t1.wheel_serial
	  INNER JOIN account a ON a.id = t.operator
    GROUP BY
	  a.team_leader_id,
	  t.operator,
	  f.wheel_serial,
	  f.rework_code
),

t3 AS (
    SELECT
      t2.team_leader_id,
	  t2.operator,
      t2.rework_code,
      COUNT(t2.wheel_serial) AS rework_count
    FROM
      t2
    GROUP BY
      t2.team_leader_id,
	  t2.operator,
      t2.rework_code
),

t4 AS (
    SELECT
      a.team_leader_id,
	  t.operator,
      COUNT(t.wheel_serial) AS machine_count
    FROM
      t
	  INNER JOIN account a ON a.id = t.operator
	  INNER JOIN machine_record m ON t.wheel_serial = m.wheel_serial
    GROUP BY
      a.team_leader_id,
	  t.operator
),

t5 AS (
    SELECT
      t3.team_leader_id,
      t3.operator,
      t3.rework_code,
      t3.rework_count,
      t4.machine_count
    FROM
	  t3
      INNER JOIN t4 ON t3.team_leader_id = t4.team_leader_id AND t3.operator = t4.operator
    UNION ALL
    SELECT
      t3.team_leader_id,
      t3.operator,
      'total' AS rework_code,
      SUM(t3.rework_count) AS rework_count,
      MAX(t4.machine_count) AS machine_count
    FROM
	  t3
      INNER JOIN t4 ON t3.team_leader_id = t4.team_leader_id AND t3.operator = t4.operator
    GROUP BY
      t3.team_leader_id,
      t3.operator
    UNION ALL
    SELECT
      team_leader_id,
      'total' AS operator,
      'total' AS rework_code,
      SUM(rework_count) AS rework_count,
      SUM(machine_count) AS machine_count
    FROM (
      SELECT
        t3.team_leader_id,
        t3.operator,
        'total' AS rework_code,
        SUM(t3.rework_count) AS rework_count,
        MAX(t4.machine_count) AS machine_count
      FROM t3
        INNER JOIN t4 ON t3.team_leader_id = t4.team_leader_id AND t3.operator = t4.operator
      GROUP BY
        t3.team_leader_id,
        t3.operator
  ) t0
  GROUP BY
    team_leader_id
)

SELECT
  *
FROM
  t5
ORDER BY
  team_leader_id,
  operator,
  rework_code
