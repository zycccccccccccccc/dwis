package tech.hciot.dwis.business.infrastructure;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
public class DownloadUtil {

  public static void download(HttpServletResponse response, File file) {
    response.setContentType("application/force-download");// 设置强制下载不打开
    response.addHeader("Content-Disposition", "attachment;fileName=" + file.getName());

    byte[] buffer = new byte[1024];

    try (InputStream is = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        OutputStream os = response.getOutputStream();) {
      int i = bis.read(buffer);
      while (i != -1) {
        os.write(buffer, 0, i);
        i = bis.read(buffer);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void download(HttpServletResponse response, String fileName) {
    response.setContentType("application/force-download");// 设置强制下载不打开
    response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);

    byte[] buffer = new byte[1024];
    Resource resource = new ClassPathResource("static/" + fileName);
    try (InputStream is = resource.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        OutputStream os = response.getOutputStream();) {
      int i = bis.read(buffer);
      while (i != -1) {
        os.write(buffer, 0, i);
        i = bis.read(buffer);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
