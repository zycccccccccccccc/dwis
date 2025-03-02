package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByPourLeader implements TechQAExporter {

  @Override
  public String titleCH() {
    return "浇注工长质量报告";
  }

  @Override
  public String titleEN() {
    return "PourLeader_ID Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_POUR_LEADER;
  }

  @Override
  public int level() {
    return 2;
  }
}
