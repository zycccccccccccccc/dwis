package tech.hciot.dwis.business.application.report.multi.qa;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.application.report.ReportSqlService;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;

@Service
@Slf4j
public class MultiReportQAStatExportService {

  private static final String REPORT_NAME = "composite-report/qa-stat";

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private ReportSqlService reportSqlService;

  @Autowired
  private QAStatExportService qaStatExportService;

  @Autowired
  private ProdStatExportService prodStatExportService;

  @Autowired
  private WheelReworkExportService wheelReworkExportService;

  @Autowired
  private WheelReworkScrapExportService wheelReworkScrapExportService;

  @Autowired
  private PourTimeAndScrapExportService pourTimeAndScrapExportService;

  @Autowired
  private ConfirmScrapExportService confirmScrapExportService;

  @Autowired
  private PreScrapExportService preScrapExportService;

  @Autowired
  private MachiningStatExportService machiningStatExportService;

  @Autowired
  private AllConfirmScrapStatExportService allConfirmScrapStatExportService;

  @Autowired
  private FurWheelInfoExportService furWheelInfoExportService;

  // 3.1 综合查询业务-质量统计-质量统计
  public void qaStat(Map<String, Object> parameterMap, HttpServletResponse response) {
    qaStatExportService.export(parameterMap, response);
  }

  // 3.2 综合查询业务-质量统计-产量统计
  public void prodStat(Map<String, Object> parameterMap, HttpServletResponse response) {
    prodStatExportService.export(parameterMap, response);
  }

  // 3.3 综合查询业务-质量统计-车轮返工
  public void wheelRework(Map<String, Object> parameterMap, HttpServletResponse response) {
    wheelReworkExportService.export(parameterMap, response);
  }

  // 3.4 综合查询业务-质量统计-车轮返废
  public void wheelReworkScrapReport(Map<String, Object> parameterMap, HttpServletResponse response) {
    wheelReworkScrapExportService.export(parameterMap, response);
  }

  // 3.5 综合查询业务-质量统计-浇注时间与废品
  public void pourTimeAndScrap(Map<String, Object> parameterMap, HttpServletResponse response) {
    pourTimeAndScrapExportService.export(parameterMap, response);
  }

  // 3.6 综合查询业务-质量统计-确认废品与交验
  public void confirmScrap(Map<String, Object> parameterMap, HttpServletResponse response) {
    confirmScrapExportService.export(parameterMap, response);
  }

  // 3.7 综合查询业务-质量统计-预检废品
  public void preScrap(Map<String, Object> parameterMap, HttpServletResponse response) {
    preScrapExportService.export(parameterMap, response);
  }

  // 3.8 综合查询业务-质量统计-在制品统计
  public void machiningStatResult(HttpServletResponse response) {
    machiningStatExportService.export(response);
  }

  // 3.9 综合查询业务-质量统计-全部确认废品统计
  public void allConfirmScrapStat(Map<String, Object> parameterMap, HttpServletResponse response) {
    allConfirmScrapStatExportService.export(parameterMap, response);
  }

  // 3.11 综合查询业务-质量统计-整炉车轮信息
  public void furWheelInfo(String wheelSerial, String castDate, Integer tapSeq, HttpServletResponse response) {
    furWheelInfoExportService.export(wheelSerial, castDate, tapSeq, response);
  }
}
