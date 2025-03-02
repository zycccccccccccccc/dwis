package tech.hciot.dwis.business.application.certificate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.model.Certificate;

@Service
@Slf4j
public class CertificateThinRimExportService extends CertificateExportService implements CertficateExporter {

  @Override
  public String certficateName() {
    return "CE13";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-thinrim.xlsx";
  }

  // 内容部分分成几列
  @Override
  protected int certContentColumnSum(int sheetId) {
    return 3;
  }

  // 生成合格证每行数据
  protected String generateCertLine(Certificate data) {
    String line = "    " + data.getWheelSerial() + "   "
      + getDoubleValue(data.getC(),  2, 2)
      + getDoubleValue(data.getMn(), 2, 2)
      + getDoubleValue(data.getP(),  3, 2)
      + getDoubleValue(data.getS() , 3, 2)
      + getDoubleValue(data.getSi(), 2, 4);
    return line;
  }
}
