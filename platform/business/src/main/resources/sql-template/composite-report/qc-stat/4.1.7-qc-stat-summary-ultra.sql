WITH t1 AS (
    SELECT
        xh,
		wheel_serial,
        rework_code,
        scrap_code,
        hold_code,
        CASE xray_req WHEN 1 THEN '√' ELSE '' END AS xray_req
    FROM
        ultra_record
    WHERE
        ope_d_t >= :beginDate
        AND
        ope_d_t < :endDate
    <#if shift??>
        AND ${shift}
    </#if>
    <#if staffId??>
      AND ultra_record.inspector_id = :staffId
    </#if>
)

SELECT
  xh,
  rework_code,
	scrap_code,
  hold_code,
	xray_req,
	COUNT(wheel_serial) AS amount,
  SUM (CASE WHEN xh = '01' THEN 1 ELSE 0 END) AS total1,
  SUM (CASE WHEN xh = '02' THEN 1 ELSE 0 END) AS total2
FROM t1
GROUP BY
    xh,
	rework_code,
	scrap_code,
	hold_code,
	xray_req
ORDER BY
  CASE
	WHEN (rework_code = '' AND scrap_code = '' AND hold_code = '' AND xray_req = '') THEN 0
	WHEN (rework_code != '') THEN 1
	WHEN (scrap_code != '') THEN 2
	WHEN (hold_code != '') THEN 3
	WHEN (xray_req != '') THEN 4
	ELSE 5
  END, amount DESC