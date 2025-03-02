package tech.hciot.dwis.business.application.report;

import static tech.hciot.dwis.base.util.StandardTimeUtil.dateStr;

import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.infrastructure.UnderlineCamelUtil;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.*;

@Service
@Slf4j
public class MultiReportQcStatService {

  private static final String QC_STAT_SUMMARY_WHEEL = "4.1.1-qc-stat-summary-wheel";
  private static final String QC_STAT_SUMMARY_PRECHECK = "4.1.2-qc-stat-summary-precheck";
  private static final String QC_STAT_SUMMARY_SCRAP = "4.1.3-qc-stat-summary-scrap";
  private static final String QC_STAT_SUMMARY_PRECHECK_TIMES = "4.1.4-qc-stat-summary-precheck-times";
  private static final String QC_STAT_SUMMARY_FINALCHECK_DESIGN = "4.1.5-qc-stat-summary-finalcheck-design";
  private static final String QC_STAT_SUMMARY_FINALCHECK_REWORK = "4.1.6-qc-stat-summary-finalcheck-rework";
  private static final String QC_STAT_SUMMARY_ULTRA = "4.1.7-qc-stat-summary-ultra";
  private static final String QC_STAT_SUMMARY_BALANCE = "4.1.8-qc-stat-summary-balance";
  private static final String QC_STAT_SUMMARY_TAPE = "4.1.9-qc-stat-summary-tape";
  private static final String QC_STAT_SUMMARY_CIHEN = "4.1.10-qc-stat-summary-cihen";
  private static final String QC_STAT_SUMMARY_MAGNETIC = "4.1.11-qc-stat-summary-magnetic";
  private static final String QC_STAT_SUMMARY_TRANSPORT_XRAY = "4.1.12.1-qc-stat-summary-transport-xray";
  private static final String QC_STAT_SUMMARY_TRANSPORT_DEWEIGHT = "4.1.12.2-qc-stat-summary-transport-deweight";
  private static final String QC_STAT_SUMMARY_TRANSPORT_BORE = "4.1.12.3-qc-stat-summary-transport-bore";
  private static final String QC_STAT_SUMMARY_XRAY = "4.1.13-qc-stat-summary-xray";
  private static final String QC_STAT_REWORKSCRAP_PRECHECK = "4.2.1-qc-stat-reworkscrap-precheck";
  private static final String QC_STAT_REWORKSCRAP_FINALCHECK = "4.2.2-qc-stat-reworkscrap-finalcheck";
  private static final String QC_STAT_REWORKSCRAP_ULTRA = "4.2.3-qc-stat-reworkscrap-ultra";
  private static final String QC_STAT_REWORKSCRAP_MAGNETIC = "4.2.4-qc-stat-reworkscrap-magnetic";
  private static final String QC_STAT_REWORKSCRAP_BALANCE = "4.2.5-qc-stat-reworkscrap-balance";
  private static final String QC_STAT_SHOT = "4.3-qc-stat-shot";
  private static final String QC_STAT_SMALL_TAPE = "4.4-qc-stat-smalltape";
  private static final String QC_STAT_SMALL_PRECHECK_PERCENT = "4.5.1-qc-stat-precheck-percent";
  private static final String QC_STAT_SMALL_PRECHECK_PERCENT_DESIGN = "4.5.2-qc-stat-precheck-percent-design";
  private static final String QC_STAT_SMALL_PRECHECK_PERCENT_OUT = "4.5.4-qc-stat-precheck-percent-out";
  private static final String QC_STAT_SMALL_FINALCHECK_PERCENT = "4.6.1-qc-stat-finalcheck-percent";
  private static final String QC_STAT_SMALL_FINALCHECK_PERCENT_DESIGN = "4.6.2-qc-stat-finalcheck-percent-design";
  private static final String QC_STAT_SMALL_BALANCE_PERCENT = "4.7-qc-stat-balance-percent";
  private static final String QC_STAT_SMALL_REWORK_PERCENT = "4.8-qc-stat-rework-percent";
  private static final String QC_STAT_SMALL_WHEEL_DETAIL = "4.9-qc-stat-wheel-detail";
  private static final String QC_STAT_SCRAP_SHIFT_QC_TOP = "4.10.1-qc-stat-scrap-shift-qc-top";
  private static final String QC_STAT_SCRAP_SHIFT_QC_DETAIL = "4.10.2-qc-stat-scrap-shift-qc-detail";
  private static final String QC_STAT_SCRAP_SHIFT_MODEL_TOP = "4.10.3-qc-stat-scrap-shift-model-top";
  private static final String QC_STAT_SCRAP_SHIFT_MODEL_DETAIL = "4.10.4-qc-stat-scrap-shift-model-detail";
  private static final String QC_STAT_SCRAP_SHIFT_FURNACE_TOP = "4.10.5-qc-stat-scrap-shift-furnace-top";
  private static final String QC_STAT_SCRAP_SHIFT_FURNACE_DETAIL = "4.10.6-qc-stat-scrap-shift-furnace-detail";
  private static final String QC_STAT_SCRAP_SHIFT_MACHINE_TOP = "4.10.7-qc-stat-scrap-shift-machine-top";
  private static final String QC_STAT_SCRAP_SHIFT_MACHINE_DETAIL = "4.10.8-qc-stat-scrap-shift-machine-detail";
  private static final String QC_STAT_REWORK_SHIFT_PRECHECK_TOP = "4.11.1-qc-stat-rework-shift-precheck-top";
  private static final String QC_STAT_REWORK_SHIFT_PRECHECK_DETAIL = "4.11.2-qc-stat-rework-shift-precheck-detail";
  private static final String QC_STAT_REWORK_SHIFT_FINALCHECK_TOP = "4.11.3-qc-stat-rework-shift-finalcheck-top";
  private static final String QC_STAT_REWORK_SHIFT_FINALCHECK_DETAIL = "4.11.4-qc-stat-rework-shift-finalcheck-detail";
  private static final String QC_STAT_SCRAP_LADLE_SCRAP = "4.12.1-qc-stat-scrap-ladle";
  private static final String QC_STAT_SCRAP_LADLE = "4.12.2-qc-stat-scrap-ladle";
  private static final String QC_STAT_FINAL_CHECK_REWORK_DETAIL = "4.13.1-qc-stat-final-check-rework";
  private static final String QC_STAT_FINAL_CHECK_REWORK_DATE_TOTAL = "4.13.2-qc-stat-final-check-rework";
  private static final String QC_STAT_FINAL_CHECK_REWORK_DATE_DETAIL = "4.13.3-qc-stat-final-check-rework";
  private static final String QC_STAT_FINAL_CHECK_REWORK_LINE_TOTAL = "4.13.4-qc-stat-final-check-rework";
  private static final String QC_STAT_FINAL_CHECK_REWORK_LINE_DETAIL = "4.13.5-qc-stat-final-check-rework";
  private static final String QC_STAT_POUR_REWORK_DETAIL = "4.13.6-qc-stat-final-check-rework";
  private static final String QC_STAT_POUR_REWORK_DATE_TOTAL = "4.13.7-qc-stat-final-check-rework";
  private static final String QC_STAT_POUR_REWORK_DATE_DETAIL = "4.13.8-qc-stat-final-check-rework";
  private static final String QC_STAT_POUR_REWORK_LINE_TOTAL = "4.13.9-qc-stat-final-check-rework";
  private static final String QC_STAT_POUR_REWORK_LINE_DETAIL = "4.13.10-qc-stat-final-check-rework";
  private static final String QC_STAT_MACHINE_REWORK_DETAIL = "4.14.1-qc-stat-machine-rework";
  private static final String QC_STAT_MACHINE_REWORK_DATE_DETAIL = "4.14.2-qc-stat-machine-rework";
  private static final String QC_STAT_FINAL_REWORK_JMACHINE_NO_DETAIL = "4.15.1-qc-stat-final-rework";
  private static final String QC_STAT_FINAL_REWORK_JMACHINE_HAS_DETAIL = "4.15.2-qc-stat-final-rework";
  private static final String QC_STAT_FINAL_REWORK_TMACHINE_NO_DETAIL = "4.15.3-qc-stat-final-rework";
  private static final String QC_STAT_FINAL_REWORK_TMACHINE_HAS_DETAIL = "4.15.4-qc-stat-final-rework";
  private static final String QC_STAT_FINAL_REWORK_KMACHINE_NO_DETAIL = "4.15.5-qc-stat-final-rework";
  private static final String QC_STAT_FINAL_REWORK_KMACHINE_HAS_DETAIL = "4.15.6-qc-stat-final-rework";

  @Resource
  SqlTemplateParser sqlTemplateParser;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private ReportAssembler assembler;

  private List queryResultList(String templateName, Map<String, Object> parameterMap, Class z) {
    log.info("machine stat query - {} begin", templateName);
    String sql = sqlTemplateParser.parseSqlTemplate("composite-report/qc-stat", templateName, parameterMap);
    Query query = entityManager.createNativeQuery(sql);
    parameterMap.entrySet().forEach(entry -> {
      if (!entry.getKey().startsWith("shift")) {
        query.setParameter(entry.getKey(), entry.getValue());
      }
    });
    query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    try {
      List<Map<String, Object>> resultList = query.getResultList();
      List objectList = new ArrayList<>();
      resultList.forEach(data -> {
        Map<String, Object> humpData = UnderlineCamelUtil.underlineMapToHumpMap(data);
        objectList.add(new JSONObject(humpData).toJavaObject(z));
      });
      return objectList;
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error("qc query error: " + e.getMessage(), e);
    }
    return new ArrayList<>();
  }


  public SummaryData summary(Map<String, Object> parameterMap) {
    changeParameterMap(parameterMap);
    SummaryData summaryData = new SummaryData();
    fillPreCheck(summaryData, parameterMap);
    fillPreCheckTimes(summaryData, parameterMap);
    fillFinalCheck(summaryData, parameterMap);
    fillUltra(summaryData, parameterMap);
    fillBalance(summaryData, parameterMap);
    fillXray(summaryData, parameterMap);
    fillCihen(summaryData, parameterMap);
    fillMagnetic(summaryData, parameterMap);
    fillTransport(summaryData, parameterMap);
    return summaryData;
  }

  private void fillXray(SummaryData summaryData, Map<String, Object> parameterMap) {
    if(parameterMap.containsKey("staffId")){
      parameterMap.remove("staffId");
    }
    List<XrayData> xrayDataList = queryResultList(QC_STAT_SUMMARY_XRAY, parameterMap,
        XrayData.class);
    summaryData.setXray(xrayDataList);
  }

  private void changeParameterMap(Map<String, Object> parameterMap) {
    if (parameterMap.containsKey("shift")) {
      Integer shift = (Integer) parameterMap.get("shift");
      String condition = null;
      String balanceCondition = null;
      if (shift == 1) {
        condition = "CONVERT(INT, CONVERT(varchar(2),ope_d_t, 108)) < 8 ";
        balanceCondition = "CONVERT(INT, CONVERT(varchar(2),last_balance, 108)) < 8 ";
      } else if (shift == 2) {
        condition = "CONVERT(INT, CONVERT(varchar(2),ope_d_t, 108)) BETWEEN 8 AND 15 ";
        balanceCondition = "CONVERT(INT, CONVERT(varchar(2),last_balance, 108)) BETWEEN 8 AND 15 ";
      } else if (shift == 3) {
        condition = "CONVERT(INT, CONVERT(varchar(2),ope_d_t, 108)) > 15 ";
        balanceCondition = "CONVERT(INT, CONVERT(varchar(2),last_balance, 108)) > 15 ";
      }
      parameterMap.put("shift", condition);
      parameterMap.put("shiftBalance", balanceCondition);
    }
  }

  private void fillTransport(SummaryData summaryData, Map<String, Object> parameterMap) {
    TransportData transportData = new TransportData();
    //统计X光发运
    List<TransportXrayData> transportXrayDataList = queryResultList(QC_STAT_SUMMARY_TRANSPORT_XRAY, parameterMap, TransportXrayData.class);
    transportData.setXrayList(transportXrayDataList);
    int xrayAmount = 0;
    for (TransportXrayData transportXrayData : transportXrayDataList) {
      xrayAmount += transportXrayData.getAmount();
    }
    transportData.setXrayAmount(xrayAmount);

    //统计去重发运
    List<TransportDeweightData> transportDeweightDataList = queryResultList(QC_STAT_SUMMARY_TRANSPORT_DEWEIGHT, parameterMap, TransportDeweightData.class);
    transportData.setDeweightList(transportDeweightDataList);
    int deweightAmount = 0;
    for (TransportDeweightData transportDeweightData : transportDeweightDataList) {
      deweightAmount += transportDeweightData.getAmount();
    }
    transportData.setDeweightAmount(deweightAmount);

    //统计镗孔发运
    List<TransportBoreData> transportBoreDataList = queryResultList(QC_STAT_SUMMARY_TRANSPORT_BORE, parameterMap, TransportBoreData.class);
    transportData.setBoreList(transportBoreDataList);
    int boreAmount = 0;
    for (TransportBoreData transportBoreData : transportBoreDataList) {
      boreAmount += transportBoreData.getAmount();
    }
    transportData.setBoreAmount(boreAmount);

    summaryData.setTransport(transportData);
  }

  private void fillMagnetic(SummaryData summaryData, Map<String, Object> parameterMap) {
    List<MagneticData> magneticDataList = queryResultList(QC_STAT_SUMMARY_MAGNETIC, parameterMap,
        MagneticData.class);
    summaryData.setMagnetic(magneticDataList);
  }

