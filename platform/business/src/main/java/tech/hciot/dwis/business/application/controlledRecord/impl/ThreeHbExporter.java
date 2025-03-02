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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExporter;
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.ThreeHbData;

@Service
@Slf4j
public class ThreeHbExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "threehb";
  }

  @Override
  public String fileName() {
    return "Insp-03-F-03-00.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT threehb_record.ope_d_t AS test_date,threehb_record.shift_no,threehb_record.wheel_serial,threehb_record.brinnel1,"
            + "threehb_record.brinnel2,threehb_record.brinnel3,threehb_record.difference,'合格'AS result,"
            + "threehb_record.re_test,threehb_record.is_inspec_check,account.nickname AS operator "
            + "FROM threehb_record "
            + "INNER JOIN "
            + "account ON account.username = threehb_record.operator "
            + "WHERE "
            + "CONVERT(VARCHAR(10),threehb_record.ope_d_t,120) = :date "
            + "ORDER BY threehb_record.ope_d_t ASC";

    params.put("date", date.substring(0, 10));

    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 6;
    List<ThreeHbData> threeHbDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(ThreeHbData.class));
    insertHbTestData(threeHbDataList, sheet, currentRowNum);
  }

  private void insertHbTestData(List<ThreeHbData> threeHbDataList, Sheet sheet, int currentRowNum) {
    addRow(threeHbDataList, sheet, currentRowNum, 13);
    for (int i = 0; i < threeHbDataList.size(); i++) {
      ThreeHbData threeHbData = threeHbDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValueOfDate(threeHbData.getTestDate(), "yyyy/M/d"));
      row.getCell(1).setCellValue(threeHbData.getShiftNo());
      row.getCell(2).setCellValue(threeHbData.getWheelSerial());
      row.getCell(3).setCellValue(getStringValue(threeHbData.getBrinnel1()));
      row.getCell(4).setCellValue(getStringValue(threeHbData.getBrinnel2()));
      row.getCell(5).setCellValue(getStringValue(threeHbData.getBrinnel3()));
      row.getCell(6).setCellValue(getStringValue(threeHbData.getDifference()));
      row.getCell(7).setCellValue(threeHbData.getResult());
      row.getCell(8).setCellValue(threeHbData.getReTest() == 0 ? "否" : "是");
      row.getCell(9).setCellValue(threeHbData.getIsInspecCheck() == 0 ? "否" : "是");
      row.getCell(10).setCellValue(threeHbData.getOperator());
    }
  }
}

