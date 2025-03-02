SELECT
  chemistry_detail.c,
  chemistry_detail.si,
  chemistry_detail.mn,
  chemistry_detail.p,
  chemistry_detail.s,
  chemistry_detail.al,
  chemistry_detail.cr,
  chemistry_detail.ni,
  chemistry_detail.mo,
  chemistry_detail.cu,
  chemistry_detail.v,
  chemistry_detail.ti,
  chemistry_detail.sn,
  chemistry_detail.nb
FROM wheel_record
INNER JOIN chemistry_detail ON wheel_record.ladle_id = chemistry_detail.ladle_id
WHERE wheel_record.wheel_serial = :wheelSerial
