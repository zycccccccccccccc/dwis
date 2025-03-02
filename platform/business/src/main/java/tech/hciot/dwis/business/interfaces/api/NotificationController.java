package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.NotificationService;
import tech.hciot.dwis.business.domain.model.Notification;
import tech.hciot.dwis.business.domain.model.NotificationInfo;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/notification")
@Api(tags = "通知信息")
public class NotificationController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private NotificationService notificationService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询通知信息列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<NotificationInfo> find(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String accountId = user.getAccountId();
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<NotificationInfo> page = notificationService.find(accountId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/mgr", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "管理员查询通知信息列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Notification> findByMgr(
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<Notification> page = notificationService.findForMgr(currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加通知信息")
  @PreAuthorize("isAuthenticated()")
  public Notification add(@Validated @RequestBody @Request Notification request, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    request.setAuthor(operator);
    return notificationService.add(request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑通知信息")
  @PreAuthorize("isAuthenticated()")
  public Notification modify(@PathVariable @Id Integer id,
                     @RequestBody @Request Notification request,
                             HttpServletRequest servletRequest) {
    String hostIp;
    if (servletRequest.getHeader("x-forwarded-for") == null) {
      hostIp = servletRequest.getRemoteAddr();
    }
    hostIp = servletRequest.getHeader("x-forwarded-for");

    return notificationService.modify(id, request);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除通知信息")
  @Log(name = "删除通知信息", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    notificationService.delete(id);
  }

  @PutMapping(value = "/publish/{id}")
  @ApiOperation(value = "发布通知信息")
  @PreAuthorize("isAuthenticated()")
  public void publish(@PathVariable @Id Integer id) {
    notificationService.publish(id);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查看通知信息详情")
  @PreAuthorize("isAuthenticated()")
  public Notification findById(@PathVariable Integer id, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String accountId = user.getAccountId();
    return notificationService.findById(id, accountId);
  }
}
