package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.FurnaceService;
import tech.hciot.dwis.business.domain.model.AdditionMaterialTable;
import tech.hciot.dwis.business.domain.model.ChargeMaterialTable;
import tech.hciot.dwis.business.domain.model.DipelectrodeTable;
import tech.hciot.dwis.business.domain.model.FurnaceTapCurrent;
import tech.hciot.dwis.business.domain.model.FurnaceTapDetail;
import tech.hciot.dwis.business.domain.model.FurnaceTapTable;
import tech.hciot.dwis.business.domain.model.O2blowingTable;
import tech.hciot.dwis.business.domain.model.TempmeasureTable;
import tech.hciot.dwis.business.domain.model.VoltChangeTable;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/furnace")
@Api(tags = "熔炼业务")
public class FurnaceController {

  @Autowired
  private FurnaceService furnaceService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询熔炼记录")
  public PageDataResponse<FurnaceTapTable> find(
    @RequestParam(required = false) String furnaceNo,
    @RequestParam(required = false) String castDate,
    @RequestParam(required = false) String furnaceSeq,
    @RequestParam(required = false, defaultValue = "10") Integer pageSize,
    @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(furnaceService.find(furnaceNo, castDate, furnaceSeq,
      currentPage, pageSize));
  }

  @GetMapping(value = "detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取熔炼信息详情")
  public Optional<FurnaceTapDetail> findDetail(@PathVariable(required = false) Integer id) {
    return furnaceService.findDetail(id);
  }

  @GetMapping(value = "current", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取当前已保存的熔炼信息")
  public FurnaceTapCurrent findCurrent(@RequestParam(required = false) Integer furnaceNo) {
    return furnaceService.findCurrent(furnaceNo);
  }

  @GetMapping(value = "last", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "获取上一条已提交的熔炼信息")
  public FurnaceTapTable findLast(@RequestParam(required = false) Integer furnaceNo) {
    return furnaceService.findLast(furnaceNo);
  }

  @GetMapping(value = "seq", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询炉次号")
  @PreAuthorize("isAuthenticated()")
  public Integer findHeatSeq(String date, Integer furnaceNo) {
    return furnaceService.findCurrentSeq(date, furnaceNo);
  }

  @PutMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存熔炼记录")
  @Log(name = "保存熔炼记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public FurnaceTapTable save(@Validated @RequestBody @Request FurnaceTapTable request) {
    return furnaceService.save(request);
  }

  @PutMapping(value = "commit", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "提交熔炼记录")
  @Log(name = "提交熔炼记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public FurnaceTapTable commit(@RequestBody @Request FurnaceTapTable request) {
    return furnaceService.commit(request);
  }

  @PutMapping(value = "chargematerial", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存加料信息")
  @Log(name = "保存加料信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public ChargeMaterialTable saveChargeMaterial(@Validated @RequestBody @Request ChargeMaterialTable request) {
    return furnaceService.saveChargeMaterial(request);
  }

  @PutMapping(value = "additionmaterial", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存添加剂信息")
  @Log(name = "保存加料信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public AdditionMaterialTable saveAdditionMaterial(@Validated @RequestBody @Request AdditionMaterialTable request) {
    return furnaceService.saveAdditionMaterial(request);
  }

  @PutMapping(value = "o2blowing", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存吹氧信息")
  @Log(name = "保存加料信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public O2blowingTable saveO2blowing(@Validated @RequestBody @Request O2blowingTable request) {
    return furnaceService.saveO2blowing(request);
  }

  @PutMapping(value = "dipelectrode", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存浸电极信息")
  @Log(name = "保存加料信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public DipelectrodeTable saveDipelectrode(@Validated @RequestBody @Request DipelectrodeTable request) {
    return furnaceService.saveDipelectrode(request);
  }

  @PutMapping(value = "voltchange", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存电压变化信息")
  @Log(name = "保存加料信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public VoltChangeTable saveVoltChange(@Validated @RequestBody @Request VoltChangeTable request) {
    return furnaceService.saveVoltChange(request);
  }

  @PutMapping(value = "tempmeasure", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存测温信息")
  @Log(name = "保存加料信息", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public TempmeasureTable saveTempmeasure(@Validated @RequestBody @Request TempmeasureTable request) {
    return furnaceService.saveTempmeasure(request);
  }
}
