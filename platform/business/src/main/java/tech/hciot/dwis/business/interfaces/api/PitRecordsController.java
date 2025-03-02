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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.PitRecordsService;
import tech.hciot.dwis.business.domain.model.PitRecords;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.PitRecordsOutRequest;
import tech.hciot.dwis.business.interfaces.dto.PitRecordsRequest;
import tech.hciot.dwis.business.interfaces.dto.PourRecordForPitRequest;

@RestController
@RequestMapping(value = "/pit")
@Api(tags = "进桶信息")
public class PitRecordsController {

  @Autowired
  private PitRecordsService pitRecordsService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建进桶记录")
  @Log(name = "新建进桶记录", type = OperationType.OPERATION_TYPE_ADD)
  public Integer addPitRecords(@Validated @RequestBody @Request PitRecordsRequest pitRecordsRequest) {
    return pitRecordsService.addPitRecords(pitRecordsRequest.convert2Model());
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "修改进桶记录")
  @Log(name = "修改进桶记录", type = OperationType.OPERATION_TYPE_MODIFY)
  public void editPitRecords(@PathVariable @Id Integer id,
      @Validated @RequestBody @Request PitRecordsRequest pitRecordsRequest) {
    pitRecordsService.editPitRecords(id, pitRecordsRequest.convert2Model());
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询进桶记录")
  public PitRecords findPitRecords(@PathVariable Integer id) {
    return pitRecordsService.findPitRecords(id);
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询出桶记录列表")
  public PageDataResponse<PitRecords> listPitRecords(
      @RequestParam(required = false) Integer pitSeq,
      @RequestParam(required = false) String outDTCal,
      @RequestParam(required = false) String outDTAct,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return mgrAssembler.toPageDataResponse(pitRecordsService.listPitRecords(pitSeq, outDTCal, outDTAct, currentPage, pageSize));
  }

  @PutMapping(value = "/{id}/save", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "保存进桶记录")
  @Log(name = "保存进桶记录", type = OperationType.OPERATION_TYPE_ADD)
  public void savePitRecords(@PathVariable Integer id, @Validated @RequestBody @Request PourRecordForPitRequest pourRecordForPitRequest) {
    pitRecordsService.savePitRecords(id, pourRecordForPitRequest.convert2Model());
  }

  @PutMapping(value = "/{id}/out", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "出桶")
  @Log(name = "出桶", type = OperationType.OPERATION_TYPE_ADD)
  public void outPitRecords(@PathVariable Integer id, @Validated @RequestBody @Request PitRecordsOutRequest pitRecordsOutRequest) {
    pitRecordsService.outPitRecords(id, pitRecordsOutRequest.convert2Model());
  }

  @PutMapping(value = "/{id}/commit", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "提交进桶记录")
  @Log(name = "提交进桶记录", type = OperationType.OPERATION_TYPE_ADD)
  public void commitPitRecords(@PathVariable Integer id,
      @RequestBody @Request List<PourRecordForPitRequest> pourRecordForPitRequestList) {
    pitRecordsService.commitPitRecords(id, mgrAssembler.toPourRecordList(pourRecordForPitRequestList));
  }

  @GetMapping(value = "/out/report", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询出桶报告")
  public List<PitRecords> findOutRecord() {
    return pitRecordsService.findOutRecord();
  }

  @GetMapping(value = "/out/seq", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询可以出桶的缓冷桶序号")
  public List<Integer> findOutRecordSeq() {
    return pitRecordsService.findOutRecordSeq();
  }
}
