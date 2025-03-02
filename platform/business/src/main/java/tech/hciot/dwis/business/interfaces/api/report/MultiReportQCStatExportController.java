package tech.hciot.dwis.business.interfaces.api.report;

import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.BALANCE_PERCENT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.FINALCHECK_PERCENT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.FINALCHECK_REWORK_WHEEL;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.FINALCHECK_TIMES;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.LADLE_SEQ;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.MACHINE_REWORK;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.PRECHECK_PERCENT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.REWORK_CODE_PERCENT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.REWORK_CODE_SHIFT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.REWORK_WHEEL_REPORT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.SCRAP_CODE_SHIFT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.SHOT_REPORT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.SINGLE_WHEEL_REPORT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.SMALL_TAPE_REPORT;
import static tech.hciot.dwis.business.application.report.MultiReportQcStatExportService.SUMMARY_REPORT;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.report.MultiReportQcStatExportService;

@RestController
@RequestMapping(value = "/report/composite/qc")
@Api(tags = "综合查询业务-质检统计导出")
public class MultiReportQCStatExportController {

  @Autowired
  private MultiReportQcStatExportService multiReportQcStatExportService;

  @Autowired
  private ReportAssembler assembler;

  @GetMapping(value = "/summary/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.1综合查询业务-质检统计-汇总查询导出")
  @PreAuthorize("isAuthenticated()")
  public void summary(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String staffId,
      @RequestParam(required = false) String shiftNo,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, staffId, shiftNo);
    multiReportQcStatExportService.export(SUMMARY_REPORT, parameterMap, response);
  }

  @GetMapping(value = "/reworkscrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.2综合查询业务-质检统计-返废代码轮号导出")
  @PreAuthorize("isAuthenticated()")
  public void reworkScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String shiftNo,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, shiftNo);
    multiReportQcStatExportService.export(REWORK_WHEEL_REPORT, parameterMap, response);
  }

  @GetMapping(value = "/shot/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.3综合查询业务-质检统计-纠抛丸导出")
  @PreAuthorize("isAuthenticated()")
  public void shot(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    multiReportQcStatExportService.export(SHOT_REPORT, parameterMap, response);
  }

  @GetMapping(value = "/smalltape/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.4综合查询业务-质检统计-小带尺导出")
  @PreAuthorize("isAuthenticated()")
  public void smallTape(
      @RequestParam String beginDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, null, null, null);
    parameterMap.remove("endDate");
    multiReportQcStatExportService.export(SMALL_TAPE_REPORT, parameterMap, response);
  }

  @GetMapping(value = "/precheckpercent/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.5综合查询业务-质检统计-预检一次通过率导出")
  @PreAuthorize("isAuthenticated()")
  public void preCheckPercent(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    multiReportQcStatExportService.export(PRECHECK_PERCENT, parameterMap, response);
  }

  @GetMapping(value = "/finalcheckpercent/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.6综合查询业务-质检统计-终检一次通过率导出")
  @PreAuthorize("isAuthenticated()")
  public void finalCheckPercent(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String shiftNo,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, shiftNo);
    multiReportQcStatExportService.export(FINALCHECK_PERCENT, parameterMap, response);
  }

  @GetMapping(value = "/balancepercent/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.7综合查询业务-质检统计-一次平衡率导出")
  @PreAuthorize("isAuthenticated()")
  public void balancePercent(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportQcStatExportService.export(BALANCE_PERCENT, parameterMap, response);
  }

  @GetMapping(value = "/reworkpercent/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.8综合查询业务-质检统计-返工代码一次通过率导出")
  @PreAuthorize("isAuthenticated()")
  public void reworkPercent(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> reworkCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null,null);
    parameterMap.put("reworkCode", reworkCode);
    multiReportQcStatExportService.export(REWORK_CODE_PERCENT, parameterMap, response);
  }

  @GetMapping(value = "/wheeldetail/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.9综合查询业务-质检统计-单日轮号明细导出")
  @PreAuthorize("isAuthenticated()")
  public void wheelDetail(
      @RequestParam String beginDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, null, null, null);
    multiReportQcStatExportService.export(SINGLE_WHEEL_REPORT, parameterMap, response);
  }

  @GetMapping(value = "/scrapshift/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.10综合查询业务-质检统计-废品代码分班导出")
  @PreAuthorize("isAuthenticated()")
  public void scrapShift(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> scrapCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null,null);
    parameterMap.put("scrapCode", scrapCode);
    multiReportQcStatExportService.export(SCRAP_CODE_SHIFT, parameterMap, response);
  }

  @GetMapping(value = "/reworkshift/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.11综合查询业务-质检统计-返工代码分班导出")
  @PreAuthorize("isAuthenticated()")
  public void reworkShift(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> reworkCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null,null);
    parameterMap.put("reworkCode", reworkCode);
    multiReportQcStatExportService.export(REWORK_CODE_SHIFT,parameterMap,response);
  }

  @GetMapping(value = "/scrapladle/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.12综合查询业务-质检统计-确废小包顺序导出")
  @PreAuthorize("isAuthenticated()")
  public void scrapLadle(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> scrapCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    parameterMap.put("scrapCode", scrapCode);
    multiReportQcStatExportService.export(LADLE_SEQ, parameterMap, response);
  }

  @GetMapping(value = "/finalcheckrework/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.13综合查询业务-质检统计-终检次数与返工导出")
  @PreAuthorize("isAuthenticated()")
  public void finalCheckRework(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> reworkCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null,null);
    parameterMap.put("reworkCode", reworkCode);
    multiReportQcStatExportService.export(FINALCHECK_TIMES,parameterMap,response);
  }

  @GetMapping(value = "/machinerework/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.14综合查询业务-质检统计-机床返修统计导出")
  @PreAuthorize("isAuthenticated()")
  public void machineRework(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> reworkCode,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    parameterMap.put("reworkCode", reworkCode);
    multiReportQcStatExportService.export(MACHINE_REWORK,parameterMap,response);
  }

  @GetMapping(value = "/finalreworkdetail/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.15综合查询业务-质检统计-终检返工车轮加工明细导出")
  @PreAuthorize("isAuthenticated()")
  public void finalReworkDetail(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    multiReportQcStatExportService.export(FINALCHECK_REWORK_WHEEL,parameterMap,response);
  }
}
