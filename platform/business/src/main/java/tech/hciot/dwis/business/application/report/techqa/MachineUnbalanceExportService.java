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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.TechQAReportService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.MachineUnbalance;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.MachineUnbalanceDetail;

@Service
@Slf4j
public class MachineUnbalanceExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    List<MachineUnbalance> machineUnbalanceList = techQAReportService.machineUnbalance(parameterMap);
    if (machineUnbalanceList == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "9-machine-unbalance.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(parameterMap, sheet);

      int currentRow = 4; // 当前行号
      int currentNo = 1; // 当前行序号
      for (int i = 0; i < machineUnbalanceList.size(); i++) {
        MachineUnbalance machineUnbalance = machineUnbalanceList.get(i);
        List<MachineUnbalanceDetail> detailList = machineUnbalance.getDetail();
        int beginRow = currentRow;
        for (int j = 0; j < detailList.size(); j++) {
          // 拷贝空行
          ExcelUtil.copyCell(sheet, 3, 0,
            sheet, currentRow, 0,
            1, 8);
          sheet.getRow(currentRow).getCell(0).setCellValue(currentNo);
          sheet.getRow(currentRow).getCell(1).setCellValue(machineUnbalance.getWheelSerial());
          sheet.getRow(currentRow).getCell(2).setCellValue(detailList.get(j).getMachineNo());
          sheet.getRow(currentRow).getCell(3).setCellValue(detailList.get(j).getOperator());
          sheet.getRow(currentRow).getCell(4).setCellValue(detailList.get(j).getS2());
          sheet.getRow(currentRow).getCell(5).setCellValue(detailList.get(j).getS1());
          sheet.getRow(currentRow).getCell(6).setCellValue(detailList.get(j).getHoldCode());
          sheet.getRow(currentRow).getCell(7).setCellValue(detailList.get(j).getOpeDT());

          currentRow++;
        }
        if (beginRow != currentRow - 1) {
          CellRangeAddress noRegion = new CellRangeAddress(beginRow, currentRow - 1, 0, 0);
          sheet.addMergedRegion(noRegion);
          CellRangeAddress wheelSerialRegion = new CellRangeAddress(beginRow, currentRow - 1, 1, 1);
          sheet.addMergedRegion(wheelSerialRegion);
        }
        currentNo++;
      }
      sheet.shiftRows(4, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "machine-unbalance.xlsx";
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
