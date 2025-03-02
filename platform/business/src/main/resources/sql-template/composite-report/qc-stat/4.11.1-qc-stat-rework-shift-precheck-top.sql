SELECT pre_check_record.inspector_id,
       COUNT(pre_check_record.wheel_serial) AS pre_amount
FROM pre_check_record
WHERE pre_check_record.ope_d_t > :beginDate
  AND pre_check_record.ope_d_t <= :endDate
GROUP BY pre_check_record.inspector_id
