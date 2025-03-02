package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByXh implements TechQAExporter {

  @Override
  public String titleCH() {
    return "生产线质量报告";
  }

  @Override
  public String titleEN() {
    return "Line Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_XH;
  }

  @Override
  public int level() {
    return 2;
  }
}
