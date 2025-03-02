package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByMonth implements TechQAExporter {

  @Override
  public String titleCH() {
    return "质量报告";
  }

  @Override
  public String titleEN() {
    return "Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_MONTH;
  }

  @Override
  public int level() {
    return 1;
  }
}
