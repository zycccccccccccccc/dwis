WITH t2 AS (
SELECT
  CONVERT(varchar(100), stock_date, 111) AS stock_date,
  COUNT(1) AS stock
FROM wheel_record
WHERE stock_date >= :beginDate
  AND stock_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY stock_date
),

t3 AS (
SELECT
  CONVERT(varchar(100), scrap_date, 111) AS scrap_date,
  SUM(confirmed_scrap) AS scrap,
  SUM(CASE WHEN scrap_code = '67RS' THEN 1 ELSE 0 END) AS scrap_67rs,
  SUM(CASE WHEN scrap_code = '23S' THEN 1 ELSE 0 END) AS scrap_23s,
  SUM(CASE WHEN scrap_code = '41' THEN 1 ELSE 0 END) AS scrap_41,
  SUM(CASE WHEN scrap_code = '9B' THEN 1 ELSE 0 END) AS scrap_9b,
  SUM(CASE WHEN scrap_code = '44AS' THEN 1 ELSE 0 END) AS scrap_44as,
  SUM(CASE WHEN SUBSTRING(scrap_code, 1, 2) = '56' THEN 1 ELSE 0 END) AS scrap_56,
  SUM(CASE WHEN scrap_code = '58S' THEN 1 ELSE 0 END) AS scrap_58s,
  SUM(CASE WHEN scrap_code = '88S' THEN 1 ELSE 0 END) AS scrap_88s,
  SUM(CASE WHEN scrap_code = '9AS' THEN 1 ELSE 0 END) AS scrap_9as,
  SUM(CASE WHEN scrap_code = '9CS' THEN 1 ELSE 0 END) AS scrap_9cs,
  SUM(CASE WHEN scrap_code = '67FS' THEN 1 ELSE 0 END) AS scrap_67fs,
  SUM(CASE WHEN scrap_code = '69' THEN 1 ELSE 0 END) AS scrap_69,
  SUM(CASE WHEN scrap_code = '77' THEN 1 ELSE 0 END) AS scrap_77,
  SUM(CASE WHEN scrap_code = '66' THEN 1 ELSE 0 END) AS scrap_66,
  SUM(CASE WHEN SUBSTRING(scrap_code, 1, 2) = '12' THEN 1 ELSE 0 END) AS scrap_12,
  SUM(CASE WHEN scrap_code = '2AS' THEN 1 ELSE 0 END) AS scrap_2as,
  SUM(CASE WHEN scrap_code = '8CS' THEN 1 ELSE 0 END) AS scrap_8cs,
  SUM(CASE WHEN scrap_code = '42' THEN 1 ELSE 0 END) AS scrap_42,
  SUM(CASE WHEN scrap_code = '7' THEN 1 ELSE 0 END) AS scrap_7,
  SUM(CASE WHEN scrap_code = '30' THEN 1 ELSE 0 END) AS scrap_30,
  SUM(CASE WHEN scrap_code = '65B' THEN 1 ELSE 0 END) AS scrap_65b
FROM wheel_record
WHERE scrap_date >= :beginDate
  AND scrap_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
GROUP BY scrap_date
),

dt AS (
SELECT
  CONVERT(VARCHAR(10), dateadd(d, number, :beginDate), 111) AS scrap_date
FROM
  master..spt_values
WHERE type = 'p'
  AND number <= DATEDIFF(d, :beginDate, :endDate)
)

SELECT * FROM (
SELECT
  dt.scrap_date,
  ISNULL(stock, 0) AS stock,
  ISNULL(stock, 0) + ISNULL(scrap, 0) AS ss,
  ISNULL(scrap, 0) AS scrap,
  ISNULL(scrap_67rs, 0) AS scrap_67rs,
  ISNULL(scrap_23s, 0) AS scrap_23s,
  ISNULL(scrap_41, 0) AS scrap_41,
  ISNULL(scrap_9b, 0) AS scrap_9b,
  ISNULL(scrap_44as, 0) AS scrap_44as,
  ISNULL(scrap_56, 0) AS scrap_56,
  ISNULL(scrap_58s, 0) AS scrap_58s,
  ISNULL(scrap_88s, 0) AS scrap_88s,
  ISNULL(scrap_9as, 0) AS scrap_9as,
  ISNULL(scrap_9cs, 0) AS scrap_9cs,
  ISNULL(scrap_67fs, 0) AS scrap_67fs,
  ISNULL(scrap_69, 0) AS scrap_69,
  ISNULL(scrap_77, 0) AS scrap_77,
  ISNULL(scrap_66, 0) AS scrap_66,
  ISNULL(scrap_12, 0) AS scrap_12,
  ISNULL(scrap_2as, 0) AS scrap_2as,
  ISNULL(scrap_8cs, 0) AS scrap_8cs,
  ISNULL(scrap_42, 0) AS scrap_42,
  ISNULL(scrap_7, 0) AS scrap_7,
  ISNULL(scrap_30, 0) AS scrap_30,
  ISNULL(scrap_65b, 0) AS scrap_65b
FROM dt
LEFT JOIN t2 ON dt.scrap_date = t2.stock_date
LEFT JOIN t3 ON dt.scrap_date = t3.scrap_date
UNION ALL
SELECT
  'total' AS scrap_date,
  SUM(ISNULL(stock, 0)) AS stock,
  SUM(ISNULL(stock, 0) + ISNULL(scrap, 0)) AS ss,
  SUM(scrap) AS scrap,
  SUM(scrap_67rs) AS scrap_67rs,
  SUM(scrap_23s) AS scrap_23s,
  SUM(scrap_41) AS scrap_41,
  SUM(scrap_9b) AS scrap_9b,
  SUM(scrap_44as) AS scrap_44as,
  SUM(scrap_56) AS scrap_56,
  SUM(scrap_58s) AS scrap_58s,
  SUM(scrap_88s) AS scrap_88s,
  SUM(scrap_9as) AS scrap_9as,
  SUM(scrap_9cs) AS scrap_9cs,
  SUM(scrap_67fs) AS scrap_67fs,
  SUM(scrap_69) AS scrap_69,
  SUM(scrap_77) AS scrap_77,
  SUM(scrap_66) AS scrap_66,
  SUM(scrap_12) AS scrap_12,
  SUM(scrap_2as) AS scrap_2as,
  SUM(scrap_8cs) AS scrap_8cs,
  SUM(scrap_42) AS scrap_42,
  SUM(scrap_7) AS scrap_7,
  SUM(scrap_30) AS scrap_30,
  SUM(scrap_65b) AS scrap_65b
FROM dt
LEFT JOIN t2 ON dt.scrap_date = t2.stock_date
LEFT JOIN t3 ON dt.scrap_date = t3.scrap_date
) t
ORDER BY scrap_date
