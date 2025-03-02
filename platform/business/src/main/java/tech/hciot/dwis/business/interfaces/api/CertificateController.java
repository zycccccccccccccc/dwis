package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.certificate.CertificateInternalExportService;
import tech.hciot.dwis.business.application.certificate.CertificateService;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.dto.CertificateInfo;
import tech.hciot.dwis.business.interfaces.dto.CertificatePrintRequest;

@RestController
@RequestMapping(value = "/certificate")
@Api(tags = "打印合格证业务")
public class CertificateController {

  @Autowired
  private CertificateService certificateService;

  @Autowired
  private CertificateInternalExportService certificateExportService;

  @PostMapping(value = "/print", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "打印合格证")
  public void print(@Validated @RequestBody @Request CertificatePrintRequest certificatePrintRequest,
                    HttpServletResponse response, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    certificatePrintRequest.setOpeId(user.getOperatorId());
    CertificateInfo certificateInfo = certificateService.getPrintData(certificatePrintRequest);
    certificateExportService.export(certificatePrintRequest.getCertificateType(), certificateInfo, response);
  }
}
