WITH t1 AS (
SELECT
  wheel_serial,
  scrap_code,
  CONVERT(VARCHAR(10),MAX(create_date),120) AS maxDate
FROM
  scrap_record
WHERE CONVERT(VARCHAR(10),create_date,120) >= :beginDate
  AND CONVERT(VARCHAR(10),create_date,120) <= :endDate
  AND confirmed_scrap = 1
  AND inspector_id = :opeId
GROUP BY wheel_serial, scrap_code
)

SELECT
  wheel_record.wheel_serial,
  wheel_record.design,
  wheel_record.scrap_code
FROM
  wheel_record
INNER JOIN t1 ON wheel_record.wheel_serial = t1.wheel_serial AND wheel_record.scrap_code = t1.scrap_code AND wheel_record.scrap_date = t1.maxDate
WHERE wheel_record.confirmed_scrap = 1
GROUP BY wheel_record.wheel_serial, wheel_record.design, wheel_record.scrap_code
ORDER BY wheel_record.design,wheel_record.scrap_code
