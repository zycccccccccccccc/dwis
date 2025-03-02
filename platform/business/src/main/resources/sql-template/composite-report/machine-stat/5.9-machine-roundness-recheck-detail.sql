
WITH t1 AS (
	SELECT
	  wheel_serial,
	  tape_size
	FROM
	  wheel_record
	WHERE
	  finished = 1
	  AND
	  tape_size < 845
	  AND
	  CONVERT(VARCHAR(10),last_barcode,120) >= :beginDate
	  AND
	  CONVERT(VARCHAR(10),last_barcode,120) <= :endDate
	  AND
	  check_code = ''
),

t2 AS (
	SELECT
	  wheel_serial,
	  tape_size,
	  operator,
	  create_time
	FROM
	  (SELECT
	  t1.wheel_serial,
	  t1.tape_size,
	  t.operator,
	  t.create_time,
	  ROW_NUMBER() OVER (PARTITION BY t.wheel_serial ORDER BY t.id ASC ) rn
	  FROM
	    t_machine_record t
	    INNER JOIN t1 ON t1.wheel_serial = t.wheel_serial
	  WHERE
		t.t_s2 = 51
	  )t
	WHERE
	  rn = 1
),

t3 AS (
	SELECT
	  wheel_serial,
	  create_time
	FROM
	  (SELECT
	  t1.wheel_serial,
	  f.create_time,
	  ROW_NUMBER() OVER (PARTITION BY f.wheel_serial ORDER BY f.id ASC ) rn
	  FROM
	    final_check_record f
	    INNER JOIN t1 ON t1.wheel_serial = f.wheel_serial
	  )t
	WHERE
	  rn = 1
)

SELECT
  RANK() OVER(ORDER BY t2.wheel_serial) AS SN,
  t2.wheel_serial,
  t2.tape_size,
  t2.operator
FROM
  t2
  INNER JOIN t3 ON t2.wheel_serial = t3.wheel_serial
WHERE
  t2.create_time < t3.create_time
ORDER BY
  t2.wheel_serial ASC
