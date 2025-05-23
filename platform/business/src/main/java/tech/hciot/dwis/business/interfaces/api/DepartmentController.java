package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
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
import tech.hciot.dwis.business.application.DepartmentService;
import tech.hciot.dwis.business.domain.model.Department;
import tech.hciot.dwis.business.infrastructure.log.OperationType;
import tech.hciot.dwis.business.infrastructure.log.aspect.Id;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.infrastructure.log.aspect.Request;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;

@RestController
@RequestMapping(value = "/department")
@Api(tags = "部门管理")
public class DepartmentController {

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private DepartmentService departmentService;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查询部门列表")
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<Department> find(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize, Principal principal) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    Page<Department> page = departmentService.find(currentPage, pageSize);
    return mgrAssembler.toPageDataResponse(page);
  }

  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "添加部门")
  @Log(name = "添加部门", type = OperationType.OPERATION_TYPE_ADD)
  @PreAuthorize("isAuthenticated()")
  public Integer add(@Validated @RequestBody Department request) {
    return departmentService.add(request);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "编辑部门")
  @Log(name = "编辑部门", type = OperationType.OPERATION_TYPE_MODIFY, operationObject = "department")
  @PreAuthorize("isAuthenticated()")
  public void modify(@PathVariable @Id Integer id,
                     @RequestBody @Request Department request) {
    departmentService.modify(id, request);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "删除部门")
  @Log(name = "删除部门", type = OperationType.OPERATION_TYPE_DELETE, operationObject = "department")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable Integer id) {
    departmentService.delete(id);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "查看部门详情")
  @PreAuthorize("isAuthenticated()")
  public void findById(@PathVariable Integer id) {
    departmentService.findById(id);
  }
}
