WITH t AS (
    SELECT
      CONVERT(VARCHAR(7),t.ope_d_t, 120) AS machine_date,
      SUM(CASE WHEN rework_code IN ('9A','9C','67R') AND t_s2 IN (52,53,54) THEN 1 ELSE 0 END) AS cast_flat,
      SUM(CASE WHEN rework_code IN ('23','88','67F') AND t_s2 IN (51,54) THEN 1 ELSE 0 END) AS cast_tap,
      SUM(CASE WHEN rework_code IN ('H1','H2','65') AND t_s2 IN (52,53,54) THEN 1 ELSE 0 END) AS machine_flat,
      SUM(CASE WHEN rework_code IN ('H3','H4','H5','65') AND t_s2 IN (51,54) THEN 1 ELSE 0 END) AS machine_tap
    FROM t_machine_record t
    WHERE
      CONVERT(VARCHAR(10),t.ope_d_t,120) >= :beginDate
      AND
      CONVERT(VARCHAR(10),t.ope_d_t,120) <= :endDate
    GROUP BY
      CONVERT(VARCHAR(7),t.ope_d_t, 120)
),

k AS (
   SELECT
     CONVERT(VARCHAR(7),k.ope_d_t, 120) AS machine_date,
     SUM(CASE WHEN rework_code IN ('44A') AND k_s2 IN (44,46) THEN 1 ELSE 0 END) AS cast_hub,
     SUM(CASE WHEN rework_code IN ('H6') AND k_s2 IN (44,46)  THEN 1 ELSE 0 END) AS machine_hub
   FROM k_machine_record k
   WHERE
     CONVERT(VARCHAR(10),k.ope_d_t,120) >= :beginDate
     AND
     CONVERT(VARCHAR(10),k.ope_d_t,120) <= :endDate
   GROUP BY
     CONVERT(VARCHAR(7),k.ope_d_t, 120)
),

j AS (
   SELECT
     CONVERT(VARCHAR(7),j.ope_d_t, 120) AS machine_date,
     SUM(CASE WHEN rework_code IN ('88','67F','2A') AND j_s2 IN (45) THEN 1 ELSE 0 END) AS cast_datum,
     SUM(CASE WHEN rework_code IN ('H4','H5','65') AND j_s2 IN (45)  THEN 1 ELSE 0 END) AS machine_datum
   FROM j_machine_record j
   WHERE
     CONVERT(VARCHAR(10),j.ope_d_t,120) >= :beginDate
     AND
     CONVERT(VARCHAR(10),j.ope_d_t,120) <= :endDate
    GROUP BY
      CONVERT(VARCHAR(7),j.ope_d_t, 120)
)

SELECT
  t.machine_date,
  t.cast_flat,
  t.cast_tap,
  j.cast_datum,
  k.cast_hub,
  t.machine_flat,
  t.machine_tap,
  j.machine_datum,
  k.machine_hub,
  (t.cast_flat+t.cast_tap+j.cast_datum+k.cast_hub) AS month_cast,
  (t.machine_flat+t.machine_tap+j.machine_datum+k.machine_hub) AS month_machine,
  (t.cast_flat+t.cast_tap+j.cast_datum+k.cast_hub+t.machine_flat+t.machine_tap+j.machine_datum+k.machine_hub) AS month_total
FROM
  t
  INNER JOIN j ON t.machine_date = j.machine_date
  INNER JOIN k ON t.machine_date = k.machine_date
ORDER BY
  t.machine_date
