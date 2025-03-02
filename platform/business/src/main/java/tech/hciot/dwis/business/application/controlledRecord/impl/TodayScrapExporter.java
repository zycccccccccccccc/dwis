package tech.hciot.dwis.business.application.controlledRecord.impl;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import tech.hciot.dwis.business.application.controlledRecord.impl.dto.ScrapWheelData;
import tech.hciot.dwis.business.application.report.ReportSqlService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAData;

import javax.annotation.Resource;

@Service
@Slf4j
public class TodayScrapExporter implements ControlledRecordExporter {

  private static final String REPORT_NAME = "composite-report/qa-stat";

  private static final int PAGE_SIZE = 100;
  private static final int TOTAL_ROW = 56;
  private static final int TOTAL_COLUMN = 7;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Resource
  SqlTemplateParser sqlTemplateParser;

  @Autowired
  private ReportSqlService reportSqlService;

  @Override
  public String type() {
    return "todayscrap";
  }

  @Override
  public String fileName() {
    return "today-scrap.xlsx";
  }

  @Override
  public void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId) {

    Map<String, Object> params = new HashMap<>();

    String beginDate = "";
    String endDate = "";
    if (date.contains(",")) {
      String[] temp = StringUtils.split(date, ",");
      beginDate = temp[0];
      endDate = temp[1];
    }

    params.put("beginDate", beginDate);
    params.put("endDate", endDate);
    params.put("opeId", opeId);

    List<ScrapWheelData> scrapWheelDataList = reportSqlService.queryResultList(REPORT_NAME,
            "3.10-confirm-scrap-list",
            params,
            ScrapWheelData.class);

    //单独查询每种轮型的数量，显示在Excel的第3行
    List<ScrapWheelData> scrapWheelDataList2 = reportSqlService.queryResultList(REPORT_NAME,
            "3.10-confirm-scrap-design",
            params,
            ScrapWheelData.class);

    insertData(beginDate, endDate, opeId, workbook, scrapWheelDataList, scrapWheelDataList2);
  }

  private void insertData(String beginDate, String endDate, String opeId, Workbook workbook,
      List<ScrapWheelData> scrapWheelDataList, List<ScrapWheelData> scrapWheelDataList2) {
    int pageSum = (scrapWheelDataList.size() + PAGE_SIZE - 1) / PAGE_SIZE;
    Sheet sheet = workbook.getSheetAt(0);

    Row row = sheet.getRow(1);
    row.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK)
        .setCellValue(getStringValueOfDate(parseDate(beginDate), "yyyy/MM/dd") + " to " + getStringValueOfDate(parseDate(endDate),
            "yyyy/MM/dd"));

    row = sheet.getRow(2);

    //取出轮型值和对应的数量,放入第三行单元格
    StringBuffer sbf = new StringBuffer();
    for (int s = 0; s < scrapWheelDataList2.size(); s++) {
      ScrapWheelData sd = scrapWheelDataList2.get(s);
      String design = sd.getDesign();//轮型名称
      String counts = sd.getCounts();//轮型对应的数量
      sbf.append(design + ":" + counts + " ");
    }
    String cell3 = sbf.toString();
    row.getCell(0, MissingCellPolicy.CREATE_NULL_AS_BLANK)
        .setCellValue(cell3 + "操作员工号：" + opeId);

    for (int page = 1; page < pageSum; page++) {
      log.info("copy page {}", page);
      ExcelUtil.copyCell(sheet, 0, 0,
          sheet, TOTAL_ROW * page, 0,
          TOTAL_ROW, TOTAL_COLUMN);
    }

    for (int k = 0; k < pageSum; k++) {
      int currentPageFirstRow = k * TOTAL_ROW;
      for (int i = 0; i < PAGE_SIZE; i++) {
        int index = k * PAGE_SIZE + i;
        if (index < scrapWheelDataList.size()) {
          ScrapWheelData scrapWheelData = scrapWheelDataList.get(index);
          if (i < PAGE_SIZE / 2) {
            row = sheet.getRow(currentPageFirstRow + i + 4);
            row.getCell(0).setCellValue(getStringValue(scrapWheelData.getWheelSerial()));
            row.getCell(1).setCellValue(getStringValue(scrapWheelData.getDesign()));
            row.getCell(2).setCellValue(getStringValue(scrapWheelData.getScrapCode()));
          } else {
            row = sheet.getRow(currentPageFirstRow + i + 4 - PAGE_SIZE / 2);
            row.getCell(4).setCellValue(getStringValue(scrapWheelData.getWheelSerial()));
            row.getCell(5).setCellValue(getStringValue(scrapWheelData.getDesign()));
            row.getCell(6).setCellValue(getStringValue(scrapWheelData.getScrapCode()));
          }
        } else {
          break;
        }
      }

      row = sheet.getRow(currentPageFirstRow + TOTAL_ROW - 2);
      row.getCell(5).setCellValue(StringUtils.join("Page ", k + 1, " of ", pageSum));
    }

    sheet.protectSheet("dwis");
  }
}

