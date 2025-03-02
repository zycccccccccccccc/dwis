package tech.hciot.dwis.business.infrastructure;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.ExcelParameter;

@Slf4j
public class ExcelUtil {

  // 创建单元格
  public static void createCell(Row row, int columnNum, Object value, Integer[] lengthArray) {
    createCellWithScale(row, columnNum, value, lengthArray, 3);
  }

  public static void createCellWithScale(Row row, int columnNum, Object value, Integer[] lengthArray, int scale) {
    String rtnValue;
    if (value instanceof Integer || value instanceof Long || value instanceof Double) {
      rtnValue = String.valueOf(value);
    } else if (value instanceof BigDecimal) {
      BigDecimal bigDecimal = (BigDecimal) value;
      rtnValue = bigDecimal.setScale(scale, RoundingMode.HALF_UP).toPlainString();
    } else if (value instanceof Timestamp) {
      rtnValue = StandardTimeUtil.toTimeStr(((Timestamp) value).getTime());
    } else if (value instanceof Date) {
      rtnValue = StandardTimeUtil.dateStr(((Date) value).getTime());
    } else if (value == null) {
      rtnValue = "";
    } else {
      rtnValue = (String) value;
    }
    row.createCell(columnNum).setCellValue(rtnValue);
    try {
      lengthArray[columnNum] = Math.max(lengthArray[columnNum], rtnValue.getBytes("gbk").length);
    } catch (UnsupportedEncodingException e) {

    }
  }

