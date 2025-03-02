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
import tech.hciot.dwis.business.application.MecRecordService;
import tech.hciot.dwis.business.domain.model.MecRecord;
import tech.hciot.dwis.business.domain.model.XNReleaseRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.WheelRecordMecResponse;
import tech.hciot.dwis.business.interfaces.dto.XNWheelResponse;

@RestController
@RequestMapping(value = "/mecrecord")
@Api(tags = "机械性能检测业务")
public class MecRecordController {

  @Autowired
  private MecRecordService mecRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询机械性能批次号列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> find(
      @RequestParam String type) {
    return mecRecordService.getMecSerialList(type);
  }

  @GetMapping(value = "/XNwheellist", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询XN车轮列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> find() {
    return mecRecordService.getXNWheelList();
  }

  @GetMapping(value = "/default", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询默认记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<MecRecord> find(
      @RequestParam String type,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(mecRecordService.find(type, currentPage, pageSize));
  }

  @GetMapping(value = "/wheel", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询性能批次车轮列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<WheelRecordMecResponse> getWheel(
      @RequestParam String mecSerial,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(mecRecordService.getWheel(mecSerial, currentPage, pageSize));
  }

  @PostMapping(value = "/check", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "性能抽检")
  @Log(name = "性能抽检", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void check(@Validated @RequestBody @Request MecRecord mecRecord) {
    mecRecordService.check(mecRecord);
  }

  @PostMapping(value = "/correct", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "性能纠回")
  @Log(name = "性能纠回", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void correct(@Validated @RequestBody @Request MecRecord mecRecord) {
    mecRecordService.correct(mecRecord);
  }

  @PostMapping(value = "/release", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "性能放行")
  @Log(name = "性能放行", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void release(@Validated @RequestBody @Request MecRecord mecRecord) {
    mecRecordService.release(mecRecord);
  }

  @GetMapping(value = "/wheel/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询性能车轮数据")
  @PreAuthorize("isAuthenticated()")
  public WheelRecordMecResponse findWheel(@PathVariable String wheelSerial) {
    return mecRecordService.findWheel(wheelSerial);
  }

  @GetMapping(value = "/xnwheel/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询xn车轮相关数据")
  @PreAuthorize("isAuthenticated()")
  public List<XNWheelResponse> findxnWheel(@PathVariable String wheelSerial) {
    return mecRecordService.findxnWheel(wheelSerial);
  }

  @PostMapping(value = "/xnRelease", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "XN车轮放行")
  @Log(name = "XN车轮放行", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void XNRelease(@Validated @RequestBody @Request List<XNReleaseRecord> request) {
    mecRecordService.xnRelease(request);
  }
}
