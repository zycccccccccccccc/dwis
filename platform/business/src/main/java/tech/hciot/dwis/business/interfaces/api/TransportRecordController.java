package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
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
import tech.hciot.dwis.business.application.TransportRecordService;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.TransportRecordRequest;
import tech.hciot.dwis.business.interfaces.dto.TransportRecordResponse;

@RestController
@RequestMapping(value = "/transport")
@Api(tags = "X光发运/去重发运/镗孔发运业务")
public class TransportRecordController {

  @Autowired
  private TransportRecordService transportRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询发运记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<TransportRecordResponse> find(
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String design,
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false) Integer opeType,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(transportRecordService.find(wheelSerial, design, inspectorId, opeType,
        currentPage, pageSize));
  }

  @GetMapping(value = "/xray/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "X光发运车轮号下拉框列表")
  public List<String> findXrayWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return transportRecordService.findXrayWheelSerialList(keyword, limit);
  }

  @GetMapping(value = "/deweight/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "去重发运车轮号下拉框列表")
  public List<String> findDeWeightWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return transportRecordService.findDeWeightWheelSerialList(keyword, limit);
  }

  @GetMapping(value = "/bore/wheelserial/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "镗孔发运车轮号下拉框列表")
  public List<String> findBoreWheelSerialList(
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false, defaultValue = "50") Integer limit) {
    return transportRecordService.findBoreWheelSerialList(keyword, limit);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加发运记录")
  @Log(name = "添加发运记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public WheelRecord add(@Validated @RequestBody @Request TransportRecordRequest request) {
    return transportRecordService.add(request.convert2Model());
  }
}
