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
import tech.hciot.dwis.business.application.ChemistryDetailService;
import tech.hciot.dwis.business.application.HeatRecordService;
import tech.hciot.dwis.business.domain.model.ChemistryDetail;
import tech.hciot.dwis.business.domain.model.HeatRecord;
import tech.hciot.dwis.business.domain.model.LadleAdditionRecord;
import tech.hciot.dwis.business.domain.model.PourParams;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.HElementHeatResponse;
import tech.hciot.dwis.business.interfaces.dto.LadleAddRequest;
import tech.hciot.dwis.business.interfaces.dto.LadleAddsResponse;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/heatrecord")
@Api(tags = "炉信息")
public class HeatRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private HeatRecordService heatRecordService;

  @Autowired
  private ChemistryDetailService chemistryDetailService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询炉信息列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<HeatRecord> find(
      @RequestParam(required = false) Integer furnaceNo,
      @RequestParam(required = false) Integer heatSeq,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<HeatRecord> page = heatRecordService.find(furnaceNo, heatSeq, null, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/heatseq", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询炉次号")
  @PreAuthorize("isAuthenticated()")
  public Integer findHeatSeq(String date, Integer furnaceNo) {
    return heatRecordService.findCurrentHeatSeq(date, furnaceNo);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加炉信息")
  @Log(name = "添加炉信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request HeatRecord request) {
    return heatRecordService.add(request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑炉信息")
  @Log(name = "编辑炉信息", type = OperationType.OPERATION_TYPE_MODIFY, operationObject = "heat_record")
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
      @RequestBody @Request HeatRecord request) {
    heatRecordService.modify(id, request);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除炉信息")
  @Log(name = "删除炉信息", type = OperationType.OPERATION_TYPE_DELETE, operationObject = "heat_record")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    heatRecordService.delete(id);
  }

  @GetMapping(value = "/helement", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询氢元素录入业务炉次号")
  @PreAuthorize("isAuthenticated()")
  public List<HElementHeatResponse> findHElementHeatRecord(@RequestParam String date) {
    return heatRecordService.findHElementHeatRecord(date);
  }

  @GetMapping(value = "/tapseq/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询炉次号")
  @PreAuthorize("isAuthenticated()")
  public List<Integer> findTapSeqList(@RequestParam String date) {
    return heatRecordService.findTapSeqList(date);
  }

  @GetMapping(value = "/pourParams", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询工艺参数")
  public List<PourParams> findEnabledParams(@RequestParam(required = true) String type ) {
    return heatRecordService.findPourParams(type);
  }

  @GetMapping(value = "/ladleAdds", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询底注包加料值")
  @PreAuthorize("isAuthenticated()")
  public LadleAddsResponse findLadleAddsForPour(@RequestParam Integer heatRecordId,
                                                @RequestParam String furnaceNo,
                                                @RequestParam String heatSeq) {
    return heatRecordService.findLadleAddsForPour(heatRecordId, furnaceNo, heatSeq);
  }

  @PostMapping(value = "/addLadleAdds", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加底注包加料记录")
  @Log(name = "添加底注包加料记录", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request LadleAddRequest request) {
    return heatRecordService.addLadleAdds(request);
  }

  @GetMapping(value = "/idsNewest", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询一天内最新的浇注指导确认后的heatRecordId && ladleSeq")
  @PreAuthorize("isAuthenticated()")
  public Object findHeatRecordIdNewest() {
    return heatRecordService.findIdsNewest();
  }

  @GetMapping(value = "/ladleAddsForDisplay", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询浇注指导确认后的底注包加料值")
  @PreAuthorize("isAuthenticated()")
  public LadleAddsResponse findLadleAddsForDisplay(@RequestParam Integer heatRecordId) {
    return heatRecordService.findLadleAddsForDisplay(heatRecordId);
  }

  @GetMapping(value = "/chemiForDisplay", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询茶壶包&底注包化学成分值")
  @PreAuthorize("isAuthenticated()")
  public ChemistryDetail findChemiNewest(@RequestParam Integer heatRecordId) {
    return heatRecordService.findChemiNewest(heatRecordId);
  }
}


