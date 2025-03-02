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
public class CertificatePAK950ExportService extends CertificateExportService implements CertficateExporter {

  @Override
  public String certficateName() {
    return "CE3";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-PAK950.xlsx";
  }

  // 内容部分分成几列
  @Override
  protected int certContentColumnSum(int sheetId) {
    if (sheetId == 0) {
      return 6;
    } else {
      return 1;
    }
  }

  // 更新单元格的值
  @Override
  protected void updateCellValue(Cell cell, CertificateInfo certificateInfo, int pageSum, ExcelParameter excelParameter) {
    if ("$design".equals(ExcelUtil.getCellStringValue(cell))) { // 规格型号
      cell.setCellValue(certificateInfo.getDesign());
      int row = cell.getAddress().getRow();
      int column = cell.getAddress().getColumn();
      Sheet sheet = cell.getRow().getSheet();
      if ("B".equals(certificateInfo.getSteelClass())) { // 抗拉强度
        sheet.getRow(row + 4).getCell(column + 1).setCellValue("Hardness 277-341HB");
        sheet.getRow(row + 5).getCell(column + 1).setCellValue("表面硬度277-341HB");
      }
    }
    super.updateCellValue(cell, certificateInfo, pageSum, excelParameter);
  }

  // 根据sheet编号生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data, int sheetId) {
    if (sheetId == 0) {
      return generateNoListLine(data);
    } else {
      return generateCertLine(data);
    }
  }

  // 生成合格证每行数据
  private String generateNoListLine(Certificate certificate) {
    String line = "   " + certificate.getWheelSerial() + " ";
    return line;
  }

  // 生成合格证每行数据
  protected String generateCertLine(Certificate data) {
    String line = String.format("%-23s", data.getLadleRecordKey())
      + data.getWheelSerial() + "     "
      + getDoubleValue(data.getC(),  2, 2)
      + getDoubleValue(data.getMn(), 2, 2)
      + getDoubleValue(data.getP(),  3, 2)
      + getDoubleValue(data.getS() , 3, 2)
      + getDoubleValue(data.getSi(), 2, 3)
      + getDoubleValue(data.getNi(), 2, 2)
      + getDoubleValue(data.getCr(), 2, 3)
      + getDoubleValue(data.getCu(), 3, 2)
      + getDoubleValue(data.getMo(), 3, 3)
      + getDoubleValue(data.getV(),  3, 2)
      + getDoubleValue(data.getAl(), 3, 2)
      + getDoubleValue(data.getTi(), 3, 2)
      + getDoubleValue(data.getNb(), 4, 3)
      + getDoubleValue(data.getTapeSize(), 0, 5)
      + data.getBrinnelReading();
    return line;
  }
}
