package tech.hciot.dwis.business.application.report.techqa;

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
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.DecarbonChartData;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;

@Service
@Slf4j
public class DecarbonExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    List<DecarbonChartData> decarbonChartDataList = techQAReportService.decQueryRes(parameterMap);
    if (decarbonChartDataList == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "14-fur-decarbon.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet);

      for (int i = 0; i < decarbonChartDataList.size(); i++) {
        // 拷贝空行
        ExcelUtil.copyCell(sheet, 3, 0,
          sheet, 4 + i, 0,
          1, 4);

        sheet.getRow(4 + i).getCell(0).setCellValue(i + 1);
        sheet.getRow(4 + i).getCell(1).setCellValue(decarbonChartDataList.get(i).getHeatSeq());
        sheet.getRow(4 + i).getCell(2).setCellValue(getStringValue(decarbonChartDataList.get(i).getMeltedPure()));
        sheet.getRow(4 + i).getCell(3).setCellValue(getStringValue(decarbonChartDataList.get(i).getDecarbon()));
      }
      sheet.shiftRows(4, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "dec-query-res.xlsx";
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
        if ("$titleCH".equals(cellValue)) { // 中文标题
          cell.setCellValue("电炉溶清/脱碳查询表");

        } else if ("$date".equals(cellValue)) { // 日期
          String dateCellValue = "查询日期：" + parameterMap.get("beginDate") + " to " + parameterMap.get("endDate");
          if (parameterMap.get("pourLeaderId") != null) { dateCellValue += " 工长：" + parameterMap.get("pourLeaderId");}
          if (parameterMap.get("furnaceId") != null) { dateCellValue += " 炉长：" + parameterMap.get("furnaceId");}
          if (parameterMap.get("furNo") != null) { dateCellValue += " 炉号：" + parameterMap.get("furNo"); }
          cell.setCellValue(dateCellValue);
        }
      }
    }
  }
}
