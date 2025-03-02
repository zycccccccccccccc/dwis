SELECT final_check_record.inspector_id,
       COUNT(final_check_record.wheel_serial) AS pre_amount
FROM final_check_record
WHERE final_check_record.ope_d_t > :beginDate
  AND final_check_record.ope_d_t <= :endDate
GROUP BY final_check_record.inspector_id
