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
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.MachineScrap;

@Service
@Slf4j
public class MachineScrapExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    List<MachineScrap> machineScrapList = techQAReportService.machineScrap(parameterMap);
    if (machineScrapList == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "6-machine-scrap.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet);

      for (int i = 0; i < machineScrapList.size(); i++) {
        // 拷贝空行
        ExcelUtil.copyCell(sheet, 4, 0,
          sheet, 5 + i, 0,
          1, 9);

        sheet.getRow(5 + i).getCell(0).setCellValue(i + 1);
        sheet.getRow(5 + i).getCell(1).setCellValue(machineScrapList.get(i).getWheelSerial());
        sheet.getRow(5 + i).getCell(2).setCellValue(machineScrapList.get(i).getOpeDT());
        sheet.getRow(5 + i).getCell(3).setCellValue(machineScrapList.get(i).getS2());
        sheet.getRow(5 + i).getCell(4).setCellValue(machineScrapList.get(i).getMachineNo());
        sheet.getRow(5 + i).getCell(5).setCellValue(machineScrapList.get(i).getOperator());
        sheet.getRow(5 + i).getCell(6).setCellValue(machineScrapList.get(i).getScrapCode());
        sheet.getRow(5 + i).getCell(7).setCellValue(machineScrapList.get(i).getConfirmedScrap() == 1 ? "√" : "");
        sheet.getRow(5 + i).getCell(8).setCellValue(machineScrapList.get(i).getFinished() == 1 ? "√" : "");
      }
      sheet.shiftRows(5, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "machine-scrap.xlsx";
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
        List<String> reworkCodeList = (List<String>) parameterMap.get("reworkCode");
        String reworkCode = String.join(" ", reworkCodeList);
        if ("$titleCH".equals(cellValue)) { // 中文标题
          cell.setCellValue(reworkCode + " 机加返工代码查废品统计");

        } else if ("$titleEN".equals(cellValue)) { // 英文标题
          cell.setCellValue(reworkCode + " Scrap Report");

        } else if ("$date".equals(cellValue)) { // 日期
          cell.setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));
        }
      }
    }
  }
}
