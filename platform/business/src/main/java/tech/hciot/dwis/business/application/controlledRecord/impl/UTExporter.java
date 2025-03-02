package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;

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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.UTData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.UTTopInfo;

@Service
@Slf4j
public class UTExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "ut";
  }

  @Override
  public String fileName() {
    return "Insp-05-F-01-08.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 ut_test_record.eq_model,ut_test_record.eq_no,ut_test_record.ope_d_t "
        + "FROM ut_test_record "
        + "WHERE "
        + "CONVERT(VARCHAR(10),ut_test_record.ope_d_t,120) = :date ";

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
      row.getCell(13).setCellValue(utTopInfo.getEqModel());
      row.getCell(18).setCellValue(utTopInfo.getEqNo());
      row.getCell(22).setCellValue(getStringValueOfDate(utTopInfo.getOpeDT(), "yyyy/M/d"));

      sql = "SELECT ut_test_record.ope_d_t,ut_test_record.shift_no,ut_test_record.wheel_serial,ut_test_record.design,"
          + "ut_test_record.t_db,ut_test_record.j1,ut_test_record.j2,ut_test_record.j3,ut_test_record.j4,ut_test_record.j5,"
          + "ut_test_record.j6,ut_test_record.j7,ut_test_record.b_db,ut_test_record.z1,ut_test_record.z2,ut_test_record.z3,"
          + "ut_test_record.z4,ut_test_record.z5,ut_test_record.wheel_check,ut_test_record.probe_check,ut_test_record.room_temp,"
          + "a1.nickname AS operator,a2.nickname AS inspector_id "
          + "FROM ut_test_record "
          + "INNER JOIN account a1 ON a1.username = ut_test_record.operator "
          + "INNER JOIN account a2 ON a2.username = ut_test_record.inspector_id "
          + "WHERE "
          + "CONVERT(VARCHAR(10),ut_test_record.ope_d_t,120) = :date "
          + "ORDER BY ut_test_record.ope_d_t ASC";
      List<UTData> utDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(UTData.class));
      currentRowNum = 7;

      insertUtData(utDataList, sheet, currentRowNum);
    }
  }

  private void insertUtData(List<UTData> utDataList, Sheet sheet, int currentRowNum) {
    addRow(utDataList, sheet, currentRowNum, 9);
    for (int i = 0; i < utDataList.size(); i++) {
      UTData utData = utDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValueOfDate(utData.getOpeDT(), "yyyy/M/d HH:mm:ss"));
      row.getCell(1).setCellValue(utData.getShiftNo());
      row.getCell(2).setCellValue(utData.getWheelSerial());
      row.getCell(3).setCellValue(utData.getDesign());
      row.getCell(4).setCellValue(getStringValue(utData.getTDb()));
      row.getCell(5).setCellValue(getStringValue(utData.getJ1()));
      row.getCell(6).setCellValue(getStringValue(utData.getJ2()));
      row.getCell(7).setCellValue(getStringValue(utData.getJ3()));
      row.getCell(8).setCellValue(getStringValue(utData.getJ4()));
      row.getCell(9).setCellValue(getStringValue(utData.getJ5()));
      row.getCell(10).setCellValue(getStringValue(utData.getJ6()));
      row.getCell(11).setCellValue(getStringValue(utData.getJ7()));
      row.getCell(12).setCellValue(getStringValue(utData.getBDb()));
      row.getCell(13).setCellValue(getStringValue(utData.getZ1()));
      row.getCell(14).setCellValue(getStringValue(utData.getZ2()));
      row.getCell(15).setCellValue(getStringValue(utData.getZ3()));
      row.getCell(16).setCellValue(getStringValue(utData.getZ4()));
      row.getCell(17).setCellValue(getStringValue(utData.getZ5()));
      row.getCell(18).setCellValue(utData.getWheelCheck() == 0 ? "否" : "是");
      row.getCell(19).setCellValue(utData.getProbeCheck() == 0 ? "否" : "是");
      row.getCell(20).setCellValue(utData.getRoomTemp());
      row.getCell(21).setCellValue(utData.getOperator());
      row.getCell(22).setCellValue(utData.getInspectorId());
    }
  }
}

