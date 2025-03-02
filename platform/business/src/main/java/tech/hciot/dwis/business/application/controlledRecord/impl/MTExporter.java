package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.MTData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.UTTopInfo;

@Service
@Slf4j
public class MTExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "mt";
  }

  @Override
  public String fileName() {
    return "Insp-06-F-01-09.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 mt_test_record.eq_model,mt_test_record.eq_no,mt_test_record.ope_d_t "
        + "FROM mt_test_record "
        + "WHERE "
        + "CONVERT(VARCHAR(10),mt_test_record.ope_d_t,120) = :date ";

    params.put("date", date.substring(0, 10));
    UTTopInfo utTopInfo = null;
    try {
      utTopInfo = template
          .queryForObject(sql, params, BeanPropertyRowMapper.newInstance(UTTopInfo.class));
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
    if (utTopInfo != null) {
      Sheet sheet = workbook.getSheetAt(0);
      int currentRowNum = 2;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(10).setCellValue(utTopInfo.getEqModel());
      row.getCell(19).setCellValue(utTopInfo.getEqNo());
      row.getCell(26).setCellValue(getStringValueOfDate(utTopInfo.getOpeDT(), "yyyy/M/d"));

      sql = "SELECT mt_test_record.ope_d_t,mt_test_record.shift_no,mt_test_record.solution_amount,mt_test_record.magn_amount,"
          + "mt_test_record.disp_amount,mt_test_record.defo_amount,mt_test_record.solution_density,mt_test_record.solution_pre,"
          + "mt_test_record.magn_current,mt_test_record.light_cope_left,mt_test_record.light_cope_right,mt_test_record.light_tread,"
          + "mt_test_record.light_drag_left,mt_test_record.light_drag_right,mt_test_record.white_light,mt_test_record.striprev_up,"
          + "mt_test_record.striprev_down,mt_test_record.remanence_intensity,'' AS manufacturer,'' AS granularity,"
          + "mt_test_record.batchno_mt,mt_test_record.room_temp,a1.nickname AS operator,a2.nickname AS inspector_id "
          + "FROM mt_test_record "
          + "INNER JOIN account a1 ON a1.username = mt_test_record.operator "
          + "INNER JOIN account a2 ON a2.username = mt_test_record.inspector_id "
          + "WHERE "
          + "CONVERT(VARCHAR(10),mt_test_record.ope_d_t,120) = :date "
          + "ORDER BY mt_test_record.ope_d_t ASC";
      List<MTData> mtDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(MTData.class));
      currentRowNum = 7;

      insertMtData(mtDataList, sheet, currentRowNum);
    }
  }

  private void insertMtData(List<MTData> mtDataList, Sheet sheet, int currentRowNum) {
    addRow(mtDataList, sheet, currentRowNum, 12);
    for (int i = 0; i < mtDataList.size(); i++) {
      MTData mtData = mtDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValueOfDate(mtData.getOpeDT(), "HH:mm"));
      row.getCell(1).setCellValue(mtData.getShiftNo());
      row.getCell(2).setCellValue(getStringValue(mtData.getSolutionAmount()));
      row.getCell(4).setCellValue(getStringValue(mtData.getMagnAmount()));
      row.getCell(6).setCellValue(getStringValue(mtData.getDispAmount()));
      row.getCell(8).setCellValue(getStringValue(mtData.getDefoAmount()));
      row.getCell(10).setCellValue(getStringValueWithScale(mtData.getSolutionDensity(), 2));
      row.getCell(11).setCellValue(mtData.getSolutionPre());
      row.getCell(12).setCellValue(getStringValueWithScale(mtData.getMagnCurrent(), 1));
      row.getCell(13).setCellValue(getStringValue(mtData.getLightCopeLeft()));
      row.getCell(14).setCellValue(getStringValue(mtData.getLightCopeRight()));
      row.getCell(15).setCellValue(getStringValue(mtData.getLightTread()));
      row.getCell(16).setCellValue(getStringValue(mtData.getLightDragLeft()));
      row.getCell(17).setCellValue(getStringValue(mtData.getLightDragRight()));
      row.getCell(18).setCellValue(getStringValueWithScale(mtData.getWhiteLight(), 1));
      row.getCell(19).setCellValue(mtData.getStriprevUp() == 0 ? "否" : "是");
      row.getCell(20).setCellValue(mtData.getStriprevDown() == 0 ? "否" : "是");
      row.getCell(21).setCellValue(getStringValue(mtData.getRemanenceIntensity()));
      row.getCell(22).setCellValue(mtData.getManufacturer());
      row.getCell(23).setCellValue(mtData.getGranularity());
      row.getCell(24).setCellValue(mtData.getBatchnoMT());
      row.getCell(25).setCellValue(mtData.getRoomTemp());
      row.getCell(26).setCellValue(mtData.getOperator());
      row.getCell(27).setCellValue(mtData.getInspectorId());
    }
  }
}

