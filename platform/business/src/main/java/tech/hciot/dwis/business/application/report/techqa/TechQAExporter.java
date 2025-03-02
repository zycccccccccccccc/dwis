package tech.hciot.dwis.business.application.report.techqa;

public interface TechQAExporter {
  
  String titleCH();

  String titleEN();

  String sqlTemplate();

  int level();
}
