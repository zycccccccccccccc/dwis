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
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.AngleUnbalance;

@Service
@Slf4j
public class AngleUnbalanceExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    List<AngleUnbalance> angleUnbalanceList = techQAReportService.angleUnbalance(parameterMap);
    if (angleUnbalanceList == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "8-angle-unbalance.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet);

      for (int i = 0; i < angleUnbalanceList.size(); i++) {
        // 拷贝空行
        ExcelUtil.copyCell(sheet, 3, 0,
          sheet, 4 + i, 0,
          1, 8);

        sheet.getRow(4 + i).getCell(0).setCellValue(i + 1);
        sheet.getRow(4 + i).getCell(1).setCellValue(angleUnbalanceList.get(i).getWheelSerial());
        sheet.getRow(4 + i).getCell(2).setCellValue(angleUnbalanceList.get(i).getAv());
        sheet.getRow(4 + i).getCell(3).setCellValue(angleUnbalanceList.get(i).getLine());
        sheet.getRow(4 + i).getCell(4).setCellValue(angleUnbalanceList.get(i).getBalanceV());
        sheet.getRow(4 + i).getCell(5).setCellValue(angleUnbalanceList.get(i).getBalanceA());
        sheet.getRow(4 + i).getCell(6).setCellValue(angleUnbalanceList.get(i).getOpeDT());
        sheet.getRow(4 + i).getCell(7).setCellValue(angleUnbalanceList.get(i).getHoldCode());
      }
      sheet.shiftRows(4, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "angle-unbalance.xlsx";
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
        if ("$date".equals(cellValue)) { // 日期
          cell.setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));
        }
      }
    }
  }
}
