package tech.hciot.dwis.business.infrastructure;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import lombok.Cleanup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class AccessUtil {

  public static void copyBlankMdbFile(String fileName) throws Exception {
    Resource resource = new ClassPathResource("static/abc-hgz.mdb");
    @Cleanup InputStream is = resource.getInputStream();
    @Cleanup OutputStream out = new FileOutputStream(fileName);
    byte[] buffer = new byte[1024];
    int numRead;
    while ((numRead = is.read(buffer)) != -1) {
      out.write(buffer, 0, numRead);
    }
  }

  public static Connection connectAccessDB(String path) throws Exception {
    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
    String database = "jdbc:ucanaccess://" + path + ";memory=true";
    return DriverManager.getConnection(database, "", "");
  }
}
