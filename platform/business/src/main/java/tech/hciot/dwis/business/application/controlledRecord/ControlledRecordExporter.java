package tech.hciot.dwis.business.application.controlledRecord;

import static tech.hciot.dwis.business.infrastructure.ExcelUtil.copyRow;

import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface ControlledRecordExporter {

  String type();

  String fileName();

  void generateReport(Workbook workbook, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId);

  default void addRow(List dataList, Sheet sheet, int currentRowNum, int originSize) {
    for (int i = 0; i < dataList.size() - originSize; i++) {
      copyRow(sheet, currentRowNum, sheet.getRow(currentRowNum).getPhysicalNumberOfCells());
    }
  }

  default void add2PartRow(List dataList, Sheet sheet, int currentRowNum, int originSize) {
    for (int i = 0; i < (dataList.size() + 1) / 2 - originSize; i++) {
      copyRow(sheet, currentRowNum, sheet.getRow(currentRowNum).getPhysicalNumberOfCells());
    }
  }

  default String getMark(Integer value) {
    String mark = "";
    if (value != null && value == 1) {
      mark = "√";
    }
    return mark;
  }
}
