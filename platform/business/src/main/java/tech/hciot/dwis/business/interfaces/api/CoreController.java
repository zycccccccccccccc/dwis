package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.PourRecordService;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.PourRecordRequest;

@RestController
@RequestMapping(value = "/core")
@Api(tags = "下芯信息")
public class CoreController {

  @Autowired
  private PourRecordService pourRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建下芯记录")
  @Log(name = "新建下芯记录", type = OperationType.OPERATION_TYPE_ADD)
  public void addPourRecord(@Validated @RequestBody @Request PourRecordRequest pourRecordRequest) {
    pourRecordService.addPourRecord(pourRecordRequest.convert2Model());
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询下芯记录")
  public PageDataResponse<PourRecord> findPourRecord(
      @RequestParam(required = false) String operatorId,
      @RequestParam(required = false) String recordDate,
      @RequestParam(required = false) String batchNo,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(pourRecordService.findPourRecord(operatorId, recordDate, batchNo, null, currentPage,
        pageSize));
  }

  @GetMapping(value = "/pit", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询进桶流程的下芯记录")
  public List<PourRecord> findPourRecordPit() {
    return pourRecordService.getForPit();
  }

  @GetMapping(value = "/pour", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询浇注流程的下芯记录")
  public PageDataResponse<PourRecord> findPourRecordPour(
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(pourRecordService.getForPour(currentPage, pageSize));
  }

  @GetMapping(value = "/pour/ladle/{ladleId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询小包的下芯记录")
  public PageDataResponse<PourRecord> findPourRecordLadle(
      @PathVariable Integer ladleId,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(pourRecordService.findPourRecordLadle(ladleId, currentPage, pageSize));
  }
}
