WITH t AS (
  SELECT
    cope_no,
    SUM (CASE WHEN vibrate_wheel = 1 THEN 1 ELSE 0 END ) AS vibrate_sum,
	SUM (CASE WHEN off_pants = 1 THEN 1 ELSE 0 END ) AS off_sum
  FROM
    pour_record
  WHERE
    CONVERT(VARCHAR(10),open_time_act,120) >= :beginDate
	AND
	CONVERT(VARCHAR(10),open_time_act,120) <= :endDate
  GROUP BY
    cope_no
)

SELECT
  t.*
FROM
  t
WHERE
  t.vibrate_sum > 0
  OR
  t.off_sum > 0
ORDER BY
  vibrate_sum DESC,
  off_sum DESC