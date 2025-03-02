
SELECT
  h.heat_record_key,
  h.tap_seq,
  l.ladle_seq,
  w.wheel_serial,
  w.heat_code,
  w.test_code,
  w.rework_code,
  w.scrap_code,
  CASE WHEN w.confirmed_scrap = 1 THEN '是' ELSE '否' END AS isConfirmScrap,
  w.scrap_date,
  CASE WHEN w.finished = 1 THEN '是' ELSE '否' END AS isFinished,
  w.check_code,
  w.stock_date,
  w.shipped_no,
  t.shipped_date,
  c.customer_name
FROM
  wheel_record w
  INNER JOIN ladle_record l ON w.ladle_id = l.id
  INNER JOIN heat_record h ON h.id = l.heat_record_id
  LEFT JOIN train_no t ON w.shipped_no = t.shipped_no
  LEFT JOIN customer c ON t.customer_id = c.customer_id
WHERE
  h.cast_date = :castDate
  AND h.tap_seq = :tapSeq
ORDER BY
  l.ladle_seq,
  w.wheel_serial