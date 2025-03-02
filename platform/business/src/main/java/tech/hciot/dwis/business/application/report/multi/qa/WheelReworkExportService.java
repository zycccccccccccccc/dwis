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
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelRework;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelReworkResult;

@Service
@Slf4j
public class WheelReworkExportService {

  private static final String TEMPLATE_NAME = "3.3-wheel-rework.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.3 综合查询业务-质量统计-车轮返工
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    WheelReworkResult wheelReworkResult = multiReportQAStatService.wheelRework(parameterMap);
    if (wheelReworkResult == null) {
      return;
    }
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 日期
      String endDate = StandardTimeUtil.beforeDay((String) parameterMap.get("endDate"));
      sheet.getRow(1).getCell(0)
        .setCellValue(parameterMap.get("beginDate") + " to " + endDate);

      List<WheelRework> resultList = wheelReworkResult.getResultList();
      int currentRow = 13;
      for (int i = 0; i < resultList.size(); i++) {

        WheelRework data = resultList.get(i);
        ExcelUtil.copyCell(sheet, 7, 0,
          sheet, currentRow, 0,
          6, 9);

        sheet.getRow(currentRow).getCell(0).setCellValue(data.getCastDate());

        sheet.getRow(currentRow + 1).getCell(1).setCellValue(data.getCastTotal());
        sheet.getRow(currentRow + 1).getCell(2).setCellValue(data.getPre1());
        sheet.getRow(currentRow + 1).getCell(3).setCellValue(data.getPre2());
        sheet.getRow(currentRow + 1).getCell(4).setCellValue(data.getPre3());
        sheet.getRow(currentRow + 1).getCell(5).setCellValue(data.getPre4());
        sheet.getRow(currentRow + 1).getCell(6).setCellValue(data.getFin1());
        sheet.getRow(currentRow + 1).getCell(7).setCellValue(data.getFin2());
        sheet.getRow(currentRow + 1).getCell(8).setCellValue(data.getFin3());

        sheet.getRow(currentRow + 3).getCell(1).setCellValue(data.getPreInsp());
        sheet.getRow(currentRow + 3).getCell(2).setCellValue(data.getPrew());
        sheet.getRow(currentRow + 3).getCell(3).setCellValue(data.getP4());
        sheet.getRow(currentRow + 3).getCell(4).setCellValue(data.getP5());
        sheet.getRow(currentRow + 3).getCell(5).setCellValue(data.getP6());
        sheet.getRow(currentRow + 3).getCell(6).setCellValue(data.getP7());
        sheet.getRow(currentRow + 3).getCell(7).setCellValue(data.getPh());
        sheet.getRow(currentRow + 3).getCell(8).setCellValue(data.getPoth());

        sheet.getRow(currentRow + 5).getCell(1).setCellValue(data.getGood1());
        sheet.getRow(currentRow + 5).getCell(2).setCellValue(data.getFrew());
        sheet.getRow(currentRow + 5).getCell(3).setCellValue(data.getF4());
        sheet.getRow(currentRow + 5).getCell(4).setCellValue(data.getF5());
        sheet.getRow(currentRow + 5).getCell(5).setCellValue(data.getF6());
        sheet.getRow(currentRow + 5).getCell(6).setCellValue(data.getF7());
        sheet.getRow(currentRow + 5).getCell(7).setCellValue(data.getFh());
        sheet.getRow(currentRow + 5).getCell(8).setCellValue(data.getFoth());
        currentRow += 6;
      }

      WheelRework total = wheelReworkResult.getTotal();
      sheet.getRow(5).getCell(0).setCellValue(total.getCastTotal());

      sheet.getRow(3).getCell(1).setCellValue(total.getPreInsp());
      sheet.getRow(3).getCell(2).setCellValue(total.getPrew());
      sheet.getRow(3).getCell(3).setCellValue(total.getP4());
      sheet.getRow(3).getCell(4).setCellValue(total.getP5());
      sheet.getRow(3).getCell(5).setCellValue(total.getP6());
      sheet.getRow(3).getCell(6).setCellValue(total.getP7());
      sheet.getRow(3).getCell(7).setCellValue(total.getPh());
      sheet.getRow(3).getCell(8).setCellValue(total.getPoth());

      sheet.getRow(5).getCell(1).setCellValue(total.getGood1());
      sheet.getRow(5).getCell(2).setCellValue(total.getFrew());
      sheet.getRow(5).getCell(3).setCellValue(total.getF4());
      sheet.getRow(5).getCell(4).setCellValue(total.getF5());
      sheet.getRow(5).getCell(5).setCellValue(total.getF6());
      sheet.getRow(5).getCell(6).setCellValue(total.getF7());
      sheet.getRow(5).getCell(7).setCellValue(total.getFh());
      sheet.getRow(5).getCell(8).setCellValue(total.getFoth());

      sheet.shiftRows(13, sheet.getLastRowNum(), -6);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
