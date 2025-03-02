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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.TechQAReportService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;

@Service
@Slf4j
public class Level1ExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response,
                     TechQAExporter exporter) {
    JSON result = techQAReportService.stat(parameterMap, exporter.sqlTemplate());
    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "level1-report.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet, exporter);

      JSONArray dataArray = ((JSONObject) result).getJSONArray("data");

      // 拷贝单元格模板
      for (int i = 1; i < dataArray.size(); i++) {
        ExcelUtil.copyCell(sheet, 4, 0,
          sheet, i * 6 + 4, 0,
          6, 11);
      }

      // 拷贝数据部分内容
      copyData(sheet, dataArray);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = exporter.sqlTemplate().substring(exporter.sqlTemplate().indexOf("-") + 1) + ".xlsx";
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
  // 拷贝数据部分内容
  private void copyData(Sheet sheet, JSONArray dataArray) {
    for (int i = 0; i < dataArray.size(); i++) {
      JSONObject data = (JSONObject) dataArray.get(i);
      sheet.getRow(i * 6 + 4).getCell(0).setCellValue(data.getString("subTitle"));

      sheet.getRow(i * 6 + 4).getCell(2).setCellValue(data.getString("castTotal"));
      sheet.getRow(i * 6 + 4).getCell(4).setCellValue(data.getString("preInsp"));
      sheet.getRow(i * 6 + 4).getCell(6).setCellValue(data.getString("preCast") + "%");
      sheet.getRow(i * 6 + 4).getCell(8).setCellValue(data.getString("scrapSum"));
      sheet.getRow(i * 6 + 4).getCell(10).setCellValue(data.getString("scrapPre") + "%");

      sheet.getRow(i * 6 + 5).getCell(2).setCellValue(data.getString("sconfSum"));
      sheet.getRow(i * 6 + 5).getCell(4).setCellValue(data.getString("sconfPre") + "%");
      sheet.getRow(i * 6 + 5).getCell(6).setCellValue(data.getString("toDock"));
      sheet.getRow(i * 6 + 5).getCell(8).setCellValue(data.getString("toDockPre") + "%");
      sheet.getRow(i * 6 + 5).getCell(10).setCellValue(data.getString("sconfDockAndSconf") + "%");

      JSONArray detailArray = data.getJSONArray("data");
      if (detailArray != null && !detailArray.isEmpty()) {
        List<JSONObject> detailList = detailArray.toJavaList(JSONObject.class);
        for (int j = 0; j < detailList.size(); j++) {
          int row = j / 5;
          int column = j - row * 5;
          sheet.getRow(i * 6 + 6 + row).getCell(1 + column * 2)
            .setCellValue(detailList.get(j).getString("detail2"));
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
