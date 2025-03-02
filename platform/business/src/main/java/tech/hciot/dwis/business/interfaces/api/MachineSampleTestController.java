package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.JMachineRecordService;
import tech.hciot.dwis.business.application.KMachineRecordService;
import tech.hciot.dwis.business.application.QMachineRecordService;
import tech.hciot.dwis.business.application.SampleTestService;
import tech.hciot.dwis.business.application.TMachineRecordService;
import tech.hciot.dwis.business.application.WMachineRecordService;
import tech.hciot.dwis.business.domain.model.JMachineRecord;
import tech.hciot.dwis.business.domain.model.KMachineRecord;
import tech.hciot.dwis.business.domain.model.QMachineRecord;
import tech.hciot.dwis.business.domain.model.TMachineRecord;
import tech.hciot.dwis.business.domain.model.WMachineRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.SampleTestRequest;

@RestController
@RequestMapping(value = "/machinesample")
@Api(tags = "机加工长抽验业务")
public class MachineSampleTestController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private JMachineRecordService jMachineRecordService;

  @Autowired
  private TMachineRecordService tMachineRecordService;

  @Autowired
  private KMachineRecordService kMachineRecordService;

  @Autowired
  private QMachineRecordService qMachineRecordService;

  @Autowired
  private WMachineRecordService wMachineRecordService;

  @Autowired
  private SampleTestService sampleTestService;

  @GetMapping(value = "jmachine", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询基面加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<JMachineRecord> findJMachineRecordList(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<JMachineRecord> page = jMachineRecordService.find(machineNo, operatorId, isInspector, null,
      null, null, null, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "tmachine", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询踏面加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<TMachineRecord> findTMachineRecordList(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<TMachineRecord> page = tMachineRecordService.find(machineNo, operatorId, isInspector, null,
      null, null, null, null, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "kmachine", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询镗孔加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<KMachineRecord> findKMachineRecordList(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<KMachineRecord> page = kMachineRecordService.find(machineNo, operatorId, isInspector, null, null,
      null, null, null, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "qmachine", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询去重加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<QMachineRecord> findQMachineRecordList(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<QMachineRecord> page = qMachineRecordService.find(machineNo, operatorId, isInspector, null, null,
      currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "wmachine", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询外幅板加工日志列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<WMachineRecord> findWMachineRecordList(
      @RequestParam(required = true) String machineNo,
      @RequestParam(required = true) Boolean isInspector,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    Page<WMachineRecord> page = wMachineRecordService.find(machineNo, operatorId, isInspector, null, null,
      null, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "nolist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "机床号列表")
  @PreAuthorize("isAuthenticated()")
  public List<Integer> machineNoList(@ApiParam(name = "checkType", value = "jMachine-基面抽验，" +
    "tMachine-踏面，kMachine-镗孔，qMachine-去重，wMachine-外辐板")
                                     @RequestParam String checkType) {
    return sampleTestService.machineNoList(checkType);
  }

  @RequestMapping(value = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "质检工长抽验")
  @Log(name = "质检工长抽验", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void check(@ApiParam(name = "checkType", value = "jMachine-基面抽验，" +
    "tMachine-踏面，kMachine-镗孔，qMachine-去重，wMachine-外辐板")
                    @RequestParam String checkType,
                    @Validated @RequestBody @Request List<SampleTestRequest> request) {
    sampleTestService.check(checkType, request);
  }
}
