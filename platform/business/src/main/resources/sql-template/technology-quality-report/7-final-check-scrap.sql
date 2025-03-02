SELECT
  f.wheel_serial,
  SUBSTRING(CONVERT(varchar(100), f.ope_d_t, 20), 1, 16) AS ope_d_t,
  w.scrap_code,
  CONVERT(varchar(100), w.scrap_date, 23) AS scrap_date,
  w.confirmed_scrap
FROM
  final_check_record f
  INNER JOIN wheel_record w ON w.wheel_serial = f.wheel_serial
WHERE f.ope_d_t >= :beginDate
  AND f.ope_d_t < :endDate
  AND f.scrap_code IN :scrapCode
  AND w.scrap_code IN :scrapCode
ORDER BY
  ope_d_t
