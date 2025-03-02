package tech.hciot.dwis.business.infrastructure.templatesql;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Service
@Slf4j
public class SqlTemplateParser {
  @Resource
  FreeMarkerConfigurer freeMarkerConfigurer;

  public String parseSqlTemplate(String sqlTemplateDir, String sqlTemplateName) {
    Configuration configuration = freeMarkerConfigurer.getConfiguration();
    configuration.setClassForTemplateLoading(this.getClass(), "/sql-template/" + sqlTemplateDir);
    StringWriter sw = new StringWriter();
    Template template;
    try {
      template = configuration.getTemplate(sqlTemplateName + ".sql");
      template.process(null, sw);
      log.info("sql template: {}", sw);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return sw.toString();
  }

  // 根据参数Map生成sql语句
  public String parseSqlTemplate(String sqlTemplateDir, String sqlTemplateName, Map<String, Object> parameterMap) {
    Configuration configuration = freeMarkerConfigurer.getConfiguration();
    configuration.setClassForTemplateLoading(this.getClass(), "/sql-template/" + sqlTemplateDir);
    StringWriter sw = new StringWriter();
    Template template;
    try {
      template = configuration.getTemplate(sqlTemplateName + ".sql");
      template.process(parameterMap, sw);
      log.info("sql template: {}", sw);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return sw.toString();
  }
}
