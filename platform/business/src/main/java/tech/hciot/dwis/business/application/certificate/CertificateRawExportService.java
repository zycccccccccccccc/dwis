package tech.hciot.dwis.business.application.certificate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.model.Certificate;

@Service
@Slf4j
public class CertificateRawExportService extends CertificateExportService implements CertficateExporter {

  @Override
  public String certficateName() {
    return "CE10";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-raw.xlsx";
  }

  // 生成合格证每行数据
  protected String generateCertLine(Certificate data) {
    String line = "  " + String.format("%-13s", data.getShelfNumber())
      + String.format("%-21s", data.getLadleRecordKey())
      + data.getWheelSerial() + "  "
      + getDoubleValue(data.getC(), 2, 2)
      + getDoubleValue(data.getMn(), 2, 2)
      + getDoubleValue(data.getP(),  3, 2)
      + getDoubleValue(data.getS() , 3, 2)
      + getDoubleValue(data.getSi(), 2, 2)
      + getDoubleValue(data.getNi(), 2, 2)
      + getDoubleValue(data.getCr(), 2, 2)
      + getDoubleValue(data.getCu(), 3, 2)
      + getDoubleValue(data.getMo(), 3, 2)
      + getDoubleValue(data.getV(),  3, 2)
      + getDoubleValue(data.getAl(), 3, 2)
      + getDoubleValue(data.getTi(), 3, 2)
      + getDoubleValue(data.getNb(), 4, 2)
      + getDoubleValue(data.getH(),  3, 2);
    return line;
  }
}
