WITH t AS (
    SELECT
      CONVERT(VARCHAR(10),k.ope_d_t,120) AS ope_d_t,
      k.operator,
      k.machine_no,
      CASE WHEN k.location = '1' THEN '左工位' WHEN k.location = '2' THEN '右工位' ELSE '无' END AS location,
      w.design,
      COUNT(k.wheel_serial) AS quantity,
      SUM (CASE WHEN k.k_s2 = 40 THEN 1 ELSE 0 END ) AS tag40,
      SUM (CASE WHEN k.k_s2 = 44 THEN 1 ELSE 0 END ) AS tag44,
      SUM (CASE WHEN k.k_s2 = 43 THEN 1 ELSE 0 END) AS tag43,
      SUM (CASE WHEN k.k_s2 = 46 THEN 1 ELSE 0 END) AS tag46,
      SUM (CASE WHEN k.k_s2 = 30 THEN 1 ELSE 0 END) AS tag30,
      SUM (CASE WHEN k.rework_code !='' AND k.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND k.k_s2 NOT IN (30, 40) THEN 1 ELSE 0 END) AS rework_quantity
    FROM
      k_machine_record k
      INNER JOIN wheel_record w ON k.wheel_serial = w.wheel_serial
      LEFT JOIN rework_code r ON k.rework_code = r.code
    WHERE
      CONVERT(VARCHAR(10),k.ope_d_t,120) >= :beginDate
      AND
      CONVERT(VARCHAR(10),k.ope_d_t,120) <= :endDate
<#if shift??>
    AND ${shift}
</#if>
    GROUP BY
      CONVERT(VARCHAR(10),k.ope_d_t,120),
      k.operator,
      k.machine_no,
      w.design,
      k.location
)

SELECT
  *
FROM
  t
ORDER BY
  ope_d_t,
  operator
