package tech.hciot.dwis.business.application;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.StockData;
import tech.hciot.dwis.business.interfaces.dto.StockProduct;

@Service
@Slf4j
public class StockExportService {

  private static final int CONTENT_COLUMN_SUM = 4; // 内容部分分几列

  public void exportStock(StockData stockData, HttpServletResponse response) {
    ClassPathResource resource = new ClassPathResource("static/stock.xlsx");
    try (InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream); ) {
      generateWorkSheet(workbook, stockData);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=stock.xlsx");
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void generateWorkSheet(Workbook workbook, StockData stockData) {
//    stockData = testData(stockData); // TODO 测试数据

    ExcelParameter firstPageParameter = ExcelUtil.extractExcelParameter(workbook.getSheetAt(0)); // 第1页的参数
    ExcelParameter otherPageParameter = ExcelUtil.extractExcelParameter(workbook.getSheetAt(1)); // 其它页的参数
    List<String> contextTextList = generateContextTextList(stockData);
    int pageSum = computePageSum(contextTextList.size(),
      firstPageParameter.getContentRowSum(), otherPageParameter.getContentRowSum()); // 总页数
    List<String> excelTextList = generateExcelTextList(contextTextList, pageSum, firstPageParameter.getContentRowSum(),
      otherPageParameter.getContentRowSum());
    copySheet(workbook, pageSum, firstPageParameter.getTotalRowSum(), otherPageParameter.getTotalRowSum(),
      firstPageParameter.getTotalColumnSum()); // 拷贝其它页
    Sheet sheet = workbook.getSheetAt(0);

    try {
      updateContent(stockData, firstPageParameter, otherPageParameter, pageSum, excelTextList, sheet);
      sheet.protectSheet("dwis");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // 总页数
  private int computePageSum(int sum, int firstPageContentRowSum, int secondPageContentRowSum) {
    if (sum / CONTENT_COLUMN_SUM <= firstPageContentRowSum) {
      return 1;
    } else {
      return (sum - firstPageContentRowSum * CONTENT_COLUMN_SUM) / (secondPageContentRowSum * CONTENT_COLUMN_SUM) + 2;
    }
  }

  // 超过1页的时候，拷贝其它页
  private void copySheet(Workbook workbook, int pageSum, int firstPageRowSum, int secondPageRowSum, int columnSum) {
    if (pageSum == 1) { // 只有1页时，只拷贝备注那一行
      log.info("copy page {}", 1);
      ExcelUtil.copyCell(workbook.getSheetAt(2), secondPageRowSum - 3, 0,
        workbook.getSheetAt(0), firstPageRowSum - 3, 0,
        1, columnSum);
    } else if (pageSum > 1) {
      for (int page = 1; page < pageSum - 1; page ++) {
        log.info("copy page {}", page);
        ExcelUtil.copyCell(workbook.getSheetAt(1), 0, 0,
          workbook.getSheetAt(0), firstPageRowSum + secondPageRowSum * (page - 1), 0,
          secondPageRowSum, columnSum);
      }
      log.info("copy last page");
      ExcelUtil.copyCell(workbook.getSheetAt(2), 0, 0,
        workbook.getSheetAt(0), firstPageRowSum + secondPageRowSum * (pageSum - 2), 0,
        secondPageRowSum, columnSum);
    } else {
      log.info("no need to copy cell ");
    }
    workbook.removeSheetAt(2);
    workbook.removeSheetAt(1);
  }

  // 生成excel最终要展示的产品编号数据
  private List<String> generateExcelTextList(List<String> contextTextList, int pageSum,
                                             int firstPageContentRowSum, int secondPageContentRowSum) {
    List<String> excelTextList = new ArrayList<>();
    int totalPage; // 总页数
    // 第一页
    for (int line = 0; line < firstPageContentRowSum; line++) {
      String excelText = "";
      for(int i = 0; i < CONTENT_COLUMN_SUM; i ++) {
        int recordIndex = firstPageContentRowSum * i + line;
        if (recordIndex >= contextTextList.size()) {
          continue;
        }
        excelText = excelText + contextTextList.get(recordIndex);
      }
      excelTextList.add(excelText);
    }
    int firstPageDataSum = firstPageContentRowSum * CONTENT_COLUMN_SUM; // 第一页总记录数
    if (contextTextList.size() - firstPageDataSum <= 0) { // 只有一页的情况
      return excelTextList;
    }
    // 第二页往后
    for (int page = 1; page < pageSum; page++) {
      for (int line = 0; line < secondPageContentRowSum; line++) {
        String excelText = "";
        for(int i = 0; i < CONTENT_COLUMN_SUM; i ++) {
          int recordIndex = firstPageDataSum + secondPageContentRowSum * CONTENT_COLUMN_SUM * (page - 1)
            + secondPageContentRowSum * i + line;
          if (recordIndex >= contextTextList.size()) {
            continue;
          }
          excelText = excelText + contextTextList.get(recordIndex);
        }
        excelTextList.add(excelText);
      }
    }
    return excelTextList;
  }

  // 向表格中填写内容
  private void updateContent(StockData stockData, ExcelParameter firstPageParameter, ExcelParameter otherPageParameter,
                             int pageSum, List<String> excelTextList, Sheet sheet) {
    // 第1页
    for (int r = 0; r < firstPageParameter.getTotalRowSum(); r ++) {
      Row row = sheet.getRow(r);
      if (row == null) {
        continue;
      }
      for (int col = 0; col <= row.getLastCellNum(); col ++) {
        Cell cell = row.getCell(col);
        if (cell == null) {
          continue;
        }
        updateCellValue(cell, stockData, 0, pageSum);
      }
    }
    // 第2页以后
    for (int page = 1; page < pageSum; page ++) {
      int firstRow = firstPageParameter.getTotalRowSum() + otherPageParameter.getTotalRowSum() * (page - 1); // 第一行行号
      int lastRow = firstPageParameter.getTotalRowSum() + otherPageParameter.getTotalRowSum() * page; // 最后一行的下一行行号
      for (int r = firstRow; r < lastRow; r ++) {
        Row row = sheet.getRow(r);
        if (row == null) {
          continue;
        }
        for (int col = 0; col <= row.getLastCellNum(); col ++) {
          Cell cell = row.getCell(col);
          if (cell == null) {
            continue;
          }
          updateCellValue(cell, stockData, page, pageSum);
        }
      }
    }

    for (int page = 0; page < pageSum; page ++) {
      // 列表中每页的第一行行号
      int firstLine = page == 0 ? 0
        : firstPageParameter.getContentRowSum() + otherPageParameter.getContentRowSum() * (page - 1);

      // 当前页内容部分首行行号
      int firstContentLine = page == 0 ? firstPageParameter.getContentStartRow()
        : firstPageParameter.getTotalRowSum() + otherPageParameter.getTotalRowSum() * (page - 1)
        + otherPageParameter.getContentStartRow();

      // 当前页内容部分行数
      int lineSum = page == 0 ? Math.min(firstPageParameter.getContentRowSum(), excelTextList.size())
        : Math.min(otherPageParameter.getContentRowSum(), excelTextList.size() - firstLine);

      log.info("update content, page: {}, firstContentLine: {}", page, firstContentLine);
      for (int line = 0; line < lineSum; line ++) {
        log.info("update content, line: {}", line);
        Row row = sheet.getRow(firstContentLine + line);
        Cell cell = row.getCell(0);
        cell.setCellValue(excelTextList.get(firstLine + line));
      }
    }
  }

  // 更新单元格的值
  protected void updateCellValue(Cell cell, StockData stockData, int page, int pageSum) {
    String cellValue = ExcelUtil.getCellStringValue(cell);
    if (cellValue == null || !cellValue.startsWith("$")) {
      return;
    }

    if ("$stockNo".equals(cellValue)) { // 编号
      cell.setCellValue("编号：" + stockData.getIndex());

    } else if ("$design".equals(cellValue)) { // 规格型号
      cell.setCellValue(stockData.getSpec());

    } else if ("$drawingNo".equals(cellValue)) { // 图号
      cell.setCellValue(stockData.getDrawingNo());

    } else if ("$sum".equals(cellValue)) { // 核查数量
      cell.setCellValue(stockData.getAmount());

    } else if ("$date".equals(cellValue)) { // 右下角日期
      cell.setCellValue(currentDate(stockData.getDate()));

    } else if ("$page".equals(cellValue)) { // 页数
      cell.setCellValue("Page " + (page + 1) + " of " + pageSum);
    }
  }

  // 年月日字符串
  private String currentDate(Date date) {
    DateFormat timeFormat = new SimpleDateFormat("yyyy年M月d日");
    return timeFormat.format(date);
  }

  // 生成每一列的产品编号数据
  private List<String> generateContextTextList(StockData stockData) {
    List<StockProduct> allStockProductList = stockData.getProducts();
    List<String> stockTextList = new ArrayList<>();
    allStockProductList.forEach(stockProduct -> {
      stockTextList.add("  轴孔 带尺 轮辋 E3  ");
      String type = "  " + String.format("%-4d", stockProduct.getBoreSize())
        + " " + String.format("%-5.1f", stockProduct.getTapeSize().doubleValue())
        + " " + String.format("%-3d", stockProduct.getWheelW())
        + " " + String.format("%-4s", StringUtils.isBlank(stockProduct.getE3()) ? "Not" : stockProduct.getE3());
      stockTextList.add(type);
      stockTextList.add("    监造产品编号     ");
      stockProduct.getProductCodeList().forEach(productCode -> {
        stockTextList.add("     " + productCode + "      ");
      });
      stockTextList.add("     小计    " + String.format("%2d", stockProduct.getProductCodeList().size()) + "      ");
      stockTextList.add("  ────────   ");
    });
    return stockTextList;
  }

  private StockData testData(StockData stockData) {
    List<StockProduct> stockProductList = new ArrayList<>();
    // 1页：10 * 10
    // 2页：10 * 20
    // 3页：10 * 30
    for (int i = 0; i < 10; i ++) {
      List<String> productCodeList = new ArrayList<>();
      for (int j = 0; j < 30; j ++) {
        productCodeList.add("210800" + i + String.format("%03d", j));
      }
      StockProduct product = StockProduct.builder()
        .boreSize(185)
        .tapeSize(new BigDecimal(832.0))
        .wheelW(125)
        .e3("E3")
        .productCodeList(productCodeList)
        .sum(productCodeList.size())
        .build();
      stockProductList.add(product);
    }
    stockData.setProducts(stockProductList);
    stockData.setDrawingNo("JV33-10B");
    stockData.setAmount(stockData.getProducts().stream().mapToInt(StockProduct::getSum).sum());
    return stockData;
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
