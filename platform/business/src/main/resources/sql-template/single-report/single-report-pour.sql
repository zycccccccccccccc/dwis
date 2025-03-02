SELECT
  heat_record.tap_seq AS 出钢号,
  ladle_record.ladle_record_key AS 小包关键字,
  pourleader_id AS 浇注工长,
  model_id AS 造型工长,
  dbo.getShortDateTime(pour_record.pour_d_t) AS 浇注时间,
  ladle_record.ladle_temp AS 浇注温度,
  pour_record.cope_no AS 上箱石墨号,
  pour_record.drag_no AS 下箱石墨号,
  pit_records.pit_no AS 桶号,
  dbo.getShortDateTime(pour_record.open_time_act) AS 实际开箱时间,
  dbo.getShortDateTime(pour_record.in_pit_date_time) AS 进桶时间,
  dbo.getShortDateTime(pit_records.out_pit_d_t_act) AS 出桶时间
FROM pour_record
INNER JOIN ladle_record ON pour_record.ladle_id = ladle_record.id
INNER JOIN heat_record ON heat_record.id = ladle_record.heat_record_id
INNER JOIN pit_records ON pour_record.pit_seq = pit_records.pit_seq
WHERE pour_record.wheel_serial = :wheelSerial
