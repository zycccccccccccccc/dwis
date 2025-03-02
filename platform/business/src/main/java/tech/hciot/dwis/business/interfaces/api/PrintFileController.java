package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.PrintFileService;

@RestController
@RequestMapping(value = "/printfile")
@Api(tags = "打印电子文档业务")
public class PrintFileController {

  @Autowired
  private PrintFileService printFileService;

  @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "导出文档")
  public void export(
      @RequestParam String shippedNo,
      @RequestParam String code,
      HttpServletResponse response) {
    printFileService.export(shippedNo, code, response);
  }

}
