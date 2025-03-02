package tech.hciot.dwis.business.infrastructure;

import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileUtil {

  public static String generateFilename(MultipartFile file, String fileType) {
    String filename = file.getOriginalFilename();
    String suffix = getSuffix(filename);
    return fileType + "_" + System.currentTimeMillis() + suffix;
  }

  public static String uploadFile(MultipartFile file, String staticFilePath, String fileType) throws IOException {
    String filename = file.getOriginalFilename();
    String suffix = getSuffix(filename);
    String dir = staticFilePath + fileType;
    File dirFile = new File(dir);
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }
    String savedFilename = fileType + "_" + System.currentTimeMillis() + suffix;
    File dest = new File(dir + File.separator + savedFilename);
    file.transferTo(dest);
    return savedFilename;
  }

  // 获取文件后缀名（包含.）
  private static String getSuffix(String filename) {
    int lastIndex = filename.lastIndexOf(".");
    return lastIndex == -1 ? "" : filename.substring(lastIndex);
  }
}
