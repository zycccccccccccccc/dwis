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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.TapeData;

@Service
@Slf4j
public class TapeExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "tape";
  }

  @Override
  public String fileName() {
    return "Insp-04-F-01-00.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT tape_testing_record.ope_d_t,tape_testing_record.shift_no,tape_testing_record.wheel_serial,"
            + "tape_testing_record.design,tape_testing_record.tape_size,tape_testing_record.ts,account.nickname,'' AS memo "
            + "FROM tape_testing_record "
            + "INNER JOIN "
            + "account ON account.username = tape_testing_record.inspector_id "
            + "WHERE "
            + "CONVERT(VARCHAR(10),tape_testing_record.ope_d_t,120) = :date "
            + "ORDER BY tape_testing_record.ope_d_t ASC";

    params.put("date", date.substring(0, 10));

    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 5;
    List<TapeData> tapeDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(TapeData.class));
    insertTapeData(tapeDataList, sheet, currentRowNum);
  }

  private void insertTapeData(List<TapeData> tapeDataList, Sheet sheet, int currentRowNum) {
    addRow(tapeDataList, sheet, currentRowNum, 10);
    for (int i = 0; i < tapeDataList.size(); i++) {
      TapeData tapeData = tapeDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(getStringValueOfDate(tapeData.getOpeDT(), "yyyy/M/d"));
      row.getCell(1).setCellValue(tapeData.getShiftNo());
      row.getCell(2).setCellValue(tapeData.getWheelSerial());
      row.getCell(3).setCellValue(tapeData.getDesign());
      row.getCell(4).setCellValue(getStringValueWithScale(tapeData.getTapeSize(), 1));
      row.getCell(5).setCellValue(getStringValue(tapeData.getTs()));
      row.getCell(6).setCellValue(getStringValueOfDate(tapeData.getOpeDT(), "HH:mm"));
      row.getCell(7).setCellValue(tapeData.getNickname());
      row.getCell(8).setCellValue(tapeData.getMemo());
    }
  }
}

