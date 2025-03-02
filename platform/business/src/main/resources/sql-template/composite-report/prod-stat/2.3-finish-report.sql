WITH t1 AS (
SELECT
  design,
  CONVERT(varchar(10), last_barcode, 111) AS cast_date,
  CAST(bore_size AS varchar(32)) AS bore_size,
  SUM (finished) AS dock,
  SUM(CASE WHEN tape_size >= 844 AND wheel_w = 137 AND finished = 1 THEN 1 ELSE 0 END) AS big_tape,
  SUM(CASE WHEN balance_s = 'E3' AND finished = 1 THEN 1 ELSE 0 END) AS e3
FROM wheel_record
WHERE
  CONVERT(varchar(10), last_barcode, 120) >= :beginDate
  AND
  CONVERT(varchar(10), last_barcode, 120) <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY
  design,
  CONVERT(varchar(10), last_barcode, 111),
  bore_size
),

t2 AS (
SELECT
  design,
  cast_date,
  bore_size,
  dock,
  big_tape,
  dock - big_tape AS small_tape,
  e3
FROM
  t1
)

SELECT
  *
FROM (
  SELECT
    *
  FROM
    t2
  UNION ALL
  SELECT
    design,
    cast_date,
    'total' AS bore_size,
    SUM(dock) AS dock,
    SUM(big_tape) AS big_tape,
    SUM(small_tape) AS small_tape,
    SUM(e3) AS e3
  FROM
    t2
  GROUP BY
    design,
    cast_date
  UNION ALL
  SELECT
    design,
    'total' AS cast_date,
    'total' AS bore_size,
    SUM(dock) AS dock,
    SUM(big_tape) AS big_tape,
    SUM(small_tape) AS small_tape,
    SUM(e3) AS e3
    FROM
      t2
    GROUP BY
      design
  UNION ALL
  SELECT
    'total' AS design,
    'total' AS cast_date,
    'total' AS bore_size,
    SUM(dock) AS dock,
    SUM(big_tape) AS big_tape,
    SUM(small_tape) AS small_tape,
    SUM(e3) AS e3
  FROM t2
) t
ORDER BY
  design,
  cast_date,
  bore_size
