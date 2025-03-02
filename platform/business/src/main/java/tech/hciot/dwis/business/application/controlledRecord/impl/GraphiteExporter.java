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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.GraphiteData;

@Service
@Slf4j
public class GraphiteExporter implements ControlledRecordExporter {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public String type() {
    return "graphite";
  }

  @Override
  public String fileName() {
    return "Mold-03-F-01-00.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "SELECT graphite_record.process_date,graphite_record.graphite_key,graphite_firm.manufacturer_name,"
        + "graphite_record.graphite,graphite_record.design,CASE graphite_record.cd WHEN 0 THEN '上箱' WHEN 1 THEN '下箱' END AS cd,"
        + "CASE graphite_record.status WHEN 1 THEN '新制' WHEN 4 THEN '改石墨号' WHEN 5 THEN '返修' END AS status,"
        + "'1085.5' AS 'size1086','1110.3' AS 'size1111',graphite_record.height,graphite_record.process_size,"
        + "graphite_record.rework_code,'<0.08' AS sample,graphite_record.process_id,graphite_record.graphite_ope_id "
        + "FROM graphite_record "
        + "INNER JOIN graphite_firm ON graphite_record.graphite_key = graphite_firm.graphite_key "
        + "WHERE graphite_record.process_date = :date "
        + "AND graphite_record.status in (1,4,5) "
        + "ORDER BY graphite_record.create_time ASC";

    params.put("date", date.substring(0, 10));
    List<GraphiteData> graphiteDataList = template.query(sql, params, BeanPropertyRowMapper.newInstance(GraphiteData.class));
    Sheet sheet = workbook.getSheetAt(0);
    insertGraphiteData(graphiteDataList, sheet, 3);
  }

  private void insertGraphiteData(List<GraphiteData> graphiteDataList, Sheet sheet, int currentRowNum) {
    addRow(graphiteDataList, sheet, currentRowNum, 10);
    for (int i = 0; i < graphiteDataList.size(); i++) {
      GraphiteData graphiteData = graphiteDataList.get(i);
      Row row = sheet.getRow(currentRowNum + i);
      row.getCell(0).setCellValue(i + 1);
      row.getCell(1).setCellValue(getStringValueOfDate(graphiteData.getProcessDate(), "yyyy/M/d"));
      row.getCell(2).setCellValue(graphiteData.getGraphiteKey());
      row.getCell(3).setCellValue(graphiteData.getManufacturerName());
      row.getCell(4).setCellValue(graphiteData.getGraphite());
      row.getCell(5).setCellValue(graphiteData.getDesign());
      row.getCell(6).setCellValue(graphiteData.getCd());
      row.getCell(7).setCellValue(graphiteData.getStatus());
      row.getCell(8).setCellValue(graphiteData.getSize1086());
      row.getCell(9).setCellValue(graphiteData.getSize1111());
      row.getCell(10).setCellValue(getStringValue(graphiteData.getHeight()));
      row.getCell(11).setCellValue(getStringValue(graphiteData.getProcessSize()));
      row.getCell(12).setCellValue(graphiteData.getReworkCode());
      row.getCell(13).setCellValue(graphiteData.getSample());
      row.getCell(14).setCellValue(graphiteData.getProcessId());
      row.getCell(15).setCellValue(graphiteData.getGraphiteOpeId());
    }
  }
}

