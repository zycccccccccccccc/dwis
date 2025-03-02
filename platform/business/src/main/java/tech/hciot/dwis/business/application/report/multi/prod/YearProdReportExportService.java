package tech.hciot.dwis.business.application.report.multi.prod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.MultiReportProdStatService;

@Service
@Slf4j
public class YearProdReportExportService {

  private static final String TEMPLATE_NAME = "2.6-year-prod-report.xlsx";

  @Autowired
  private MultiReportProdStatService multiReportProdStatService;

  // 2.6 综合查询业务-年/月度产量统计-年度生产汇总报告
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    JSONObject stat = multiReportProdStatService.yearProdReport(parameterMap);
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/prod-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 日期
      DateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      String date = timeFormat.format(new Date());
      sheet.getRow(17).getCell(0).setCellValue("报告生成日期：" + date);

      // 内容
      JSONArray resultList = stat.getJSONArray("resultList");
      int currentRow = 3;
      for (int i = 0; i < resultList.size(); i++) {
        JSONObject result = resultList.getJSONObject(i);
        fillRow(sheet, currentRow, result);
        currentRow++;
      }
      fillRow(sheet, currentRow, stat.getJSONObject("total"));

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void fillRow(Sheet sheet, int currentRow, JSONObject data) {
    int column = 0;
    Row row = sheet.getRow(currentRow);
    row.getCell(column++).setCellValue(data.getString("dd").equals("total") ? "合计" : data.getString("dd"));
    row.getCell(column++).setCellValue(data.getString("cnt"));
    row.getCell(column++).setCellValue(data.getString("pres"));
    row.getCell(column++).setCellValue(data.getString("scrap"));
    row.getCell(column++).setCellValue(data.getString("presScrap"));
    row.getCell(column++).setCellValue(data.getString("finishDock"));
    row.getCell(column++).setCellValue(data.getString("finishTapeOver840"));
    row.getCell(column++).setCellValue(data.getString("finishTapeUnder840"));
    row.getCell(column++).setCellValue(data.getString("finishE3"));
    row.getCell(column++).setCellValue(data.getString("stock"));
    row.getCell(column++).setCellValue(data.getString("shipDock"));
    row.getCell(column++).setCellValue(data.getString("shipTapeOver840"));
    row.getCell(column++).setCellValue(data.getString("shipTapeUnder840"));
    row.getCell(column++).setCellValue(data.getString("shipE3"));
    row.getCell(column++).setCellValue(data.getString("confirmedScrap"));
  }
}
