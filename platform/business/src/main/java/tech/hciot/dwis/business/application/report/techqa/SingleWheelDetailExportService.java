package tech.hciot.dwis.business.application.report.techqa;

import java.io.InputStream;
import java.util.List;
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
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.report.TechQAReportService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.SingleWheelDetail;

@Service
@Slf4j
public class SingleWheelDetailExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(String beginDate,
                     HttpServletResponse response) {
    List<SingleWheelDetail> wheelDetailList = techQAReportService.singleWheelDetail(beginDate);
    if (wheelDetailList == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "1.13-single-wheel-detail.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 替换标题等宏参数
      replaceParameters(beginDate, sheet);

      for (int i = 0; i < wheelDetailList.size(); i++) {
        // 拷贝空行
        ExcelUtil.copyCell(sheet, 3, 0,
          sheet, 4 + i, 0,
          1, 14);

        sheet.getRow(4 + i).getCell(0).setCellValue(wheelDetailList.get(i).getHeatRecordKey());
        sheet.getRow(4 + i).getCell(1).setCellValue(wheelDetailList.get(i).getTapSeq());
        sheet.getRow(4 + i).getCell(2).setCellValue(wheelDetailList.get(i).getLadleSeq());
        sheet.getRow(4 + i).getCell(3).setCellValue(wheelDetailList.get(i).getWheelSerial());
        sheet.getRow(4 + i).getCell(4).setCellValue(wheelDetailList.get(i).getOpenTimeAct());
        sheet.getRow(4 + i).getCell(5).setCellValue(wheelDetailList.get(i).getScrapCode());
        sheet.getRow(4 + i).getCell(6).setCellValue(wheelDetailList.get(i).getConfirmedScrap());
        sheet.getRow(4 + i).getCell(7).setCellValue(wheelDetailList.get(i).getScrapDate());
        sheet.getRow(4 + i).getCell(8).setCellValue(wheelDetailList.get(i).getPre());
        sheet.getRow(4 + i).getCell(9).setCellValue(wheelDetailList.get(i).getFinalCount());
        sheet.getRow(4 + i).getCell(10).setCellValue(wheelDetailList.get(i).getUltra());
        sheet.getRow(4 + i).getCell(11).setCellValue(wheelDetailList.get(i).getBalance());
        sheet.getRow(4 + i).getCell(12).setCellValue(wheelDetailList.get(i).getFinished());
        sheet.getRow(4 + i).getCell(13).setCellValue(wheelDetailList.get(i).getMecSerial());
      }
      sheet.shiftRows(4, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "single-wheel-detail.xlsx";
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 替换宏参数
  private void replaceParameters(String beginDate, Sheet sheet) {

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
        String slashDate = StandardTimeUtil.slashDate(beginDate);
        if ("$titleCH".equals(cellValue)) { // 中文标题
          cell.setCellValue(slashDate + " 单日轮号明细");

        } else if ("$titleEN".equals(cellValue)) { // 英文标题
          cell.setCellValue(slashDate + " Wheel List");
        }
      }
    }
  }
}
