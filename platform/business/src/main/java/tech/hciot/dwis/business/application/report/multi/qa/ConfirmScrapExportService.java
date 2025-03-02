package tech.hciot.dwis.business.application.report.multi.qa;

import com.alibaba.fastjson.JSONObject;
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
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ConfirmScrapResult;

@Service
@Slf4j
public class ConfirmScrapExportService {

  private static final String TEMPLATE_NAME = "3.6-confirm-scrap.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.6 综合查询业务-质量统计-确认废品与交验
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    ConfirmScrapResult confirmScrapResult = multiReportQAStatService.confirmScrap(parameterMap, true);
    if (confirmScrapResult == null) {
      return;
    }
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      Sheet sheet = workbook.getSheetAt(0);

      // 日期
      //String endDate = StandardTimeUtil.beforeDay((String) parameterMap.get("endDate"));
      sheet.getRow(1).getCell(0)
        .setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));

      List<JSONObject> resultList = confirmScrapResult.getResultList();
      int currentRow = 15;
      for (int i = 0; i < resultList.size(); i++) {

        JSONObject data = resultList.get(i);
        ExcelUtil.copyCell(sheet, 9, 0,
          sheet, currentRow, 0,
          6, 9);

        sheet.getRow(currentRow).getCell(0).setCellValue(data.getString("scrapDate"));

        sheet.getRow(currentRow + 1).getCell(1).setCellValue(data.getString("ss"));
        sheet.getRow(currentRow + 1).getCell(2).setCellValue(data.getString("scrap"));
        sheet.getRow(currentRow + 1).getCell(3).setCellValue(data.getString("stock"));
        sheet.getRow(currentRow + 1).getCell(4).setCellValue(data.getString("scrap67rs"));
        sheet.getRow(currentRow + 1).getCell(5).setCellValue(data.getString("scrap23s"));
        sheet.getRow(currentRow + 1).getCell(6).setCellValue(data.getString("scrap41"));
        sheet.getRow(currentRow + 1).getCell(7).setCellValue(data.getString("scrap9b"));
        sheet.getRow(currentRow + 1).getCell(8).setCellValue(data.getString("scrap44as"));

        sheet.getRow(currentRow + 3).getCell(1).setCellValue(data.getString("scrap56"));
        sheet.getRow(currentRow + 3).getCell(2).setCellValue(data.getString("scrap58s"));
        sheet.getRow(currentRow + 3).getCell(3).setCellValue(data.getString("scrap88s"));
        sheet.getRow(currentRow + 3).getCell(4).setCellValue(data.getString("scrap9as"));
        sheet.getRow(currentRow + 3).getCell(5).setCellValue(data.getString("scrap9cs"));
        sheet.getRow(currentRow + 3).getCell(6).setCellValue(data.getString("scrap67fs"));
        sheet.getRow(currentRow + 3).getCell(7).setCellValue(data.getString("scrap69"));
        sheet.getRow(currentRow + 3).getCell(8).setCellValue(data.getString("scrap77"));

        sheet.getRow(currentRow + 5).getCell(1).setCellValue(data.getString("scrap66"));
        sheet.getRow(currentRow + 5).getCell(2).setCellValue(data.getString("scrap12"));
        sheet.getRow(currentRow + 5).getCell(3).setCellValue(data.getString("scrap2as"));
        sheet.getRow(currentRow + 5).getCell(4).setCellValue(data.getString("scrap8cs"));
        sheet.getRow(currentRow + 5).getCell(5).setCellValue(data.getString("scrap42"));
        sheet.getRow(currentRow + 5).getCell(6).setCellValue(data.getString("scrap7"));
        sheet.getRow(currentRow + 5).getCell(7).setCellValue(data.getString("scrap30"));
        sheet.getRow(currentRow + 5).getCell(8).setCellValue(data.getString("scrap65b"));
        currentRow += 6;
      }

      JSONObject total = confirmScrapResult.getTotal();

      sheet.getRow(3).getCell(1).setCellValue(total.getString("ss"));
      sheet.getRow(3).getCell(2).setCellValue(total.getString("scrap"));
      sheet.getRow(3).getCell(3).setCellValue(total.getString("stock"));
      sheet.getRow(3).getCell(4).setCellValue(total.getString("scrap67rs"));
      sheet.getRow(3).getCell(5).setCellValue(total.getString("scrap23s"));
      sheet.getRow(3).getCell(6).setCellValue(total.getString("scrap41"));
      sheet.getRow(3).getCell(7).setCellValue(total.getString("scrap9b"));
      sheet.getRow(3).getCell(8).setCellValue(total.getString("scrap44as"));

      sheet.getRow(5).getCell(1).setCellValue(total.getString("scrap56"));
      sheet.getRow(5).getCell(2).setCellValue(total.getString("scrap58s"));
      sheet.getRow(5).getCell(3).setCellValue(total.getString("scrap88s"));
      sheet.getRow(5).getCell(4).setCellValue(total.getString("scrap9as"));
      sheet.getRow(5).getCell(5).setCellValue(total.getString("scrap9cs"));
      sheet.getRow(5).getCell(6).setCellValue(total.getString("scrap67fs"));
      sheet.getRow(5).getCell(7).setCellValue(total.getString("scrap69"));
      sheet.getRow(5).getCell(8).setCellValue(total.getString("scrap77"));

      sheet.getRow(7).getCell(1).setCellValue(total.getString("scrap66"));
      sheet.getRow(7).getCell(2).setCellValue(total.getString("scrap12"));
      sheet.getRow(7).getCell(3).setCellValue(total.getString("scrap2as"));
      sheet.getRow(7).getCell(4).setCellValue(total.getString("scrap8cs"));
      sheet.getRow(7).getCell(5).setCellValue(total.getString("scrap42"));
      sheet.getRow(7).getCell(6).setCellValue(total.getString("scrap7"));
      sheet.getRow(7).getCell(7).setCellValue(total.getString("scrap30"));
      sheet.getRow(7).getCell(8).setCellValue(total.getString("scrap65b"));

      sheet.shiftRows(15, sheet.getLastRowNum(), -6);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
