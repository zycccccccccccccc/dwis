package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByTemp implements TechQAExporter {

  @Override
  public String titleCH() {
    return "浇注温度质量报告";
  }

  @Override
  public String titleEN() {
    return "Temp Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_TEMP;
  }

  @Override
  public int level() {
    return 2;
  }
}
