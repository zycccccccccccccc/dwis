WITH t1 AS
    (SELECT
        final_check_record.wheel_serial,
        final_check_record.rework_code,
        CASE WHEN rework_code IN ('H4','H5') THEN 'J' ELSE CASE WHEN rework_code
            In ('H6','H7') THEN 'K' ELSE 'T' END END AS machine_class,
        MIN(final_check_record.ope_d_t) AS final_first_time
        FROM
            final_check_record
	WHERE
            final_check_record.rework_code IN ('H1','H2','H3','H4','H5','H6','H7','TR','TRH','66B','66C')
        AND
            final_check_record.ope_d_t >= :beginDate
        AND
            final_check_record.ope_d_t < :endDate
        GROUP BY
            final_check_record.wheel_serial,
            final_check_record.rework_code
)

SELECT
    t1.wheel_serial,
    machine_no,
    operator,
    t1.rework_code,
    ope_d_t,
    t1.final_first_time
FROM
    t1
INNER JOIN
    k_machine_record ON k_machine_record.wheel_serial = t1.wheel_serial
INNER JOIN
    machine_record ON machine_record.k_id = k_machine_record.id
WHERE
    k_machine_record.id IS NOT NULL
AND
    k_machine_record.k_s2 = 40
AND
    t1.machine_class = 'K'
