package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByLadleNo implements TechQAExporter {

  @Override
  public String titleCH() {
    return "底注包质量报告";
  }

  @Override
  public String titleEN() {
    return "Ladle Pour Order Analyses Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_LADLE_NO;
  }

  @Override
  public int level() {
    return 1;
  }
}
