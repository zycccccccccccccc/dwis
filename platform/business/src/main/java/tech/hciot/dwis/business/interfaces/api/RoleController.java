package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.AccountService;
import tech.hciot.dwis.business.application.RoleService;
import tech.hciot.dwis.business.interfaces.assembler.SmCenterAssembler;
import tech.hciot.dwis.business.interfaces.dto.AddRoleRequest;
import tech.hciot.dwis.business.interfaces.dto.RoleResponse;

@RestController
@Api(tags = "角色管理")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private SmCenterAssembler smCenterAssembler;

  @GetMapping(value = "/oauth/role", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<RoleResponse> getRolePage(
      @RequestParam(required = false, defaultValue = "1") Integer currentPage,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return smCenterAssembler
        .toRoleListResponse(roleService.getAllRole(currentPage, pageSize));
  }

  @GetMapping(value = "/oauth/role/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('QueryRole','SERVICE_CALL')")
  public List<RoleResponse> getRoleList() {
    return smCenterAssembler
        .toRoleResponseList(roleService.getRoleList());
  }

  @GetMapping(value = "/oauth/role/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public RoleResponse getRoleById(@PathVariable("id") String id) {
    return smCenterAssembler.toRoleResponse(roleService.getRole(id));
  }

  @PostMapping(value = "/oauth/role", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public void addRole(@Validated @RequestBody AddRoleRequest addRoleRequest) {
    roleService.addRole(addRoleRequest);
  }

  @PutMapping(value = "/oauth/role/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public void editRoleById(@PathVariable("id") String id, @RequestBody AddRoleRequest addRoleRequest) {
    roleService.editRole(id, addRoleRequest);
  }

  @DeleteMapping(value = "/oauth/role/{id}")
  @PreAuthorize("isAuthenticated()")
  public void deleteRoleById(@PathVariable("id") String id) {
    roleService.deleteRole(id);
  }

  @PutMapping(value = "/oauth/user/{username}/role", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public void editUserRole(@PathVariable("username") String username, @RequestBody List<String> roleIdList) {
    roleService.editUserRole(username, roleIdList);
  }

  @GetMapping(value = "/oauth/user/{username}/role", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('SERVICE_CALL')")
  public List<String> findUserRole(@PathVariable("username") String username) {
    return accountService.getAccountRoles(username);
  }
}
