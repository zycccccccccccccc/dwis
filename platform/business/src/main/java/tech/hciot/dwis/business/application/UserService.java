package tech.hciot.dwis.business.application;

import feign.FeignException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.dto.ResetPasswordRequest;
import tech.hciot.dwis.business.domain.UsersRepository;
import tech.hciot.dwis.business.domain.model.User;
import tech.hciot.dwis.business.infrastructure.exception.ErrorEnum;

@Service
@Slf4j
public class UserService {

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private PasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private SmCenterRemoteService smCenterRemoteService;


  public User getUserInfo(String username) {
    return usersRepository.findByUsername(username);
  }

  public void updateUserInfo(String username, User user) {
    update(username, user);
  }

  public void updateUserStatus(String username, Integer status) {
    usersRepository.findByIdOrUsername(username, username).ifPresent(user -> {
      user.setStatus(status);
      usersRepository.save(user);
    });
  }

  private void update(String username, User user) {
    User updateUser = usersRepository.findByUsername(username);

    if (StringUtils.isNotEmpty(user.getNickname())) {
      updateUser.setNickname(user.getNickname());
    }
    if (StringUtils.isNotEmpty(user.getEmail())) {
      updateUser.setEmail(user.getEmail());
    }

    if (StringUtils.isNotEmpty(user.getAvatar())) {
      updateUser.setAvatar(user.getAvatar());
    }

    if (StringUtils.isNotEmpty(user.getMobile())) {
      updateUser.setMobile(user.getMobile());
    }

    if (user.getStatus() != null) {
      updateUser.setStatus(user.getStatus());
    }
    usersRepository.save(updateUser);
  }

  public void resetPassword(String identity, String password) {
    usersRepository.findByIdOrUsername(identity, identity).ifPresent(user -> {
      user.setPassword(bCryptPasswordEncoder.encode(password));
      usersRepository.save(user);
    });
  }

  public void firstResetPassword(ResetPasswordRequest resetPasswordRequest) {
    User user = usersRepository.findByUsername(resetPasswordRequest.getUsername());
    Optional.ofNullable(user).map(u -> {
      u.setPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
      u.setLoginStatus(User.LOGIN_STATUS_NOT_FIRST_LOGIN);
      usersRepository.save(u);
      return Optional.empty();
    }).orElseThrow(ErrorEnum.ACCOUNT_NOT_EXIST::getPlatformException);
  }

  public void deleteUser(String username) {
    try {
      smCenterRemoteService.deleteAccount(username);
    } catch (FeignException e) {
      log.error(e.contentUTF8());
      throw ErrorEnum.ACCOUNT_DELETE_FAILED.getPlatformException();
    }
  }

  public void editUser(String username, User editUser) {
    usersRepository.findByIdOrUsername(username, username).map(user -> {
      user.setEmail(editUser.getEmail());
      user.setMobile(editUser.getMobile());
      user.setNickname(editUser.getNickname());
      usersRepository.save(user);
      return Optional.empty();
    }).orElseThrow(ErrorEnum.ACCOUNT_NOT_EXIST::getPlatformException);
  }
}
