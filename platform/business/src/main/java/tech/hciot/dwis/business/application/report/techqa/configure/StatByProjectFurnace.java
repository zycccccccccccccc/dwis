package tech.hciot.dwis.business.application.report.techqa.configure;

import tech.hciot.dwis.business.application.report.techqa.TechQAExporter;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.TechQAConstant;

public class StatByProjectFurnace implements TechQAExporter {

  @Override
  public String titleCH() {
    return "炉长按项目汇总统计";
  }

  @Override
  public String titleEN() {
    return "Furnace_ID Scrap Report";
  }

  @Override
  public String sqlTemplate() {
    return TechQAConstant.SQL_TEMPLATE_PROJECT_FURNACE;
  }

  @Override
  public int level() {
    return 2;
  }
}
