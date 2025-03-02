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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.KMachineData;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.TMachineTopInfo;

@Service
@Slf4j
public class KMachineExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "kmachine";
  }

  @Override
  public String fileName() {
    return "MS-04-F-01-02.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT TOP 1 k_machine_record.operator,k_machine_record.machine_no,k_machine_record.ope_d_t,"
        + "k_machine_record.inspector_id,k_machine_record.is_measure_check "
        + "FROM k_machine_record "
        + "WHERE "
        + "CONVERT(VARCHAR(10),k_machine_record.ope_d_t,120) = :date "
        + "AND "
        + "k_machine_record.operator = :opeId "
        + "AND "
        + "k_machine_record.machine_no = :machineNo "
        + "ORDER BY k_machine_record.ope_d_t ASC";

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
      int currentRowNum = 3;
      Row row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(tMachineTopInfo.getOperator());
      row.getCell(3).setCellValue(getStringValue(tMachineTopInfo.getMachineNo()));
      row.getCell(5).setCellValue(getStringValueOfDate(tMachineTopInfo.getOpeDT(), "yyyy/M/d"));
      currentRowNum = currentRowNum + 2;
      row = sheet.getRow(currentRowNum);
      row.getCell(1).setCellValue(tMachineTopInfo.getInspectorId());
      row.getCell(3).setCellValue(getMark(tMachineTopInfo.getIsMeasureCheck()));

      sql = "SELECT k_machine_record.location,k_machine_record.Wheel_Serial,k_machine_record.concentricity,"
          + "k_machine_record.bore_dia,k_machine_record.memo "
          + "FROM k_machine_record "
          + "WHERE "
          + "CONVERT(VARCHAR(10),k_machine_record.ope_d_t,120) = :date "
          + "AND "
          + "k_machine_record.operator = :opeId "
          + "AND "
          + "k_machine_record.machine_no = :machineNo "
          + "ORDER BY k_machine_record.ope_d_t ASC";
      List<KMachineData> kMachineDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(KMachineData.class));
      currentRowNum = currentRowNum + 5;

      insertKMachineData(kMachineDataList, sheet, currentRowNum);
    }
  }

  private void insertKMachineData(List<KMachineData> kMachineDataList, Sheet sheet, int currentRowNum) {
    addRow(kMachineDataList, sheet, currentRowNum, 8);
    for (int i = 0; i < kMachineDataList.size(); i++) {
      KMachineData kMachineData = kMachineDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(i + 1);
      String location = kMachineData.getLocation();
      if ("0".equals(location)) {
        location = "";
      } else if ("1".equals(location)) {
        location = "左";
      } else if ("2".equals(location)) {
        location = "右";
      }
      row.getCell(1).setCellValue(location);
      row.getCell(2).setCellValue(kMachineData.getWheelSerial());
      row.getCell(3).setCellValue(getStringValueWithScale(kMachineData.getConcentricity(), 2));
      row.getCell(4).setCellValue(getStringValueWithScale(kMachineData.getBoreDia(), 1));
      row.getCell(5).setCellValue(kMachineData.getMemo());
    }
  }
}