  private void fillCihen(SummaryData summaryData, Map<String, Object> parameterMap) {
    List<CihenData> cihenDataList = queryResultList(QC_STAT_SUMMARY_CIHEN, parameterMap,
        CihenData.class);
    summaryData.setCihen(cihenDataList);
  }

  private void fillBalance(SummaryData summaryData, Map<String, Object> parameterMap) {
    BalanceData balanceData = new BalanceData();
    List<BalanceSummaryData> balanceSummaryDataList = queryResultList(QC_STAT_SUMMARY_BALANCE, parameterMap,
        BalanceSummaryData.class);
    balanceData.setBalance(balanceSummaryDataList);
    int line1Total = 0;
    int line2Total = 0;
    int balanceTotal = 0;
    for (BalanceSummaryData balanceSummaryData : balanceSummaryDataList) {
      balanceTotal += balanceSummaryData.getAmount();
      if (balanceSummaryData.getXh().equals("01")) {
        line1Total += balanceSummaryData.getAmount();
      } else {
        line2Total += balanceSummaryData.getAmount();
      }
    }
    balanceData.setLine1Total(line1Total);
    balanceData.setLine2Total(line2Total);
    balanceData.setBalanceTotal(balanceTotal);

    List<JSONObject> jsonObjectList = queryResultList(QC_STAT_SUMMARY_TAPE, parameterMap, JSONObject.class);
    if (jsonObjectList.size() != 1) {
      List<String> keyList = new ArrayList<>();
      keyList.add("design");
      keyList.add("boreSize");
      balanceData.setTape(assembler.generateProdLevelResult(jsonObjectList, keyList));
    } else {
      balanceData.setTape(null);
    }
    summaryData.setBalance(balanceData);
  }

  private void fillUltra(SummaryData summaryData, Map<String, Object> parameterMap) {
    UltraData ultraData = new UltraData();
    List<UltraSummaryData> ultraSummaryDataList = queryResultList(QC_STAT_SUMMARY_ULTRA, parameterMap,
        UltraSummaryData.class);
    ultraData.setUltra(ultraSummaryDataList);
    int line1Total = 0;
    int line2Total = 0;
    int ultraTotal = 0;
    for (UltraSummaryData ultraSummaryData : ultraSummaryDataList) {
      ultraTotal += ultraSummaryData.getAmount();
      line1Total += ultraSummaryData.getTotal1();
      line2Total += ultraSummaryData.getTotal2();
    }
    ultraData.setLine1Total(line1Total);
    ultraData.setLine2Total(line2Total);
    ultraData.setUltraTotal(ultraTotal);

    summaryData.setUltra(ultraData);
  }

  private void fillFinalCheck(SummaryData summaryData, Map<String, Object> parameterMap) {
    FinalCheckData finalCheckData = new FinalCheckData();
    List<DesignSummaryData> designSummaryDataList = queryResultList(QC_STAT_SUMMARY_FINALCHECK_DESIGN, parameterMap,
        DesignSummaryData.class);
    finalCheckData.setDesign(designSummaryDataList);
    int cj33Amount = 0;
    int sa34Amount = 0;
    int otherAmount = 0;
    int totalAmount = 0;
    int line1Amount = 0;
    int line2Amount = 0;
    int totalLineAmount = 0;
    for (DesignSummaryData designSummaryData : designSummaryDataList) {
      totalAmount += designSummaryData.getAmount();
      if (designSummaryData.getDesign().equalsIgnoreCase("cj33")) {
        cj33Amount += designSummaryData.getAmount();
      } else if (designSummaryData.getDesign().equalsIgnoreCase("sa34")) {
        sa34Amount += designSummaryData.getAmount();
      } else {
        otherAmount += designSummaryData.getAmount();
      }
      totalLineAmount += designSummaryData.getAmount();
      if (designSummaryData.getXh().equals("01")) {
        line1Amount += designSummaryData.getAmount();
      } else {
        line2Amount += designSummaryData.getAmount();
      }
    }
    finalCheckData.setCj33Amount(cj33Amount);
    finalCheckData.setSa34Amount(sa34Amount);
    finalCheckData.setOtherAmount(otherAmount);
    finalCheckData.setTotalAmount(totalAmount);
    finalCheckData.setLine1Amount(line1Amount);
    finalCheckData.setLine2Amount(line2Amount);
    finalCheckData.setTotalLineAmount(totalLineAmount);

    List<ReworkSummaryData> reworkSummaryDataList = queryResultList(QC_STAT_SUMMARY_FINALCHECK_REWORK, parameterMap,
        ReworkSummaryData.class);
    ReworkSummaryData reworkSummaryData = reworkSummaryDataList.get(0);
    finalCheckData.setH1Amount(reworkSummaryData.getH1() == null ? 0 : reworkSummaryData.getH1());
    finalCheckData.setH6Amount(reworkSummaryData.getH6() == null ? 0 : reworkSummaryData.getH6());
    finalCheckData.setTirAmount(reworkSummaryData.getTir() == null ? 0 : reworkSummaryData.getTir());
    finalCheckData
        .setTotalReworkAmount(finalCheckData.getH1Amount() + finalCheckData.getH6Amount() + finalCheckData.getTirAmount());
    summaryData.setFinalCheck(finalCheckData);
  }

  private void fillPreCheckTimes(SummaryData summaryData, Map<String, Object> parameterMap) {
    List<PreCheckTimesData> preCheckTimesDataList = queryResultList(QC_STAT_SUMMARY_PRECHECK_TIMES, parameterMap,
        PreCheckTimesData.class);
    summaryData.setPreCheckTimes(preCheckTimesDataList);
  }

  private void fillPreCheck(SummaryData summaryData, Map<String, Object> parameterMap) {
    PreCheckData preCheckData = new PreCheckData();
    List<WheelSummaryData> wheelSummaryDataList = queryResultList(QC_STAT_SUMMARY_WHEEL, parameterMap,
        WheelSummaryData.class);
    preCheckData.setWheel(wheelSummaryDataList);
    int internalAmount = 0;
    int otherAmount = 0;
    int totalAmount = 0;
    for (WheelSummaryData wheelSummaryData : wheelSummaryDataList) {
      totalAmount += wheelSummaryData.getAmount();
      if (wheelSummaryData.getInternal() == 1) {
        internalAmount += wheelSummaryData.getAmount();
      } else {
        otherAmount += wheelSummaryData.getAmount();
      }
    }
    preCheckData.setInternalAmount(internalAmount);
    preCheckData.setOtherAmount(otherAmount);
    preCheckData.setTotalAmount(totalAmount);

    List<PreCheckSummaryData> preCheckSummaryDataList = queryResultList(QC_STAT_SUMMARY_PRECHECK, parameterMap,
        PreCheckSummaryData.class);
    preCheckData.setPreCheck(preCheckSummaryDataList);

    List<PreCheckScrapData> preCheckScrapDataList = queryResultList(QC_STAT_SUMMARY_SCRAP, parameterMap,
        PreCheckScrapData.class);
    preCheckData.setPreCheckScrap(preCheckScrapDataList);
    int preCheckScrapTotal = 0;
    preCheckScrapTotal = preCheckScrapDataList.stream().mapToInt(data -> data.getAmount()).sum();
    preCheckData.setPreCheckScrapTotal(preCheckScrapTotal);
    summaryData.setPreCheck(preCheckData);
  }

  public ReworkScrapData reworkScrap(Map<String, Object> parameterMap) {
    changeParameterMap(parameterMap);
    ReworkScrapData reworkScrapData = new ReworkScrapData();
    fillPreCheckReworkScrap(reworkScrapData, parameterMap);
    fillFinalCheckReworkScrap(reworkScrapData, parameterMap);
    fillUltraReworkScrap(reworkScrapData, parameterMap);
    fillMagneticReworkScrap(reworkScrapData, parameterMap);
    fillBalanceReworkScrap(reworkScrapData, parameterMap);
    return reworkScrapData;
  }

  private void fillBalanceReworkScrap(ReworkScrapData reworkScrapData, Map<String, Object> parameterMap) {
    List<BalanceReworkScrapData> balanceReworkScrapDataList = queryResultList(QC_STAT_REWORKSCRAP_BALANCE, parameterMap,
        BalanceReworkScrapData.class);
    reworkScrapData.setBalance(balanceReworkScrapDataList);
  }

  private void fillMagneticReworkScrap(ReworkScrapData reworkScrapData, Map<String, Object> parameterMap) {
    List<PreCheckReworkScrapData> magneticReworkScrapDataList = queryResultList(QC_STAT_REWORKSCRAP_MAGNETIC, parameterMap,
        PreCheckReworkScrapData.class);
    reworkScrapData.setMagnetic(magneticReworkScrapDataList);
  }

  private void fillUltraReworkScrap(ReworkScrapData reworkScrapData, Map<String, Object> parameterMap) {
    List<PreCheckReworkScrapData> ultraReworkScrapDataList = queryResultList(QC_STAT_REWORKSCRAP_ULTRA, parameterMap,
        PreCheckReworkScrapData.class);
    reworkScrapData.setUltra(ultraReworkScrapDataList);
  }

  private void fillFinalCheckReworkScrap(ReworkScrapData reworkScrapData, Map<String, Object> parameterMap) {
    List<PreCheckReworkScrapData> finalCheckReworkScrapDataList = queryResultList(QC_STAT_REWORKSCRAP_FINALCHECK, parameterMap,
        PreCheckReworkScrapData.class);
    reworkScrapData.setFinalCheck(finalCheckReworkScrapDataList);
  }

  private void fillPreCheckReworkScrap(ReworkScrapData reworkScrapData, Map<String, Object> parameterMap) {
    List<PreCheckReworkScrapData> preCheckReworkScrapDataList = queryResultList(QC_STAT_REWORKSCRAP_PRECHECK, parameterMap,
        PreCheckReworkScrapData.class);
    reworkScrapData.setPreCheck(preCheckReworkScrapDataList);
  }

  public List<ShotData> shot(Map<String, Object> parameterMap) {
    List<ShotData> shotDataList = queryResultList(QC_STAT_SHOT, parameterMap,
        ShotData.class);
    return shotDataList;
  }

  public List<SmallTapeData> smallTape(Map<String, Object> parameterMap) {
    parameterMap.remove("endDate");
    List<SmallTapeData> smallTapeDataList = queryResultList(QC_STAT_SMALL_TAPE, parameterMap,
        SmallTapeData.class);
    return smallTapeDataList;
  }

  public PreCheckPercentData preCheckPercent(Map<String, Object> parameterMap) {
    PreCheckPercentData preCheckPercentData = new PreCheckPercentData();
    fillDate(parameterMap, preCheckPercentData);
    fillDesign(parameterMap, preCheckPercentData);
    fillStaff(parameterMap, preCheckPercentData);
    fillOut(parameterMap, preCheckPercentData);
    return preCheckPercentData;
  }

