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
public class Level2ExportService {

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
    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "level2-report.xlsx");
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
    int currentRow = 4;
    for (int i = 0; i < dataArray.size(); i++) {
      JSONObject data = dataArray.getJSONObject(i);

      ExcelUtil.copyCell(workbook.getSheetAt(1), 0, 0,
        workbook.getSheetAt(0), currentRow, 0,
        2, 12);
      currentRow += 2;
      JSONArray level2Data = data.getJSONArray("data");
      for (int j = 0; j < level2Data.size(); j++) {
        ExcelUtil.copyCell(workbook.getSheetAt(1), 2, 0,
          workbook.getSheetAt(0), currentRow, 0,
          6, 12);
        currentRow += 6;
      }
    }
  }

  // 拷贝数据部分内容
  private void copyData(Sheet sheet, JSONArray dataArray) {
    int currentRow = 4;
    for (int i = 0; i < dataArray.size(); i++) {
      int beginRow = currentRow;
      // 合计部分
      JSONObject data = dataArray.getJSONObject(i);
      sheet.getRow(currentRow).getCell(0).setCellValue(data.getString("minorTitle"));

      sheet.getRow(currentRow).getCell(3).setCellValue(data.getString("castTotal"));
      sheet.getRow(currentRow).getCell(5).setCellValue(data.getString("preInsp"));
      sheet.getRow(currentRow).getCell(7).setCellValue(data.getString("preCast") + "%");
      sheet.getRow(currentRow).getCell(9).setCellValue(data.getString("scrapSum"));
      sheet.getRow(currentRow).getCell(11).setCellValue(data.getString("scrapPre") + "%");
      currentRow++;

      sheet.getRow(currentRow).getCell(3).setCellValue(data.getString("sconfSum"));
      sheet.getRow(currentRow).getCell(5).setCellValue(data.getString("sconfPre") + "%");
      sheet.getRow(currentRow).getCell(7).setCellValue(data.getString("toDock"));
      sheet.getRow(currentRow).getCell(9).setCellValue(data.getString("toDockPre") + "%");
      sheet.getRow(currentRow).getCell(11).setCellValue(data.getString("sconfDockAndSconf") + "%");
      currentRow++;

      // 详情部分
      JSONArray leve2DataArray = data.getJSONArray("data");
      for (int j = 0; j < leve2DataArray.size(); j++) {
        JSONObject leve2Data = leve2DataArray.getJSONObject(j);
        sheet.getRow(currentRow).getCell(1).setCellValue(leve2Data.getString("subTitle"));

        sheet.getRow(currentRow).getCell(3).setCellValue(leve2Data.getString("castTotal"));
        sheet.getRow(currentRow).getCell(5).setCellValue(leve2Data.getString("preInsp"));
        sheet.getRow(currentRow).getCell(7).setCellValue(leve2Data.getString("preCast") + "%");
        sheet.getRow(currentRow).getCell(9).setCellValue(leve2Data.getString("scrapSum"));
        sheet.getRow(currentRow).getCell(11).setCellValue(leve2Data.getString("scrapPre") + "%");
        currentRow++;

        sheet.getRow(currentRow).getCell(3).setCellValue(leve2Data.getString("sconfSum"));
        sheet.getRow(currentRow).getCell(5).setCellValue(leve2Data.getString("sconfPre") + "%");
        sheet.getRow(currentRow).getCell(7).setCellValue(leve2Data.getString("toDock"));
        sheet.getRow(currentRow).getCell(9).setCellValue(leve2Data.getString("toDockPre") + "%");
        sheet.getRow(currentRow).getCell(11).setCellValue(leve2Data.getString("sconfDockAndSconf") + "%");
        currentRow++;

        JSONArray detailArray = leve2Data.getJSONArray("data");
        if (detailArray != null && !detailArray.isEmpty()) {
          List<JSONObject> detailList = detailArray.toJavaList(JSONObject.class);
          for (int k = 0; k < detailList.size(); k++) {
            int row = k / 5;
            int column = k - row * 5;
            sheet.getRow(currentRow + row).getCell(2 + column * 2)
              .setCellValue(detailList.get(k).getString("detail2"));
          }
        }
        currentRow += 4;
        log.info("leve2 currentRow: {}", currentRow);
      }

      if (beginRow != currentRow - 1) {
        CellRangeAddress region = new CellRangeAddress(beginRow, currentRow - 1, 0, 0);
        sheet.addMergedRegion(region);
      }
      log.info("level1 currentRow: {}", currentRow);
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
        if ("$titleCH".equals(cellValue)) { // 中文标题
          cell.setCellValue(exporter.titleCH());

        } else if ("$titleEN".equals(cellValue)) { // 英文标题
          cell.setCellValue(exporter.titleEN());

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

  // 将轮型列表转成空格分割的字符串
  private String designStr(List<String> designList) {
    if (designList == null || designList.isEmpty()) {
      return "*";
    }
    String design = String.join(" ", designList);
    return design;
  }
}
