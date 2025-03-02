package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.MachineRecordService;
import tech.hciot.dwis.business.domain.model.MachineParams;
import tech.hciot.dwis.business.domain.model.MachineRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/machine")
@Api(tags = "机加工业务")
public class MachineRecordController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private MachineRecordService machineRecordService;

  @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询机加工状态列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<MachineRecord> find(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<MachineRecord> page = machineRecordService.find(currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加机加工状态")
  @Log(name = "添加机加工状态", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody @Request MachineRecord request) {
    return machineRecordService.add(request);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑机加工状态")
  @Log(name = "编辑机加工状态", type = OperationType.OPERATION_TYPE_MODIFY)
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
                     @RequestBody @Request MachineRecord request) {
    machineRecordService.modify(id, request);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除机加工状态")
  @Log(name = "删除机加工状态", type = OperationType.OPERATION_TYPE_DELETE)
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id Integer id) {
    machineRecordService.delete(id);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查看机加工状态详情")
  @PreAuthorize("isAuthenticated()")
  public void findById(@PathVariable Integer id) {
    machineRecordService.findById(id);
  }

  @GetMapping(value = "/params", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "查询机加工工艺参数")
  public List<MachineParams> findEnabledParams(@RequestParam(required = true) String process) {
    return machineRecordService.findProcessParams(process);
  }
}
