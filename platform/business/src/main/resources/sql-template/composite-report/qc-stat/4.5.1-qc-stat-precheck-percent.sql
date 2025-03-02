WITH a AS (
    SELECT CONVERT(VARCHAR (10), ope_d_t, 120) AS                                       ope_d_t,
           cope_inspector_id,
           COUNT(wheel_serial) AS                                                       amount,
           SUM(CASE WHEN rework_code = '' THEN 1 ELSE 0 END) AS                         no_rework_amount,
           SUM(
                   CASE WHEN rework_code = '' AND scrap_code = '' THEN 1 ELSE 0 END) AS no_rework_scrap_amount,
           SUM(CASE
                   WHEN rework_code IN ('3E', '3R', '4E', '4R', '5E', '5R', '6E', '6R') THEN 1
                   ELSE 0 END) AS                                                       er3456,
           SUM(CASE WHEN rework_code IN ('3E', '3R') THEN 1 ELSE 0 END) AS              er3,
           SUM(CASE WHEN rework_code IN ('4E', '4R') THEN 1 ELSE 0 END) AS              er4,
           SUM(CASE WHEN rework_code IN ('5E', '5R') THEN 1 ELSE 0 END) AS              er5,
           SUM(CASE WHEN rework_code IN ('6E', '6R') THEN 1 ELSE 0 END) AS              er6
    FROM pre_check_record
    WHERE ts = 1
      AND ope_d_t >= :beginDate
      AND ope_d_t < :endDate
    GROUP BY CONVERT(VARCHAR (10), ope_d_t, 120),
             cope_inspector_id
)

SELECT a.*,dbo.percentage(no_rework_amount,amount) AS no_rework_percent,
       dbo.percentage(no_rework_scrap_amount,amount) AS no_rework_scrap_percent,
       dbo.percentage(er3456,amount) AS er3456_percent,dbo.percentage(er3,amount) AS er3_percent,
       dbo.percentage(er4,amount) AS er4_percent,dbo.percentage(er5,amount) AS er5_percent,
       dbo.percentage(er6,amount) AS er6_percent,amount - no_rework_amount - er3456 AS other_amount,
       dbo.percentage((amount - no_rework_amount - er3456),amount) AS other_percent
FROM a
ORDER BY
    ope_d_t ASC
