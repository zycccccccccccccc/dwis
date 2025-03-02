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
public class CerticateAllSamePageGenerator implements CerticatePageGenerator {

  // 计算合格证总页数
  @Override
  public int computeCertPageSum(int dataSum, ExcelParameter excelParameter, int contentColumnSum) {
    if (excelParameter.getContentRowSum() == 0 || dataSum == 0) { // 无需填充数据或者没有数据
      return 1;
    }
    return (dataSum - 1) / contentColumnSum / excelParameter.getContentRowSum() + 1;
  }

  // 超过1页的时候，拷贝其它页模板
  @Override
  public void copySheetModel(Sheet sheet, int pageSum, ExcelParameter excelParameter) {
    if (pageSum > 1) {
      for (int page = 1; page < pageSum; page ++) {
        log.info("copy page {}", page);
        ExcelUtil.copyCell(sheet, 0, 0,
          sheet, excelParameter.getTotalRowSum() * page, 0,
          excelParameter.getTotalRowSum(), excelParameter.getTotalColumnSum());
      }
    }
  }

  // 替换数据部分内容
  @Override
  public  void replaceSheetData(Sheet sheet,
                               ExcelParameter excelParameter,
                               int sheetId,
                               int pageSum,
                               List<String> excelTextList) {
    if (excelParameter.getContentRowSum() == 0) {
      return;
    }
    for (int page = 0; page < pageSum; page ++) {
      // 当前页内容部分行数
      int lineSum = Math.min(excelParameter.getContentRowSum(),
        excelTextList.size() - excelParameter.getContentRowSum() * page);
      for (int line = 0; line < lineSum; line ++) {
        Row row = sheet.getRow(excelParameter.getTotalRowSum() * page
          + excelParameter.getContentStartRow() + line);
        Cell cell = row.getCell(0);
        cell.setCellValue(excelTextList.get(excelParameter.getContentRowSum() * page + line));
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
    for (int page = 0; page < pageSum; page++) {
      for (int line = 0; line < excelParameter.getContentRowSum(); line++) {
        String excelText = "";
        for(int i = 0; i < certContentColumnSum; i ++) {
          int recordIndex = excelParameter.getContentRowSum() * certContentColumnSum * page
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
