package tech.hciot.dwis.business.interfaces.api.report;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.report.SingleReportService;

@RestController
@RequestMapping(value = "/report/single")
@Api(tags = "单轮报表业务")
public class SingleReportController {

  @Autowired
  private SingleReportService singleReportService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询单轮报表")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Object> find(@RequestParam String wheelSerial) {
    return singleReportService.find(wheelSerial);
  }

}
