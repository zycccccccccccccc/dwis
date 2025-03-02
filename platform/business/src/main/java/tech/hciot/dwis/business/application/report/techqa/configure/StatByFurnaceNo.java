package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByFurnaceNo implements TechQAExporter {

  @Override
  public String titleCH() {
    return "电炉号质量报告";
  }

  @Override
  public String titleEN() {
    return "Furnace No Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_FURNACE_NO;
  }

  @Override
  public int level() {
    return 2;
  }
}
