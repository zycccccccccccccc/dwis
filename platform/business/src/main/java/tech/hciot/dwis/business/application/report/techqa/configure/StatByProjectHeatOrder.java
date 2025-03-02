package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByProjectHeatOrder implements TechQAExporter {
  @Override
  public String titleCH() {
    return "大包浇注顺序质量报告";
  }

  @Override
  public String titleEN() {
    return "Bag Pour Order Analyses Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_PROJECT_HEAT_ORDER;
  }

  @Override
  public int level() {
    return 2;
  }
}
