package tech.hciot.dwis.business.application.report.multi.prod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.prod.RepositoryReportResult;

@Service
@Slf4j
public class RepositoryReportExportService {

  private static final String TEMPLATE_NAME = "2.1-repository-report.xlsx";

  @Autowired
  private MultiReportProdStatService multiReportProdStatService;

  // 2.1 综合查询业务-年/月度产量统计-库存报告
  public void export(List<String> design,
                     HttpServletResponse response) {
    RepositoryReportResult repositoryReportResult = multiReportProdStatService.repositoryReport(design);
    if (repositoryReportResult == null) {
      return;
    }
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/prod-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 生成日期
      DateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      String date = timeFormat.format(new Date());
      sheet.getRow(1).getCell(0).setCellValue("生成日期：" + date);

      // 成品数据填充
      JSONObject finish = repositoryReportResult.getFinish();
      fillFinishData(finish, sheet);

      // 在制品数据填充
      JSONObject machining = repositoryReportResult.getMachining();
      fillMachiningData(machining, sheet);

      //库存合计数据填充
      int lastNum = sheet.getLastRowNum() + 2;
      ExcelUtil.copyCell(sheet, 5, 0,
              sheet, lastNum, 0,
              1, 3);
      Row row = sheet.getRow(lastNum);
      row.getCell(0).setCellValue("库存总合计");
      row.getCell(2).setCellValue(repositoryReportResult.getTotal());
      CellRangeAddress region = new CellRangeAddress(lastNum, lastNum, 0, 1);
      sheet.addMergedRegion(region);

      //表单数据上移两行
      sheet.shiftRows(6, sheet.getLastRowNum(), -2);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void fillFinishData(JSONObject finish, Sheet sheet) {
    int currentRow = 6;
    JSONArray finishResultList = finish.getJSONArray("resultList");
    for (int i = 0; i < finishResultList.size(); i++) {
      int beginRow = currentRow;
      JSONObject result = finishResultList.getJSONObject(i);
      JSONArray dataList = result.getJSONArray("data");
      for (int j = 0; j < dataList.size(); j++) {
        JSONObject subData = dataList.getJSONObject(j);
        fillFinishRow(sheet, 5, currentRow, subData);
        currentRow++;
        JSONArray subDataList = subData.getJSONArray("data");
        for (int k = 0; k < subDataList.size(); k++) {
          fillFinishRow(sheet, 4, currentRow, subDataList.getJSONObject(k));
          currentRow++;
        }
      }
      fillFinishRow(sheet, 5, currentRow, result);
      sheet.getRow(currentRow).getCell(1).setCellValue("小计");

      CellRangeAddress region = new CellRangeAddress(beginRow, currentRow, 0, 0);
      sheet.addMergedRegion(region);
      currentRow++;
    }
    fillFinishRow(sheet, 5, currentRow, finish.getJSONObject("total"));
    CellRangeAddress region = new CellRangeAddress(currentRow, currentRow, 0, 1);
    sheet.addMergedRegion(region);
    sheet.getRow(currentRow).getCell(0).setCellValue("合计");
  }

  private void fillMachiningData(JSONObject machining, Sheet sheet) {
    int currentRow = 6;
    JSONArray machiningResultList = machining.getJSONArray("resultList");
    for (int i = 0; i < machiningResultList.size(); i++) {
      int beginRow = currentRow;
      JSONObject result = machiningResultList.getJSONObject(i);
      fillMachiningRow(sheet, 5, currentRow, result);
      currentRow++;

      JSONArray dataList = result.getJSONArray("data");
      for (int j = 0; j < dataList.size(); j++) {
        JSONObject subResult = dataList.getJSONObject(j);
        fillMachiningRow(sheet, 4, currentRow, subResult);
        currentRow++;
      }
      CellRangeAddress region = new CellRangeAddress(beginRow, currentRow - 1, 8, 8);
      sheet.addMergedRegion(region);
    }
    JSONObject machiningTotal = machining.getJSONObject("total");
    sheet.getRow(currentRow).getCell(8).setCellValue("合计");
    sheet.getRow(currentRow).getCell(10).setCellValue(machiningTotal.getString("cnt"));
    CellRangeAddress region = new CellRangeAddress(currentRow, currentRow, 8, 9);
    sheet.addMergedRegion(region);
  }

  // 成品数据填充
  private void fillFinishRow(Sheet sheet, int templateRow, int currentRow, JSONObject data) {
    // 拷贝空行
    ExcelUtil.copyCell(sheet, templateRow, 0,
            sheet, currentRow, 0,
            1, 11);
    int column = 0;
    Row row = sheet.getRow(currentRow);
    row.getCell(column++).setCellValue(data.getString("design"));
    row.getCell(column++).setCellValue(data.getString("boreSize").equals("total") ?
            data.getString("wheelW") : data.getString("boreSize"));
    row.getCell(column++).setCellValue(data.getString("cnt"));
    row.getCell(column++).setCellValue(data.getString("bigTape"));
    row.getCell(column++).setCellValue(data.getString("smallTape"));
    row.getCell(column++).setCellValue(data.getString("e3"));
    row.getCell(column++).setCellValue(data.getString("stock"));
  }

  // 在制品数据填充
  private void fillMachiningRow(Sheet sheet, int templateRow, int currentRow, JSONObject data) {
    int column = 8;
    Row row = sheet.getRow(currentRow);
    if (row == null) {
      ExcelUtil.copyCell(sheet, templateRow, 8,
              sheet, currentRow, 8,
              1, 3);
    }
    row.getCell(column++).setCellValue(data.getString("design"));
    row.getCell(column++).setCellValue(data.getString("process").equals("total") ? "" : data.getString("process"));
    row.getCell(column++).setCellValue(data.getString("cnt"));
  }
}
