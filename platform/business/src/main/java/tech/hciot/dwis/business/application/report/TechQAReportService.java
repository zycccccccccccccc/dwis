package tech.hciot.dwis.business.application.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.domain.HeatRecordRepository;
import tech.hciot.dwis.business.infrastructure.DigitalUtil;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Service
@Slf4j
public class TechQAReportService {

  private static final int GRAPHTE_TYPE_DRAG = 1; // 下箱
  private static final int GRAPHTE_TYPE_COPE = 2; // 上箱

  private static final String DATE_FORMAT = "yyyy/MM/dd";

  private static final String REPORT_NAME = "technology-quality-report";

  @Autowired
  private ReportSqlService reportSqlService;

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private HeatRecordRepository heatRecordRepository;

  @Autowired
  private EntityManager entityManager;

  @Resource
  SqlTemplateParser sqlTemplateParser;

  // 0 技术质量业务查询-汇总统计
  public QAStat totalStat(Map<String, Object> parameterMap) {
    QAStat response = (QAStat) reportSqlService.querySingleResult(REPORT_NAME,
      "0-total-stat",
      parameterMap, QAStat.class);
    List<QAData> dataList = (List<QAData>) reportSqlService.queryResultList(REPORT_NAME,
      "0-total-stat-data",
      parameterMap, QAData.class);
    fillDataDetail(dataList, response);
    response.setData(dataList);
    return response.hasData() ? response : null;
  }

  // 1.1 技术质量业务查询-按天汇总-按月-详情(图表)
  public List<QAStat> statByMonthChart(Map<String, Object> parameterMap) {
    List<QAStat> dataList = (List<QAStat>) reportSqlService.queryResultList(REPORT_NAME,
      "1.1-stat-by-month-chart",
      parameterMap, QAStat.class);
    return dataList.isEmpty() ? null : dataList;
  }

