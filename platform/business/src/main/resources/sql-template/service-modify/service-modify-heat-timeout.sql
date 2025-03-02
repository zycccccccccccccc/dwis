
SELECT * FROM (
  SELECT
    heat.id,
    heat.heat_line AS heatLine,
    wheel_serial_1 AS wheelSerial1,
    wheel_serial_2 AS wheelSerial2,
    heat_code_1 AS heatCode1,
    heat_code_2 AS heatCode2,
    CONVERT(VARCHAR(10), hi_heat_in_date, 23) AS hiHeatInDate,
    CONVERT(VARCHAR(8), hi_heat_in_time, 24) AS hiHeatInTime,
    CONVERT(VARCHAR(10), hi_heat_out_date, 23) AS hiHeatOutDate,
    CONVERT(VARCHAR(8), hi_heat_out_time, 24) AS hiHeatOutTime,
    DATEDIFF(n, hi_heat_in_date, hi_heat_out_date) + DATEDIFF(n, hi_heat_in_time, hi_heat_out_time) AS hiTime,
    CONVERT(VARCHAR(10), low_heat_in_date, 23) AS lowHeatInDate,
    CONVERT(VARCHAR(8), low_heat_in_time, 24) AS lowHeatInTime,
    CONVERT(VARCHAR(10), low_heat_out_date, 23) AS lowHeatOutDate,
    CONVERT(VARCHAR(8), low_heat_out_time, 24) AS lowHeatOutTime,
    DATEDIFF(n, low_heat_in_date, low_heat_out_date) + DATEDIFF(n, low_heat_in_time, low_heat_out_time) AS lowTime,
    DATEDIFF(n, hi_heat_out_date, low_heat_in_date) + DATEDIFF(n, hi_heat_out_time, low_heat_in_time) AS hiOutLowInDiff,
    heat.xh AS xh
  FROM heat
  JOIN pour_record ON heat.wheel_serial_1 = pour_record.wheel_serial
  JOIN ladle_record ON pour_record.ladle_id = ladle_record.id
  JOIN heat_record ON ladle_record.heat_record_id = heat_record.id
  WHERE hi_heat_out_date >= :beginDate AND hi_heat_out_date <= :endDate
) ht
WHERE (ht.hiTime < 132 OR ht.hiTime > 400)
   OR (ht.lowTime < 180 OR ht.lowTime > 400)
   OR (ht.hiOutLowInDiff < 12 OR ht.hiOutLowInDiff > 25)
   OR ((lowHeatOutDate IS NULL OR lowHeatOutTime IS NULL) AND xh = 5)
ORDER BY heatLine, hiHeatInDate, hiHeatInTime
