WITH t AS (
    SELECT
      CONVERT(VARCHAR(10),wf.ope_d_t,120) AS ope_d_t,
      wf.operator,
      wf.machine_no,
      w.design,
      COUNT(wf.wheel_serial) AS quantity,
      SUM (CASE WHEN wf.w_s2 = 70 THEN 1 ELSE 0 END ) AS tag70,
      SUM (CASE WHEN wf.w_s2 = 701 THEN 1 ELSE 0 END) AS tag701,
      SUM (CASE WHEN wf.w_s2 = 48 THEN 1 ELSE 0 END) AS tag48,
      SUM (CASE WHEN wf.w_s2 = 47 THEN 1 ELSE 0 END) AS tag47,
      SUM (CASE WHEN wf.rework_code !='' AND wf.rework_code != '8C' AND r.rework_flag LIKE 'F%' AND wf.w_s2 != 70 THEN 1 ELSE 0 END) AS rework_quantity
    FROM
      w_machine_record wf
      INNER JOIN wheel_record w ON wf.wheel_serial = w.wheel_serial
      LEFT JOIN rework_code r ON wf.rework_code = r.code
    WHERE
      CONVERT(VARCHAR(10),wf.ope_d_t,120) >= :beginDate
      AND
      CONVERT(VARCHAR(10),wf.ope_d_t,120) <= :endDate
<#if shift??>
    AND ${shift}
</#if>
    GROUP BY
      CONVERT(VARCHAR(10),wf.ope_d_t,120),
      wf.operator,
      wf.machine_no,
      w.design
)

SELECT
  *
FROM
  t
ORDER BY
  ope_d_t,
  operator
