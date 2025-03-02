package tech.hciot.dwis.business.application.report.multi.qa;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.MultiReportQAStatService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;

@Service
@Slf4j
public class PourTimeAndScrapExportService {

  private static final String TEMPLATE_NAME = "3.5-pour-time-and-scrap.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.5 综合查询业务-质量统计-浇注时间与废品
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    List<Map<String, String>> resultList = multiReportQAStatService.pourTimeAndScrap(parameterMap);
    if (resultList.isEmpty()) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      String[] dateArray = resultList.get(0).keySet().toArray(new String[0]);
      for (int i = 0; i < dateArray.length - 1; i++) {
        ExcelUtil.copyCell(sheet, 0, 1,
          sheet, 0, i + 2,
          31, 1, false);
      }
      for (int i = 0; i < dateArray.length; i++) {
        sheet.getRow(0).getCell(i + 1).setCellValue(dateArray[i]);
      }

      for (int i = 0; i < resultList.size(); i++) {
        Map<String, String> dataMap = resultList.get(i);
        int j = 0;
        for (Entry data : dataMap.entrySet()) {
          sheet.getRow(i + 1).getCell(j + 1)
            .setCellValue(data.getValue() == null ? "" : data.getValue().toString());
          j++;
        }
      }

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
