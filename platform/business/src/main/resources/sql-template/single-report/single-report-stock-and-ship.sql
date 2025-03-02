SELECT
  wheel_record.check_code AS 验收编号,
  CONVERT(VARCHAR(8), wheel_record.stock_date, 11) AS 入库日期,
  CONVERT(VARCHAR(8), train_no.shipped_date, 11) AS 发运日期,
  wheel_record.shipped_no AS 合格证号,
  train_no.train_no AS 车皮号,
  wheel_record.shelf_number AS 串号,
  customer.customer_name AS 收货单位
FROM wheel_record
LEFT JOIN train_no ON wheel_record.shipped_no = train_no.shipped_no
LEFT JOIN customer ON train_no.customer_id = customer.customer_id
WHERE wheel_record.wheel_serial = :wheelSerial
