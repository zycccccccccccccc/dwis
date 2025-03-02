package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
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
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.RawWheelPrintRecordService;
import tech.hciot.dwis.business.application.WheelRecordService;
import tech.hciot.dwis.business.domain.model.RawWheelPrintRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.RawWheelPrintResponse;

@RestController
@RequestMapping(value = "/rawwheelprint")
@Api(tags = "毛坯车轮成品打印")
public class RawWheelPrintRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private RawWheelPrintRecordService rawWheelPrintRecordService;

  @Autowired
  private WheelRecordService wheelRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询毛坯车轮成品打印记录列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<RawWheelPrintRecord> find(
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String design,
      @RequestParam(required = false) Integer boreSize,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    Page<RawWheelPrintRecord> page = rawWheelPrintRecordService.find(wheelSerial, design, boreSize, scrapCode,
      operator, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/wheel/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "车轮号下拉框列表")
  public List<String> findWheelSerialList(
    @RequestParam(required = false, defaultValue = "") String keyword,
    @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return wheelRecordService.findRawWheelPrintWheelSerialList(keyword, limit);
  }

  @GetMapping(value = "/wheel/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取车轮信息")
  public RawWheelPrintResponse findWheel(@PathVariable String wheelSerial) {
    return rawWheelPrintRecordService.findWheel(wheelSerial);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加毛坯车轮成品打印记录")
  @Log(name = "添加毛坯车轮成品打印记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public RawWheelPrintResponse add(@Validated @RequestBody @Request RawWheelPrintRecord request,
                         Principal principal) {
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operator = user.getOperatorId();
    request.setOperator(operator);
    return rawWheelPrintRecordService.add(request);
  }
}
