package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.MoldService;
import tech.hciot.dwis.business.domain.model.*;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/mold")
@Api(tags = "造型线业务")
public class MoldController {

  @Autowired
  private MoldService moldService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "/addPreShift", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建造型线开班记录")
  @Log(name = "新建造型线开班记录", type = OperationType.OPERATION_TYPE_ADD)
  public Integer addPreShift(@Validated @RequestBody @Request MoldPreShiftRequest moldPreShiftRequest) {
    return moldService.addPreShiftRecord(moldPreShiftRequest.convert2Model());
  }

  @GetMapping(value = "/preShift", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询造型线开班记录")
  @Log(name = "查询造型线开班记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<MoldPreShiftRecord> getPreShift(
          @RequestParam Integer cd,
          @RequestParam(required = false, defaultValue = "1") Integer currentPage,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(moldService.getPreShift(cd, currentPage, pageSize));
  }

  @PutMapping(value = "/preShift/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改造型线开班信息")
  @Log(name = "修改造型射砂开班信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void editPreShift(@PathVariable Integer id, @Validated @RequestBody @Request MoldPreShiftRequest moldPreShiftRequest) {
    moldService.editPreShift(id, moldPreShiftRequest.convert2Model());
  }

  @GetMapping(value = "/preShiftDetail", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询造型开班详情")
  @PreAuthorize("isAuthenticated()")
  public MoldPreShiftRecord findById(@RequestParam Integer preShiftId, @RequestParam Integer type) {
    return moldService.findPreShiftByIdAndType(preShiftId, type);
  }

  @GetMapping(value = "/metalMold/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询金型编号列表")
  public List<String> findMetalMoldList(
          @RequestParam Integer cdType) {
    return moldService.getMetalMoldList(cdType);
  }

  @PostMapping(value = "/addSandJet", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建造型射砂记录")
  @Log(name = "新建造型射砂记录", type = OperationType.OPERATION_TYPE_ADD)
  public Integer addSandJet(@Validated @RequestBody @Request SandJetRequest sandJetRequest) {
    return moldService.addSandJetRecord(sandJetRequest.convert2Model());
  }

  @PutMapping(value = "/editSandJet/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改造型射砂记录")
  @Log(name = "修改造型射砂记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void editSandJet(@PathVariable Integer id, @Validated @RequestBody @Request SandJetRequest sandJetRequest) {
    moldService.editSandJet(id, sandJetRequest.convert2Model());
  }

  @DeleteMapping(value = "/deleteSandJet/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除造型射砂记录")
  @Log(name = "删除造型射砂记录", type = OperationType.OPERATION_TYPE_DELETE, operationObject = "sand_jet_record")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    moldService.deleteSandJet(id);
  }

  @GetMapping(value = "/sandJet/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询造型射砂记录")
  @Log(name = "查询造型射砂记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<SandJetRecord> getSandJetRecordList(
          @RequestParam() Integer preShiftId,
          @RequestParam(required = false) Integer lineNo,
          @RequestParam(required = false) String graphite,
          @RequestParam(required = false, defaultValue = "1") Integer currentPage,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    Page<SandJetRecord> page = moldService.findSandJetList(preShiftId, lineNo, graphite, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PostMapping(value = "/addSandMix", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建造型混砂记录")
  @Log(name = "新建造型混砂记录", type = OperationType.OPERATION_TYPE_ADD)
  public Integer addSandMix(@Validated @RequestBody @Request SandMixRequest sandMixRequest) {
    return moldService.addSandMixRecord(sandMixRequest.convert2Model());
  }

  @GetMapping(value = "/sandMix/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询造型混砂记录")
  @Log(name = "查询造型混砂记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<SandMixRecord> getSandMixRecordList(
          @RequestParam(required = false) String inspectorId,
          @RequestParam(required = false, defaultValue = "1") Integer currentPage,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    Page<SandMixRecord> page = moldService.findSandMixList(inspectorId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PutMapping(value = "/sandMix/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改造型混砂记录")
  @Log(name = "修改造型混砂记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void editSandMix(@PathVariable Integer id, @Validated @RequestBody @Request SandMixRequest sandMixRequest) {
    moldService.editSandMix(id, sandMixRequest.convert2Model());
  }

  @GetMapping(value = "/nextGraphite", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询下一个石墨号")
  @PreAuthorize("isAuthenticated()")
  public String findNextGraphite(@RequestParam() Integer lineNo) {
    return moldService.findNextGraphite(lineNo);
  }

  @GetMapping(value = "/nextWheelNo", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询下一个序列号")
  @PreAuthorize("isAuthenticated()")
  public String findNextWheelNo(@RequestParam() String moldDate,
                                @RequestParam() Integer lineNo) {
    return moldService.findNextWheelNo(moldDate, lineNo);
  }

  @PostMapping(value = "/addPreSpray", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建造型预喷记录")
  @Log(name = "新建造型预喷记录", type = OperationType.OPERATION_TYPE_ADD)
  public Integer addPreSpray(@Validated @RequestBody @Request PreSprayRequest preSprayRequest) {
    return moldService.addPreSprayRecord(preSprayRequest.convert2Model());
  }

  @GetMapping(value = "/preSpray/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询造型预喷记录")
  @Log(name = "查询造型预喷记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<PreSprayRecord> getPreSprayRecordList(
          @RequestParam Integer preShiftId,
          @RequestParam(required = false, defaultValue = "1") Integer currentPage,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    Page<PreSprayRecord> page = moldService.findPreSprayList(preShiftId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PutMapping(value = "/preSpray/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改造型预喷记录")
  @Log(name = "修改造型预喷记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void editPreSpray(@PathVariable Integer id, @Validated @RequestBody @Request PreSprayRequest preSprayRequest) {
    moldService.editPreSpray(id, preSprayRequest.convert2Model());
  }

  @PostMapping(value = "/addFinalSpray", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建造型终喷记录")
  @Log(name = "新建造型终喷记录", type = OperationType.OPERATION_TYPE_ADD)
  public Integer addFinalSpray(@Validated @RequestBody @Request FinalSprayRequest finalSprayRequest) {
    return moldService.addFinalSprayRecord(finalSprayRequest.convert2Model());
  }

  @GetMapping(value = "/finalSpray/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询造型终喷记录")
  @Log(name = "查询造型终喷记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<FinalSprayRecord> getFinalSprayRecordList(
          @RequestParam Integer preShiftId,
          @RequestParam(required = false, defaultValue = "1") Integer currentPage,
          @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    Page<FinalSprayRecord> page = moldService.findFinalSprayList(preShiftId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PutMapping(value = "/finalSpray/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改造型终喷记录")
  @Log(name = "修改造型终喷记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void editFinalSpray(@PathVariable Integer id, @Validated @RequestBody @Request FinalSprayRequest finalSprayRequest) {
    moldService.editFinalSpray(id, finalSprayRequest.convert2Model());
  }

  @GetMapping(value = "/spcChart/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询造型SPC控制图数据")
  @Log(name = "查询造型SPC控制图数据")
  @PreAuthorize("isAuthenticated()")
  public List<SPCChartData> getSPCChartList(
          @RequestParam() String beginDate,
          @RequestParam() String endDate,
          @RequestParam() String type,
          @RequestParam() Integer lineNo,
          @RequestParam(required = false, defaultValue = "") String inspectorId,
          @RequestParam(required = false, defaultValue = "") String operatorId) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    parameterMap.put("lineNo", lineNo);
    if (!StringUtils.isEmpty(inspectorId)) { parameterMap.put("inspectorId", inspectorId); }
    if (!StringUtils.isEmpty(operatorId)) { parameterMap.put("operatorId", operatorId); }
    return moldService.findSPCChartList(type, parameterMap);
  }
}
