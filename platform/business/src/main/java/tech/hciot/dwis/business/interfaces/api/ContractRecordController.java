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
import tech.hciot.dwis.business.application.ContractRecordService;
import tech.hciot.dwis.business.domain.model.ContractRecord;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.ContractRecordRequest;

@RestController
@RequestMapping(value = "/contract")
@Api(tags = "合同管理业务")
public class ContractRecordController {

  @Autowired
  private ContractRecordService contractRecordService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "新建合同记录")
  @Log(name = "新建合同记录", type = OperationType.OPERATION_TYPE_ADD)
  public void addContractRecord(@Validated @RequestBody @Request ContractRecordRequest contractRecordRequest) {
    contractRecordService.addContractRecord(contractRecordRequest.convert2Model());
  }

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询合同记录")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<ContractRecord> find(
      @RequestParam(required = false) String contractNo,
      @RequestParam(required = false) Integer enabled,
      @RequestParam(required = false) String createDate,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;

    return mgrAssembler.toPageDataResponse(contractRecordService.find(contractNo, enabled, createDate, currentPage, pageSize));
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询合同列表")
  @PreAuthorize("isAuthenticated()")
  public List<ContractRecord> getList() {
    return contractRecordService.getList();
  }

  @PutMapping(value = "/{id}/close", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @ApiOperation(value = "结束合同")
  @Log(name = "结束合同", type = OperationType.OPERATION_TYPE_MODIFY)
  public void closeContractRecord(@PathVariable Integer id) {
    contractRecordService.closeContractRecord(id);
  }
}
