WITH t1 AS (
SELECT
  cast_date,
  heat_record_key,
  furnace_no,
  SUBSTRING(bag_no, 1, CHARINDEX('-', bag_no) - 1) AS bag_no,
  SUBSTRING(ladle_no, 1, CHARINDEX('-', ladle_no) - 1) AS ladle_no
FROM
  heat_record
WHERE
  CONVERT(VARCHAR(10),cast_date,120) >= :beginDate
  AND CONVERT(VARCHAR(10),cast_date,120) <= :endDate
  <#if furnace_no??>
      AND furnace_no = :furnace_no
  </#if>
  <#if bag_no??>
      AND SUBSTRING(bag_no, 1, CHARINDEX('-', bag_no) - 1) = :bag_no
      AND CHARINDEX('-', bag_no) > 1
  </#if>
  <#if ladle_no??>
      AND SUBSTRING(ladle_no, 1, CHARINDEX('-', ladle_no) - 1) = :ladle_no
      AND CHARINDEX('-', ladle_no) > 1
  </#if>
)

SELECT
  COUNT(heat_record_key) AS quantity
FROM
  t1