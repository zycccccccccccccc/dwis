package tech.hciot.dwis.business.application.report.techqa;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.TechQAReportService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.MoldAgeScrap;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.VibrateWheel;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class MoldAgeScrapExportService {

  @Autowired
  private TechQAReportService techQAReportService;

  @Autowired
  private ReportAssembler assembler;

  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    MoldAgeScrap moldAgeScrap = techQAReportService.moldAgeScrap(parameterMap);
    if (moldAgeScrap == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/tech-qa/" + "11-mold-age-scrap.xlsx");
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      genMoldAgeScrapReport("cope", moldAgeScrap, parameterMap, workbook);
      genMoldAgeScrapReport("drag", moldAgeScrap, parameterMap, workbook);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      String exportFileName = "mold_age_scrap.xlsx";
      response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 生成石墨模龄废品报告
  private void genMoldAgeScrapReport(String type, MoldAgeScrap moldAgeScrap, Map<String, Object> parameterMap,  Workbook workbook) {

    JSONArray resList = null;
    JSONObject resTotal = null;
    List<JSONObject> totalList = null;
    Sheet sheet = workbook.getSheetAt(0);

    if (Objects.equals(type, "cope")) {
      sheet.getRow(0).getCell(0).setCellValue("上箱石墨模龄废品统计");
      sheet.getRow(1).getCell(0)
              .setCellValue("射砂日期：" + parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));
      resList = moldAgeScrap.getCopeResult().getJSONArray("resultList");
      totalList = moldAgeScrap.getCopeTotalList();
      resTotal = moldAgeScrap.getCopeResult().getJSONObject("total");
    } else if (Objects.equals(type, "drag")) {
      sheet = workbook.getSheetAt(1);
      sheet.getRow(0).getCell(0).setCellValue("下箱石墨模龄废品统计");
      sheet.getRow(1).getCell(0)
              .setCellValue("射砂日期：" + parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));
      resList = moldAgeScrap.getDragResult().getJSONArray("resultList");
      totalList = moldAgeScrap.getDragTotalList();
      resTotal = moldAgeScrap.getDragResult().getJSONObject("total");
    }

    int currentRow = 5;
    if (resList != null && resList.size() > 0) {
      for (int i = 0; i < resList.size(); i++) {
        int beginRow = currentRow;
        JSONObject result = resList.getJSONObject(i);
        JSONArray dataList = result.getJSONArray("data");
        for (int j = 0; j < dataList.size(); j++) {
          JSONObject subData = dataList.getJSONObject(j);
          fillRow(sheet, 4, currentRow, subData);
          currentRow++;
          JSONArray subDataList = subData.getJSONArray("data");
          for (int k = 0; k < subDataList.size(); k++) {
            fillRow(sheet, 3, currentRow, subDataList.getJSONObject(k));
            currentRow++;
          }
        }
        fillRow(sheet, 4, currentRow, result);
        sheet.getRow(currentRow).getCell(1).setCellValue("小计");

        CellRangeAddress regionDesign = new CellRangeAddress(beginRow, currentRow, 0, 0);
        sheet.addMergedRegion(regionDesign);
        currentRow++;
      }
      for (int t = 0; t < totalList.size(); t++) {
        fillRow(sheet, 3, currentRow, totalList.get(t));
        sheet.getRow(currentRow).getCell(0).setCellValue("轮型/日期");
        sheet.getRow(currentRow).getCell(0).setCellStyle(sheet.getRow(4).getCell(1).getCellStyle());
        currentRow++;
      }
      CellRangeAddress regionMoldAge = new CellRangeAddress(currentRow - totalList.size(),  currentRow - 1, 0, 0);
      sheet.addMergedRegion(regionMoldAge);

      fillRow(sheet, 4, currentRow, resTotal);
      CellRangeAddress regionTotal = new CellRangeAddress(currentRow, currentRow, 0, 1);
      sheet.addMergedRegion(regionTotal);
      sheet.getRow(currentRow).getCell(0).setCellValue("总计");

      sheet.shiftRows(5, sheet.getLastRowNum(), -2);
    }
  }

  //模龄数字转换
  private String transMoldAge(String type) {
    String res = null;
    switch (type) {
      case "1":
        res = "<20";
        break;
      case "2":
        res = "[20, 40)";
        break;
      case "3":
        res = "[40, 60)";
        break;
      case "4":
        res = "[60, 90]";
        break;
      case "5":
        res = ">90";
        break;
    }
    return res;
  }

  private void fillRow(Sheet sheet, int templateRow, int currentRow, JSONObject data) {
    // 拷贝空行
    ExcelUtil.copyCell(sheet, templateRow, 0,
            sheet, currentRow, 0,
            1, 10);
    int column = 0;
    Row row = sheet.getRow(currentRow);
    row.getCell(column++).setCellValue(data.getString("design"));
    row.getCell(column++).setCellValue(data.getString("moldAge").equals("total") ?
            data.getString("jetDate") : transMoldAge(data.getString("moldAge")));
    row.getCell(column++).setCellValue(data.getString("wheelSum"));
    row.getCell(column++).setCellValue(data.getString("scrapSum") + "-" + data.getString("scrapRate"));
    row.getCell(column++).setCellValue(data.getString("statScrapSum") + "-" + data.getString("statScrapRate"));
    row.getCell(column++).setCellValue(data.getString("scrap9AS") + "-" + data.getString("scrap9ASRate"));
    row.getCell(column++).setCellValue(data.getString("scrap9B") + "-" + data.getString("scrap9BRate"));
    row.getCell(column++).setCellValue(data.getString("scrap9CS") + "-" + data.getString("scrap9CSRate"));
    row.getCell(column++).setCellValue(data.getString("scrap88S") + "-" + data.getString("scrap88SRate"));
    row.getCell(column++).setCellValue(data.getString("scrap23S") + "-" + data.getString("scrap23SRate"));
  }
}
