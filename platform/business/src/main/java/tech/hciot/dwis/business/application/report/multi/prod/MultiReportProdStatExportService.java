package tech.hciot.dwis.business.application.report.multi.prod;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.MultiReportProdStatService;
import tech.hciot.dwis.business.application.report.ReportSqlService;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;

@Service
@Slf4j
public class MultiReportProdStatExportService {

  private static final String REPORT_NAME = "composite-report/prod-stat";

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private ReportSqlService reportSqlService;

  @Autowired
  private MultiReportProdStatService multiReportProdStatService;

  @Autowired
  private RepositoryReportExportService repositoryReportExportService;

  @Autowired
  private PourReportExportService pourReportExportService;

  @Autowired
  private FinishReportExportService finishReportExportService;

  @Autowired
  private StockReportExportService stockReportExportService;

  @Autowired
  private ShipReportExportService shipReportExportService;

  @Autowired
  private YearProdReportExportService yearProdReportExportService;

  // 2.1 综合查询业务-年/月度产量统计-库存报告
  public void repositoryReport(List<String> design, HttpServletResponse response) {
    repositoryReportExportService.export(design, response);
  }

  // 2.2 综合查询业务-年/月度产量统计-浇注报告
  public void pourReport(Map<String, Object> parameterMap,
                         HttpServletResponse response) {
    pourReportExportService.export(parameterMap, response);
  }

  // 2.3 综合查询业务-年/月度产量统计-成品报告
  public void finishReport(Map<String, Object> parameterMap,
                           HttpServletResponse response) {
    finishReportExportService.export(parameterMap, response);
  }

  // 2.4 综合查询业务-年/月度产量统计-入库报告
  public void stockReport(Map<String, Object> parameterMap,
                          HttpServletResponse response) {
    stockReportExportService.export(parameterMap, response);
  }

  // 2.5 综合查询业务-年/月度产量统计-发运报告
  public void shipReport(Map<String, Object> parameterMap,
                         HttpServletResponse response) {
    shipReportExportService.export(parameterMap, response);
  }

  // 2.6 综合查询业务-年/月度产量统计-年度生产汇总报告
  public void yearProdReport(Map<String, Object> parameterMap,
                             HttpServletResponse response) {
    yearProdReportExportService.export(parameterMap, response);
  }
}
