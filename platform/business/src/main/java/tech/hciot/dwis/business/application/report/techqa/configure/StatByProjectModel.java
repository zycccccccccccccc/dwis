package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByProjectModel implements TechQAExporter {

  @Override
  public String titleCH() {
    return "造型工长废品统计";
  }

  @Override
  public String titleEN() {
    return "Molding_ID Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_PROJECT_MODEL;
  }

  @Override
  public int level() {
    return 1;
  }
}
