package tech.hciot.dwis.business.application.report;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.CommonUtil.getStringValueWithScale;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.getClonedSheet;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.setStyle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
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
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine.*;

@Service
@Slf4j
public class MultiReportMachineStatExportService {

  public static final String MACHINE_STAT_STAFF_DETAIL = "5.1-machine-stat-staff-detail";
  public static final String MACHINE_STAT_STAFF_QUANTITY = "5.2-machine-stat-staff-quantity";
  public static final String MACHINE_STAT_MACHINE_QUANTITY = "5.3-machine-stat-machine-quantity";
  public static final String MACHINE_STAT_MACHINE_REWORK = "5.4-machine-stat-rework";
  public static final String MACHINE_STAT_MACHINE_STAFF = "5.5-machine-stat-machine-staff";
  public static final String MACHINE_STAT_MACHINE_TOOL = "5.6-machine-stat-machine-tool";
  public static final String MACHINE_STAT_JMACHINE8 = "5.7-machine-stat-jmachine8";
  public static final String MACHINE_STAT_MACHINE_REWORKSTAT = "5.8-machine-stat-reworkstat";
  public static final String MACHINE_STAT_ROUNDNESS_RECHECK = "5.9-machine-roundness-recheck-detail";
  private static final String SUFFIX = ".xlsx";

  @Autowired
  private MultiReportMachineStatService multiReportMachineStatService;

