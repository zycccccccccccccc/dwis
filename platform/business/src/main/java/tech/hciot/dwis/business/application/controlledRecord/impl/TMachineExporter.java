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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.TMachineData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.TMachineTopInfo;

@Service
@Slf4j
public class TMachineExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "tmachine";
  }

  @Override
  public String fileName() {
    return "MS-03-F-01-02.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 t_machine_record.operator,t_machine_record.machine_no,t_machine_record.ope_d_t,"
        + "t_machine_record.inspector_id,t_machine_record.is_measure_check "
        + "FROM t_machine_record "
        + "WHERE "
        + "CONVERT(VARCHAR(10),t_machine_record.ope_d_t,120) = :date "
        + "AND "
        + "t_machine_record.operator = :opeId "
        + "AND "
        + "t_machine_record.machine_no = :machineNo "
        + "ORDER BY t_machine_record.ope_d_t ASC";

    params.put("date", date.substring(0, 10));
    params.put("opeId", opeId);
    params.put("machineNo", machineNo);
    TMachineTopInfo tMachineTopInfo = null;
    try {
      tMachineTopInfo = template
          .queryForObject(sql, params, BeanPropertyRowMapper.newInstance(TMachineTopInfo.class));
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
    if (tMachineTopInfo != null) {
      Sheet sheet = workbook.getSheetAt(0);
      int currentRowNum = 2;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(tMachineTopInfo.getOperator());
      row.getCell(3).setCellValue(getStringValue(tMachineTopInfo.getMachineNo()));
      row.getCell(5).setCellValue(getStringValueOfDate(tMachineTopInfo.getOpeDT(), "yyyy/M/d"));
      row.getCell(7).setCellValue(tMachineTopInfo.getInspectorId());
      row.getCell(9)
          .setCellValue(getMark(tMachineTopInfo.getIsMeasureCheck()));

      sql = "SELECT t_machine_record.wheel_serial,t_machine_record.rim_width,t_machine_record.hub_length,"
          + "t_machine_record.flange_tread_profile,t_machine_record.rolling_circle_dia,"
          + "t_machine_record.rimdev1,t_machine_record.rimdev2,t_machine_record.rimdev3,machine_record.design "
          + "FROM t_machine_record "
          + "LEFT JOIN "
          + "machine_record ON t_machine_record.wheel_serial = machine_record.wheel_serial "
          + "WHERE "
          + "CONVERT(VARCHAR(10),t_machine_record.ope_d_t,120) = :date "
          + "AND "
          + "t_machine_record.operator = :opeId "
          + "AND "
          + "t_machine_record.machine_no = :machineNo "
          + "ORDER BY t_machine_record.ope_d_t ASC";
      List<TMachineData> tMachineDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(TMachineData.class));
      currentRowNum = currentRowNum + 6;

      insertTMachineData(tMachineDataList, sheet, currentRowNum);
    }
  }

  private void insertTMachineData(List<TMachineData> tMachineDataList, Sheet sheet, int currentRowNum) {
    addRow(tMachineDataList, sheet, currentRowNum, 15);
    for (int i = 0; i < tMachineDataList.size(); i++) {
      TMachineData tMachineData = tMachineDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(i + 1);
      row.getCell(1).setCellValue(tMachineData.getWheelSerial());
      row.getCell(3).setCellValue(getStringValueWithScale(tMachineData.getRimWidth(), 1));
      row.getCell(4).setCellValue(getStringValueWithScale(tMachineData.getHubLength(), 1));
      row.getCell(5).setCellValue(getMark(tMachineData.getFlangeTreadProfile()));
      row.getCell(6).setCellValue(getStringValueWithScale(tMachineData.getRollingCircleDia(), 1));
      row.getCell(7).setCellValue(getStringValueWithScale(tMachineData.getRimdev1(), 2));
      row.getCell(8).setCellValue(getStringValueWithScale(tMachineData.getRimdev2(), 2));
      row.getCell(9).setCellValue(getStringValueWithScale(tMachineData.getRimdev3(), 2));
      row.getCell(10).setCellValue(tMachineData.getDesign());
    }
  }
}

