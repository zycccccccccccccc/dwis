package tech.hciot.dwis.business.interfaces.assembler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tech.hciot.dwis.base.dto.AccountRequest;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.business.application.AccountService;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.domain.model.Role;
import tech.hciot.dwis.business.interfaces.dto.RoleResponse;
import tech.hciot.dwis.business.interfaces.dto.SaasUser;

@Component
@Slf4j
public class SmCenterAssembler {

  @Autowired
  private PasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private AccountService accountService;

  public SaasUser toSaasUser(Account account) {
    List<SimpleGrantedAuthority> authorities = Optional.ofNullable(account.getAuthorities())
        .map(authorityList ->
            authorityList.stream().map(auth -> new SimpleGrantedAuthority(auth.getAuthorityName()))
                .collect(Collectors.toList())
        ).orElse(Collections.emptyList());
    List<String> roleNames = accountService.getAccountRoles(account.getUsername());
    String operationId = accountService.getOperationId(account.getId());
    Integer depId = account.getDepId();
    SaasUser user = new SaasUser(account.getUsername(), account.getPassword(), authorities, account.getId(), operationId, roleNames, depId);
    return user;
  }

  public Account toAccount(AccountRequest accountRequest) {
    Account account = new Account();
    account.setUsername(accountRequest.getUsername());
    account.setPassword(bCryptPasswordEncoder.encode(accountRequest.getPassword()));
    account.setStatus(Account.STATUS_ENABLED);
    account.setNickname(accountRequest.getNickname());
    account.setMobile(accountRequest.getMobile());
    return account;
  }

  public RoleResponse toRoleResponse(Role role) {
    return RoleResponse.builder().id(role.getId()).roleName(role.getRoleName()).remark(
        StringUtils.defaultString(role.getRemark(), "")).authorities(
        role.getAuthorities().stream().map(authority -> authority.getId()).collect(Collectors.toList())
    ).build();
  }

  public List<RoleResponse> toRoleResponseList(List<Role> roleList) {
    return roleList.stream().map(role ->
        RoleResponse.builder().id(role.getId()).roleName(role.getRoleName()).remark(role.getRemark()).build()
    ).collect(Collectors.toList());
  }

  public PageDataResponse<RoleResponse> toRoleListResponse(Page<Role> elements) {
    return new PageDataResponse<>(elements.getTotalElements(), elements.getTotalPages(), elements.getSize(),
        elements.getNumber(), elements.stream().map(
        role -> toRoleResponse(role))
        .collect(Collectors.toList()));
  }

  public <T> PageDataResponse<T> toPageDataResponse(Page<T> elements) {
    return new PageDataResponse<>(elements.getTotalElements(), elements.getTotalPages(),
        elements.getSize(), elements.getNumber(), elements.getContent());
  }

}
