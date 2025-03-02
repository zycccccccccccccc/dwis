package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
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
import tech.hciot.dwis.business.application.StaffService;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddStaffRequest;
import tech.hciot.dwis.business.interfaces.dto.ModifyStaffRequest;
import tech.hciot.dwis.business.interfaces.dto.StaffDetail;
import tech.hciot.dwis.business.interfaces.dto.StaffIdAndNameResponse;

@RestController
@RequestMapping(value = "/staff")
@Api(tags = "员工管理")
public class AccountController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private StaffService staffService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询员工列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<StaffDetail> find(
      @RequestParam(required = false) String operatorId,
      @RequestParam(required = false) String operatorName,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) Integer depId,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<StaffDetail> page = staffService.find(operatorId, operatorName, status, depId, currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询员工工号列表")
  @PreAuthorize("isAuthenticated()")
  public List<StaffIdAndNameResponse> findOperatorIdList(
      @RequestParam(required = false) String location,
      @RequestParam(required = false, defaultValue = "") String keyword,
      @RequestParam(required = false) Boolean isLeader,
      @RequestParam(required = false) Integer depId,
      @RequestParam(required = false) Integer stationId,
      @RequestParam(required = false, defaultValue = "1000") Integer limit) {
    return staffService.findOperatorIdList(keyword, location, isLeader, depId, stationId, limit);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加员工")
  @Log(name = "添加员工", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Account add(@Validated @RequestBody @Request AddStaffRequest request) {
    return staffService.add(request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑员工")
  @Log(name = "编辑员工", type = OperationType.OPERATION_TYPE_MODIFY, operationObject = "staff")
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id String id,
      @RequestBody @Request ModifyStaffRequest request) {
    staffService.modify(id, request);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除员工")
  @Log(name = "删除员工", type = OperationType.OPERATION_TYPE_DELETE, operationObject = "staff")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable @Id String id) {
    staffService.delete(id);
  }
}
