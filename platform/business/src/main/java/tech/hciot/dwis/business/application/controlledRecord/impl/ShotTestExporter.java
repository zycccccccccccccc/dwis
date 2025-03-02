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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.ShotTestData;

@Service
@Slf4j
public class ShotTestExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "shottest";
  }

  @Override
  public String fileName() {
    return "Insp-01-F-01-05.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT shot_test_record.ope_d_t,shot_test_record.shift_no,shot_test_record.peening_time,"
            + "shot_test_record.intensity_front,shot_test_record.intensity_back,shot_test_record.coverage_front,"
            + "shot_test_record.coverage_back,shot_test_record.sieve_no,shot_test_record.shot_type,shot_test_record.amount_onsieve,"
            + "shot_test_record.shotpeener_no,account.nickname "
            + "FROM shot_test_record "
            + "INNER JOIN "
            + "account ON account.username = shot_test_record.operator "
            + "WHERE "
            + "CONVERT(VARCHAR(10),shot_test_record.ope_d_t,120) = :date "
            + "ORDER BY shot_test_record.ope_d_t ASC";

    params.put("date", date.substring(0, 10));

    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 6;
    List<ShotTestData> shotTestDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(ShotTestData.class));
    insertShotTestData(shotTestDataList, sheet, currentRowNum);
  }

  private void insertShotTestData(List<ShotTestData> shotTestDataList, Sheet sheet, int currentRowNum) {
    addRow(shotTestDataList, sheet, currentRowNum, 13);
    for (int i = 0; i < shotTestDataList.size(); i++) {
      ShotTestData shotTestData = shotTestDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValueOfDate(shotTestData.getOpeDT(), "yyyy/M/d"));
      row.getCell(1).setCellValue(shotTestData.getShiftNo());
      row.getCell(2).setCellValue(getStringValueOfDate(shotTestData.getOpeDT(), "HH:mm"));
      row.getCell(3).setCellValue(getStringValue(shotTestData.getPeeningTime()));
      row.getCell(4).setCellValue(getStringValueWithScale(shotTestData.getIntensityFront(), 2));
      row.getCell(5).setCellValue(getStringValueWithScale(shotTestData.getIntensityBack(), 2));
      row.getCell(6).setCellValue(getStringValue(shotTestData.getCoverageFront()));
      row.getCell(7).setCellValue(getStringValue(shotTestData.getCoverageBack()));
      row.getCell(8).setCellValue(getStringValue(shotTestData.getSieveNo()));
      row.getCell(9).setCellValue(shotTestData.getShotType());
      row.getCell(10).setCellValue(getStringValue(shotTestData.getAmountOnsieve()));
      row.getCell(11).setCellValue(shotTestData.getShotpeenerNo());
      row.getCell(12).setCellValue(shotTestData.getNickname());
    }
  }
}

