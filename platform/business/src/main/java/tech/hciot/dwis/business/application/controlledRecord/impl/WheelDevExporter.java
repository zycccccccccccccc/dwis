package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;

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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.WheelDevData;

@Service
@Slf4j
public class WheelDevExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "wheeldev";
  }

  @Override
  public String fileName() {
    return "Insp-03-F-02-02.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 2;
    Row row = sheet.getRow(currentRowNum);
    row.getCell(2).setCellValue(getStringValueOfDate(parseDate(date.substring(0, 10)), "yyyy/M/d"));

    for (int i = 0; i < 3; i++) {
      List<WheelDevData> wheelDevDataList = getShiftData(date, String.valueOf(i + 1));
      if (!wheelDevDataList.isEmpty()) {
        WheelDevData rowData = wheelDevDataList.get(0);
        row = sheet.getRow(5 + i * 2);
        row.getCell(3).setCellValue(rowData.getWheelSerial());
        row.getCell(4).setCellValue(rowData.getDistance());
        row.getCell(5).setCellValue(getStringValue(rowData.getRimDev()));
        row.getCell(6).setCellValue(getStringValue(rowData.getHubBack()));
        row.getCell(7).setCellValue(getStringValue(rowData.getHubFront()));
        row.getCell(8).setCellValue(getStringValue(rowData.getSideBack()));
        row.getCell(9).setCellValue(getStringValue(rowData.getSideFront()));
        row.getCell(10).setCellValue(rowData.getNickname());
        row.getCell(11).setCellValue(rowData.getMemo());

        row = sheet.getRow(13 + i);
        row.getCell(2).setCellValue(rowData.getWheelSerial());
        row.getCell(3).setCellValue(getStringValue(rowData.getFrontRim()));
        row.getCell(5).setCellValue(getStringValue(rowData.getBackRim()));
        row.getCell(7).setCellValue(getStringValue(rowData.getDiffRim()));
        row.getCell(9).setCellValue(rowData.getNickname());
        row.getCell(11).setCellValue(rowData.getMemo());
      }
      if (wheelDevDataList.size() > 1) {
        WheelDevData rowData = wheelDevDataList.get(wheelDevDataList.size() - 1);
        row = sheet.getRow(6 + i * 2);
        row.getCell(3).setCellValue(rowData.getWheelSerial());
        row.getCell(4).setCellValue(rowData.getDistance());
        row.getCell(5).setCellValue(getStringValue(rowData.getRimDev()));
        row.getCell(6).setCellValue(getStringValue(rowData.getHubBack()));
        row.getCell(7).setCellValue(getStringValue(rowData.getHubFront()));
        row.getCell(8).setCellValue(getStringValue(rowData.getSideBack()));
        row.getCell(9).setCellValue(getStringValue(rowData.getSideFront()));
        row.getCell(10).setCellValue(rowData.getNickname());
        row.getCell(11).setCellValue(rowData.getMemo());
      }
    }
  }

  private List<WheelDevData> getShiftData(String date, String shiftNo) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql =
        "SELECT wheel_dev_record.ope_d_t,wheel_dev_record.shift_no,wheel_dev_record.wheel_serial,wheel_dev_record.distance,"
            + "wheel_dev_record.rim_dev,wheel_dev_record.hub_back,wheel_dev_record.hub_front,wheel_dev_record.side_back,"
            + "wheel_dev_record.side_front,wheel_dev_record.front_rim,wheel_dev_record.back_rim,wheel_dev_record.diff_rim,"
            + "account.nickname,''AS memo "
            + "FROM wheel_dev_record "
            + "INNER JOIN "
            + "account ON account.username = wheel_dev_record.operator "
            + "WHERE "
            + "CONVERT(VARCHAR(10),wheel_dev_record.ope_d_t,120) = :date AND wheel_dev_record.shift_no = :shiftNo "
            + "ORDER BY wheel_dev_record.ope_d_t ASC";

    params.put("date", date.substring(0, 10));
    params.put("shiftNo", shiftNo);
    return template.query(sql, params, BeanPropertyRowMapper.newInstance(WheelDevData.class));
  }
}

