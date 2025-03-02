
WITH total AS (
SELECT
  CONVERT(VARCHAR(10), dateadd(d, number, :beginDate), 120) AS cast_date
FROM
  master..spt_values
WHERE type = 'p'
  AND number < DATEDIFF(d, :beginDate, :endDate)
)

SELECT
  cast_date
FROM total
UNION ALL
SELECT
  'total' AS cast_date

ORDER BY cast_date
