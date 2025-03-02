package tech.hciot.dwis.business.application.certificate;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.ExcelParameter;
import tech.hciot.dwis.business.domain.model.Certificate;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;

@Service
@Slf4j
public class CertificateGEZExportService extends CertificateExportService implements CertficateExporter {

  @Override
  public String certficateName() {
    return "CE6";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-GEZ.xlsx";
  }

  // 更新单元格的值
  @Override
  protected void updateCellValue(Cell cell, CertificateInfo certificateInfo, int pageSum, ExcelParameter excelParameter) {
    String cellValue = ExcelUtil.getCellStringValue(cell);
    if (cellValue == null) {
      return;
    }
    if ("$design".equals(cellValue)) { // 钢级别
      int row = cell.getAddress().getRow();
      int column = cell.getAddress().getColumn();
      Sheet sheet = cell.getRow().getSheet();
      String steelClass = certificateInfo.getSteelClass();
      if ("B".equals(steelClass)) { // 抗拉强度
        sheet.getRow(row + 3).getCell(column + 3).setCellValue("Rm≥910N/mm2");
        sheet.getRow(row + 6).getCell(column + 3).setCellValue("277-341HBW");
        sheet.getRow(row + 9).getCell(column + 3).setCellValue("≥265HBW");
        sheet.getRow(row + 12).getCell(column + 3).setCellValue("KU2≥12J");
        sheet.getRow(row + 3).getCell(column + 6).setCellValue("A5≥5%");
      }

    } else if ("$designNoList".equals(ExcelUtil.getCellStringValue(cell))) { // 轮型+车轮编号清单
      cell.setCellValue(certificateInfo.getDesign() + "车轮编号清单");
    }
    super.updateCellValue(cell, certificateInfo, pageSum, excelParameter);
  }

  // 内容部分分成几列
  @Override
  protected int certContentColumnSum(int sheetId) {
    if (sheetId == 0) {
      return 1;
    } else {
      return 6;
    }
  }

  // 根据sheet编号生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data, int sheetId) {
    if (sheetId == 0) {
      return generateCertLine(data);
    } else {
      return generateNoListLine(data);
    }
  }

  // 生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data) {
    String line = String.format("%-20s", data.getLadleRecordKey()) + "  "
      + data.getWheelSerial() + "  "
      + getDoubleValue(data.getC(),  2, 3)
      + getDoubleValue(data.getMn(), 2, 3)
      + getDoubleValue(data.getP(),  3, 3)
      + getDoubleValue(data.getS() , 3, 3)
      + getDoubleValue(data.getSi(), 2, 4)
      + getDoubleValue(data.getNi(), 2, 4)
      + getDoubleValue(data.getCr(), 2, 4)
      + getDoubleValue(data.getCu(), 3, 4)
      + getDoubleValue(data.getMo(), 3, 3)
      + getDoubleValue(data.getV(),  3, 4)
      + getDoubleValue(data.getAl(), 3, 3)
      + getDoubleValue(data.getTi(), 3, 3)
      + getDoubleValue(data.getNb(), 4, 6)
      + getDoubleValue(data.getTapeSize(), 0, 5)
      + data.getBrinnelReading();
    return line;
  }

  // 生成产品编号清单每行数据
  private String generateNoListLine(Certificate certificate) {
    String line = "  " + certificate.getWheelSerial() + "  ";
    return line;
  }
}
