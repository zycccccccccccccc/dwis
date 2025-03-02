package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.LadleRecordService;
import tech.hciot.dwis.business.domain.model.LadleRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/ladlerecord")
@Api(tags = "小包底注信息")
public class LadleRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private LadleRecordService ladleRecordService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询小包底注列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<LadleRecord> find(
      @RequestParam(required = false) Integer heatRecordId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<LadleRecord> page = ladleRecordService.find(heatRecordId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加小包底注")
  @Log(name = "添加小包底注", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public LadleRecord add(@Validated @RequestBody @Request LadleRecord request) {
    return ladleRecordService.add(request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑小包底注")
  @Log(name = "编辑小包底注", type = OperationType.OPERATION_TYPE_MODIFY, operationObject = "ladle_record")
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id, @RequestBody @Request LadleRecord request) {
    ladleRecordService.modify(id, request);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除小包底注")
  @Log(name = "删除小包底注", type = OperationType.OPERATION_TYPE_DELETE, operationObject = "ladle_record")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    ladleRecordService.delete(id);
  }
}
