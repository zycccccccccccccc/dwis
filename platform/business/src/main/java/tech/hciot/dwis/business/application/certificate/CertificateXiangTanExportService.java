package tech.hciot.dwis.business.application.certificate;

import java.math.BigDecimal;
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
public class CertificateXiangTanExportService extends CertificateExportService implements CertficateExporter {

  @Override
  public String certficateName() {
    return "CE8";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-xiangtan.xlsx";
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
        sheet.getRow(row).getCell(column + 1).setCellValue("抗拉强度Rm≥910N/mm2");
        sheet.getRow(row + 1).getCell(column + 1).setCellValue("延伸率 A5≥5%");
        sheet.getRow(row + 2).getCell(column + 1).setCellValue("常温冲击功KU2≥12J");
        sheet.getRow(row + 3).getCell(column + 1).setCellValue("断面硬度≥265HBW");
        sheet.getRow(row + 4).getCell(column + 1).setCellValue("表面硬度277-341HBW");
        sheet.getRow(row + 4).getCell(column).setCellValue("ZL-B");
      }
    }
    super.updateCellValue(cell, certificateInfo, pageSum, excelParameter);
  }

  // 生成合格证每行数据
  protected String generateCertLine(Certificate data) {
    BigDecimal crMoNi = computeCrMoNi(data);
    String line = String.format("%-22s", data.getLadleRecordKey())
      + data.getWheelSerial() + "  "
      + getDoubleValue(data.getC(),  2, 3)
      + getDoubleValue(data.getSi(), 2, 3)
      + getDoubleValue(data.getMn(), 2, 2)
      + getDoubleValue(data.getP(),  3, 2)
      + getDoubleValue(data.getS() , 3, 2)
      + getDoubleValue(data.getCr(), 2, 3)
      + getDoubleValue(data.getCu(), 3, 3)
      + getDoubleValue(data.getMo(), 3, 2)
      + getDoubleValue(data.getNi(), 2, 2)
      + getDoubleValue(data.getV(),  3, 3)
      + getDoubleValue(crMoNi, 3, 8)
      + data.getBrinnelReading() + "            "
      + getDoubleValue(data.getTapeSize(), 1, 2);
    return line;
  }
}