  // 1.13 技术质量业务查询-按天汇总-单日轮号明细"
  public List<SingleWheelDetail> singleWheelDetail(String beginDate) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    List<SingleWheelDetail> detailList = (List<SingleWheelDetail>) reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SINGLE_WHEEL_DETAIL,
      parameterMap, SingleWheelDetail.class);
    return detailList.isEmpty() ? null : detailList;
  }

  // 2.1 技术质量业务查询-按项目-废品代码
  public QAStat statByProjectScrapCode(Map<String, Object> parameterMap) {
    QAStat response = (QAStat) reportSqlService.querySingleResult(REPORT_NAME,
      "2.1-stat-by-project-scrapcode",
      parameterMap, QAStat.class);
    List<QAData> dataList = (List<QAData>) reportSqlService.queryResultList(REPORT_NAME,
      "2.1-stat-by-project-scrapcode-data",
      parameterMap, QAData.class);
    response.setData(dataList);
    return response.hasData() ? response : null;
  }

  // 2.9 技术质量业务查询-按项目-试验码
  public QAStat statByProjectTestCode(Map<String, Object> parameterMap) {
    QAStat response = (QAStat) reportSqlService.querySingleResult(REPORT_NAME,
      "2.9-stat-by-project-testcode",
      parameterMap, QAStat.class);
    List<QAData> dataList = (List<QAData>) reportSqlService.queryResultList(REPORT_NAME,
      "2.9-stat-by-project-testcode-data",
      parameterMap, QAData.class);
    response.setData(dataList);
    for (int i = 0, cnt = dataList.size(); i < cnt; i++) {
      if ("total".equals(dataList.get(i).getScrapCode())) {
        QAData total = dataList.get(i);
        dataList.remove(i);
        dataList.add(total);
        break;
      }
    }
    return dataList.isEmpty() ? null : response;
  }

  // 3 石墨废品
  public JSONObject graphiteScrap(Map<String, Object> parameterMap) {
    parameterMap.put("type", GRAPHTE_TYPE_DRAG);
    JSON drapStatJson = stat(parameterMap, TechQAConstant.SQL_TEMPLATE_GRAPHITE_SCRAP, 12);

    parameterMap.put("type", GRAPHTE_TYPE_COPE);
    JSON copeStatJson = stat(parameterMap, TechQAConstant.SQL_TEMPLATE_GRAPHITE_SCRAP, 12);

    JSONObject graphiteScrapJson = new JSONObject();
    if (drapStatJson != null) {
      graphiteScrapJson.put("drag", drapStatJson);
    }
    if (copeStatJson != null) {
      graphiteScrapJson.put("cope", copeStatJson);
    }
    return ObjectUtils.allNull(drapStatJson, copeStatJson) ? null : graphiteScrapJson;
  }

  // 5 技术质量业务查询-单项废品-图表
  public SingleScrapChart singleScrapChart(String beginDate, String endDate, List<String> scrapCode) {

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    parameterMap.put("scrapCode", scrapCode);

    List<JSONObject> total = reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SINGLE_SCRAP + "-chart-total",
      parameterMap, JSONObject.class);
    List<JSONObject> xh = reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SINGLE_SCRAP + "-chart-xh",
      parameterMap, JSONObject.class);
    List<JSONObject> model = reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SINGLE_SCRAP + "-chart-model",
      parameterMap, JSONObject.class);
    SingleScrapChart singleScrapChart = SingleScrapChart.builder().build();
    if (!total.isEmpty()) {
      singleScrapChart.setTotalChart(generateSingleScrapChart(total, "total"));
    }
    if (!xh.isEmpty()) {
      singleScrapChart.setXhChart(generateSingleScrapChart(xh, "xh"));
    }
    if (!model.isEmpty()) {
      singleScrapChart.setModelChart(generateSingleScrapChart(model, "modelId"));
    }
    return ObjectUtils.allNull(singleScrapChart.getTotalChart(), singleScrapChart.getXhChart(), singleScrapChart.getModelChart())
      ? null : singleScrapChart;
  }

  // 5-生成单项废品图表数据
  private List<Map<String, String>> generateSingleScrapChart(List<JSONObject> dataList, String type) {

    // 先生成所有的key集合
    Set<String> keySet = new TreeSet<>();
    dataList.forEach(data -> {
      if (!type.equals("total")) {
        String key = data.getString(type);
        if (key != null) {
          keySet.add(key);
        }
      }
    });

    // 生成日期和key组成的数据结构
    Map<String, Map<String, String>> chartMap = new TreeMap<>();
    dataList.forEach(data -> {
      String date = data.getString("castDate");
      Map<String, String> dateMap = chartMap.get(date);
      if (dateMap == null) {
        dateMap = new HashMap<>();
        dateMap.put("date", date);
        chartMap.put(date, dateMap);
        for (String key : keySet) {
          dateMap.put(key, "0");
        }
        if (type.equals("total")) {
          dateMap.put("num", "0");
        }
      }
    });

    // 把数据填充进去
    dataList.forEach(data -> {
      String date = data.getString("castDate");
      String key;
      if (type.equals("total")) {
        key = "num";
      } else {
        key = data.getString(type);
      }
      if (key != null) {
        String value = data.getString("scrap");
        Map<String, String> dateMap = chartMap.get(date);
        if (value != null) {
          dateMap.put(key, value);
        }
      }
    });

    List<Map<String, String>> chartList = new ArrayList<>();
    chartMap.entrySet().forEach(entry -> {
      chartList.add(entry.getValue());
    });
    return chartList;
  }

  // 5 技术质量业务查询-单项废品-统计
  public SingleScrapStat singleScrapStat(Map<String, Object> parameterMap) {
    List<SingleScrap> total = reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SINGLE_SCRAP + "-stat-total",
      parameterMap, SingleScrap.class);
    List<SingleScrap> xh = reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SINGLE_SCRAP + "-stat-xh",
      parameterMap, SingleScrap.class);
    List<SingleScrap> model = reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SINGLE_SCRAP + "-stat-model",
      parameterMap, SingleScrap.class);

    SingleScrapStat singleScrapStat = SingleScrapStat.builder()
      .total(total)
      .xh(xh)
      .model(model)
      .build();
    return ObjectUtils.allNull(singleScrapStat.getTotal(), singleScrapStat.getXh(), singleScrapStat.getModel())
      ? null : singleScrapStat;
  }

  // 6 机加返工代码查废品
  public List<MachineScrap> machineScrap(Map<String, Object> parameterMap) {
    List<MachineScrap> machineScrapList = (List<MachineScrap>) reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_MACHINE_SCRAP,
      parameterMap, MachineScrap.class);
    return machineScrapList.isEmpty() ? null : machineScrapList;
  }

  // 7 终检时间查废品
  public List<FinalCheckScrap> finalCheckScrap(Map<String, Object> parameterMap) {
    List<FinalCheckScrap> finalCheckScrapList = (List<FinalCheckScrap>) reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_FINAL_CHECK_SCRAP,
      parameterMap, FinalCheckScrap.class);
    return finalCheckScrapList.isEmpty() ? null : finalCheckScrapList;
  }

  // 8 角度不平衡
  public List<AngleUnbalance> angleUnbalance(Map<String, Object> parameterMap) {
    List<AngleUnbalance> angleUnbalanceList = (List<AngleUnbalance>) reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_ANGLE_UNBALANCE,
      parameterMap, AngleUnbalance.class);
    return angleUnbalanceList.isEmpty() ? null : angleUnbalanceList;
  }

  // 9 机床不平衡
  public List<MachineUnbalance> machineUnbalance(Map<String, Object> parameterMap) {
    List<MachineUnbalanceDetail> machineUnbalanceDetailList
      = (List<MachineUnbalanceDetail>) reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_MACHINE_UNBALANCE,
      parameterMap, MachineUnbalanceDetail.class);
    List<MachineUnbalance> machineUnbalanceList = generateMachineUnbalance(machineUnbalanceDetailList);
    return machineUnbalanceList.isEmpty() ? null : machineUnbalanceList;
  }

  // 9 机床不平衡-详情数据填充至统计数据中
  private List<MachineUnbalance> generateMachineUnbalance(List<MachineUnbalanceDetail> machineUnbalanceDetailList) {
    Map<String, MachineUnbalance> machineUnbalanceMap = new HashMap<>();
    machineUnbalanceDetailList.forEach(detail -> {
      String wheelSerial = detail.getWheelSerial();
      MachineUnbalance machineUnbalance = machineUnbalanceMap.get(wheelSerial);
      if (machineUnbalance == null) {
        machineUnbalanceMap.put(wheelSerial,
          MachineUnbalance.builder().wheelSerial(wheelSerial).detail(new ArrayList<>()).build());
      }
      machineUnbalanceMap.get(wheelSerial).getDetail().add(detail);
    });
    List<MachineUnbalance> machineUnbalanceList = new ArrayList<>();
    machineUnbalanceMap.entrySet().forEach(entry -> {
      machineUnbalanceList.add(entry.getValue());
    });
    return machineUnbalanceList;
  }

  // 10 44AS
  public List<Scrap44AS> scrap44AS(Map<String, Object> parameterMap) {
    List<Scrap44AS> scrap44ASList
      = (List<Scrap44AS>) reportSqlService.queryResultList(REPORT_NAME,
      TechQAConstant.SQL_TEMPLATE_SCRAP_44AS,
      parameterMap, Scrap44AS.class);
    return scrap44ASList.isEmpty() ? null : scrap44ASList;
  }

  // 11 石墨模龄废品统计
  public MoldAgeScrap moldAgeScrap(Map<String, Object> parameterMap) {
    MoldAgeScrap moldAgeScrap = null;
    // 上箱石墨模龄废品详细数据
    List<JSONObject> copeList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
            TechQAConstant.SQL_TEMPLATE_MOLD_AGE_SCRAP_COPE,
            parameterMap, JSONObject.class);

    // 下箱石墨模龄废品详细数据
    List<JSONObject> dragList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
            TechQAConstant.SQL_TEMPLATE_MOLD_AGE_SCRAP_DRAG,
            parameterMap, JSONObject.class);

    // 上箱石墨模龄废品合计数据
    List<JSONObject> copeTotalList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
            TechQAConstant.SQL_TEMPLATE_MOLD_AGE_SCRAP_COPE_TOTAL,
            parameterMap, JSONObject.class);

    // 下箱石墨模龄废品合计数据
    List<JSONObject> dragTotalList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
            TechQAConstant.SQL_TEMPLATE_MOLD_AGE_SCRAP_DRAG_TOTAL,
            parameterMap, JSONObject.class);

    if (copeList.size() > 1 || dragList.size() > 1) {
      moldAgeScrap = new MoldAgeScrap();
      List<String> keyList = new ArrayList<>();
      keyList.add("design");
      keyList.add("jetDate");
      keyList.add("moldAge");
      if (copeList.size() > 1) {
        JSONObject copeResult = assembler.generateProdLevelResult(copeList, keyList);
        moldAgeScrap.setCopeResult(copeResult);
        moldAgeScrap.setCopeTotalList(copeTotalList);
      }
      if (dragList.size() > 1) {
        JSONObject dragResult = assembler.generateProdLevelResult(dragList, keyList);
        moldAgeScrap.setDragResult(dragResult);
        moldAgeScrap.setDragTotalList(dragTotalList);
      }
    }
    return moldAgeScrap;
  }

  // 12 振轮次数
  public List<VibrateWheel> vibrateWheels(Map<String, Object> parameterMap) {
    log.info("report {} - {} begin", REPORT_NAME, TechQAConstant.SQL_TEMPLATE_VIBRATE_WHEELS);
    try {
      List<VibrateWheel> vibrateWheelList = reportSqlService.queryResultList(
              REPORT_NAME, TechQAConstant.SQL_TEMPLATE_VIBRATE_WHEELS, parameterMap, VibrateWheel.class);
      return vibrateWheelList;
    } catch(Exception e) {
      log.error("report " + REPORT_NAME + " error: " + e.getMessage(), e);
    }
    log.info("report {} - {} finish", REPORT_NAME, TechQAConstant.SQL_TEMPLATE_VIBRATE_WHEELS);
    return null;
  }

  // 13 电炉/茶壶包/底注包使用次数
  public Map<String, Integer> furQueryRes(String beginDate, String endDate, Integer furType, Integer furNo, Integer teaBagNo, Integer ladleBagNo) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    if (furType == 1) {
      parameterMap.put("furnace_no", furNo);
    } else if (furType == 2) {
      parameterMap.put("bag_no", teaBagNo);
    } else if (furType == 3) {
      parameterMap.put("ladle_no", ladleBagNo);
    }
    log.info("report {} - {} begin", REPORT_NAME, TechQAConstant.SQL_TEMPLATE_FUR_QUERY_RES);
    String sql = sqlTemplateParser.parseSqlTemplate(REPORT_NAME, TechQAConstant.SQL_TEMPLATE_FUR_QUERY_RES, parameterMap);
    Query query = entityManager.createNativeQuery(sql);
    parameterMap.entrySet().forEach(entry -> {
      if (entry.getValue() != null) {
        query.setParameter(entry.getKey(), entry.getValue());
      }
    });
    query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    try {
      log.info("report {} - {} finish", REPORT_NAME, TechQAConstant.SQL_TEMPLATE_FUR_QUERY_RES);
      return (Map<String, Integer>) query.getSingleResult();
    } catch(Exception e) {
      log.error("report " + REPORT_NAME + " error: " + e.getMessage(), e);
    }
    return null;
  }

  // 14 电炉溶清/脱碳量查询
  public List<DecarbonChartData> decQueryRes(Map<String, Object> parameterMap) {
    log.info("report {} - {} begin", REPORT_NAME, TechQAConstant.SQL_TEMPLATE_DEC_QUERY_RES);
    try {
      List<DecarbonChartData> decarbonChartDataList = reportSqlService.queryResultList(
              REPORT_NAME, TechQAConstant.SQL_TEMPLATE_DEC_QUERY_RES, parameterMap, DecarbonChartData.class);
      return decarbonChartDataList;
    } catch(Exception e) {
      log.error("report " + REPORT_NAME + " error: " + e.getMessage(), e);
    }
    log.info("report {} - {} finish", REPORT_NAME, TechQAConstant.SQL_TEMPLATE_DEC_QUERY_RES);
    return null;
  }

  public JSON stat(Map<String, Object> parameterMap,
                   String sqlTemplate,
                   int maxDataSize) {
    List<QAStat> statList = statList(parameterMap, sqlTemplate);
    if (statList == null || statList.isEmpty()) {
      return null;
    }
    return assembler.generateLevelResult(statList, maxDataSize);
  }

  public JSON stat(Map<String, Object> parameterMap,
                   String sqlTemplate) {
    return stat(parameterMap, sqlTemplate, 20);
  }

  // 通用统计-根据模板和参数生成统计数据
  public List<QAStat> statList(Map<String, Object> parameterMap, String sqlTemplate) {
    List<QAStat> statList = (List<QAStat>) reportSqlService.queryResultList(REPORT_NAME,
      sqlTemplate,
      parameterMap, QAStat.class);
    List<QAData> dataList = (List<QAData>) reportSqlService.queryResultList(REPORT_NAME,
      sqlTemplate + "-data",
      parameterMap, QAData.class);
    fillStatData(statList, dataList);
    return statList;
  }

  // 通用统计-详情数据填充至统计数据中
  private void fillStatData(List<QAStat> statList, List<QAData> dataList) {
    Map<String, QAStat> statMap = new HashMap<>();
    statList.forEach(stat -> {
      stat.setData(new ArrayList<>());
      statMap.put(stat.getStatKey(), stat);
    });
    dataList.forEach(data -> {
      QAStat stat = statMap.get(data.getStatKey());
      if (stat != null) {
        stat.getData().add(data);
      }
    });
    statList.forEach(stat -> {
      fillDataDetail(stat.getData(), stat);
    });
  }

  // 通用统计-填充数据文本框，值为：废品代码 - 确废数量 - 确废数量/(确废数量+成品数量)
  private void fillDataDetail(List<QAData> dataList, QAStat response) {
    dataList.forEach(data -> {
      if (data.getSconfSum() != null && response.getSconfSum() != null && response.getToDock() != null) {
        data.setSconfDockAndSconf(DigitalUtil.percentage(Integer.parseInt(data.getSconfSum()),
                Integer.parseInt(response.getSconfSum()) + Integer.parseInt(response.getToDock())));
        data.setDetail2(data.getScrapCode()
                + " - " + data.getSconfSum()
                + " - " + DigitalUtil.percentage(Integer.parseInt(data.getSconfSum()),
                Integer.parseInt(response.getSconfSum()) + Integer.parseInt(response.getToDock())));
      }
    });
  }

  // 1.2 技术质量业务查询-按天汇总-浇注工长-图表
  public JSON statChart(Map<String, Object> parameterMap, String sqlTemplatePourLeader) {
    List<QAStat> statList = (List<QAStat>) reportSqlService.queryResultList(REPORT_NAME,
      sqlTemplatePourLeader,
      parameterMap, QAStat.class);
    List<String> dateList = StandardTimeUtil.generateDateList((String) parameterMap.get("beginDate"),
      (String) parameterMap.get("endDate"));
    return generatePourLeaderChart(statList, dateList);
  }

  // 生成浇注工长折线图
  private JSON generatePourLeaderChart(List<QAStat> statList, List<String> dateList) {

    // 先生成所有的key集合
    Set<String> keySet = new TreeSet<>();
    statList.forEach(data -> {
      String key = data.getSubTitle();
      if (!"total".equals(key)) {
        keySet.add(key);
      }
    });

    // 生成日期和key组成的数据结构
    Map<String, Map<String, String>> chartMap = new TreeMap<>();
    dateList.forEach(date -> {
      Map<String, String> dateMap = new HashMap<>();
      dateMap.put("date", date);
      for (String key : keySet) {
        dateMap.put(key, "0");
      }
      chartMap.put(date, dateMap);
    });

    // 把数据填充进去
    statList.forEach(data -> {
      String date = data.getMinorTitle();
      date = StandardTimeUtil.slashDate(date);
      String key = data.getSubTitle();
      if (!"total".equals(key)) {
        String value = data.getCastTotal();
        Map<String, String> dateMap = chartMap.get(date);
        dateMap.put(key, value);
      }
    });

    List<Map<String, String>> chartList = new ArrayList<>();
    chartMap.entrySet().forEach(entry -> {
      chartList.add(entry.getValue());
    });
    return (JSON) JSON.toJSON(chartList);
  }

  public List<String> tapSeqList() {
    return heatRecordRepository.findTapSeqList();
  }
}
