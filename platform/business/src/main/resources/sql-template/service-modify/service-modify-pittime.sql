SELECT
  wheel_serial AS wheelSerial,
  ladle_record_key AS ladleRecordKey,
  scrap_code AS scrapCode,
  CONVERT(VARCHAR(20), pour_d_t, 20) AS pourDT,
  CONVERT(VARCHAR(8), open_time_act, 24) AS openTimeAct,
  CONVERT(VARCHAR(10), in_pit_date_time, 23) AS inPitDate,
  CONVERT(VARCHAR(8), in_pit_date_time, 24) AS inPitTime,
  DATEDIFF(n, open_time_act, in_pit_date_time) AS timeDiff
FROM pour_record
JOIN ladle_record ON pour_record.ladle_id = ladle_record.id
WHERE DATEDIFF(n, open_time_act, in_pit_date_time) < 0 OR DATEDIFF(n, open_time_act, in_pit_date_time) > 20
AND pour_record.cast_date >= :beginDate AND cast_date <= :endDate
ORDER BY pour_record.cast_date DESC
