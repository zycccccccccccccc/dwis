package tech.hciot.dwis.business.interfaces.api;

import static tech.hciot.dwis.base.jwt.JwtTokenUtil.getUsername;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.hciot.dwis.base.dto.AccountRequest;
import tech.hciot.dwis.base.dto.AccountResponse;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.dto.ResetPasswordRequest;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.AccountService;
import tech.hciot.dwis.business.application.AuthorityService;
import tech.hciot.dwis.business.domain.model.AuthorityTree;
import tech.hciot.dwis.business.interfaces.assembler.SmCenterAssembler;
import tech.hciot.dwis.business.interfaces.dto.ChangePasswordRequest;

@RestController
@Api(tags = "权限管理")
@Slf4j
public class SmController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private SmCenterAssembler smCenterAssembler;

  @Autowired
  private AuthorityService authorityService;

  @PostMapping(value = "/oauth/password", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public void changePassword(@Validated @RequestBody ChangePasswordRequest changePasswordRequest, Principal principal) {
    accountService.changePassword(getUsername(principal), changePasswordRequest);
  }

  @PostMapping(value = "/oauth/resetpassword", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('SERVICE_CALL')")
  public void resetPassword(@Validated @RequestBody ResetPasswordRequest resetPasswordRequest) {
    accountService.resetPassword(resetPasswordRequest);
  }

  @GetMapping(value = "/oauth/info", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public JSONObject getUserInfo(Principal principal, HttpServletRequest request) {
    accountService.checkRepeatLogin(getUsername(principal), request);
    JwtTokenUser user = JwtTokenUtil.toJwtTokenUser(principal);
    JSONObject userInfo = accountService.getUserInfoJSON(user.getAccountId());
    return userInfo;
  }

  @GetMapping(value = "/oauth/authority", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public List<AuthorityTree> getAuthority() {
    return authorityService.getAuthorityTree();
  }

  @PostMapping(value = "/oauth/account", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('AddAccount','SERVICE_CALL')")
  public void addAccount(@Validated @RequestBody AccountRequest accountRequest) {
    accountService.createAccountAndRole(smCenterAssembler.toAccount(accountRequest), accountRequest.getRole());
  }

  @DeleteMapping(value = "/oauth/account/{username}")
  @PreAuthorize("hasAuthority('SERVICE_CALL')")
  public void deleteAccount(@PathVariable String username) {
    accountService.deleteAccount(username);
  }

  @GetMapping(value = "/oauth/account", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public PageDataResponse<AccountResponse> findAccount(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String mobile,
      @RequestParam(required = false) String role,
      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(required = false, defaultValue = "1") Integer currentPage) {
    currentPage = currentPage > 0 ? currentPage - 1 : currentPage;
    return smCenterAssembler.toPageDataResponse(accountService.findAccount(username, mobile, role, currentPage, pageSize));
  }

  @PostMapping(value = "/oauth/logout")
  @PreAuthorize("isAuthenticated()")
  public void logout(Principal principal) {
    accountService.removeLoginUserInfo(getUsername(principal));
  }
}
