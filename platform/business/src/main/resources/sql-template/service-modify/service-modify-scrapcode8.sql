
SELECT
  pour_record.wheel_serial AS wheelSerial,
  CONVERT(VARCHAR(20), pit_records.in_pit_d_t, 20) AS inPitDT,
  pour_record.pit_seq AS pitSeq,
  pit_records.pit_no AS pitNo,
  CONVERT(VARCHAR(8), pit_records.out_pit_d_t_cal, 24) AS outPitDTCal,
  CONVERT(VARCHAR(8), pit_records.out_pit_d_t_act, 24) AS outPitDTAct
FROM pour_record
INNER JOIN pit_records ON pour_record.pit_seq = pit_records.pit_seq
WHERE pour_record.scrap_code = '8'
AND pour_record.cast_date >= :beginDate AND pour_record.cast_date <= :endDate
