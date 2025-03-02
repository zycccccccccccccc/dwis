SELECT
  wheel_serial,
  CASE
    WHEN balance_a >= 0   AND balance_a < 30  THEN 0
    WHEN balance_a >= 30  AND balance_a < 60  THEN 1
    WHEN balance_a >= 60  AND balance_a < 90  THEN 2
    WHEN balance_a >= 90  AND balance_a < 120 THEN 3
    WHEN balance_a >= 120 AND balance_a < 150 THEN 4
    WHEN balance_a >= 150 AND balance_a < 180 THEN 5
    WHEN balance_a >= 180 AND balance_a < 210 THEN 6
    WHEN balance_a >= 210 AND balance_a < 240 THEN 7
    WHEN balance_a >= 240 AND balance_a < 270 THEN 8
    WHEN balance_a >= 270 AND balance_a < 300 THEN 9
    WHEN balance_a >= 300 AND balance_a < 330 THEN 10
    ELSE 11
  END AS av,
  SUBSTRING(wheel_serial, 5, 1) AS line,
  balance_v,
  balance_a,
  CONVERT(varchar(100), ope_d_t, 20) AS ope_d_t,
  hold_code
FROM balance_record
WHERE ope_d_t >= :beginDate
  AND ope_d_t <= :endDate
  AND hold_code IN :holdCode
ORDER BY av, line