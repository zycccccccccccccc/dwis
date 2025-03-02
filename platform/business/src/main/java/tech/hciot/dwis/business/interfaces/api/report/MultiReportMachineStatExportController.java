package tech.hciot.dwis.business.interfaces.api.report;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.report.MultiReportMachineStatExportService;
import tech.hciot.dwis.business.application.report.MultiReportMachineStatService;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static tech.hciot.dwis.business.application.report.MultiReportMachineStatExportService.*;

@RestController
@RequestMapping(value = "/report/composite/machine")
@Api(tags = "综合查询业务-机加统计")
public class MultiReportMachineStatExportController {

  @Autowired
  private MultiReportMachineStatService multiReportMachineStatService;

  @Autowired
  private MultiReportMachineStatExportService multiReportMachineStatExportService;

  @Autowired
  private ReportAssembler assembler;

  @GetMapping(value = "/staff/detail/export")
  @ApiOperation(value = "5.1综合查询业务-机加统计-员工加工明细导出")
  @PreAuthorize("isAuthenticated()")
  public void staffDetailExport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam String staffId,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, staffId);
    multiReportMachineStatExportService.export(MACHINE_STAT_STAFF_DETAIL, parameterMap, response);
  }

  @GetMapping(value = "/staff/quantity/export")
  @ApiOperation(value = "5.2综合查询业务-机加统计-员工加工数量导出")
  @PreAuthorize("isAuthenticated()")
  public void staffQuantityExport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    multiReportMachineStatExportService.export(MACHINE_STAT_STAFF_QUANTITY, parameterMap, response);
  }

  @GetMapping(value = "/machine/quantity/export")
  @ApiOperation(value = "5.3综合查询业务-机加统计-机床加工数量导出")
  @PreAuthorize("isAuthenticated()")
  public void machineQuantityExport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String shiftNo,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, shiftNo);
    multiReportMachineStatExportService.export(MACHINE_STAT_MACHINE_QUANTITY, parameterMap, response);
  }

  @GetMapping(value = "/machine/rework/export")
  @ApiOperation(value = "5.4综合查询业务-机加统计-机加返修车轮明细导出")
  @PreAuthorize("isAuthenticated()")
  public void machineReworkExport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    multiReportMachineStatExportService.export(MACHINE_STAT_MACHINE_REWORK, parameterMap, response);
  }

  @GetMapping(value = "/machine/machine-staff/export")
  @ApiOperation(value = "5.5综合查询业务-机加统计-机加数量与返工(员工)")
  @PreAuthorize("isAuthenticated()")
  public void machineStaffExport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    multiReportMachineStatExportService.export(MACHINE_STAT_MACHINE_STAFF, parameterMap, response);
  }

  @GetMapping(value = "/machine/machine-tool/export")
  @ApiOperation(value = "5.6综合查询业务-机加统计-机加数量与返工(机床)")
  @PreAuthorize("isAuthenticated()")
  public void machineToolExport(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    multiReportMachineStatExportService.export(MACHINE_STAT_MACHINE_TOOL, parameterMap, response);
  }

  @GetMapping(value = "/machine/jmachine8/export")
  @ApiOperation(value = "5.7综合查询业务-机加统计-基面漏加6导出")
  @PreAuthorize("isAuthenticated()")
  public void jMachine8Export(
      @RequestParam String beginDate,
      @RequestParam String endDate,
    HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    multiReportMachineStatExportService.export(MACHINE_STAT_JMACHINE8, parameterMap, response);
  }

  @GetMapping(value = "/machine/reworkStat/export")
  @ApiOperation(value = "5.8综合查询业务-机加统计-机加返修轮数统计导出")
  @PreAuthorize("isAuthenticated()")
  public void machineReworkStatExport(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    multiReportMachineStatExportService.export(MACHINE_STAT_MACHINE_REWORKSTAT, parameterMap, response);
  }

  @GetMapping(value = "/machine/roundnessrecheck/export")
  @ApiOperation(value = "5.9 综合查询业务-机加统计-踏面圆度复检")
  @Log(name = "圆度复检车轮明细")
  @PreAuthorize("isAuthenticated()")
  public void roundnessRecheck(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForMachine(beginDate, endDate, null);
    multiReportMachineStatExportService.export(MACHINE_STAT_ROUNDNESS_RECHECK, parameterMap, response);
  }
}
