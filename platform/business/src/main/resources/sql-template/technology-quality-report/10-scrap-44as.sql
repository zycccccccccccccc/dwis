
WITH a1 AS (
SELECT
  w.wheel_serial,
	MIN(h.create_date_time) AS scrap_date
FROM heat h
JOIN wheel_record w ON h.wheel_serial_1 = w.wheel_serial
WHERE w.scrap_code IN :scrapCode
  AND h.create_date_time >= :beginDate
  AND h.create_date_time <= :endDate
GROUP BY w.wheel_serial
), a2 AS (
SELECT
  w.wheel_serial,
	MIN(h.create_date_time) AS scrap_date
FROM heat h
JOIN wheel_record w ON h.wheel_serial_2 = w.wheel_serial
WHERE w.scrap_code IN :scrapCode
  AND h.create_date_time >= :beginDate
  AND h.create_date_time <= :endDate
GROUP BY w.wheel_serial
)

SELECT * FROM (
  SELECT
    wheel_serial,
  	h.cut_id,
  	CONVERT(varchar(100), scrap_date, 111) AS scrap_date,
  	CONVERT(varchar(100), hi_heat_in_date, 111) AS hi_heat_in_date,
  	CONVERT(varchar(100), hi_heat_in_time, 108) AS hi_heat_in_time,
  	hi_heat_in_shift,
  	hi_heat_in_id,
  	hi_heat_in_operator,
  	heat_line
  FROM heat h JOIN a1 ON h.wheel_serial_1 = a1.wheel_serial
  UNION ALL
  SELECT
    wheel_serial,
  	h.cut_id,
  	CONVERT(varchar(100), scrap_date, 111) AS scrap_date,
  	CONVERT(varchar(100), hi_heat_in_date, 111) AS hi_heat_in_date,
  	CONVERT(varchar(100), hi_heat_in_time, 108) AS hi_heat_in_time,
  	hi_heat_in_shift,
  	hi_heat_in_id,
  	hi_heat_in_operator,
  	heat_line
  FROM heat h JOIN a2 ON h.wheel_serial_2 = a2.wheel_serial
) t
ORDER BY scrap_date