  public void export(String type, Map<String, Object> parameterMap, HttpServletResponse response) {
    Workbook workbook = null;
    String filename = "";
    switch (type) {
      case MACHINE_STAT_STAFF_DETAIL:
        filename = MACHINE_STAT_STAFF_DETAIL + SUFFIX;
        workbook = getWorkbook(filename);
        staffDetailExport(workbook, parameterMap);
        break;

      case MACHINE_STAT_STAFF_QUANTITY:
        filename = MACHINE_STAT_STAFF_QUANTITY + SUFFIX;
        workbook = getWorkbook(filename);
        staffQuantityExport(workbook, parameterMap);
        break;

      case MACHINE_STAT_MACHINE_QUANTITY:
        filename = MACHINE_STAT_MACHINE_QUANTITY + SUFFIX;
        workbook = getWorkbook(filename);
        machineQuantityExport(workbook, parameterMap);
        break;

      case MACHINE_STAT_MACHINE_REWORK:
        filename = MACHINE_STAT_MACHINE_REWORK + SUFFIX;
        workbook = getWorkbook(filename);
        machineReworkExport(workbook, parameterMap);
        break;

      case MACHINE_STAT_MACHINE_STAFF:
        filename = MACHINE_STAT_MACHINE_STAFF + SUFFIX;
        workbook = getWorkbook(filename);
        machineStaffExport(workbook, parameterMap);
        break;

      case MACHINE_STAT_MACHINE_TOOL:
        filename = MACHINE_STAT_MACHINE_TOOL + SUFFIX;
        workbook = getWorkbook(filename);
        machineToolExport(workbook, parameterMap);
        break;

      case MACHINE_STAT_JMACHINE8:
        filename = MACHINE_STAT_JMACHINE8 + SUFFIX;
        workbook = getWorkbook(filename);
        jMachine8Export(workbook, parameterMap);
        break;

      case MACHINE_STAT_MACHINE_REWORKSTAT:
        filename = MACHINE_STAT_MACHINE_REWORKSTAT + SUFFIX;
        workbook = getWorkbook(filename);
        machineReworkStatExport(workbook, parameterMap);
        break;

      case MACHINE_STAT_ROUNDNESS_RECHECK:
        filename = MACHINE_STAT_ROUNDNESS_RECHECK + SUFFIX;
        workbook = getWorkbook(filename);
        machineRoundnessRecheckExport(workbook, parameterMap);
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

  public void staffDetailExport(Workbook workbook, Map<String, Object> parameterMap) {
    List<StaffDetailData> staffDetailDataList = multiReportMachineStatService.staffDetail(parameterMap);
    Sheet sheet = workbook.getSheetAt(0);
    Row row = sheet.getRow(0);
    row.getCell(0).setCellValue(parameterMap.get("staffId") + "员工加工明细统计表");
    row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));

    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < staffDetailDataList.size(); i++) {
      StaffDetailData staffDetailData = staffDetailDataList.get(i);
      Row r = sxssfSheet.createRow(currentRowNum + i);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(i + 1);
      r.getCell(1).setCellValue(staffDetailData.getMachineNo());
      r.getCell(2).setCellValue(staffDetailData.getWheelSerial());
      r.getCell(3).setCellValue(getStringValueOfDate(staffDetailData.getOpeDT(), "yyyy/M/d HH:mm"));
      r.getCell(4).setCellValue(staffDetailData.getReworkHoldCode());
      r.getCell(5).setCellValue(isChecked(staffDetailData.getIsCheck()) ? "√" : "");
      r.getCell(6).setCellValue(isChecked(staffDetailData.getIsInspecCheck()) ? "√" : "");
      r.getCell(7).setCellValue(isChecked(staffDetailData.getIsMeasureCheck()) ? "√" : "");
      r.getCell(8).setCellValue(getStringValue(staffDetailData.getS2()));
      r.getCell(9).setCellValue(getStringValueWithScale(staffDetailData.getS1(), 1));
      r.getCell(10).setCellValue(getStringValueWithScale(staffDetailData.getData1(), 2));
      r.getCell(11).setCellValue(getStringValueWithScale(staffDetailData.getData2(), 2));
      r.getCell(12).setCellValue(staffDetailData.getData3());
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "员工加工明细统计表");
  }

  private boolean isChecked(Integer value) {
    if (value != null && value == 1) {
      return true;
    }
    return false;
  }

  private void machineReworkExport(Workbook workbook, Map<String, Object> parameterMap) {
    List<ReworkData> reworkDataList = multiReportMachineStatService.machineRework(parameterMap);
    Sheet sheet = workbook.getSheetAt(0);
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    int currentRowNum = 2;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < reworkDataList.size(); i++) {
      ReworkData reworkData = reworkDataList.get(i);
      Row r = sxssfSheet.createRow(currentRowNum + i);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(reworkData.getWheelSerial());
      r.getCell(1).setCellValue(reworkData.getReworkCode());
      r.getCell(2).setCellValue(reworkData.getDesign());
      r.getCell(3).setCellValue(getStringValue(reworkData.getJMachineNo()));
      r.getCell(4).setCellValue(getStringValueOfDate(reworkData.getJMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(5).setCellValue(reworkData.getJMachineOperator());
      r.getCell(6).setCellValue(getStringValue(reworkData.getTMachineNo()));
      r.getCell(7).setCellValue(getStringValueOfDate(reworkData.getTMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(8).setCellValue(reworkData.getTMachineOperator());
      r.getCell(9).setCellValue(getStringValue(reworkData.getKMachineNo()));
      r.getCell(10).setCellValue(getStringValueOfDate(reworkData.getKMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(11).setCellValue(reworkData.getKMachineOperator());
      r.getCell(12).setCellValue(getStringValue(reworkData.getWMachineNo()));
      r.getCell(13).setCellValue(getStringValueOfDate(reworkData.getWMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(14).setCellValue(reworkData.getWMachineOperator());
      r.getCell(15).setCellValue(getStringValue(reworkData.getFirstJMachineNo()));
      r.getCell(16).setCellValue(getStringValueOfDate(reworkData.getFirstJMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(17).setCellValue(reworkData.getFirstJMachineOperator());
      r.getCell(18).setCellValue(getStringValue(reworkData.getFirstTMachineNo()));
      r.getCell(19).setCellValue(getStringValueOfDate(reworkData.getFirstTMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(20).setCellValue(reworkData.getFirstTMachineOperator());
      r.getCell(21).setCellValue(getStringValue(reworkData.getFirstKMachineNo()));
      r.getCell(22).setCellValue(getStringValueOfDate(reworkData.getFirstKMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(23).setCellValue(reworkData.getFirstKMachineOperator());
      r.getCell(24).setCellValue(getStringValue(reworkData.getFirstWMachineNo()));
      r.getCell(25).setCellValue(getStringValueOfDate(reworkData.getFirstWMachineOpeDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(26).setCellValue(reworkData.getFirstWMachineOperator());
      r.getCell(27).setCellValue(getStringValueOfDate(reworkData.getFinalCheckDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(28).setCellValue(reworkData.getDragInspectorId());
      r.getCell(29).setCellValue(getStringValueOfDate(reworkData.getMagCheckDT(), "yyyy-MM-dd HH:mm:ss"));
      r.getCell(30).setCellValue(reworkData.getMagDragInspectorId());
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "机加返修车轮明细");
  }

  private void machineQuantityExport(Workbook workbook, Map<String, Object> parameterMap) {
    StaffQuantityData staffQuantityData = multiReportMachineStatService.machineQuantity(parameterMap);
    QuantityData<JMachineQuantityTotal, JMachineQuantityStat> jMachine = staffQuantityData.getJMachine();
    genJMachineQuantityReport(jMachine, workbook, parameterMap, true);
    QuantityData<TMachineQuantityTotal, TMachineQuantityStat> tMachine = staffQuantityData.getTMachine();
    genTMachineQuantityReport(tMachine, workbook, parameterMap, true);
    QuantityData<KMachineQuantityTotal, KMachineQuantityStat> kMachine = staffQuantityData.getKMachine();
    genKMachineQuantityReport(kMachine, workbook, parameterMap, true);
    QuantityData<WMachineQuantityTotal, WMachineQuantityStat> wMachine = staffQuantityData.getWMachine();
    genWMachineQuantityReport(wMachine, workbook, parameterMap, true);
    QuantityData<QMachineQuantityTotal, QMachineQuantityStat> qMachine = staffQuantityData.getQMachine();
    genQMachineQuantityReport(qMachine, workbook, parameterMap, true);
    resetSheetName(workbook);
  }

  private void resetSheetName(Workbook workbook) {
    for (int i = 0; i < 5; i++) {
      workbook.removeSheetAt(0);
    }
    workbook.setSheetName(0, "基面");
    workbook.setSheetName(1, "踏面");
    workbook.setSheetName(2, "镗孔");
    workbook.setSheetName(3, "外辐板");
    workbook.setSheetName(4, "去重");
  }

  private void staffQuantityExport(Workbook workbook, Map<String, Object> parameterMap) {
    StaffQuantityData staffQuantityData = multiReportMachineStatService.staffQuantity(parameterMap);
    QuantityData<JMachineQuantityTotal, JMachineQuantityStat> jMachine = staffQuantityData.getJMachine();
    genJMachineQuantityReport(jMachine, workbook, parameterMap);
    QuantityData<TMachineQuantityTotal, TMachineQuantityStat> tMachine = staffQuantityData.getTMachine();
    genTMachineQuantityReport(tMachine, workbook, parameterMap);
    QuantityData<KMachineQuantityTotal, KMachineQuantityStat> kMachine = staffQuantityData.getKMachine();
    genKMachineQuantityReport(kMachine, workbook, parameterMap);
    QuantityData<WMachineQuantityTotal, WMachineQuantityStat> wMachine = staffQuantityData.getWMachine();
    genWMachineQuantityReport(wMachine, workbook, parameterMap);
    QuantityData<QMachineQuantityTotal, QMachineQuantityStat> qMachine = staffQuantityData.getQMachine();
    genQMachineQuantityReport(qMachine, workbook, parameterMap);
    resetSheetName(workbook);
  }

  private void genQMachineQuantityReport(QuantityData<QMachineQuantityTotal, QMachineQuantityStat> qMachine,
      Workbook workbook, Map<String, Object> parameterMap) {
    genQMachineQuantityReport(qMachine, workbook, parameterMap, false);
  }

  private void genQMachineQuantityReport(QuantityData<QMachineQuantityTotal, QMachineQuantityStat> qMachine,
      Workbook workbook, Map<String, Object> parameterMap, boolean isMachine) {
    Sheet sheet = workbook.getSheetAt(4);
    genDateRow(sheet, parameterMap);
    Sheet sxssfSheet = getClonedSheet(sheet, 4);

    if (!qMachine.getRecord().isEmpty()) {
      int currentRowNum = 3;
      Row tempRow = sheet.getRow(currentRowNum);
      Row totalRow = sheet.getRow(currentRowNum + 1);
      int totalRecord = 0;
      for (QMachineQuantityStat qMachineQuantityStat : qMachine.getRecord()) {
        totalRecord = totalRecord + qMachineQuantityStat.getDetail().size() + 1;
      }
      List<QMachineQuantityStat> recordList = qMachine.getRecord();
      for (int i = 0; i < recordList.size(); i++) {
        QMachineQuantityStat qMachineQuantityStat = recordList.get(i);
        Row r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue(isMachine ? getStringValue(qMachineQuantityStat.getMachineNo()) :
            qMachineQuantityStat.getOperator());
        r.getCell(4).setCellValue(getStringValue(qMachineQuantityStat.getQuantity()));
        r.getCell(5).setCellValue(getStringValue(qMachineQuantityStat.getQ1()));
        r.getCell(6).setCellValue(getStringValue(qMachineQuantityStat.getQ2()));
        List<QMachineQuantity> detailList = qMachineQuantityStat.getDetail();
        for (int j = 0; j < detailList.size(); j++) {
          QMachineQuantity qMachineQuantity = detailList.get(j);
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(0).setCellValue(getStringValueOfDate(qMachineQuantity.getOpeDT(), "yyyy/M/d"));
          r.getCell(2)
              .setCellValue(isMachine ? qMachineQuantity.getOperator() : getStringValue(qMachineQuantity.getMachineNo()));
          r.getCell(3).setCellValue(qMachineQuantity.getDesign());
          r.getCell(4).setCellValue(getStringValue(qMachineQuantity.getQuantity()));
          r.getCell(5).setCellValue(getStringValue(qMachineQuantity.getQ1()));
          r.getCell(6).setCellValue(getStringValue(qMachineQuantity.getQ2()));
        }
      }
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(totalRow, r);
      QMachineQuantityTotal qMachineQuantityTotal = qMachine.getTotal();
      r.getCell(3).setCellValue("合计");
      r.getCell(4).setCellValue(getStringValue(qMachineQuantityTotal.getQuantity()));
      r.getCell(5).setCellValue(getStringValue(qMachineQuantityTotal.getQ1()));
      r.getCell(6).setCellValue(getStringValue(qMachineQuantityTotal.getQ2()));
    }
  }

  private void genWMachineQuantityReport(QuantityData<WMachineQuantityTotal, WMachineQuantityStat> wMachine,
      Workbook workbook, Map<String, Object> parameterMap) {
    genWMachineQuantityReport(wMachine, workbook, parameterMap, false);
  }

  private void genWMachineQuantityReport(QuantityData<WMachineQuantityTotal, WMachineQuantityStat> wMachine,
      Workbook workbook, Map<String, Object> parameterMap, boolean isMachine) {
    Sheet sheet = workbook.getSheetAt(3);
    genDateRow(sheet, parameterMap);
    Sheet sxssfSheet = getClonedSheet(sheet, 3);

    if (!wMachine.getRecord().isEmpty()) {
      int currentRowNum = 3;
      Row tempRow = sheet.getRow(currentRowNum);
      Row totalRow = sheet.getRow(currentRowNum + 1);
      int totalRecord = 0;
      for (WMachineQuantityStat wMachineQuantityStat : wMachine.getRecord()) {
        totalRecord = totalRecord + wMachineQuantityStat.getDetail().size() + 1;
      }
      List<WMachineQuantityStat> recordList = wMachine.getRecord();
      for (int i = 0; i < recordList.size(); i++) {
        WMachineQuantityStat wMachineQuantityStat = recordList.get(i);
        Row r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue(isMachine ? getStringValue(wMachineQuantityStat.getMachineNo()) :
            wMachineQuantityStat.getOperator());
        r.getCell(4).setCellValue(getStringValue(wMachineQuantityStat.getQuantity()));
        r.getCell(5).setCellValue(getStringValue(wMachineQuantityStat.getTag70()));
        r.getCell(6).setCellValue(getStringValue(wMachineQuantityStat.getTag701()));
        r.getCell(7).setCellValue(getStringValue(wMachineQuantityStat.getTag48()));
        r.getCell(8).setCellValue(getStringValue(wMachineQuantityStat.getTag47()));
        r.getCell(9).setCellValue(getStringValue(wMachineQuantityStat.getReworkQuantity()));
        List<WMachineQuantity> detailList = wMachineQuantityStat.getDetail();
        for (int j = 0; j < detailList.size(); j++) {
          WMachineQuantity wMachineQuantity = detailList.get(j);
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(0).setCellValue(getStringValueOfDate(wMachineQuantity.getOpeDT(), "yyyy/M/d"));
          r.getCell(2)
              .setCellValue(isMachine ? wMachineQuantity.getOperator() : getStringValue(wMachineQuantity.getMachineNo()));
          r.getCell(3).setCellValue(wMachineQuantity.getDesign());
          r.getCell(4).setCellValue(getStringValue(wMachineQuantity.getQuantity()));
          r.getCell(5).setCellValue(getStringValue(wMachineQuantity.getTag70()));
          r.getCell(6).setCellValue(getStringValue(wMachineQuantity.getTag701()));
          r.getCell(7).setCellValue(getStringValue(wMachineQuantity.getTag48()));
          r.getCell(8).setCellValue(getStringValue(wMachineQuantity.getTag47()));
          r.getCell(9).setCellValue(getStringValue(wMachineQuantity.getReworkQuantity()));
        }
      }
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(totalRow, r);
      WMachineQuantityTotal wMachineQuantityTotal = wMachine.getTotal();
      r.getCell(3).setCellValue("合计");
      r.getCell(4).setCellValue(getStringValue(wMachineQuantityTotal.getQuantity()));
      r.getCell(5).setCellValue(getStringValue(wMachineQuantityTotal.getTag70()));
      r.getCell(6).setCellValue(getStringValue(wMachineQuantityTotal.getTag701()));
      r.getCell(7).setCellValue(getStringValue(wMachineQuantityTotal.getTag48()));
      r.getCell(8).setCellValue(getStringValue(wMachineQuantityTotal.getTag47()));
      r.getCell(9).setCellValue(getStringValue(wMachineQuantityTotal.getReworkQuantity()));
    }
  }

  private void genKMachineQuantityReport(QuantityData<KMachineQuantityTotal, KMachineQuantityStat> kMachine,
      Workbook workbook, Map<String, Object> parameterMap) {
    genKMachineQuantityReport(kMachine, workbook, parameterMap, false);
  }

  private void genKMachineQuantityReport(QuantityData<KMachineQuantityTotal, KMachineQuantityStat> kMachine,
      Workbook workbook, Map<String, Object> parameterMap, boolean isMachine) {
    Sheet sheet = workbook.getSheetAt(2);
    genDateRow(sheet, parameterMap);
    Sheet sxssfSheet = getClonedSheet(sheet, 2);

    if (!kMachine.getRecord().isEmpty()) {
      int currentRowNum = 3;
      Row tempRow = sheet.getRow(currentRowNum);
      Row totalRow = sheet.getRow(currentRowNum + 1);
      int totalRecord = 0;
      for (KMachineQuantityStat kMachineQuantityStat : kMachine.getRecord()) {
        totalRecord = totalRecord + kMachineQuantityStat.getDetail().size() + 1;
      }
      List<KMachineQuantityStat> recordList = kMachine.getRecord();
      for (int i = 0; i < recordList.size(); i++) {
        KMachineQuantityStat kMachineQuantityStat = recordList.get(i);
        Row r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1).setCellValue(isMachine ? getStringValue(kMachineQuantityStat.getMachineNo()) :
            kMachineQuantityStat.getOperator());
        r.getCell(5).setCellValue(getStringValue(kMachineQuantityStat.getQuantity()));
        r.getCell(6).setCellValue(getStringValue(kMachineQuantityStat.getTag40()));
        r.getCell(7).setCellValue(getStringValue(kMachineQuantityStat.getTag43()));
        r.getCell(8).setCellValue(getStringValue(kMachineQuantityStat.getTag44()));
        r.getCell(9).setCellValue(getStringValue(kMachineQuantityStat.getTag46()));
        r.getCell(10).setCellValue(getStringValue(kMachineQuantityStat.getTag30()));
        r.getCell(11).setCellValue(getStringValue(kMachineQuantityStat.getReworkQuantity()));
        List<KMachineQuantity> detailList = kMachineQuantityStat.getDetail();
        for (int j = 0; j < detailList.size(); j++) {
          KMachineQuantity kMachineQuantity = detailList.get(j);
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(0).setCellValue(getStringValueOfDate(kMachineQuantity.getOpeDT(), "yyyy/M/d"));
          r.getCell(2)
              .setCellValue(isMachine ? kMachineQuantity.getOperator() : getStringValue(kMachineQuantity.getMachineNo()));
          r.getCell(3).setCellValue(kMachineQuantity.getLocation());
          r.getCell(4).setCellValue(kMachineQuantity.getDesign());
          r.getCell(5).setCellValue(getStringValue(kMachineQuantity.getQuantity()));
          r.getCell(6).setCellValue(getStringValue(kMachineQuantity.getTag40()));
          r.getCell(7).setCellValue(getStringValue(kMachineQuantity.getTag43()));
          r.getCell(8).setCellValue(getStringValue(kMachineQuantity.getTag44()));
          r.getCell(9).setCellValue(getStringValue(kMachineQuantity.getTag46()));
          r.getCell(10).setCellValue(getStringValue(kMachineQuantity.getTag30()));
          r.getCell(11).setCellValue(getStringValue(kMachineQuantity.getReworkQuantity()));
        }
      }
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(totalRow, r);
      KMachineQuantityTotal kMachineQuantityTotal = kMachine.getTotal();
      r.getCell(4).setCellValue("合计");
      r.getCell(5).setCellValue(getStringValue(kMachineQuantityTotal.getQuantity()));
      r.getCell(6).setCellValue(getStringValue(kMachineQuantityTotal.getTag40()));
      r.getCell(7).setCellValue(getStringValue(kMachineQuantityTotal.getTag43()));
      r.getCell(8).setCellValue(getStringValue(kMachineQuantityTotal.getTag44()));
      r.getCell(9).setCellValue(getStringValue(kMachineQuantityTotal.getTag46()));
      r.getCell(10).setCellValue(getStringValue(kMachineQuantityTotal.getTag30()));
      r.getCell(11).setCellValue(getStringValue(kMachineQuantityTotal.getReworkQuantity()));
    }
  }

  private void genTMachineQuantityReport(QuantityData<TMachineQuantityTotal, TMachineQuantityStat> tMachine,
      Workbook workbook, Map<String, Object> parameterMap) {
    genTMachineQuantityReport(tMachine, workbook, parameterMap, false);
  }

  private void genTMachineQuantityReport(QuantityData<TMachineQuantityTotal, TMachineQuantityStat> tMachine,
      Workbook workbook, Map<String, Object> parameterMap, boolean isMachine) {
    Sheet sheet = workbook.getSheetAt(1);
    genDateRow(sheet, parameterMap);
    Sheet sxssfSheet = getClonedSheet(sheet, 1);

    if (!tMachine.getRecord().isEmpty()) {
      int currentRowNum = 3;
      Row tempRow = sheet.getRow(currentRowNum);
      Row totalRow = sheet.getRow(currentRowNum + 1);
      int totalRecord = 0;
      for (TMachineQuantityStat tMachineQuantityStat : tMachine.getRecord()) {
        totalRecord = totalRecord + tMachineQuantityStat.getDetail().size() + 1;
      }
      List<TMachineQuantityStat> recordList = tMachine.getRecord();
      for (int i = 0; i < recordList.size(); i++) {
        TMachineQuantityStat tMachineQuantityStat = recordList.get(i);
        Row r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1)
            .setCellValue(isMachine ? getStringValue(tMachineQuantityStat.getMachineNo()) : tMachineQuantityStat.getOperator());
        r.getCell(4).setCellValue(getStringValue(tMachineQuantityStat.getQuantity()));
        r.getCell(5).setCellValue(getStringValue(tMachineQuantityStat.getTag138()));
        r.getCell(6).setCellValue(getStringValue(tMachineQuantityStat.getTag8()));
        r.getCell(7).setCellValue(getStringValue(tMachineQuantityStat.getTag51()));
        r.getCell(8).setCellValue(getStringValue(tMachineQuantityStat.getTag52()));
        r.getCell(9).setCellValue(getStringValue(tMachineQuantityStat.getTag53()));
        r.getCell(10).setCellValue(getStringValue(tMachineQuantityStat.getTag54()));
        r.getCell(11).setCellValue(getStringValue(tMachineQuantityStat.getTag55()));
        r.getCell(12).setCellValue(getStringValue(tMachineQuantityStat.getTag56()));
        r.getCell(13).setCellValue(getStringValue(tMachineQuantityStat.getReworkQuantity()));
        List<TMachineQuantity> detailList = tMachineQuantityStat.getDetail();
        for (int j = 0; j < detailList.size(); j++) {
          TMachineQuantity tMachineQuantity = detailList.get(j);
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(0).setCellValue(getStringValueOfDate(tMachineQuantity.getOpeDT(), "yyyy/M/d"));
          r.getCell(2)
              .setCellValue(isMachine ? tMachineQuantity.getOperator() : getStringValue(tMachineQuantity.getMachineNo()));
          r.getCell(3).setCellValue(tMachineQuantity.getDesign());
          r.getCell(4).setCellValue(getStringValue(tMachineQuantity.getQuantity()));
          r.getCell(5).setCellValue(getStringValue(tMachineQuantity.getTag138()));
          r.getCell(6).setCellValue(getStringValue(tMachineQuantity.getTag8()));
          r.getCell(7).setCellValue(getStringValue(tMachineQuantity.getTag51()));
          r.getCell(8).setCellValue(getStringValue(tMachineQuantity.getTag52()));
          r.getCell(9).setCellValue(getStringValue(tMachineQuantity.getTag53()));
          r.getCell(10).setCellValue(getStringValue(tMachineQuantity.getTag54()));
          r.getCell(11).setCellValue(getStringValue(tMachineQuantity.getTag55()));
          r.getCell(12).setCellValue(getStringValue(tMachineQuantity.getTag56()));
          r.getCell(13).setCellValue(getStringValue(tMachineQuantity.getReworkQuantity()));
        }
      }
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(totalRow, r);
      TMachineQuantityTotal tMachineQuantityTotal = tMachine.getTotal();
      r.getCell(3).setCellValue("合计");
      r.getCell(4).setCellValue(getStringValue(tMachineQuantityTotal.getQuantity()));
      r.getCell(5).setCellValue(getStringValue(tMachineQuantityTotal.getTag138()));
      r.getCell(6).setCellValue(getStringValue(tMachineQuantityTotal.getTag8()));
      r.getCell(7).setCellValue(getStringValue(tMachineQuantityTotal.getTag51()));
      r.getCell(8).setCellValue(getStringValue(tMachineQuantityTotal.getTag52()));
      r.getCell(9).setCellValue(getStringValue(tMachineQuantityTotal.getTag53()));
      r.getCell(10).setCellValue(getStringValue(tMachineQuantityTotal.getTag54()));
      r.getCell(11).setCellValue(getStringValue(tMachineQuantityTotal.getTag55()));
      r.getCell(12).setCellValue(getStringValue(tMachineQuantityTotal.getTag56()));
      r.getCell(13).setCellValue(getStringValue(tMachineQuantityTotal.getReworkQuantity()));
    }
  }

  private void genDateRow(Sheet sheet, Map<String, Object> parameterMap) {
    Row row = sheet.getRow(1);
    String startDateStr = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDateStr = parameterMap.get("endDate").toString().substring(0, 10);
    row.getCell(0).setCellValue(StringUtils.join(getStringValueOfDate(parseDate(startDateStr), "yyyy/M/d"), " to ",
        getStringValueOfDate(parseDate(endDateStr), "yyyy/M/d")));
  }

  private void genJMachineQuantityReport(QuantityData<JMachineQuantityTotal, JMachineQuantityStat> jMachine,
      Workbook workbook, Map<String, Object> parameterMap) {
    genJMachineQuantityReport(jMachine, workbook, parameterMap, false);
  }

  private void genJMachineQuantityReport(QuantityData<JMachineQuantityTotal, JMachineQuantityStat> jMachine,
      Workbook workbook, Map<String, Object> parameterMap, boolean isMachine) {
    Sheet sheet = workbook.getSheetAt(0);
    genDateRow(sheet, parameterMap);
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    if (!jMachine.getRecord().isEmpty()) {
      int currentRowNum = 3;
      Row tempRow = sheet.getRow(currentRowNum);
      Row totalRow = sheet.getRow(currentRowNum + 1);
      int totalRecord = 0;
      for (JMachineQuantityStat jMachineQuantityStat : jMachine.getRecord()) {
        totalRecord = totalRecord + jMachineQuantityStat.getDetail().size() + 1;
      }
      List<JMachineQuantityStat> recordList = jMachine.getRecord();
      for (int i = 0; i < recordList.size(); i++) {
        JMachineQuantityStat jMachineQuantityStat = recordList.get(i);
        Row r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(1)
            .setCellValue(isMachine ? getStringValue(jMachineQuantityStat.getMachineNo()) : jMachineQuantityStat.getOperator());
        r.getCell(4).setCellValue(getStringValue(jMachineQuantityStat.getQuantity()));
        r.getCell(5).setCellValue(getStringValue(jMachineQuantityStat.getTag69()));
        r.getCell(6).setCellValue(getStringValue(jMachineQuantityStat.getTag691()));
        r.getCell(7).setCellValue(getStringValue(jMachineQuantityStat.getTag6()));
        r.getCell(8).setCellValue(getStringValue(jMachineQuantityStat.getTag45()));
        r.getCell(9).setCellValue(getStringValue(jMachineQuantityStat.getTag9()));
        r.getCell(10).setCellValue(getStringValue(jMachineQuantityStat.getReworkQuantity()));
        List<JMachineQuantity> detailList = jMachineQuantityStat.getDetail();
        for (int j = 0; j < detailList.size(); j++) {
          JMachineQuantity jMachineQuantity = detailList.get(j);
          r = sxssfSheet.createRow(currentRowNum++);
          setStyle(tempRow, r);
          r.getCell(0).setCellValue(getStringValueOfDate(jMachineQuantity.getOpeDT(), "yyyy/M/d"));
          r.getCell(2)
              .setCellValue(isMachine ? jMachineQuantity.getOperator() : getStringValue(jMachineQuantity.getMachineNo()));
          r.getCell(3).setCellValue(jMachineQuantity.getDesign());
          r.getCell(4).setCellValue(getStringValue(jMachineQuantity.getQuantity()));
          r.getCell(5).setCellValue(getStringValue(jMachineQuantity.getTag69()));
          r.getCell(6).setCellValue(getStringValue(jMachineQuantity.getTag691()));
          r.getCell(7).setCellValue(getStringValue(jMachineQuantity.getTag6()));
          r.getCell(8).setCellValue(getStringValue(jMachineQuantity.getTag45()));
          r.getCell(9).setCellValue(getStringValue(jMachineQuantity.getTag9()));
          r.getCell(10).setCellValue(getStringValue(jMachineQuantity.getReworkQuantity()));
        }
      }
      Row r = sxssfSheet.createRow(currentRowNum++);
      setStyle(totalRow, r);
      JMachineQuantityTotal jMachineQuantityTotal = jMachine.getTotal();
      r.getCell(3).setCellValue("合计");
      r.getCell(4).setCellValue(getStringValue(jMachineQuantityTotal.getQuantity()));
      r.getCell(5).setCellValue(getStringValue(jMachineQuantityTotal.getTag69()));
      r.getCell(6).setCellValue(getStringValue(jMachineQuantityTotal.getTag691()));
      r.getCell(7).setCellValue(getStringValue(jMachineQuantityTotal.getTag6()));
      r.getCell(8).setCellValue(getStringValue(jMachineQuantityTotal.getTag45()));
      r.getCell(9).setCellValue(getStringValue(jMachineQuantityTotal.getTag9()));
      r.getCell(10).setCellValue(getStringValue(jMachineQuantityTotal.getReworkQuantity()));
    }
  }

  private void machineStaffExport(Workbook workbook, Map<String, Object> parameterMap) {
    JSON machineStaff = multiReportMachineStatService.machineStaff(parameterMap);

    //int sheetId = 0;
    for (Entry entry : ((JSONObject) machineStaff).entrySet()) {
      Sheet sheet = null;
      if( entry.getKey() == "jMachine") {
         sheet = workbook.getSheet("基面");
      }
      if(entry.getKey() == "tMachine") {
         sheet = workbook.getSheet("踏面");
      }
      if(entry.getKey() == "kMachine") {
         sheet = workbook.getSheet("镗孔");
      }
      JSONArray resultList = (JSONArray) entry.getValue();
     // Sheet sheet = workbook.getSheetAt(sheetId ++);

      // 标题
      sheet.getRow(1).getCell(0)
        .setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));

      int currentRow = 4;
      for (int i = 0; i < resultList.size(); i++) {
        int beginTeamLeaderIdRow = currentRow;
        JSONObject result = resultList.getJSONObject(i);
        fillMachineStaffRow(sheet, 3, currentRow, result);
        currentRow++;
        JSONArray dataList = result.getJSONArray("data");
        for (int j = 0; j < dataList.size(); j++) {
          int beginOperatorRow = currentRow;
          JSONObject subresult = dataList.getJSONObject(j);
          fillMachineStaffRow(sheet, 3, currentRow, dataList.getJSONObject(j));
          currentRow++;
          JSONArray subdataList = subresult.getJSONArray("data");
          for (int k = 0; k < subdataList.size(); k++) {
            fillMachineStaffRow(sheet, 3, currentRow, subdataList.getJSONObject(k));
            currentRow++;
          }
          CellRangeAddress region = new CellRangeAddress(beginOperatorRow, currentRow - 1, 1, 1);
          sheet.addMergedRegion(region);
        }
        CellRangeAddress region = new CellRangeAddress(beginTeamLeaderIdRow, currentRow - 1, 0, 0);
        sheet.addMergedRegion(region);
      }
      sheet.shiftRows(4, sheet.getLastRowNum(), -1);
    }
  }

  private void fillMachineStaffRow(Sheet sheet, int templateRow, int currentRow, JSONObject data) {
    // 拷贝空行
    ExcelUtil.copyCell(sheet, templateRow, 0,
      sheet, currentRow, 0,
      1, 6);
    int column = 0;
    Row row = sheet.getRow(currentRow);
    row.getCell(column++).setCellValue(data.getString("teamLeaderId"));
    row.getCell(column++).setCellValue("total".equals(data.getString("operator")) ? "" : data.getString("operator"));
    row.getCell(column++).setCellValue("total".equals(data.getString("reworkCode")) ? "" : data.getString("reworkCode"));
    row.getCell(column++).setCellValue(data.getString("reworkCount"));
    row.getCell(column++).setCellValue(data.getString("machineCount"));
    row.getCell(column++).setCellValue(data.getString("reworkMachine"));
  }

  private void machineToolExport(Workbook workbook, Map<String, Object> parameterMap) {
    JSON machineTool = multiReportMachineStatService.machineTool(parameterMap);

    //int sheetId = 0;
    for (Entry entry : ((JSONObject) machineTool).entrySet()) {
      Sheet sheet = null;
      if( entry.getKey() == "jMachine") {
        sheet = workbook.getSheet("基面");
      }
      if(entry.getKey() == "tMachine") {
        sheet = workbook.getSheet("踏面");
      }
      if(entry.getKey() == "kMachine") {
        sheet = workbook.getSheet("镗孔");
      }
      JSONArray resultList = (JSONArray) entry.getValue();
      // Sheet sheet = workbook.getSheetAt(sheetId ++);

      // 标题
      sheet.getRow(1).getCell(0)
        .setCellValue(parameterMap.get("beginDate") + " to " + parameterMap.get("endDate"));

      int currentRow = 4;
      for (int i = 0; i < resultList.size(); i++) {
        int beginMachineNoRow = currentRow;
        JSONObject result = resultList.getJSONObject(i);
        fillMachineToolRow(sheet, 3, currentRow, result);
        currentRow++;
        JSONArray dataList = result.getJSONArray("data");
        for (int j = 0; j < dataList.size(); j++) {
          fillMachineToolRow(sheet, 3, currentRow, dataList.getJSONObject(j));
          currentRow++;
        }
        CellRangeAddress region = new CellRangeAddress(beginMachineNoRow, currentRow - 1, 0, 0);
        sheet.addMergedRegion(region);
      }
      sheet.shiftRows(4, sheet.getLastRowNum(), -1);
    }
  }

  private void fillMachineToolRow(Sheet sheet, int templateRow, int currentRow, JSONObject data) {
    // 拷贝空行
    ExcelUtil.copyCell(sheet, templateRow, 0,
      sheet, currentRow, 0,
      1, 5);
    int column = 0;
    Row row = sheet.getRow(currentRow);
    row.getCell(column++).setCellValue(data.getString("machineNo"));
    row.getCell(column++).setCellValue("total".equals(data.getString("reworkCode")) ? "" : data.getString("reworkCode"));
    row.getCell(column++).setCellValue(data.getString("reworkCount"));
    row.getCell(column++).setCellValue(data.getString("machineCount"));
    row.getCell(column++).setCellValue(data.getString("reworkMachine"));
  }

  private void jMachine8Export(Workbook workbook, Map<String, Object> parameterMap) {
    Map<String, List<JMachine8>> result = multiReportMachineStatService.jMachine8(parameterMap);

    genDateRow(workbook.getSheetAt(0), parameterMap);
    List<JMachine8> jMachine8DateList = result.get("jMachine8Date");
    jMachine8DateExport(jMachine8DateList, workbook.getSheetAt(0));

    genDateRow(workbook.getSheetAt(1), parameterMap);
    List<JMachine8> jMachine8OperatorList = result.get("jMachine8Operator");
    jMachine8OperatorExport(jMachine8OperatorList, workbook.getSheetAt(1));
  }

  private void jMachine8DateExport(List<JMachine8> jMachine8List, Sheet sheet) {
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < jMachine8List.size(); i++) {
      JMachine8 jMachine8 = jMachine8List.get(i);
      Row r = sheet.createRow(currentRowNum + i + 1);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(jMachine8.getOpeDT());
      r.getCell(1).setCellValue(jMachine8.getJMachineCount());
      r.getCell(2).setCellValue(jMachine8.getJ6Count());
      r.getCell(3).setCellValue(jMachine8.getJ6JMachineCount() + '%');
      r.getCell(4).setCellValue(jMachine8.getSconf8s());
      r.getCell(5).setCellValue(jMachine8.getJ6Sconf8s());
      r.getCell(6).setCellValue(jMachine8.getMiss6Count());
      r.getCell(7).setCellValue(jMachine8.getMiss6JMachineCount() + '%');
    }
    sheet.shiftRows(4, sheet.getLastRowNum(), -1);
  }

  private void jMachine8OperatorExport(List<JMachine8> jMachine8List, Sheet sheet) {
    int currentRowNum = 3;
    Row tempRow = sheet.getRow(currentRowNum);
    for (int i = 0; i < jMachine8List.size(); i++) {
      JMachine8 jMachine8 = jMachine8List.get(i);
      Row r = sheet.createRow(currentRowNum + i + 1);
      setStyle(tempRow, r);
      r.getCell(0).setCellValue(jMachine8.getOperator());
      r.getCell(1).setCellValue(jMachine8.getJMachineCount());
      r.getCell(2).setCellValue(jMachine8.getJ6Count());
      r.getCell(3).setCellValue(jMachine8.getJ6JMachineCount() + '%');
      r.getCell(4).setCellValue(jMachine8.getSconf8s());
      r.getCell(5).setCellValue(jMachine8.getJ6Sconf8s());
      r.getCell(6).setCellValue(jMachine8.getMiss6Count());
      r.getCell(7).setCellValue(jMachine8.getMiss6JMachineCount() + '%');
    }
    sheet.shiftRows(4, sheet.getLastRowNum(), -1);
  }

  private Workbook getWorkbook(String filename) {
    Workbook workbook = null;
    try {
      ClassPathResource resource = new ClassPathResource("static/report/multi-report/machine-stat/" + filename);
      InputStream inputStream = resource.getInputStream();
      workbook = new XSSFWorkbook(inputStream);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return workbook;
  }

  private void genReworkStatReport(QuantityData<ReworkStatTotal, ReworkStatData> quantityData, Workbook workbook, Map<String, Object> parameterMap) {
    Sheet sheet = workbook.getSheetAt(0);
    genDateRow(sheet, parameterMap);
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    if (!quantityData.getRecord().isEmpty()) {
      int currentRowNum = 4;
      Row tempRow = sheet.getRow(currentRowNum);
      Row subTotalRow = sheet.getRow(currentRowNum + 1);
      Row totalRow = sheet.getRow(currentRowNum + 2);

      List<ReworkStatData> recordList = quantityData.getRecord();
      for (int i = 0; i < recordList.size(); i++) {
        ReworkStatData reworkStatData = recordList.get(i);
        Row r = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, r);
        r.getCell(0).setCellValue(getStringValue(reworkStatData.getMachineDate()));
        r.getCell(1).setCellValue(getStringValue(reworkStatData.getCastFlat()));
        r.getCell(2).setCellValue(getStringValue(reworkStatData.getCastTap()));
        r.getCell(3).setCellValue(getStringValue(reworkStatData.getCastDatum()));
        r.getCell(4).setCellValue(getStringValue(reworkStatData.getCastHub()));
        r.getCell(5).setCellValue(getStringValue(reworkStatData.getMachineFlat()));
        r.getCell(6).setCellValue(getStringValue(reworkStatData.getMachineTap()));
        r.getCell(7).setCellValue(getStringValue(reworkStatData.getMachineDatum()));
        r.getCell(8).setCellValue(getStringValue(reworkStatData.getMachineHub()));
        r.getCell(9).setCellValue(getStringValue(reworkStatData.getMonthTotal()));
      }
      Row r1 = sxssfSheet.createRow(currentRowNum++);
      setStyle(subTotalRow, r1);
      ReworkStatTotal reworkStatTotal = quantityData.getTotal();
      r1.getCell(0).setCellValue("小计");
      r1.getCell(1).setCellValue(getStringValue(reworkStatTotal.getCastFlat()));
      r1.getCell(2).setCellValue(getStringValue(reworkStatTotal.getCastTap()));
      r1.getCell(3).setCellValue(getStringValue(reworkStatTotal.getCastDatum()));
      r1.getCell(4).setCellValue(getStringValue(reworkStatTotal.getCastHub()));
      r1.getCell(5).setCellValue(getStringValue(reworkStatTotal.getMachineFlat()));
      r1.getCell(6).setCellValue(getStringValue(reworkStatTotal.getMachineTap()));
      r1.getCell(7).setCellValue(getStringValue(reworkStatTotal.getMachineDatum()));
      r1.getCell(8).setCellValue(getStringValue(reworkStatTotal.getMachineHub()));
      r1.getCell(9).setCellValue("总计");

      Row r2 = sxssfSheet.createRow(currentRowNum);
      setStyle(totalRow, r2);
      CellRangeAddress rangeAddress1 = new CellRangeAddress(currentRowNum,currentRowNum,0,3);
      CellRangeAddress rangeAddress2 = new CellRangeAddress(currentRowNum,currentRowNum,5,7);
      sxssfSheet.addMergedRegion(rangeAddress1);
      sxssfSheet.addMergedRegion(rangeAddress2);
      r2.getCell(0).setCellValue("铸造原因返修数量合计");
      r2.getCell(5).setCellValue("加工原因返修数量合计");
      r2.getCell(4).setCellValue(getStringValue(reworkStatTotal.getMonthCast()));
      r2.getCell(8).setCellValue(getStringValue(reworkStatTotal.getMonthMachine()));
      r2.getCell(9).setCellValue(getStringValue(reworkStatTotal.getMonthTotal()));
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "返修数量统计");
  }

  private void machineReworkStatExport(Workbook workbook, Map<String, Object> parameterMap) {
    QuantityData qd = multiReportMachineStatService.machineReworkStatTotal(parameterMap);
    genReworkStatReport(qd, workbook, parameterMap);
  }

  private void genRoundnessRecheckReport(List<RoundnessRecheckDetail> recheckList, Workbook workbook, Map<String, Object> parameterMap) {
    String startDate = parameterMap.get("beginDate").toString().substring(0, 10);
    String endDate = parameterMap.get("endDate").toString().substring(0, 10);
    Sheet sheet = workbook.getSheetAt(0);
    int currentRowNum = 1;
    //生成并插入报告生成日期
    Row reportRow = sheet.getRow(currentRowNum);
    reportRow.getCell(0).setCellValue(StringUtils.join("查询日期： ", getStringValueOfDate(new Date(), "yyyy/MM/dd HH:mm")));
    //生成并插入查询日期间隔
    Row queryRow = sheet.getRow(currentRowNum + 1);
    queryRow.getCell(0).setCellValue(StringUtils.join("成品日期： ", getStringValueOfDate(parseDate(startDate), "yyyy/MM/dd"), " to ",
            getStringValueOfDate(parseDate(endDate), "yyyy/MM/dd")));
    Sheet sxssfSheet = getClonedSheet(sheet, 0);
    if (!recheckList.isEmpty()) {
      currentRowNum = 4;
      Row tempRow = sheet.getRow(currentRowNum);
      for (int i = 0; i < recheckList.size(); i++) {
        RoundnessRecheckDetail rd = recheckList.get(i);
        Row row = sxssfSheet.createRow(currentRowNum++);
        setStyle(tempRow, row);
        row.getCell(0).setCellValue(getStringValue(rd.getSn()));
        row.getCell(1).setCellValue(getStringValue(rd.getWheelSerial()));
        row.getCell(2).setCellValue(getStringValue(rd.getTapeSize()));
        row.getCell(3).setCellValue(getStringValue(rd.getOperator()));
      }
    }
    workbook.removeSheetAt(0);
    workbook.setSheetName(0, "圆度复检");
  }

  private void machineRoundnessRecheckExport(Workbook workbook, Map<String, Object> parameterMap) {
    List<RoundnessRecheckDetail> rd = multiReportMachineStatService.machineRoundnessRecheck(parameterMap);
    genRoundnessRecheckReport(rd, workbook, parameterMap);
  }
}
