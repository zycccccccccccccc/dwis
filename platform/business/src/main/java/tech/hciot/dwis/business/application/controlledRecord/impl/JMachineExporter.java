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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.JMachineData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.JMachineTopInfo;

@Service
@Slf4j
public class JMachineExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "jmachine";
  }

  @Override
  public String fileName() {
    return "MS-02-F-01-02.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 j_machine_record.operator,j_machine_record.machine_no,j_machine_record.ope_d_t,"
        + "j_machine_record.inspector_id,calibra_wheel.is_check,calibra_wheel.claw1,calibra_wheel.claw2,"
        + "calibra_wheel.claw3,calibra_wheel.rest1,calibra_wheel.rest2,calibra_wheel.rest3 "
        + "FROM j_machine_record "
        + "LEFT JOIN calibra_wheel ON j_machine_record.machine_no = calibra_wheel.machine_no "
        + "AND j_machine_record.operator = calibra_wheel.operator "
        + "AND CONVERT(VARCHAR(10),j_machine_record.ope_d_t,120) = CONVERT(VARCHAR(10),calibra_wheel.ope_d_t,120) "
        + "WHERE "
        + "CONVERT(VARCHAR(10),j_machine_record.ope_d_t,120) = :date "
        + "AND "
        + "j_machine_record.operator = :opeId "
        + "AND "
        + "j_machine_record.machine_no = :machineNo AND j_machine_record.inspector_id !='' "
        + "ORDER BY calibra_wheel.ope_d_t ASC";

    params.put("date", date.substring(0, 10));
    params.put("opeId", opeId);
    params.put("machineNo", machineNo);
    JMachineTopInfo jMachineTopInfo = null;
    try {
      jMachineTopInfo = template
          .queryForObject(sql, params, BeanPropertyRowMapper.newInstance(JMachineTopInfo.class));
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
    if (jMachineTopInfo != null) {
      Sheet sheet = workbook.getSheetAt(0);
      int currentRowNum = 2;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(jMachineTopInfo.getOperator());
      row.getCell(3).setCellValue(getStringValue(jMachineTopInfo.getMachineNo()));
      row.getCell(5).setCellValue(getStringValueOfDate(jMachineTopInfo.getOpeDT(), "yyyy/M/d"));
      row.getCell(7).setCellValue(jMachineTopInfo.getInspectorId());
      row.getCell(9).setCellValue(getMark(jMachineTopInfo.getIsCheck()));

      currentRowNum = currentRowNum + 5;
      row = sheet.getRow(currentRowNum);
      row.getCell(4).setCellValue(getStringValueWithScale(jMachineTopInfo.getClaw1(), 2));
      row.getCell(5).setCellValue(getStringValueWithScale(jMachineTopInfo.getClaw2(), 2));
      row.getCell(6).setCellValue(getStringValueWithScale(jMachineTopInfo.getClaw3(), 2));
      row.getCell(7).setCellValue(getStringValueWithScale(jMachineTopInfo.getRest1(), 2));
      row.getCell(8).setCellValue(getStringValueWithScale(jMachineTopInfo.getRest2(), 2));
      row.getCell(9).setCellValue(getStringValueWithScale(jMachineTopInfo.getRest3(), 2));

      sql = "SELECT j_machine_record.wheel_serial,j_machine_record.f,j_machine_record.d2_dia,"
          + "j_machine_record.d2_cir,j_machine_record.memo "
          + "FROM j_machine_record "
          + "LEFT JOIN calibra_wheel ON j_machine_record.machine_no = calibra_wheel.machine_no "
          + "AND j_machine_record.operator = calibra_wheel.operator "
          + "AND CONVERT(VARCHAR(10),j_machine_record.ope_d_t,120) = CONVERT(VARCHAR(10),calibra_wheel.ope_d_t,120) "
          + "WHERE "
          + "CONVERT(VARCHAR(10),j_machine_record.ope_d_t,120) = :date "
          + "AND "
          + "j_machine_record.operator = :opeId "
          + "AND "
          + "j_machine_record.machine_no = :machineNo ";
      List<JMachineData> jMachineDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(JMachineData.class));
      currentRowNum = currentRowNum + 3;

      insertJMachineData(jMachineDataList, sheet, currentRowNum);
    }
  }

  private void insertJMachineData(List<JMachineData> jMachineDataList, Sheet sheet, int currentRowNum) {
    addRow(jMachineDataList, sheet, currentRowNum, 6);
    for (int i = 0; i < jMachineDataList.size(); i++) {
      JMachineData jMachineData = jMachineDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(i + 1);
      row.getCell(1).setCellValue(jMachineData.getWheelSerial());
      row.getCell(4).setCellValue(getStringValueWithScale(jMachineData.getF(), 2));
      row.getCell(5).setCellValue(getStringValueWithScale(jMachineData.getD2Dia(), 2));
      row.getCell(7).setCellValue(getStringValueWithScale(jMachineData.getD2Cir(), 2));
      row.getCell(9).setCellValue(jMachineData.getMemo());
    }
  }
}

