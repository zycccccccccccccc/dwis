package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import tech.hciot.dwis.business.application.AuditDetailService;
import tech.hciot.dwis.business.domain.model.AuditDetail;
import tech.hciot.dwis.business.domain.model.AuditResult;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AuditRequest;

@RestController
@RequestMapping(value = "/auditdetail")
@Api(tags = "二维码校验记录")
public class AuditDetailController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private AuditDetailService auditDetailService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询二维码校验记录列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<AuditDetail> find(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    Page<AuditDetail> page = auditDetailService.find(operator, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "插入二维码校验记录")
  @Log(name = "插入二维码校验记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void add(@Validated @RequestBody @Request AuditRequest auditRequest,
                  Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    auditDetailService.add(auditRequest.getRequest(), auditRequest.getAuditBatch(), operator);
  }

  @GetMapping(value = "audit", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "二维码校验")
  @PreAuthorize("isAuthenticated()")
  public List<AuditResult> audit(@RequestParam String auditBatch) {
    return auditDetailService.audit(auditBatch);
  }

  @GetMapping(value = "audit/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "二维码校验结果导出")
  @PreAuthorize("isAuthenticated()")
  public void auditExport(
      @RequestParam String auditBatch,
      HttpServletResponse response) {
    auditDetailService.export(auditBatch, response);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑二维码校验记录")
  @Log(name = "编辑二维码校验记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
                     @RequestBody @Request AuditDetail request) {
    auditDetailService.modify(id, request);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除二维码校验记录")
  @Log(name = "删除二维码校验记录", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    auditDetailService.delete(id);
  }
}
