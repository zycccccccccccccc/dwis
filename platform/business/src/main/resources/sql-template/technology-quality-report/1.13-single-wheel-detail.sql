SELECT
  h.heat_record_key,
  h.tap_seq,
  l.ladle_seq,
  w.wheel_serial,
  SUBSTRING(CONVERT(VARCHAR(20), p.open_time_act, 108), 1, 5) AS open_time_act,
  w.scrap_code,
  CASE w.confirmed_scrap WHEN 1 THEN '√' ELSE '' END AS confirmed_scrap,
  ISNULL(CONVERT(VARCHAR(20), w.scrap_date, 11), '') AS scrap_date,
  w.pre,
  w.final AS final_count,
  w.ultra,
  w.balance,
  CASE w.finished WHEN 1 THEN '√' ELSE '' END AS finished,
  w.mec_serial
FROM heat_record h
INNER JOIN ladle_record l ON h.id = l.heat_record_id
INNER JOIN pour_record p ON l.id = p.ladle_id
INNER JOIN wheel_record w ON p.wheel_serial = w.wheel_serial
WHERE
  h.cast_date = :beginDate
ORDER BY
  h.tap_seq,
  l.ladle_seq,
  p.record_created