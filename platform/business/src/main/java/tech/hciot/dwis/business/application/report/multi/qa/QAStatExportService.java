package tech.hciot.dwis.business.application.report.multi.qa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.MultiReportQAStatService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine.ReworkStatData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine.ReworkStatTotal;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine.StaffDetailData;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAData;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAStat;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.*;

@Service
@Slf4j
public class QAStatExportService {

  private static final String TEMPLATE_NAME = "3.1-qa-stat.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.1 综合查询业务-质量统计-质量统计
  public void export(Map<String, Object> parameterMap,
                     HttpServletResponse response) {
    QAStat qaStat = multiReportQAStatService.qaStat(parameterMap);
    JSON qaStatDetail = multiReportQAStatService.qaStatDetail(parameterMap);
    List<QAStat> qaStatChart = multiReportQAStatService.qaStatChart(parameterMap);
    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      generateReport(workbook.getSheetAt(0), qaStat, parameterMap);

      generateDetail(workbook.getSheetAt(1), ((JSONObject) qaStatDetail).getJSONArray("data"),
        parameterMap, workbook.getSheetAt(4));

      generateDetail2(workbook.getSheetAt(2), ((JSONObject) qaStatDetail).getJSONArray("data"),
        parameterMap, workbook.getSheetAt(4));

      generateDetailChat(workbook.getSheetAt(3), qaStatChart, parameterMap);

      //移除两个模板template
       workbook.removeSheetAt(3);
       workbook.removeSheetAt(3);
       workbook.setSheetName(3, "废品率统计");

      // 标题
      for (int i = 0; i < 4; i++) {
        List<String> designList = (List<String>) parameterMap.get("design");
        workbook.getSheetAt(i).getRow(2).getCell(0)
                .setCellValue(designStr(designList));
        workbook.getSheetAt(i).getRow(3).getCell(0)
                .setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));
      }

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 生成第一页质量报告
  private void generateReport(Sheet sheet, QAStat qaStat, Map<String, Object> parameterMap) {
    // 数据
    sheet.getRow(4).getCell(1).setCellValue(qaStat.getCastTotal());
    sheet.getRow(4).getCell(3).setCellValue(qaStat.getPreInsp());
    sheet.getRow(4).getCell(5).setCellValue(qaStat.getPreCast() + "%");
    sheet.getRow(4).getCell(7).setCellValue(qaStat.getScrapSum());
    sheet.getRow(4).getCell(9).setCellValue(qaStat.getScrapPre() + "%");

    sheet.getRow(5).getCell(1).setCellValue(qaStat.getSconfSum());
    sheet.getRow(5).getCell(3).setCellValue(qaStat.getSconfPre() + "%");
    sheet.getRow(5).getCell(5).setCellValue(qaStat.getToDock());
    sheet.getRow(5).getCell(7).setCellValue(qaStat.getToDockPre() + "%");
    sheet.getRow(5).getCell(9).setCellValue(qaStat.getToDockDockAndSconf() + "%");

    sheet.getRow(6).getCell(1).setCellValue(qaStat.getScrapDockAndSconf() + "%");
    sheet.getRow(6).getCell(3).setCellValue(qaStat.getSconfDockAndSconf() + "%");

    List<QAData> dataList = qaStat.getData();

    // 废码/预检
    for (int i = 0; i < dataList.size(); i++) {
      String detail = dataList.get(i).getDetail();
      sheet.getRow(9 + i / 5).getCell((i % 5) * 2).setCellValue(detail);
    }

    // 废码/(确废+成品)
    for (int i = 0; i < dataList.size(); i++) {
      String detail2 = dataList.get(i).getDetail2();
      sheet.getRow(15 + i / 5).getCell((i % 5) * 2).setCellValue(detail2);
    }
  }

  // 生成第二页详细报表
  private void generateDetail(Sheet sheet, JSONArray detailList, Map<String, Object> parameterMap, Sheet templateSheet) {
    int currentRow = 4;
    for (int i = 0; i < detailList.size(); i++) {
      JSONObject detail = detailList.getJSONObject(i);
      JSONArray dataArray = detail.getJSONArray("data");
      int dataSize = dataArray.size();
      int dataRowNum = dataSize == 0 ? 0 : (dataSize - 1) / 5 + 1;
      copyCell(templateSheet, 0, 0,
        sheet, currentRow, 0,
        2 + dataRowNum, 11);
      sheet.getRow(currentRow).getCell(0).setCellValue(detail.getString("subTitle"));
      sheet.getRow(currentRow).getCell(2).setCellValue(detail.getString("castTotal"));
      sheet.getRow(currentRow).getCell(4).setCellValue(detail.getString("preInsp"));
      sheet.getRow(currentRow).getCell(6).setCellValue(detail.getString("preCast") + "%");
      sheet.getRow(currentRow).getCell(8).setCellValue(detail.getString("scrapSum"));
      sheet.getRow(currentRow).getCell(10).setCellValue(detail.getString("scrapPre") + "%");

      sheet.getRow(currentRow + 1).getCell(2).setCellValue(detail.getString("sconfSum"));
      sheet.getRow(currentRow + 1).getCell(4).setCellValue(detail.getString("sconfPre") + "%");
      sheet.getRow(currentRow + 1).getCell(6).setCellValue(detail.getString("toDock"));
      sheet.getRow(currentRow + 1).getCell(8).setCellValue(detail.getString("toDockPre") + "%");
      sheet.getRow(currentRow + 1).getCell(10).setCellValue(detail.getString("toDockDockAndSconf") + "%");

      for (int j = 0; j < dataArray.size(); j++) {
        String data = dataArray.getJSONObject(j).getString("detail");
        sheet.getRow(currentRow + 2 + j / 5).getCell((j % 5) * 2 + 1).setCellValue(data);
      }
      CellRangeAddress region = new CellRangeAddress(currentRow, currentRow + dataRowNum + 1, 0, 0);
      sheet.addMergedRegion(region);
      currentRow += dataRowNum + 2;
    }
  }

  // 生成第三页详细报表2
  private void generateDetail2(Sheet sheet, JSONArray detailList, Map<String, Object> parameterMap, Sheet templateSheet) {
    int currentRow = 4;
    for (int i = 0; i < detailList.size(); i++) {
      JSONObject detail = detailList.getJSONObject(i);
      JSONArray dataArray = detail.getJSONArray("data");
      int dataSize = dataArray.size();
      int dataRowNum = dataSize == 0 ? 0 : (dataSize - 1) / 5 + 1;
      copyCell(templateSheet, 6, 0,
        sheet, currentRow, 0,
        2 + dataRowNum, 11);
      sheet.getRow(currentRow).getCell(0).setCellValue(detail.getString("subTitle"));
      sheet.getRow(currentRow).getCell(2).setCellValue(detail.getString("castTotal"));
      sheet.getRow(currentRow).getCell(4).setCellValue(detail.getString("preInsp"));
      sheet.getRow(currentRow).getCell(6).setCellValue(detail.getString("preCast") + "%");
      sheet.getRow(currentRow).getCell(8).setCellValue(detail.getString("scrapSum"));
      sheet.getRow(currentRow).getCell(10).setCellValue(detail.getString("scrapDockAndScrap") + "%");

      sheet.getRow(currentRow + 1).getCell(2).setCellValue(detail.getString("sconfSum"));
      sheet.getRow(currentRow + 1).getCell(4).setCellValue(detail.getString("sconfDockAndSconf") + "%");
      sheet.getRow(currentRow + 1).getCell(6).setCellValue(detail.getString("toDock"));
      sheet.getRow(currentRow + 1).getCell(8).setCellValue(detail.getString("toDockDockAndScrap") + "%");
      sheet.getRow(currentRow + 1).getCell(10).setCellValue(detail.getString("toDockDockAndSconf") + "%");

      for (int j = 0; j < dataArray.size(); j++) {
        String data = dataArray.getJSONObject(j).getString("detail3");
        sheet.getRow(currentRow + 2 + j / 5).getCell((j % 5) * 2 + 1).setCellValue(data);
      }
      CellRangeAddress region = new CellRangeAddress(currentRow, currentRow + dataRowNum + 1, 0, 0);
      sheet.addMergedRegion(region);
      currentRow += dataRowNum + 2;
    }
  }

  //生成第四页废品率统计
  private void generateDetailChat(Sheet sheet, List<QAStat> qaStatChart, Map<String, Object> parameterMap){

    Sheet sxssfSheet = getClonedSheet(sheet, 3);
    if (qaStatChart != null) {
      int currentRow = 5;
      Row tempRow = sheet.getRow(currentRow);

      for (int i = 0; i < qaStatChart.size(); i++) {
        QAStat qaStat = qaStatChart.get(i);
        Row r = sxssfSheet.createRow(currentRow++);
        setStyle(tempRow, r);
        r.getCell(0).setCellValue(getStringValue(qaStat.getProductDate()));
        r.getCell(1).setCellValue(getStringValue(qaStat.getSconfSum()));
        r.getCell(2).setCellValue(getStringValue(qaStat.getToStock()));
        r.getCell(3).setCellValue(getStringValue(qaStat.getSconfStockAndSconf()));
      }
    } else return;
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
