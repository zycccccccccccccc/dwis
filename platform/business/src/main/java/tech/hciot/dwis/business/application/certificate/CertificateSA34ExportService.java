package tech.hciot.dwis.business.application.certificate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.ExcelParameter;
import tech.hciot.dwis.business.domain.model.Certificate;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;

@Service
@Slf4j
public class CertificateSA34ExportService extends CertificateExportService implements CertficateExporter {

  @Autowired
  private CertificateService certificateService;

  @Override
  public String certficateName() {
    return "CE5";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-SA34.xlsx";
  }

  @Override
  protected void generateCertificate(Sheet sheet, CertificateInfo certificateInfo, ExcelParameter excelParameter, int sheetId) {
    List<Certificate> saCertificateList;
    if (sheetId == 1) {
      String shippedNo = certificateInfo.getShippedNo();
      List saList = certificateService.findSa34(shippedNo);
      saCertificateList = new ArrayList<>();
      for (int i = 0; i < saList.size(); i++) {
        Certificate saCertificate = Certificate.builder()
          .no(String.valueOf(i))
          .wheelSerial((String) ((Map) saList.get(i)).get("wheel_serial"))
          .grindDepth(((Map) saList.get(i)).get("grind_depth").toString())
          .build();
        saCertificateList.add(saCertificate);
      }
      if (saList.isEmpty()) {
        Certificate saCertificate = Certificate.builder()
          .no(" ")
          .wheelSerial(" ")
          .grindDepth(" ")
          .build();
        saCertificateList.add(saCertificate);
      }
      certificateInfo.setCertificateList(saCertificateList);
    }
    super.generateCertificate(sheet, certificateInfo, excelParameter, sheetId);
  }
  // 内容部分分成几列
  @Override
  protected int certContentColumnSum(int sheetId) {
    if (sheetId == 0) {
      return 1;
    } else {
      return 4;
    }
  }

  // 更新单元格的值
  @Override
  protected void updateCellValue(Cell cell, CertificateInfo certificateInfo, int pageSum, ExcelParameter excelParameter) {
    super.updateCellValue(cell, certificateInfo, pageSum, excelParameter);
    if ("$sa34TitleEN".equals(ExcelUtil.getCellStringValue(cell))) { // 英文标题
      String sa34TitleEN = "List of Wheels with Grinding Depth between 2.5-3.0mm on Artificate "
        + certificateInfo.getShippedNo();
      cell.setCellValue(sa34TitleEN);
    } else if ("$sa34TitleCH".equals(ExcelUtil.getCellStringValue(cell))) { // 英文标题
      String sa34TitleCH = "合格证号" + certificateInfo.getShippedNo()
        + "打磨深度在2.5mm-3.0mm范围内的车轮列表";
      cell.setCellValue(sa34TitleCH);
    }
  }

  // 根据sheet编号生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data, int sheetId) {
    if (sheetId == 0) {
      return generateCertLine(data);
    } else {
      return generateWheelSerialLine(data);
    }
  }

  // 生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data) {
    String line = String.format("%-10s", data.getShelfNumber())
      + String.format("%-20s", data.getLadleRecordKey())
      + data.getWheelSerial() + "   "
      + getDoubleValue(data.getC(), 2, 3)
      + getDoubleValue(data.getMn(), 2, 3)
      + getDoubleValue(data.getP(),  3, 2)
      + getDoubleValue(data.getS() , 3, 2)
      + getDoubleValue(data.getSi(), 2, 2)
      + getDoubleValue(data.getNi(), 2, 2)
      + getDoubleValue(data.getCr(), 2, 3)
      + getDoubleValue(data.getCu(), 3, 3)
      + getDoubleValue(data.getMo(), 3, 2)
      + getDoubleValue(data.getV(),  3, 3)
      + getDoubleValue(data.getAl(), 3, 2)
      + getDoubleValue(data.getTi(), 3, 3)
      + getDoubleValue(data.getNb(), 4, 2)
      + getDoubleValue(data.getH(),  3, 3)
      + getDoubleValue(data.getTapeSize(), 1, 3)
      + data.getBrinnelReading();
    return line;
  }

  // 生成车轮列表每行数据
  private String generateWheelSerialLine(Certificate data) {
    String line = "   " + String.format("%-6s", data.getNo())
      + String.format("%-12s", data.getWheelSerial())
      + String.format("%-8s", data.getGrindDepth());
    return line;
  }
}
