package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.JMachineRecordService;
import tech.hciot.dwis.business.domain.model.JMachineRecord;
import tech.hciot.dwis.business.domain.model.MachineParams;
import tech.hciot.dwis.business.domain.model.PourParams;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/jmachine")
@Api(tags = "基面加工日志")
public class JMachineRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private JMachineRecordService jMachineRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询基面加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<JMachineRecord> find(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) Integer js1,
      @RequestParam(required = false) Integer js2,
      @RequestParam(required = false) Integer isCheck,
      @RequestParam(required = false) Integer isRework,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<JMachineRecord> page = jMachineRecordService.find(machineNo, operatorId, isInspector, wheelSerial,
      js1, js2, isCheck, isRework, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/machinecount", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "加工数量")
  @PreAuthorize("isAuthenticated()")
  public Integer machineCount(@RequestParam Integer machineNo, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    return jMachineRecordService.machineCount(machineNo, operator);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加基面加工日志")
  @Log(name = "添加基面加工日志", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public JMachineRecord add(@Validated @RequestBody @Request JMachineRecord request, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    request.setOperator(user.getOperatorId());
    return jMachineRecordService.add(request);
  }

  @GetMapping(value = "jmachinerecord", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询基面加工数据")
  @PreAuthorize("isAuthenticated()")
  public List<JMachineRecord> findJMachineRecordList(
          @RequestParam(required = false) String machineNo,
          @RequestParam(required = true) String wheelSerial) {
    return jMachineRecordService.findByWheelSerial(machineNo, wheelSerial);
  }

  @PutMapping(value = "/{id}")
  @ApiOperation(value = "修改基面加工记录")
  @Log(name = "修改基面加工记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyRecord(@PathVariable @Id Integer id, @RequestBody @Request JMachineRecord record) {
    jMachineRecordService.modifyRecord(id, record);
  }
}
