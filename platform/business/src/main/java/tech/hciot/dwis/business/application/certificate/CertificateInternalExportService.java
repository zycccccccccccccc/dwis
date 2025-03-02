package tech.hciot.dwis.business.application.certificate;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.ExcelParameter;
import tech.hciot.dwis.business.domain.model.Certificate;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;

@Service
@Slf4j
public class CertificateInternalExportService extends CertificateExportService implements CertficateExporter {

  @Autowired
  private CertificateService certificateService;

  @Override
  public String certficateName() {
    return "CE1";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-internal.xlsx";
  }

  // 内容部分分成几列
  @Override
  protected int certContentColumnSum(int sheetId) {
    if (sheetId == 0) {
      return 3;
    } else {
      return 1;
    }
  }

  // 更新单元格的值
  @Override
  protected void updateCellValue(Cell cell, CertificateInfo certificateInfo, int pageSum, ExcelParameter excelParameter) {
    super.updateCellValue(cell, certificateInfo, pageSum, excelParameter);
    if ("$memoInternal".equals(ExcelUtil.getCellStringValue(cell))) { // 备注
      String memoInternal;
      if ("HFZ915".equals(certificateInfo.getDesign())) {
        memoInternal = "备注：装用于GQ80E型货车进行运用考核。";
      } else if (certificateInfo.getBalanceS() != null && certificateInfo.getBalanceS().startsWith("E")) {
        memoInternal = "备注：装运车号：" + certificateInfo.getTrainNo() + " 静平衡试验：合格。抽样产品编号：";
      } else {
        memoInternal = "备注：装运车号：" + certificateInfo.getTrainNo() + " 静平衡试验：---。抽样产品编号：";
      }
      cell.setCellValue(memoInternal);

    } else if ("$memoInternal2".equals(ExcelUtil.getCellStringValue(cell))) { // 备注
      String memoInternal;
      if ("HFZ915".equals(certificateInfo.getDesign())) {
        memoInternal = "备注：装用于GQ80E型货车进行运用考核。";
      } else if (certificateInfo.getBalanceS() != null && certificateInfo.getBalanceS().startsWith("E")) {
        memoInternal = "备注：装运车号：" + certificateInfo.getTrainNo() + " 静平衡试验：合格。";
      } else {
        memoInternal = "备注：装运车号：" + certificateInfo.getTrainNo() + " 静平衡试验：---。";
      }
      cell.setCellValue(memoInternal);

    } else if ("$sampleWheelList".equals(ExcelUtil.getCellStringValue(cell))) { // 抽样产品编号
      List<String> sampleWheelList = certificateService.getSampleWheelList(certificateInfo.getShippedNo());
      String sampleWheelListStr = "";
      for (int i = 0; i < sampleWheelList.size(); i ++) {
        sampleWheelListStr = sampleWheelListStr + sampleWheelList.get(i) + "  ";
        if ((i + 1) % 2 == 0) {
          sampleWheelListStr += "\n";
        }
        if (i == 7) {
          break;
        }
      }
      cell.setCellValue(sampleWheelListStr);
    }
  }

  // 根据sheet编号生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data, int sheetId) {
    if (sheetId == 0) {
      return generateNoListLine(data);
    } else if (sheetId == 1){
      return generateCertLine(data);
    } else {
      return "";
    }
  }

  // 生成合格证每行数据
  private String generateNoListLine(Certificate certificate) {
    String line = "  " + certificate.getWheelSerial() + "     " + certificate.getCheckCode() + "      ";
    return line;
  }

  // 生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data) {
    BigDecimal crMoNi = computeCrMoNi(data);
    String line = String.format("%-20s", data.getLadleRecordKey()) + "      "
      + data.getWheelSerial() + "       "
      + getDoubleValue(data.getC(),  2, 4)
      + getDoubleValue(data.getSi(), 2, 5)
      + getDoubleValue(data.getMn(), 2, 4)
      + getDoubleValue(data.getP(),  3, 3)
      + getDoubleValue(data.getS() , 3, 4)
      + getDoubleValue(data.getCr(), 2, 4)
      + getDoubleValue(data.getCu(), 3, 3)
      + getDoubleValue(data.getMo(), 3, 3)
      + getDoubleValue(data.getNi(), 2, 3)
      + getDoubleValue(data.getV(),  3, 6)
      + getDoubleValue(crMoNi, 3, 3);
    return line;
  }
}
