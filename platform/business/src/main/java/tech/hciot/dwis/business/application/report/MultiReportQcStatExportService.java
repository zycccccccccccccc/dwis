package tech.hciot.dwis.business.application.report;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseTime;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.addMergedRegion;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.getClonedSheet;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.removeMergedRegion;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.setPrintSetupInfo;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.setStyle;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.*;

@Service
@Slf4j
public class MultiReportQcStatExportService {

  public static final String SUMMARY_REPORT = "4.1-summary-report";
  public static final String REWORK_WHEEL_REPORT = "4.2-rework-wheel-report";
  public static final String SHOT_REPORT = "4.3-shot-report";
  public static final String SMALL_TAPE_REPORT = "4.4-small-tape-report";
  public static final String PRECHECK_PERCENT = "4.5-precheck-percent";
  public static final String FINALCHECK_PERCENT = "4.6-finalcheck-percent";
  public static final String BALANCE_PERCENT = "4.7-balance-percent";
  public static final String REWORK_CODE_PERCENT = "4.8-rework-code-percent";
  public static final String SINGLE_WHEEL_REPORT = "4.9-single-wheel-report";
  public static final String SCRAP_CODE_SHIFT = "4.10-scrap-code-shift";
  public static final String REWORK_CODE_SHIFT = "4.11-rework-code-shift";
  public static final String LADLE_SEQ = "4.12-ladle-seq";
  public static final String FINALCHECK_TIMES = "4.13-finalcheck-times";
  public static final String MACHINE_REWORK = "4.14-machine-rework";
  public static final String FINALCHECK_REWORK_WHEEL = "4.15-finalcheck-rework-wheel";


  private static final String SUFFIX = ".xlsx";

  @Autowired
  private MultiReportQcStatService multiReportQcStatService;

  public void reworkScrap(Workbook workbook, Map<String, Object> parameterMap) {
    String shift = parameterMap.containsKey("shift") ? parameterMap.get("shift").toString() : "";
    ReworkScrapData reworkScrapData = multiReportQcStatService.reworkScrap(parameterMap);
    genPreCheckReport(workbook, 0, reworkScrapData.getPreCheck(), parameterMap, shift);
    genFinalCheckReport(workbook, 1, reworkScrapData.getFinalCheck(), parameterMap, shift);
    genUltraReport(workbook, 2, reworkScrapData.getUltra(), parameterMap, shift);
    genMagneticReport(workbook, 3, reworkScrapData.getMagnetic(), parameterMap, shift);
    genBalanceReport(workbook, 4, reworkScrapData.getBalance(), parameterMap, shift);

    for (int i = 0; i < 5; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "预检");
    workbook.setSheetName(1, "终检");
    workbook.setSheetName(2, "超探");
    workbook.setSheetName(3, "磁探");
    workbook.setSheetName(4, "平衡机");
  }

