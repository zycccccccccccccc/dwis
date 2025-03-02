WITH stat AS (
  SELECT
    scrap_code,
    COUNT(1) AS cnt,
    SUM (CASE WHEN pre <> 0 THEN 1 WHEN pre = 0 AND scrap_code <> '' THEN 1 ELSE 0 END) AS pre_insp,
    SUM (confirmed_scrap) AS sconf_sum
  FROM v_heat_ladle_wheel
  WHERE	cast_date >= :beginDate
  	AND cast_date <= :endDate
  <#if design??>
    AND design IN :design
  </#if>
    AND test_code IN :testCode
    AND scrap_code IS NOT NULL AND scrap_code <> ''
    GROUP BY
      scrap_code
),

total AS (
  SELECT
    SUM(pre_insp) AS pre_insp
  FROM stat
)

SELECT
  scrap_code,
	cnt,
	dbo.percentage(cnt, total.pre_insp) AS cnt_pre,
	sconf_sum
FROM stat JOIN total ON 1 = 1
UNION ALL
SELECT
  'total' AS scrap_code,
	SUM(cnt) AS cnt,
	dbo.percentage(SUM(cnt), MAX(total.pre_insp)) AS cnt_pre,
	SUM(sconf_sum) AS sconf_sum
FROM stat JOIN total ON 1 = 1
ORDER BY sconf_sum DESC
