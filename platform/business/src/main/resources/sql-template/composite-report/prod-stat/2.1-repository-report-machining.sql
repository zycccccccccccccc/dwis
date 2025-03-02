WITH t1 AS (
SELECT
  design,
  wheel_serial,
  CASE
    WHEN pre > 0 THEN '进加工'
    WHEN h.hi_heat_in_date IS NOT NULL AND h.hi_heat_out_date IS NOT NULL THEN '热处理后'
    ELSE '没热处理'
  END AS process
FROM
  wheel_record w LEFT JOIN heat h ON w.heat_id = h.id
WHERE
  finished = 0
  AND shipped_no IS NULL
  AND confirmed_scrap = 0
<#if design??>
  AND design IN :design
</#if>
)

SELECT * FROM (
  SELECT
    design,
    process,
    COUNT(1) AS cnt
  FROM
    t1
  GROUP BY
    design,
    process
  UNION ALL
  SELECT
    design,
    'total' AS process,
    COUNT(1) AS cnt
  FROM
    t1
  GROUP BY
    design
  UNION ALL
  SELECT
    'total' AS design,
    'total' AS process,
    COUNT(1) AS cnt
  FROM
    t1
) t
ORDER BY
  design,
  process
