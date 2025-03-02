package tech.hciot.dwis.business.application.report.techqa;

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
public class GraphiteScrapExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    JSONObject graphiteScrap = techQAReportService.graphiteScrap(parameterMap);
    if (graphiteScrap == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "3-graphite-scrap.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      fillData(workbook, graphiteScrap, parameterMap, "cope");
      fillData(workbook, graphiteScrap, parameterMap, "drag");
      workbook.removeSheetAt(2);
      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "graphite-scrap.xlsx";
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 填充数据
  private void fillData(Workbook workbook, JSONObject graphiteScrap, Map<String, Object> parameterMap, String type) {

    Sheet sheet = type.equals("cope") ? workbook.getSheet("上箱石墨号") : workbook.getSheet("下箱石墨号");

    JSONObject drag = graphiteScrap.getJSONObject(type);
    JSONArray dataArray = drag.getJSONArray("data");

    // 替换标题等宏参数
    replaceParameters(parameterMap, sheet);

    int rowNum = 4;
    for (int i = 0; i < dataArray.size(); i++) {
      // 拷贝模板
      ExcelUtil.copyCell(workbook.getSheetAt(2), 0, 0,
        sheet, 4 + i * 5, 0,
        5, 7);
      JSONObject data = dataArray.getJSONObject(i);
      sheet.getRow(rowNum).getCell(0).setCellValue(data.getString("subTitle"));
      sheet.getRow(rowNum).getCell(2).setCellValue(data.getString("castTotal"));
      sheet.getRow(rowNum).getCell(4).setCellValue(data.getString("preInsp"));
      sheet.getRow(rowNum).getCell(6).setCellValue(data.getString("scrapSum"));

      JSONArray detailArray = data.getJSONArray("data");
      for (int j = 0; j < detailArray.size(); j++) {
        JSONObject detail = detailArray.getJSONObject(j);
        sheet.getRow(rowNum + j / 3 + 1).getCell((j % 3) * 2 + 1).setCellValue(detail.getString("scrapCode"));
        sheet.getRow(rowNum + j / 3 + 1).getCell((j % 3) * 2 + 2).setCellValue(detail.getString("cnt"));
      }
      rowNum += 5;
    }
  }

  // 替换宏参数
  private void replaceParameters(Map<String, Object> parameterMap, Sheet sheet) {

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
        String design = "*";
        if (parameterMap.get("design") != null) {
          List<String> designList = (List<String>) parameterMap.get("design");
          design = String.join(" ", designList);
        }
        String cellValue = ExcelUtil.getCellStringValue(cell);
        if (cellValue == null || !cellValue.startsWith("$")) {
          continue;
        }
        if ("$design".equals(cellValue)) { // 轮型
          List<String> designList = (List<String>) parameterMap.get("design");
          cell.setCellValue(designStr(designList));

        } else if ("$date".equals(cellValue)) { // 日期
          cell.setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));
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
