
WITH machine AS (
SELECT
  cast_date,
  COUNT(1) AS machine
FROM (
  SELECT
    CONVERT(VARCHAR(10), t.ope_d_t, 120) AS cast_date
  FROM t_machine_record t
  INNER JOIN machine_record m ON t.id = m.t_id
  WHERE t.ope_d_t >= :beginDate
    AND t.ope_d_t < :endDate
    AND t.t_s2 = 138
  <#if design??>
    AND m.design IN :design
  </#if>
) s
GROUP BY cast_date
)

SELECT
  cast_date,
  ISNULL(machine, 0) AS machine
FROM machine
UNION ALL
SELECT
  'total' AS cast_date,
  ISNULL(SUM(machine), 0)
FROM machine
ORDER BY cast_date
