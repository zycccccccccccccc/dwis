package tech.hciot.dwis.business.application.report.techqa;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;

@Service
@Slf4j
public class TechQAExportService {

  @Autowired
  private Level1ExportService level1ExportService;

  @Autowired
  private Level2ExportService level2ExportService;

  @Autowired
  private StatByModelExportService statByModelExportService;

  @Autowired
  private StatByTestCodeExportService statByTestCodeExportService;

  @Autowired
  private SingleWheelDetailExportService singleWheelDetailExportService;

  @Autowired
  private StatByProjectScrapCodeExportService statByProjectScrapCodeExportService;

  @Autowired
  private StatByProjectTestCodeExportService statByProjectTestCodeExportService;

  @Autowired
  private GraphiteScrapExportService graphiteScrapExportService;

  @Autowired
  private SingleScrapExportService singleScrapExportService;

  @Autowired
  private MachineScrapExportService machineScrapExportService;

  @Autowired
  private FinalCheckScrapExportService finalCheckScrapExportService;

  @Autowired
  private AngleUnbalanceExportService angleUnbalanceExportService;

  @Autowired
  private MachineUnbalanceExportService machineUnbalanceExportService;

  @Autowired
  private Scrap44ASExportService scrap44ASExportService;

  @Autowired
  private VibrateWheelExportService vibrateWheelExportService;

  @Autowired
  private MoldAgeScrapExportService moldAgeScrapExportService;

  @Autowired
  private DecarbonExportService decarbonExportService;

  @Autowired
  private ReportAssembler assembler;

  public void export1Level(Map<String, Object> parameterMap,
                           HttpServletResponse response,
                           TechQAExporter exporter) {
    level1ExportService.export(parameterMap, response, exporter);
  }

  public void export2Level(Map<String, Object> parameterMap,
                           HttpServletResponse response,
                           TechQAExporter exporter) {
    level2ExportService.export(parameterMap, response, exporter);
  }

  public void statByModel(Map<String, Object> parameterMap,
                          HttpServletResponse response,
                          TechQAExporter exporter) {
    statByModelExportService.export(parameterMap, response, exporter);
  }

  public void statByTestCode(Map<String, Object> parameterMap,
                             HttpServletResponse response,
                             TechQAExporter exporter) {
    statByTestCodeExportService.export(parameterMap, response, exporter);
  }

  public void singleWheelDetail(String beginDate,
                                HttpServletResponse response) {
    singleWheelDetailExportService.export(beginDate, response);
  }

  public void statByProjectScrapCode(Map<String, Object> parameterMap,
                                    HttpServletResponse response) {
    statByProjectScrapCodeExportService.export(parameterMap, response);
  }

  public void statByProjectTestCode(Map<String, Object> parameterMap,
                             HttpServletResponse response) {
    statByProjectTestCodeExportService.export(parameterMap, response);
  }

  public void graphiteScrap(Map<String, Object> parameterMap,
                           HttpServletResponse response) {
    graphiteScrapExportService.export(parameterMap, response);
  }

  public void singleScrap(Map<String, Object> parameterMap,
                           HttpServletResponse response) {
    singleScrapExportService.export(parameterMap, response);
  }

  public void machineScrap(Map<String, Object> parameterMap,
                        HttpServletResponse response) {
    machineScrapExportService.export(parameterMap, response);
  }

  public void finalCheckScrap(Map<String, Object> parameterMap,
                              HttpServletResponse response) {
    finalCheckScrapExportService.export(parameterMap, response);
  }

  public void angleUnbalance(Map<String, Object> parameterMap,
                             HttpServletResponse response) {
    angleUnbalanceExportService.export(parameterMap, response);
  }

  public void machineUnbalance(Map<String, Object> parameterMap,
                               HttpServletResponse response) {
    machineUnbalanceExportService.export(parameterMap, response);
  }

  public void scrap44AS(Map<String, Object> parameterMap,
                        HttpServletResponse response) {
    scrap44ASExportService.export(parameterMap, response);
  }

  public void vibrateWheel(Map<String, Object> parameterMap,
                           HttpServletResponse response) {
    vibrateWheelExportService.export(parameterMap, response);
  }

  public void moldAgeScrap(Map<String, Object> parameterMap,
                           HttpServletResponse response) {
    moldAgeScrapExportService.export(parameterMap, response);
  }

  public void decarbon(Map<String, Object> parameterMap,
                           HttpServletResponse response) {
    decarbonExportService.export(parameterMap, response);
  }
}
