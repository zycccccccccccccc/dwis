package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.ScrapAnalysisService;
import tech.hciot.dwis.business.domain.model.ScrapReasonRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddScrapReasonRecordRequest;

@RestController
@RequestMapping(value = "/scrapanalysis")
@Api(tags = "废品分析业务")
public class ScrapAnalysisController {

  @Autowired
  private ScrapAnalysisService scrapAnalysisService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询报废品分析记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ScrapReasonRecord> find(
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false) String scrapReason,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler
        .toPageDataResponse(scrapAnalysisService.find(inspectorId, wheelSerial, scrapCode, scrapReason, currentPage, pageSize));
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存废品分析记录")
  @Log(name = "保存废品分析记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void save(@Validated @RequestBody @Request AddScrapReasonRecordRequest addScrapReasonRecordRequest) {
    scrapAnalysisService.addScrapReasonRecord(addScrapReasonRecordRequest.convert2Model());
  }

  @GetMapping(value = "/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "导出当天废品分析记录")
  public void export(HttpServletResponse response) {
    scrapAnalysisService.export(response);
  }

  @GetMapping(value = "/detail/export", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "废码车轮导出")
  public void exportDetail(HttpServletResponse response) {
    scrapAnalysisService.exportDetail(response);
  }

}
