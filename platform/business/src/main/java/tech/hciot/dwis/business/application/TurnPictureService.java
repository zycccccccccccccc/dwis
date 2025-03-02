package tech.hciot.dwis.business.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.hciot.dwis.business.domain.TurnPictureRepository;
import tech.hciot.dwis.business.domain.model.TurnPicture;
import tech.hciot.dwis.business.infrastructure.FileUtil;

@Service
@Slf4j
public class TurnPictureService {

  @Value("${dwis.staticFilePath}")
  private String staticFilePath;

  @Autowired
  private TurnPictureRepository turnPictureRepository;

  public List<TurnPicture> find() {
    return turnPictureRepository.findAll();
  }

  public void add(MultipartFile file) {
    String imgBase64 = saveImg(file);
    TurnPicture turnPicture = TurnPicture.builder()
      .filename(FileUtil.generateFilename(file, "turnpic"))
      .content(imgBase64)
      .createTime(new Date())
      .build();
    turnPictureRepository.save(turnPicture);
  }

  public void delete(Integer id) {
    turnPictureRepository.findById(id).ifPresent(turnPicture -> {
      turnPictureRepository.deleteById(id);
    });
  }

  private String saveImg(MultipartFile img) {
    String contentType = img.getContentType();
    String avatarBase64 = "data:" + contentType + ";base64,";
    List<Byte> byteList = new LinkedList<Byte>();
    try (InputStream fileInputStream = img.getInputStream();){
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
    }
  }
}
