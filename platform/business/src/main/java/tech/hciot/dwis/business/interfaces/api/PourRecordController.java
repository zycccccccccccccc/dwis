package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.PourRecordService;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.PourDelayStatRequest;
import tech.hciot.dwis.business.interfaces.dto.PourDelayStatResponse;

@RestController
@RequestMapping(value = "/pour")
@Api(tags = "浇注信息")
@Slf4j
public class PourRecordController {

  @Autowired
  private PourRecordService pourRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询浇注信息")
  public PageDataResponse<PourRecord> find(
      @RequestParam(required = false) Integer ladleId,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(pourRecordService.findPourRecord(null, null, null,
        ladleId, currentPage, pageSize));
  }

  @PutMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "保存浇注记录")
  @Log(name = "保存浇注记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void save(@RequestBody @Request PourRecord request) {
    log.info("begin pour/save");
    List<PourRecord> list = new ArrayList<>();
    list.add(request);
    pourRecordService.modifyList(list, PourRecord.BZ_POUR_UNCOMMIT);
    log.info("finish pour/save");
  }

  @DeleteMapping(value = "/{id}")
  @ApiOperation(value = "删除浇注记录")
  @Log(name = "删除浇注记录", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    pourRecordService.delete(id);
  }

  @PutMapping(value = "/{id}")
  @ApiOperation(value = "修改浇注记录")
  @Log(name = "修改浇注记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modifyRecord(@PathVariable @Id Integer id, @RequestBody @Request PourRecord pourRecord) {
    pourRecordService.modifyRecord(id, pourRecord);
  }

  @PutMapping(value = "{id}/commit", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "提交浇注记录")
  @Log(name = "提交浇注记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void commit(@PathVariable Integer id,
      @RequestBody @Request List<PourRecord> pourRecordList) {
    pourRecordList.forEach(pourRecord -> {
      pourRecord.setLadleId(id);
    });
    pourRecordService.modifyList(pourRecordList, PourRecord.BZ_POUR_COMMITTED);
  }

  @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑浇注记录")
  @Log(name = "编辑浇注记录", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modify(@RequestBody @Request List<PourRecord> request,
      @RequestParam(required = true, defaultValue = "1") Integer bz) {
    pourRecordService.modifyList(request, bz);
  }

  @GetMapping(value = "/delaystat", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "浇注延时统计")
  @PreAuthorize("isAuthenticated()")
  public PourDelayStatResponse delayStat(@RequestParam Integer heatRecordId) {
    return pourRecordService.delayStat(heatRecordId);
  }

  @PutMapping(value = "/delaystat/{heatRecordId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑浇注延时信息")
  @Log(name = "编辑浇注延时信息", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void delayStat(@PathVariable Integer heatRecordId,
      @RequestBody @Request PourDelayStatRequest request) {
    pourRecordService.modifyDelayStat(heatRecordId, request);
  }

  @GetMapping(value = "/unpacktime", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "获取开箱时间")
  @PreAuthorize("isAuthenticated()")
  public String unpackTime(
      @RequestParam String design,
      @RequestParam Double pourTemp,
      @RequestParam String pourTime) {
    return pourRecordService.computeUnpackTime(design, pourTemp, pourTime);
  }

  @GetMapping(value = "/pit/committed", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询开箱流程已提交的列表")
  public PageDataResponse<PourRecord> findCommittedPitPourRecords(
      @RequestParam(required = false) Integer pitSeq,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(pourRecordService.findCommittedPitPourRecords(pitSeq, pageSize, currentPage));
  }

  @RequestMapping(value = "/sse", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ApiOperation(value = "监听PourRecord修改")
  @PreAuthorize("permitAll()")
  public SseEmitter sse() {
    final SseEmitter emitter = new SseEmitter(-1L);
    pourRecordService.addSseEmitters(emitter);
    return emitter;
  }
}
