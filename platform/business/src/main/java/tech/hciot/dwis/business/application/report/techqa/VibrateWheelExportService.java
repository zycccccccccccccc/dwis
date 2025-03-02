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
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.VibrateWheel;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VibrateWheelExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    List<VibrateWheel> vibrateWheelList = techQAReportService.vibrateWheels(parameterMap);
    if (vibrateWheelList == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "12-vibrate-wheel.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet);

      for (int i = 0; i < vibrateWheelList.size(); i++) {
        // 拷贝空行
        ExcelUtil.copyCell(sheet, 3, 0,
          sheet, 4 + i, 0,
          1, 4);

        sheet.getRow(4 + i).getCell(0).setCellValue(i + 1);
        sheet.getRow(4 + i).getCell(1).setCellValue(vibrateWheelList.get(i).getCopeNo());
        sheet.getRow(4 + i).getCell(2).setCellValue(vibrateWheelList.get(i).getVibrateSum());
        sheet.getRow(4 + i).getCell(3).setCellValue(vibrateWheelList.get(i).getOffSum());
      }
      sheet.shiftRows(4, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "vibrate-wheel.xlsx";
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
          cell.setCellValue("振轮/“脱裤子”次数统计");

        } else if ("$date".equals(cellValue)) { // 日期
          cell.setCellValue("开箱日期：" + parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));
        }
      }
    }
  }
}
