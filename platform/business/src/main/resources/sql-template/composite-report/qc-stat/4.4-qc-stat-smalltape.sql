SELECT wheel_record.wheel_serial,wheel_record.design,wheel_record.wheel_w,wheel_record.tape_size,
       t2.last_scrap_code AS scrap_code
FROM
    wheel_record LEFT JOIN (SELECT DISTINCT
                                t1.wheel_serial,
                                LAST_VALUE(t1.scrap_code) over(PARTITION BY t1.wheel_serial
                                ORDER BY ope_d_t ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS last_scrap_code
                            FROM
                                (SELECT
                                     wheel_serial,
                                     scrap_code,
                                     ope_d_t
                                 FROM
                                     inspection_record
                                 WHERE
                                     scrap_code != ''
                                 UNION
                                 SELECT
                                     wheel_serial,
                                     scrap_code,
                                     ope_d_t
                                 FROM
                                     scrap_record
                                 WHERE
                                     scrap_code != ''
                                 UNION
                                 SELECT
                                     wheel_serial,
                                     scrap_code,
                                     ope_d_t
                                 FROM
                                     final_check_record
                                 WHERE
                                     Scrap_Code != ''
                                 UNION
                                 SELECT
                                     wheel_serial,
                                     scrap_code,
                                     ope_d_t
                                 FROM
                                     ultra_record
                                 WHERE
                                     scrap_code != ''
                                 UNION
                                 SELECT
                                     wheel_serial,
                                     scrap_code,
                                     ope_d_t
                                 FROM
                                     balance_record
                                 WHERE
                                     scrap_code != ''
                                 UNION
                                 SELECT
                                     wheel_serial,
                                     scrap_code ,
                                     ope_d_t
                                 FROM
                                     pre_check_record
                                 WHERE
                                     scrap_code != ''
                                 UNION
                                 SELECT
                                     wheel_serial,
                                     scrap_code,
                                     ope_d_t
                                 FROM
                                     correct_wheel_record
                                 WHERE
                                     scrap_code != ''
                                 UNION
                                 SELECT
                                     wheel_serial,
                                     scrap_code,
                                     ope_d_t
                                 FROM
                                     magnetic_record
                                 WHERE
                                     scrap_code != '') t1
                            GROUP BY
                                t1.wheel_serial,t1.ope_d_t,t1.scrap_code) t2
                           ON wheel_record.wheel_serial = t2.wheel_serial
WHERE
        CONVERT(VARCHAR(7),wheel_record.last_barcode,120) = SUBSTRING(:beginDate,1,7)
  AND (wheel_record.wheel_w = 135 OR wheel_record.tape_size < 840) AND wheel_record.finished = 1
  AND wheel_record.design != 'CJ33'
ORDER BY t2.last_scrap_code


