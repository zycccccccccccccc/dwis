package tech.hciot.dwis.business.application.certificate;

import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import tech.hciot.dwis.business.application.ExcelParameter;

public interface CerticatePageGenerator {

  /**
   * 计算合格证总页数
   * @param dataSum 总的数据条数
   * @param excelParameter excel参数对象
   * @param contentColumnSum 数据部分分成几列
   * @return
   */
  int computeCertPageSum(int dataSum, ExcelParameter excelParameter, int contentColumnSum);

  /**
   * 超过1页的时候，拷贝其它页模板
   * @param sheet
   * @param pageSum 总页数
   * @param excelParameter excel参数对象
   */
  void copySheetModel(Sheet sheet, int pageSum, ExcelParameter excelParameter);

  /**
   * 替换单元格数据部分内容
   * @param sheet
   * @param excelParameter
   * @param sheetId
   * @param pageSum 总页数
   * @param excelTextList 数据列表
   */
  void replaceSheetData(Sheet sheet,
                        ExcelParameter excelParameter,
                        int sheetId,
                        int pageSum,
                        List<String> excelTextList);

  /**
   * 生成合格证最终要展示的数据
   * @param certLineList 原始的数据
   * @param excelParameter
   * @param pageSum 总页数
   * @param certContentColumnSum 数据部分分成几列
   * @return
   */
  List<String> generateCertTextList(List<String> certLineList,
                                    ExcelParameter excelParameter,
                                    int pageSum,
                                    int certContentColumnSum);
}
