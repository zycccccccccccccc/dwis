package tech.hciot.dwis.business.application.certificate;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.ExcelParameter;
import tech.hciot.dwis.business.domain.model.Certificate;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;

@Service
@Slf4j
public class CertificateZhuZhouExportService extends CertificateExportService implements CertficateExporter {

  @Override
  public String certficateName() {
    return "CE9";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-zhuzhou.xlsx";
  }

  // 内容部分分成几列
  @Override
  protected int certContentColumnSum(int sheetId) {
    return 3;
  }

  // 根据sheet编号生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data, int sheetId) {
    if (sheetId == 0) {
      return null;
    } else {
      return generateNoListLine(data);
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
        memoInternal = "备注：装运车号：" + certificateInfo.getTrainNo() + " 静平衡试验：合格。";
      } else {
        memoInternal = "备注：装运车号：" + certificateInfo.getTrainNo() + " 静平衡试验：---。";
      }
      cell.setCellValue(memoInternal);

    } else if ("$wheelW".equals(ExcelUtil.getCellStringValue(cell))) { // 轮辋宽度、轮径等
      String wheelWInfo;
      if (certificateInfo.getWheelW() != null && certificateInfo.getWheelW() < 137) {
        wheelWInfo = "轮辋宽度>=135mm and <137mm";
      } else {
        double tapeSize = certificateInfo.getTapeSize().doubleValue();
        if (tapeSize > 836.0 && tapeSize < 840.0) {
          wheelWInfo = "轮径>=836 and <840";
        } else if (tapeSize > 831.0 && tapeSize < 839.0) {
          wheelWInfo = "轮径>=831 and <840";
        } else {
          wheelWInfo = "";
        }
      }
      cell.setCellValue(wheelWInfo);
    }
  }

  @Override
  protected String generateCertLine(Certificate data) {
    return null;
  }

  // 生成产品编号清单每行数据
  private String generateNoListLine(Certificate data) {
    String line = "  " + data.getWheelSerial() + "       " + data.getCheckCode() + "      ";
    return line;
  }
}
