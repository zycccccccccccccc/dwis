package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import tech.hciot.dwis.business.application.FinalCheckService;
import tech.hciot.dwis.business.application.WheelRecordService;
import tech.hciot.dwis.business.domain.model.FinalCheckRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.FinalCheckResponse;

@RestController
@RequestMapping(value = "/finalcheck")
@Api(tags = "终检业务")
public class FinalCheckController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private FinalCheckService finalCheckService;

  @Autowired
  private WheelRecordService wheelRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询终检记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<FinalCheckRecord> find(
    @RequestParam(required = false) String wheelSerial,
    @RequestParam(required = false) String scrapCode,
    @RequestParam(required = false) String reworkCode,
    @RequestParam(required = false) String heatCode,
    @RequestParam(required = false) String inspectorId,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<FinalCheckRecord> page = finalCheckService.find(wheelSerial, scrapCode, reworkCode, heatCode,
      inspectorId, currentPage, pageSize);
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
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    return wheelRecordService.findWheelSerialList(keyword, parameterMap, limit);
  }

  @GetMapping(value = "/wheel/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取车轮信息")
  public FinalCheckResponse findWheel(@PathVariable String wheelSerial) {
    return finalCheckService.findWheel(wheelSerial);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加终检记录")
  @Log(name = "添加终检记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request FinalCheckRecord request) {
    return finalCheckService.add(request);
  }

  @GetMapping(value = "brinreq")
  @ApiOperation(value = "是否需要硬度检查")
  @PreAuthorize("isAuthenticated()")
  public Boolean isBrinReq(@RequestParam String design, @RequestParam Integer brinnelReading) {
    return finalCheckService.isBrinReq(design, brinnelReading);
  }
}
