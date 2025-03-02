package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import tech.hciot.dwis.business.application.BalanceService;
import tech.hciot.dwis.business.application.WheelRecordService;
import tech.hciot.dwis.business.domain.model.BalanceRecord;
import tech.hciot.dwis.business.domain.model.BarcodePrintRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.BalanceResponse;

@RestController
@RequestMapping(value = "/balance")
@Api(tags = "平衡机业务")
public class BalanceController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private BalanceService balanceService;

  @Autowired
  private WheelRecordService wheelRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询平衡机记录")
  public PageDataResponse<BalanceRecord> find(
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String holdCode,
      @RequestParam(required = false) String reworkCode,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false) String balanceS,
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<BalanceRecord> page = balanceService.find(wheelSerial, holdCode, reworkCode, scrapCode,
      balanceS, inspectorId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/wheel/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "车轮号下拉框列表")
  public List<String> findWheelSerialList(
    @RequestParam(required = false, defaultValue = "") String keyword,
    @RequestParam(required = false, defaultValue = "50") Integer limit) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("pre", true);
    parameterMap.put("finalCount", true);
    parameterMap.put("ultra", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    return wheelRecordService.findWheelSerialList(keyword, parameterMap, limit);
  }

  @GetMapping(value = "/wheel/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取车轮信息")
  public BalanceResponse findWheel(@PathVariable String wheelSerial) {
    return balanceService.findWheel(wheelSerial);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加平衡机记录")
  @Log(name = "添加平衡机记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public BalanceResponse add(@Validated @RequestBody @Request BalanceRecord request) {
    return balanceService.add(request);
  }

  @GetMapping(value = "/barcode/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "补打车轮号下拉框列表")
  public List<String> findBarcodePrintWheelSerialList(
    @RequestParam(required = false, defaultValue = "") String keyword,
    @RequestParam(required = false, defaultValue = "50") Integer limit) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("balance", true);
    parameterMap.put("finishPrint", true);
    parameterMap.put("confirmedScrap", false);
    return wheelRecordService.findWheelSerialList(keyword, parameterMap, limit);
  }

  @PostMapping(value = "barcode", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "补打条码")
  @PreAuthorize("isAuthenticated()")
  public BarcodePrintRecord printBarcode(@Validated @RequestBody @Request BarcodePrintRecord request) {
    return balanceService.printBarcode(request);
  }
}
