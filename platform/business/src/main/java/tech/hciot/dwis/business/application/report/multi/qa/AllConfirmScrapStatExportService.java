package tech.hciot.dwis.business.application.report.multi.qa;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import tech.hciot.dwis.business.application.report.MultiReportQAStatService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.AllConfirmScrap;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.AllConfirmScrapResult;

@Service
@Slf4j
public class AllConfirmScrapStatExportService {

  private static final String TEMPLATE_NAME = "3.9-all-confirm-scrap-stat.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.9 综合查询业务-质量统计-全部确认废品统计
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    AllConfirmScrapResult allConfirmScrapResult = multiReportQAStatService.allConfirmScrapStat(parameterMap);
    if (allConfirmScrapResult == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 日期
      DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
      String currentDate = format.format(new Date());
      sheet.getRow(1).getCell(0).setCellValue(currentDate);

      // 数据
      List<AllConfirmScrap> resultList = allConfirmScrapResult.getResultList();
      int currentRow = 4;
      for (int i = 0; i < resultList.size(); i++) {
        AllConfirmScrap result = resultList.get(i);
        ExcelUtil.copyCell(sheet, 3, 0,
          sheet, currentRow, 0,
          1, 4);
        sheet.getRow(currentRow).getCell(0).setCellValue(result.getScrapCode());
        sheet.getRow(currentRow).getCell(1).setCellValue(result.getScrap());
        sheet.getRow(currentRow).getCell(2).setCellValue(result.getScrapPct());
        sheet.getRow(currentRow).getCell(3).setCellValue(result.getConfirmedScrap());
        currentRow++;
      }

      AllConfirmScrap total = allConfirmScrapResult.getTotal();
      ExcelUtil.copyCell(sheet, 3, 0,
        sheet, currentRow, 0,
        1, 4);
      sheet.getRow(currentRow).getCell(0).setCellValue("合计：");
      sheet.getRow(currentRow).getCell(1).setCellValue(total.getScrap());
      sheet.getRow(currentRow).getCell(2).setCellValue(total.getScrapPct());
      sheet.getRow(currentRow).getCell(3).setCellValue(total.getConfirmedScrap());

      sheet.shiftRows(4, sheet.getLastRowNum(), -1);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
