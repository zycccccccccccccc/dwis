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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExporter;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.HbTestData;

@Service
@Slf4j
public class HbTestExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "hbtest";
  }

  @Override
  public String fileName() {
    return "Insp-03-F-01-02.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT hbtest_record.test_date,hbtest_record.shift_no,hbtest_record.test_block_no,hbtest_record.stand_value,"
            + "hbtest_record.test_result,hbtest_record.indenta_dia,hbtest_record.m_indenta_dia,hbtest_record.dev_indenta_dia,"
            + "'OK' AS result,hbtest_record.operator "
            + "FROM hbtest_record "
            + "WHERE "
            + "CONVERT(VARCHAR(10),hbtest_record.ope_d_t,120) = :date "
            + "ORDER BY hbtest_record.ope_d_t ASC";

    params.put("date", date.substring(0, 10));

    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 5;
    List<HbTestData> hbTestDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(HbTestData.class));
    insertHbTestData(hbTestDataList, sheet, currentRowNum);
  }

  private void insertHbTestData(List<HbTestData> hbTestDataList, Sheet sheet, int currentRowNum) {
    addRow(hbTestDataList, sheet, currentRowNum, 19);
    for (int i = 0; i < hbTestDataList.size(); i++) {
      HbTestData hbTestData = hbTestDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValueOfDate(hbTestData.getTestDate(), "yyyy/M/d"));
      row.getCell(1).setCellValue(hbTestData.getShiftNo());
      row.getCell(2).setCellValue(hbTestData.getTestBlockNo());
      row.getCell(3).setCellValue(getStringValue(hbTestData.getStandValue()));
      row.getCell(4).setCellValue(getStringValue(hbTestData.getTestResult()));
      row.getCell(5).setCellValue(getStringValueWithScale(hbTestData.getIndentaDia(), 2));
      row.getCell(6).setCellValue(getStringValueWithScale(hbTestData.getMIndentaDia(), 2));
      row.getCell(7).setCellValue(getStringValueWithScale(hbTestData.getDevIndentaDia(), 2));
      row.getCell(8).setCellValue(hbTestData.getResult());
      row.getCell(9).setCellValue(hbTestData.getOperator());
    }
  }
}

