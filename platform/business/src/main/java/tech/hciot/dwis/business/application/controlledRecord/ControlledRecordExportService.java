package tech.hciot.dwis.business.application.controlledRecord;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ControlledRecordExportService implements ApplicationListener<ContextRefreshedEvent> {

  private Map<String, ControlledRecordExporter> map = new HashMap<>();

  public void export(String type, String date, Integer tapNo, Integer heatLine, Integer machineNo, String opeId,
      HttpServletResponse response) {
    ControlledRecordExporter exporter = map.get(type);
    try {
      ClassPathResource resource = new ClassPathResource("static/report/controlled-record/" + exporter.fileName());
      InputStream inputStream = resource.getInputStream();

      Workbook workbook = new XSSFWorkbook(inputStream);
      exporter.generateReport(workbook, date, tapNo, heatLine, machineNo, opeId);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + exporter.fileName());
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    event.getApplicationContext().getBeansOfType(ControlledRecordExporter.class).forEach((name, instance) ->
        map.put(instance.type(), instance));
  }
}

