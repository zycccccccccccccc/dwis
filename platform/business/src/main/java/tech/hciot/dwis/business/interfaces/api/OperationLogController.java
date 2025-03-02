package tech.hciot.dwis.business.interfaces.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.business.application.OperationLogService;
import tech.hciot.dwis.business.infrastructure.log.domain.model.OperationLog;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;


@RestController
@RequestMapping("/operationlog")
@Api(tags = "操作日志")
public class OperationLogController {

  @Autowired
  private OperationLogService operationLogService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询操作日志 ")
  public PageDataResponse<OperationLog> find(@RequestParam(required = false) String username,
      @RequestParam(required = false) String operationName,
      @RequestParam(required = false) Long operationTimeStart,
      @RequestParam(required = false) Long operationTimeEnd,
      @RequestParam(required = false) Boolean success,
      @RequestParam(required = false) String errorDesc,
      @RequestParam(required = false) String operationType,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      Principal principal) {
    JwtTokenUser user = (JwtTokenUser) ((OAuth2Authentication) principal).getUserAuthentication()
        .getPrincipal();
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(operationLogService
        .findAllOperationLog(username, operationName, operationTimeStart, operationTimeEnd, success, errorDesc, operationType,
            currentPage, pageSize, user));
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")

  public OperationLog findById(@PathVariable("id") String id) {
    return operationLogService.findById(id).get();
  }
}
