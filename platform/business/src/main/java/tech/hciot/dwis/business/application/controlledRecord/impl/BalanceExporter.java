package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExporter;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.BalanceData;

@Service
@Slf4j
public class BalanceExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "balance";
  }

  @Override
  public String fileName() {
    return "Insp-08-F-01-01.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT "
        + "balance_test_record.ope_d_t,balance_test_record.shift_no,balance_test_record.wheel_serial,balance_test_record.design,"
        + "balance_test_record.ts,balance_test_record.balance_v180,balance_test_record.balance_a180,"
        + "balance_test_record.balance_v270,balance_test_record.balance_a270,a1.nickname AS operator,"
        + "a2.nickname AS inspector_id,balance_test_record.is_inspec_check "
        + "FROM balance_test_record "
        + "INNER JOIN account a1 ON a1.username = balance_test_record.operator "
        + "INNER JOIN account a2 ON a2.username = balance_test_record.inspector_id "
        + "WHERE "
        + "CONVERT(VARCHAR(10),balance_test_record.ope_d_t,120) = :date "
        + "ORDER BY balance_test_record.ope_d_t ASC";

    params.put("date", date.substring(0, 10));

    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 2;
    Row row = sheet.getRow(currentRowNum);
    row.getCell(11, MissingCellPolicy.CREATE_NULL_AS_BLANK)
        .setCellValue(getStringValueOfDate(parseDate(date.substring(0, 10)), "yyyy/M/d"));
    List<BalanceData> balanceDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(BalanceData.class));
    currentRowNum = 5;
    insertBalanceData(balanceDataList, sheet, currentRowNum);
  }

  private void insertBalanceData(List<BalanceData> balanceDataList, Sheet sheet, int currentRowNum) {
    addRow(balanceDataList, sheet, currentRowNum, 13);
    for (int i = 0; i < balanceDataList.size(); i++) {
      BalanceData balanceData = balanceDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValueOfDate(balanceData.getOpeDT(), "HH:mm"));
      row.getCell(1).setCellValue(balanceData.getShiftNo());
      row.getCell(2).setCellValue(balanceData.getWheelSerial());
      row.getCell(3).setCellValue(balanceData.getDesign());
      row.getCell(4).setCellValue(getStringValue(balanceData.getTs()));
      row.getCell(5).setCellValue(getStringValueWithScale(balanceData.getBalanceV180(), 1));
      row.getCell(6).setCellValue(getStringValue(balanceData.getBalanceA180()));
      row.getCell(7).setCellValue(getStringValueWithScale(balanceData.getBalanceV270(), 1));
      row.getCell(8).setCellValue(getStringValue(balanceData.getBalanceA270()));
      row.getCell(9).setCellValue(balanceData.getOperator());
      row.getCell(10).setCellValue(balanceData.getInspectorId());
      row.getCell(11).setCellValue(balanceData.getIsInspecCheck() == 0 ? "否" : "是");
    }
  }
}

