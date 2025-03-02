package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByLadleOrder implements TechQAExporter {

  @Override
  public String titleCH() {
    return "小包浇注顺序质量报告";
  }

  @Override
  public String titleEN() {
    return "Pour Order Analyses Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_LADLE_ORDER;
  }

  @Override
  public int level() {
    return 1;
  }
}
