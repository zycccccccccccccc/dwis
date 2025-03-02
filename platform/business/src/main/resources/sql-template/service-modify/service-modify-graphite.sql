
SELECT
  ladle_record.ladle_record_key AS ladleRecordKey,
  pour_record.wheel_serial AS wheelSerial,
  pour_record.design AS pourDesign,
  pour_record.cope_no AS graphiteNo,
  graphite.design AS graphiteDesign
FROM pour_record
INNER JOIN ladle_record ON pour_record.ladle_id = ladle_record.id
INNER JOIN design ON pour_record.design = design.design
INNER JOIN graphite ON pour_record.cope_no = graphite.graphite
WHERE  cd = 0
AND design.base_design <> graphite.design
AND pour_record.cast_date >= :beginDate AND pour_record.cast_date <= :endDate
UNION ALL
SELECT
  ladle_record.ladle_record_key AS ladleRecordKey,
  pour_record.wheel_serial AS wheelSerial,
  pour_record.design AS pourDesign,
  pour_record.drag_no AS graphiteNo,
  graphite.design AS graphiteDesign
FROM pour_record
INNER JOIN ladle_record ON pour_record.ladle_id = ladle_record.id
INNER JOIN design ON pour_record.design = design.design
INNER JOIN graphite ON pour_record.drag_no = graphite.graphite
WHERE cd = 1
AND design.base_design <> graphite.design
AND pour_record.cast_date >= :beginDate AND pour_record.cast_date <= :endDate
ORDER BY pour_record.wheel_serial