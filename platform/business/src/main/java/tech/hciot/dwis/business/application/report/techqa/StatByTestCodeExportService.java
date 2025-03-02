package tech.hciot.dwis.business.application.report.techqa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.TechQAReportService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;

@Service
@Slf4j
public class StatByTestCodeExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response,
                     TechQAExporter exporter) {
    JSON result = techQAReportService.stat(parameterMap, exporter.sqlTemplate());
    if (result == null) {
      return;
    }
    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "level3-report.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet, exporter);

      // 拷贝单元格模板
      copyTemplate(workbook, (JSONArray) result);

      // 拷贝数据部分内容
      copyData(sheet, (JSONArray) result);

      workbook.removeSheetAt(1);
      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = exporter.sqlTemplate().substring(exporter.sqlTemplate().indexOf("-") + 1) + ".xlsx";
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 拷贝数据部分的模板
  private void copyTemplate(Workbook workbook, JSONArray dataArray) {
    Sheet currentSheet = workbook.getSheetAt(0);
    Sheet templateSheet = workbook.getSheetAt(1);
    int currentRow = 4;
    for (int i = 0; i < dataArray.size(); i++) { // 一层
      int level1BeginRow = currentRow;
      JSONObject level1Data = dataArray.getJSONObject(i);

      // 一层的合计
      ExcelUtil.copyCell(templateSheet, 0, 0,
        currentSheet, currentRow, 0,
        2, 13);
      currentSheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow + 1, 1, 2));
      currentRow += 2;

      JSONArray level2DataArray = level1Data.getJSONArray("data");
      for (int j = 0; j < level2DataArray.size(); j++) { // 二层
        int level2BeginRow = currentRow;
        JSONObject level2Data = level2DataArray.getJSONObject(j);

        JSONArray level3DataArray = level2Data.getJSONArray("data");
        for (int k = 0; k < level3DataArray.size(); k++) { // 三层
          ExcelUtil.copyCell(templateSheet, 2, 0,
            currentSheet, currentRow, 0,
            6, 13);
          currentRow += 6;
        }
        if (level2BeginRow != currentRow - 1) {
          currentSheet.addMergedRegion(new CellRangeAddress(level2BeginRow, currentRow - 1, 1, 1));
        }
      }
      if (level1BeginRow != currentRow - 1) {
        currentSheet.addMergedRegion(new CellRangeAddress(level1BeginRow, currentRow - 1, 0, 0));
      }
    }
  }

  // 拷贝数据部分内容
  private void copyData(Sheet sheet, JSONArray dataArray) {
    int currentRow = 4;
    for (int i = 0; i < dataArray.size(); i++) { // 一层
      JSONObject level1Data = dataArray.getJSONObject(i);
      sheet.getRow(currentRow).getCell(0).setCellValue(level1Data.getString("majorTitle")); // 一层标题
      sheet.getRow(currentRow).getCell(1).setCellValue("合计"); // 合计
      fileStatData(sheet, currentRow, 3, level1Data); // 合计部分
      currentRow += 2;

      JSONArray leve2DataArray = level1Data.getJSONArray("data");
      for (int j = 0; j < leve2DataArray.size(); j++) { // 二层
        JSONObject leve2Data = leve2DataArray.getJSONObject(j);
        sheet.getRow(currentRow).getCell(1).setCellValue(leve2Data.getString("minorTitle")); // 二层标题

        JSONArray leve3DataArray = leve2Data.getJSONArray("data");
        for (int k = 0; k < leve3DataArray.size(); k++) { // 三层
          JSONObject leve3Data = leve3DataArray.getJSONObject(k);
          sheet.getRow(currentRow).getCell(2).setCellValue(leve3Data.getString("subTitle")); // 三层标题
          fileStatData(sheet, currentRow, 3, leve3Data);
          currentRow += 2;

          // 详情部分数据
          JSONArray detailArray = leve3Data.getJSONArray("data");
          if (detailArray != null && !detailArray.isEmpty()) {
            List<JSONObject> detailList = detailArray.toJavaList(JSONObject.class);
            for (int m = 0; m < detailList.size(); m++) {
              int row = m / 5;
              int column = m - row * 5;
              sheet.getRow(currentRow + row).getCell(3 + column * 2)
                .setCellValue(detailList.get(m).getString("detail2"));
            }
          }
          currentRow += 4;
        }
      }
    }
  }

  // 替换宏参数
  private void replaceParameters(Map<String, Object> parameterMap, Sheet sheet, TechQAExporter exporter) {

    for (int r = 0, rowNum = sheet.getLastRowNum(); r <= rowNum; r++) {
      Row row = sheet.getRow(r);
      if (row == null) {
        continue;
      }
      for (int c = 0, columnNum = row.getLastCellNum(); c <= columnNum; c++) {
        Cell cell = row.getCell(c);
        if (cell == null) {
          continue;
        }
        String cellValue = ExcelUtil.getCellStringValue(cell);
        if (cellValue == null || !cellValue.startsWith("$")) {
          continue;
        }
        List<String> testCodeList = (List<String>) parameterMap.get("testCode");
        String testCode = String.join(" ", testCodeList);
        if ("$titleCH".equals(cellValue)) { // 中文标题
          cell.setCellValue(testCode + " " + exporter.titleCH());

        } else if ("$titleEN".equals(cellValue)) { // 英文标题
          cell.setCellValue(testCode + " " + exporter.titleEN());

        } else if ("$design".equals(cellValue)) { // 轮型
          List<String> designList = (List<String>) parameterMap.get("design");
          cell.setCellValue(designStr(designList));

        } else if ("$date".equals(cellValue)) { // 日期
          String beginDate = (String) parameterMap.get("beginDate");
          String endDate = (String) parameterMap.get("endDate");
          cell.setCellValue(beginDate + " to " + endDate);
        }
      }
    }
  }

  // 填充统计部分的数据
  private void fileStatData(Sheet sheet, int currentRow, int beginColumn, JSONObject data) {
    sheet.getRow(currentRow).getCell(beginColumn + 1).setCellValue(data.getString("castTotal"));
    sheet.getRow(currentRow).getCell(beginColumn + 3).setCellValue(data.getString("preInsp"));
    sheet.getRow(currentRow).getCell(beginColumn + 5).setCellValue(data.getString("preCast") + "%");
    sheet.getRow(currentRow).getCell(beginColumn + 7).setCellValue(data.getString("scrapSum"));
    sheet.getRow(currentRow).getCell(beginColumn + 9).setCellValue(data.getString("scrapPre") + "%");

    sheet.getRow(currentRow + 1).getCell(beginColumn + 1).setCellValue(data.getString("sconfSum"));
    sheet.getRow(currentRow + 1).getCell(beginColumn + 3).setCellValue(data.getString("sconfPre") + "%");
    sheet.getRow(currentRow + 1).getCell(beginColumn + 5).setCellValue(data.getString("toDock"));
    sheet.getRow(currentRow + 1).getCell(beginColumn + 7).setCellValue(data.getString("toDockPre") + "%");
    sheet.getRow(currentRow + 1).getCell(beginColumn + 9).setCellValue(data.getString("sconfDockAndSconf") + "%");
  }

  // 将轮型列表转成空格分割的字符串
  private String designStr(List<String> designList) {
    if (designList == null || designList.isEmpty()) {
      return "*";
    }
    String design = String.join(" ", designList);
    return design;
  }
}
