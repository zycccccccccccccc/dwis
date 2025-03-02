WITH t1 AS (
  SELECT
    w.design,
    CONVERT(VARCHAR, w.wheel_w) AS wheel_w,
    CONVERT(VARCHAR, w.bore_size) AS bore_size,
    COUNT(1) AS cnt,
    SUM(CASE WHEN d.internal = 1 AND tape_size >= 844 AND wheel_w = 137 THEN 1 ELSE 0 END) AS big_tape,
    SUM(CASE WHEN d.internal = 1 AND (tape_size < 844 OR wheel_w < 137) THEN 1 ELSE 0 END) AS small_tape,
    SUM(CASE WHEN balance_s = 'E3' THEN 1 ELSE 0 END) AS e3,
    SUM(CASE WHEN check_code != '' AND stock_date IS NOT NULL THEN 1 ELSE 0 END) AS stock
  FROM
    wheel_record w
    INNER JOIN design d ON w.design = d.design
  WHERE
    finished = 1
    AND shipped_no IS NULL
    AND confirmed_scrap = 0
  <#if design??>
    AND w.design IN :design
  </#if>
  GROUP BY
    w.design,
    w.wheel_w,
    bore_size
)

SELECT
  *
FROM (
  SELECT
    *
  FROM
    t1
  UNION ALL
  SELECT
  	design,
  	wheel_w,
  	'total' AS bore_size,
  	SUM(cnt) AS cnt,
  	SUM(big_tape) AS big_tape,
  	SUM(small_tape) AS small_tape,
  	SUM(e3) AS e3,
  	SUM(stock) AS stock
  FROM
  	t1
  GROUP BY
  	design,
  	wheel_w
  UNION ALL
  SELECT
    design,
    'total' AS wheel_w,
    'total' AS bore_size,
    SUM(cnt) AS cnt,
    SUM(big_tape) AS big_tape,
    SUM(small_tape) AS small_tape,
    SUM(e3) AS e3,
    SUM(stock) AS stock
  FROM
    t1
  GROUP BY
    design
  UNION ALL
  SELECT
    'total' AS design,
    'total' AS wheel_w,
    'total' AS bore_size,
    SUM(cnt) AS cnt,
    SUM(big_tape) AS big_tape,
    SUM(small_tape) AS small_tape,
    SUM(e3) AS e3,
    SUM(stock) AS stock
  FROM
    t1
) t
ORDER BY
  design,
  wheel_w,
  bore_size
