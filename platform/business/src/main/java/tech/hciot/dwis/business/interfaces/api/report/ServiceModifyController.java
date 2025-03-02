package tech.hciot.dwis.business.interfaces.api.report;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.HeatRecordService;
import tech.hciot.dwis.business.application.HeatService;
import tech.hciot.dwis.business.application.LadleRecordService;
import tech.hciot.dwis.business.application.PitRecordsService;
import tech.hciot.dwis.business.application.PourRecordService;
import tech.hciot.dwis.business.application.report.ServiceModifyService;
import tech.hciot.dwis.business.domain.model.Heat;
import tech.hciot.dwis.business.domain.model.HeatRecord;
import tech.hciot.dwis.business.domain.model.LadleRecord;
import tech.hciot.dwis.business.domain.model.PitRecords;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/report/servicemodify")
@Api(tags = "数据修改业务")
public class ServiceModifyController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private ServiceModifyService serviceModifyService;

  @Autowired
  private HeatService heatService;

  @Autowired
  private HeatRecordService heatRecordService;

  @Autowired
  private LadleRecordService ladleRecordService;

  @Autowired
  private PourRecordService pourRecordService;

  @Autowired
  private PitRecordsService pitRecordsService;

  @GetMapping(value = "heattimeout", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "热处理超时")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Object> findHeatTimeout(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    PageDataResponse<Object> page = serviceModifyService.findHeatTimeout(beginDate, endDate, currentPage, pageSize);
    return page;
  }

  @PutMapping(value = "heattimeout/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改热处理超时")
  @Log(name = "修改热处理超时", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyHeatTimeout(@PathVariable @Id Integer id,
      @RequestBody @Request Heat request) {
    heatService.modifyHeatTimeout(id, request);
  }

  @GetMapping(value = "heatcode", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "热处理代码")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Object> findHeatCode(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    PageDataResponse<Object> page = serviceModifyService.findHeatCode(beginDate, endDate, currentPage, pageSize);
    return page;
  }

  @GetMapping(value = "tpot", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询大包信息列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<HeatRecord> findTpot(
      @RequestParam String castDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<HeatRecord> page = heatRecordService.find(null, null, castDate, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PutMapping(value = "tpot/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改大包信息")
  @Log(name = "修改大包信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyTpot(@PathVariable @Id Integer id,
      @RequestBody @Request HeatRecord request) {
    heatRecordService.modify(id, request);
  }

  @GetMapping(value = "ladle", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询小包信息列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<LadleRecord> findLadle(
      @RequestParam Integer heatRecordId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<LadleRecord> page = ladleRecordService.find(heatRecordId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PutMapping(value = "ladle/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改小包信息")
  @Log(name = "修改小包信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyLadle(@PathVariable @Id Integer id,
      @RequestBody @Request LadleRecord request) {
    ladleRecordService.modify(id, request);
  }

  @GetMapping(value = "pourrecord", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询浇注信息列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<PourRecord> findPourRecord(
      @RequestParam Integer ladleId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<PourRecord> page = pourRecordService.findPourRecord(ladleId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PutMapping(value = "pour/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改浇注信息")
  @Log(name = "修改浇注信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyPour(@PathVariable @Id Integer id,
      @RequestBody @Request PourRecord request) {
    pourRecordService.modify(id, request);
  }

  @GetMapping(value = "pittime", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "开箱入桶时间")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Object> findPitTime(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    PageDataResponse<Object> page = serviceModifyService.findPitTime(beginDate, endDate, currentPage, pageSize);
    return page;
  }

  @GetMapping(value = "scrapcode8", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "废品代码8")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Object> findScrapCode8(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    PageDataResponse<Object> page = serviceModifyService.findScrapCode8(beginDate, endDate, currentPage, pageSize);
    return page;
  }

  @PutMapping(value = "scrapcode8/{wheelSerial}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改废品代码8")
  @Log(name = "修改废品代码8", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyScrapCode8(@PathVariable @Id String wheelSerial) {
    serviceModifyService.modifyScrapCode8(wheelSerial);
  }

  @GetMapping(value = "pit", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "缓冷桶信息")
  public PitRecords findPitRecords(@RequestParam Integer id) {
    return pitRecordsService.findPitRecords(id);
  }

  @PutMapping(value = "pit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改缓冷桶信息")
  @Log(name = "修改缓冷桶信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyPitRecords(@PathVariable @Id Integer id, @RequestBody @Request PitRecords pitRecords) {
    pitRecordsService.modify(id, pitRecords);
  }

  @GetMapping(value = "graphite", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "轮型石墨")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Object> findGraphite(
      @RequestParam String beginDate,
      @RequestParam String endDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    PageDataResponse<Object> page = serviceModifyService.findGraphite(beginDate, endDate, currentPage, pageSize);
    return page;
  }
}
