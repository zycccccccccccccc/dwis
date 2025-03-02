package tech.hciot.dwis.business.interfaces.api.report;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.report.multi.qa.MultiReportQAStatExportService;

@RestController
@RequestMapping(value = "/report/composite/qa")
@Api(tags = "综合查询业务-质量统计导出")
public class MultiReportQAStatExportController {

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private MultiReportQAStatExportService multiReportQAStatExportService;

  @GetMapping(value = "qa-stat/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.1 综合查询业务-质量统计-质量统计")
  @PreAuthorize("isAuthenticated()")
  public void qaStat(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportQAStatExportService.qaStat(parameterMap, response);
  }

  @GetMapping(value = "prod-stat/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.2 综合查询业务-质量统计-产量统计")
  @PreAuthorize("isAuthenticated()")
  public void prodStat(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    String endDateNextDay = StandardTimeUtil.nextDay(endDate);
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDateNextDay, design);
    multiReportQAStatExportService.prodStat(parameterMap, response);
  }

  @GetMapping(value = "wheel-rework/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.3 综合查询业务-质量统计-车轮返工")
  @PreAuthorize("isAuthenticated()")
  public void wheelRework(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportQAStatExportService.wheelRework(parameterMap, response);
  }

  @GetMapping(value = "wheel-rework-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.4 综合查询业务-质量统计-车轮返废")
  @PreAuthorize("isAuthenticated()")
  public void wheelReworkScrapReport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      @RequestParam(required = false)  List<String> reworkCode,
    HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, null);
    if (!CollectionUtils.isEmpty(reworkCode)) {
      parameterMap.put("reworkCode", reworkCode);
    }
    multiReportQAStatExportService.wheelReworkScrapReport(parameterMap, response);
  }

  @GetMapping(value = "pour-time-and-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.5 综合查询业务-质量统计-浇注时间与废品")
  @PreAuthorize("isAuthenticated()")
  public void pourTimeAndScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportQAStatExportService.pourTimeAndScrap(parameterMap, response);
  }

  @GetMapping(value = "confirm-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.6 综合查询业务-质量统计-确认废品与交验")
  @PreAuthorize("isAuthenticated()")
  public void confirmScrap(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportQAStatExportService.confirmScrap(parameterMap, response);
  }

  @GetMapping(value = "pre-scrap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.7 综合查询业务-质量统计-预检废品")
  @PreAuthorize("isAuthenticated()")
  public void preScrap(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportQAStatExportService.preScrap(parameterMap, response);
  }

  @GetMapping(value = "machining-stat/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.8 综合查询业务-质量统计-在制品统计")
  @PreAuthorize("isAuthenticated()")
  public void xMachiningStat(HttpServletResponse response) {
    multiReportQAStatExportService.machiningStatResult(response);
  }

  @GetMapping(value = "all-confirm-scrap-stat/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.9 综合查询业务-质量统计-全部确认废品统计")
  @PreAuthorize("isAuthenticated()")
  public void allConfirmScrapStat(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
    HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportQAStatExportService.allConfirmScrapStat(parameterMap, response);
  }

  @GetMapping(value = "wheel-info-serial/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.11 综合查询业务-质量统计-整炉车轮信息(轮号)")
  @PreAuthorize("isAuthenticated()")
  public void wheelInfoSerial(
          @RequestParam String wheelSerial,
          HttpServletResponse response) {
    multiReportQAStatExportService.furWheelInfo( wheelSerial,null, null, response);
  }

  @GetMapping(value = "wheel-info-date-tap/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "3.11 综合查询业务-质量统计-整炉车轮信息(日期&&出钢号)")
  @PreAuthorize("isAuthenticated()")
  public void wheelInfoDateTap(
          @RequestParam String castDate,
          @RequestParam Integer tapSeq,
          HttpServletResponse response) {
    multiReportQAStatExportService.furWheelInfo(null, castDate, tapSeq, response);
  }
}
