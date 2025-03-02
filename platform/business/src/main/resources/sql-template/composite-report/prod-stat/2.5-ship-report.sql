WITH t1 AS (
SELECT
  design,
  CONVERT(varchar(100), shipped_date, 111) AS shipped_date,
  shipped_no,
  SUM (finished) AS finished,
  SUM(CASE WHEN balance_s = 'E3' THEN 1 ELSE 0 END) AS e3,
  SUM(CASE WHEN tape_size >= 844 AND wheel_w = 137 AND finished = 1 THEN 1 ELSE 0 END) AS tape_over_840,
  SUM(CASE WHEN wheel_w = 135 THEN 1 ELSE 0 END) AS wheel_135,
  customer_name
FROM v_train_customer_wheel
WHERE shipped_date >= :beginDate
  AND shipped_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  design,
  shipped_date,
  shipped_no,
  customer_name
),

t2 AS (
SELECT
  design,
  shipped_date,
  shipped_no,
  finished,
  e3,
  tape_over_840,
  finished - tape_over_840 AS tape_other,
  wheel_135,
  customer_name
FROM t1
)

SELECT * FROM (
  SELECT *
  FROM t2
  UNION ALL
  SELECT
    design,
    shipped_date,
    'total' AS shipped_no,
    SUM(finished) AS finished,
    SUM(e3) AS e3,
    SUM(tape_over_840) AS tape_over_840,
    SUM(tape_other) AS tape_other,
    SUM(wheel_135) AS wheel_135,
    '' AS customer_name
  FROM t2
  GROUP BY
    design,
    shipped_date
  UNION ALL
  SELECT
    design,
    'total' AS shipped_date,
    'total' AS shipped_no,
    SUM(finished) AS finished,
    SUM(e3) AS e3,
    SUM(tape_over_840) AS tape_over_840,
    SUM(tape_other) AS tape_other,
    SUM(wheel_135) AS wheel_135,
    '' AS customer_name
  FROM t2
  GROUP BY
    design
  UNION ALL
  SELECT
    'total' AS design,
    'total' AS shipped_date,
    'total' AS shipped_no,
    SUM(finished) AS finished,
    SUM(e3) AS e3,
    SUM(tape_over_840) AS tape_over_840,
    SUM(tape_other) AS tape_other,
    SUM(wheel_135) AS wheel_135,
    '' AS customer_name
  FROM t2
) t
ORDER BY
  design,
  shipped_date,
  shipped_no,
  customer_name
