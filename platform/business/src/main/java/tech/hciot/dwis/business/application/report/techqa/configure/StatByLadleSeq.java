package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByLadleSeq implements TechQAExporter {

  @Override
  public String titleCH() {
    return "小包顺序质量报告";
  }

  @Override
  public String titleEN() {
    return "Ladle_Seq#  Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_LADLE_SEQ;
  }

  @Override
  public int level() {
    return 2;
  }
}
