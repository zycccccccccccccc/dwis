package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.dto.AccountResponse;
import tech.hciot.dwis.base.dto.ResetPasswordRequest;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.AccountRoleRepository;
import tech.hciot.dwis.business.domain.AccountRoleViewRepository;
import tech.hciot.dwis.business.domain.AccountsRepository;
import tech.hciot.dwis.business.domain.model.Account;
import tech.hciot.dwis.business.domain.model.AccountRole;
import tech.hciot.dwis.business.domain.model.AccountRoleView;
import tech.hciot.dwis.business.domain.model.Role;
import tech.hciot.dwis.business.infrastructure.exception.ErrorEnum;
import tech.hciot.dwis.business.infrastructure.logininfo.domain.LoginUserInfoRepository;
import tech.hciot.dwis.business.interfaces.dto.ChangePasswordRequest;

@Service
@Slf4j
public class AccountService {

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private PasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private AccountRoleRepository accountRoleRepository;

  @Autowired
  private AccountRoleViewRepository accountRoleViewRepository;

  @Autowired
  private LoginUserInfoRepository loginUserInfoRepository;

  public Page<AccountResponse> findAccount(String username, String mobile, String roleId, Integer currentPage, Integer pageSize) {
    Specification<AccountRoleView> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(username)) {
        list.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
      }
      if (StringUtils.isNotBlank(mobile)) {
        list.add(criteriaBuilder.like(root.get("mobile"), "%" + mobile + "%"));
      }
      if (StringUtils.isNotBlank(roleId)) {
        Join<AccountRoleView, Role> join = root.join("roles", JoinType.INNER);
        Path<String> path = join.get("id");
        list.add(criteriaBuilder.equal(path, roleId));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return accountRoleViewRepository.findAll(specification, pageable).map(
        accountRoleView -> AccountResponse.builder().avatar(accountRoleView.getAvatar())
            .loginStatus(accountRoleView.getLoginStatus())
            .mobile(accountRoleView.getMobile()).nickname(accountRoleView.getNickname()).status(accountRoleView.getStatus())
            .username(accountRoleView.getUsername())
            .roles(accountRoleView.getRoles().stream().map(role -> role.getId()).collect(Collectors.toList()))
            .build());
  }

  @Transactional
  public Account createAccountAndRole(Account account, String roleId) {
    accountsRepository.findByUsername(account.getUsername()).ifPresent(a -> {
      throw ErrorEnum.ACCOUNT_EXIST.getPlatformException();
    });
    account = accountsRepository.saveAndFlush(account);
    AccountRole accountRole = new AccountRole();
    accountRole.setAccountId(account.getId());
    accountRole.setRoleId(roleId);
    accountRoleRepository.save(accountRole);
    return account;
  }

  public void update(String id, Account newAccount) {
    accountsRepository.findById(id).ifPresent(account -> {
      BeanUtil.copyProperties(newAccount, account, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      accountsRepository.save(account);
    });
  }

  public void changePassword(String username, ChangePasswordRequest changePasswordRequest) {
    Account account = accountsRepository.findByUsername(username).get();
    Optional.ofNullable(account)
        .filter(account1 -> bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), account1.getPassword()))
        .map(account2 -> {
          account2.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
          return account2;
        }).orElseThrow(() -> ErrorEnum.PASSWORD_NOT_VALID.getPlatformException());
    accountsRepository.save(account);
  }

  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    Account account = accountsRepository.findByUsername(resetPasswordRequest.getUsername()).get();
    Optional.ofNullable(account).ifPresent(account1 -> {
      account1.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
      account1.setLoginStatus(1);
      accountsRepository.save(account1);
    });
  }

  public Account getAccountById(String id) {
    return accountsRepository.findById(id).orElseThrow(ErrorEnum.ACCOUNT_NOT_EXIST::getPlatformException);
  }

  public Account getAccount(String username) {
    return accountsRepository.findByUsername(username).orElseThrow(ErrorEnum.ACCOUNT_NOT_EXIST::getPlatformException);
  }

  public List<String> getAccountRoles(String username) {
    return accountsRepository.findAccountRoles(username);
  }

  @Transactional
  public void deleteAccount(String username) {
    accountsRepository.findByUsername(username).ifPresent(account -> {
      accountRoleRepository.deleteByAccountId(account.getId());
      accountsRepository.deleteByUsername(username);
    });
  }

  public void deleteAccountById(String id) {
    accountsRepository.findById(id).ifPresent(account -> {
      accountRoleRepository.deleteByAccountId(account.getId());
      accountsRepository.deleteByUsername(account.getUsername());
    });
  }

  public String getOperationId(String id) {
    return accountsRepository.findByUsername(id).map(account -> account.getUsername()).orElse("0");
  }

  public JSONArray getLocation(String username) {
    JSONArray locationArray = new JSONArray();
    String location = accountsRepository.findByUsername(username)
        .orElse(Account.builder().location("").build())
        .getLocation();
    String[] locations = location.split(",");
    for (String s : locations) {
      locationArray.add(s);
    }
    return locationArray;
  }

  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    System.out.println(encoder.encode("dwis_web_app"));
  }

  public JSONObject getUserInfoJSON(String accountId) {
    Account account = accountsRepository.findById(accountId).get();
    String username = account.getUsername();
    String nickname = account.getNickname();
    JSONArray locationArray = new JSONArray();
    String location = account.getLocation().substring(1, account.getLocation().length() - 1);
    String[] locations = location.split(",");
    for (String s : locations) {
      locationArray.add(s);
    }
    List<String> roleList = accountsRepository.findAccountRoles(username);

    JSONObject userInfo = new JSONObject();
    userInfo.fluentPut("username", username)
            .fluentPut("nickname",nickname)
            .fluentPut("authorities", locationArray)
            .fluentPut("roles", roleList)
            .fluentPut("operationId", account.getUsername())
            .fluentPut("depId", account.getDepId());
    return userInfo;
  }

  public void checkRepeatLogin(String username, HttpServletRequest request) {

    loginUserInfoRepository.findByUsername(username).ifPresent(loginUserInfo -> {
      String ip = getRemoteIp(request);
      Date last24 = DateUtils.addHours(new Date(), -24);
      if (!ip.equals(loginUserInfo.getIp()) && loginUserInfo.getLastActiveTime().after(last24)) {
        throw PlatformException.badRequestException("该账户已在 " + loginUserInfo.getIp() + " 登录，请检查登录账户是否正确！");
      }
    });

    String remoteIp = request.getHeader("X-Forwarded-For");
    if (StringUtils.isBlank(remoteIp)) {
      remoteIp = request.getRemoteAddr();
    }
    if (StringUtils.isBlank(remoteIp)) {
      remoteIp = "127.0.0.1";
    }
  }

  public void removeLoginUserInfo(String username) {
    loginUserInfoRepository.findByUsername(username).ifPresent(loginUserInfo -> {
      loginUserInfoRepository.delete(loginUserInfo);
    });
  }

  private String getRemoteIp(HttpServletRequest request) {
    String remoteIp = request.getHeader("X-Forwarded-For");
    if (StringUtils.isBlank(remoteIp)) {
      remoteIp = request.getRemoteAddr();
    }
    if (StringUtils.isBlank(remoteIp)) {
      remoteIp = "127.0.0.1";
    }
    return remoteIp;
  }
}
