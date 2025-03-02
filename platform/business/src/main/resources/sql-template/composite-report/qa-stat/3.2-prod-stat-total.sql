WITH total AS (
SELECT
  COUNT(1) AS total,
  SUM(finished) AS finished_total,
  SUM(CASE WHEN pre > 0 AND finished = 0 THEN 1 ELSE 0 END) AS machine_total
FROM wheel_record w
WHERE confirmed_scrap = 0
  AND shipped_no IS NULL
),

other AS (
SELECT
  SUM(CASE WHEN pre = 0 AND (( h1.hi_heat_in_date IS NOT NULL AND h1.low_heat_in_date IS NOT NULL ) OR ( h2.hi_heat_in_date IS NOT NULL AND h2.low_heat_in_date IS NOT NULL )) THEN 1 ELSE 0 END) AS heated,
  SUM(CASE WHEN pre = 0 AND ( h1.hi_heat_in_date IS NULL OR h1.low_heat_in_date IS NULL ) AND ( h2.hi_heat_in_date IS NULL OR h2.low_heat_in_date IS NULL ) THEN 1 ELSE 0 END) AS unheat,
  SUM(CASE WHEN heat_code IN ('ARHT', 'BBOB') AND pre > 0 THEN 1 ELSE 0 END) AS machining_arht,
  SUM(CASE WHEN heat_code IN ('ARHT', 'BBOB') AND pre = 0 THEN 1 ELSE 0 END) AS unmachine_arht,
  SUM(xray_req) AS xray,
  SUM(CASE WHEN scrap_code <> '' THEN 1 ELSE 0 END) AS scrap
FROM wheel_record w
LEFT JOIN heat h1 ON w.heat_id = h1.id
LEFT JOIN heat h2 ON w.heat_id = h2.id
WHERE confirmed_scrap = 0
  AND shipped_no IS NULL
)

SELECT
  *
FROM total
JOIN other ON 1 = 1

