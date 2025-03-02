WITH t AS (
    SELECT
      CONVERT(VARCHAR(10),t.ope_d_t,120) AS ope_d_t,
      t.operator,
      t.machine_no,
      w.design,
      COUNT(t.wheel_serial) AS quantity,
      SUM (CASE WHEN t.t_s2 = 138 THEN 1 ELSE 0 END ) AS tag138,
      SUM (CASE WHEN t.t_s2 = 8 THEN 1 ELSE 0 END) AS tag8,
      SUM (CASE WHEN t.t_s2 = 51 THEN 1 ELSE 0 END) AS tag51,
      SUM (CASE WHEN t.t_s2 = 52 THEN 1 ELSE 0 END) AS tag52,
      SUM (CASE WHEN t.t_s2 = 53 THEN 1 ELSE 0 END) AS tag53,
      SUM (CASE WHEN t.t_s2 = 54 THEN 1 ELSE 0 END) AS tag54,
      SUM (CASE WHEN t.t_s2 = 55 THEN 1 ELSE 0 END) AS tag55,
      SUM (CASE WHEN t.t_s2 = 56 THEN 1 ELSE 0 END) AS tag56,
      SUM (CASE WHEN t.rework_code !='' AND t.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND t.t_s2 NOT IN (138, 8) THEN 1 ELSE 0 END) AS rework_quantity
    FROM
      t_machine_record t
      INNER JOIN wheel_record w ON t.wheel_serial = w.wheel_serial
      LEFT JOIN rework_code r ON t.rework_code = r.code
    WHERE
      CONVERT(VARCHAR(10),t.ope_d_t,120) >= :beginDate
      AND
      CONVERT(VARCHAR(10),t.ope_d_t,120) <= :endDate
<#if shift??>
    AND ${shift}
</#if>
    GROUP BY
      CONVERT(VARCHAR(10),t.ope_d_t,120),
      t.operator,
      t.machine_no,
      w.design
)

SELECT
  *
FROM
  t
ORDER BY
  ope_d_t,
  operator
