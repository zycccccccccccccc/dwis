package tech.hciot.dwis.business.application.report.multi.prod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.MultiReportProdStatService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;

@Service
@Slf4j
public class ShipReportExportService {

  private static final String TEMPLATE_NAME = "2.5-ship-report.xlsx";

  @Autowired
  private MultiReportProdStatService multiReportProdStatService;

  // 2.5 综合查询业务-年/月度产量统计-发运报告
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    JSONObject stat = multiReportProdStatService.shipReport(parameterMap);
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/prod-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 标题
      sheet.getRow(0).getCell(0).setCellValue("发运成品报告");
      sheet.getRow(1).getCell(0)
        .setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));

      // 内容
      JSONArray resultList = stat.getJSONArray("resultList");
      int currentRow = 5;
      for (int i = 0; i < resultList.size(); i++) {
        int beginRow = currentRow;
        JSONObject result = resultList.getJSONObject(i);
        JSONArray dataList = result.getJSONArray("data");
        for (int j = 0; j < dataList.size(); j++) {
          JSONObject subData = dataList.getJSONObject(j);
          fillRow(sheet, 4, currentRow, subData);
          currentRow++;
          JSONArray subDataList = subData.getJSONArray("data");
          for (int k = 0; k < subDataList.size(); k++) {
            fillRow(sheet, 3, currentRow, subDataList.getJSONObject(k));
            currentRow++;
          }
        }
        CellRangeAddress region = new CellRangeAddress(beginRow, currentRow - 1, 0, 0);
        sheet.addMergedRegion(region);
      }
      fillRow(sheet, 4, currentRow, stat.getJSONObject("total"));
      CellRangeAddress region = new CellRangeAddress(currentRow, currentRow, 0, 1);
      sheet.addMergedRegion(region);
      sheet.getRow(currentRow).getCell(0).setCellValue("Grand Total");


      sheet.shiftRows(5, sheet.getLastRowNum(), -2);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void fillRow(Sheet sheet, int templateRow, int currentRow, JSONObject data) {
    // 拷贝空行
    ExcelUtil.copyCell(sheet, templateRow, 0,
      sheet, currentRow, 0,
      1, 8);
    int column = 0;
    Row row = sheet.getRow(currentRow);
    row.getCell(column++).setCellValue(data.getString("design"));
    row.getCell(column++).setCellValue(data.getString("shippedNo").equals("total") ?
      data.getString("shippedDate") : data.getString("shippedNo"));
    row.getCell(column++).setCellValue(data.getString("finished"));
    row.getCell(column++).setCellValue(data.getString("e3"));
    row.getCell(column++).setCellValue(data.getString("tapeOver840"));
    row.getCell(column++).setCellValue(data.getString("tapeOther"));
    row.getCell(column++).setCellValue(data.getString("wheel135"));
    row.getCell(column++).setCellValue(data.getString("customerName"));
  }
}
