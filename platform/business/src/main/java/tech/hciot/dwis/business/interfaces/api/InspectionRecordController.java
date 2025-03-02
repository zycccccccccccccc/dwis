package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.InspectionRecordService;
import tech.hciot.dwis.business.domain.model.InspectionRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddInspectionRecordRequest;
import tech.hciot.dwis.business.interfaces.dto.AddXRayRecordRequest;
import tech.hciot.dwis.business.interfaces.dto.XRayCheckData;
import tech.hciot.dwis.business.interfaces.dto.XRayCheckResponse;

@RestController
@RequestMapping(value = "/inspectionrecord")
@Api(tags = "质检操作记录")
public class InspectionRecordController {

  @Autowired
  private InspectionRecordService inspectionRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询质检操作记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<InspectionRecord> find(
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String testCode,
      @RequestParam(required = false) String holdCode,
      @RequestParam(required = false) String heatCode,
      @RequestParam(required = false) String scrapCode,
      @RequestParam(required = false) String reworkCode,
      @RequestParam(required = false) Integer xrayReq,
      @RequestParam(required = false) String opeTime,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler
        .toPageDataResponse(inspectionRecordService.find(inspectorId, wheelSerial, testCode, holdCode, heatCode, scrapCode,
            reworkCode, xrayReq, opeTime, currentPage, pageSize));
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存质检操作记录")
  @Log(name = "保存质检操作记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void save(@Validated @RequestBody @Request AddInspectionRecordRequest addInspectionRecordRequest) {
    inspectionRecordService.add(addInspectionRecordRequest.convert2Model());
  }

  @GetMapping(value = "/xray", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询X光录入记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<InspectionRecord> findXRay(
      @RequestParam(required = false) String inspectorId,
      @RequestParam(required = false) String wheelSerial,
      @RequestParam(required = false) String xrayResult,
      @RequestParam(required = false) String scrapResult,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    String operatorId = user.getOperatorId();
    inspectorId = StringUtils.defaultString(inspectorId, operatorId);
    return mgrAssembler
        .toPageDataResponse(
            inspectionRecordService.findXRay(inspectorId, wheelSerial, xrayResult, scrapResult, currentPage, pageSize));
  }

  @PostMapping(value = "/xray", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存X光录入记录")
  @Log(name = "保存X光录入记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public void addXRay(@Validated @RequestBody @Request AddXRayRecordRequest addXRayRecordRequest) {
    inspectionRecordService.addXRay(addXRayRecordRequest.convert2Model());
  }

  @GetMapping(value = "/xray/check/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询X光检查列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<XRayCheckResponse> findXRayCheckList(
      @RequestParam String castDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler
        .toPageDataResponse(inspectionRecordService.findXRayCheckList(castDate, currentPage, pageSize));
  }

  @GetMapping(value = "/xray/check/{ladleId}/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "进入X光检查")
  @PreAuthorize("isAuthenticated()")
  public List<XRayCheckData> findXRayCheckData(@PathVariable Integer ladleId) {
    return inspectionRecordService.findXRayCheckData(ladleId);
  }

  @PostMapping(value = "/xray/check", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "X光检查")
  @PreAuthorize("isAuthenticated()")
  public void xRayCheck(@Validated @RequestBody XRayCheckData xRayCheckData) {
    inspectionRecordService.xRayCheck(xRayCheckData);
  }
}
