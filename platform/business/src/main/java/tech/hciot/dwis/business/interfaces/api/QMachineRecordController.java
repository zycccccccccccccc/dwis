package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.QMachineRecordService;
import tech.hciot.dwis.business.domain.model.QMachineRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/qmachine")
@Api(tags = "去重加工日志")
public class QMachineRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private QMachineRecordService qMachineRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询去重加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<QMachineRecord> find(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String holdCode,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<QMachineRecord> page = qMachineRecordService.find(machineNo, operatorId, isInspector, wheelSerial, holdCode,
      currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/machinecount", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "加工数量")
  @PreAuthorize("isAuthenticated()")
  public Integer machineCount(@RequestParam Integer machineNo, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    return qMachineRecordService.machineCount(machineNo, operator);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加去重加工日志")
  @Log(name = "添加去重加工日志", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public QMachineRecord add(@Validated @RequestBody @Request QMachineRecord request, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    request.setOperator(user.getOperatorId());
    return qMachineRecordService.add(request);
  }
}
