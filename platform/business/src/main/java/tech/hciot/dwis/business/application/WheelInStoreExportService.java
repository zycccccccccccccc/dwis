package tech.hciot.dwis.business.application;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.interfaces.dto.OutfitCheckResponse;

@Service
@Slf4j
public class WheelInStoreExportService {

  // 抽检单相关的excel参数
  private static final int TOTAL_ROW_SUM = 40; // 抽检单总行数
  private static final int CONTENT_START_ROW = 5; // 抽检单内容部分第一行行号
  private static final int CONCLUSION_START_ROW = 23; // 抽检单结论部分第一行行号
  private static final int CONTENT_ROW_SUM = 33; // 抽检单前面几页内容部分总行数
  private static final int LAST_PAGE_CONTENT_ROW_SUM = 18; // 抽检单最后一页内容部分总行数

  public void exportCheckData(List<OutfitCheckResponse> checkDataList, HttpServletResponse response) {
    ClassPathResource resource = new ClassPathResource("static/checkout.xlsx");
    try (InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream); ) {
      generateWorkSheet(workbook, checkDataList);
      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=checkout.xlsx");
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void generateWorkSheet(Workbook workbook, List<OutfitCheckResponse> checkDataList) {
//    checkDataList = testData(checkDataList); // TODO 测试数据

    int dataSum = checkDataList.size(); // 总记录数
    int pageSum = computePageSum(dataSum); // 总页数

    Sheet sheet = workbook.getSheetAt(0);

    String design = checkDataList.get(0).getDesign();

    // 填数据
    for (int page = 0; page < pageSum; page ++) {
      // 标题
      sheet.getRow(TOTAL_ROW_SUM * page).getCell(0)
        .setCellValue(design + " 车轮外观和磁探抽验交验报表");
      sheet.getRow(TOTAL_ROW_SUM * page + 1).getCell(0)
        .setCellValue(" Report of " + design + " Surface and MPI Sampling Audit");

      // 数量
      sheet.getRow(TOTAL_ROW_SUM * page + 3).getCell(1)
        .setCellValue(dataSum);

      // 日期
      sheet.getRow(TOTAL_ROW_SUM * page + 3).getCell(6)
        .setCellValue(checkDate());

      int lineSum; // 当前页内容部分行数
      if (page == pageSum - 1) { // 最后一页
        lineSum = Math.min(dataSum - CONTENT_ROW_SUM * 2 * page, LAST_PAGE_CONTENT_ROW_SUM);
      } else {
        lineSum = CONTENT_ROW_SUM;
      }
      int dataStart = CONTENT_ROW_SUM * 2 * page;
      int pageStart = TOTAL_ROW_SUM * page + CONTENT_START_ROW;
      for (int line = 0; line < lineSum; line ++) {
        int dataLeftIndex = dataStart + line;
        if (dataLeftIndex < dataSum) {
          // 左边部分数据
          OutfitCheckResponse dataLeft = checkDataList.get(dataLeftIndex);
          sheet.getRow(pageStart + line).getCell(0).setCellValue(dataLeft.getWheelSerial());
          sheet.getRow(pageStart + line).getCell(1).setCellValue(dataLeft.getMachineNo());
        }

        int dataRightIndex = dataStart + lineSum + line;
        if (dataRightIndex < dataSum) {
          // 右边部分数据
          OutfitCheckResponse dataRight = checkDataList.get(dataRightIndex);
          sheet.getRow(pageStart + line).getCell(4).setCellValue(dataRight.getWheelSerial());
          sheet.getRow(pageStart + line).getCell(5).setCellValue(dataRight.getMachineNo());
        }
      }

      // 共x页，第x页
      Cell totalCell = sheet.getRow(TOTAL_ROW_SUM * (page + 1) - 1).getCell(6);
      totalCell.setCellValue("共" + pageSum + "页，第" + (page + 1) + "页");
    }

    // 拷贝结论部分
    copyConcluionContent(workbook.getSheetAt(1), sheet, TOTAL_ROW_SUM * (pageSum - 1));

    // 删除多余的内容
    int lastRow = TOTAL_ROW_SUM * pageSum; // 最后一行下面一行的行号
    for (int line = sheet.getLastRowNum(); line >= lastRow; line --) {
      sheet.removeRow(sheet.getRow(line));
    }
    workbook.removeSheetAt(1);
  }

  // 拷贝结论部分内容
  private void copyConcluionContent(Sheet srcSheet, Sheet destSheet, int lastPageFirstRow) {
    int startRow = lastPageFirstRow + CONCLUSION_START_ROW; // 结论部分起始行号
    CellRangeAddress region = new CellRangeAddress(startRow, startRow, 0, 7); // 空行
    destSheet.addMergedRegion(region);
    int i = 1;
    region = new CellRangeAddress(startRow + i, startRow + i + 3, 0, 0); // 外观
    destSheet.addMergedRegion(region);
    region = new CellRangeAddress(startRow + i, startRow + i + 3, 1, 7);
    destSheet.addMergedRegion(region);
    i += 4;
    region = new CellRangeAddress(startRow + i, startRow + i + 2, 0, 0); // 磁探
    destSheet.addMergedRegion(region);
    region = new CellRangeAddress(startRow + i, startRow + i + 2, 1, 7);
    destSheet.addMergedRegion(region);
    i += 3;
    region = new CellRangeAddress(startRow + i, startRow + i, 0, 7); // 磁探结论
    destSheet.addMergedRegion(region);
    i ++;
    region = new CellRangeAddress(startRow + i, startRow + i + 1, 0, 7); // 结论
    destSheet.addMergedRegion(region);
    i += 2;
    region = new CellRangeAddress(startRow + i, startRow + i + 1, 4, 7); // 签字
    destSheet.addMergedRegion(region);
    i += 2;
    region = new CellRangeAddress(startRow + i, startRow + i + 1, 4, 7); // 日期
    destSheet.addMergedRegion(region);

    for (int row = CONCLUSION_START_ROW; row < CONCLUSION_START_ROW + 15; row ++) {
      for (int col = 0; col < 8; col ++) {
        Cell srcCell = srcSheet.getRow(row).getCell(col);
        Cell destCell = destSheet.getRow(lastPageFirstRow + row).getCell(col);
        if (srcCell != null && destCell != null) {
          destCell.setCellStyle(srcCell.getCellStyle());
          destCell.setCellValue(srcCell.getStringCellValue());
        }
      }
    }
  }

  private List<OutfitCheckResponse> testData(List<OutfitCheckResponse> checkDataList) {
    checkDataList = new ArrayList<>();
    for (int i = 0; i < 4; i ++) {
      for (int j = 0; j < 20; j ++) {
        OutfitCheckResponse data = OutfitCheckResponse.builder()
          .wheelSerial("210800" + i + String.format("%03d", j))
          .design("CJ33")
          .machineNo(i)
          .build();
        checkDataList.add(data);
      }
    }
    return checkDataList;
  }

  // 根据记录数计算总页数
  public int computePageSum(int stockDataSum) {
    return stockDataSum <= LAST_PAGE_CONTENT_ROW_SUM
      ? 1 : (stockDataSum - LAST_PAGE_CONTENT_ROW_SUM - 1) / (CONTENT_ROW_SUM * 2) + 2;
  }

  // 年月日字符串
  private String checkDate() {
    DateFormat timeFormat = new SimpleDateFormat("日期：yyyy/M/d");
    return timeFormat.format(new Date());
  }

  public static void main(String[] args) {
    int sum = 18;
    int pageSum = new WheelInStoreExportService().computePageSum(sum);

  }
}
