package tech.hciot.dwis.business.application.certificate;

import javax.servlet.http.HttpServletResponse;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;

public interface CertficateExporter {
  String certficateName();
  void export(CertificateInfo certificateInfo, HttpServletResponse response);
}
