package tech.hciot.dwis.business.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.business.domain.AccountRoleRepository;
import tech.hciot.dwis.business.domain.AuthorityRepository;
import tech.hciot.dwis.business.domain.RoleRepository;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.domain.model.AccountRole;
import tech.hciot.dwis.business.domain.model.Role;
import tech.hciot.dwis.business.infrastructure.exception.ErrorEnum;
import tech.hciot.dwis.business.interfaces.dto.AddRoleRequest;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private AccountRoleRepository accountRoleRepository;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AuthorityService authorityService;

  @Autowired
  private AuthorityRepository authorityRepository;

  public Page<Role> getAllRole(int currentPage, int pageSize) {
    Page<Role> pages = roleRepository.findAll(PageRequest.of(currentPage, pageSize));
    for (Role role : pages.getContent()) {
      role.setAuthorities(authorityRepository.findAllAuthorityOfLeaf(role.getId()));
    }
    return pages;
  }

  public List<Role> getRoleList() {
    return roleRepository.findAll();
  }

  public Role getRole(String id) {
    return roleRepository.findById(id).orElseThrow(ErrorEnum.ROLE_NOT_EXIST::getPlatformException);
  }

  public void addRole(AddRoleRequest addRoleRequest) {
    assertRoleNameExist(addRoleRequest.getRoleName());
    Role role = new Role();
    role.setRoleName(addRoleRequest.getRoleName());
    role.setRemark(addRoleRequest.getRemark());
    role.setAuthorities(authorityService.findByIdList(addRoleRequest.getAuthorities()));
    roleRepository.save(role);
  }

  private void assertRoleNameExist(String roleName) {
    if (roleRepository.countByRoleName(roleName) > 0) {
      throw ErrorEnum.ROLE_NAME_EXIST.getPlatformException();
    }
  }

  public void editRole(String id, AddRoleRequest addRoleRequest) {
    roleRepository.findById(id).map(role -> {
      role.setRemark(addRoleRequest.getRemark());
      role.setAuthorities(authorityService.findByIdList(addRoleRequest.getAuthorities()));
      roleRepository.save(role);
      return Optional.empty();
    }).orElseThrow(ErrorEnum.ROLE_NOT_EXIST::getPlatformException);
  }

  public void deleteRole(String id) {
    roleRepository.findById(id).map(role -> {
      if (accountRoleRepository.countByRoleId(id) > 0) {
        throw ErrorEnum.ROLE_IS_USED.getPlatformException();
      }
      roleRepository.delete(role);
      return Optional.empty();
    }).orElseThrow(ErrorEnum.ROLE_NOT_EXIST::getPlatformException);
  }

  @Transactional
  public void editUserRole(String username, List<String> roleIdList) {
    Account account = accountService.getAccount(username);
    accountRoleRepository.deleteByAccountId(account.getId());
    List<AccountRole> accountRoleList = new ArrayList<>();
    roleIdList.forEach(roleId -> {
      getRole(roleId);
      AccountRole accountRole = new AccountRole();
      accountRole.setAccountId(account.getId());
      accountRole.setRoleId(roleId);
      accountRoleList.add(accountRole);
    });
    accountRoleRepository.saveAll(accountRoleList);
  }
}
