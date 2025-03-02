package tech.hciot.dwis.business.application.report.multi.qa;

import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.report.MultiReportQAStatService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelReworkScrapResult;

@Service
@Slf4j
public class WheelReworkScrapExportService {

  private static final String TEMPLATE_NAME = "3.4-wheel-rework-scrap.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.4 综合查询业务-质量统计-车轮返废
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    WheelReworkScrapResult wheelReworkScrapResult = multiReportQAStatService.wheelReworkScrapReport(parameterMap);
    if (wheelReworkScrapResult == null) {
      return;
    }
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 日期
      String endDate = StandardTimeUtil.beforeDay((String) parameterMap.get("endDate"));
      sheet.getRow(1).getCell(0)
        .setCellValue(parameterMap.get("beginDate") + " to " + endDate);

      List<JSONObject> resultList = wheelReworkScrapResult.getResultList();
      int currentRow = 9;
      for (int i = 0; i < resultList.size(); i++) {

        JSONObject data = resultList.get(i);
        if (i != resultList.size() - 1) {
          ExcelUtil.copyCell(sheet, 9, 0,
            sheet, currentRow + 6, 0,
            6, 9);
        }

        sheet.getRow(currentRow).getCell(0).setCellValue(data.getString("castDate"));

        sheet.getRow(currentRow + 1).getCell(1).setCellValue(data.getString("castTotal"));
        sheet.getRow(currentRow + 1).getCell(3).setCellValue(data.getString("pre"));
        sheet.getRow(currentRow + 1).getCell(4).setCellValue(data.getString("reworkTotal"));
        sheet.getRow(currentRow + 1).getCell(5).setCellValue(data.getString("rework9a"));
        sheet.getRow(currentRow + 1).getCell(6).setCellValue(data.getString("rework9c"));
        sheet.getRow(currentRow + 1).getCell(7).setCellValue(data.getString("rework23"));
        sheet.getRow(currentRow + 1).getCell(8).setCellValue(data.getString("rework88"));

        sheet.getRow(currentRow + 3).getCell(1).setCellValue(data.getString("rework12"));
        sheet.getRow(currentRow + 3).getCell(2).setCellValue(data.getString("rework67r"));
        sheet.getRow(currentRow + 3).getCell(3).setCellValue(data.getString("rework67f"));
        sheet.getRow(currentRow + 3).getCell(4).setCellValue(data.getString("rework67h"));
        sheet.getRow(currentRow + 3).getCell(5).setCellValue(data.getString("rework44a"));
        sheet.getRow(currentRow + 3).getCell(6).setCellValue(data.getString("rework58"));
        sheet.getRow(currentRow + 3).getCell(7).setCellValue(data.getString("rework59"));
        sheet.getRow(currentRow + 3).getCell(8).setCellValue(data.getString("rework8c"));

        sheet.getRow(currentRow + 5).getCell(1).setCellValue(data.getString("rework67c"));
        sheet.getRow(currentRow + 5).getCell(2).setCellValue(data.getString("rework2a"));
        sheet.getRow(currentRow + 5).getCell(3).setCellValue(data.getString("reworkH1"));
        sheet.getRow(currentRow + 5).getCell(4).setCellValue(data.getString("reworkH2"));
        sheet.getRow(currentRow + 5).getCell(5).setCellValue(data.getString("reworkH3"));
        sheet.getRow(currentRow + 5).getCell(6).setCellValue(data.getString("reworkH4"));
        sheet.getRow(currentRow + 5).getCell(7).setCellValue(data.getString("reworkH5"));
        sheet.getRow(currentRow + 5).getCell(8).setCellValue(data.getString("reworkH6"));
        currentRow += 6;
      }

      JSONObject total = wheelReworkScrapResult.getTotal();

      sheet.getRow(3).getCell(1).setCellValue(total.getString("castTotal"));
      sheet.getRow(3).getCell(3).setCellValue(total.getString("pre"));
      sheet.getRow(3).getCell(4).setCellValue(total.getString("reworkTotal"));
      sheet.getRow(3).getCell(5).setCellValue(total.getString("rework9a"));
      sheet.getRow(3).getCell(6).setCellValue(total.getString("rework9c"));
      sheet.getRow(3).getCell(7).setCellValue(total.getString("rework23"));
      sheet.getRow(3).getCell(8).setCellValue(total.getString("rework88"));

      sheet.getRow(5).getCell(1).setCellValue(total.getString("rework12"));
      sheet.getRow(5).getCell(2).setCellValue(total.getString("rework67r"));
      sheet.getRow(5).getCell(3).setCellValue(total.getString("rework67f"));
      sheet.getRow(5).getCell(4).setCellValue(total.getString("rework67h"));
      sheet.getRow(5).getCell(5).setCellValue(total.getString("rework44a"));
      sheet.getRow(5).getCell(6).setCellValue(total.getString("rework58"));
      sheet.getRow(5).getCell(7).setCellValue(total.getString("rework59"));
      sheet.getRow(5).getCell(8).setCellValue(total.getString("rework8c"));

      sheet.getRow(7).getCell(1).setCellValue(total.getString("rework67c"));
      sheet.getRow(7).getCell(2).setCellValue(total.getString("rework2a"));
      sheet.getRow(7).getCell(3).setCellValue(total.getString("reworkH1"));
      sheet.getRow(7).getCell(4).setCellValue(total.getString("reworkH2"));
      sheet.getRow(7).getCell(5).setCellValue(total.getString("reworkH3"));
      sheet.getRow(7).getCell(6).setCellValue(total.getString("reworkH4"));
      sheet.getRow(7).getCell(7).setCellValue(total.getString("reworkH5"));
      sheet.getRow(7).getCell(8).setCellValue(total.getString("reworkH6"));

//      sheet.shiftRows(15, sheet.getLastRowNum(), -6);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