  // 创建导出excel中的标题行
  public static void createTitleRow(Sheet sheet, String[] headers) {
    Row row = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++) {
      Cell cell = row.createCell(i);
      XSSFRichTextString text = new XSSFRichTextString(headers[i]);
      cell.setCellValue(text);
    }
  }

  // 生成列宽数组，用于后面自动调整列宽
  public static Integer[] createColumnWidthArray(String[] headers) {
    Integer[] lengthArray = new Integer[headers.length];
    for (int i = 0; i < lengthArray.length; i++) {
      try {
        lengthArray[i] = headers[i].getBytes("gbk").length;
      } catch (UnsupportedEncodingException e) {
        lengthArray[i] = 10; // 默认10个字符
      }
    }
    return lengthArray;
  }

  // 自动调整列宽
  public static void autoSizeColumnWidth(Sheet sheet, Integer[] lengthArray) {
    for (int i = 0; i < lengthArray.length; i++) {
      sheet.setColumnWidth(i, (lengthArray[i] + 3) * 256);
    }
  }

  // 根据excel里面预置的标签初始化excel参数
  // 其中：
  // firstline定位数据部分的第一行
  // lastline定位数据部分的最后一行
  // secondpage定位第二页
  // lastcolumn定位最后一列
  public static ExcelParameter extractExcelParameter(Sheet sheet) {
    log.info("start extractExcelParameter");
    ExcelParameter excelParameter = ExcelParameter.builder().build();
    for (int r = 0, rowNum = sheet.getLastRowNum(); r <= rowNum; r++) {
      Row row = sheet.getRow(r);
      if (row == null) {
        continue;
      }
      int newPageRowNum = 0;
      for (int col = 0; col <= row.getLastCellNum(); col++) {
        Cell cell = row.getCell(col);
        if (cell == null) {
          continue;
        }
        String value = ExcelUtil.getCellStringValue(cell);
        String updatedValue = " ";
        if (value != null && value.startsWith("contentcolumn")) {
          int contentColumnSum = Integer.parseInt(value.split(":")[1]);
          excelParameter.setContentColumnSum(contentColumnSum);
          cell.setCellValue(updatedValue);
          continue;
        }
        switch (value) {
          case "firstline":
            excelParameter.setContentStartRow(r);
            break;
          case "lastline":
            excelParameter.setContentRowSum(r - excelParameter.getContentStartRow() + 1);
            break;
          case "secondpage":
            excelParameter.setTotalRowSum(r);
            newPageRowNum = r;
            break;
          case "lastcolumn":
            excelParameter.setTotalColumnSum(col + 1);
            break;
          case "thirdpage":
            excelParameter.setOtherPageTotalRowSum(r - excelParameter.getTotalRowSum());
            excelParameter.setPagingType(ExcelParameter.PAGING_TYPE_FIRST_PAGE);
            newPageRowNum = r;
            break;
          case "firstline2":
            excelParameter.setOtherPageContentStartRow(r - excelParameter.getTotalRowSum());
            break;
          case "lastline2":
            excelParameter.setOtherPageContentRowSum(
                r - excelParameter.getTotalRowSum() - excelParameter.getOtherPageContentStartRow() + 1);
            break;
          default:
            continue;
        }
        cell.setCellValue(updatedValue);
      }
      if (newPageRowNum != 0) { // 删除secondpage这一行，不然打印的时候会打印一张空白页
        sheet.removeRow(sheet.getRow(newPageRowNum));
      }
    }
    log.info("paging type: {}", excelParameter.getPagingType());
    log.info("page total rows: {}", excelParameter.getTotalRowSum());
    log.info("page total columns: {}", excelParameter.getTotalColumnSum());
    log.info("content start row: {}", excelParameter.getContentStartRow());
    log.info("content total rows: {}", excelParameter.getContentRowSum());

    log.info("other page total rows: {}", excelParameter.getOtherPageTotalRowSum());
    log.info("other page content start row: {}", excelParameter.getOtherPageContentStartRow());
    log.info("other page content total rows: {}", excelParameter.getOtherPageTotalRowSum());
    log.info("finish extractExcelParameter");
    return excelParameter;
  }

  /**
   * 拷贝单元格
   *
   * @param srcSheet       源单元格所在表单
   * @param srcStartRow    源单元格起始行
   * @param srcStartColumn 源单元格起始列
   * @param dstSheet       目标单元格所在表单
   * @param dstStartRow    目标单元格起始行
   * @param dstStartColumn 目标单元格起始列
   * @param rowSum         要拷贝的行数
   * @param columnSum      要拷贝的列数
   */
  public static void copyCell(Sheet srcSheet, int srcStartRow, int srcStartColumn,
      Sheet dstSheet, int dstStartRow, int dstStartColumn,
      int rowSum, int columnSum) {
    log.info("copy cell from row: {}, to row: {}", srcStartRow, dstStartRow);
    for (int row = 0; row < rowSum; row++) {
      Row oldRow = srcSheet.getRow(srcStartRow + row);
      if (oldRow == null) {
        continue;
      }
      Row newRow = dstSheet.createRow(dstStartRow + row);
      newRow.setHeightInPoints(oldRow.getHeightInPoints());
      for (int col = 0; col < columnSum; col++) {
        Cell oldCell = oldRow.getCell(srcStartColumn + col);
        if (oldCell != null) {
          Cell newCell = newRow.createCell(dstStartColumn + col);
          ExcelUtil.copyCell(srcSheet, dstSheet, oldCell, newCell);
        }
      }
    }
    copyMergedRegion(srcSheet, srcStartRow, srcStartColumn, dstSheet, dstStartRow, dstStartColumn, rowSum, columnSum);
  }

  /**
   * 拷贝单元格
   *
   * @param srcSheet       源单元格所在表单
   * @param srcStartRow    源单元格起始行
   * @param srcStartColumn 源单元格起始列
   * @param dstSheet       目标单元格所在表单
   * @param dstStartRow    目标单元格起始行
   * @param dstStartColumn 目标单元格起始列
   * @param rowSum         要拷贝的行数
   * @param columnSum      要拷贝的列数
   * @param createNewRow   是否创建新行
   */
  public static void copyCell(Sheet srcSheet, int srcStartRow, int srcStartColumn,
      Sheet dstSheet, int dstStartRow, int dstStartColumn,
      int rowSum, int columnSum,
      boolean createNewRow) {
    if (createNewRow) {
      copyCell(srcSheet, srcStartRow, srcStartColumn, dstSheet, dstStartRow, dstStartColumn, rowSum, columnSum);
    }
    log.info("copy cell from row: {}, to row: {}", srcStartRow, dstStartRow);
    for (int row = 0; row < rowSum; row++) {
      Row oldRow = srcSheet.getRow(srcStartRow + row);
      if (oldRow == null) {
        continue;
      }
      Row newRow = dstSheet.getRow(dstStartRow + row);
      newRow.setHeightInPoints(oldRow.getHeightInPoints());
      for (int col = 0; col < columnSum; col++) {
        Cell oldCell = oldRow.getCell(srcStartColumn + col);
        if (oldCell != null) {
          Cell newCell = newRow.createCell(dstStartColumn + col);
          ExcelUtil.copyCell(srcSheet, dstSheet, oldCell, newCell);
        }
      }
    }
    copyMergedRegion(srcSheet, srcStartRow, srcStartColumn, dstSheet, dstStartRow, dstStartColumn, rowSum, columnSum);
  }

  // 拷贝合并单元格
  public static void copyMergedRegion(Sheet srcSheet, int srcStartRow, int srcStartColumn,
      Sheet dstSheet, int dstStartRow, int dstStartColumn,
      int rowSum, int columnSum) {
    int regions = srcSheet.getNumMergedRegions();
    for (int i = 0; i < regions; i++) {
      CellRangeAddress cellRangeAddress = srcSheet.getMergedRegion(i);

      if (cellRangeAddress.getFirstRow() >= srcStartRow
          && cellRangeAddress.getLastRow() < srcStartRow + rowSum
          && cellRangeAddress.getFirstColumn() >= srcStartColumn
          && cellRangeAddress.getLastColumn() < srcStartColumn + columnSum) {
//        log.info("from row:{}, col:{}, to row:{}, col:{}",
//          cellRangeAddress.getFirstRow(),
//          cellRangeAddress.getFirstColumn(),
//          cellRangeAddress.getLastRow(),
//          cellRangeAddress.getLastColumn());

        CellRangeAddress newCellRangeAddress = new CellRangeAddress(
            cellRangeAddress.getFirstRow() - srcStartRow + dstStartRow,
            cellRangeAddress.getLastRow() - srcStartRow + dstStartRow,
            cellRangeAddress.getFirstColumn() - srcStartColumn + dstStartColumn,
            cellRangeAddress.getLastColumn() - srcStartColumn + dstStartColumn);
        dstSheet.addMergedRegionUnsafe(newCellRangeAddress);
      }
    }
  }

  // 多页的时候，拷贝其它页的模板
  public static void copyCell(Sheet sheet, int pageSum, int totalRowSum, int totalColumnSum) {
    log.info("start copyCell");
    int regions = sheet.getNumMergedRegions();
    for (int page = 1; page < pageSum; page++) {
      // 拷贝单元格内容和样式
      for (int row = 0; row < totalRowSum; row++) {
        Row oldRow = sheet.getRow(row);
        if (oldRow == null) {
          continue;
        }

        Row newRow = sheet.createRow(row + totalRowSum * page);
        newRow.setHeightInPoints(oldRow.getHeightInPoints());

        for (int col = 0; col < totalColumnSum; col++) {
//          log.info("row: {}, col: {}, page: {}", row, col, page);
          Cell oldCell = oldRow.getCell(col);
          if (oldCell != null) {
            Cell newCell = newRow.createCell(col);
            copyCell(sheet, sheet, oldCell, newCell);
          }
        }
      }
//      copyMergedRegion(sheet, regions, page, totalRowSum);
    }
    log.info("finish copyCell");
  }

  // 拷贝单元格内容和样式
  public static void copyCell(Sheet srcSheet, Sheet dstSheet, Cell oldCell, Cell newCell) {
    CellStyle newStyle;
    if (srcSheet != dstSheet) {
      newStyle = dstSheet.getWorkbook().createCellStyle();
      copyStyle(srcSheet, oldCell.getCellStyle(), newStyle);
    } else {
      newStyle = oldCell.getCellStyle();
    }
    newCell.setCellStyle(newStyle);
    if (oldCell.getCellType().equals(CellType.BLANK)) {
      newCell.setCellType(CellType.BLANK);
    } else if (oldCell.getCellType().equals(CellType.NUMERIC)) {
      if (DateUtil.isCellDateFormatted(oldCell)) {
        newCell.setCellValue(oldCell.getDateCellValue());
      } else {
        newCell.setCellValue(oldCell.getNumericCellValue());
      }
    } else {
      newCell.setCellValue(oldCell.getStringCellValue());
    }
  }

  // 拷贝单元格样式
  public static void copyStyle(Sheet srcSheet, CellStyle oldStyle, CellStyle newStyle) {
    newStyle.setAlignment(oldStyle.getAlignment());
    newStyle.setDataFormat(oldStyle.getDataFormat());
    newStyle.setFillPattern(oldStyle.getFillPattern());
    newStyle.setHidden(oldStyle.getHidden());
    newStyle.setLocked(oldStyle.getLocked());
    newStyle.setIndention(oldStyle.getIndention());
    newStyle.setRotation(oldStyle.getRotation());
    newStyle.setVerticalAlignment(oldStyle.getVerticalAlignment());
    newStyle.setWrapText(oldStyle.getWrapText());
    newStyle.cloneStyleFrom(oldStyle);
    newStyle.setFont(srcSheet.getWorkbook().getFontAt(oldStyle.getFontIndexAsInt()));
  }

  // 返回单元格字符串类型的值
  public static String getCellStringValue(Cell cell) {
    if (cell == null) {
      return "";
    } else if (cell.getCellType().equals(CellType.BLANK)) {
      return "";
    } else if (cell.getCellType().equals(CellType.NUMERIC)) {
      return String.valueOf(cell.getNumericCellValue());
    } else {
      return cell.getStringCellValue();
    }
  }

  public static void copyRow(Sheet sheet, int startRow, int cellNum) {
    sheet.shiftRows(startRow + 1, sheet.getLastRowNum(), 1, true, false);
    //新增行
    Row rowInsert = sheet.createRow(startRow + 1);
    //获取当前行
    Row rowSource = sheet.getRow(startRow);
    //获取当前行样式
    CellStyle rowStyle = rowSource.getRowStyle();

    if (rowStyle != null) {
      rowInsert.setRowStyle(rowStyle);
      rowInsert.setHeight(rowSource.getHeight());
    }
    copyCell(sheet, startRow, 0, sheet, rowInsert.getRowNum(), 0, 1, cellNum);
  }

  public static void setStyle(Row tempRow, Row r) {
    //获取当前行样式
    CellStyle rowStyle = tempRow.getRowStyle();

    if (tempRow != null) {
      r.setRowStyle(rowStyle);
      r.setHeight(tempRow.getHeight());
    }
    for (int col = 0; col < tempRow.getLastCellNum(); col++) {
      Cell oldCell = tempRow.getCell(col);
      Cell newCell = r.createCell(col);
      if (oldCell != null) {
        newCell.setCellStyle(oldCell.getCellStyle());
      }
    }
    copyMergedRegion(tempRow, r);
  }

  public static void setStyle(Row tempRow, Row r, int startCol, int endCol) {
    //获取当前行样式
    CellStyle rowStyle = tempRow.getRowStyle();

    if (tempRow != null) {
      r.setRowStyle(rowStyle);
      r.setHeight(tempRow.getHeight());
    }
    for (int col = startCol; col <= endCol; col++) {
      Cell oldCell = tempRow.getCell(col);
      Cell newCell = r.createCell(col);
      if (oldCell != null) {
        newCell.setCellStyle(oldCell.getCellStyle());
      }
    }
  }

  private static void copyMergedRegion(Row tempRow, Row r) {
    tempRow.getSheet().getMergedRegions().forEach(region -> {
      if (region.getFirstRow() == tempRow.getRowNum() && region.getLastRow() == tempRow.getRowNum()) {
        CellRangeAddress newCellRangeAddress = new CellRangeAddress(
            r.getRowNum(),
            r.getRowNum(),
            region.getFirstColumn(),
            region.getLastColumn());
        r.getSheet().addMergedRegionUnsafe(newCellRangeAddress);
      }
    });
  }

  public static void removeMergedRegion(Sheet sheet, int startIndex) {
    List<Integer> indexList = new ArrayList<>();
    List<CellRangeAddress> cellRangeAddressList = sheet.getMergedRegions();
    for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
      CellRangeAddress cellRangeAddress = cellRangeAddressList.get(i);
      if (cellRangeAddress.getFirstRow() >= startIndex) {
        indexList.add(i);
      }
    }
    sheet.removeMergedRegions(indexList);
  }

  public static void addMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
    try {
      CellRangeAddress newCellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
      sheet.addMergedRegionUnsafe(newCellRangeAddress);
    } catch (Exception e) {

    }
  }

  public static void setPrintSetupInfo(Sheet sheet1, Sheet sheet2) {
    PrintSetup psTemplete = sheet1.getPrintSetup();
    PrintSetup psOutput = sheet2.getPrintSetup();

    //页眉边距设置
    psOutput.setHeaderMargin(psTemplete.getHeaderMargin());
    psOutput.setFooterMargin(psTemplete.getFooterMargin());
    //设置宽、高页数以适合纸张
    psOutput.setFitWidth(psTemplete.getFitWidth());
    psOutput.setFitHeight(psTemplete.getFitHeight());
    //设置纸张尺寸
    psOutput.setPaperSize(psTemplete.getPaperSize());
    //设置页码开始
    psOutput.setPageStart(psTemplete.getPageStart());
    //设置是否横向打印
    psOutput.setLandscape(psTemplete.getLandscape());
    //设置是按顺序从左到右还是自上而下
    psOutput.setLeftToRight(psTemplete.getLeftToRight());
    //设置是否处于草稿模式
    psOutput.setDraft(psTemplete.getDraft());
    //设置比例
    psOutput.setScale(psTemplete.getScale());
    //页边距设置
//    psOutput.setTopMargin(psTemplete.getTopMargin());
//    psOutput.setBottomMargin(psTemplete.getBottomMargin());
//    psOutput.setLeftMargin(psTemplete.getLeftMargin());
//    psOutput.setRightMargin(psTemplete.getRightMargin());

    //页面打印选项设置（根据模板的选择来设置）
    sheet2.setFitToPage(sheet1.getFitToPage());
    //页脚设置
    Footer psTempletefooter = sheet1.getFooter();
    Footer psOutputfooter = sheet2.getFooter();
    psOutputfooter.setCenter(psTempletefooter.getCenter());
    //设置标题（实现打印时每一页都有同个头部标题）
    sheet2.setRepeatingRows(sheet1.getRepeatingRows());
    sheet2.setRepeatingColumns(sheet1.getRepeatingColumns());
  }

  public static Sheet getClonedSheet(Sheet sheet, int sheetIndex) {
    Sheet sxssfSheet = sheet.getWorkbook().cloneSheet(sheetIndex);
    setPrintSetupInfo(sheet, sxssfSheet);
    return sxssfSheet;
  }
}