  private void genBalanceReport(Workbook workbook, int sheetIndex, List<BalanceReworkScrapData> balance,
      Map<String, Object> parameterMap, String shift) {
    String title = (StringUtils.isEmpty(shift) ? "*" : shift) + "班平衡机返废轮号统计表";
    Sheet sheet = workbook.getSheetAt(sheetIndex);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);

    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, sheetIndex);

    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < balance.size(); i++) {
      BalanceReworkScrapData data = balance.get(i);
      Row r = sxssfSheet.createRow(currentRowNum + i);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(data.getWheelSerial());
      r.getCell(1).setCellValue(data.getDesign());
      r.getCell(2).setCellValue(data.getReworkCode());
      r.getCell(3).setCellValue(data.getScrapCode());
      r.getCell(4).setCellValue(data.getCurrentReworkCode());
      r.getCell(5).setCellValue(data.getCurrentScrapCode());
      r.getCell(6).setCellValue(data.getConfirmedScrap() == 1 ? "是" : "否");
      r.getCell(7).setCellValue(data.getBalanceInspectorId());
      r.getCell(8).setCellValue(getStringValueOfDate(data.getOpeDT(), "yyyy-MM-dd HH:mm"));
    }
  }

  private void genMagneticReport(Workbook workbook, int sheetIndex, List<PreCheckReworkScrapData> magnetic,
      Map<String, Object> parameterMap, String shift) {
    String title = (StringUtils.isEmpty(shift) ? "*" : shift) + "班磁探返废轮号统计表";
    genReworkScrapSheet(workbook, sheetIndex, magnetic, parameterMap, title);
  }

  private void genUltraReport(Workbook workbook, int sheetIndex, List<PreCheckReworkScrapData> ultra,
      Map<String, Object> parameterMap, String shift) {
    String title = (StringUtils.isEmpty(shift) ? "*" : shift) + "班超探返废轮号统计表";
    genReworkScrapSheet(workbook, sheetIndex, ultra, parameterMap, title);
  }

  private void genFinalCheckReport(Workbook workbook, int sheetIndex, List<PreCheckReworkScrapData> finalCheck,
      Map<String, Object> parameterMap, String shift) {
    String title = (StringUtils.isEmpty(shift) ? "*" : shift) + "班终检返废轮号统计表";
    genReworkScrapSheet(workbook, sheetIndex, finalCheck, parameterMap, title);
  }

  private void genReworkScrapSheet(Workbook workbook, int sheetIndex, List<PreCheckReworkScrapData> finalCheck,
      Map<String, Object> parameterMap, String title) {
    Sheet sheet = workbook.getSheetAt(sheetIndex);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);

    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, sheetIndex);

    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < finalCheck.size(); i++) {
      PreCheckReworkScrapData data = finalCheck.get(i);
      Row r = sxssfSheet.createRow(currentRowNum + i);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(data.getWheelSerial());
      r.getCell(1).setCellValue(data.getDesign());
      r.getCell(2).setCellValue(data.getReworkCode());
      r.getCell(3).setCellValue(data.getScrapCode());
      r.getCell(4).setCellValue(data.getCurrentReworkCode());
      r.getCell(5).setCellValue(data.getCurrentScrapCode());
      r.getCell(6).setCellValue(data.getConfirmedScrap() == 1 ? "是" : "否");
      r.getCell(7).setCellValue(data.getDragInspectorId());
      r.getCell(8).setCellValue(data.getCopeInspectorId());
      r.getCell(9).setCellValue(getStringValueOfDate(data.getOpeDT(), "yyyy-MM-dd HH:mm"));
    }
  }

  private void genPreCheckReport(Workbook workbook, int sheetIndex, List<PreCheckReworkScrapData> preCheck,
      Map<String, Object> parameterMap, String shift) {
    String title = (StringUtils.isEmpty(shift) ? "*" : shift) + "班预检返废轮号统计表";
    genReworkScrapSheet(workbook, sheetIndex, preCheck, parameterMap, title);
  }

  public void export(String type, Map<String, Object> parameterMap, HttpServletResponse response) {
    Workbook workbook = null;
    String filename = "";
    switch (type) {
      case SUMMARY_REPORT:
        filename = SUMMARY_REPORT + SUFFIX;
        workbook = getWorkbook(filename);
        summary(workbook, parameterMap);
        break;
      case REWORK_WHEEL_REPORT:
        filename = REWORK_WHEEL_REPORT + SUFFIX;
        workbook = getWorkbook(filename);
        reworkScrap(workbook, parameterMap);
        break;
      case SHOT_REPORT:
        filename = SHOT_REPORT + SUFFIX;
        workbook = getWorkbook(filename);
        shot(workbook, parameterMap);
        break;
      case SMALL_TAPE_REPORT:
        filename = SMALL_TAPE_REPORT + SUFFIX;
        workbook = getWorkbook(filename);
        smallTape(workbook, parameterMap);
        break;
      case PRECHECK_PERCENT:
        filename = PRECHECK_PERCENT + SUFFIX;
        workbook = getWorkbook(filename);
        preCheckPercent(workbook, parameterMap);
        break;
      case FINALCHECK_PERCENT:
        filename = FINALCHECK_PERCENT + SUFFIX;
        workbook = getWorkbook(filename);
        finalCheckPercent(workbook, parameterMap);
        break;
      case BALANCE_PERCENT:
        filename = BALANCE_PERCENT + SUFFIX;
        workbook = getWorkbook(filename);
        balancePercent(workbook, parameterMap);
        break;
      case REWORK_CODE_PERCENT:
        filename = REWORK_CODE_PERCENT + SUFFIX;
        workbook = getWorkbook(filename);
        reworkPercent(workbook, parameterMap);
        break;
      case SINGLE_WHEEL_REPORT:
        filename = SINGLE_WHEEL_REPORT + SUFFIX;
        workbook = getWorkbook(filename);
        wheelDetail(workbook, parameterMap);
        break;
      case SCRAP_CODE_SHIFT:
        filename = SCRAP_CODE_SHIFT + SUFFIX;
        workbook = getWorkbook(filename);
        scrapShift(workbook, parameterMap);
        break;
      case REWORK_CODE_SHIFT:
        filename = REWORK_CODE_SHIFT + SUFFIX;
        workbook = getWorkbook(filename);
        reworkShift(workbook, parameterMap);
        break;
      case LADLE_SEQ:
        filename = LADLE_SEQ + SUFFIX;
        workbook = getWorkbook(filename);
        scrapLadle(workbook, parameterMap);
        break;
      case FINALCHECK_TIMES:
        filename = FINALCHECK_TIMES + SUFFIX;
        workbook = getWorkbook(filename);
        finalCheckRework(workbook, parameterMap);
        break;
      case MACHINE_REWORK:
        filename = MACHINE_REWORK + SUFFIX;
        workbook = getWorkbook(filename);
        machineRework(workbook, parameterMap);
        break;
      case FINALCHECK_REWORK_WHEEL:
        filename = FINALCHECK_REWORK_WHEEL + SUFFIX;
        workbook = getWorkbook(filename);
        finalReworkDetail(workbook, parameterMap);
        break;
      default:
        break;
    }
    if (workbook != null) {
      try {
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + filename);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      } finally {
        if (workbook instanceof SXSSFWorkbook) {
          ((SXSSFWorkbook) workbook).dispose();
        }
      }
    }
  }

  private void summary(Workbook workbook, Map<String, Object> parameterMap) {
    String shift = parameterMap.containsKey("shift") ? parameterMap.get("shift").toString() + '班' : "";
    String staffId = parameterMap.containsKey("staffId") ? "工长" + parameterMap.get("staffId").toString() : "";
    SummaryData data = multiReportQcStatService.summary(parameterMap);
    genPrecheckSummaryReport(data.getPreCheck(), workbook, parameterMap, shift, staffId);
    genPrecheckTimesReport(data.getPreCheckTimes(), workbook, parameterMap, shift, staffId);
    genFinalCheckReport(data.getFinalCheck(), workbook, parameterMap, shift, staffId);
    genUltraReport(data.getUltra(), workbook, parameterMap, shift, staffId);
    genBalanceSummaryReport(data.getBalance(), workbook, parameterMap, shift, staffId);
    genXraySummaryReport(data.getXray(), workbook, parameterMap);
    genCihenSummaryReport(data.getCihen(), workbook, parameterMap, shift, staffId);
    genMagneticSummaryReport(data.getMagnetic(), workbook, parameterMap, shift, staffId);
    genTransportSummaryReport(data.getTransport(), workbook, parameterMap, shift, staffId);
    for (int i = 0; i < 9; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "预检");
    workbook.setSheetName(1, "预检次数与轮号");
    workbook.setSheetName(2, "终检");
    workbook.setSheetName(3, "超探");
    workbook.setSheetName(4, "平衡机");
    workbook.setSheetName(5, "X光");
    workbook.setSheetName(6, "磁痕");
    workbook.setSheetName(7, "磁探");
    workbook.setSheetName(8, "X光去重镗孔发运");
  }

  private void genXraySummaryReport(List<XrayData> xray, Workbook workbook, Map<String, Object> parameterMap) {
    Sheet sheet = workbook.getSheetAt(5);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(2);
    Sheet sxssfSheet = getClonedSheet(sheet, 5);
    int currentRowNum = 3;
    for (int i = 0; i < xray.size(); i++) {
      XrayData summaryData = xray.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row);
      row.getCell(0).setCellValue(summaryData.getWheelSerial());
      row.getCell(1).setCellValue(summaryData.getDesign());
      row.getCell(2).setCellValue(summaryData.getXray());
      row.getCell(3).setCellValue(summaryData.getScrapCode());
      row.getCell(4).setCellValue(summaryData.getConfirmedScrap() == 1 ? "是" : "否");
      row.getCell(5).setCellValue(getStringValueOfDate(summaryData.getScrapDate(), "yyyy-MM-dd"));
    }
  }

  private void genTransportSummaryReport(TransportData transport, Workbook workbook, Map<String, Object> parameterMap,
      String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  + "X光、去重、镗孔发运统计表";
    Sheet sheet = workbook.getSheetAt(8);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    //填充X光发运
    Row tempRow = sheet.getRow(4);
    removeMergedRegion(sheet, 4);
    Sheet sxssfSheet = getClonedSheet(sheet, 8);
    int currentRowNum = 4;
    List<TransportXrayData> transportXrayDataList = transport.getXrayList();
    for (int i = 0; i < transportXrayDataList.size(); i++) {
      TransportXrayData transportXrayData = transportXrayDataList.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row,0, 1);
      row.getCell(0).setCellValue(transportXrayData.getDesign());
      row.getCell(1).setCellValue(transportXrayData.getAmount());
    }
    row = sxssfSheet.createRow(currentRowNum++);
    setStyle(tempRow, row, 0, 1);
    row.getCell(0).setCellValue("合计");
    row.getCell(1).setCellValue(getStringValue(transport.getXrayAmount()));

    //填充去重发运
    currentRowNum = 4;
    List<TransportDeweightData> transportDeweightDataList = transport.getDeweightList();
    for (int i = 0; i < transportDeweightDataList.size(); i++) {
      TransportDeweightData transportDeweightData = transportDeweightDataList.get(i);
      row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 3, 4);
      row.getCell(3).setCellValue(transportDeweightData.getDesign());
      row.getCell(4).setCellValue(getStringValue(transportDeweightData.getAmount()));
    }
    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 3, 4);
    row.getCell(3).setCellValue("合计");
    row.getCell(4).setCellValue(getStringValue(transport.getDeweightAmount()));

    //填充镗孔发运
    currentRowNum = 4;
    List<TransportBoreData> transportBoreDataList = transport.getBoreList();
    for (int i = 0; i < transportBoreDataList.size(); i++) {
      TransportBoreData transportBoreData = transportBoreDataList.get(i);
      row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 6, 8);
      row.getCell(6).setCellValue(transportBoreData.getDesign());
      row.getCell(7).setCellValue(getStringValue(transportBoreData.getBoreSize()));
      row.getCell(8).setCellValue(getStringValue(transportBoreData.getAmount()));
    }
    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 6, 8);
    row.getCell(6).setCellValue("合计");
    row.getCell(8).setCellValue(getStringValue(transport.getBoreAmount()));
    addMergedRegion(sxssfSheet, currentRowNum-1, currentRowNum-1, 6, 7);
  }

  private void genMagneticSummaryReport(List<MagneticData> magnetic, Workbook workbook, Map<String, Object> parameterMap,
      String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  + "磁探情况统计表";
    Sheet sheet = workbook.getSheetAt(7);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(2);
    Sheet sxssfSheet = getClonedSheet(sheet, 7);
    int currentRowNum = 3;
    for (int i = 0; i < magnetic.size(); i++) {
      MagneticData summaryData = magnetic.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row);
      row.getCell(0).setCellValue(summaryData.getWheelSerial());
      row.getCell(1).setCellValue(summaryData.getDesign());
      row.getCell(2).setCellValue(summaryData.getScrapCode());
      row.getCell(3).setCellValue(summaryData.getReworkCode());
      row.getCell(4).setCellValue(getStringValue(summaryData.getTs()));
    }
  }

  private void genCihenSummaryReport(List<CihenData> cihen, Workbook workbook, Map<String, Object> parameterMap, String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  + "磁痕情况统计表";
    Sheet sheet = workbook.getSheetAt(6);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(2);
    Sheet sxssfSheet = getClonedSheet(sheet, 6);
    int currentRowNum = 3;
    for (int i = 0; i < cihen.size(); i++) {
      CihenData summaryData = cihen.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row);
      row.getCell(0).setCellValue(summaryData.getWheelSerial());
      row.getCell(1).setCellValue(getStringValue(summaryData.getGrindTime()));
      row.getCell(2).setCellValue(getStringValue(summaryData.getCopeCihenSum()));
      row.getCell(3).setCellValue(getStringValue(summaryData.getCopeSandHoles()));
      row.getCell(4).setCellValue(getStringValue(summaryData.getDragCihenSum()));
      row.getCell(5).setCellValue(getStringValue(summaryData.getDragSandHoles()));
      row.getCell(6).setCellValue(getStringValue(summaryData.getTs()));
    }
  }

  private void genBalanceSummaryReport(BalanceData balance, Workbook workbook, Map<String, Object> parameterMap, String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  + "平衡机情况统计表";
    Sheet sheet = workbook.getSheetAt(4);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(2);
    removeMergedRegion(sheet, 3);
    Sheet sxssfSheet = getClonedSheet(sheet, 4);
    int currentRowNum = 3;
    List<BalanceSummaryData> summaryDataList = balance.getBalance();
    for (int i = 0; i < summaryDataList.size(); i++) {
      BalanceSummaryData summaryData = summaryDataList.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row, 0, 6);
      row.getCell(0).setCellValue(summaryData.getXh());
      row.getCell(1).setCellValue(summaryData.getReworkCode());
      row.getCell(2).setCellValue(summaryData.getHoldCode());
      row.getCell(3).setCellValue(summaryData.getScrapCode());
      row.getCell(4).setCellValue(getStringValue(summaryData.getE3()));
      row.getCell(5).setCellValue(getStringValue(summaryData.getMc()));
      row.getCell(6).setCellValue(getStringValue(summaryData.getAmount()));
    }
    row = sxssfSheet.createRow(currentRowNum);
    setStyle(tempRow, row, 0, 6);
    CellRangeAddress rangeAddress1 = new CellRangeAddress(currentRowNum,currentRowNum,4,5);
    sxssfSheet.addMergedRegion(rangeAddress1);
    row.getCell(0).setCellValue("1线合计");
    row.getCell(1).setCellValue(getStringValue(balance.getLine1Total()));
    row.getCell(2).setCellValue("2线合计");
    row.getCell(3).setCellValue(getStringValue(balance.getLine2Total()));
    row.getCell(4).setCellValue("总合计");
    row.getCell(6).setCellValue(getStringValue(balance.getBalanceTotal()));

    currentRowNum = 3;
    JSONArray tapSummaryDataList = balance.getTape().getJSONArray("resultList");
    for (int i = 0; i < tapSummaryDataList.size(); i++) {
      JSONObject result = tapSummaryDataList.getJSONObject(i);
      JSONArray dataList = result.getJSONArray("data");
      for (int j = 0; j < dataList.size(); j++) {
        row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 8, 12);
        row.getCell(8).setCellValue(dataList.getJSONObject(j).getString("design"));
        row.getCell(9).setCellValue(dataList.getJSONObject(j).getString("boreSize"));
        row.getCell(10).setCellValue(dataList.getJSONObject(j).getString("e3"));
        row.getCell(11).setCellValue(dataList.getJSONObject(j).getString("amount"));
        row.getCell(12).setCellValue(dataList.getJSONObject(j).getString("smallTape"));
      }
      row = createRowIfNull(sxssfSheet, currentRowNum, tempRow, 8, 12);
      row.getCell(8).setCellValue(result.getString("design"));
      row.getCell(9).setCellValue("小计");
      row.getCell(10).setCellValue(result.getString("e3"));
      row.getCell(11).setCellValue(result.getString("amount"));
      row.getCell(12).setCellValue(result.getString("smallTape"));
      CellRangeAddress region = new CellRangeAddress(currentRowNum - dataList.size(), currentRowNum, 8, 8);
      sxssfSheet.addMergedRegion(region);
      currentRowNum++;
    }
    row = createRowIfNull(sxssfSheet, currentRowNum, tempRow, 8, 12);
    row.getCell(10).setCellValue(balance.getTape().getJSONObject("total").getString("e3"));
    row.getCell(11).setCellValue(balance.getTape().getJSONObject("total").getString("amount"));
    row.getCell(12).setCellValue(balance.getTape().getJSONObject("total").getString("smallTape"));
    CellRangeAddress region = new CellRangeAddress(currentRowNum, currentRowNum, 8, 9);
    sxssfSheet.addMergedRegion(region);
    sxssfSheet.getRow(currentRowNum).getCell(8).setCellValue("总计");
  }

  private void genUltraReport(UltraData ultra, Workbook workbook, Map<String, Object> parameterMap, String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  + "超探情况统计表";
    Sheet sheet = workbook.getSheetAt(3);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(3);
    Row totalRow = sheet.getRow(5);
    Sheet sxssfSheet = getClonedSheet(sheet, 3);
    removeMergedRegion(sxssfSheet, 4);
    int currentRowNum = 4;
    List<UltraSummaryData> summaryDataList = ultra.getUltra();
    for (int i = 0; i < summaryDataList.size(); i++) {
      UltraSummaryData summaryData = summaryDataList.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row);
      row.getCell(0).setCellValue(summaryData.getXh());
      row.getCell(1).setCellValue(summaryData.getReworkCode());
      row.getCell(2).setCellValue(summaryData.getScrapCode());
      row.getCell(3).setCellValue(summaryData.getHoldCode());
      row.getCell(4).setCellValue(summaryData.getXray_req());
      row.getCell(5).setCellValue(getStringValue(summaryData.getAmount()));
    }
    row = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, row);
    row.getCell(0).setCellValue("1线合计");
    row.getCell(1).setCellValue(getStringValue(ultra.getLine1Total()));
    row.getCell(2).setCellValue("2线合计");
    row.getCell(3).setCellValue(getStringValue(ultra.getLine2Total()));
    row.getCell(4).setCellValue("总合计");
    row.getCell(5).setCellValue(getStringValue(ultra.getUltraTotal()));
  }

  private void genFinalCheckReport(FinalCheckData finalCheck, Workbook workbook, Map<String, Object> parameterMap, String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  + "终检情况统计表";
    Sheet sheet = workbook.getSheetAt(2);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(2);
    removeMergedRegion(sheet, 3);
    Sheet sxssfSheet = getClonedSheet(sheet, 2);
    int currentRowNum = 3;
    List<DesignSummaryData> summaryDataList = finalCheck.getDesign();
    for (int i = 0; i < summaryDataList.size(); i++) {
      DesignSummaryData summaryData = summaryDataList.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row, 0, 3);
      row.getCell(0).setCellValue(summaryData.getDesign());
      row.getCell(1).setCellValue(getStringValue(summaryData.getAmount()));
      row.getCell(2).setCellValue(summaryData.getReworkCode());
      row.getCell(3).setCellValue(summaryData.getScrapCode());
    }
    row = sxssfSheet.createRow(currentRowNum++);
    setStyle(tempRow, row, 0, 3);
    row.getCell(0).setCellValue("其他合计");
    row.getCell(1).setCellValue(getStringValue(finalCheck.getOtherAmount()));
    row.getCell(2).setCellValue("CJ33合计");
    row.getCell(3).setCellValue(getStringValue(finalCheck.getCj33Amount()));
    row = sxssfSheet.createRow(currentRowNum++);
    setStyle(tempRow, row, 0, 3);
    row.getCell(0).setCellValue("SA34合计");
    row.getCell(1).setCellValue(getStringValue(finalCheck.getSa34Amount()));
    row.getCell(2).setCellValue("总合计");
    row.getCell(3).setCellValue(getStringValue(finalCheck.getTotalAmount()));

    currentRowNum = 3;
    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 5, 6);
    row.getCell(5).setCellValue("返工代码");
    row.getCell(6).setCellValue("数量");

    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 5, 6);
    row.getCell(5).setCellValue("H1_5");
    row.getCell(6).setCellValue(getStringValue(finalCheck.getH1Amount()));

    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 5, 6);
    row.getCell(5).setCellValue("H6");
    row.getCell(6).setCellValue(getStringValue(finalCheck.getH6Amount()));

    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 5, 6);
    row.getCell(5).setCellValue("TIR");
    row.getCell(6).setCellValue(getStringValue(finalCheck.getTirAmount()));

    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 5, 6);
    row.getCell(5).setCellValue("合计");
    row.getCell(6).setCellValue(getStringValue(finalCheck.getTotalReworkAmount()));

    currentRowNum = 3;
    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 8, 9);
    row.getCell(8).setCellValue("01");
    row.getCell(9).setCellValue(getStringValue(finalCheck.getLine1Amount()));

    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 8, 9);
    row.getCell(8).setCellValue("02");
    row.getCell(9).setCellValue(getStringValue(finalCheck.getLine2Amount()));

    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 8, 9);
    row.getCell(8).setCellValue("总合计");
    row.getCell(9).setCellValue(getStringValue(finalCheck.getTotalLineAmount()));
  }

  private void genPrecheckTimesReport(List<PreCheckTimesData> preCheckTimes,
                                      Workbook workbook, Map<String, Object> parameterMap, String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  +  "预检次数与轮号统计表";
    Sheet sheet = workbook.getSheetAt(1);
    Row tempRow = sheet.getRow(0);
    tempRow.getCell(0).setCellValue(title);
    tempRow = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    tempRow.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
            getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 1);
    int currentRowNum = 3;
    tempRow = sheet.getRow(3);
    for (int i = 0; i < preCheckTimes.size(); i++) {
      PreCheckTimesData data = preCheckTimes.get(i);
      Row row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row);
      row.getCell(0).setCellValue(getStringValue(data.getTimes()));
      row.getCell(1).setCellValue(data.getWheelSerial());
    }
  }

  private void genPrecheckSummaryReport(PreCheckData preCheck, Workbook workbook, Map<String, Object> parameterMap,
      String shift, String staffId) {
    String title = (StringUtils.isEmpty(shift) ? "" : shift) + (StringUtils.isEmpty(staffId) ? "" : staffId)  +  "预检情况统计表";
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(3);
    removeMergedRegion(sheet, 3);
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    int currentRowNum = 3;
    List<WheelSummaryData> summaryDataList = preCheck.getWheel();
    for (int i = 0; i < summaryDataList.size(); i++) {
      WheelSummaryData summaryData = summaryDataList.get(i);
      row = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, row, 0, 3);
      row.getCell(0).setCellValue(summaryData.getDesign());
      row.getCell(1).setCellValue(getStringValue(summaryData.getAmount()));
      row.getCell(2).setCellValue(summaryData.getReworkCode());
      row.getCell(3).setCellValue(summaryData.getScrapCode());
    }
    row = sxssfSheet.createRow(currentRowNum++);
    setStyle(tempRow, row, 0, 3);
    row.getCell(0).setCellValue("国内轮合计");
    row.getCell(1).setCellValue(getStringValue(preCheck.getInternalAmount()));
    row.getCell(2).setCellValue("其他轮合计");
    row.getCell(3).setCellValue(getStringValue(preCheck.getOtherAmount()));
    row = sxssfSheet.createRow(currentRowNum++);
    setStyle(tempRow, row, 0, 3);
    row.getCell(0).setCellValue("总合计");
    row.getCell(1).setCellValue(getStringValue(preCheck.getTotalAmount()));

    currentRowNum = 3;
    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 5, 7);
    row.getCell(5).setCellValue("轮型");
    row.getCell(6).setCellValue("次数");
    row.getCell(7).setCellValue("数量");
    List<PreCheckSummaryData> preCheckSummaryData = preCheck.getPreCheck();
    for (int i = 0; i < preCheckSummaryData.size(); i++) {
      PreCheckSummaryData summaryData = preCheckSummaryData.get(i);
      row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 5, 7);
      row.getCell(5).setCellValue(summaryData.getDesign());
      row.getCell(6).setCellValue(getStringValue(summaryData.getTimes()));
      row.getCell(7).setCellValue(getStringValue(summaryData.getAmount()));
    }

    currentRowNum = 3;
    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 9, 11);
    row.getCell(9).setCellValue("轮型");
    row.getCell(10).setCellValue("废码");
    row.getCell(11).setCellValue("数量");
    List<PreCheckScrapData> preCheckScrapDataList = preCheck.getPreCheckScrap();
    for (int i = 0; i < preCheckScrapDataList.size(); i++) {
      PreCheckScrapData summaryData = preCheckScrapDataList.get(i);
      row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 9, 11);
      row.getCell(9).setCellValue(summaryData.getDesign());
      row.getCell(10).setCellValue(summaryData.getScrapCode());
      row.getCell(11).setCellValue(getStringValue(summaryData.getAmount()));
    }
    row = createRowIfNull(sxssfSheet, currentRowNum++, tempRow, 9, 11);
    row.getCell(9).setCellValue("合计");
    row.getCell(11).setCellValue(getStringValue(preCheck.getPreCheckScrapTotal()));
    addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 9, 10);
  }

  private Row createRowIfNull(Sheet sheet, int rowIndex, Row tempRow, int startCol, int endCol) {
    Row row = sheet.getRow(rowIndex);
    if (row == null) {
      row = sheet.createRow(rowIndex);
    }
    setStyle(tempRow, row, startCol, endCol);
    return row;
  }

  private void finalReworkDetail(Workbook workbook, Map<String, Object> parameterMap) {
    FinalReworkData data = multiReportQcStatService.finalReworkDetail(parameterMap);
    genFinalReworkReport(data.getJMachine(), workbook, 0, parameterMap);
    genFinalReworkReport(data.getTMachine(), workbook, 1, parameterMap);
    genFinalReworkReport(data.getKMachine(), workbook, 2, parameterMap);
    for (int i = 0; i < 3; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "基面");
    workbook.setSheetName(1, "踏面");
    workbook.setSheetName(2, "镗孔");
  }

  private void genFinalReworkReport(FinalReworkMachineData machineData, Workbook workbook, int sheetIndex,
      Map<String, Object> parameterMap) {
    Sheet sheet = workbook.getSheetAt(sheetIndex);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Row tempRow = sheet.getRow(2);
    Row detailRow = sheet.getRow(3);
    removeMergedRegion(sheet, 2);
    int currentRowNum = 2;
    Sheet sxssfSheet = getClonedSheet(sheet, sheetIndex);
    currentRowNum = genMachineRecordBlock(tempRow, detailRow, currentRowNum, sxssfSheet, machineData.getNoMachine(), false);
    currentRowNum++;
    genMachineRecordBlock(tempRow, detailRow, currentRowNum, sxssfSheet, machineData.getHasMachine(), true);
  }

  private int genMachineRecordBlock(Row tempRow, Row detailRow, int currentRowNum, Sheet sxssfSheet, FinalReworkStatData data,
      boolean isHasMachine) {
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(tempRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(1).setCellValue(getStringValue(data.getAmount()));
    int start = currentRowNum;
    r = sxssfSheet.createRow(currentRowNum++);
    setStyle(detailRow, r);
    r.getCell(0).setCellValue((isHasMachine ? "有" : "无") + "加工信息");
    r.getCell(1).setCellValue("轮号");
    r.getCell(2).setCellValue("机床号");
    r.getCell(3).setCellValue("操作工号");
    r.getCell(4).setCellValue("返工代码");
    r.getCell(5).setCellValue("第一次加工时间");
    r.getCell(6).setCellValue("终检第一次代码时间");
    List<MachineRecord> detailList = data.getDetail();
    if (detailList.isEmpty()) {
      r = sxssfSheet.createRow(currentRowNum++);
      setStyle(detailRow, r);
    } else {
      for (int i = 0; i < detailList.size(); i++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(detailRow, r);
        MachineRecord record = detailList.get(i);
        r.getCell(1).setCellValue(record.getWheelSerial());
        r.getCell(2).setCellValue(getStringValue(record.getMachineNo()));
        r.getCell(3).setCellValue(record.getOperator());
        r.getCell(4).setCellValue(record.getReworkCode());
        r.getCell(5).setCellValue(getStringValueOfDate(record.getOpeDT(), "yyyy-MM-dd HH:mm"));
        r.getCell(6).setCellValue(getStringValueOfDate(record.getFinalFirstTime(), "yyyy-MM-dd HH:mm"));
      }
    }
    addMergedRegion(sxssfSheet, start, currentRowNum - 1, 0, 0);
    return currentRowNum;
  }

  private void machineRework(Workbook workbook, Map<String, Object> parameterMap) {
    List<String> reworkCode = (List<String>) parameterMap.remove("reworkCode");
    MachineReworkData data = multiReportQcStatService.machineRework(parameterMap, reworkCode);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(0);
    String title = reworkCode == null ? "" : StringUtils.join(reworkCode, ",");
    row.getCell(0).setCellValue(title);

    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    row = sheet.getRow(3);
    row.getCell(1).setCellValue(getStringValue(data.getAmount()));
    row.getCell(3).setCellValue(getStringValueWithScale(data.getReworkPercent(), 2) + "%");
    row.getCell(5).setCellValue(getStringValueWithScale(data.getReworkFinishPercent(), 2) + "%");
    row.getCell(7).setCellValue(getStringValueWithScale(data.getReworkScrapPercent(), 2) + "%");
    Row tempRow = sheet.getRow(17);
    removeMergedRegion(sheet, 4);
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    int currentRowNum = 4;
    MachineReworkTotalData total = data.getTotal();
    List<MachineReworkTotalDetail> totalDetailList = total.getDetail();
    int page = (totalDetailList.size() + 6 - 1) / 6;
    int startIndex = currentRowNum;
    for (int block = 0; block < page; block++) {
      int endIndex = totalDetailList.size() > (block + 1) * 6 ? (block + 1) * 6 : totalDetailList.size();
      for (int i = block * 6; i < endIndex; i++) {
        MachineReworkTotalDetail detail = totalDetailList.get(i);
        Row r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(0).setCellValue("总合计");
        r.getCell(2).setCellValue("合计");
        r.getCell(3 + (i % 6)).setCellValue(detail.getReworkCode());

        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue("返工数");
        r.getCell(2).setCellValue(getStringValue(total.getReworkAmount()));
        r.getCell(3 + (i % 6)).setCellValue(getStringValue(detail.getReworkAmount()));

        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue("返工比");
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
        r.getCell(3 + (i % 6)).setCellValue(getStringValueWithScale(detail.getReworkPercent(), 2) + "%");

        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue("成品数");
        r.getCell(2).setCellValue(getStringValue(total.getFinishAmount()));
        r.getCell(3 + (i % 6)).setCellValue(getStringValue(detail.getFinishAmount()));

        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue("成品比");
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
        r.getCell(3 + (i % 6)).setCellValue(getStringValueWithScale(detail.getReworkFinishPercent(), 2) + "%");

        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue("废品数");
        r.getCell(2).setCellValue(getStringValue(total.getScrapAmount()));
        r.getCell(3 + (i % 6)).setCellValue(getStringValue(detail.getScrapAmount()));

        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue("废品比");
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
        r.getCell(3 + (i % 6)).setCellValue(getStringValueWithScale(detail.getReworkScrapPercent(), 2) + "%");
      }
    }
    addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    List<MachineReworkDateData> dateList = data.getData();
    for (int dateIndex = 0; dateIndex < dateList.size(); dateIndex++) {
      MachineReworkDateData dateData = dateList.get(dateIndex);
      totalDetailList = dateData.getDetail();
      page = (totalDetailList.size() + 6 - 1) / 6;
      startIndex = currentRowNum;
      for (int block = 0; block < page; block++) {
        int endIndex = totalDetailList.size() > (block + 1) * 6 ? (block + 1) * 6 : totalDetailList.size();
        for (int i = block * 6; i < endIndex; i++) {
          MachineReworkTotalDetail detail = totalDetailList.get(i);
          Row r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(0).setCellValue(dateData.getOpeDT());
          r.getCell(2).setCellValue("合计");
          r.getCell(3 + (i % 6)).setCellValue(detail.getReworkCode());

          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(1).setCellValue("返工数");
          r.getCell(2).setCellValue(getStringValue(total.getReworkAmount()));
          r.getCell(3 + (i % 6)).setCellValue(getStringValue(detail.getReworkAmount()));

          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(1).setCellValue("返工比");
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
          r.getCell(3 + (i % 6)).setCellValue(getStringValueWithScale(detail.getReworkPercent(), 2) + "%");

          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(1).setCellValue("成品数");
          r.getCell(2).setCellValue(getStringValue(total.getFinishAmount()));
          r.getCell(3 + (i % 6)).setCellValue(getStringValue(detail.getFinishAmount()));

          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(1).setCellValue("成品比");
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
          r.getCell(3 + (i % 6)).setCellValue(getStringValueWithScale(detail.getReworkFinishPercent(), 2) + "%");

          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(1).setCellValue("废品数");
          r.getCell(2).setCellValue(getStringValue(total.getScrapAmount()));
          r.getCell(3 + (i % 6)).setCellValue(getStringValue(detail.getScrapAmount()));

          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(1).setCellValue("废品比");
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
          r.getCell(3 + (i % 6)).setCellValue(getStringValueWithScale(detail.getReworkScrapPercent(), 2) + "%");
        }
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "机床返修");
  }

  private void finalCheckRework(Workbook workbook, Map<String, Object> parameterMap) {
    List<String> reworkCode = (List<String>) parameterMap.remove("reworkCode");
    FinalCheckReworkData data = multiReportQcStatService.finalCheckRework(parameterMap, reworkCode);
    genFinalCheckReworkReport(workbook, 0, parameterMap, reworkCode, data.getCheck());
    genFinalCheckReworkReport(workbook, 1, parameterMap, reworkCode, data.getPour());
    for (int i = 0; i < 2; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "终检日期");
    workbook.setSheetName(1, "浇注日期");
  }

  private void genFinalCheckReworkReport(Workbook workbook, int sheetIndex, Map<String, Object> parameterMap,
      List<String> reworkCode, FinalCheckDateData checkData) {
    Sheet sheet = workbook.getSheetAt(sheetIndex);
    Row row = sheet.getRow(0);
    String title = reworkCode == null ? "" : StringUtils.join(reworkCode, ",");
    row.getCell(0).setCellValue(title);

    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    removeMergedRegion(sheet, 3);
    Row tempRow = sheet.getRow(3);
    Row detailRow = sheet.getRow(4);
    Sheet sxssfSheet = getClonedSheet(sheet, sheetIndex);

    int currentRowNum = 3;
    row = sxssfSheet.createRow(currentRowNum++);
    setStyle(tempRow, row);
    row.getCell(0).setCellValue("总合计");
    row.getCell(2).setCellValue("终检次数");
    row.getCell(3).setCellValue(getStringValue(checkData.getTotal().getCheckTimes()));
    addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 7);

    List<String> detailList = checkData.getTotal().getDetail();
    for (int i = 0; i < detailList.size(); i++) {
      if (i % 6 == 0) {
        row = sxssfSheet.createRow(currentRowNum++);
        setStyle(detailRow, row);
      }
      row.getCell(2 + (i % 6)).setCellValue(detailList.get(i));
    }
    addMergedRegion(sxssfSheet, 3, currentRowNum - 1, 0, 1);

    List<FinalCheckDateRecord> leaderDataList = checkData.getDate();
    int startIndex;
    int lineIndex;
    for (int i = 0; i < leaderDataList.size(); i++) {
      startIndex = currentRowNum;
      FinalCheckDateRecord leaderData = leaderDataList.get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(getStringValueOfDate(leaderData.getDate(), "yyyy-MM-dd"));
      r.getCell(1).setCellValue("合计");
      r.getCell(2).setCellValue("终检次数");
      r.getCell(3).setCellValue(getStringValue(leaderData.getTotal().getCheckTimes()));
      addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 7);
      detailList = leaderData.getTotal().getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        if (j % 6 == 0) {
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(detailRow, r);
        }
        r.getCell(2 + (j % 6)).setCellValue(detailList.get(j));
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 1, 1);
      List<FinalCheckLineTotalData> lineDataList = leaderData.getLine();
      for (int k = 0; k < lineDataList.size(); k++) {
        lineIndex = currentRowNum;
        FinalCheckLineTotalData lineData = lineDataList.get(k);
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue(lineData.getXh());
        r.getCell(2).setCellValue("终检次数");
        r.getCell(3).setCellValue(getStringValue(lineData.getCheckTimes()));
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 7);
        detailList = lineData.getDetail();
        for (int l = 0; l < detailList.size(); l++) {
          if (l % 6 == 0) {
            r = sxssfSheet.createRow(currentRowNum++);
            setStyle(detailRow, r);
          }
          r.getCell(2 + (l % 6)).setCellValue(detailList.get(l));
        }
        addMergedRegion(sxssfSheet, lineIndex, currentRowNum - 1, 1, 1);
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    }
  }

  private void reworkShift(Workbook workbook, Map<String, Object> parameterMap) {
    List<String> reworkCode = (List<String>) parameterMap.remove("reworkCode");
    ReworkShiftData data = multiReportQcStatService.reworkShift(parameterMap, reworkCode);
    genReworkShiftReport(workbook, 0, parameterMap, reworkCode, data.getPrecheck(), true);
    genReworkShiftReport(workbook, 1, parameterMap, reworkCode, data.getFinalcheck(), false);
    for (int i = 0; i < 2; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "预检");
    workbook.setSheetName(1, "终检");
  }

  private void genReworkShiftReport(Workbook workbook, int sheetIndex, Map<String, Object> parameterMap, List<String> reworkCode,
      ReworkShiftCheckData checkData, boolean isPrecheck) {
    Sheet sheet = workbook.getSheetAt(sheetIndex);
    Row row = sheet.getRow(0);
    String title = reworkCode == null ? "" : StringUtils.join(reworkCode, ",");
    row.getCell(0).setCellValue(title);

    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    row = sheet.getRow(3);
    row.getCell(2).setCellValue(getStringValue(checkData.getPreAmount()));
    row.getCell(4).setCellValue(getStringValue(checkData.getScrapCodeAmount()));
    row.getCell(6).setCellValue(getStringValueWithScale(checkData.getScrapCodePrecheckPercent(), 2) + "%");

    Sheet sxssfSheet = getClonedSheet(sheet, sheetIndex);
    removeMergedRegion(sxssfSheet, 4);
    int currentRowNum = 4;
    Row tempRow = sheet.getRow(3);
    List<ReworkShiftLeaderData> leaderDataList = checkData.getData();
    int startIndex;
    for (int i = 0; i < leaderDataList.size(); i++) {
      startIndex = currentRowNum;
      ReworkShiftLeaderData leaderData = leaderDataList.get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(leaderData.getInspectorId());
      r.getCell(1).setCellValue(isPrecheck ? "预检数量" : "终检数量");
      r.getCell(2).setCellValue(getStringValue(leaderData.getPreAmount()));
      r.getCell(3).setCellValue("返码数量");
      r.getCell(4).setCellValue(getStringValue(leaderData.getScrapCodeAmount()));
      r.getCell(5).setCellValue(isPrecheck ? "返码/预检%" : "返码/终检%");
      r.getCell(6).setCellValue(getStringValueWithScale(leaderData.getScrapCodePrecheckPercent(), 2) + "%");

      List<String> detailList = leaderData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        if (j % 3 == 0) {
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(1).setCellValue(detailList.get(j));
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
        } else if (j % 3 == 1) {
          r.getCell(3).setCellValue(detailList.get(j));
        } else {
          r.getCell(5).setCellValue(detailList.get(j));
        }
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    }
  }

  private void scrapLadle(Workbook workbook, Map<String, Object> parameterMap) {
    List<String> scrapCode = (List<String>) parameterMap.remove("scrapCode");
    ScrapLadleData data = multiReportQcStatService.scrapLadle(parameterMap, scrapCode);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(0);
    String title = StringUtils.join(scrapCode, ",");
    row.getCell(0).setCellValue(title);

    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    row = sheet.getRow(3);
    row.getCell(2).setCellValue(getStringValue(data.getTotalAmount()));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    removeMergedRegion(sxssfSheet, 4);
    int currentRowNum = 4;
    Row tempRow = sheet.getRow(currentRowNum);
    Row titleRow = sheet.getRow(currentRowNum + 1);
    Row detailRow = sheet.getRow(currentRowNum + 2);
    List<ScrapLadleRecord> recordList = data.getList();
    int mergeStart = 4;
    for (int i = 0; i < recordList.size(); i++) {
      ScrapLadleRecord record = recordList.get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(record.getScrapCode());
      r.getCell(1).setCellValue("合计");
      r.getCell(2).setCellValue(getStringValue(record.getTotalAmount()));
      List<ScrapLadleSeqRecord> seqRecordList = record.getData();
      for (int j = 0; j < seqRecordList.size(); j++) {
        ScrapLadleSeqRecord seqRecord = seqRecordList.get(j);
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(titleRow, r);
        r.getCell(1).setCellValue(getStringValue(seqRecord.getXh()));
        r.getCell(2).setCellValue("轮号");
        r.getCell(3).setCellValue("轮型");
        r.getCell(4).setCellValue("小包号");
        r.getCell(5).setCellValue("浇注温度");
        r.getCell(6).setCellValue("浇注时间");
        List<ScrapLadleSeqDetail> detailList = seqRecord.getDetail();
        for (int k = 0; k < detailList.size(); k++) {
          ScrapLadleSeqDetail detail = detailList.get(k);
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(detailRow, r);
          r.getCell(2).setCellValue(detail.getWheelSerial());
          r.getCell(3).setCellValue(detail.getDesign());
          r.getCell(4).setCellValue(detail.getLadleRecordKey());
          r.getCell(5).setCellValue(getStringValueWithScale(detail.getPourTemp(), 0));
          r.getCell(6).setCellValue(getStringValueOfDate(detail.getPourDT(), "yyyy-MM-dd HH:mm"));
        }
        addMergedRegion(sxssfSheet, currentRowNum - detailList.size() - 1, currentRowNum - 1, 1, 1);
      }
      addMergedRegion(sxssfSheet, mergeStart, currentRowNum - 1, 0, 0);
      mergeStart = currentRowNum;
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "小包浇注顺序");
  }

  private void scrapShift(Workbook workbook, Map<String, Object> parameterMap) {
    List<String> scrapCode = (List<String>) parameterMap.remove("scrapCode");
    ScrapShiftData data = multiReportQcStatService.scrapShift(parameterMap, scrapCode);
    String title = StringUtils.join(scrapCode, ",");
    genScrapShiftQcReport(data.getQc(), workbook, parameterMap, title);
    genScrapShiftModelReport(data.getModel(), workbook, parameterMap, title);
    genScrapShiftFurnaceReport(data.getFurnace(), workbook, parameterMap, title);
    genScrapShiftMachineReport(data.getMachine(), workbook, parameterMap, title);
    for (int i = 0; i < 4; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "质检");
    workbook.setSheetName(1, "造型");
    workbook.setSheetName(2, "熔炼");
    workbook.setSheetName(3, "机加");
  }

  private void genScrapShiftMachineReport(ScrapShiftMachineData machine, Workbook workbook, Map<String, Object> parameterMap,
      String title) {
    Sheet sheet = workbook.getSheetAt(3);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    row = sheet.getRow(3);
    row.getCell(2).setCellValue(getStringValue(machine.getAmount()));
    row.getCell(4).setCellValue(getStringValue(machine.getScrapAmount()));
    row.getCell(6).setCellValue(getStringValueWithScale(machine.getScrapPercent(), 2) + "%");
    int currentRowNum = 4;
    Row tempRow = sheet.getRow(3);
    Sheet sxssfSheet = getClonedSheet(sheet, 3);
    removeMergedRegion(sxssfSheet, currentRowNum);
    List<ScrapShiftQcTopData> topList = machine.getTop();
    if (topList.isEmpty()) {
      return;
    }
    int startIndex = currentRowNum;
    Row r1 = null;
    Row r2 = null;
    for (int i = 0; i < topList.size(); i++) {
      ScrapShiftQcTopData topData = topList.get(i);
      if (i % 3 == 0) {
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        r1.getCell(0).setCellValue("入库数量(踏面)");
        r1.getCell(1).setCellValue("工长名称");
        r1.getCell(2).setCellValue(topData.getInspectorId());

        r2 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r2);
        r2.getCell(1).setCellValue("入库数量");
        r2.getCell(2).setCellValue(getStringValue(topData.getAmount()));
      } else if (i % 3 == 1) {
        r1.getCell(3).setCellValue("工长名称");
        r1.getCell(4).setCellValue(topData.getInspectorId());
        r2.getCell(3).setCellValue("入库数量");
        r2.getCell(4).setCellValue(getStringValue(topData.getAmount()));
      } else {
        r1.getCell(5).setCellValue("工长名称");
        r1.getCell(6).setCellValue(topData.getInspectorId());
        r2.getCell(5).setCellValue("入库数量");
        r2.getCell(6).setCellValue(getStringValue(topData.getAmount()));
      }
    }
    addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    //基面
    List<ScrapShiftMachineLeaderData> jMachine = machine.getJMachine();
    currentRowNum = genScrapShiftMachineDetail(currentRowNum, tempRow, sxssfSheet, jMachine);
    List<ScrapShiftMachineLeaderData> tMachine = machine.getTMachine();
    currentRowNum = genScrapShiftMachineDetail(currentRowNum, tempRow, sxssfSheet, tMachine);
    List<ScrapShiftMachineLeaderData> kMachine = machine.getKMachine();
    currentRowNum = genScrapShiftMachineDetail(currentRowNum, tempRow, sxssfSheet, kMachine);
    List<ScrapShiftMachineLeaderData> wMachine = machine.getWMachine();
    genScrapShiftMachineDetail(currentRowNum, tempRow, sxssfSheet, wMachine);
  }

  private int genScrapShiftMachineDetail(int currentRowNum, Row tempRow, Sheet sxssfSheet,
      List<ScrapShiftMachineLeaderData> jMachine) {
    if (!jMachine.isEmpty()) {
      int startIndex = currentRowNum;
      Row r1;
      for (int i = 0; i < jMachine.size(); i++) {
        ScrapShiftMachineLeaderData leaderData = jMachine.get(i);
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        String title = "";
        switch (leaderData.getType()) {
          case "jmachine":
            title = "基面";
            break;
          case "tmachine":
            title = "踏面";
            break;
          case "kmachine":
            title = "镗孔";
            break;
          case "wmachine":
            title = "外辐板";
            break;
        }
        r1.getCell(0).setCellValue(title);
        r1.getCell(1).setCellValue("工长号");
        r1.getCell(2).setCellValue("员工号");
        r1.getCell(3).setCellValue("合计");
        r1.getCell(5).setCellValue(getStringValue(leaderData.getScrapAmount()));
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        r1.getCell(3).setCellValue("废品代码");
        r1.getCell(5).setCellValue("确废数量");
        addMergedRegion(sxssfSheet, currentRowNum - 2, currentRowNum - 1, 1, 1);
        addMergedRegion(sxssfSheet, currentRowNum - 2, currentRowNum - 1, 2, 2);
        addMergedRegion(sxssfSheet, currentRowNum - 2, currentRowNum - 2, 3, 4);
        addMergedRegion(sxssfSheet, currentRowNum - 2, currentRowNum - 2, 5, 6);
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
        List<ScrapShiftMachineRecord> detailList = leaderData.getDetail();
        for (int j = 0; j < detailList.size(); j++) {
          ScrapShiftMachineRecord detail = detailList.get(j);
          r1 = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r1);
          r1.getCell(1).setCellValue(detail.getInspectorId());
          r1.getCell(2).setCellValue(detail.getOperator());
          r1.getCell(3).setCellValue(detail.getScrapCode());
          r1.getCell(5).setCellValue(detail.getScrapAmount());
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
          addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
        }
        addMergedRegion(sxssfSheet, currentRowNum - detailList.size(), currentRowNum - 1, 1, 1);
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    }
    return currentRowNum;
  }

  private void genScrapShiftFurnaceReport(ScrapShiftQcData furnace, Workbook workbook, Map<String, Object> parameterMap,
      String title) {
    Sheet sheet = workbook.getSheetAt(2);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    row = sheet.getRow(3);
    row.getCell(2).setCellValue(getStringValue(furnace.getAmount()));
    row.getCell(4).setCellValue(getStringValue(furnace.getScrapAmount()));
    row.getCell(6).setCellValue(getStringValueWithScale(furnace.getScrapPercent(), 2) + "%");
    int currentRowNum = 4;
    Row tempRow = sheet.getRow(3);
    Sheet sxssfSheet = getClonedSheet(sheet, 2);
    removeMergedRegion(sxssfSheet, currentRowNum);
    List<ScrapShiftQcTopData> topList = furnace.getTop();
    if (topList.isEmpty()) {
      return;
    }
    int startIndex = currentRowNum;
    Row r1 = null;
    Row r2 = null;
    for (int i = 0; i < topList.size(); i++) {
      ScrapShiftQcTopData topData = topList.get(i);
      if (i % 3 == 0) {
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        r1.getCell(0).setCellValue("入库数量");
        r1.getCell(1).setCellValue("工长名称");
        r1.getCell(2).setCellValue(topData.getInspectorId());

        r2 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r2);
        r2.getCell(1).setCellValue("入库数量");
        r2.getCell(2).setCellValue(getStringValue(topData.getAmount()));
      } else if (i % 3 == 1) {
        r1.getCell(3).setCellValue("工长名称");
        r1.getCell(4).setCellValue(topData.getInspectorId());
        r2.getCell(3).setCellValue("入库数量");
        r2.getCell(4).setCellValue(getStringValue(topData.getAmount()));
      } else {
        r1.getCell(5).setCellValue("工长名称");
        r1.getCell(6).setCellValue(topData.getInspectorId());
        r2.getCell(5).setCellValue("入库数量");
        r2.getCell(6).setCellValue(getStringValue(topData.getAmount()));
      }
    }
    addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);

    List<ScrapShiftQcLeaderData> leaderDataList = furnace.getData();
    for (int i = 0; i < leaderDataList.size(); i++) {
      startIndex = currentRowNum;
      ScrapShiftQcLeaderData leaderData = leaderDataList.get(i);
      r1 = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r1);
      r1.getCell(0).setCellValue(leaderData.getInspectorId());
      r1.getCell(1).setCellValue("废品代码");
      r1.getCell(3).setCellValue("小计");
      r1.getCell(4).setCellValue(getStringValue(leaderData.getScrapAmount()));
      r1.getCell(5).setCellValue("小计");
      r1.getCell(6).setCellValue(getStringValueWithScale(leaderData.getScrapPercent(), 2) + "%");

      r1 = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r1);
      r1.getCell(3).setCellValue("确废数量");
      r1.getCell(5).setCellValue("确废数量/(确废数量+入库数量)%");
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 1, 2);
      addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
      addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
      List<ScrapShiftQcRecord> detailList = leaderData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        ScrapShiftQcRecord detail = detailList.get(j);
        r1.getCell(1).setCellValue(detail.getScrapCode());
        r1.getCell(3).setCellValue(getStringValue(detail.getScrapAmount()));
        r1.getCell(5).setCellValue(getStringValueWithScale(detail.getScrapPercent(), 2) + "%");
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    }
  }

  private void genScrapShiftModelReport(ScrapShiftModelData model, Workbook workbook, Map<String, Object> parameterMap,
      String title) {
    Sheet sheet = workbook.getSheetAt(1);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    row = sheet.getRow(3);
    row.getCell(2).setCellValue(getStringValue(model.getAmount()));
    row.getCell(4).setCellValue(getStringValue(model.getScrapAmount()));
    row.getCell(6).setCellValue(getStringValueWithScale(model.getScrapPercent(), 2) + "%");
    int currentRowNum = 4;
    Row tempRow = sheet.getRow(3);
    Sheet sxssfSheet = getClonedSheet(sheet, 1);
    removeMergedRegion(sxssfSheet, currentRowNum);
    List<ScrapShiftQcTopData> topList = model.getTop();
    if (topList.isEmpty()) {
      return;
    }
    int startIndex = currentRowNum;
    Row r1 = null;
    Row r2 = null;
    for (int i = 0; i < topList.size(); i++) {
      ScrapShiftQcTopData topData = topList.get(i);
      if (i % 3 == 0) {
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        r1.getCell(0).setCellValue("入库数量");
        r1.getCell(1).setCellValue("工长名称");
        r1.getCell(2).setCellValue(topData.getInspectorId());

        r2 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r2);
        r2.getCell(1).setCellValue("入库数量");
        r2.getCell(2).setCellValue(getStringValue(topData.getAmount()));
      } else if (i % 3 == 1) {
        r1.getCell(3).setCellValue("工长名称");
        r1.getCell(4).setCellValue(topData.getInspectorId());
        r2.getCell(3).setCellValue("入库数量");
        r2.getCell(4).setCellValue(getStringValue(topData.getAmount()));
      } else {
        r1.getCell(5).setCellValue("工长名称");
        r1.getCell(6).setCellValue(topData.getInspectorId());
        r2.getCell(5).setCellValue("入库数量");
        r2.getCell(6).setCellValue(getStringValue(topData.getAmount()));
      }
    }
    addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);

    List<ScrapShiftModelLeaderData> leaderDataList = model.getData();
    for (int i = 0; i < leaderDataList.size(); i++) {
      startIndex = currentRowNum;
      ScrapShiftModelLeaderData leaderData = leaderDataList.get(i);
      r1 = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r1);
      r1.getCell(0).setCellValue(leaderData.getInspectorId());
      r1.getCell(1).setCellValue("线号");
      r1.getCell(2).setCellValue("废品代码");
      r1.getCell(3).setCellValue("小计");
      r1.getCell(4).setCellValue(getStringValue(leaderData.getScrapAmount()));
      r1.getCell(5).setCellValue("小计");
      r1.getCell(6).setCellValue(getStringValueWithScale(leaderData.getScrapPercent(), 2) + "%");

      r1 = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r1);
      r1.getCell(3).setCellValue("确废数量");
      r1.getCell(5).setCellValue("确废数量/(确废数量+入库数量)%");
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 1, 1);
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 2, 2);
      addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
      addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
      List<ScrapShiftModelRecord> detailList = leaderData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        ScrapShiftModelRecord detail = detailList.get(j);
        r1.getCell(1).setCellValue(detail.getXh());
        r1.getCell(2).setCellValue(detail.getScrapCode());
        r1.getCell(3).setCellValue(getStringValue(detail.getScrapAmount()));
        r1.getCell(5).setCellValue(getStringValueWithScale(detail.getScrapPercent(), 2) + "%");
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    }
  }

  private void genScrapShiftQcReport(ScrapShiftQcData qc, Workbook workbook, Map<String, Object> parameterMap, String title) {
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(title);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row = sheet.getRow(2);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    row = sheet.getRow(3);
    row.getCell(2).setCellValue(getStringValue(qc.getAmount()));
    row.getCell(4).setCellValue(getStringValue(qc.getScrapAmount()));
    row.getCell(6).setCellValue(getStringValueWithScale(qc.getScrapPercent(), 2) + "%");
    int currentRowNum = 4;
    Row tempRow = sheet.getRow(3);
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    removeMergedRegion(sxssfSheet, currentRowNum);
    List<ScrapShiftQcTopData> topList = qc.getTop();
    if (topList.isEmpty()) {
      return;
    }
    int startIndex = currentRowNum;
    Row r1 = null;
    Row r2 = null;
    for (int i = 0; i < topList.size(); i++) {
      ScrapShiftQcTopData topData = topList.get(i);
      if (i % 3 == 0) {
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        r1.getCell(0).setCellValue("入库数量");
        r1.getCell(1).setCellValue("工长名称");
        r1.getCell(2).setCellValue(topData.getInspectorId());

        r2 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r2);
        r2.getCell(1).setCellValue("入库数量");
        r2.getCell(2).setCellValue(getStringValue(topData.getAmount()));
      } else if (i % 3 == 1) {
        r1.getCell(3).setCellValue("工长名称");
        r1.getCell(4).setCellValue(topData.getInspectorId());
        r2.getCell(3).setCellValue("入库数量");
        r2.getCell(4).setCellValue(getStringValue(topData.getAmount()));
      } else {
        r1.getCell(5).setCellValue("工长名称");
        r1.getCell(6).setCellValue(topData.getInspectorId());
        r2.getCell(5).setCellValue("入库数量");
        r2.getCell(6).setCellValue(getStringValue(topData.getAmount()));
      }
    }
    addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);

    List<ScrapShiftQcLeaderData> leaderDataList = qc.getData();
    for (int i = 0; i < leaderDataList.size(); i++) {
      startIndex = currentRowNum;
      ScrapShiftQcLeaderData leaderData = leaderDataList.get(i);
      r1 = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r1);
      r1.getCell(0).setCellValue(leaderData.getInspectorId());
      r1.getCell(1).setCellValue("废品代码");
      r1.getCell(3).setCellValue("小计");
      r1.getCell(4).setCellValue(getStringValue(leaderData.getScrapAmount()));
      r1.getCell(5).setCellValue("小计");
      r1.getCell(6).setCellValue(getStringValueWithScale(leaderData.getScrapPercent(), 2) + "%");

      r1 = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r1);
      r1.getCell(3).setCellValue("确废数量");
      r1.getCell(5).setCellValue("确废数量/(确废数量+入库数量)%");
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 1, 2);
      addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
      addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
      List<ScrapShiftQcRecord> detailList = leaderData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r1 = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r1);
        ScrapShiftQcRecord detail = detailList.get(j);
        r1.getCell(1).setCellValue(detail.getScrapCode());
        r1.getCell(3).setCellValue(getStringValue(detail.getScrapAmount()));
        r1.getCell(5).setCellValue(getStringValueWithScale(detail.getScrapPercent(), 2) + "%");
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 1, 2);
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 3, 4);
        addMergedRegion(sxssfSheet, currentRowNum - 1, currentRowNum - 1, 5, 6);
      }
      addMergedRegion(sxssfSheet, startIndex, currentRowNum - 1, 0, 0);
    }
  }

  private void wheelDetail(Workbook workbook, Map<String, Object> parameterMap) {
    List<WheelDetail> data = multiReportQcStatService.wheelDetail(parameterMap);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(0);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " 单日轮号明细"));
    row = sheet.getRow(1);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " Wheel List"));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < data.size(); i++) {
      WheelDetail wheelDetail = data.get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(wheelDetail.getHeatRecordKey());
      r.getCell(1).setCellValue(getStringValue(wheelDetail.getTapSeq()));
      r.getCell(2).setCellValue(getStringValue(wheelDetail.getLadleSeq()));
      r.getCell(3).setCellValue(wheelDetail.getWheelSerial());
      r.getCell(4).setCellValue(wheelDetail.getOpenTimeAct());
      r.getCell(5).setCellValue(wheelDetail.getScrapCode());
      r.getCell(6).setCellValue(isChecked(wheelDetail.getConfirmedScrap()) ? "√" : "");
      r.getCell(7).setCellValue(getStringValueOfDate(wheelDetail.getScrapDate(), "yyyy/M/d"));
      r.getCell(8).setCellValue(getStringValue(wheelDetail.getPre()));
      r.getCell(9).setCellValue(getStringValue(wheelDetail.getFinalTimes()));
      r.getCell(10).setCellValue(getStringValue(wheelDetail.getUltra()));
      r.getCell(11).setCellValue(getStringValue(wheelDetail.getBalance()));
      r.getCell(12).setCellValue(isChecked(wheelDetail.getFinished()) ? "√" : "");
      r.getCell(13).setCellValue(getStringValue(wheelDetail.getMecSerial()));
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "单日轮号");
  }

  private void reworkPercent(Workbook workbook, Map<String, Object> parameterMap) {
    List<String> reworkCode = (List<String>) parameterMap.remove("reworkCode");
    ReworkPercentData data = multiReportQcStatService.reworkPercent(parameterMap, reworkCode);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row detailRow = sheet.getRow(currentRowNum + 1);
    Row totalRow = sheet.getRow(5);
    for (int i = 0; i < data.getList().size(); i++) {
      ReworkPercentDateData totalData = data.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue("合计");
      r.getCell(3).setCellValue(totalData.getAmount());
      r.getCell(4).setCellValue(totalData.getPassedAmount());
      r.getCell(5).setCellValue(getStringValueWithScale(totalData.getPassedPercent(), 2) + "%");
      List<ReworkPercentCodeData> detailList = totalData.getData();
      int dateRow = 0;
      for (int j = 0; j < detailList.size(); j++) {
        ReworkPercentCodeData detail = detailList.get(j);
        List<ReworkPercentRecord> recordList = detail.getDetail();
        for (int k = 0; k < recordList.size(); k++) {
          dateRow++;
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(detailRow, r);
          ReworkPercentRecord record = recordList.get(k);
          r.getCell(0).setCellValue(getStringValueOfDate(totalData.getOpeDT(), "yyyy/M/d"));
          r.getCell(1).setCellValue(record.getReworkCode());
          r.getCell(2).setCellValue(record.getDesign());
          r.getCell(3).setCellValue(record.getAmount());
          r.getCell(4).setCellValue(record.getPassedAmount());
          r.getCell(5).setCellValue(getStringValueWithScale(record.getPassedPercent(), 2) + "%");
        }
        addMergedRegion(sxssfSheet, currentRowNum - recordList.size(), currentRowNum - 1, 1, 1);
      }
      addMergedRegion(sxssfSheet, currentRowNum - dateRow, currentRowNum - 1, 0, 0);
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("总合计");
    r.getCell(3).setCellValue(data.getAmount());
    r.getCell(4).setCellValue(data.getPassedAmount());
    r.getCell(5).setCellValue(getStringValueWithScale(data.getPassedPercent(), 2) + "%");
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "日期");
  }

  private void balancePercent(Workbook workbook, Map<String, Object> parameterMap) {
    BalancePercentData data = multiReportQcStatService.balancePercent(parameterMap);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(1);
    String title = "*";
    if (parameterMap.containsKey("design")) {
      List<String> designList = (List<String>) parameterMap.get("design");
      title = StringUtils.join(designList, ",");
    }
    row.getCell(0).setCellValue(title);
    row = sheet.getRow(2);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    removeMergedRegion(sxssfSheet, 4);
    int currentRowNum = 4;
    Row tempRow = sheet.getRow(currentRowNum);
    Row detailRow = sheet.getRow(currentRowNum + 1);
    Row totalRow = sheet.getRow(6);
    for (int i = 0; i < data.getData().size(); i++) {
      BalancePercentTotalData totalData = data.getData().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(getStringValueOfDate(totalData.getOpeDT(), "yyyy/M/d"));
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getQualifiedAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getQualifiedPercent(), 2) + "%");
      List<BalancePercentRecord> detailList = totalData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(detailRow, r);
        BalancePercentRecord record = detailList.get(j);
        r.getCell(0).setCellValue(record.getBalanceInspectorId());
        r.getCell(2).setCellValue(record.getAmount());
        r.getCell(3).setCellValue(record.getQualifiedAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(record.getQualifiedPercent(), 2) + "%");
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(data.getAmount());
    r.getCell(3).setCellValue(data.getQualifiedAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(data.getQualifiedPercent(), 2) + "%");
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "一次平衡率");
  }

  private void finalCheckPercent(Workbook workbook, Map<String, Object> parameterMap) {
    String shift = parameterMap.containsKey("shift") ? parameterMap.get("shift").toString() : "";
    FinalCheckPercentData data = multiReportQcStatService.finalCheckPercent(parameterMap);
    genFinalCheckPercentDateReport(workbook, data.getDate(), parameterMap, shift);
    genFinalCheckPercentDesignReport(workbook, data.getDesign(), parameterMap, shift);
    genFinalCheckPercentStaffReport(workbook, data.getStaff(), parameterMap, shift);

    for (int i = 0; i < 3; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "日期");
    workbook.setSheetName(1, "轮型");
    workbook.setSheetName(2, "员工");
  }

  private void genFinalCheckPercentStaffReport(Workbook workbook, FinalCheckPercentStaffTopData staff,
      Map<String, Object> parameterMap, String shift) {
    Sheet sheet = workbook.getSheetAt(2);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue("终检一次通过率(" + (StringUtils.isEmpty(shift) ? "*" : shift) + "班)情况统计表");
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 2);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row detailRow = sheet.getRow(currentRowNum + 1);
    Row totalRow = sheet.getRow(5);
    for (int i = 0; i < staff.getList().size(); i++) {
      FinalCheckPercentStaffTotalData totalData = staff.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(totalData.getCopeInspectorID());
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getNoReworkAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getNoReworkPercent(), 2) + "%");
      r.getCell(5).setCellValue(totalData.getNoScrapAmount());
      r.getCell(6).setCellValue(getStringValueWithScale(totalData.getNoScrapPercent(), 2) + "%");
      r.getCell(7).setCellValue(totalData.getNoReworkScrapAmount());
      r.getCell(8).setCellValue(getStringValueWithScale(totalData.getNoReworkScrapPercent(), 2) + "%");
      List<FinalCheckPercentRecord> detailList = totalData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(detailRow, r);
        FinalCheckPercentRecord record = detailList.get(j);
        r.getCell(0).setCellValue(getStringValueOfDate(record.getOpeDT(), "yyyy/M/d"));
        r.getCell(2).setCellValue(record.getAmount());
        r.getCell(3).setCellValue(record.getNoReworkAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(record.getNoReworkPercent(), 2) + "%");
        r.getCell(5).setCellValue(record.getNoScrapAmount());
        r.getCell(6).setCellValue(getStringValueWithScale(record.getNoScrapPercent(), 2) + "%");
        r.getCell(7).setCellValue(record.getNoReworkScrapAmount());
        r.getCell(8).setCellValue(getStringValueWithScale(record.getNoReworkScrapPercent(), 2) + "%");
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(staff.getAmount());
    r.getCell(3).setCellValue(staff.getNoReworkAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(staff.getNoReworkPercent(), 2) + "%");
    r.getCell(5).setCellValue(staff.getNoScrapAmount());
    r.getCell(6).setCellValue(getStringValueWithScale(staff.getNoScrapPercent(), 2) + "%");
    r.getCell(7).setCellValue(staff.getNoReworkScrapAmount());
    r.getCell(8).setCellValue(getStringValueWithScale(staff.getNoReworkScrapPercent(), 2) + "%");
  }

  private void genFinalCheckPercentDesignReport(Workbook workbook, FinalCheckPercentDesignTopData design,
      Map<String, Object> parameterMap, String shift) {
    Sheet sheet = workbook.getSheetAt(1);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue("终检一次通过率(" + (StringUtils.isEmpty(shift) ? "*" : shift) + "班)情况统计表");
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 1);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row dateRow = sheet.getRow(currentRowNum + 1);
    Row detailRow = sheet.getRow(currentRowNum + 2);
    Row totalRow = sheet.getRow(6);
    for (int i = 0; i < design.getList().size(); i++) {
      FinalCheckPercentDesignTotalData totalData = design.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(totalData.getDesign());
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getNoReworkAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getNoReworkPercent(), 2) + "%");
      r.getCell(5).setCellValue(totalData.getNoScrapAmount());
      r.getCell(6).setCellValue(getStringValueWithScale(totalData.getNoScrapPercent(), 2) + "%");
      r.getCell(7).setCellValue(totalData.getNoReworkScrapAmount());
      r.getCell(8).setCellValue(getStringValueWithScale(totalData.getNoReworkScrapPercent(), 2) + "%");
      List<FinalCheckPercentDateTotalData> detailList = totalData.getData();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(dateRow, r);
        FinalCheckPercentDateTotalData detail = detailList.get(j);
        r.getCell(0).setCellValue(getStringValueOfDate(detail.getOpeDT(), "yyyy/M/d"));
        r.getCell(2).setCellValue(detail.getAmount());
        r.getCell(3).setCellValue(detail.getNoReworkAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(detail.getNoReworkPercent(), 2) + "%");
        r.getCell(5).setCellValue(detail.getNoScrapAmount());
        r.getCell(6).setCellValue(getStringValueWithScale(detail.getNoScrapPercent(), 2) + "%");
        r.getCell(7).setCellValue(detail.getNoReworkScrapAmount());
        r.getCell(8).setCellValue(getStringValueWithScale(detail.getNoReworkScrapPercent(), 2) + "%");
        List<FinalCheckPercentRecord> recordList = detail.getDetail();
        for (int k = 0; k < recordList.size(); k++) {
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(detailRow, r);
          FinalCheckPercentRecord record = recordList.get(k);
          r.getCell(0).setCellValue(record.getCopeInspectorID());
          r.getCell(2).setCellValue(record.getAmount());
          r.getCell(3).setCellValue(record.getNoReworkAmount());
          r.getCell(4).setCellValue(getStringValueWithScale(record.getNoReworkPercent(), 2) + "%");
          r.getCell(5).setCellValue(record.getNoScrapAmount());
          r.getCell(6).setCellValue(getStringValueWithScale(record.getNoScrapPercent(), 2) + "%");
          r.getCell(7).setCellValue(record.getNoReworkScrapAmount());
          r.getCell(8).setCellValue(getStringValueWithScale(record.getNoReworkScrapPercent(), 2) + "%");
        }
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(design.getAmount());
    r.getCell(3).setCellValue(design.getNoReworkAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(design.getNoReworkPercent(), 2) + "%");
    r.getCell(5).setCellValue(design.getNoScrapAmount());
    r.getCell(6).setCellValue(getStringValueWithScale(design.getNoScrapPercent(), 2) + "%");
    r.getCell(7).setCellValue(design.getNoReworkScrapAmount());
    r.getCell(8).setCellValue(getStringValueWithScale(design.getNoReworkScrapPercent(), 2) + "%");
  }

  private void genFinalCheckPercentDateReport(Workbook workbook, FinalCheckPercentDateTopData date,
      Map<String, Object> parameterMap, String shift) {
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue("终检一次通过率(" + (StringUtils.isEmpty(shift) ? "*" : shift) + "班)情况统计表");
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row detailRow = sheet.getRow(currentRowNum + 1);
    Row totalRow = sheet.getRow(5);
    for (int i = 0; i < date.getList().size(); i++) {
      FinalCheckPercentDateTotalData totalData = date.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(getStringValueOfDate(totalData.getOpeDT(), "yyyy/M/d"));
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getNoReworkAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getNoReworkPercent(), 2) + "%");
      r.getCell(5).setCellValue(totalData.getNoScrapAmount());
      r.getCell(6).setCellValue(getStringValueWithScale(totalData.getNoScrapPercent(), 2) + "%");
      r.getCell(7).setCellValue(totalData.getNoReworkScrapAmount());
      r.getCell(8).setCellValue(getStringValueWithScale(totalData.getNoReworkScrapPercent(), 2) + "%");
      List<FinalCheckPercentRecord> detailList = totalData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(detailRow, r);
        FinalCheckPercentRecord record = detailList.get(j);
        r.getCell(0).setCellValue(record.getCopeInspectorID());
        r.getCell(2).setCellValue(record.getAmount());
        r.getCell(3).setCellValue(record.getNoReworkAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(record.getNoReworkPercent(), 2) + "%");
        r.getCell(5).setCellValue(record.getNoScrapAmount());
        r.getCell(6).setCellValue(getStringValueWithScale(record.getNoScrapPercent(), 2) + "%");
        r.getCell(7).setCellValue(record.getNoReworkScrapAmount());
        r.getCell(8).setCellValue(getStringValueWithScale(record.getNoReworkScrapPercent(), 2) + "%");
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(date.getAmount());
    r.getCell(3).setCellValue(date.getNoReworkAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(date.getNoReworkPercent(), 2) + "%");
    r.getCell(5).setCellValue(date.getNoScrapAmount());
    r.getCell(6).setCellValue(getStringValueWithScale(date.getNoScrapPercent(), 2) + "%");
    r.getCell(7).setCellValue(date.getNoReworkScrapAmount());
    r.getCell(8).setCellValue(getStringValueWithScale(date.getNoReworkScrapPercent(), 2) + "%");
  }

  private void preCheckPercent(Workbook workbook, Map<String, Object> parameterMap) {
    PreCheckPercentData data = multiReportQcStatService.preCheckPercent(parameterMap);
    genPreCheckPercentDateReport(workbook, data.getDate(), parameterMap);
    genPreCheckPercentDesignReport(workbook, data.getDesign(), parameterMap);
    genPreCheckPercentStaffReport(workbook, data.getStaff(), parameterMap);
    genPreCheckPercentOutReport(workbook, data.getOut(), parameterMap);

    for (int i = 0; i < 4; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "日期");
    workbook.setSheetName(1, "轮型");
    workbook.setSheetName(2, "员工");
    workbook.setSheetName(3, "委外");
  }

  private void genPreCheckPercentOutReport(Workbook workbook, PreCheckPercentOutTopData out, Map<String, Object> parameterMap) {
    Sheet sheet = workbook.getSheetAt(3);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 3);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row dateRow = sheet.getRow(currentRowNum + 1);
    Row detailRow = sheet.getRow(currentRowNum + 2);
    Row totalRow = sheet.getRow(6);
    for (int i = 0; i < out.getList().size(); i++) {
      PreCheckPercentOutTotalData totalData = out.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(totalData.getOutCompany());
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getNoReworkAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getNoReworkPercent(), 2) + "%");
      r.getCell(5).setCellValue(totalData.getNoReworkScrapAmount());
      r.getCell(6).setCellValue(getStringValueWithScale(totalData.getNoReworkScrapPercent(), 2) + "%");
      r.getCell(7).setCellValue(totalData.getEr3456());
      r.getCell(8).setCellValue(getStringValueWithScale(totalData.getEr3456Percent(), 2) + "%");
      r.getCell(9).setCellValue(totalData.getEr3());
      r.getCell(10).setCellValue(getStringValueWithScale(totalData.getEr3Percent(), 2) + "%");
      r.getCell(11).setCellValue(totalData.getEr4());
      r.getCell(12).setCellValue(getStringValueWithScale(totalData.getEr4Percent(), 2) + "%");
      r.getCell(13).setCellValue(totalData.getEr5());
      r.getCell(14).setCellValue(getStringValueWithScale(totalData.getEr5Percent(), 2) + "%");
      r.getCell(15).setCellValue(totalData.getEr6());
      r.getCell(16).setCellValue(getStringValueWithScale(totalData.getEr6Percent(), 2) + "%");
      r.getCell(17).setCellValue(totalData.getOtherAmount());
      r.getCell(18).setCellValue(getStringValueWithScale(totalData.getOtherPercent(), 2) + "%");
      List<PreCheckPercentDateTotalData> detailList = totalData.getData();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(dateRow, r);
        PreCheckPercentDateTotalData detail = detailList.get(j);
        r.getCell(0).setCellValue(getStringValueOfDate(detail.getOpeDT(), "yyyy/M/d"));
        r.getCell(2).setCellValue(detail.getAmount());
        r.getCell(3).setCellValue(detail.getNoReworkAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(detail.getNoReworkPercent(), 2) + "%");
        r.getCell(5).setCellValue(detail.getNoReworkScrapAmount());
        r.getCell(6).setCellValue(getStringValueWithScale(detail.getNoReworkScrapPercent(), 2) + "%");
        r.getCell(7).setCellValue(detail.getEr3456());
        r.getCell(8).setCellValue(getStringValueWithScale(detail.getEr3456Percent(), 2) + "%");
        r.getCell(9).setCellValue(detail.getEr3());
        r.getCell(10).setCellValue(getStringValueWithScale(detail.getEr3Percent(), 2) + "%");
        r.getCell(11).setCellValue(detail.getEr4());
        r.getCell(12).setCellValue(getStringValueWithScale(detail.getEr4Percent(), 2) + "%");
        r.getCell(13).setCellValue(detail.getEr5());
        r.getCell(14).setCellValue(getStringValueWithScale(detail.getEr5Percent(), 2) + "%");
        r.getCell(15).setCellValue(detail.getEr6());
        r.getCell(16).setCellValue(getStringValueWithScale(detail.getEr6Percent(), 2) + "%");
        r.getCell(17).setCellValue(detail.getOtherAmount());
        r.getCell(18).setCellValue(getStringValueWithScale(detail.getOtherPercent(), 2) + "%");
        List<PreCheckPercentRecord> recordList = detail.getDetail();
        for (int k = 0; k < recordList.size(); k++) {
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(detailRow, r);
          PreCheckPercentRecord record = recordList.get(k);
          r.getCell(0).setCellValue(record.getCopeInspectorID());
          r.getCell(2).setCellValue(record.getAmount());
          r.getCell(3).setCellValue(record.getNoReworkAmount());
          r.getCell(4).setCellValue(getStringValueWithScale(record.getNoReworkPercent(), 2) + "%");
          r.getCell(5).setCellValue(record.getNoReworkScrapAmount());
          r.getCell(6).setCellValue(getStringValueWithScale(record.getNoReworkScrapPercent(), 2) + "%");
          r.getCell(7).setCellValue(record.getEr3456());
          r.getCell(8).setCellValue(getStringValueWithScale(record.getEr3456Percent(), 2) + "%");
          r.getCell(9).setCellValue(record.getEr3());
          r.getCell(10).setCellValue(getStringValueWithScale(record.getEr3Percent(), 2) + "%");
          r.getCell(11).setCellValue(record.getEr4());
          r.getCell(12).setCellValue(getStringValueWithScale(record.getEr4Percent(), 2) + "%");
          r.getCell(13).setCellValue(record.getEr5());
          r.getCell(14).setCellValue(getStringValueWithScale(record.getEr5Percent(), 2) + "%");
          r.getCell(15).setCellValue(record.getEr6());
          r.getCell(16).setCellValue(getStringValueWithScale(record.getEr6Percent(), 2) + "%");
          r.getCell(17).setCellValue(record.getOtherAmount());
          r.getCell(18).setCellValue(getStringValueWithScale(record.getOtherPercent(), 2) + "%");
        }
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(out.getAmount());
    r.getCell(3).setCellValue(out.getNoReworkAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(out.getNoReworkPercent(), 2) + "%");
    r.getCell(5).setCellValue(out.getNoReworkScrapAmount());
    r.getCell(6).setCellValue(getStringValueWithScale(out.getNoReworkScrapPercent(), 2) + "%");
    r.getCell(7).setCellValue(out.getEr3456());
    r.getCell(8).setCellValue(getStringValueWithScale(out.getEr3456Percent(), 2) + "%");
    r.getCell(9).setCellValue(out.getEr3());
    r.getCell(10).setCellValue(getStringValueWithScale(out.getEr3Percent(), 2) + "%");
    r.getCell(11).setCellValue(out.getEr4());
    r.getCell(12).setCellValue(getStringValueWithScale(out.getEr4Percent(), 2) + "%");
    r.getCell(13).setCellValue(out.getEr5());
    r.getCell(14).setCellValue(getStringValueWithScale(out.getEr5Percent(), 2) + "%");
    r.getCell(15).setCellValue(out.getEr6());
    r.getCell(16).setCellValue(getStringValueWithScale(out.getEr6Percent(), 2) + "%");
    r.getCell(17).setCellValue(out.getOtherAmount());
    r.getCell(18).setCellValue(getStringValueWithScale(out.getOtherPercent(), 2) + "%");
  }

  private void genPreCheckPercentStaffReport(Workbook workbook, PreCheckPercentStaffTopData staff,
      Map<String, Object> parameterMap) {
    Sheet sheet = workbook.getSheetAt(2);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 2);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row detailRow = sheet.getRow(currentRowNum + 1);
    Row totalRow = sheet.getRow(5);
    for (int i = 0; i < staff.getList().size(); i++) {
      PreCheckPercentStaffTotalData totalData = staff.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(totalData.getCopeInspectorID());
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getNoReworkAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getNoReworkPercent(), 2) + "%");
      r.getCell(5).setCellValue(totalData.getNoReworkScrapAmount());
      r.getCell(6).setCellValue(getStringValueWithScale(totalData.getNoReworkScrapPercent(), 2) + "%");
      r.getCell(7).setCellValue(totalData.getEr3456());
      r.getCell(8).setCellValue(getStringValueWithScale(totalData.getEr3456Percent(), 2) + "%");
      r.getCell(9).setCellValue(totalData.getEr3());
      r.getCell(10).setCellValue(getStringValueWithScale(totalData.getEr3Percent(), 2) + "%");
      r.getCell(11).setCellValue(totalData.getEr4());
      r.getCell(12).setCellValue(getStringValueWithScale(totalData.getEr4Percent(), 2) + "%");
      r.getCell(13).setCellValue(totalData.getEr5());
      r.getCell(14).setCellValue(getStringValueWithScale(totalData.getEr5Percent(), 2) + "%");
      r.getCell(15).setCellValue(totalData.getEr6());
      r.getCell(16).setCellValue(getStringValueWithScale(totalData.getEr6Percent(), 2) + "%");
      r.getCell(17).setCellValue(totalData.getOtherAmount());
      r.getCell(18).setCellValue(getStringValueWithScale(totalData.getOtherPercent(), 2) + "%");
      List<PreCheckPercentRecord> detailList = totalData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(detailRow, r);
        PreCheckPercentRecord record = detailList.get(j);
        r.getCell(0).setCellValue(getStringValueOfDate(record.getOpeDT(), "yyyy/M/d"));
        r.getCell(2).setCellValue(record.getAmount());
        r.getCell(3).setCellValue(record.getNoReworkAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(record.getNoReworkPercent(), 2) + "%");
        r.getCell(5).setCellValue(record.getNoReworkScrapAmount());
        r.getCell(6).setCellValue(getStringValueWithScale(record.getNoReworkScrapPercent(), 2) + "%");
        r.getCell(7).setCellValue(record.getEr3456());
        r.getCell(8).setCellValue(getStringValueWithScale(record.getEr3456Percent(), 2) + "%");
        r.getCell(9).setCellValue(record.getEr3());
        r.getCell(10).setCellValue(getStringValueWithScale(record.getEr3Percent(), 2) + "%");
        r.getCell(11).setCellValue(record.getEr4());
        r.getCell(12).setCellValue(getStringValueWithScale(record.getEr4Percent(), 2) + "%");
        r.getCell(13).setCellValue(record.getEr5());
        r.getCell(14).setCellValue(getStringValueWithScale(record.getEr5Percent(), 2) + "%");
        r.getCell(15).setCellValue(record.getEr6());
        r.getCell(16).setCellValue(getStringValueWithScale(record.getEr6Percent(), 2) + "%");
        r.getCell(17).setCellValue(record.getOtherAmount());
        r.getCell(18).setCellValue(getStringValueWithScale(record.getOtherPercent(), 2) + "%");
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(staff.getAmount());
    r.getCell(3).setCellValue(staff.getNoReworkAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(staff.getNoReworkPercent(), 2) + "%");
    r.getCell(5).setCellValue(staff.getNoReworkScrapAmount());
    r.getCell(6).setCellValue(getStringValueWithScale(staff.getNoReworkScrapPercent(), 2) + "%");
    r.getCell(7).setCellValue(staff.getEr3456());
    r.getCell(8).setCellValue(getStringValueWithScale(staff.getEr3456Percent(), 2) + "%");
    r.getCell(9).setCellValue(staff.getEr3());
    r.getCell(10).setCellValue(getStringValueWithScale(staff.getEr3Percent(), 2) + "%");
    r.getCell(11).setCellValue(staff.getEr4());
    r.getCell(12).setCellValue(getStringValueWithScale(staff.getEr4Percent(), 2) + "%");
    r.getCell(13).setCellValue(staff.getEr5());
    r.getCell(14).setCellValue(getStringValueWithScale(staff.getEr5Percent(), 2) + "%");
    r.getCell(15).setCellValue(staff.getEr6());
    r.getCell(16).setCellValue(getStringValueWithScale(staff.getEr6Percent(), 2) + "%");
    r.getCell(17).setCellValue(staff.getOtherAmount());
    r.getCell(18).setCellValue(getStringValueWithScale(staff.getOtherPercent(), 2) + "%");
  }

  private void genPreCheckPercentDesignReport(Workbook workbook, PreCheckPercentDesignTopData design,
      Map<String, Object> parameterMap) {
    Sheet sheet = workbook.getSheetAt(1);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 1);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row dateRow = sheet.getRow(currentRowNum + 1);
    Row detailRow = sheet.getRow(currentRowNum + 2);
    Row totalRow = sheet.getRow(6);
    for (int i = 0; i < design.getList().size(); i++) {
      PreCheckPercentDesignTotalData totalData = design.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(totalData.getDesign());
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getNoReworkAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getNoReworkPercent(), 2) + "%");
      r.getCell(5).setCellValue(totalData.getNoReworkScrapAmount());
      r.getCell(6).setCellValue(getStringValueWithScale(totalData.getNoReworkScrapPercent(), 2) + "%");
      r.getCell(7).setCellValue(totalData.getEr3456());
      r.getCell(8).setCellValue(getStringValueWithScale(totalData.getEr3456Percent(), 2) + "%");
      r.getCell(9).setCellValue(totalData.getEr3());
      r.getCell(10).setCellValue(getStringValueWithScale(totalData.getEr3Percent(), 2) + "%");
      r.getCell(11).setCellValue(totalData.getEr4());
      r.getCell(12).setCellValue(getStringValueWithScale(totalData.getEr4Percent(), 2) + "%");
      r.getCell(13).setCellValue(totalData.getEr5());
      r.getCell(14).setCellValue(getStringValueWithScale(totalData.getEr5Percent(), 2) + "%");
      r.getCell(15).setCellValue(totalData.getEr6());
      r.getCell(16).setCellValue(getStringValueWithScale(totalData.getEr6Percent(), 2) + "%");
      r.getCell(17).setCellValue(totalData.getOtherAmount());
      r.getCell(18).setCellValue(getStringValueWithScale(totalData.getOtherPercent(), 2) + "%");
      List<PreCheckPercentDateTotalData> detailList = totalData.getData();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(dateRow, r);
        PreCheckPercentDateTotalData detail = detailList.get(j);
        r.getCell(0).setCellValue(getStringValueOfDate(detail.getOpeDT(), "yyyy/M/d"));
        r.getCell(2).setCellValue(detail.getAmount());
        r.getCell(3).setCellValue(detail.getNoReworkAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(detail.getNoReworkPercent(), 2) + "%");
        r.getCell(5).setCellValue(detail.getNoReworkScrapAmount());
        r.getCell(6).setCellValue(getStringValueWithScale(detail.getNoReworkScrapPercent(), 2) + "%");
        r.getCell(7).setCellValue(detail.getEr3456());
        r.getCell(8).setCellValue(getStringValueWithScale(detail.getEr3456Percent(), 2) + "%");
        r.getCell(9).setCellValue(detail.getEr3());
        r.getCell(10).setCellValue(getStringValueWithScale(detail.getEr3Percent(), 2) + "%");
        r.getCell(11).setCellValue(detail.getEr4());
        r.getCell(12).setCellValue(getStringValueWithScale(detail.getEr4Percent(), 2) + "%");
        r.getCell(13).setCellValue(detail.getEr5());
        r.getCell(14).setCellValue(getStringValueWithScale(detail.getEr5Percent(), 2) + "%");
        r.getCell(15).setCellValue(detail.getEr6());
        r.getCell(16).setCellValue(getStringValueWithScale(detail.getEr6Percent(), 2) + "%");
        r.getCell(17).setCellValue(detail.getOtherAmount());
        r.getCell(18).setCellValue(getStringValueWithScale(detail.getOtherPercent(), 2) + "%");
        List<PreCheckPercentRecord> recordList = detail.getDetail();
        for (int k = 0; k < recordList.size(); k++) {
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(detailRow, r);
          PreCheckPercentRecord record = recordList.get(k);
          r.getCell(0).setCellValue(record.getCopeInspectorID());
          r.getCell(2).setCellValue(record.getAmount());
          r.getCell(3).setCellValue(record.getNoReworkAmount());
          r.getCell(4).setCellValue(getStringValueWithScale(record.getNoReworkPercent(), 2) + "%");
          r.getCell(5).setCellValue(record.getNoReworkScrapAmount());
          r.getCell(6).setCellValue(getStringValueWithScale(record.getNoReworkScrapPercent(), 2) + "%");
          r.getCell(7).setCellValue(record.getEr3456());
          r.getCell(8).setCellValue(getStringValueWithScale(record.getEr3456Percent(), 2) + "%");
          r.getCell(9).setCellValue(record.getEr3());
          r.getCell(10).setCellValue(getStringValueWithScale(record.getEr3Percent(), 2) + "%");
          r.getCell(11).setCellValue(record.getEr4());
          r.getCell(12).setCellValue(getStringValueWithScale(record.getEr4Percent(), 2) + "%");
          r.getCell(13).setCellValue(record.getEr5());
          r.getCell(14).setCellValue(getStringValueWithScale(record.getEr5Percent(), 2) + "%");
          r.getCell(15).setCellValue(record.getEr6());
          r.getCell(16).setCellValue(getStringValueWithScale(record.getEr6Percent(), 2) + "%");
          r.getCell(17).setCellValue(record.getOtherAmount());
          r.getCell(18).setCellValue(getStringValueWithScale(record.getOtherPercent(), 2) + "%");
        }
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(design.getAmount());
    r.getCell(3).setCellValue(design.getNoReworkAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(design.getNoReworkPercent(), 2) + "%");
    r.getCell(5).setCellValue(design.getNoReworkScrapAmount());
    r.getCell(6).setCellValue(getStringValueWithScale(design.getNoReworkScrapPercent(), 2) + "%");
    r.getCell(7).setCellValue(design.getEr3456());
    r.getCell(8).setCellValue(getStringValueWithScale(design.getEr3456Percent(), 2) + "%");
    r.getCell(9).setCellValue(design.getEr3());
    r.getCell(10).setCellValue(getStringValueWithScale(design.getEr3Percent(), 2) + "%");
    r.getCell(11).setCellValue(design.getEr4());
    r.getCell(12).setCellValue(getStringValueWithScale(design.getEr4Percent(), 2) + "%");
    r.getCell(13).setCellValue(design.getEr5());
    r.getCell(14).setCellValue(getStringValueWithScale(design.getEr5Percent(), 2) + "%");
    r.getCell(15).setCellValue(design.getEr6());
    r.getCell(16).setCellValue(getStringValueWithScale(design.getEr6Percent(), 2) + "%");
    r.getCell(17).setCellValue(design.getOtherAmount());
    r.getCell(18).setCellValue(getStringValueWithScale(design.getOtherPercent(), 2) + "%");
  }

  private void genPreCheckPercentDateReport(Workbook workbook, PreCheckPercentDateTopData date,
      Map<String, Object> parameterMap) {
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    removeMergedRegion(sxssfSheet, 3);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    Row detailRow = sheet.getRow(currentRowNum + 1);
    Row totalRow = sheet.getRow(5);
    for (int i = 0; i < date.getList().size(); i++) {
      PreCheckPercentDateTotalData totalData = date.getList().get(i);
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(getStringValueOfDate(totalData.getOpeDT(), "yyyy/M/d"));
      r.getCell(2).setCellValue(totalData.getAmount());
      r.getCell(3).setCellValue(totalData.getNoReworkAmount());
      r.getCell(4).setCellValue(getStringValueWithScale(totalData.getNoReworkPercent(), 2) + "%");
      r.getCell(5).setCellValue(totalData.getNoReworkScrapAmount());
      r.getCell(6).setCellValue(getStringValueWithScale(totalData.getNoReworkScrapPercent(), 2) + "%");
      r.getCell(7).setCellValue(totalData.getEr3456());
      r.getCell(8).setCellValue(getStringValueWithScale(totalData.getEr3456Percent(), 2) + "%");
      r.getCell(9).setCellValue(totalData.getEr3());
      r.getCell(10).setCellValue(getStringValueWithScale(totalData.getEr3Percent(), 2) + "%");
      r.getCell(11).setCellValue(totalData.getEr4());
      r.getCell(12).setCellValue(getStringValueWithScale(totalData.getEr4Percent(), 2) + "%");
      r.getCell(13).setCellValue(totalData.getEr5());
      r.getCell(14).setCellValue(getStringValueWithScale(totalData.getEr5Percent(), 2) + "%");
      r.getCell(15).setCellValue(totalData.getEr6());
      r.getCell(16).setCellValue(getStringValueWithScale(totalData.getEr6Percent(), 2) + "%");
      r.getCell(17).setCellValue(totalData.getOtherAmount());
      r.getCell(18).setCellValue(getStringValueWithScale(totalData.getOtherPercent(), 2) + "%");
      List<PreCheckPercentRecord> detailList = totalData.getDetail();
      for (int j = 0; j < detailList.size(); j++) {
        r = sxssfSheet.createRow(currentRowNum++);
        setStyle(detailRow, r);
        PreCheckPercentRecord record = detailList.get(j);
        r.getCell(0).setCellValue(record.getCopeInspectorID());
        r.getCell(2).setCellValue(record.getAmount());
        r.getCell(3).setCellValue(record.getNoReworkAmount());
        r.getCell(4).setCellValue(getStringValueWithScale(record.getNoReworkPercent(), 2) + "%");
        r.getCell(5).setCellValue(record.getNoReworkScrapAmount());
        r.getCell(6).setCellValue(getStringValueWithScale(record.getNoReworkScrapPercent(), 2) + "%");
        r.getCell(7).setCellValue(record.getEr3456());
        r.getCell(8).setCellValue(getStringValueWithScale(record.getEr3456Percent(), 2) + "%");
        r.getCell(9).setCellValue(record.getEr3());
        r.getCell(10).setCellValue(getStringValueWithScale(record.getEr3Percent(), 2) + "%");
        r.getCell(11).setCellValue(record.getEr4());
        r.getCell(12).setCellValue(getStringValueWithScale(record.getEr4Percent(), 2) + "%");
        r.getCell(13).setCellValue(record.getEr5());
        r.getCell(14).setCellValue(getStringValueWithScale(record.getEr5Percent(), 2) + "%");
        r.getCell(15).setCellValue(record.getEr6());
        r.getCell(16).setCellValue(getStringValueWithScale(record.getEr6Percent(), 2) + "%");
        r.getCell(17).setCellValue(record.getOtherAmount());
        r.getCell(18).setCellValue(getStringValueWithScale(record.getOtherPercent(), 2) + "%");
      }
    }
    Row r = sxssfSheet.createRow(currentRowNum++);
    setStyle(totalRow, r);
    r.getCell(0).setCellValue("合计");
    r.getCell(2).setCellValue(date.getAmount());
    r.getCell(3).setCellValue(date.getNoReworkAmount());
    r.getCell(4).setCellValue(getStringValueWithScale(date.getNoReworkPercent(), 2) + "%");
    r.getCell(5).setCellValue(date.getNoReworkScrapAmount());
    r.getCell(6).setCellValue(getStringValueWithScale(date.getNoReworkScrapPercent(), 2) + "%");
    r.getCell(7).setCellValue(date.getEr3456());
    r.getCell(8).setCellValue(getStringValueWithScale(date.getEr3456Percent(), 2) + "%");
    r.getCell(9).setCellValue(date.getEr3());
    r.getCell(10).setCellValue(getStringValueWithScale(date.getEr3Percent(), 2) + "%");
    r.getCell(11).setCellValue(date.getEr4());
    r.getCell(12).setCellValue(getStringValueWithScale(date.getEr4Percent(), 2) + "%");
    r.getCell(13).setCellValue(date.getEr5());
    r.getCell(14).setCellValue(getStringValueWithScale(date.getEr5Percent(), 2) + "%");
    r.getCell(15).setCellValue(date.getEr6());
    r.getCell(16).setCellValue(getStringValueWithScale(date.getEr6Percent(), 2) + "%");
    r.getCell(17).setCellValue(date.getOtherAmount());
    r.getCell(18).setCellValue(getStringValueWithScale(date.getOtherPercent(), 2) + "%");
  }

  private void smallTape(Workbook workbook, Map<String, Object> parameterMap) {
    List<SmallTapeData> dataList = multiReportQcStatService.smallTape(parameterMap);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(getStringValueOfDate(parseDate(startDateStr), "yyyy/M"));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    setPrintSetupInfo(sheet, sxssfSheet);

    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < dataList.size(); i++) {
      SmallTapeData data = dataList.get(i);
      Row r = sxssfSheet.createRow(currentRowNum + i);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(i + 1);
      r.getCell(1).setCellValue(data.getWheelSerial());
      r.getCell(2).setCellValue(data.getDesign());
      r.getCell(3).setCellValue(data.getWheelW());
      r.getCell(4).setCellValue(getStringValueWithScale(data.getTapeSize(), 1));
      r.getCell(5).setCellValue(data.getScrapCode());
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "小带尺");
  }

  private void shot(Workbook workbook, Map<String, Object> parameterMap) {
    List<ShotData> dataList = multiReportQcStatService.shot(parameterMap);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 16);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 16);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseTime(startDateStr), "yyyy/M/d HH:mm"), " to ",
        getStringValueOfDate(parseTime(endDateStr), "yyyy/M/d HH:mm")));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);

    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < dataList.size(); i++) {
      ShotData data = dataList.get(i);
      Row r = sxssfSheet.createRow(currentRowNum + i);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(getStringValueOfDate(data.getLastPre(), "yyyy-MM-dd HH:mm"));
      r.getCell(1).setCellValue(data.getWheelSerial());
      r.getCell(2).setCellValue(data.getScrapCode());
      r.getCell(3).setCellValue(data.getShippedNo());
      r.getCell(4).setCellValue(data.getFinished() == 1 ? "是" : "否");
      r.getCell(5).setCellValue(getStringValueOfDate(data.getLastBalance(), "yyyy-MM-dd HH:mm"));
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "预检");
  }

  private Workbook getWorkbook(String filename) {
    Workbook workbook = null;
    try {
      ClassPathResource resource = new ClassPathResource("static/report/multi-report/qc-stat/" + filename);
      InputStream inputStream = resource.getInputStream();
      workbook = new XSSFWorkbook(inputStream);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return workbook;
  }

  private boolean isChecked(Integer value) {
    if (value != null && value == 1) {
      return true;
    }
    return false;
  }

}
