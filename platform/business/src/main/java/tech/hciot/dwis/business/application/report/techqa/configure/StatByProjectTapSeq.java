package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByProjectTapSeq implements TechQAExporter {

  @Override
  public String titleCH() {
    return "出钢号质量报告";
  }

  @Override
  public String titleEN() {
    return "Tap_Seq#  Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_PROJECT_TAP_SEQ;
  }

  @Override
  public int level() {
    return 1;
  }
}
