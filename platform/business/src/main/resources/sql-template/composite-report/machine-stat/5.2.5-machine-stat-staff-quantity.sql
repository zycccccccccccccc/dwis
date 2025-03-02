WITH t AS (
    SELECT
      CONVERT(VARCHAR(10),q.ope_d_t,120) AS ope_d_t,
      q.operator,
      q.machine_no,
      w.design,
      COUNT(q.wheel_serial) AS quantity,
      SUM (CASE WHEN q.hold_code = 'Q1' THEN 1 ELSE 0 END ) AS q1,
      SUM (CASE WHEN q.hold_code = 'Q2' THEN 1 ELSE 0 END ) AS q2
    FROM
      q_machine_record q
      INNER JOIN wheel_record w ON q.wheel_serial = w.wheel_serial
    WHERE
      CONVERT(VARCHAR(10),q.ope_d_t,120) >= :beginDate
      AND
      CONVERT(VARCHAR(10),q.ope_d_t,120) <= :endDate
<#if shift??>
    AND ${shift}
</#if>
    GROUP BY
      CONVERT(VARCHAR(10),q.ope_d_t,120),
      q.operator,
      q.machine_no,
      w.design
)

SELECT
  *
FROM
  t
ORDER BY
  ope_d_t,
  operator
