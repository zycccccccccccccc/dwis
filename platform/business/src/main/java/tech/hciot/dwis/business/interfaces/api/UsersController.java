package tech.hciot.dwis.business.interfaces.api;

import io.swagger.annotations.Api;
import java.security.Principal;
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
import org.springframework.web.multipart.MultipartFile;
import tech.hciot.dwis.base.dto.ResetPasswordRequest;
import tech.hciot.dwis.base.jwt.JwtTokenUtil;
import tech.hciot.dwis.business.application.AccountService;
import tech.hciot.dwis.business.application.UserAvatarService;
import tech.hciot.dwis.business.application.UserService;
import tech.hciot.dwis.business.domain.model.User;
import tech.hciot.dwis.business.infrastructure.log.aspect.Log;
import tech.hciot.dwis.business.interfaces.assembler.MgrAssembler;
import tech.hciot.dwis.business.interfaces.dto.UserAvatarResponse;
import tech.hciot.dwis.business.interfaces.dto.UserResponse;

@RestController
@Api(tags = "用户管理")
public class UsersController {

  @Autowired
  private UserService userService;

  @Autowired
  private UserAvatarService userAvatarService;

  @Autowired
  private MgrAssembler mgrAssembler;

  @Autowired
  private AccountService accountService;

  @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public UserResponse getUserInfo(Principal principal) {
    return mgrAssembler.toUserResponse(accountService.getAccount(JwtTokenUtil.getUsername(principal)));
  }

  @PutMapping(value = "/user")
  @PreAuthorize("isAuthenticated()")
  @Log(name = "更新用户信息")
  public void update(@RequestBody User user, Principal principal) {
    userService.updateUserInfo(JwtTokenUtil.getUsername(principal), user);
  }

  @PostMapping(value = "/user/avatar")
  @PreAuthorize("isAuthenticated()")
  @Log(name = "上传头像")
  public UserAvatarResponse uploadAvatar(@RequestParam("avatar") MultipartFile avatar, Principal principal) {
    return userAvatarService.uploadAvatar(JwtTokenUtil.getUsername(principal), avatar);
  }

  @PostMapping(value = "/user/{id}/status")
  @PreAuthorize("isAuthenticated()")
  @Log(name = "配置用户状态")
  public void updateUserStatus(@PathVariable("id") String id, @RequestParam Integer status) {
    userService.updateUserStatus(id, status);
  }

  @PostMapping(value = "/user/resetpass", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @Log(name = "用户首次重置密码")
  public void firstResetPassword(@Validated @RequestBody ResetPasswordRequest resetPasswordRequest, Principal principal) {
    resetPasswordRequest.setUsername(JwtTokenUtil.getUsername(principal));
    userService.firstResetPassword(resetPasswordRequest);
  }

  @PostMapping(value = "/user/{id}/resetpass", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @Log(name = "管理员用户重置密码")
  public void initPassword(@PathVariable("id") String id, @Validated @RequestBody ResetPasswordRequest resetPasswordRequest) {
    userService.resetPassword(id, resetPasswordRequest.getPassword());
  }

  @DeleteMapping(value = "/user/{username}")
  @PreAuthorize("isAuthenticated()")
  @Log(name = "删除用户")
  public void deleteUser(@PathVariable("username") String username) {
    userService.deleteUser(username);
  }

  @PutMapping(value = "/user/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  @Log(name = "编辑用户")
  public void editUser(@PathVariable("username") String username, @RequestBody User user) {
    userService.editUser(username, user);
  }
}
