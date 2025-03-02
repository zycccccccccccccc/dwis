package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import tech.hciot.dwis.business.application.ScrapRecordService;
import tech.hciot.dwis.business.domain.model.ScrapRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddScrapRecordRequest;
import tech.hciot.dwis.business.interfaces.dto.CorrectScrapRecordRequest;

@RestController
@RequestMapping(value = "/scraprecord")
@Api(tags = "报废和纠回操作记录")
public class ScrapRecordController {

  @Autowired
  private ScrapRecordService scrapRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询报废和纠回操作记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ScrapRecord> find(
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String design,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false, defaultValue = "1") Integer confirmedScrap,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler
        .toPageDataResponse(scrapRecordService.find(inspectorId, wheelSerial, design, scrapCode, confirmedScrap,
            currentPage, pageSize));
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存车轮报废记录")
  @Log(name = "保存车轮报废记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void save(@Validated @RequestBody @Request AddScrapRecordRequest addScrapRecordRequest) {
    scrapRecordService.add(addScrapRecordRequest.convert2Model());
  }

  @PostMapping(value = "/correct", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存车轮报废纠回记录")
  @Log(name = "保存车轮报废纠回记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void correct(@Validated @RequestBody @Request CorrectScrapRecordRequest correctScrapRecordRequest) {
    scrapRecordService.correct(correctScrapRecordRequest.convert2Model());
  }
}
