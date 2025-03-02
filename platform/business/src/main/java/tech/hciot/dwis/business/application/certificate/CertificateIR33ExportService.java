package tech.hciot.dwis.business.application.certificate;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.model.Certificate;

@Service
@Slf4j
public class CertificateIR33ExportService extends CertificateExportService implements CertficateExporter {

  @Override
  public String certficateName() {
    return "CE2";
  }

  @Override
  protected String certificateFileName() {
    return "certificate-IR33.xlsx";
  }

  // 生成合格证每行数据
  @Override
  protected String generateCertLine(Certificate data) {
    BigDecimal crMoNi = computeCrMoNi(data);
    String line = String.format("%-23s", data.getLadleRecordKey())
      + data.getWheelSerial() + "       "
      + getDoubleValue(data.getC(),  2, 5)
      + getDoubleValue(data.getMn(), 2, 3)
      + getDoubleValue(data.getP(),  3, 3)
      + getDoubleValue(data.getS() , 3, 3)
      + getDoubleValue(data.getSi(), 2, 3)
      + getDoubleValue(data.getNi(), 2, 3)
      + getDoubleValue(data.getCr(), 2, 3)
      + getDoubleValue(data.getMo(), 3, 5)
      + getDoubleValue(crMoNi, 3, 8)
      + getDoubleValue(data.getTapeSize(), 1, 3)
      + data.getBrinnelReading() + "    "
      + data.getBatchNo();
    return line;
  }
}
