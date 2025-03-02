WITH t2 AS (
SELECT
  'total' AS stock_date,
  COUNT(1) AS stock
FROM wheel_record
WHERE stock_date >= :beginDate
  AND stock_date <= :endDate
<#if design??>
  AND design IN :design
</#if>
),

t3 AS (
SELECT
  'total' AS scrap_date,
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
)

SELECT
  t3.scrap_date,
  ISNULL(t2.stock, 0) AS stock,
  (ISNULL(t2.stock, 0) + ISNULL(t3.scrap, 0)) AS ss,
  t3.scrap,
  t3.scrap_67rs,
  t3.scrap_23s,
  t3.scrap_41,
  t3.scrap_9b,
  t3.scrap_44as,
  t3.scrap_56,
  t3.scrap_58s,
  t3.scrap_88s,
  t3.scrap_9as,
  t3.scrap_9cs,
  t3.scrap_67fs,
  t3.scrap_69,
  t3.scrap_77,
  t3.scrap_66,
  t3.scrap_12,
  t3.scrap_2as,
  t3.scrap_8cs,
  t3.scrap_42,
  t3.scrap_7,
  t3.scrap_30,
  t3.scrap_65b
FROM t3
INNER JOIN t2 ON t3.scrap_date = t2.stock_date
