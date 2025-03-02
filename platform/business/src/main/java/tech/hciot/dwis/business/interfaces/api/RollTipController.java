package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.RollTipService;
import tech.hciot.dwis.business.domain.model.RollTip;

@RestController
@RequestMapping(value = "/rolltip")
@Api(tags = "滚动提示")
public class RollTipController {

  @Autowired
  private RollTipService rollTipService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查看滚动提示")
  @PreAuthorize("isAuthenticated()")
  public String rollTip() {
    return rollTipService.find();
  }

  @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑滚动提示")
  @PreAuthorize("isAuthenticated()")
  public void modify(@RequestBody RollTip rollTip) {
    rollTipService.add(rollTip);
  }
}
