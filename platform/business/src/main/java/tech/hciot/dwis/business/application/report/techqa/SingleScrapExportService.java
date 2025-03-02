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
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.SingleScrap;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.SingleScrapStat;

@Service
@Slf4j
public class SingleScrapExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    SingleScrapStat singleScrapStat = techQAReportService.singleScrapStat(parameterMap);
    if (singleScrapStat == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "5-single-scrap.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      fillData(parameterMap, singleScrapStat.getTotal(), workbook.getSheetAt(0), "废品查询报告单");
      fillData(parameterMap, singleScrapStat.getXh(), workbook.getSheetAt(1), "废品查询(分线)报告单");
      fillData(parameterMap, singleScrapStat.getModel(), workbook.getSheetAt(2), "废品查询(班次)报告单");

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "single-scrap.xlsx";
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 填充内容
  private void fillData(Map<String, Object> parameterMap, List<SingleScrap> singleScrapList, Sheet sheet, String titleCH) {
    // 替换标题等宏参数
    replaceParameters(parameterMap, sheet, titleCH);

    int currentRow = 6;
    int majorTitleBeginRow = 6;
    String majorTitle = singleScrapList.get(1).getMajorTitle();

    for (int i = 1; i < singleScrapList.size(); i++) {
      ExcelUtil.copyCell(sheet, 5, 0,
        sheet, currentRow, 0,
        1, 10);
      SingleScrap singleScrap = singleScrapList.get(i);
      if (!singleScrap.getMajorTitle().equals(majorTitle)) {
        if (majorTitleBeginRow != currentRow - 1) {
          CellRangeAddress region = new CellRangeAddress(majorTitleBeginRow, currentRow - 1, 0, 0);
          sheet.addMergedRegion(region);
        }
        majorTitleBeginRow = currentRow;
        majorTitle = singleScrap.getMajorTitle();
      }
      fileRow(sheet, currentRow, singleScrap);
      currentRow++;
    }
    if (majorTitleBeginRow != currentRow - 1) {
      CellRangeAddress region = new CellRangeAddress(majorTitleBeginRow, currentRow - 1, 0, 0);
      sheet.addMergedRegion(region);
    }

    sheet.shiftRows(6, sheet.getLastRowNum(), -1);
  }

  // 替换宏参数
  private void replaceParameters(Map<String, Object> parameterMap, Sheet sheet, String titleCH) {

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
        List<String> scrapCodeList = (List<String>) parameterMap.get("scrapCode");
        String scrapCode = String.join(" ", scrapCodeList);
        if ("$titleCH".equals(cellValue)) { // 中文标题
          cell.setCellValue(scrapCode + " " + titleCH);

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

  // 填充每行数据
  private void fileRow(Sheet sheet, int row, SingleScrap singleScrap) {
    sheet.getRow(row).getCell(0).setCellValue(singleScrap.getMajorTitle());
    sheet.getRow(row).getCell(1).setCellValue("".equals(singleScrap.getMinorTitle()) ? "合计" : singleScrap.getMinorTitle());
    sheet.getRow(row).getCell(2).setCellValue(singleScrap.getCastTotal());
    sheet.getRow(row).getCell(3).setCellValue(singleScrap.getPreInsp());
    sheet.getRow(row).getCell(4).setCellValue(singleScrap.getScrapSum());
    sheet.getRow(row).getCell(5).setCellValue(singleScrap.getScrapPre());
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
