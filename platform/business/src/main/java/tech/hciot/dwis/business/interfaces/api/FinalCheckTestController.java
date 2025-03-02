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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.FinalCheckTestService;
import tech.hciot.dwis.business.application.WheelRecordService;
import tech.hciot.dwis.business.domain.model.HbtestRecord;
import tech.hciot.dwis.business.domain.model.TapeTestingRecord;
import tech.hciot.dwis.business.domain.model.TestWheel;
import tech.hciot.dwis.business.domain.model.ThreehbRecord;
import tech.hciot.dwis.business.domain.model.TroundRecord;
import tech.hciot.dwis.business.domain.model.WheelDevRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/finalcheck/test")
@Api(tags = "终检开班检测信息")
public class FinalCheckTestController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private FinalCheckTestService finalCheckTestService;

  @Autowired
  private WheelRecordService wheelRecordService;

  @RequestMapping(value = "hbtest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询布氏硬度机试验记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<HbtestRecord> findHbtestRecord(
    @RequestParam String inspectorId,
    @RequestParam String shiftNo,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<HbtestRecord> page = finalCheckTestService.findHbtestRecord(inspectorId, shiftNo, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "hbtest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加布氏硬度机试验记录")
  @Log(name = "添加布氏硬度机试验记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public HbtestRecord add(@Validated @RequestBody @Request HbtestRecord request) {
    return finalCheckTestService.add(request);
  }


  @RequestMapping(value = "threehb", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询轮辋三点硬度检测记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ThreehbRecord> findThreehbRecord(
    @RequestParam String inspectorId,
    @RequestParam String shiftNo,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<ThreehbRecord> page = finalCheckTestService.findThreehbRecord(inspectorId, shiftNo, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "threehb", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加轮辋三点硬度检测记录")
  @Log(name = "添加轮辋三点硬度检测记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public ThreehbRecord add(@Validated @RequestBody @Request ThreehbRecord request) {
    return finalCheckTestService.add(request);
  }

  @RequestMapping(value = "tround", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询踏面机床首件圆度记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<TroundRecord> findTroundRecord(
    @RequestParam String inspectorId,
    @RequestParam String shiftNo,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<TroundRecord> page = finalCheckTestService.findTroundRecord(inspectorId, shiftNo, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "tround", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加踏面机床首件圆度记录")
  @Log(name = "添加踏面机床首件圆度记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public TroundRecord add(@Validated @RequestBody @Request TroundRecord request) {
    return finalCheckTestService.add(request);
  }


  @RequestMapping(value = "wheeldev", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询车轮尺寸偏差记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<WheelDevRecord> findWheelDevRecord(
    @RequestParam String inspectorId,
    @RequestParam String shiftNo,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<WheelDevRecord> page = finalCheckTestService.findWheelDevRecord(inspectorId, shiftNo, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "wheellist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "三点硬度、踏面机床、尺寸偏差页面的车轮列表")
  @PreAuthorize("isAuthenticated()")
  public List<String> findWheelSerialList(
    @RequestParam(required = false, defaultValue = "") String keyword,
    @RequestParam(required = false, defaultValue = "50") Integer limit) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("pre", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    return wheelRecordService.findWheelSerialList(keyword, parameterMap, limit);
  }

  @GetMapping(value = "wheeldev/wheel/{wheelSerial}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "车轮尺寸偏差记录中的车轮信息")
  public WheelRecord findWheel(@PathVariable String wheelSerial) {
    return wheelRecordService.findWheel(wheelSerial);
  }

  @RequestMapping(value = "wheeldev", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加车轮尺寸偏差记录")
  @Log(name = "添加车轮尺寸偏差记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public WheelDevRecord add(@Validated @RequestBody @Request WheelDevRecord request) {
    return finalCheckTestService.add(request);
  }


  @RequestMapping(value = "tapetesting", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询带尺检测记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<TapeTestingRecord> findTapeTestingRecord(
    @RequestParam String inspectorId,
    @RequestParam String shiftNo,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<TapeTestingRecord> page = finalCheckTestService.findTapeTestingRecord(inspectorId, shiftNo, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "tapetesting/wheel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "带尺检测记录中的车轮列表")
  @PreAuthorize("isAuthenticated()")
  public List<TestWheel> findTestWheelList(
    @RequestParam(required = false, defaultValue = "") String keyword,
    @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return finalCheckTestService.findTestWheelList(keyword, limit);
  }

  @RequestMapping(value = "tapetesting", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加带尺检测记录")
  @Log(name = "添加带尺检测记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public TapeTestingRecord TapeTestingRecord(@Validated @RequestBody @Request TapeTestingRecord request) {
    return finalCheckTestService.add(request);
  }
}
