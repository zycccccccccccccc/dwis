package tech.hciot.dwis.business.application.report.multi.qa;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.MultiReportQAStatService;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.MachiningStat;

@Service
@Slf4j
public class MachiningStatExportService {

  private static final String TEMPLATE_NAME = "3.8-machining-stat.xlsx";

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  // 3.8 综合查询业务-质量统计-在制品统计
  public void export(HttpServletResponse response) {
    MachiningStat machiningStat = multiReportQAStatService.machiningStatResult();
    if (machiningStat == null) {
      return;
    }

    ClassPathResource resource = new ClassPathResource("static/report/multi-report/qa-stat/" + TEMPLATE_NAME);
    try (InputStream inputStream = resource.getInputStream();
         Workbook workbook = new XSSFWorkbook(inputStream); ) {

      generateMachiningStatSheet(machiningStat, workbook.getSheetAt(0));
      generateXMachiningStatSheet(machiningStat, workbook.getSheetAt(1));

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + TEMPLATE_NAME);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 生成在制品统计页
  private void generateMachiningStatSheet(MachiningStat machiningStat, Sheet sheet) {
    // 日期
    DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    String currentDate = format.format(new Date());
    sheet.getRow(1).getCell(0).setCellValue("报告生成日期：" + currentDate);

    // 数据
    JSONArray machiningStatList = machiningStat.getMachiningStat();

    // 根据轮型数量拷贝列
    Set<String> designSet = machiningStatList.getJSONObject(0).keySet();
    String[] designArray = designSet.toArray(new String[0]);
    int designSize = designArray.length - 2;
    for (int i = 0; i < designSize; i++) {
      ExcelUtil.copyCell(sheet, 0, 1,
        sheet, 0, i + 2,
        4, 1, false);
      sheet.getRow(2).getCell(i + 2).setCellValue(designArray[i + 2]);
    }
    CellRangeAddress region = new CellRangeAddress(0, 0, 0, designSize + 1);
    sheet.addMergedRegion(region);
    region = new CellRangeAddress(1, 1, 0, designSize + 1);
    sheet.addMergedRegion(region);

    // 数据部分
    int currentRow = 4;
    for (int i = 0; i < machiningStatList.size(); i++) {
      ExcelUtil.copyCell(sheet, 3, 0,
        sheet, currentRow, 0,
        1, designSize + 2);

      JSONObject result = machiningStatList.getJSONObject(i);

      sheet.getRow(currentRow).getCell(0).setCellValue(result.getString("castDate"));
      sheet.getRow(currentRow).getCell(1).setCellValue(result.getString("total"));
      int j = 2;
      for (Entry entry : result.entrySet()) {
        if (!entry.getKey().equals("castDate") && !entry.getKey().equals("total")) {
          sheet.getRow(currentRow).getCell(j).setCellValue(entry.getValue().toString());
          j++;
        }
      }
      currentRow++;
    }

    sheet.shiftRows(4, sheet.getLastRowNum(), -1);
  }

  // 生成X光在制品统计页
  private void generateXMachiningStatSheet(MachiningStat machiningStat, Sheet sheet) {
    // 日期
    DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    String currentDate = format.format(new Date());
    sheet.getRow(1).getCell(0).setCellValue("报告生成日期：" + currentDate);

    // 数据
    JSONObject xMachiningStat = machiningStat.getXMachiningStat();

    JSONArray resultList = xMachiningStat.getJSONArray("resultList");

    int currentRow = 4;
    for (int i = 0; i < resultList.size(); i++) {
      int beginRow = currentRow;
      JSONObject result = resultList.getJSONObject(i);
      String design = result.getString("design");
      ExcelUtil.copyCell(sheet, 3, 0,
        sheet, currentRow, 0,
        1, 5);
      sheet.getRow(currentRow).getCell(0).setCellValue(design);
      sheet.getRow(currentRow).getCell(1).setCellValue("总计");
      sheet.getRow(currentRow).getCell(2).setCellValue(result.getString("cnt"));
      sheet.getRow(currentRow).getCell(3).setCellValue(result.getString("machining"));
      sheet.getRow(currentRow).getCell(4).setCellValue(result.getString("scrap"));
      currentRow++;

      JSONArray dataList = result.getJSONArray("data");
      for (int j = 0; j < dataList.size(); j++) {
        ExcelUtil.copyCell(sheet, 3, 0,
          sheet, currentRow, 0,
          1, 5);
        JSONObject data = dataList.getJSONObject(j);
        sheet.getRow(currentRow).getCell(1).setCellValue(data.getString("castDate"));
        sheet.getRow(currentRow).getCell(2).setCellValue(data.getString("cnt"));
        sheet.getRow(currentRow).getCell(3).setCellValue(data.getString("machining"));
        sheet.getRow(currentRow).getCell(4).setCellValue(data.getString("scrap"));
        currentRow++;
      }
      if (beginRow != currentRow - 1) {
        CellRangeAddress region = new CellRangeAddress(beginRow, currentRow - 1, 0, 0);
        sheet.addMergedRegion(region);
      }
    }

    JSONObject total = xMachiningStat.getJSONObject("total");
    ExcelUtil.copyCell(sheet, 3, 0,
      sheet, currentRow, 0,
      1, 5);
    sheet.getRow(currentRow).getCell(0).setCellValue("合计");
    sheet.getRow(currentRow).getCell(2).setCellValue(total.getString("cnt"));
    sheet.getRow(currentRow).getCell(3).setCellValue(total.getString("machining"));
    sheet.getRow(currentRow).getCell(4).setCellValue(total.getString("scrap"));
    CellRangeAddress region = new CellRangeAddress(currentRow, currentRow, 0, 1);
    sheet.addMergedRegion(region);

    sheet.shiftRows(4, sheet.getLastRowNum(), -1);
  }
}
