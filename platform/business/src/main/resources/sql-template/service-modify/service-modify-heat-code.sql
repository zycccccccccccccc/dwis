
SELECT
  id,
  heat.heat_line AS heatLine,
  wheel_serial_1 AS wheelSerial1,
  wheel_serial_2 AS wheelSerial2,
  heat_code_1 AS heatCode1,
  heat_code_2 AS heatCode2,
  CONVERT(VARCHAR(10), hi_heat_in_date, 23) AS hiHeatInDate,
  CONVERT(VARCHAR(8), hi_heat_in_time, 24) AS hiHeatInTime,
  CONVERT(VARCHAR(10), hi_heat_out_date, 23) AS hiHeatOutDate,
  CONVERT(VARCHAR(8), hi_heat_out_time, 24) AS hiHeatOutTime,
  CONVERT(VARCHAR(10), low_heat_in_date, 23) AS lowHeatInDate,
  CONVERT(VARCHAR(8), low_heat_in_time, 24) AS lowHeatInTime,
  CONVERT(VARCHAR(10), low_heat_out_date, 23) AS lowHeatOutDate,
  CONVERT(VARCHAR(8), low_heat_out_time, 24) AS lowHeatOutTime
FROM heat
WHERE (heat_code_1 != '' OR heat_code_2 != '')
AND low_heat_in_date >= :beginDate AND low_heat_in_date <= :endDate
ORDER BY heatLine, hiHeatInDate, hiHeatInTime
