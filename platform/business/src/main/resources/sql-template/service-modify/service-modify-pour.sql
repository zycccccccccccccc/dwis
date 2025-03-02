SELECT
  id,
  cast_date AS castDate,
  furnace_no AS furnaceNo,
  heat_seq AS heatSeq,
  tap_seq AS tapSeq
FROM heat_record
WHERE CASt_Date = :beginDate
