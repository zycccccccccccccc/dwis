package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.KMachineRecordService;
import tech.hciot.dwis.business.domain.model.KMachineRecord;
import tech.hciot.dwis.business.domain.model.TMachineRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/kmachine")
@Api(tags = "镗孔加工日志")
public class KMachineRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private KMachineRecordService kMachineRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询镗孔加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<KMachineRecord> find(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) Integer ks1,
      @RequestParam(required = false) Integer ks2,
      @RequestParam(required = false) Integer isCheck,
      @RequestParam(required = false) Integer isRework,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<KMachineRecord> page = kMachineRecordService.find(machineNo, operatorId, isInspector, wheelSerial, location,
      ks1, ks2, isCheck, isRework, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/machinecount", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "加工数量")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Integer> machineCount(@RequestParam Integer machineNo, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    return kMachineRecordService.machineCount(machineNo, operator);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加镗孔加工日志")
  @Log(name = "添加镗孔加工日志", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public KMachineRecord add(@Validated @RequestBody @Request KMachineRecord request, Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    request.setOperator(user.getOperatorId());
    return kMachineRecordService.add(request);
  }

  @GetMapping(value = "kmachinerecord", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询镗孔加工数据")
  @PreAuthorize("isAuthenticated()")
  public List<KMachineRecord> findKMachineRecordList(
          @RequestParam(required = false) String machineNo,
          @RequestParam(required = true) String wheelSerial) {
    return kMachineRecordService.findByWheelSerial(machineNo, wheelSerial);
  }

  @PutMapping(value = "/{id}")
  @ApiOperation(value = "修改镗孔加工记录")
  @Log(name = "修改镗孔加工记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyRecord(@PathVariable @Id Integer id, @RequestBody @Request KMachineRecord record) {
    kMachineRecordService.modifyRecord(id, record);
  }
}
