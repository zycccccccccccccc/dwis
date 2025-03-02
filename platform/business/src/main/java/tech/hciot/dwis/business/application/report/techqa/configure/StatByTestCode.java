package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByTestCode implements TechQAExporter {

  @Override
  public String titleCH() {
    return "试验码质量报告";
  }

  @Override
  public String titleEN() {
    return "Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_TEST_CODE;
  }

  @Override
  public int level() {
    return 3;
  }
}
