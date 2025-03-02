WITH a AS (
    SELECT
        j_machine_record.machine_no,
        j_machine_record.wheel_serial,
        j_machine_record.ope_d_t,
        j_machine_record.rework_code AS rework_hold_code,
        j_machine_record.is_check,
        j_machine_record.is_inspec_check,
        calibra_wheel.is_check AS is_measure_check,
        j_machine_record.J_S2 AS s2,
        j_machine_record.J_S1 AS s1,
        j_machine_record.F AS data1,
        j_machine_record.D2_Dia AS data2,
        CAST(j_machine_record.D2_Cir AS varchar) AS data3
    FROM
        j_machine_record
            LEFT  JOIN calibra_wheel ON j_machine_record.cali_wheel_id = calibra_wheel.id
    WHERE
            j_machine_record.operator = :staffId
      AND
            CONVERT(VARCHAR(10),j_machine_record.ope_d_t,120) >= :beginDate
      AND
            CONVERT(VARCHAR(10),j_machine_record.ope_d_t,120) <= :endDate
    UNION ALL
    SELECT
        machine_no,
        wheel_serial,
        ope_d_t,
        rework_code AS rework_hold_code,
        is_check,
        is_inspec_check,
        is_measure_check,
        t_s2 AS s2,
        t_s1 AS s1,
        flange_tread_profile AS data1,
        t_chamfer AS data2,
        CAST(rolling_circle_dia AS varchar) AS data3
    FROM
        t_machine_record
    WHERE
            operator = :staffId
      AND
            CONVERT(VARCHAR(10),t_machine_record.ope_d_t,120) >= :beginDate
      AND
            CONVERT(VARCHAR(10),t_machine_record.ope_d_t,120) <= :endDate
    UNION ALL
    SELECT
        machine_no,
        wheel_serial,
        ope_d_t,
        rework_code AS rework_hold_code,
        is_check,
        is_inspec_check,
        is_measure_check,
        k_s2 AS s2,
        k_s1 AS s1,
        concentricity AS data1,
        bore_dia AS data2,
        CASE location WHEN '1' THEN '左工位' ELSE '右工位' END AS data3
    FROM
        k_machine_record
    WHERE
            operator = :staffId
      AND
            CONVERT(VARCHAR(10),k_machine_record.ope_d_t,120) >= :beginDate
      AND
            CONVERT(VARCHAR(10),k_machine_record.ope_d_t,120) <= :endDate
    UNION ALL
    SELECT
        machine_no,
        wheel_serial,
        ope_d_t,
        hold_code AS rework_hold_code,
        0 AS is_check,
        is_inspec_check,
        0 AS is_measure_check,
        NULL AS s2,
        NULL AS s1,
        chuck1 AS data1,
        pad1 AS data2,
        CAST(deviation AS varchar) AS data3
    FROM
        q_machine_record
    WHERE
            operator = :staffId
      AND
            CONVERT(VARCHAR(10),q_machine_record.ope_d_t,120) >= :beginDate
      AND
            CONVERT(VARCHAR(10),q_machine_record.ope_d_t,120) <= :endDate
    union ALL
    SELECT
        machine_no,
        wheel_serial,
        ope_d_t,
        rework_code AS rework_hold_code,
        machined_step AS is_check,
        is_inspec_check,
        is_measure_check,
        w_s2 AS s2,
        w_s1 AS s1,
        hub_exradius AS data1,
        plate_thickness AS data2,
        CAST(rim_thickness AS varchar) AS data3
    FROM
        w_machine_record
    WHERE
            operator = :staffId
      AND
            CONVERT(VARCHAR(10),w_machine_record.ope_d_t,120) >= :beginDate
      AND
            CONVERT(VARCHAR(10),w_machine_record.ope_d_t,120) <= :endDate
)

SELECT * FROM a ORDER BY ope_d_t
