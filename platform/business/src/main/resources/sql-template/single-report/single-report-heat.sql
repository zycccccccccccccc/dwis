SELECT
  wheel_record.wheel_serial,
  heat.heat_line AS 炉号,
  dbo.getShortDateTime1(heat.hi_heat_in_date, heat.hi_heat_in_time) AS 高进时间,
  dbo.getShortDateTime1(heat.hi_heat_out_date, heat.hi_heat_out_time) AS 高出时间,
  dbo.getShortDateTime1(heat.low_heat_in_date, heat.low_heat_in_time) AS 低进时间,
  dbo.getShortDateTime1(heat.low_heat_out_date, heat.low_heat_out_time) AS 低出时间,
  CONVERT(VARCHAR(20), hi_heat_prework_record.tread_quench_delay, 108) AS 淬火延迟时间,
  hi_heat_prework_record.water_pressure AS 水压,
  hi_heat_prework_record.water_temp AS 淬火水温度,
  hi_heat_in_operator AS 高进操作工,
  hi_heat_out_operator AS 高出操作工,
  low_heat_in_operator AS 低进操作工,
  Low_Heat_Out_Operator AS 低出操作工
FROM wheel_record
INNER JOIN heat ON wheel_record.heat_id = heat.id
LEFT JOIN hi_heat_prework_record ON heat.h_id = hi_heat_prework_record.id
WHERE wheel_record.wheel_serial = :wheelSerial
