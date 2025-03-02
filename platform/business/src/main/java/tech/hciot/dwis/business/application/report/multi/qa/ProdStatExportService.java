package tech.hciot.dwis.business.application.report.multi.qa;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.report.MultiReportQAStatService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatByDay;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatReport;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatTotal;

@Service
@Slf4j
public class ProdStatExportService {

  private static final String TEMPLATE_NAME = "3.2-prod-stat.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.2 综合查询业务-质量统计-产量统计
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    ProdStatReport prodStat = multiReportQAStatService.prodStatReport(parameterMap);
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 日期
      String endDate = StandardTimeUtil.beforeDay((String) parameterMap.get("endDate"));
      sheet.getRow(2).getCell(0)
        .setCellValue(parameterMap.get("beginDate") + " to " + endDate);

      List<ProdStatByDay> dataList = prodStat.getDataList();
      int currentRow = 8;
      for (int i = 0; i < dataList.size(); i++) {

        ProdStatByDay data = dataList.get(i);
        if (data.getCastDate().equals("total")) {
          ExcelUtil.copyCell(sheet, 5, 0,
            sheet, currentRow, 0,
            1, 10);
        } else {
          ExcelUtil.copyCell(sheet, 4, 0,
            sheet, currentRow, 0,
            1, 10);
          sheet.getRow(currentRow).getCell(0).setCellValue(data.getCastDate());
        }
        sheet.getRow(currentRow).getCell(1).setCellValue(data.getWhlCast());
        sheet.getRow(currentRow).getCell(2).setCellValue(data.getTlInPit());
        sheet.getRow(currentRow).getCell(3).setCellValue(data.getTlHt());
        sheet.getRow(currentRow).getCell(4).setCellValue(data.getPreInsp());
        sheet.getRow(currentRow).getCell(5).setCellValue(data.getFinalInsp());
        sheet.getRow(currentRow).getCell(6).setCellValue(data.getMachine());
        sheet.getRow(currentRow).getCell(7).setCellValue(data.getToDock());
        sheet.getRow(currentRow).getCell(8).setCellValue(data.getFinScrap());
        sheet.getRow(currentRow).getCell(9).setCellValue(data.getShipped());
        currentRow++;
      }

      ExcelUtil.copyCell(sheet, 6, 0,
        sheet, currentRow, 0,
        2, 10);
      ProdStatTotal totalStat = prodStat.getTotalStat();
      sheet.getRow(currentRow).getCell(1).setCellValue(totalStat.getTotal());
      sheet.getRow(currentRow).getCell(3).setCellValue(totalStat.getFinishedTotal());
      sheet.getRow(currentRow).getCell(5).setCellValue(totalStat.getMachineTotal());
      sheet.getRow(currentRow).getCell(7).setCellValue(totalStat.getHeated());
      sheet.getRow(currentRow).getCell(9).setCellValue(totalStat.getUnheat());
      sheet.getRow(currentRow + 1).getCell(3).setCellValue(totalStat.getMachiningArht());
      sheet.getRow(currentRow + 1).getCell(5).setCellValue(totalStat.getUnmachineArht());
      sheet.getRow(currentRow + 1).getCell(7).setCellValue(totalStat.getXray());
      sheet.getRow(currentRow + 1).getCell(9).setCellValue(totalStat.getScrap());

      sheet.shiftRows(8, sheet.getLastRowNum(), -4);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
