package tech.hciot.dwis.business.application.report.multi.qa;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
public class PreScrapExportService {

  private static final String TEMPLATE_NAME = "3.7-pre-scrap.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.7 综合查询业务-质量统计-预检废品
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    JSONObject preScrap = multiReportQAStatService.preScrap(parameterMap);
    if (preScrap == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 日期
      DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
      String currentDate = format.format(new Date());
      sheet.getRow(1).getCell(3).setCellValue("报告生成日期：" + currentDate);

      // 数据
      JSONArray resultList = preScrap.getJSONArray("resultList");
      int currentRow = 4;
      for (int i = 0; i < resultList.size(); i++) {
        int beginRow = currentRow;
        JSONObject result = resultList.getJSONObject(i);

        // 每天的合计部分
        ExcelUtil.copyCell(sheet, 3, 0,
          sheet, currentRow, 0,
          1, 5);
        sheet.getRow(currentRow).getCell(0).setCellValue(result.getString("castDate"));
        sheet.getRow(currentRow).getCell(1).setCellValue("合计");
        sheet.getRow(currentRow).getCell(2).setCellValue(result.getString("pre"));
        sheet.getRow(currentRow).getCell(3).setCellValue(result.getString("scrap"));
        sheet.getRow(currentRow).getCell(4).setCellValue(result.getString("pct") + "%");
        currentRow++;

        JSONArray dataList = result.getJSONArray("data");
        for (int j = 0; j < dataList.size(); j++) {
          ExcelUtil.copyCell(sheet, 3, 0,
            sheet, currentRow, 0,
            1, 5);
          JSONObject data = dataList.getJSONObject(j);
          sheet.getRow(currentRow).getCell(1).setCellValue(data.getString("design"));
          sheet.getRow(currentRow).getCell(2).setCellValue(data.getString("pre"));
          sheet.getRow(currentRow).getCell(3).setCellValue(data.getString("scrap"));
          sheet.getRow(currentRow).getCell(4).setCellValue(data.getString("pct") + "%");
          currentRow++;
        }
        if (beginRow != currentRow - 1) {
          CellRangeAddress region = new CellRangeAddress(beginRow, currentRow - 1, 0, 0);
          sheet.addMergedRegion(region);
        }
      }

      // 合计部分
      ExcelUtil.copyCell(sheet, 3, 0,
        sheet, currentRow, 0,
        1, 5);
      JSONObject total = preScrap.getJSONObject("total");
      sheet.getRow(currentRow).getCell(0).setCellValue("合计");
      sheet.getRow(currentRow).getCell(1).setCellValue("");
      sheet.getRow(currentRow).getCell(2).setCellValue(total.getString("pre"));
      sheet.getRow(currentRow).getCell(3).setCellValue(total.getString("scrap"));
      sheet.getRow(currentRow).getCell(4).setCellValue(total.getString("pct") + "%");
      CellRangeAddress region = new CellRangeAddress(currentRow, currentRow, 0, 1);
      sheet.addMergedRegion(region);

      sheet.shiftRows(4, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
