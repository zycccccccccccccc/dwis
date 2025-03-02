package tech.hciot.dwis.business.interfaces.api.report;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.report.MultiReportQcStatService;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.BalancePercentData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.FinalCheckPercentData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.FinalCheckReworkData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.FinalReworkData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.MachineReworkData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.PreCheckPercentData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.ReworkPercentData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.ReworkScrapData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.ReworkShiftData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.ScrapLadleData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.ScrapShiftData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.ShotData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.SmallTapeData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.SummaryData;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.WheelDetail;

@RestController
@RequestMapping(value = "/report/composite/qc")
@Api(tags = "综合查询业务-质检统计")
public class MultiReportQCStatController {

  @Autowired
  private MultiReportQcStatService multiReportQcStatService;

  @Autowired
  private ReportAssembler assembler;

  @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.1综合查询业务-质检统计-汇总查询")
  @PreAuthorize("isAuthenticated()")
  public SummaryData summary(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String staffId,
      @RequestParam(required = false) String shiftNo) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, staffId, shiftNo);
    return multiReportQcStatService.summary(parameterMap);
  }

  @GetMapping(value = "/reworkscrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.2综合查询业务-质检统计-返废代码轮号")
  @PreAuthorize("isAuthenticated()")
  public ReworkScrapData reworkScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String shiftNo) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, shiftNo);
    return multiReportQcStatService.reworkScrap(parameterMap);
  }

  @GetMapping(value = "/shot", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.3综合查询业务-质检统计-纠抛丸")
  @PreAuthorize("isAuthenticated()")
  public List<ShotData> shot(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.shot(parameterMap);
  }

  @GetMapping(value = "/smalltape", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.4综合查询业务-质检统计-小带尺")
  @PreAuthorize("isAuthenticated()")
  public List<SmallTapeData> smallTape(
      @RequestParam String beginDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, null, null, null);
    return multiReportQcStatService.smallTape(parameterMap);
  }

  @GetMapping(value = "/precheckpercent", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.5综合查询业务-质检统计-预检一次通过率")
  @PreAuthorize("isAuthenticated()")
  public PreCheckPercentData preCheckPercent(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.preCheckPercent(parameterMap);
  }

  @GetMapping(value = "/finalcheckpercent", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.6综合查询业务-质检统计-终检一次通过率")
  @PreAuthorize("isAuthenticated()")
  public FinalCheckPercentData finalCheckPercent(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) String shiftNo) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, shiftNo);
    return multiReportQcStatService.finalCheckPercent(parameterMap);
  }

  @GetMapping(value = "/balancepercent", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.7综合查询业务-质检统计-一次平衡率")
  @PreAuthorize("isAuthenticated()")
  public BalancePercentData balancePercent(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    return multiReportQcStatService.balancePercent(parameterMap);
  }

  @GetMapping(value = "/reworkpercent", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.8综合查询业务-质检统计-返工代码一次通过率")
  @PreAuthorize("isAuthenticated()")
  public ReworkPercentData reworkPercent(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> reworkCode) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.reworkPercent(parameterMap, reworkCode);
  }

  @GetMapping(value = "/wheeldetail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.9综合查询业务-质检统计-单日轮号明细")
  @PreAuthorize("isAuthenticated()")
  public List<WheelDetail> wheelDetail(
      @RequestParam String beginDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, null, null, null);
    return multiReportQcStatService.wheelDetail(parameterMap);
  }

  @GetMapping(value = "/scrapshift", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.10综合查询业务-质检统计-废品代码分班")
  @PreAuthorize("isAuthenticated()")
  public ScrapShiftData scrapShift(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> scrapCode) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.scrapShift(parameterMap, scrapCode);
  }

  @GetMapping(value = "/reworkshift", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.11综合查询业务-质检统计-返工代码分班")
  @PreAuthorize("isAuthenticated()")
  public ReworkShiftData reworkShift(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> reworkCode) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.reworkShift(parameterMap, reworkCode);
  }

  @GetMapping(value = "/scrapladle", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.12综合查询业务-质检统计-确废小包顺序")
  @PreAuthorize("isAuthenticated()")
  public ScrapLadleData scrapLadle(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> scrapCode) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.scrapLadle(parameterMap, scrapCode);
  }

  @GetMapping(value = "/finalcheckrework", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.13综合查询业务-质检统计-终检次数与返工")
  @PreAuthorize("isAuthenticated()")
  public FinalCheckReworkData finalCheckRework(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> reworkCode) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.finalCheckRework(parameterMap, reworkCode);
  }

  @GetMapping(value = "/machinerework", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.14综合查询业务-质检统计-机床返修统计")
  @PreAuthorize("isAuthenticated()")
  public MachineReworkData machineRework(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam List<String> reworkCode) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.machineRework(parameterMap, reworkCode);
  }

  @GetMapping(value = "/finalreworkdetail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "4.15综合查询业务-质检统计-终检返工车轮加工明细")
  @PreAuthorize("isAuthenticated()")
  public FinalReworkData finalReworkDetail(
      @RequestParam String beginDate,
      @RequestParam String endDate) {
    Map<String, Object> parameterMap = assembler.parameterMapForQc(beginDate, endDate, null, null);
    return multiReportQcStatService.finalReworkDetail(parameterMap);
  }
}
