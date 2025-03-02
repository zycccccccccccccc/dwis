package tech.hciot.dwis.business.interfaces.api.report;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.report.MultiReportMachineStatExportService;
import tech.hciot.dwis.business.application.report.MultiReportMachineStatService;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/report/composite/machine")
@Api(tags = "综合查询业务-机加统计")
public class MultiReportMachineStatController {

  @Autowired
  private MultiReportMachineStatService multiReportMachineStatService;

  @Autowired
  private MultiReportMachineStatExportService multiReportMachineStatExportService;

  @Autowired
  private ReportAssembler assembler;

  @GetMapping(value = "/staff/detail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.1综合查询业务-机加统计-员工加工明细")
  @PreAuthorize("isAuthenticated()")
  public List<StaffDetailData> staffDetail(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam String staffId) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, staffId);
    return multiReportMachineStatService.staffDetail(parameterMap);
  }

  @GetMapping(value = "/staff/quantity", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.2综合查询业务-机加统计-员工加工数量")
  @PreAuthorize("isAuthenticated()")
  public StaffQuantityData staffQuantity(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    return multiReportMachineStatService.staffQuantity(parameterMap);
  }

  @GetMapping(value = "/machine/quantity", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.3综合查询业务-机加统计-机床加工数量")
  @PreAuthorize("isAuthenticated()")
  public StaffQuantityData machineQuantity(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String shiftNo) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, shiftNo);
    return multiReportMachineStatService.machineQuantity(parameterMap);
  }

  @GetMapping(value = "/machine/rework", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.4综合查询业务-机加统计-机加返修车轮明细")
  @PreAuthorize("isAuthenticated()")
  public List<ReworkData> machineRework(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    return multiReportMachineStatService.machineRework(parameterMap);
  }

  @GetMapping(value = "/machine/machine-staff", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.5综合查询业务-机加统计-机加数量与返工(员工)")
  @PreAuthorize("isAuthenticated()")
  public JSON machineStaff(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    return multiReportMachineStatService.machineStaff(parameterMap);
  }

  @GetMapping(value = "/machine/machine-tool", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.6综合查询业务-机加统计-机加数量与返工(机床)")
  @PreAuthorize("isAuthenticated()")
  public JSON machineTool(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    return multiReportMachineStatService.machineTool(parameterMap);
  }

  @GetMapping(value = "/machine/jmachine8", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.7综合查询业务-机加统计-基面漏加6")
  @PreAuthorize("isAuthenticated()")
  public Map<String, List<JMachine8>> jMachine8(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    return multiReportMachineStatService.jMachine8(parameterMap);
  }

  @GetMapping(value = "/machine/reworkStat", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "5.8综合查询业务-机加统计-机加返修轮数统计")
  @PreAuthorize("isAuthenticated()")
  public List<ReworkStatData> machineReworkStat(
    @RequestParam String beginDate,
    @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate,  null);
    return multiReportMachineStatService.machineReworkStat(parameterMap);
  }
}