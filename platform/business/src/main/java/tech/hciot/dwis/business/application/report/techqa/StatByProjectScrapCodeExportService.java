package tech.hciot.dwis.business.application.report.techqa;

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
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAData;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAStat;

@Service
@Slf4j
public class StatByProjectScrapCodeExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    QAStat result = techQAReportService.statByProjectScrapCode(parameterMap);
    if (result == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "2.1-stat-by-project-scrapcode.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {
      Sheet sheet = workbook.getSheetAt(0);

      List<QAData> dataList = result.getData();

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet);

      sheet.getRow(4).getCell(1).setCellValue(result.getCastTotal());
      sheet.getRow(4).getCell(3).setCellValue(result.getPreInsp());

      int rowNum = 7;
      for (int i = 0; i < dataList.size(); i++) {
        QAData data = dataList.get(i);
        if (data.getScrapCode().equals("total")) {
          continue;
        }
        // 拷贝模板
        ExcelUtil.copyCell(sheet, 6, 0,
          sheet, rowNum, 0,
          1, 4);
        sheet.getRow(rowNum).getCell(0).setCellValue(data.getScrapCode());
        sheet.getRow(rowNum).getCell(1).setCellValue(data.getCnt());
        sheet.getRow(rowNum).getCell(2).setCellValue(data.getCntPre() + "%");
        sheet.getRow(rowNum).getCell(3).setCellValue(data.getSconfSum());
        rowNum++;
      }

      sheet.shiftRows(7, sheet.getLastRowNum(), -1);
      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "stat-by-project-scrapcode.xlsx";
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
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
        String cellValue = ExcelUtil.getCellStringValue(cell);
        if (cellValue == null || !cellValue.startsWith("$")) {
          continue;
        }
        String scrapCode = "*";
        if (parameterMap.get("scrapCode") != null) {
          List<String> scrapCodeList = (List<String>) parameterMap.get("scrapCode");
          scrapCode = String.join(" ", scrapCodeList);
        }
        if ("$titleCH".equals(cellValue)) { // 中文标题
          cell.setCellValue(scrapCode + " 废品代码");

        } else if ("$titleEN".equals(cellValue)) { // 英文标题
          cell.setCellValue(scrapCode + " Scrap Report");

        } else if ("$design".equals(cellValue)) { // 轮型
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
