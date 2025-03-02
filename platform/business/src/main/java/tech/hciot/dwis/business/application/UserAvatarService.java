package tech.hciot.dwis.business.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.hciot.dwis.business.domain.UsersRepository;
import tech.hciot.dwis.business.domain.model.User;
import tech.hciot.dwis.business.interfaces.dto.UserAvatarResponse;

@Service
@Slf4j
public class UserAvatarService {

  @Autowired
  private UsersRepository usersRepository;

  public UserAvatarResponse uploadAvatar(String username, MultipartFile avatar) {
    String avatarBase64 = saveImg(avatar);
    User updateUser = usersRepository.findByUsername(username);
    updateUser.setAvatar(avatarBase64);
    usersRepository.save(updateUser);
    return UserAvatarResponse.builder().avatar(avatarBase64).build();
  }

  private String saveImg(MultipartFile avatar) {
    String contentType = avatar.getContentType();
    String avatarBase64 = new String("data:" + contentType + ";base64,");
    List<Byte> byteList = new LinkedList<Byte>();
    InputStream fileInputStream = null;
    try {
      fileInputStream = avatar.getInputStream();
      byte[] bs = new byte[1024];
      while ((fileInputStream.read(bs)) != -1) {
        for(byte b : bs) {
          byteList.add(b);
        }
      }
      byte[] bsout = new byte[byteList.size()];
      int i = 0;
      for(Byte b : byteList) {
        bsout[i] = b;
        i++;
      }
      avatarBase64 = avatarBase64 + Base64.getEncoder().encodeToString(bsout);
      return avatarBase64;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return "";
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch (IOException e) {
        }
      }
    }
  }
}
