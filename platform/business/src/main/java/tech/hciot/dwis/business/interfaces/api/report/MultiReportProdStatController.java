package tech.hciot.dwis.business.interfaces.api.report;

import com.alibaba.fastjson.JSONObject;
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
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.application.report.MultiReportProdStatService;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.prod.RepositoryReportResult;

@RestController
@RequestMapping(value = "/report/composite/prod")
@Api(tags = "综合查询业务-年月度产量统计")
public class MultiReportProdStatController {

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private MultiReportProdStatService multiReportProdStatService;

  @GetMapping(value = "repository-report", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.1 综合查询业务-年/月度产量统计-库存报告")
  @PreAuthorize("isAuthenticated()")
  public RepositoryReportResult repositoryReport(@RequestParam(required = false) List<String> design) {
    return multiReportProdStatService.repositoryReport(design);
  }

  @GetMapping(value = "pour-report", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.2 综合查询业务-年/月度产量统计-浇注报告")
  @PreAuthorize("isAuthenticated()")
  public JSONObject pourReport(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    return multiReportProdStatService.pourReport(parameterMap);
  }

  @GetMapping(value = "finish-report", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.3 综合查询业务-年/月度产量统计-成品报告")
  @PreAuthorize("isAuthenticated()")
  public JSONObject finishReport(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    return multiReportProdStatService.finishReport(parameterMap);
  }

  @GetMapping(value = "stock-report", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.4 综合查询业务-年/月度产量统计-入库报告")
  @PreAuthorize("isAuthenticated()")
  public JSONObject stockReport(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    return multiReportProdStatService.stockReport(parameterMap);
  }

  @GetMapping(value = "ship-report", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.5 综合查询业务-年/月度产量统计-发运报告")
  @PreAuthorize("isAuthenticated()")
  public JSONObject shipReport(
    @RequestParam String beginDate,
    @RequestParam String endDate,
    @RequestParam(required = false) List<String> design) {
    Map<String, Object> parameterMap = assembler.parameterMap(beginDate, endDate, design);
    return multiReportProdStatService.shipReport(parameterMap);
  }

  @GetMapping(value = "year-prod-report", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "2.6 综合查询业务-年/月度产量统计-年度生产汇总报告")
  @PreAuthorize("isAuthenticated()")
  public JSONObject yearProdReport(
    @RequestParam String beginDate,
    @RequestParam(required = false) List<String> design) {
    return multiReportProdStatService.yearProdReport(assembler.generateCurrentYearMap(beginDate, design));
  }
}
