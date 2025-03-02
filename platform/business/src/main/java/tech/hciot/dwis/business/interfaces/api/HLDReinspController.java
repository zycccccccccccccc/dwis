package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.HLDReinspService;
import tech.hciot.dwis.business.application.PreCheckService;
import tech.hciot.dwis.business.application.WheelRecordService;
import tech.hciot.dwis.business.domain.model.HLDReinspRecord;
import tech.hciot.dwis.business.domain.model.PreCheckRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import java.util.List;

@RestController
@RequestMapping(value = "/HLDReinsp")
@Api(tags = "HLD废码检查业务")
public class HLDReinspController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private HLDReinspService hldReinspService;

  @Autowired
  private PreCheckService preCheckService;

  @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询HLD废码检查记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<HLDReinspRecord> find(
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = true) String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<HLDReinspRecord> page = hldReinspService.findRecords(wheelSerial, inspectorId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取HLD废码检查业务车轮列表")
  public List<String> findHLDReinspWheelSerialList(
          @RequestParam(required = false, defaultValue = "") String wheelSerial,
          @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return wheelRecordService.findHLDReinspWheelSerialList(wheelSerial, limit);
  }

  @GetMapping(value = "/wheel/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取HLD车轮相关信息")
  public HLDReinspRecord findWheel(@PathVariable String wheelSerial) {
    return hldReinspService.findWheel(wheelSerial);
  }

  @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加HLD废码检查记录")
  @Log(name = "添加HLD废码检查记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public HLDReinspRecord add(@Validated @RequestBody @Request HLDReinspRecord request) {
    return hldReinspService.add(request);
  }
}
