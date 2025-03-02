WITH t1 AS (
  SELECT
    w.design,
    w.bore_size,
    SUM (CASE WHEN w.balance_s= 'E3' THEN 1 ELSE 0 END ) AS e3,
    COUNT (w.wheel_serial) AS amount,
    SUM (CASE WHEN w.design LIKE '840%' AND w.wheel_w = 137 AND w.tape_size >= 844 THEN 1 ELSE 0 END) AS max_tape
  FROM
    wheel_record w INNER JOIN balance_record b ON w.balance_id = b.id
  WHERE
    w.last_balance >= :beginDate
    AND
    w.last_balance < :endDate
    AND
    w.finished = 1
    AND
    (w.x_finished_id IS NULL AND w.re_weight_id IS NULL AND w.k_finished_id IS NULL)
  <#if shiftBalance??>
    AND ${shiftBalance}
  </#if>
  <#if staffId??>
    AND b.inspector_id = :staffId
  </#if>
  GROUP BY
    w.design,
    w.bore_size
),

t2 AS (
  SELECT
    design,
    CAST(bore_size AS varchar(32)) AS bore_size,
    e3,
    amount,
    max_tape,
    amount - max_tape AS small_tape
  FROM
    t1
),

t3 AS (
  SELECT
	*
  FROM
	t2
  UNION ALL
  SELECT
	design,
	'total' AS bore_size,
	SUM(e3) AS e3,
	SUM(amount) AS amount,
	SUM(max_tape) AS max_tape,
	SUM(small_tape) AS small_tape
  FROM
	t2
  GROUP BY
	design
  UNION ALL
  SELECT
	'total' AS design,
	'total' AS bore_size,
	SUM(e3) AS e3,
	SUM(amount) AS amount,
	SUM(max_tape) AS max_tape,
	SUM(small_tape) AS small_tape
  FROM
	t2
)

SELECT
  *
FROM
  t3
ORDER BY
  design,
  bore_size