  private void fillOut(Map<String, Object> parameterMap, PreCheckPercentData preCheckPercentData) {
    List<PreCheckPercentRecord> preCheckPercentRecordList = queryResultList(QC_STAT_SMALL_PRECHECK_PERCENT_OUT, parameterMap,
        PreCheckPercentRecord.class);
    PreCheckPercentOutTopData preCheckPercentOutTopData = new PreCheckPercentOutTopData();
    Map<String, PreCheckPercentOutTotalData> outMap = new LinkedHashMap<>();
    Map<String, PreCheckPercentDateTotalData> dateMap = new LinkedHashMap<>();
    preCheckPercentRecordList.forEach(record -> {
      String key = record.getOutCompany();
      String dateKey = key + DateFormatUtils.format(record.getOpeDT(), "yyyy/M/d");
      PreCheckPercentOutTotalData totalData;
      PreCheckPercentDateTotalData totalDateData;
      preCheckPercentOutTopData.setAmount(preCheckPercentOutTopData.getAmount() + record.getAmount());
      preCheckPercentOutTopData.setNoReworkAmount(preCheckPercentOutTopData.getNoReworkAmount() + record.getNoReworkAmount());
      preCheckPercentOutTopData
          .setNoReworkScrapAmount(preCheckPercentOutTopData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
      preCheckPercentOutTopData.setEr3(preCheckPercentOutTopData.getEr3() + record.getEr3());
      preCheckPercentOutTopData.setEr4(preCheckPercentOutTopData.getEr4() + record.getEr4());
      preCheckPercentOutTopData.setEr5(preCheckPercentOutTopData.getEr5() + record.getEr5());
      preCheckPercentOutTopData.setEr6(preCheckPercentOutTopData.getEr6() + record.getEr6());
      preCheckPercentOutTopData.setEr3456(preCheckPercentOutTopData.getEr3456() + record.getEr3456());
      preCheckPercentOutTopData.setOtherAmount(preCheckPercentOutTopData.getOtherAmount() + record.getOtherAmount());
      if (outMap.containsKey(key)) {
        totalData = outMap.get(key);
        totalData.setAmount(totalData.getAmount() + record.getAmount());
        totalData.setNoReworkAmount(totalData.getNoReworkAmount() + record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(totalData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
        totalData.setEr3(totalData.getEr3() + record.getEr3());
        totalData.setEr4(totalData.getEr4() + record.getEr4());
        totalData.setEr5(totalData.getEr5() + record.getEr5());
        totalData.setEr6(totalData.getEr6() + record.getEr6());
        totalData.setEr3456(totalData.getEr3456() + record.getEr3456());
        totalData.setOtherAmount(totalData.getOtherAmount() + record.getOtherAmount());

        if (dateMap.containsKey(dateKey)) {
          totalDateData = dateMap.get(dateKey);
          totalDateData.setAmount(totalDateData.getAmount() + record.getAmount());
          totalDateData.setNoReworkAmount(totalDateData.getNoReworkAmount() + record.getNoReworkAmount());
          totalDateData.setNoReworkScrapAmount(totalDateData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
          totalDateData.setEr3(totalDateData.getEr3() + record.getEr3());
          totalDateData.setEr4(totalDateData.getEr4() + record.getEr4());
          totalDateData.setEr5(totalDateData.getEr5() + record.getEr5());
          totalDateData.setEr6(totalDateData.getEr6() + record.getEr6());
          totalDateData.setEr3456(totalDateData.getEr3456() + record.getEr3456());
          totalDateData.setOtherAmount(totalDateData.getOtherAmount() + record.getOtherAmount());
          totalDateData.getDetail().add(record);
        } else {
          totalDateData = new PreCheckPercentDateTotalData();
          totalDateData.setOpeDT(record.getOpeDT());
          totalDateData.setAmount(record.getAmount());
          totalDateData.setNoReworkAmount(record.getNoReworkAmount());
          totalDateData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
          totalDateData.setEr3(record.getEr3());
          totalDateData.setEr4(record.getEr4());
          totalDateData.setEr5(record.getEr5());
          totalDateData.setEr6(record.getEr6());
          totalDateData.setEr3456(record.getEr3456());
          totalDateData.setOtherAmount(record.getOtherAmount());
          List<PreCheckPercentRecord> detail = new ArrayList<>();
          detail.add(record);
          totalDateData.setDetail(detail);
          totalData.getData().add(totalDateData);
          dateMap.put(dateKey, totalDateData);
        }
      } else {
        totalData = new PreCheckPercentOutTotalData();
        totalData.setOutCompany(record.getOutCompany());
        totalData.setAmount(record.getAmount());
        totalData.setNoReworkAmount(record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalData.setEr3(record.getEr3());
        totalData.setEr4(record.getEr4());
        totalData.setEr5(record.getEr5());
        totalData.setEr6(record.getEr6());
        totalData.setEr3456(record.getEr3456());
        totalData.setOtherAmount(record.getOtherAmount());
        List<PreCheckPercentDateTotalData> date = new ArrayList<>();
        totalDateData = new PreCheckPercentDateTotalData();
        totalDateData.setOpeDT(record.getOpeDT());
        totalDateData.setAmount(record.getAmount());
        totalDateData.setNoReworkAmount(record.getNoReworkAmount());
        totalDateData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalDateData.setEr3(record.getEr3());
        totalDateData.setEr4(record.getEr4());
        totalDateData.setEr5(record.getEr5());
        totalDateData.setEr6(record.getEr6());
        totalDateData.setEr3456(record.getEr3456());
        totalDateData.setOtherAmount(record.getOtherAmount());
        List<PreCheckPercentRecord> detail = new ArrayList<>();
        detail.add(record);
        totalDateData.setDetail(detail);
        date.add(totalDateData);
        totalData.setData(date);
        dateMap.put(dateKey, totalDateData);
        outMap.put(key, totalData);
      }
    });
    preCheckPercentOutTopData.setList(outMap.values().stream().map(data -> {
      data.setNoReworkPercent(calcPercent(data.getNoReworkAmount(), data.getAmount()));
      data.setNoReworkScrapPercent(calcPercent(data.getNoReworkScrapAmount(), data.getAmount()));
      data.setEr3456Percent(calcPercent(data.getEr3456(), data.getAmount()));
      data.setEr3Percent(calcPercent(data.getEr3(), data.getAmount()));
      data.setEr4Percent(calcPercent(data.getEr4(), data.getAmount()));
      data.setEr5Percent(calcPercent(data.getEr5(), data.getAmount()));
      data.setEr6Percent(calcPercent(data.getEr6(), data.getAmount()));
      data.setOtherPercent(calcPercent(data.getOtherAmount(), data.getAmount()));
      data.getData().forEach(total -> {
        total.setNoReworkPercent(calcPercent(total.getNoReworkAmount(), total.getAmount()));
        total.setNoReworkScrapPercent(calcPercent(total.getNoReworkScrapAmount(), total.getAmount()));
        total.setEr3456Percent(calcPercent(total.getEr3456(), total.getAmount()));
        total.setEr3Percent(calcPercent(total.getEr3(), total.getAmount()));
        total.setEr4Percent(calcPercent(total.getEr4(), total.getAmount()));
        total.setEr5Percent(calcPercent(total.getEr5(), total.getAmount()));
        total.setEr6Percent(calcPercent(total.getEr6(), total.getAmount()));
        total.setOtherPercent(calcPercent(total.getOtherAmount(), total.getAmount()));
      });
      return data;
    }).collect(Collectors.toList()));
    preCheckPercentOutTopData.setNoReworkPercent(
        calcPercent(preCheckPercentOutTopData.getNoReworkAmount(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentOutTopData.setNoReworkScrapPercent(
        calcPercent(preCheckPercentOutTopData.getNoReworkScrapAmount(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentOutTopData
        .setEr3456Percent(calcPercent(preCheckPercentOutTopData.getEr3456(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentOutTopData
        .setEr3Percent(calcPercent(preCheckPercentOutTopData.getEr3(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentOutTopData
        .setEr4Percent(calcPercent(preCheckPercentOutTopData.getEr4(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentOutTopData
        .setEr5Percent(calcPercent(preCheckPercentOutTopData.getEr5(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentOutTopData
        .setEr6Percent(calcPercent(preCheckPercentOutTopData.getEr6(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentOutTopData
        .setOtherPercent(calcPercent(preCheckPercentOutTopData.getOtherAmount(), preCheckPercentOutTopData.getAmount()));
    preCheckPercentData.setOut(preCheckPercentOutTopData);
  }

  private void fillStaff(Map<String, Object> parameterMap, PreCheckPercentData preCheckPercentData) {
    List<PreCheckPercentRecord> preCheckPercentRecordList = queryResultList(QC_STAT_SMALL_PRECHECK_PERCENT, parameterMap,
        PreCheckPercentRecord.class);
    PreCheckPercentStaffTopData preCheckPercentStaffTopData = new PreCheckPercentStaffTopData();
    Map<String, PreCheckPercentStaffTotalData> tempMap = new LinkedHashMap<>();
    preCheckPercentRecordList.forEach(preCheckPercentRecord -> {
      preCheckPercentStaffTopData.setAmount(preCheckPercentStaffTopData.getAmount() + preCheckPercentRecord.getAmount());
      preCheckPercentStaffTopData
          .setNoReworkAmount(preCheckPercentStaffTopData.getNoReworkAmount() + preCheckPercentRecord.getNoReworkAmount());
      preCheckPercentStaffTopData.setNoReworkScrapAmount(
          preCheckPercentStaffTopData.getNoReworkScrapAmount() + preCheckPercentRecord.getNoReworkScrapAmount());
      preCheckPercentStaffTopData.setEr3(preCheckPercentStaffTopData.getEr3() + preCheckPercentRecord.getEr3());
      preCheckPercentStaffTopData.setEr4(preCheckPercentStaffTopData.getEr4() + preCheckPercentRecord.getEr4());
      preCheckPercentStaffTopData.setEr5(preCheckPercentStaffTopData.getEr5() + preCheckPercentRecord.getEr5());
      preCheckPercentStaffTopData.setEr6(preCheckPercentStaffTopData.getEr6() + preCheckPercentRecord.getEr6());
      preCheckPercentStaffTopData.setEr3456(preCheckPercentStaffTopData.getEr3456() + preCheckPercentRecord.getEr3456());
      preCheckPercentStaffTopData
          .setOtherAmount(preCheckPercentStaffTopData.getOtherAmount() + preCheckPercentRecord.getOtherAmount());

      String key = preCheckPercentRecord.getCopeInspectorID();
      PreCheckPercentStaffTotalData preCheckPercentStaffTotalData;
      if (tempMap.containsKey(key)) {
        preCheckPercentStaffTotalData = tempMap.get(key);
        preCheckPercentStaffTotalData
            .setAmount(preCheckPercentStaffTotalData.getAmount() + preCheckPercentRecord.getAmount());
        preCheckPercentStaffTotalData.setNoReworkAmount(
            preCheckPercentStaffTotalData.getNoReworkAmount() + preCheckPercentRecord.getNoReworkAmount());
        preCheckPercentStaffTotalData.setNoReworkScrapAmount(
            preCheckPercentStaffTotalData.getNoReworkScrapAmount() + preCheckPercentRecord.getNoReworkScrapAmount());
        preCheckPercentStaffTotalData.setEr3(preCheckPercentStaffTotalData.getEr3() + preCheckPercentRecord.getEr3());
        preCheckPercentStaffTotalData.setEr4(preCheckPercentStaffTotalData.getEr4() + preCheckPercentRecord.getEr4());
        preCheckPercentStaffTotalData.setEr5(preCheckPercentStaffTotalData.getEr5() + preCheckPercentRecord.getEr5());
        preCheckPercentStaffTotalData.setEr6(preCheckPercentStaffTotalData.getEr6() + preCheckPercentRecord.getEr6());
        preCheckPercentStaffTotalData
            .setEr3456(preCheckPercentStaffTotalData.getEr3456() + preCheckPercentRecord.getEr3456());
        preCheckPercentStaffTotalData
            .setOtherAmount(preCheckPercentStaffTotalData.getOtherAmount() + preCheckPercentRecord.getOtherAmount());
        preCheckPercentStaffTotalData.getDetail().add(preCheckPercentRecord);
      } else {
        preCheckPercentStaffTotalData = new PreCheckPercentStaffTotalData();
        preCheckPercentStaffTotalData.setCopeInspectorID(preCheckPercentRecord.getCopeInspectorID());
        preCheckPercentStaffTotalData.setAmount(preCheckPercentRecord.getAmount());
        preCheckPercentStaffTotalData.setNoReworkAmount(preCheckPercentRecord.getNoReworkAmount());
        preCheckPercentStaffTotalData.setNoReworkScrapAmount(preCheckPercentRecord.getNoReworkScrapAmount());
        preCheckPercentStaffTotalData.setEr3(preCheckPercentRecord.getEr3());
        preCheckPercentStaffTotalData.setEr4(preCheckPercentRecord.getEr4());
        preCheckPercentStaffTotalData.setEr5(preCheckPercentRecord.getEr5());
        preCheckPercentStaffTotalData.setEr6(preCheckPercentRecord.getEr6());
        preCheckPercentStaffTotalData.setEr3456(preCheckPercentRecord.getEr3456());
        preCheckPercentStaffTotalData.setOtherAmount(preCheckPercentRecord.getOtherAmount());
        List<PreCheckPercentRecord> detail = new ArrayList<>();
        detail.add(preCheckPercentRecord);
        preCheckPercentStaffTotalData.setDetail(detail);
        tempMap.put(key, preCheckPercentStaffTotalData);
      }
    });

    List<PreCheckPercentStaffTotalData> preCheckPercentStaffTotalDataList = tempMap.values().stream().map(data -> {
      data.setNoReworkPercent(calcPercent(data.getNoReworkAmount(), data.getAmount()));
      data.setNoReworkScrapPercent(calcPercent(data.getNoReworkScrapAmount(), data.getAmount()));
      data.setEr3456Percent(calcPercent(data.getEr3456(), data.getAmount()));
      data.setEr3Percent(calcPercent(data.getEr3(), data.getAmount()));
      data.setEr4Percent(calcPercent(data.getEr4(), data.getAmount()));
      data.setEr5Percent(calcPercent(data.getEr5(), data.getAmount()));
      data.setEr6Percent(calcPercent(data.getEr6(), data.getAmount()));
      data.setOtherPercent(calcPercent(data.getOtherAmount(), data.getAmount()));
      return data;
    }).collect(Collectors.toList());

    preCheckPercentStaffTopData.setList(preCheckPercentStaffTotalDataList);
    preCheckPercentStaffTopData.setNoReworkPercent(
        calcPercent(preCheckPercentStaffTopData.getNoReworkAmount(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentStaffTopData.setNoReworkScrapPercent(
        calcPercent(preCheckPercentStaffTopData.getNoReworkScrapAmount(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentStaffTopData
        .setEr3456Percent(calcPercent(preCheckPercentStaffTopData.getEr3456(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentStaffTopData
        .setEr3Percent(calcPercent(preCheckPercentStaffTopData.getEr3(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentStaffTopData
        .setEr4Percent(calcPercent(preCheckPercentStaffTopData.getEr4(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentStaffTopData
        .setEr5Percent(calcPercent(preCheckPercentStaffTopData.getEr5(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentStaffTopData
        .setEr6Percent(calcPercent(preCheckPercentStaffTopData.getEr6(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentStaffTopData
        .setOtherPercent(calcPercent(preCheckPercentStaffTopData.getOtherAmount(), preCheckPercentStaffTopData.getAmount()));
    preCheckPercentData.setStaff(preCheckPercentStaffTopData);
  }

  private void fillDesign(Map<String, Object> parameterMap, PreCheckPercentData preCheckPercentData) {
    List<PreCheckPercentRecord> preCheckPercentRecordList = queryResultList(QC_STAT_SMALL_PRECHECK_PERCENT_DESIGN, parameterMap,
        PreCheckPercentRecord.class);
    PreCheckPercentDesignTopData preCheckPercentDesignTopData = new PreCheckPercentDesignTopData();
    Map<String, PreCheckPercentDesignTotalData> designMap = new LinkedHashMap<>();
    Map<String, PreCheckPercentDateTotalData> dateMap = new LinkedHashMap<>();
    preCheckPercentRecordList.forEach(record -> {
      String key = record.getDesign();
      String dateKey = key + DateFormatUtils.format(record.getOpeDT(), "yyyy/M/d");
      PreCheckPercentDesignTotalData totalData;
      PreCheckPercentDateTotalData totalDateData;
      preCheckPercentDesignTopData.setAmount(preCheckPercentDesignTopData.getAmount() + record.getAmount());
      preCheckPercentDesignTopData
          .setNoReworkAmount(preCheckPercentDesignTopData.getNoReworkAmount() + record.getNoReworkAmount());
      preCheckPercentDesignTopData
          .setNoReworkScrapAmount(preCheckPercentDesignTopData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
      preCheckPercentDesignTopData.setEr3(preCheckPercentDesignTopData.getEr3() + record.getEr3());
      preCheckPercentDesignTopData.setEr4(preCheckPercentDesignTopData.getEr4() + record.getEr4());
      preCheckPercentDesignTopData.setEr5(preCheckPercentDesignTopData.getEr5() + record.getEr5());
      preCheckPercentDesignTopData.setEr6(preCheckPercentDesignTopData.getEr6() + record.getEr6());
      preCheckPercentDesignTopData.setEr3456(preCheckPercentDesignTopData.getEr3456() + record.getEr3456());
      preCheckPercentDesignTopData.setOtherAmount(preCheckPercentDesignTopData.getOtherAmount() + record.getOtherAmount());
      if (designMap.containsKey(key)) {
        totalData = designMap.get(key);
        totalData.setAmount(totalData.getAmount() + record.getAmount());
        totalData.setNoReworkAmount(totalData.getNoReworkAmount() + record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(totalData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
        totalData.setEr3(totalData.getEr3() + record.getEr3());
        totalData.setEr4(totalData.getEr4() + record.getEr4());
        totalData.setEr5(totalData.getEr5() + record.getEr5());
        totalData.setEr6(totalData.getEr6() + record.getEr6());
        totalData.setEr3456(totalData.getEr3456() + record.getEr3456());
        totalData.setOtherAmount(totalData.getOtherAmount() + record.getOtherAmount());

        if (dateMap.containsKey(dateKey)) {
          totalDateData = dateMap.get(dateKey);
          totalDateData.setAmount(totalDateData.getAmount() + record.getAmount());
          totalDateData.setNoReworkAmount(totalDateData.getNoReworkAmount() + record.getNoReworkAmount());
          totalDateData.setNoReworkScrapAmount(totalDateData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
          totalDateData.setEr3(totalDateData.getEr3() + record.getEr3());
          totalDateData.setEr4(totalDateData.getEr4() + record.getEr4());
          totalDateData.setEr5(totalDateData.getEr5() + record.getEr5());
          totalDateData.setEr6(totalDateData.getEr6() + record.getEr6());
          totalDateData.setEr3456(totalDateData.getEr3456() + record.getEr3456());
          totalDateData.setOtherAmount(totalDateData.getOtherAmount() + record.getOtherAmount());
          totalDateData.getDetail().add(record);
        } else {
          totalDateData = new PreCheckPercentDateTotalData();
          totalDateData.setOpeDT(record.getOpeDT());
          totalDateData.setAmount(record.getAmount());
          totalDateData.setNoReworkAmount(record.getNoReworkAmount());
          totalDateData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
          totalDateData.setEr3(record.getEr3());
          totalDateData.setEr4(record.getEr4());
          totalDateData.setEr5(record.getEr5());
          totalDateData.setEr6(record.getEr6());
          totalDateData.setEr3456(record.getEr3456());
          totalDateData.setOtherAmount(record.getOtherAmount());
          List<PreCheckPercentRecord> detail = new ArrayList<>();
          detail.add(record);
          totalDateData.setDetail(detail);
          totalData.getData().add(totalDateData);
          dateMap.put(dateKey, totalDateData);
        }
      } else {
        totalData = new PreCheckPercentDesignTotalData();
        totalData.setDesign(record.getDesign());
        totalData.setAmount(record.getAmount());
        totalData.setNoReworkAmount(record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalData.setEr3(record.getEr3());
        totalData.setEr4(record.getEr4());
        totalData.setEr5(record.getEr5());
        totalData.setEr6(record.getEr6());
        totalData.setEr3456(record.getEr3456());
        totalData.setOtherAmount(record.getOtherAmount());
        List<PreCheckPercentDateTotalData> date = new ArrayList<>();
        totalDateData = new PreCheckPercentDateTotalData();
        totalDateData.setOpeDT(record.getOpeDT());
        totalDateData.setAmount(record.getAmount());
        totalDateData.setNoReworkAmount(record.getNoReworkAmount());
        totalDateData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalDateData.setEr3(record.getEr3());
        totalDateData.setEr4(record.getEr4());
        totalDateData.setEr5(record.getEr5());
        totalDateData.setEr6(record.getEr6());
        totalDateData.setEr3456(record.getEr3456());
        totalDateData.setOtherAmount(record.getOtherAmount());
        List<PreCheckPercentRecord> detail = new ArrayList<>();
        detail.add(record);
        totalDateData.setDetail(detail);
        date.add(totalDateData);
        totalData.setData(date);
        dateMap.put(dateKey, totalDateData);
        designMap.put(key, totalData);
      }
    });
    preCheckPercentDesignTopData.setList(designMap.values().stream().map(data -> {
      data.setNoReworkPercent(calcPercent(data.getNoReworkAmount(), data.getAmount()));
      data.setNoReworkScrapPercent(calcPercent(data.getNoReworkScrapAmount(), data.getAmount()));
      data.setEr3456Percent(calcPercent(data.getEr3456(), data.getAmount()));
      data.setEr3Percent(calcPercent(data.getEr3(), data.getAmount()));
      data.setEr4Percent(calcPercent(data.getEr4(), data.getAmount()));
      data.setEr5Percent(calcPercent(data.getEr5(), data.getAmount()));
      data.setEr6Percent(calcPercent(data.getEr6(), data.getAmount()));
      data.setOtherPercent(calcPercent(data.getOtherAmount(), data.getAmount()));
      data.getData().forEach(total -> {
        total.setNoReworkPercent(calcPercent(total.getNoReworkAmount(), total.getAmount()));
        total.setNoReworkScrapPercent(calcPercent(total.getNoReworkScrapAmount(), total.getAmount()));
        total.setEr3456Percent(calcPercent(total.getEr3456(), total.getAmount()));
        total.setEr3Percent(calcPercent(total.getEr3(), total.getAmount()));
        total.setEr4Percent(calcPercent(total.getEr4(), total.getAmount()));
        total.setEr5Percent(calcPercent(total.getEr5(), total.getAmount()));
        total.setEr6Percent(calcPercent(total.getEr6(), total.getAmount()));
        total.setOtherPercent(calcPercent(total.getOtherAmount(), total.getAmount()));
      });
      return data;
    }).collect(Collectors.toList()));
    preCheckPercentDesignTopData.setNoReworkPercent(
        calcPercent(preCheckPercentDesignTopData.getNoReworkAmount(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentDesignTopData.setNoReworkScrapPercent(
        calcPercent(preCheckPercentDesignTopData.getNoReworkScrapAmount(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentDesignTopData
        .setEr3456Percent(calcPercent(preCheckPercentDesignTopData.getEr3456(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentDesignTopData
        .setEr3Percent(calcPercent(preCheckPercentDesignTopData.getEr3(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentDesignTopData
        .setEr4Percent(calcPercent(preCheckPercentDesignTopData.getEr4(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentDesignTopData
        .setEr5Percent(calcPercent(preCheckPercentDesignTopData.getEr5(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentDesignTopData
        .setEr6Percent(calcPercent(preCheckPercentDesignTopData.getEr6(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentDesignTopData
        .setOtherPercent(calcPercent(preCheckPercentDesignTopData.getOtherAmount(), preCheckPercentDesignTopData.getAmount()));
    preCheckPercentData.setDesign(preCheckPercentDesignTopData);
  }

  private void fillDate(Map<String, Object> parameterMap, PreCheckPercentData preCheckPercentData) {
    List<PreCheckPercentRecord> preCheckPercentRecordList = queryResultList(QC_STAT_SMALL_PRECHECK_PERCENT, parameterMap,
        PreCheckPercentRecord.class);
    PreCheckPercentDateTopData preCheckPercentDateTopData = new PreCheckPercentDateTopData();
    Map<String, PreCheckPercentDateTotalData> tempMap = new LinkedHashMap<>();
    preCheckPercentRecordList.forEach(preCheckPercentRecord -> {
      preCheckPercentDateTopData.setAmount(preCheckPercentDateTopData.getAmount() + preCheckPercentRecord.getAmount());
      preCheckPercentDateTopData
          .setNoReworkAmount(preCheckPercentDateTopData.getNoReworkAmount() + preCheckPercentRecord.getNoReworkAmount());
      preCheckPercentDateTopData.setNoReworkScrapAmount(
          preCheckPercentDateTopData.getNoReworkScrapAmount() + preCheckPercentRecord.getNoReworkScrapAmount());
      preCheckPercentDateTopData.setEr3(preCheckPercentDateTopData.getEr3() + preCheckPercentRecord.getEr3());
      preCheckPercentDateTopData.setEr4(preCheckPercentDateTopData.getEr4() + preCheckPercentRecord.getEr4());
      preCheckPercentDateTopData.setEr5(preCheckPercentDateTopData.getEr5() + preCheckPercentRecord.getEr5());
      preCheckPercentDateTopData.setEr6(preCheckPercentDateTopData.getEr6() + preCheckPercentRecord.getEr6());
      preCheckPercentDateTopData.setEr3456(preCheckPercentDateTopData.getEr3456() + preCheckPercentRecord.getEr3456());
      preCheckPercentDateTopData
          .setOtherAmount(preCheckPercentDateTopData.getOtherAmount() + preCheckPercentRecord.getOtherAmount());

      String key = DateFormatUtils.format(preCheckPercentRecord.getOpeDT(), "yyyy/M/d");
      PreCheckPercentDateTotalData preCheckPercentDateTotalData;
      if (tempMap.containsKey(key)) {
        preCheckPercentDateTotalData = tempMap.get(key);
        preCheckPercentDateTotalData
            .setAmount(preCheckPercentDateTotalData.getAmount() + preCheckPercentRecord.getAmount());
        preCheckPercentDateTotalData.setNoReworkAmount(
            preCheckPercentDateTotalData.getNoReworkAmount() + preCheckPercentRecord.getNoReworkAmount());
        preCheckPercentDateTotalData.setNoReworkScrapAmount(
            preCheckPercentDateTotalData.getNoReworkScrapAmount() + preCheckPercentRecord.getNoReworkScrapAmount());
        preCheckPercentDateTotalData.setEr3(preCheckPercentDateTotalData.getEr3() + preCheckPercentRecord.getEr3());
        preCheckPercentDateTotalData.setEr4(preCheckPercentDateTotalData.getEr4() + preCheckPercentRecord.getEr4());
        preCheckPercentDateTotalData.setEr5(preCheckPercentDateTotalData.getEr5() + preCheckPercentRecord.getEr5());
        preCheckPercentDateTotalData.setEr6(preCheckPercentDateTotalData.getEr6() + preCheckPercentRecord.getEr6());
        preCheckPercentDateTotalData
            .setEr3456(preCheckPercentDateTotalData.getEr3456() + preCheckPercentRecord.getEr3456());
        preCheckPercentDateTotalData
            .setOtherAmount(preCheckPercentDateTotalData.getOtherAmount() + preCheckPercentRecord.getOtherAmount());
        preCheckPercentDateTotalData.getDetail().add(preCheckPercentRecord);
      } else {
        preCheckPercentDateTotalData = new PreCheckPercentDateTotalData();
        preCheckPercentDateTotalData.setOpeDT(preCheckPercentRecord.getOpeDT());
        preCheckPercentDateTotalData.setAmount(preCheckPercentRecord.getAmount());
        preCheckPercentDateTotalData.setNoReworkAmount(preCheckPercentRecord.getNoReworkAmount());
        preCheckPercentDateTotalData.setNoReworkScrapAmount(preCheckPercentRecord.getNoReworkScrapAmount());
        preCheckPercentDateTotalData.setEr3(preCheckPercentRecord.getEr3());
        preCheckPercentDateTotalData.setEr4(preCheckPercentRecord.getEr4());
        preCheckPercentDateTotalData.setEr5(preCheckPercentRecord.getEr5());
        preCheckPercentDateTotalData.setEr6(preCheckPercentRecord.getEr6());
        preCheckPercentDateTotalData.setEr3456(preCheckPercentRecord.getEr3456());
        preCheckPercentDateTotalData.setOtherAmount(preCheckPercentRecord.getOtherAmount());
        List<PreCheckPercentRecord> detail = new ArrayList<>();
        detail.add(preCheckPercentRecord);
        preCheckPercentDateTotalData.setDetail(detail);
        tempMap.put(key, preCheckPercentDateTotalData);
      }
    });

    List<PreCheckPercentDateTotalData> preCheckPercentDateTotalDataList = tempMap.values().stream().map(data -> {
      data.setNoReworkPercent(calcPercent(data.getNoReworkAmount(), data.getAmount()));
      data.setNoReworkScrapPercent(calcPercent(data.getNoReworkScrapAmount(), data.getAmount()));
      data.setEr3456Percent(calcPercent(data.getEr3456(), data.getAmount()));
      data.setEr3Percent(calcPercent(data.getEr3(), data.getAmount()));
      data.setEr4Percent(calcPercent(data.getEr4(), data.getAmount()));
      data.setEr5Percent(calcPercent(data.getEr5(), data.getAmount()));
      data.setEr6Percent(calcPercent(data.getEr6(), data.getAmount()));
      data.setOtherPercent(calcPercent(data.getOtherAmount(), data.getAmount()));
      return data;
    }).collect(Collectors.toList());

    preCheckPercentDateTopData.setList(preCheckPercentDateTotalDataList);
    preCheckPercentDateTopData.setNoReworkPercent(
        calcPercent(preCheckPercentDateTopData.getNoReworkAmount(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentDateTopData.setNoReworkScrapPercent(
        calcPercent(preCheckPercentDateTopData.getNoReworkScrapAmount(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentDateTopData
        .setEr3456Percent(calcPercent(preCheckPercentDateTopData.getEr3456(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentDateTopData
        .setEr3Percent(calcPercent(preCheckPercentDateTopData.getEr3(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentDateTopData
        .setEr4Percent(calcPercent(preCheckPercentDateTopData.getEr4(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentDateTopData
        .setEr5Percent(calcPercent(preCheckPercentDateTopData.getEr5(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentDateTopData
        .setEr6Percent(calcPercent(preCheckPercentDateTopData.getEr6(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentDateTopData
        .setOtherPercent(calcPercent(preCheckPercentDateTopData.getOtherAmount(), preCheckPercentDateTopData.getAmount()));
    preCheckPercentData.setDate(preCheckPercentDateTopData);
  }

  private BigDecimal calcPercent(Integer a, Integer b) {
    if (b == null || b == 0) {
      return BigDecimal.ZERO;
    }
    return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(b), 2, RoundingMode.HALF_UP);
  }

  public FinalCheckPercentData finalCheckPercent(Map<String, Object> parameterMap) {
    changeParameterMap(parameterMap);
    FinalCheckPercentData finalCheckPercentData = new FinalCheckPercentData();
    fillDateFinal(parameterMap, finalCheckPercentData);
    fillDesignFinal(parameterMap, finalCheckPercentData);
    fillStaffFinal(parameterMap, finalCheckPercentData);
    return finalCheckPercentData;
  }

  private void fillStaffFinal(Map<String, Object> parameterMap, FinalCheckPercentData finalCheckPercentData) {
    List<FinalCheckPercentRecord> recordList = queryResultList(QC_STAT_SMALL_FINALCHECK_PERCENT, parameterMap,
        FinalCheckPercentRecord.class);
    FinalCheckPercentStaffTopData topData = new FinalCheckPercentStaffTopData();
    Map<String, FinalCheckPercentStaffTotalData> tempMap = new LinkedHashMap<>();
    recordList.forEach(record -> {
      topData.setAmount(topData.getAmount() + record.getAmount());
      topData.setNoReworkAmount(topData.getNoReworkAmount() + record.getNoReworkAmount());
      topData.setNoReworkScrapAmount(topData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
      topData.setNoScrapAmount(topData.getNoScrapAmount() + record.getNoScrapAmount());
      topData.setOtherAmount(topData.getOtherAmount() + record.getOtherAmount());

      String key = record.getCopeInspectorID();
      FinalCheckPercentStaffTotalData totalData;
      if (tempMap.containsKey(key)) {
        totalData = tempMap.get(key);
        totalData.setAmount(totalData.getAmount() + record.getAmount());
        totalData.setNoReworkAmount(totalData.getNoReworkAmount() + record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(totalData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
        totalData.setNoScrapAmount(totalData.getNoScrapAmount() + record.getNoScrapAmount());
        totalData.setOtherAmount(totalData.getOtherAmount() + record.getOtherAmount());
        totalData.getDetail().add(record);
      } else {
        totalData = new FinalCheckPercentStaffTotalData();
        totalData.setCopeInspectorID(record.getCopeInspectorID());
        totalData.setAmount(record.getAmount());
        totalData.setNoReworkAmount(record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalData.setNoScrapAmount(record.getNoScrapAmount());
        totalData.setOtherAmount(record.getOtherAmount());
        List<FinalCheckPercentRecord> detail = new ArrayList<>();
        detail.add(record);
        totalData.setDetail(detail);
        tempMap.put(key, totalData);
      }
    });

    List<FinalCheckPercentStaffTotalData> totalDataList = tempMap.values().stream().map(data -> {
      data.setNoReworkPercent(calcPercent(data.getNoReworkAmount(), data.getAmount()));
      data.setNoReworkScrapPercent(calcPercent(data.getNoReworkScrapAmount(), data.getAmount()));
      data.setNoScrapPercent(calcPercent(data.getNoScrapAmount(), data.getAmount()));
      data.setOtherPercent(calcPercent(data.getOtherAmount(), data.getAmount()));
      return data;
    }).collect(Collectors.toList());

    topData.setList(totalDataList);
    topData.setNoReworkPercent(
        calcPercent(topData.getNoReworkAmount(), topData.getAmount()));
    topData.setNoReworkScrapPercent(
        calcPercent(topData.getNoReworkScrapAmount(), topData.getAmount()));
    topData
        .setNoScrapPercent(calcPercent(topData.getNoScrapAmount(), topData.getAmount()));
    topData
        .setOtherPercent(calcPercent(topData.getOtherAmount(), topData.getAmount()));
    finalCheckPercentData.setStaff(topData);
  }

  private void fillDesignFinal(Map<String, Object> parameterMap, FinalCheckPercentData finalCheckPercentData) {
    List<FinalCheckPercentRecord> finalCheckPercentRecordList = queryResultList(QC_STAT_SMALL_FINALCHECK_PERCENT_DESIGN,
        parameterMap,
        FinalCheckPercentRecord.class);
    FinalCheckPercentDesignTopData topData = new FinalCheckPercentDesignTopData();
    Map<String, FinalCheckPercentDesignTotalData> designMap = new LinkedHashMap<>();
    Map<String, FinalCheckPercentDateTotalData> dateMap = new LinkedHashMap<>();
    finalCheckPercentRecordList.forEach(record -> {
      String key = record.getDesign();
      String dateKey = key + DateFormatUtils.format(record.getOpeDT(), "yyyy/M/d");
      FinalCheckPercentDesignTotalData totalData;
      FinalCheckPercentDateTotalData totalDateData;
      topData.setAmount(topData.getAmount() + record.getAmount());
      topData
          .setNoReworkAmount(topData.getNoReworkAmount() + record.getNoReworkAmount());
      topData
          .setNoReworkScrapAmount(topData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
      topData.setNoScrapAmount(topData.getNoScrapAmount() + record.getNoScrapAmount());
      topData.setOtherAmount(topData.getOtherAmount() + record.getOtherAmount());
      if (designMap.containsKey(key)) {
        totalData = designMap.get(key);
        totalData.setAmount(totalData.getAmount() + record.getAmount());
        totalData.setNoReworkAmount(totalData.getNoReworkAmount() + record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(totalData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
        totalData.setNoScrapAmount(totalData.getNoScrapAmount() + record.getNoScrapAmount());
        totalData.setOtherAmount(totalData.getOtherAmount() + record.getOtherAmount());

        if (dateMap.containsKey(dateKey)) {
          totalDateData = dateMap.get(dateKey);
          totalDateData.setAmount(totalDateData.getAmount() + record.getAmount());
          totalDateData.setNoReworkAmount(totalDateData.getNoReworkAmount() + record.getNoReworkAmount());
          totalDateData.setNoReworkScrapAmount(totalDateData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
          totalDateData.setNoScrapAmount(totalDateData.getNoScrapAmount() + record.getNoScrapAmount());
          totalDateData.setOtherAmount(totalDateData.getOtherAmount() + record.getOtherAmount());
          totalDateData.getDetail().add(record);
        } else {
          totalDateData = new FinalCheckPercentDateTotalData();
          totalDateData.setOpeDT(record.getOpeDT());
          totalDateData.setAmount(record.getAmount());
          totalDateData.setNoReworkAmount(record.getNoReworkAmount());
          totalDateData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
          totalDateData.setNoScrapAmount(record.getNoScrapAmount());
          totalDateData.setOtherAmount(record.getOtherAmount());
          List<FinalCheckPercentRecord> detail = new ArrayList<>();
          detail.add(record);
          totalDateData.setDetail(detail);
          totalData.getData().add(totalDateData);
          dateMap.put(dateKey, totalDateData);
        }
      } else {
        totalData = new FinalCheckPercentDesignTotalData();
        totalData.setDesign(record.getDesign());
        totalData.setAmount(record.getAmount());
        totalData.setNoReworkAmount(record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalData.setNoScrapAmount(record.getNoScrapAmount());
        totalData.setOtherAmount(record.getOtherAmount());
        List<FinalCheckPercentDateTotalData> date = new ArrayList<>();
        totalDateData = new FinalCheckPercentDateTotalData();
        totalDateData.setOpeDT(record.getOpeDT());
        totalDateData.setAmount(record.getAmount());
        totalDateData.setNoReworkAmount(record.getNoReworkAmount());
        totalDateData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalDateData.setNoScrapAmount(record.getNoScrapAmount());
        totalDateData.setOtherAmount(record.getOtherAmount());
        List<FinalCheckPercentRecord> detail = new ArrayList<>();
        detail.add(record);
        totalDateData.setDetail(detail);
        date.add(totalDateData);
        totalData.setData(date);
        dateMap.put(dateKey, totalDateData);
        designMap.put(key, totalData);
      }
    });
    topData.setList(designMap.values().stream().map(data -> {
      data.setNoReworkPercent(calcPercent(data.getNoReworkAmount(), data.getAmount()));
      data.setNoReworkScrapPercent(calcPercent(data.getNoReworkScrapAmount(), data.getAmount()));
      data.setNoScrapPercent(calcPercent(data.getNoScrapAmount(), data.getAmount()));
      data.setOtherPercent(calcPercent(data.getOtherAmount(), data.getAmount()));
      data.getData().forEach(total -> {
        total.setNoReworkPercent(calcPercent(total.getNoReworkAmount(), total.getAmount()));
        total.setNoReworkScrapPercent(calcPercent(total.getNoReworkScrapAmount(), total.getAmount()));
        total.setNoScrapPercent(calcPercent(total.getNoScrapAmount(), total.getAmount()));
        total.setOtherPercent(calcPercent(total.getOtherAmount(), total.getAmount()));
      });
      return data;
    }).collect(Collectors.toList()));
    topData.setNoReworkPercent(calcPercent(topData.getNoReworkAmount(), topData.getAmount()));
    topData.setNoReworkScrapPercent(calcPercent(topData.getNoReworkScrapAmount(), topData.getAmount()));
    topData.setNoScrapPercent(calcPercent(topData.getNoScrapAmount(), topData.getAmount()));
    topData.setOtherPercent(calcPercent(topData.getOtherAmount(), topData.getAmount()));
    finalCheckPercentData.setDesign(topData);
  }

  private void fillDateFinal(Map<String, Object> parameterMap, FinalCheckPercentData finalCheckPercentData) {
    List<FinalCheckPercentRecord> finalCheckPercentRecordList = queryResultList(QC_STAT_SMALL_FINALCHECK_PERCENT, parameterMap,
        FinalCheckPercentRecord.class);
    FinalCheckPercentDateTopData finalCheckPercentDateTopData = new FinalCheckPercentDateTopData();
    Map<String, FinalCheckPercentDateTotalData> tempMap = new LinkedHashMap<>();
    finalCheckPercentRecordList.forEach(record -> {
      finalCheckPercentDateTopData.setAmount(finalCheckPercentDateTopData.getAmount() + record.getAmount());
      finalCheckPercentDateTopData
          .setNoReworkAmount(finalCheckPercentDateTopData.getNoReworkAmount() + record.getNoReworkAmount());
      finalCheckPercentDateTopData.setNoReworkScrapAmount(
          finalCheckPercentDateTopData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
      finalCheckPercentDateTopData
          .setNoScrapAmount(finalCheckPercentDateTopData.getNoScrapAmount() + record.getNoScrapAmount());
      finalCheckPercentDateTopData
          .setOtherAmount(finalCheckPercentDateTopData.getOtherAmount() + record.getOtherAmount());

      String key = DateFormatUtils.format(record.getOpeDT(), "yyyy/M/d");
      FinalCheckPercentDateTotalData totalData;
      if (tempMap.containsKey(key)) {
        totalData = tempMap.get(key);
        totalData
            .setAmount(totalData.getAmount() + record.getAmount());
        totalData.setNoReworkAmount(
            totalData.getNoReworkAmount() + record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(
            totalData.getNoReworkScrapAmount() + record.getNoReworkScrapAmount());
        totalData.setNoScrapAmount(totalData.getNoScrapAmount() + record.getNoScrapAmount());
        totalData
            .setOtherAmount(totalData.getOtherAmount() + record.getOtherAmount());
        totalData.getDetail().add(record);
      } else {
        totalData = new FinalCheckPercentDateTotalData();
        totalData.setOpeDT(record.getOpeDT());
        totalData.setAmount(record.getAmount());
        totalData.setNoReworkAmount(record.getNoReworkAmount());
        totalData.setNoReworkScrapAmount(record.getNoReworkScrapAmount());
        totalData.setNoScrapAmount(record.getNoScrapAmount());
        totalData.setOtherAmount(record.getOtherAmount());
        List<FinalCheckPercentRecord> detail = new ArrayList<>();
        detail.add(record);
        totalData.setDetail(detail);
        tempMap.put(key, totalData);
      }
    });

    List<FinalCheckPercentDateTotalData> totalDataList = tempMap.values().stream().map(data -> {
      data.setNoReworkPercent(calcPercent(data.getNoReworkAmount(), data.getAmount()));
      data.setNoReworkScrapPercent(calcPercent(data.getNoReworkScrapAmount(), data.getAmount()));
      data.setNoScrapPercent(calcPercent(data.getNoScrapAmount(), data.getAmount()));
      data.setOtherPercent(calcPercent(data.getOtherAmount(), data.getAmount()));
      return data;
    }).collect(Collectors.toList());

    finalCheckPercentDateTopData.setList(totalDataList);
    finalCheckPercentDateTopData.setNoReworkPercent(
        calcPercent(finalCheckPercentDateTopData.getNoReworkAmount(), finalCheckPercentDateTopData.getAmount()));
    finalCheckPercentDateTopData.setNoReworkScrapPercent(
        calcPercent(finalCheckPercentDateTopData.getNoReworkScrapAmount(), finalCheckPercentDateTopData.getAmount()));
    finalCheckPercentDateTopData
        .setNoScrapPercent(
            calcPercent(finalCheckPercentDateTopData.getNoScrapAmount(), finalCheckPercentDateTopData.getAmount()));
    finalCheckPercentDateTopData
        .setOtherPercent(calcPercent(finalCheckPercentDateTopData.getOtherAmount(), finalCheckPercentDateTopData.getAmount()));
    finalCheckPercentData.setDate(finalCheckPercentDateTopData);
  }

  public BalancePercentData balancePercent(Map<String, Object> parameterMap) {
    BalancePercentData balancePercentData = new BalancePercentData();
    List<BalancePercentRecord> recordList = queryResultList(QC_STAT_SMALL_BALANCE_PERCENT, parameterMap,
        BalancePercentRecord.class);
    Map<String, BalancePercentTotalData> tempMap = new LinkedHashMap<>();
    recordList.forEach(record -> {
      balancePercentData.setAmount(balancePercentData.getAmount() + record.getAmount());
      balancePercentData.setQualifiedAmount(balancePercentData.getQualifiedAmount() + record.getQualifiedAmount());

      String key = DateFormatUtils.format(record.getOpeDT(), "yyyy/M/d");
      BalancePercentTotalData totalData;
      if (tempMap.containsKey(key)) {
        totalData = tempMap.get(key);
        totalData.setAmount(totalData.getAmount() + record.getAmount());
        totalData.setQualifiedAmount(totalData.getQualifiedAmount() + record.getQualifiedAmount());
        totalData.getDetail().add(record);
      } else {
        totalData = new BalancePercentTotalData();
        totalData.setOpeDT(record.getOpeDT());
        totalData.setAmount(record.getAmount());
        totalData.setQualifiedAmount(record.getQualifiedAmount());
        List<BalancePercentRecord> detail = new ArrayList<>();
        detail.add(record);
        totalData.setDetail(detail);
        tempMap.put(key, totalData);
      }
    });

    List<BalancePercentTotalData> totalDataList = tempMap.values().stream().map(data -> {
      data.setQualifiedPercent(calcPercent(data.getQualifiedAmount(), data.getAmount()));
      return data;
    }).collect(Collectors.toList());

    balancePercentData.setData(totalDataList);
    balancePercentData.setQualifiedPercent(
        calcPercent(balancePercentData.getQualifiedAmount(), balancePercentData.getAmount()));
    return balancePercentData;
  }

  public ReworkPercentData reworkPercent(Map<String, Object> parameterMap, List<String> reworkCode) {
    parameterMap.put("reworkCode", reworkCode);
    ReworkPercentData reworkPercentData = new ReworkPercentData();
    List<ReworkPercentRecord> recordList = queryResultList(QC_STAT_SMALL_REWORK_PERCENT, parameterMap,
        ReworkPercentRecord.class);
    Map<String, ReworkPercentDateData> dateMap = new LinkedHashMap<>();
    Map<String, ReworkPercentCodeData> codeMap = new LinkedHashMap<>();
    recordList.forEach(record -> {
      reworkPercentData.setAmount(reworkPercentData.getAmount() + record.getAmount());
      reworkPercentData.setPassedAmount(reworkPercentData.getPassedAmount() + record.getPassedAmount());
      String key = DateFormatUtils.format(record.getOpeDT(), "yyyy/M/d");
      String codeKey = key + record.getReworkCode();
      ReworkPercentDateData dateData;
      ReworkPercentCodeData codeData;

      if (dateMap.containsKey(key)) {
        dateData = dateMap.get(key);
        dateData.setAmount(dateData.getAmount() + record.getAmount());
        dateData.setPassedAmount(dateData.getPassedAmount() + record.getPassedAmount());
        if (codeMap.containsKey(codeKey)) {
          codeData = codeMap.get(codeKey);
          codeData.getDetail().add(record);
        } else {
          codeData = new ReworkPercentCodeData();
          codeData.setReworkCode(record.getReworkCode());
          List<ReworkPercentRecord> detail = new ArrayList<>();
          detail.add(record);
          codeData.setDetail(detail);
          dateData.getData().add(codeData);
          codeMap.put(codeKey, codeData);
        }
      } else {
        dateData = new ReworkPercentDateData();
        dateData.setOpeDT(record.getOpeDT());
        dateData.setAmount(record.getAmount());
        dateData.setPassedAmount(record.getPassedAmount());
        List<ReworkPercentCodeData> codeList = new ArrayList<>();
        codeData = new ReworkPercentCodeData();
        codeData.setReworkCode(record.getReworkCode());
        List<ReworkPercentRecord> detail = new ArrayList<>();
        detail.add(record);
        codeData.setDetail(detail);
        codeList.add(codeData);
        dateData.setData(codeList);
        codeMap.put(codeKey, codeData);
        dateMap.put(key, dateData);
      }
    });
    reworkPercentData.setList(dateMap.values().stream().map(data -> {
      data.setPassedPercent(calcPercent(data.getPassedAmount(), data.getAmount()));
      return data;
    }).collect(Collectors.toList()));
    reworkPercentData.setPassedPercent(
        calcPercent(reworkPercentData.getPassedAmount(), reworkPercentData.getAmount()));

    return reworkPercentData;
  }

  public List<WheelDetail> wheelDetail(Map<String, Object> parameterMap) {
    parameterMap.remove("endDate");
    List<WheelDetail> wheelDetailList = queryResultList(QC_STAT_SMALL_WHEEL_DETAIL, parameterMap,
        WheelDetail.class);
    return wheelDetailList;
  }

  public ScrapShiftData scrapShift(Map<String, Object> parameterMap, List<String> scrapCode) {
    ScrapShiftData scrapShiftData = new ScrapShiftData();
    fillScrapShiftQcData(parameterMap, scrapShiftData, scrapCode);
    fillScrapShiftModelData(parameterMap, scrapShiftData, scrapCode);
    fillScrapShiftFurnaceData(parameterMap, scrapShiftData, scrapCode);
    fillScrapShiftMachineData(parameterMap, scrapShiftData, scrapCode);
    return scrapShiftData;
  }

  private void fillScrapShiftMachineData(Map<String, Object> parameterMap, ScrapShiftData scrapShiftData,
      List<String> scrapCode) {
    List<ScrapShiftQcTopData> topDataListList = queryResultList(QC_STAT_SCRAP_SHIFT_MACHINE_TOP, parameterMap,
        ScrapShiftQcTopData.class);
    ScrapShiftMachineData machineData = new ScrapShiftMachineData();
    machineData.setTop(topDataListList);
    parameterMap.put("scrapCode", scrapCode);
    List<ScrapShiftMachineRecord> recordList = queryResultList(QC_STAT_SCRAP_SHIFT_MACHINE_DETAIL, parameterMap,
        ScrapShiftMachineRecord.class);
    Map<String, ScrapShiftMachineLeaderData> leaderMap = new LinkedHashMap<>();
    recordList.forEach(record -> {
      String key = record.getType() + record.getInspectorId();
      ScrapShiftMachineLeaderData leaderData;
      if (leaderMap.containsKey(key)) {
        leaderData = leaderMap.get(key);
        leaderData.setScrapAmount(leaderData.getScrapAmount() + record.getScrapAmount());
        leaderData.getDetail().add(record);
      } else {
        leaderData = new ScrapShiftMachineLeaderData();
        leaderData.setType(record.getType());
        leaderData.setInspectorId(record.getInspectorId());
        leaderData.setScrapAmount(record.getScrapAmount());
        List<ScrapShiftMachineRecord> detail = new ArrayList<>();
        detail.add(record);
        leaderData.setDetail(detail);
        leaderMap.put(key, leaderData);
      }
    });
    machineData.setJMachine(
        leaderMap.values().stream().filter(leaderData -> leaderData.getType().equals("jmachine")).collect(Collectors.toList()));
    machineData.setTMachine(
        leaderMap.values().stream().filter(leaderData -> leaderData.getType().equals("tmachine")).collect(Collectors.toList()));
    machineData.setKMachine(
        leaderMap.values().stream().filter(leaderData -> leaderData.getType().equals("kmachine")).collect(Collectors.toList()));
    machineData.setWMachine(
        leaderMap.values().stream().filter(leaderData -> leaderData.getType().equals("wmachine")).collect(Collectors.toList()));
    machineData.setAmount(machineData.getTop().stream().mapToInt(top -> top.getAmount()).sum());
    machineData.setScrapAmount(machineData.getTMachine().stream().mapToInt(data -> data.getScrapAmount()).sum());
    machineData.setScrapPercent(calcPercent(machineData.getScrapAmount(),
        machineData.getScrapAmount() + machineData.getAmount()));
    scrapShiftData.setMachine(machineData);
    parameterMap.remove("scrapCode");
  }

  private void fillScrapShiftFurnaceData(Map<String, Object> parameterMap, ScrapShiftData scrapShiftData,
      List<String> scrapCode) {
    List<ScrapShiftQcTopData> topDataListList = queryResultList(QC_STAT_SCRAP_SHIFT_FURNACE_TOP, parameterMap,
        ScrapShiftQcTopData.class);
    ScrapShiftQcData scrapShiftQcData = new ScrapShiftQcData();
    scrapShiftQcData.setTop(topDataListList);
    parameterMap.put("scrapCode", scrapCode);
    List<ScrapShiftQcRecord> recordList = queryResultList(QC_STAT_SCRAP_SHIFT_FURNACE_DETAIL, parameterMap,
        ScrapShiftQcRecord.class);
    Map<String, ScrapShiftQcLeaderData> leaderMap = new LinkedHashMap<>();
    recordList.forEach(record -> {
      String key = record.getInspectorId();
      ScrapShiftQcLeaderData leaderData;
      if (leaderMap.containsKey(key)) {
        leaderData = leaderMap.get(key);
        leaderData.setScrapAmount(leaderData.getScrapAmount() + record.getScrapAmount());
        leaderData.getDetail().add(record);
      } else {
        leaderData = new ScrapShiftQcLeaderData();
        leaderData.setAmount(record.getAmount());
        leaderData.setInspectorId(record.getInspectorId());
        leaderData.setScrapAmount(record.getScrapAmount());
        List<ScrapShiftQcRecord> detail = new ArrayList<>();
        detail.add(record);
        leaderData.setDetail(detail);
        leaderMap.put(key, leaderData);
      }
    });
    scrapShiftQcData.setData(leaderMap.values().stream().map(data -> {
      data.setScrapPercent(calcPercent(data.getScrapAmount(), data.getScrapAmount() + data.getAmount()));
      return data;
    }).collect(Collectors.toList()));
    scrapShiftQcData.setAmount(scrapShiftQcData.getTop().stream().mapToInt(top -> top.getAmount()).sum());
    scrapShiftQcData.setScrapAmount(scrapShiftQcData.getData().stream().mapToInt(data -> data.getScrapAmount()).sum());
    scrapShiftQcData.setScrapPercent(calcPercent(scrapShiftQcData.getScrapAmount(),
        scrapShiftQcData.getScrapAmount() + scrapShiftQcData.getAmount()));
    scrapShiftData.setFurnace(scrapShiftQcData);
    parameterMap.remove("scrapCode");
  }

  private void fillScrapShiftModelData(Map<String, Object> parameterMap, ScrapShiftData scrapShiftData, List<String> scrapCode) {
    List<ScrapShiftQcTopData> topDataListList = queryResultList(QC_STAT_SCRAP_SHIFT_MODEL_TOP, parameterMap,
        ScrapShiftQcTopData.class);
    ScrapShiftModelData modelData = new ScrapShiftModelData();
    modelData.setTop(topDataListList);
    parameterMap.put("scrapCode", scrapCode);
    List<ScrapShiftModelRecord> recordList = queryResultList(QC_STAT_SCRAP_SHIFT_MODEL_DETAIL, parameterMap,
        ScrapShiftModelRecord.class);
    Map<String, ScrapShiftModelLeaderData> leaderMap = new LinkedHashMap<>();
    recordList.forEach(record -> {
      String key = record.getInspectorId();
      ScrapShiftModelLeaderData leaderData;
      if (leaderMap.containsKey(key)) {
        leaderData = leaderMap.get(key);
        leaderData.setScrapAmount(leaderData.getScrapAmount() + record.getScrapAmount());
        leaderData.getDetail().add(record);
      } else {
        leaderData = new ScrapShiftModelLeaderData();
        leaderData.setAmount(record.getAmount());
        leaderData.setInspectorId(record.getInspectorId());
        leaderData.setScrapAmount(record.getScrapAmount());
        List<ScrapShiftModelRecord> detail = new ArrayList<>();
        detail.add(record);
        leaderData.setDetail(detail);
        leaderMap.put(key, leaderData);
      }
    });
    modelData.setData(leaderMap.values().stream().map(data -> {
      data.setScrapPercent(calcPercent(data.getScrapAmount(), data.getScrapAmount() + data.getAmount()));
      return data;
    }).collect(Collectors.toList()));
    modelData.setAmount(modelData.getTop().stream().mapToInt(top -> top.getAmount()).sum());
    modelData.setScrapAmount(modelData.getData().stream().mapToInt(data -> data.getScrapAmount()).sum());
    modelData.setScrapPercent(calcPercent(modelData.getScrapAmount(),
        modelData.getScrapAmount() + modelData.getAmount()));
    scrapShiftData.setModel(modelData);
    parameterMap.remove("scrapCode");
  }

  private void fillScrapShiftQcData(Map<String, Object> parameterMap, ScrapShiftData scrapShiftData, List<String> scrapCode) {
    List<ScrapShiftQcTopData> topDataListList = queryResultList(QC_STAT_SCRAP_SHIFT_QC_TOP, parameterMap,
        ScrapShiftQcTopData.class);
    ScrapShiftQcData scrapShiftQcData = new ScrapShiftQcData();
    scrapShiftQcData.setTop(topDataListList);
    parameterMap.put("scrapCode", scrapCode);
    List<ScrapShiftQcRecord> recordList = queryResultList(QC_STAT_SCRAP_SHIFT_QC_DETAIL, parameterMap,
        ScrapShiftQcRecord.class);
    Map<String, ScrapShiftQcLeaderData> leaderMap = new LinkedHashMap<>();
    recordList.forEach(record -> {
      String key = record.getInspectorId();
      ScrapShiftQcLeaderData leaderData;
      if (leaderMap.containsKey(key)) {
        leaderData = leaderMap.get(key);
        leaderData.setScrapAmount(leaderData.getScrapAmount() + record.getScrapAmount());
        leaderData.getDetail().add(record);
      } else {
        leaderData = new ScrapShiftQcLeaderData();
        leaderData.setAmount(record.getAmount());
        leaderData.setInspectorId(record.getInspectorId());
        leaderData.setScrapAmount(record.getScrapAmount());
        List<ScrapShiftQcRecord> detail = new ArrayList<>();
        detail.add(record);
        leaderData.setDetail(detail);
        leaderMap.put(key, leaderData);
      }
    });
    scrapShiftQcData.setData(leaderMap.values().stream().map(data -> {
      data.setScrapPercent(calcPercent(data.getScrapAmount(), data.getScrapAmount() + data.getAmount()));
      return data;
    }).collect(Collectors.toList()));
    scrapShiftQcData.setAmount(scrapShiftQcData.getTop().stream().mapToInt(top -> top.getAmount()).sum());
    scrapShiftQcData.setScrapAmount(scrapShiftQcData.getData().stream().mapToInt(data -> data.getScrapAmount()).sum());
    scrapShiftQcData.setScrapPercent(calcPercent(scrapShiftQcData.getScrapAmount(),
        scrapShiftQcData.getScrapAmount() + scrapShiftQcData.getAmount()));
    scrapShiftData.setQc(scrapShiftQcData);
    parameterMap.remove("scrapCode");
  }

  public ReworkShiftData reworkShift(Map<String, Object> parameterMap, List<String> reworkCode) {
    ReworkShiftData reworkShiftData = new ReworkShiftData();
    fillReworkShiftData(parameterMap, reworkShiftData, reworkCode, false);
    fillReworkShiftData(parameterMap, reworkShiftData, reworkCode, true);
    return reworkShiftData;
  }

  private void fillReworkShiftData(Map<String, Object> parameterMap, ReworkShiftData reworkShiftData, List<String> reworkCode,
      boolean isPreCheck) {
    List<ReworkShiftLeaderData> leaderData = queryResultList(
        isPreCheck ? QC_STAT_REWORK_SHIFT_PRECHECK_TOP : QC_STAT_REWORK_SHIFT_FINALCHECK_TOP, parameterMap,
        ReworkShiftLeaderData.class);
    Map<String, ReworkShiftLeaderData> leaderDataMap = new LinkedHashMap<>();
    leaderData.forEach(leader ->
        leaderDataMap.put(leader.getInspectorId(), leader));
    if (reworkCode != null) {
      parameterMap.put("reworkCode", reworkCode);
    }
    List<ReworkShiftRecord> recordList = queryResultList(
        isPreCheck ? QC_STAT_REWORK_SHIFT_PRECHECK_DETAIL : QC_STAT_REWORK_SHIFT_FINALCHECK_DETAIL, parameterMap,
        ReworkShiftRecord.class);
    recordList.stream().filter(record -> StringUtils.isNotEmpty(record.getReworkCode())).forEach(record -> {
      if (leaderDataMap.containsKey(record.getInspectorId())) {
        ReworkShiftLeaderData reworkShiftLeaderData = leaderDataMap.get(record.getInspectorId());
        List<String> list = reworkShiftLeaderData.getDetail() == null ? new ArrayList<>() :
            reworkShiftLeaderData.getDetail();
        list.add(StringUtils.join(record.getReworkCode(), " - ", record.getScrapAmount(), " - ", record.getScrapPercent(), "%"));
        reworkShiftLeaderData.setDetail(list);
        reworkShiftLeaderData.setScrapCodeAmount(reworkShiftLeaderData.getScrapCodeAmount() + record.getScrapAmount());
      }
    });
    leaderData.forEach(leader -> {
      if (StringUtils.isEmpty(leader.getInspectorId())) {
        leader.setDetail(new ArrayList<>());
      }
      leader.setScrapCodePrecheckPercent(calcPercent(leader.getScrapCodeAmount(), leader.getPreAmount()));
    });
    ReworkShiftCheckData checkData = new ReworkShiftCheckData();
    checkData.setData(leaderData);
    checkData.setPreAmount(leaderData.stream().mapToInt(leader -> leader.getPreAmount()).sum());
    checkData.setScrapCodeAmount(leaderData.stream().mapToInt(leader -> leader.getScrapCodeAmount()).sum());
    checkData.setScrapCodePrecheckPercent(calcPercent(checkData.getScrapCodeAmount(), checkData.getPreAmount()));
    if (isPreCheck) {
      reworkShiftData.setPrecheck(checkData);
    } else {
      reworkShiftData.setFinalcheck(checkData);
    }
    parameterMap.remove("reworkCode");
  }

  public ScrapLadleData scrapLadle(Map<String, Object> parameterMap, List<String> scrapCode) {
    ScrapLadleData scrapLadleData = new ScrapLadleData();
    parameterMap.put("scrapCode", scrapCode);
    List<ScrapLadleSeq> scrapList = queryResultList(QC_STAT_SCRAP_LADLE_SCRAP, parameterMap,
        ScrapLadleSeq.class);

    List<ScrapLadleSeqTemp> tempList = queryResultList(QC_STAT_SCRAP_LADLE, parameterMap,
        ScrapLadleSeqTemp.class);

    Map<String, ScrapLadleSeqTemp> xhMap = new HashMap<>();
    Map<String, Integer> xhCache = new HashMap<>();
    tempList.forEach(detail -> {
      int xh = getXh(detail.getLadleRecordKey(), xhCache);
      detail.setXh(xh);
      xhMap.put(detail.getWheelSerial(), detail);
    });

    List<ScrapLadleSeqDetail> detailList =
        scrapList.stream().map(scrap -> {
          ScrapLadleSeqTemp scrapLadleSeqTemp = xhMap.get(scrap.getWheelSerial());
          return ScrapLadleSeqDetail.builder().scrapCode(scrap.getScrapCode()).design(scrap.getDesign())
              .wheelSerial(scrap.getWheelSerial())
              .pourTemp(scrap.getPourTemp()).xh(scrapLadleSeqTemp.getXh()).pourDT(scrapLadleSeqTemp.getPourDT())
              .ladleRecordKey(scrapLadleSeqTemp.getLadleRecordKey())
              .build();
        }).collect(Collectors.toList());

    Map<String, ScrapLadleRecord> scrapLadleRecordMap = new LinkedHashMap<>();
    Map<String, ScrapLadleSeqRecord> seqRecordMap = new LinkedHashMap<>();
    detailList.forEach(detail -> {
      String key = detail.getScrapCode();
      ScrapLadleRecord scrapLadleRecord;
      if (scrapLadleRecordMap.containsKey(key)) {
        fillData(seqRecordMap, detail, key);
      } else {
        scrapLadleRecord = new ScrapLadleRecord();
        scrapLadleRecord.setScrapCode(detail.getScrapCode());
        fillData(seqRecordMap, detail, key);
        scrapLadleRecordMap.put(key, scrapLadleRecord);
      }
    });

    scrapLadleData.setList(scrapLadleRecordMap.values().stream().map(scrapLadleRecord -> {
      scrapLadleRecord.setData(seqRecordMap.values().stream()
          .sorted(Comparator.comparingInt(ScrapLadleSeqRecord::getXh))
          .filter(scrapLadleSeqRecord -> scrapLadleSeqRecord.getScrapCode().equals(scrapLadleRecord.getScrapCode())).collect(
              Collectors.toList()));
      scrapLadleRecord.setTotalAmount(scrapLadleRecord.getData().stream().mapToInt(record -> record.getDetail().size()).sum());
      return scrapLadleRecord;
    }).collect(Collectors.toList()));
    scrapLadleData.setTotalAmount(scrapLadleData.getList().stream().mapToInt(record -> record.getTotalAmount()).sum());
    return scrapLadleData;
  }

  private void fillData(Map<String, ScrapLadleSeqRecord> seqRecordMap, ScrapLadleSeqDetail detail, String key) {
    int xh = detail.getXh();
    String seqKey = key + xh;
    ScrapLadleSeqRecord seqRecord;
    if (seqRecordMap.containsKey(seqKey)) {
      seqRecord = seqRecordMap.get(seqKey);
      seqRecord.getDetail().add(detail);
    } else {
      seqRecord = new ScrapLadleSeqRecord();
      seqRecord.setXh(xh);
      seqRecord.setScrapCode(detail.getScrapCode());
      List<ScrapLadleSeqDetail> details = new ArrayList<>();
      details.add(detail);
      seqRecord.setDetail(details);
      seqRecordMap.put(seqKey, seqRecord);
    }
  }

  private Integer getXh(String ladleRecordKey, Map<String, Integer> xhCache) {
    Integer xh = 1;
    if (xhCache.containsKey(ladleRecordKey)) {
      xh = xhCache.get(ladleRecordKey) + 1;
    }
    xhCache.put(ladleRecordKey, xh);
    return xh;
  }

  public FinalCheckReworkData finalCheckRework(Map<String, Object> parameterMap, List<String> reworkCode) {
    FinalCheckReworkData finalCheckReworkData = new FinalCheckReworkData();
    fillFinalCheckRework(parameterMap, reworkCode, finalCheckReworkData);
    fillPourRework(parameterMap, reworkCode, finalCheckReworkData);
    return finalCheckReworkData;
  }

  private void fillPourRework(Map<String, Object> parameterMap, List<String> reworkCode,
      FinalCheckReworkData finalCheckReworkData) {
    String sql = "SELECT COUNT(final_check_record.wheel_serial) AS final_check_times FROM final_check_record "
        + "INNER JOIN pour_record ON final_check_record.wheel_serial = pour_record.wheel_serial "
        + "WHERE pour_record.cast_date >= '" + parameterMap.get("beginDate")
        + "' AND pour_record.cast_date <= '" + parameterMap.get("endDate") + "'";
    try {
      Map result = jdbcTemplate.queryForMap(sql);
      Integer finalCheckTimes = (Integer) result.get("final_check_times");
      parameterMap.put("reworkCode", reworkCode);
      List<FinalCheckReworkRecord> detailList = queryResultList(QC_STAT_POUR_REWORK_DETAIL, parameterMap,
          FinalCheckReworkRecord.class);
      List<FinalCheckReworkDateDetailRecord> dateDetailRecordList = queryResultList(QC_STAT_POUR_REWORK_DATE_DETAIL,
          parameterMap, FinalCheckReworkDateDetailRecord.class);
      List<FinalCheckReworkDateDetailRecord> lineDetailRecordList = queryResultList(QC_STAT_POUR_REWORK_LINE_DETAIL,
          parameterMap,
          FinalCheckReworkDateDetailRecord.class);
      parameterMap.remove("reworkCode");
      List<FinalCheckReworkDateTotalRecord> totalRecords = queryResultList(QC_STAT_POUR_REWORK_DATE_TOTAL, parameterMap,
          FinalCheckReworkDateTotalRecord.class);
      List<FinalCheckReworkLineTotalRecord> lineTotalRecords = queryResultList(QC_STAT_POUR_REWORK_LINE_TOTAL,
          parameterMap, FinalCheckReworkLineTotalRecord.class);
      FinalCheckTotalData totalData = new FinalCheckTotalData();
      totalData.setCheckTimes(finalCheckTimes);
      List<String> details = detailList.stream().map(detail -> {
        detail.setFinalCheckPercent(calcPercent(detail.getAmount(), finalCheckTimes));
        return StringUtils.join(detail.getReworkCode(), " - ", detail.getAmount(), " - ", detail.getFinalCheckPercent(), "%");
      }).collect(Collectors.toList());
      totalData.setDetail(details);
      FinalCheckDateData finalCheckDateData = new FinalCheckDateData();
      finalCheckDateData.setTotal(totalData);
      finalCheckDateData.setDate(getDateRecord(dateDetailRecordList, totalRecords, lineDetailRecordList, lineTotalRecords));
      finalCheckReworkData.setPour(finalCheckDateData);
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
  }

  private void fillFinalCheckRework(Map<String, Object> parameterMap, List<String> reworkCode,
      FinalCheckReworkData finalCheckReworkData) {
    String sql = "SELECT COUNT(final_check_record.wheel_serial) AS final_check_times FROM final_check_record "
        + "WHERE final_check_record.ope_d_t >= '" + parameterMap.get("beginDate")
        + "' AND final_check_record.ope_d_t <= '" + parameterMap.get("endDate") + "'";
    try {
      Map result = jdbcTemplate.queryForMap(sql);
      Integer finalCheckTimes = (Integer) result.get("final_check_times");
      parameterMap.put("reworkCode", reworkCode);
      List<FinalCheckReworkRecord> detailList = queryResultList(QC_STAT_FINAL_CHECK_REWORK_DETAIL, parameterMap,
          FinalCheckReworkRecord.class);
      List<FinalCheckReworkDateDetailRecord> dateDetailRecordList = queryResultList(QC_STAT_FINAL_CHECK_REWORK_DATE_DETAIL,
          parameterMap, FinalCheckReworkDateDetailRecord.class);
      List<FinalCheckReworkDateDetailRecord> lineDetailRecordList = queryResultList(QC_STAT_FINAL_CHECK_REWORK_LINE_DETAIL,
          parameterMap,
          FinalCheckReworkDateDetailRecord.class);
      parameterMap.remove("reworkCode");
      List<FinalCheckReworkDateTotalRecord> totalRecords = queryResultList(QC_STAT_FINAL_CHECK_REWORK_DATE_TOTAL, parameterMap,
          FinalCheckReworkDateTotalRecord.class);
      List<FinalCheckReworkLineTotalRecord> lineTotalRecords = queryResultList(QC_STAT_FINAL_CHECK_REWORK_LINE_TOTAL,
          parameterMap, FinalCheckReworkLineTotalRecord.class);
      FinalCheckTotalData totalData = new FinalCheckTotalData();
      totalData.setCheckTimes(finalCheckTimes);
      List<String> details = detailList.stream().map(detail -> {
        detail.setFinalCheckPercent(calcPercent(detail.getAmount(), finalCheckTimes));
        return StringUtils.join(detail.getReworkCode(), " - ", detail.getAmount(), " - ", detail.getFinalCheckPercent(), "%");
      }).collect(Collectors.toList());
      totalData.setDetail(details);
      FinalCheckDateData finalCheckDateData = new FinalCheckDateData();
      finalCheckDateData.setTotal(totalData);
      finalCheckDateData
          .setDate(getDateRecord(dateDetailRecordList, totalRecords, lineDetailRecordList, lineTotalRecords));
      finalCheckReworkData.setCheck(finalCheckDateData);
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
  }

  private List<FinalCheckDateRecord> getDateRecord(List<FinalCheckReworkDateDetailRecord> detailRecords,
      List<FinalCheckReworkDateTotalRecord> totalRecords, List<FinalCheckReworkDateDetailRecord> lineDetailRecordList,
      List<FinalCheckReworkLineTotalRecord> lineTotalRecords) {
    List<FinalCheckDateRecord> dateList = new ArrayList<>();
    totalRecords.forEach(record -> {
      FinalCheckDateRecord dateRecord = new FinalCheckDateRecord();
      dateRecord.setDate(record.getOpeDT());
      FinalCheckTotalData finalCheckTotalData = new FinalCheckTotalData();
      finalCheckTotalData.setCheckTimes(record.getFinalCheckTimes());
      List<String> detailList = new ArrayList<>();
      detailRecords.stream().filter(detail -> dateStr(detail.getOpeDT()).equals(dateStr(record.getOpeDT()))).forEach(detail -> {
        detail.setFinalCheckPercent(calcPercent(detail.getAmount(), record.getFinalCheckTimes()));
        detailList.add(StringUtils.join(detail.getReworkCode(), " - ", detail.getAmount(), " - ", detail.getFinalCheckPercent()
            , "%"));
      });
      finalCheckTotalData.setDetail(detailList);
      dateRecord.setTotal(finalCheckTotalData);
      getLineRecord(lineDetailRecordList, dateRecord, lineTotalRecords);
      dateList.add(dateRecord);
    });
    return dateList;
  }

  private void getLineRecord(List<FinalCheckReworkDateDetailRecord> detailRecords, FinalCheckDateRecord dateRecord,
      List<FinalCheckReworkLineTotalRecord> lineTotalRecords) {
    List<FinalCheckLineTotalData> totalDataList =
        lineTotalRecords.stream().filter(record -> dateStr(record.getOpeDT()).equals(dateStr(dateRecord.getDate())))
            .map(record -> {
              FinalCheckLineTotalData totalData = new FinalCheckLineTotalData();
              totalData.setCheckTimes(record.getFinalCheckTimes());
              List<String> detailList = new ArrayList<>();
              detailRecords.stream().filter(
                  detail -> dateStr(detail.getOpeDT()).equals(dateStr(record.getOpeDT())) && detail.getXh()
                      .equals(record.getXh()))
                  .forEach(detail -> {
                    detail.setFinalCheckPercent(calcPercent(detail.getAmount(), record.getFinalCheckTimes()));
                    detailList
                        .add(StringUtils
                            .join(detail.getReworkCode(), " - ", detail.getAmount(), " - ", detail.getFinalCheckPercent()
                                , "%"));
                  });
              totalData.setDetail(detailList);
              totalData.setXh(record.getXh());
              return totalData;
            }).collect(Collectors.toList());

    dateRecord.setLine(totalDataList);
  }

  public MachineReworkData machineRework(Map<String, Object> parameterMap, List<String> reworkCode) {
    MachineReworkData machineReworkData = new MachineReworkData();
    String sql = "SELECT COUNT(t1.amount) AS amount FROM (SELECT COUNT(wheel_serial) AS amount "
        + "FROM t_machine_record "
        + "WHERE t_machine_record.t_s2 = 138 "
        + "AND t_machine_record.ope_d_t >= '" + parameterMap.get("beginDate")
        + "' AND t_machine_record.ope_d_t < '" + parameterMap.get("endDate") + "' "
        + "GROUP BY t_machine_record.wheel_serial) t1";
    try {
      Map result = jdbcTemplate.queryForMap(sql);
      Integer amount = (Integer) result.get("amount");
      machineReworkData.setAmount(amount);
      parameterMap.put("reworkCode", reworkCode);
      List<MachineReworkTotalDetail> detailList = queryResultList(QC_STAT_MACHINE_REWORK_DETAIL, parameterMap,
          MachineReworkTotalDetail.class);
      MachineReworkTotalData totalData = new MachineReworkTotalData();
      totalData.setReworkAmount(detailList.stream().mapToInt(detail -> detail.getReworkAmount()).sum());
      totalData.setFinishAmount(detailList.stream().mapToInt(detail -> detail.getFinishAmount()).sum());
      totalData.setScrapAmount(detailList.stream().mapToInt(detail -> detail.getScrapAmount()).sum());
      detailList.forEach(detail -> {
        detail.setReworkPercent(calcPercent(detail.getReworkAmount(), totalData.getReworkAmount()));
        detail.setReworkFinishPercent(calcPercent(detail.getFinishAmount(), totalData.getFinishAmount()));
        detail.setReworkScrapPercent(calcPercent(detail.getScrapAmount(), totalData.getScrapAmount()));
      });
      totalData.setDetail(detailList);
      machineReworkData.setTotal(totalData);

      List<MachineReworkTotalDetail> dateList = queryResultList(QC_STAT_MACHINE_REWORK_DATE_DETAIL, parameterMap,
          MachineReworkTotalDetail.class);
      Map<String, MachineReworkDateData> map = new LinkedHashMap<>();
      dateList.forEach(detail -> {
        String key = detail.getOpeDT();
        MachineReworkDateData dateData;
        if (map.containsKey(key)) {
          dateData = map.get(key);
          dateData.setReworkAmount(dateData.getReworkAmount() + detail.getReworkAmount());
          dateData.setScrapAmount(dateData.getScrapAmount() + detail.getScrapAmount());
          dateData.setFinishAmount(dateData.getFinishAmount() + detail.getFinishAmount());
          dateData.getDetail().add(detail);
        } else {
          dateData = new MachineReworkDateData();
          dateData.setReworkAmount(detail.getReworkAmount());
          dateData.setScrapAmount(detail.getScrapAmount());
          dateData.setFinishAmount(detail.getFinishAmount());
          dateData.setOpeDT(key);
          List<MachineReworkTotalDetail> dateDetailList = new ArrayList<>();
          dateDetailList.add(detail);
          dateData.setDetail(dateDetailList);
          map.put(key, dateData);
        }
      });
      machineReworkData.setData(map.values().stream().map(data -> {
            data.getDetail().forEach(detail -> {
              detail.setReworkPercent(calcPercent(detail.getReworkAmount(), data.getReworkAmount()));
              detail.setReworkFinishPercent(calcPercent(detail.getFinishAmount(), data.getFinishAmount()));
              detail.setReworkScrapPercent(calcPercent(detail.getScrapAmount(), data.getScrapAmount()));
            });
            return data;
          }
      ).collect(Collectors.toList()));
      machineReworkData
          .setReworkPercent(calcPercent(machineReworkData.getTotal().getReworkAmount(), machineReworkData.getAmount()));
      machineReworkData
          .setReworkFinishPercent(calcPercent(machineReworkData.getTotal().getFinishAmount(), machineReworkData.getAmount()));
      machineReworkData.setReworkScrapPercent(calcPercent(machineReworkData.getTotal().getScrapAmount(),
          machineReworkData.getAmount()));
    } catch (EmptyResultDataAccessException e) {
      log.info("no data");
    }
    return machineReworkData;
  }

  public FinalReworkData finalReworkDetail(Map<String, Object> parameterMap) {
    FinalReworkData finalReworkData = new FinalReworkData();
    fillJMachineData(finalReworkData, parameterMap);
    fillTMachineData(finalReworkData, parameterMap);
    fillKMachineData(finalReworkData, parameterMap);
    return finalReworkData;
  }

  private void fillKMachineData(FinalReworkData finalReworkData, Map<String, Object> parameterMap) {
    FinalReworkMachineData machineData = new FinalReworkMachineData();
    FinalReworkStatData noMachineData = new FinalReworkStatData();
    List<MachineRecord> noMachineDetailList = queryResultList(QC_STAT_FINAL_REWORK_KMACHINE_NO_DETAIL, parameterMap,
        MachineRecord.class);
    noMachineData.setDetail(noMachineDetailList);
    noMachineData.setAmount(noMachineDetailList.size());
    machineData.setNoMachine(noMachineData);

    FinalReworkStatData hasMachineData = new FinalReworkStatData();
    List<MachineRecord> hasMachineDetailList = queryResultList(QC_STAT_FINAL_REWORK_KMACHINE_HAS_DETAIL, parameterMap,
        MachineRecord.class);
    hasMachineData.setDetail(hasMachineDetailList);
    hasMachineData.setAmount(hasMachineDetailList.size());
    machineData.setHasMachine(hasMachineData);

    finalReworkData.setKMachine(machineData);
  }

  private void fillTMachineData(FinalReworkData finalReworkData, Map<String, Object> parameterMap) {
    FinalReworkMachineData machineData = new FinalReworkMachineData();
    FinalReworkStatData noMachineData = new FinalReworkStatData();
    List<MachineRecord> noMachineDetailList = queryResultList(QC_STAT_FINAL_REWORK_TMACHINE_NO_DETAIL, parameterMap,
        MachineRecord.class);
    noMachineData.setDetail(noMachineDetailList);
    noMachineData.setAmount(noMachineDetailList.size());
    machineData.setNoMachine(noMachineData);

    FinalReworkStatData hasMachineData = new FinalReworkStatData();
    List<MachineRecord> hasMachineDetailList = queryResultList(QC_STAT_FINAL_REWORK_TMACHINE_HAS_DETAIL, parameterMap,
        MachineRecord.class);
    hasMachineData.setDetail(hasMachineDetailList);
    hasMachineData.setAmount(hasMachineDetailList.size());
    machineData.setHasMachine(hasMachineData);

    finalReworkData.setTMachine(machineData);
  }

  private void fillJMachineData(FinalReworkData finalReworkData, Map<String, Object> parameterMap) {
    FinalReworkMachineData machineData = new FinalReworkMachineData();
    FinalReworkStatData noMachineData = new FinalReworkStatData();
    List<MachineRecord> noMachineDetailList = queryResultList(QC_STAT_FINAL_REWORK_JMACHINE_NO_DETAIL, parameterMap,
        MachineRecord.class);
    noMachineData.setDetail(noMachineDetailList);
    noMachineData.setAmount(noMachineDetailList.size());
    machineData.setNoMachine(noMachineData);

    FinalReworkStatData hasMachineData = new FinalReworkStatData();
    List<MachineRecord> hasMachineDetailList = queryResultList(QC_STAT_FINAL_REWORK_JMACHINE_HAS_DETAIL, parameterMap,
        MachineRecord.class);
    hasMachineData.setDetail(hasMachineDetailList);
    hasMachineData.setAmount(hasMachineDetailList.size());
    machineData.setHasMachine(hasMachineData);

    finalReworkData.setJMachine(machineData);
  }
}
