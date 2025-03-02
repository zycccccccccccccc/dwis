package tech.hciot.dwis.business.application;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.interfaces.dto.WheelStockData;

@Service
@Slf4j
public class StockWheelListExportService {

  // 可供发运车轮单相关的excel参数
  private static final int TOTAL_COLUMN_SUM = 3; // 发运车轮单总列数

  private static final int TOTAL_ROW_SUM = 50; // 发运车轮单总行数
  private static final int CONTENT_START_ROW = 3; // 发运车轮单内容部分第一行行号
  private static final int CONTENT_ROW_SUM = 46; // 发运车轮单内容部分总行数

  public void exportStock(WheelStockData stockData, HttpServletResponse response) {
    ClassPathResource resource = new ClassPathResource("static/stock-wheellist.xlsx");
    try (InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream); ) {
      generateWorkSheet(workbook, stockData);
      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=stock-wheellist.xlsx");
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void generateWorkSheet(Workbook workbook, WheelStockData stockData) {
//    stockData = testData(stockData); // TODO 测试数据
    List<String> excelTextList = generateExcelTextList(stockData.getWheels());
    int pageSum = (stockData.getWheels().size() - 1) / 4 / CONTENT_ROW_SUM + 1; // 总页数
    try {
      Sheet sheet = workbook.getSheetAt(0);
      String pros = "轴孔：" + stockData.getBoreSize() + " 带尺：" + stockData.getTapeSize() + " 轮辋宽：" + stockData.getWheelW()
                    + " 平衡标识：" + stockData.getBalanceFlag();

      for (int page = 0; page < pageSum; page ++) {

        // 标题
        sheet.getRow(TOTAL_ROW_SUM * page).getCell(0).setCellValue(stockData.getDesign() + " 可供发运车轮单");

        //属性
        sheet.getRow(TOTAL_ROW_SUM * page + 1).getCell(0).setCellValue(pros);

        // 编号
        sheet.getRow(TOTAL_ROW_SUM * page + 2).getCell(0).setCellValue("编号：" + stockData.getIndex());

        // 数量
        sheet.getRow(TOTAL_ROW_SUM * page + 2).getCell(1).setCellValue("数量：" + stockData.getAmount());

        // 右上角日期
        sheet.getRow(TOTAL_ROW_SUM * page + 2).getCell(2).setCellValue(stockDate(stockData.getDate()));

        // 左下角日期时间
        sheet.getRow(TOTAL_ROW_SUM * page + TOTAL_ROW_SUM - 1).getCell(0).setCellValue(currentDateTime());

        // 共x页，第x页
        Cell totalCell = sheet.getRow(TOTAL_ROW_SUM * page + TOTAL_ROW_SUM - 1).getCell(2);
        totalCell.setCellValue("共" + pageSum + "页，第" + (page + 1) + "页");

        int lineSum = Math.min(excelTextList.size() - CONTENT_ROW_SUM * page, CONTENT_ROW_SUM); // 当前页内容部分行数
        for (int line = 0; line < lineSum; line ++) {
          Row row = sheet.getRow(TOTAL_ROW_SUM * page + CONTENT_START_ROW + line);
          Cell cell = row.getCell(0);
          cell.setCellValue(excelTextList.get(CONTENT_ROW_SUM * page + line));
        }
      }

      // 删除模板中没用的内容
      for (int rowNum = sheet.getLastRowNum();
           rowNum >= TOTAL_ROW_SUM * pageSum;
           rowNum --) {
        sheet.removeRow(sheet.getRow(rowNum));
      }
      sheet.protectSheet("dwis");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private WheelStockData testData(WheelStockData stockData) {
    List<String> wheelList = new ArrayList<>();
    for (int i = 0; i < 5; i ++) {
      for (int j = 0; j < 50; j ++) {
        wheelList.add("210800" + i + String.format("%03d", j));
      }
      stockData = WheelStockData.builder()
        .date(stockData.getDate())
        .design("CJ33")
        .amount(wheelList.size())
        .index(stockData.getIndex())
        .wheels(wheelList)
        .build();
    }
    return stockData;
  }

  // 生成excel最终要展示的产品编号数据
  private List<String> generateExcelTextList(List<String> wheelList) {
    List<String> excelTextList = new ArrayList<>();
    int pageSum = (wheelList.size() - 1) / 4 / CONTENT_ROW_SUM + 1; // 总页数
    for (int page = 0; page < pageSum; page++) {
      for (int line = 0; line < CONTENT_ROW_SUM; line++) {
        String excelText = "";
        for(int i = 0; i < 4; i ++) {
          int recordIndex = CONTENT_ROW_SUM * 4 * page + CONTENT_ROW_SUM * i + line;
          if (recordIndex >= wheelList.size()) {
            continue;
          }
          excelText = excelText + "     " + wheelList.get(recordIndex) + "     ";
        }
        excelTextList.add(excelText);
      }
    }
    return excelTextList;
  }

  // 年月日字符串
  private String stockDate(Date date) {
    DateFormat timeFormat = new SimpleDateFormat("日期：yyyy/M/d");
    return timeFormat.format(date);
  }

  // 年月日时间字符串
  private String currentDateTime() {
    DateFormat timeFormat = new SimpleDateFormat("yyyy/M/d HH:mm");
    return timeFormat.format(new Date());
  }

  public static void main(String[] args) {
    int d = 1;
    // 0 代表前面补充0
    // 4 代表长度为4
    // d 代表参数为正数型
    String str = String.format("%03d", d);
    System.out.println("str:" + str + ":end"); // 0001

    Calendar date = Calendar.getInstance();
    String year = String.valueOf(date.get(Calendar.YEAR));
    String month = String.valueOf(date.get(Calendar.MONTH) + 1);
    String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
    System.out.println(year + "年" + month + "月" + day + "日");

    int x = 1 / 10;
    System.out.println(x);
  }
}
