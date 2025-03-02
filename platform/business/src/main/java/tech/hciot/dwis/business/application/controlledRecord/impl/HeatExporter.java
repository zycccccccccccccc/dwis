package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;
import static tech.hciot.dwis.base.util.StandardTimeUtil.timeStr;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExporter;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.HeatHeader;
import tech.hciot.dwis.business.domain.model.Heat;

@Service
@Slf4j
public class HeatExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public HeatHeader getHeatHeader(String date, Integer heatLine, Integer shift) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 heat.hi_heat_in_date,heat.heat_line,heat.cut_id,heat.hi_heat_in_shift,heat.hi_heat_in_id,"
        + "heat.hi_heat_in_operator,heat.hi_heat_out_shift,heat.hi_heat_out_id,heat.hi_heat_out_operator,heat.low_heat_in_shift,"
        + "heat.low_heat_in_id,heat.low_heat_in_operator,heat.low_heat_out_shift,heat.low_heat_out_id,heat.low_heat_out_operator,"
        + "hi_heat_prework_record.target_temp,hi_heat_prework_record.actual_cycle,hi_heat_prework_record.time_checked,"
        + "hi_heat_prework_record.tread_quench_delay,hi_heat_prework_record.tread_time_checked,hi_heat_prework_record.spray_time,"
        + "hi_heat_prework_record.spray_time_checked,hi_heat_prework_record.water_pressure,"
        + "hi_heat_prework_record.water_pressure_time_checked,hi_heat_prework_record.water_temp,"
        + "hi_heat_prework_record.water_temp_time_checked,low_heat_prework_record.target_temp AS target_temp_low,"
        + "low_heat_prework_record.actual_cycle AS actual_cycle_low,low_heat_prework_record.time_checked AS time_checked_low "
        + "FROM heat LEFT JOIN hi_heat_prework_record ON heat.h_id = hi_heat_prework_record.id "
        + "LEFT JOIN low_heat_prework_record ON heat.l_id = low_heat_prework_record.id "
        + "WHERE "
        + "heat.hi_heat_in_date = :date AND heat.heat_line = :heatLine AND hi_heat_in_shift = :shift";

    params.put("heatLine", heatLine);
    params.put("date", date.substring(0, 10));
    params.put("shift", shift);
    HeatHeader heatHeader = null;
    try {
      heatHeader = template
          .queryForObject(sql, params, BeanPropertyRowMapper.newInstance(HeatHeader.class));
    } catch (EmptyResultDataAccessException e) {
      log.info("shift {} is no data", shift);
    }
    return heatHeader;
  }

  @Override
  public String type() {
    return "heat";
  }

  @Override
  public String fileName() {
    return "HT-04-F-02-02.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    for (int i = 1; i < 4; i++) {
      HeatHeader heatHeader = getHeatHeader(date, heatLine, i);
      if (heatHeader == null) {
        continue;
      }
      Sheet sheet = workbook.getSheetAt(i - 1);
      int currentRowNum = 2;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValueOfDate(heatHeader.getHiHeatInDate(), "yyyy/M/d"));
      row.getCell(3).setCellValue(getStringValue(heatHeader.getHeatLine()));
      row.getCell(5).setCellValue(heatHeader.getCutId());

      currentRowNum = currentRowNum + 2;
      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValue(heatHeader.getHiHeatInShift()));
      row.getCell(3).setCellValue(heatHeader.getHiHeatInId());
      row.getCell(5).setCellValue(heatHeader.getHiHeatInOperator());
      row.getCell(7).setCellValue(getStringValue(heatHeader.getHiHeatOutShift()));
      row.getCell(9).setCellValue(heatHeader.getHiHeatOutId());
      row.getCell(11).setCellValue(heatHeader.getHiHeatOutOperator());

      currentRowNum = currentRowNum + 2;
      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValue(heatHeader.getLowHeatInShift()));
      row.getCell(3).setCellValue(heatHeader.getLowHeatInId());
      row.getCell(5).setCellValue(heatHeader.getLowHeatInOperator());
      row.getCell(7).setCellValue(getStringValue(heatHeader.getLowHeatOutShift()));
      row.getCell(9).setCellValue(heatHeader.getLowHeatOutId());
      row.getCell(11).setCellValue(heatHeader.getLowHeatOutOperator());

      currentRowNum = currentRowNum + 3;
      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(heatHeader.getTargetTemp());
      row.getCell(3).setCellValue(getStringValueOfDate(heatHeader.getActualCycle(), "mm:ss"));
      row.getCell(5).setCellValue(getStringValueOfDate(heatHeader.getTimeChecked(), "HH:mm"));
      row.getCell(7).setCellValue(getStringValueOfDate(heatHeader.getTreadQuenchDelay(), "m''ss'\"'"));
      row.getCell(9).setCellValue(getStringValueOfDate(heatHeader.getTreadTimeChecked(), "HH:mm"));
      row.getCell(11).setCellValue(getStringValueOfDate(heatHeader.getSprayTime(), "mm:ss"));

      currentRowNum = currentRowNum + 2;
      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(getStringValueOfDate(heatHeader.getSprayTimeChecked(), "HH:mm"));
      row.getCell(3).setCellValue(getStringValueWithScale(heatHeader.getWaterPressure(), 3));
      row.getCell(5).setCellValue(getStringValueOfDate(heatHeader.getWaterPressureTimeChecked(), "HH:mm"));
      row.getCell(7).setCellValue(getStringValue(heatHeader.getWaterTemp()));
      row.getCell(9).setCellValue(getStringValueOfDate(heatHeader.getWaterTempTimeChecked(), "HH:mm"));

      currentRowNum = currentRowNum + 3;
      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(heatHeader.getTargetTempLow());
      row.getCell(3).setCellValue(getStringValueOfDate(heatHeader.getActualCycleLow(), "mm:ss"));
      row.getCell(5).setCellValue(getStringValueOfDate(heatHeader.getTimeCheckedLow(), "HH:mm"));

      currentRowNum = currentRowNum + 5;
      insertData(date, heatLine, i, sheet, currentRowNum);
    }
  }

  private void insertData(String date, Integer heatLine, Integer shift, Sheet sheet, int currentRowNum) {
    String sql = "";
    if (shift == 1 || shift == 2) {
       sql = "SELECT wheel_serial_1 AS wheel_serial1,wheel_serial_2 AS wheel_serial2,design_1 AS design1,design_2 AS design2, "
              + "test_code_1 AS test_code1,test_code_2 AS test_code2,heat_code_1 AS heat_code1,heat_code_2 AS heat_code2,"
              + "hi_heat_in_time,hi_heat_out_time,low_heat_in_time,low_heat_out_time "
              + "FROM heat "
              + "WHERE heat.hi_heat_in_date = :date AND heat.heat_line = :heatLine AND hi_heat_in_shift = :shift "
              + "ORDER BY hi_heat_in_time ASC";
    } else {
       sql = " DECLARE @StartDate DATETIME "
              + "DECLARE @EndDate DATETIME "
              + "SET @StartDate =  CONVERT (DATETIME, CONVERT(CHAR(32), CONVERT(DATETIME, :date), 112) + ' ' + '23:00:00') "
              + "SET @EndDate = CONVERT (DATETIME, CONVERT(CHAR(32), DATEADD(dd, +1, :date), 112) + ' ' + '08:00:00') "
              + "BEGIN "
              + "SELECT wheel_serial_1 AS wheel_serial1,wheel_serial_2 AS wheel_serial2,design_1 AS design1,design_2 AS design2, "
              + "test_code_1 AS test_code1,test_code_2 AS test_code2,heat_code_1 AS heat_code1,heat_code_2 AS heat_code2,"
              + "hi_heat_in_time,hi_heat_out_time,low_heat_in_time,low_heat_out_time "
              + "FROM heat "
              + "WHERE heat.heat_line = :heatLine AND hi_heat_in_shift = :shift "
              + "AND CONVERT(DATETIME, CONVERT (CHAR(8), hi_heat_in_date, 112 ) + ' ' + CONVERT(CHAR(8), hi_heat_in_time, 108)) BETWEEN  @StartDate AND  @EndDate "
              + "ORDER BY CONVERT(DATETIME, CONVERT (CHAR(8), hi_heat_in_date, 112 ) + ' ' + CONVERT(CHAR(8), hi_heat_in_time, 108)) ASC "
              + "END";
    }
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    params.put("heatLine", heatLine);
    params.put("date", date.substring(0, 10));
    params.put("shift", shift);
    List<Heat> dataList = template
        .query(sql, params, BeanPropertyRowMapper.newInstance(Heat.class));
    addRow(dataList, sheet, currentRowNum, 5);
    for (int i = 0; i < dataList.size(); i++) {
      Heat heat = dataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(heat.getWheelSerial1());
      row.getCell(1).setCellValue(heat.getWheelSerial2());
      row.getCell(2).setCellValue(heat.getDesign1());
      row.getCell(3).setCellValue(heat.getDesign2());
      row.getCell(4).setCellValue(heat.getTestCode1());
      row.getCell(5).setCellValue(heat.getTestCode2());
      row.getCell(6).setCellValue(heat.getHeatCode1());
      row.getCell(7).setCellValue(heat.getHeatCode2());
      row.getCell(8).setCellValue(timeStr(heat.getHiHeatInTime()));
      row.getCell(9).setCellValue(timeStr(heat.getHiHeatOutTime()));
      row.getCell(10).setCellValue(timeStr(heat.getLowHeatInTime()));
      row.getCell(11).setCellValue(timeStr(heat.getLowHeatOutTime()));
    }
  }
}

