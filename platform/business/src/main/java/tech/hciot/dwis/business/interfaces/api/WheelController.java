package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.business.application.WheelRecordService;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.MachineResponse;
import tech.hciot.dwis.business.interfaces.dto.WheelRecordResponse;

@RestController
@RequestMapping(value = "/wheel")
@Api(tags = "车轮信息")
public class WheelController {

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询车轮列表")
  public List<String> findWheelList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return wheelRecordService.findWheelList(wheelSerial, limit);
  }

  @GetMapping(value = "/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "根据车轮序列号查询车轮信息")
  public WheelRecord findWheel(@PathVariable String wheelSerial) {
    return wheelRecordService.findWheel(wheelSerial);
  }

  @RequestMapping(value = "/release/serial/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取待放行车轮号列表")
  public List<WheelRecordResponse> findReleaseWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findReleaseWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/inspection/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取质检操作车轮号列表")
  public List<WheelRecordResponse> findInspectionWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findNotFinishNotScrapWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/scrap/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取报废车轮号列表")
  public List<WheelRecordResponse> findScrapWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findScrapWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/needscrap/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取待报废车轮号列表")
  public List<WheelRecordResponse> findNeedScrapWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findNeedScrapWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/correctscrap/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取报废纠回车轮号列表")
  public List<WheelRecordResponse> findCorrectScrapWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findCorrectScrapWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/correctfinish/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取成品纠回车轮号列表")
  public List<WheelRecordResponse> findCorrectFinishWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findCorrectFinishWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/correctstock/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取入库纠回车轮号列表")
  public List<WheelRecordResponse> findCorrectStockWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findCorrectStockWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/correctreturn/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取返厂纠回车轮号列表")
  public List<WheelRecordResponse> findCorrectReturnWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findCorrectReturnWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/xray/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取X光检测车轮号列表")
  public List<WheelRecordResponse> findXRayWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findXRayWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/cihen/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取磁痕检测车轮号列表")
  public List<WheelRecordResponse> findCihenWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findCihenWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/performance/serial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取性能录入业务车轮列表")
  public List<WheelRecordResponse> findPerformanceWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String wheelSerial,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return mgrAssembler.toWheelRecordResponseList(wheelRecordService.findPerformanceWheelSerialList(wheelSerial, limit));
  }

  @GetMapping(value = "/machine/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "加工业务车轮号下拉框列表")
  public List<String> findMachineWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "50") Integer limit,
      @RequestParam String location) {
    return wheelRecordService.findMachineWheelSerialList(keyword, limit, location);
  }

  @GetMapping(value = "/machine/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取待加工车轮信息")
  public MachineResponse findMachineWheel(@PathVariable String wheelSerial) {
    return wheelRecordService.findMachineWheel(wheelSerial);
  }

  @GetMapping(value = "/sample/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "抽检车轮录入业务车轮下拉框列表")
  public List<String> findSampleWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return wheelRecordService.findSampleWheelSerialList(keyword, limit);
  }
}
