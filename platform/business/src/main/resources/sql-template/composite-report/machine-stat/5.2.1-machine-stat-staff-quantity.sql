WITH t AS (
    SELECT
      CONVERT(VARCHAR(10),j.ope_d_t,120) AS ope_d_t,
      j.operator,
      j.machine_no,
      w.design,
      COUNT(j.wheel_serial) AS quantity,
      SUM (CASE WHEN j.j_s2 = 69 THEN 1 ELSE 0 END ) AS tag69,
      SUM (CASE WHEN j.j_s2 = 691 THEN 1 ELSE 0 END) AS tag691,
      SUM (CASE WHEN j.j_s2 = 6 THEN 1 ELSE 0 END) AS tag6,
      SUM (CASE WHEN j.j_s2 = 45 THEN 1 ELSE 0 END) AS tag45,
      SUM (CASE WHEN j.j_s2 = 9 THEN 1 ELSE 0 END) AS tag9,
      SUM (CASE WHEN j.rework_code !='' AND j.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND j.j_s2 NOT IN (69, 691, 6, 9) THEN 1 ELSE 0 END) AS rework_quantity
    FROM
      j_machine_record j
      INNER JOIN wheel_record w ON j.wheel_serial = w.wheel_serial
      LEFT JOIN rework_code r ON j.rework_code = r.code
    WHERE
      CONVERT(VARCHAR(10),j.ope_d_t,120) >= :beginDate
      AND
      CONVERT(VARCHAR(10),j.ope_d_t,120) <= :endDate
<#if shift??>
    AND ${shift}
</#if>
    GROUP BY
      CONVERT(VARCHAR(10),j.ope_d_t,120),
      j.operator,
      j.machine_no,
      w.design
)

SELECT
  *
FROM
  t
ORDER BY
  ope_d_t,
  operator
