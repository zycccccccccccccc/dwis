package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.DashboardService;

@RestController
@RequestMapping(value = "/dashboard")
@Api(tags = "首页")
public class DashboardController {

  @Autowired
  private DashboardService dashboardService;

  @GetMapping(value = "/unreadnotify", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取未读通知数量")
  public Integer unreadCount(Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String accountId = user.getAccountId();
    Integer depId = user.getDepId();
    return dashboardService.unreadCount(accountId, depId);
  }
}
