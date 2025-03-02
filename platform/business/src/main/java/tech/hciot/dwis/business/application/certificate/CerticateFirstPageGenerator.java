package tech.hciot.dwis.business.application.certificate;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import tech.hciot.dwis.business.application.ExcelParameter;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;

@Slf4j
public class CerticateFirstPageGenerator implements CerticatePageGenerator{

  // 计算合格证总页数
  @Override
  public int computeCertPageSum(int dataSum, ExcelParameter excelParameter, int contentColumnSum) {
    if (excelParameter.getContentRowSum() == 0 || dataSum == 0) { // 无需填充数据或者没有数据
      return 1;
    }
    int firstPageDataSum = contentColumnSum * excelParameter.getContentRowSum(); // 第一页数据部分行数
    if (dataSum <= firstPageDataSum) { // 只有一页
      return 1;
    } else { // 有多页
      int otherPageDataSum = dataSum - firstPageDataSum;
      return (otherPageDataSum - 1) / contentColumnSum / excelParameter.getOtherPageContentRowSum() + 2;
    }
  }

  // 超过2页的时候，拷贝其它页模板
  @Override
  public void copySheetModel(Sheet sheet, int pageSum, ExcelParameter excelParameter) {
    if (pageSum > 2){
      for (int page = 2; page < pageSum; page ++) {
        log.info("copy page {}", page);
        ExcelUtil.copyCell(sheet, excelParameter.getTotalRowSum(), 0,
          sheet, excelParameter.getTotalRowSum() + excelParameter.getOtherPageTotalRowSum() * (page - 1), 0,
          excelParameter.getOtherPageTotalRowSum(), excelParameter.getTotalColumnSum());
      }
    }
  }

  // 替换数据部分内容
  @Override
  public void replaceSheetData(Sheet sheet,
                               ExcelParameter excelParameter,
                               int sheetId,
                               int pageSum,
                               List<String> excelTextList) {
    // 替换第一页数据
    int lineSum = Math.min(excelParameter.getContentRowSum(), excelTextList.size()); // 当前页内容部分行数
    for (int line = 0; line < lineSum; line ++) {
      Row row = sheet.getRow(excelParameter.getContentStartRow() + line);
      Cell cell = row.getCell(0);
      cell.setCellValue(excelTextList.get(line));
    }

    // 替换其它页数据
    for (int page = 1; page < pageSum; page ++) {
      // 当前页内容部分行数
      lineSum = Math.min(excelParameter.getOtherPageContentRowSum(),
        excelTextList.size() - excelParameter.getContentRowSum() - excelParameter.getOtherPageContentRowSum() * (page - 1));
      for (int line = 0; line < lineSum; line ++) {
        Row row = sheet.getRow(excelParameter.getTotalRowSum()
          + excelParameter.getOtherPageTotalRowSum() * (page - 1)
          + excelParameter.getOtherPageContentStartRow() + line);
        Cell cell = row.getCell(0);
        int recordIndex = excelParameter.getContentRowSum()
          + excelParameter.getOtherPageContentRowSum() * (page - 1)
          + line;
        cell.setCellValue(excelTextList.get(recordIndex));
      }
    }
  }

  // 生成合格证最终要展示的数据
  @Override
  public List<String> generateCertTextList(List<String> certLineList,
                                           ExcelParameter excelParameter,
                                           int pageSum,
                                           int certContentColumnSum) {
    List<String> excelTextList = new ArrayList<>();
    // 生成第一页数据
    for (int line = 0; line < excelParameter.getContentRowSum(); line++) {
      String excelText = "";
      for(int i = 0; i < certContentColumnSum; i++) {
        int recordIndex = excelParameter.getContentRowSum() * i + line;
        if (recordIndex >= certLineList.size()) {
          break;
        }
        excelText = excelText + certLineList.get(recordIndex);
      }
      excelTextList.add(excelText);
    }

    // 生成其它页数据
    for (int page = 1; page < pageSum; page++) {
      for (int line = 0; line < excelParameter.getOtherPageContentRowSum(); line++) {
        String excelText = "";
        for(int i = 0; i < certContentColumnSum; i ++) {
          int recordIndex = excelParameter.getContentRowSum() * certContentColumnSum
            + excelParameter.getOtherPageContentRowSum() * certContentColumnSum * (page - 1)
            + excelParameter.getContentRowSum() * i + line;
          if (recordIndex >= certLineList.size()) {
            continue;
          }
          excelText = excelText + certLineList.get(recordIndex);
        }
        excelTextList.add(excelText);
      }
    }
    return excelTextList;
  }
}
