package tech.hciot.dwis.business.interfaces.api.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.controlledRecord.ControlledRecordExportService;
import tech.hciot.dwis.business.application.report.MultiReportQAStatService;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.AllConfirmScrapResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ConfirmScrapResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.MachiningStat;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatChart;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelReworkResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelReworkScrapResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAStat;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/report/composite/qa")
@Api(tags = "综合查询业务-质量统计")
public class MultiReportQAStatController {

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private MultiReportQAStatService multiReportQAStatService;

  @Autowired
  private ControlledRecordExportService controlledRecordExportService;

  @GetMapping(value = "qa-stat", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.1 综合查询业务-质量统计-质量统计")
  @PreAuthorize("isAuthenticated()")
  public QAStat qaStat(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    return multiReportQAStatService.qaStat(parameterMap);
  }

  @GetMapping(value = "qa-stat/detail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.1 综合查询业务-质量统计-质量统计-详情(报表)")
  @PreAuthorize("isAuthenticated()")
  public JSON qaStatDetail(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportQAStatService.qaStatDetail(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "qa-stat/chart", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.1 综合查询业务-质量统计-质量统计-详情(图表)")
  @PreAuthorize("isAuthenticated()")
  public List<QAStat> qaStatChart(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportQAStatService.qaStatChart(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "prod-stat", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.2 综合查询业务-质量统计-产量统计-图表")
  @PreAuthorize("isAuthenticated()")
  public ProdStatChart prodStatChart(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    String endDateNextDay = StandardTimeUtil.nextDay(endDate);
    return multiReportQAStatService.prodStatChart(assembler.parameterMap(beginDate, endDateNextDay, design));
  }

  @GetMapping(value = "wheel-rework", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.3 综合查询业务-质量统计-车轮返工")
  @PreAuthorize("isAuthenticated()")
  public WheelReworkResult wheelRework(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportQAStatService.wheelRework(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "wheel-rework-scrap/chart", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.4 综合查询业务-质量统计-车轮返废(图表)")
  @PreAuthorize("isAuthenticated()")
  public WheelReworkScrapResult wheelReworkScrapChart(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    @RequestParam(required = false)  List<String> reworkCode) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    if (!CollectionUtils.isEmpty(reworkCode)) {
      parameterMap.put("reworkCode", reworkCode);
    }
    return multiReportQAStatService.wheelReworkScrapChart(parameterMap);
  }

  @GetMapping(value = "pour-time-and-scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.5 综合查询业务-质量统计-浇注时间与废品")
  @PreAuthorize("isAuthenticated()")
  public List<Map<String, String>> pourTimeAndScrap(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportQAStatService.pourTimeAndScrap(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "confirm-scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.6 综合查询业务-质量统计-确认废品与交验")
  @PreAuthorize("isAuthenticated()")
  public ConfirmScrapResult confirmScrap(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportQAStatService.confirmScrap(assembler.parameterMap(beginDate, endDate, design), false);
  }

  @GetMapping(value = "pre-scrap", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.7 综合查询业务-质量统计-预检废品")
  @PreAuthorize("isAuthenticated()")
  public JSONObject preScrap(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportQAStatService.preScrap(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "machining-stat", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.8 综合查询业务-质量统计-在制品统计")
  @PreAuthorize("isAuthenticated()")
  public MachiningStat xMachiningStat() {
    return multiReportQAStatService.machiningStatResult();
  }

  @GetMapping(value = "all-confirm-scrap-stat", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.9 综合查询业务-质量统计-全部确认废品统计")
  @PreAuthorize("isAuthenticated()")
  public AllConfirmScrapResult allConfirmScrapStat(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportQAStatService.allConfirmScrapStat(assembler.parameterMap(beginDate, endDate, design));
  }

  @GetMapping(value = "/scrapwheel")
  @ApiOperation(value = "3.10 综合查询业务-质量统计-确废车轮明细")
  @Log(name = "确废车轮明细")
  @PreAuthorize("isAuthenticated()")
  public void scrapwheel(
          @RequestParam String beginDate,
          @RequestParam String endDate,
          HttpServletResponse response,
          Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    controlledRecordExportService
            .export("todayscrap", beginDate + "," + endDate, null, null, null, user.getOperatorId(), response);
  }

}
