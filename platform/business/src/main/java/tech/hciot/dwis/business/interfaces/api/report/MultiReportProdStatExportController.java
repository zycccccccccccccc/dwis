package tech.hciot.dwis.business.interfaces.api.report;

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
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.report.multi.prod.MultiReportProdStatExportService;

@RestController
@RequestMapping(value = "/report/composite/prod")
@Api(tags = "综合查询业务-年月度产量统计")
public class MultiReportProdStatExportController {

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private MultiReportProdStatExportService multiReportProdStatExportService;

  @GetMapping(value = "repository-report/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.1 综合查询业务-年/月度产量统计-库存报告")
  @PreAuthorize("isAuthenticated()")
  public void repositoryReport(
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    multiReportProdStatExportService.repositoryReport(design, response);
  }

  @GetMapping(value = "pour-report/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.2 综合查询业务-年/月度产量统计-浇注报告")
  @PreAuthorize("isAuthenticated()")
  public void pourReport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportProdStatExportService.pourReport(parameterMap, response);
  }

  @GetMapping(value = "finish-report/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.3 综合查询业务-年/月度产量统计-成品报告")
  @PreAuthorize("isAuthenticated()")
  public void finishReport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportProdStatExportService.finishReport(parameterMap, response);
  }

  @GetMapping(value = "stock-report/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.4 综合查询业务-年/月度产量统计-入库报告")
  @PreAuthorize("isAuthenticated()")
  public void stockReport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportProdStatExportService.stockReport(parameterMap, response);
  }

  @GetMapping(value = "ship-report/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.5 综合查询业务-年/月度产量统计-发运报告")
  @PreAuthorize("isAuthenticated()")
  public void shipReport(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    multiReportProdStatExportService.shipReport(parameterMap, response);
  }

  @GetMapping(value = "year-prod-report/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.6 综合查询业务-年/月度产量统计-年度生产汇总报告")
  @PreAuthorize("isAuthenticated()")
  public void yearProdReport(
      @RequestParam String beginDate,
      @RequestParam(required = false) List<String> design,
      HttpServletResponse response) {
    multiReportProdStatExportService.yearProdReport(assembler.generateCurrentYearMap(beginDate, design), response);
  }
}
