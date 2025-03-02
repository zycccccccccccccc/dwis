package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.PerformanceService;
import tech.hciot.dwis.business.domain.model.MecProperty;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddPerformanceRequest;
import tech.hciot.dwis.business.interfaces.dto.PerformanceReportResponse;

@RestController
@RequestMapping(value = "/performance")
@Api(tags = "性能录入/汇总业务")
public class PerformanceController {

  @Autowired
  private PerformanceService performanceService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建性能录入")
  @Log(name = "新建性能录入", type = OperationType.OPERATION_TYPE_ADD)
  public void addPerformance(@Validated @RequestBody @Request AddPerformanceRequest addPerformanceRequest) {
    performanceService.addPerformance(addPerformanceRequest.convert2Model());
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "删除性能录入")
  @Log(name = "删除性能录入", type = OperationType.OPERATION_TYPE_DELETE)
  public void deletePerformance(@PathVariable Integer id) {
    performanceService.deletePerformance(id);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "修改性能录入")
  @Log(name = "修改性能录入", type = OperationType.OPERATION_TYPE_DELETE)
  public void modifyPerformance(@PathVariable Integer id, @RequestBody @Request AddPerformanceRequest addPerformanceRequest) {
    performanceService.modifyPerformance(id, addPerformanceRequest.convert2Model());
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询性能录入")
  public PageDataResponse<MecProperty> find(
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String mecSerial,
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(performanceService.find(wheelSerial, mecSerial, inspectorId, currentPage, pageSize));
  }

  @GetMapping(value = "/{id}/report", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "生成性能录入报告")
  public PerformanceReportResponse report(@PathVariable Integer id) {
    return performanceService.report(id);
  }

  @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询性能汇总")
  public PageDataResponse<MecProperty> summary(
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(performanceService.summary(startDate, endDate, currentPage, pageSize));
  }

  @GetMapping(value = "/summary/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "导出性能汇总")
  public void exportSummary(
      @RequestParam String startDate,
      @RequestParam String endDate,
      HttpServletResponse response) {
    performanceService.exportSummary(startDate, endDate, response);
  }
}
