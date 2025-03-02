package tech.hciot.dwis.business.application.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.infrastructure.DigitalUtil;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.AllConfirmScrap;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.AllConfirmScrapResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ConfirmScrapResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.MachiningStat;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.PourTimeAndScrap;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatByDay;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatChart;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatReport;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.ProdStatTotal;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelRework;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelReworkResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qa.WheelReworkScrapResult;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAData;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAStat;

@Service
@Slf4j
public class MultiReportQAStatService {

  private static final String REPORT_NAME = "composite-report/qa-stat";

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private ReportSqlService reportSqlService;

  // 3.1 综合查询业务-质量统计-质量统计
  public QAStat qaStat(Map<String, Object> parameterMap) {
    QAStat response = (QAStat) reportSqlService.querySingleResult(REPORT_NAME,
      "3.1-qa-stat",
      parameterMap, QAStat.class);
    List<QAData> dataList = (List<QAData>) reportSqlService.queryResultList(REPORT_NAME,
      "3.1-qa-stat-data",
      parameterMap, QAData.class);
    fillDataDetail(dataList, response);
    response.setData(dataList);
    return response.hasData() ? response : null;
  }

  // 填充数据文本框
  // 数据1文本框值为："废品代码 - 数量 - 数量/预检数量"
  // 数据2文本框值为："废品代码 - 确废数量 - 确废数量/(确废数量+成品数量)"
  // 数据3文本框值为："废品代码 - 数量 - 数量/(废码数量+成品数量)"
  private void fillDataDetail(List<QAData> dataList, QAStat response) {
    dataList.forEach(data -> {
      data.setDetail(data.getScrapCode()
        + " - " + data.getCnt()
        + " - " + DigitalUtil.percentage(Integer.parseInt(data.getCnt()), 
        Integer.parseInt(response.getPreInsp())));
    });
    dataList.forEach(data -> {
      data.setDetail2(data.getScrapCode()
        + " - " + data.getSconfSum()
        + " - " + DigitalUtil.percentage(Integer.parseInt(data.getSconfSum()),
        Integer.parseInt(response.getSconfSum()) + Integer.parseInt(response.getToDock())));
    });
    dataList.forEach(data -> {
      data.setDetail3(data.getScrapCode()
        + " - " + data.getCnt()
        + " - " + DigitalUtil.percentage(Integer.parseInt(data.getCnt()),
        Integer.parseInt(response.getScrapSum()) + Integer.parseInt(response.getToDock())));
    });
  }

  // 3.1 综合查询业务-质量统计-质量统计-详情(报表)
  public JSON qaStatDetail(Map<String, Object> parameterMap) {
    return stat(parameterMap, "3.1-qa-stat-detail", 20);
  }

  // 3.1 综合查询业务-质量统计-质量统计-详情(图表)
  public List<QAStat> qaStatChart(Map<String, Object> parameterMap) {
    List<QAStat> dataList = (List<QAStat>) reportSqlService.queryResultList(REPORT_NAME,
      "3.1-qa-stat-chart",
      parameterMap, QAStat.class);
    return dataList.isEmpty() ? null : dataList;
  }

  // 3.2 综合查询业务-质量统计-产量统计
  public ProdStatChart prodStatChart(Map<String, Object> parameterMap) {
    // 折线图
    List<ProdStatByDay> polylineChart = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-date", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodCast = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-cast", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodHeat = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-heat", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodPit = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-pit", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodPre = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-pre", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodFinal = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-final", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodMachine = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-machine", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodDock = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-dock", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodScrap = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-scrap", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodShip = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-ship", parameterMap, ProdStatByDay.class);

    for(int i = 0; i < polylineChart.size(); i++) {
      if(prodCast.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setWhlCast(prodCast.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getWhlCast());
      }
      if(prodHeat.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setTlHt(prodHeat.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getTlHt());
      }
      if(prodPit.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setTlInPit(prodPit.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getTlInPit());
      }
      if(prodPre.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setPreInsp(prodPre.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getPreInsp());
      }
      if(prodFinal.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setFinalInsp(prodFinal.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getFinalInsp());
      }
      if(prodMachine.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setMachine(prodMachine.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getMachine());
      }
      if(prodDock.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setToDock(prodDock.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getToDock());
      }
      if(prodScrap.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setFinScrap(prodScrap.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getFinScrap());
      }
      if(prodShip.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(polylineChart.get(i).getCastDate())) {
        polylineChart.get(i).setShipped(prodShip.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(polylineChart.get(i).getCastDate()).getShipped());
      }
    }

    ProdStatByDay total = null;
    for (ProdStatByDay stat : polylineChart) {
      if ("total".equals(stat.getCastDate())) {
        total = stat;
        polylineChart.remove(stat);
        break;
      }
    }

    // 柱状图
    List<Map<String, String>> barChart = generateChartList(total);
    String barTotal = computeTotal(barChart);

    // 饼图
    ProdStatTotal prodStatTotal = (ProdStatTotal) reportSqlService.querySingleResult(REPORT_NAME,
      "3.2-prod-stat-total",
      new HashMap<>(), ProdStatTotal.class);
    List<Map<String, String>> sectorChart = generateChartList(prodStatTotal);
    String sectorTotal = computeTotal(sectorChart);

    ProdStatChart prodStatChart = ProdStatChart.builder()
      .polylineChart(polylineChart)
      .barChart(barChart)
      .barTotal(barTotal)
      .sectorChart(sectorChart)
      .sectorTotal(sectorTotal)
      .build();
    return prodStatChart;
  }

  // 3.2 综合查询业务-质量统计-产量统计
  public ProdStatReport prodStatReport(Map<String, Object> parameterMap) {
    // 每日数据部分
    List<ProdStatByDay> dataList = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-date", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodCast = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-cast", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodHeat = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-heat", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodPit = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-pit", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodPre = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-pre", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodFinal = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-final", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodMachine = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-machine", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodDock = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-dock", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodScrap = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-scrap", parameterMap, ProdStatByDay.class);
    List<ProdStatByDay> prodShip = (List<ProdStatByDay>) reportSqlService.queryResultList(REPORT_NAME, "3.2-prod-stat-ship", parameterMap, ProdStatByDay.class);

    for(int i = 0; i < dataList.size(); i++) {
      if(prodCast.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setWhlCast(prodCast.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getWhlCast());
      }
      if(prodHeat.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setTlHt(prodHeat.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getTlHt());
      }
      if(prodPit.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setTlInPit(prodPit.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getTlInPit());
      }
      if(prodPre.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setPreInsp(prodPre.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getPreInsp());
      }
      if(prodFinal.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setFinalInsp(prodFinal.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getFinalInsp());
      }
      if(prodMachine.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setMachine(prodMachine.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getMachine());
      }
      if(prodDock.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setToDock(prodDock.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getToDock());
      }
      if(prodScrap.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setFinScrap(prodScrap.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getFinScrap());
      }
      if(prodShip.stream().map(ProdStatByDay::getCastDate).collect(Collectors.toList()).contains(dataList.get(i).getCastDate())) {
        dataList.get(i).setShipped(prodShip.stream().collect(Collectors.toMap(ProdStatByDay::getCastDate, Function.identity())).get(dataList.get(i).getCastDate()).getShipped());
      }
    }

    // 统计部分
    ProdStatTotal totalStat = (ProdStatTotal) reportSqlService.querySingleResult(REPORT_NAME,
      "3.2-prod-stat-total",
      new HashMap<>(), ProdStatTotal.class);

    ProdStatReport prodStatReport = ProdStatReport.builder()
      .dataList(dataList)
      .totalStat(totalStat)
      .build();
    return prodStatReport;
  }

  private String computeTotal(List<Map<String, String>> barChart) {
    int total = 0;
    for (Map<String, String> bar : barChart) {
      int value = Integer.parseInt(bar.get("num"));
      total += value;
    }
    return String.valueOf(total);
  }

  private List<Map<String, String>> generateChartList(Object stat) {
    List<Map<String, String>> chartList = new ArrayList<>();
    if (stat == null) {
      return chartList;
    }
    Field[] fields = stat.getClass().getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      String type = field.getName();
      String name = ProdStatChart.NAME_MAP.get(type);
      if (name == null) {
        continue;
      }
      try {
        String num = (String) field.get(stat);
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("type", name);
        valueMap.put("num", num);
        chartList.add(valueMap);
      } catch (IllegalAccessException e) {
        log.error(e.getMessage());
      }
      field.setAccessible(false);
    }
    return chartList;
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

  // 3.3 综合查询业务-质量统计-车轮返工
  public WheelReworkResult wheelRework(Map<String, Object> parameterMap) {
    List<WheelRework> wheelReworkList = new ArrayList<>();
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "3.3-wheel-rework",
      parameterMap, JSONObject.class);
    if (jsonList.size() == 1) {
      return null;
    }
    for (JSONObject json : jsonList) {
      for (Entry entry : json.entrySet()) {
        String pctKey = ((String) entry.getKey());
        // 把Pct结尾的百分比值拼接到不带Pct结尾的值后面，格式为xxxx xx.xx%
        if (pctKey.endsWith("Pct")) {
          String key = pctKey.substring(0, pctKey.indexOf("Pct"));
          String value = json.getString(key);
          String pctValue = entry.getValue() + "%";
          json.put(key, value + " - " + pctValue);
        }
      }
      WheelRework w = json.toJavaObject(WheelRework.class);
      wheelReworkList.add(w);
    }

    WheelRework total = null;
    for (WheelRework wheelRework : wheelReworkList) {
      if ("total".equals(wheelRework.getCastDate())) {
        total = wheelRework;
        wheelReworkList.remove(wheelRework);
        break;
      }
    }
    WheelReworkResult wheelReworkResult = WheelReworkResult.builder()
      .resultList(wheelReworkList)
      .total(total).build();
    return wheelReworkResult;
  }

  // 3.4 综合查询业务-质量统计-车轮返废(图表)
  public WheelReworkScrapResult wheelReworkScrapChart(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "3.4-wheel-rework-scrap",
      parameterMap, JSONObject.class);
    if (jsonList.size() == 1) {
      return null;
    }

    List<JSONObject> newJsonList = new ArrayList<>();
    JSONObject total = null;
    for (JSONObject json : jsonList) {
      json.fluentRemove("castTotal")
        .fluentRemove("pre")
        .fluentRemove("dock")
        .fluentRemove("reworkTotal");
      JSONObject newJson = new JSONObject();
      json.entrySet().forEach(entry -> {
        String key = entry.getKey();
        String value = entry.getValue().toString();
        if (!key.equals("castDate")) {
          newJson.put(key.substring(6).toUpperCase(), value);
        } else {
          newJson.put(key, value);
        }
      });
      newJsonList.add(newJson);
    }

    for (JSONObject json : newJsonList) {
      String scrapDate = json.getString("castDate");
      if (scrapDate.equals("total")) {
        json.remove("castDate");
        total = json;
      }
    }
    newJsonList.remove(total);

    WheelReworkScrapResult wheelReworkScrapResult = WheelReworkScrapResult.builder()
      .resultList(newJsonList)
      .total(total)
      .build();
    return wheelReworkScrapResult;
  }

  // 3.4 综合查询业务-质量统计-车轮返废(报表)
  public WheelReworkScrapResult wheelReworkScrapReport(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "3.4-wheel-rework-scrap",
      parameterMap, JSONObject.class);
    if (jsonList.size() == 1) {
      return null;
    }

    JSONObject total = null;
    for (JSONObject json : jsonList) {
      String scrapDate = json.getString("castDate");
      if (scrapDate.equals("total")) {
        total = json;
      }

      String pre = json.getString("pre");
      for (Entry entry : json.entrySet()) {
        String key = ((String) entry.getKey());
        // 把Pct结尾的百分比值拼接到不带Pct结尾的值后面，格式为xxxx xx.xx%
        if (!key.equals("pre") && !key.equals("castDate") && !key.equals("castTotal")) {
          String value = json.getString(key);
          String pctValue = DigitalUtil.percentage(Integer.parseInt(value), Integer.parseInt(pre));
          json.put(key, value + " - " + pctValue);
        } else if (key.equals("pre")) {
          String castTotal = json.getString("castTotal");
          String value = json.getString(key);
          String pctValue = DigitalUtil.percentage(Integer.parseInt(value), Integer.parseInt(castTotal));
          json.put(key, value + " - " + pctValue);
        }
      }
    }
    jsonList.remove(total);
    WheelReworkScrapResult wheelReworkScrapResult = WheelReworkScrapResult.builder()
      .resultList(jsonList)
      .total(total)
      .build();
    return wheelReworkScrapResult;
  }

  // 3.5 综合查询业务-质量统计-浇注时间与废品
  public List<Map<String, String>> pourTimeAndScrap(Map<String, Object> parameterMap) {
    List<PourTimeAndScrap> pourTimeAndScrapList = (List<PourTimeAndScrap>) reportSqlService.queryResultList(REPORT_NAME,
      "3.5-pour-time-and-scrap",
      parameterMap, PourTimeAndScrap.class);
    if (pourTimeAndScrapList.isEmpty()) {
      return null;
    }

    int dateSize = pourTimeAndScrapList.size() / 30;
    List<Map<String, String>> result = new ArrayList<>();
    Map<String, String> tapSeqMap = null;
    for (int i = 0; i < pourTimeAndScrapList.size(); i++) {
      if (i % dateSize == 0) {
        tapSeqMap = new TreeMap<>();
        result.add(tapSeqMap);
      }
      String castDate = pourTimeAndScrapList.get(i).getCastDate();
      String bb1 = pourTimeAndScrapList.get(i).getBb1();
      tapSeqMap.put(castDate, bb1);
    }
    return result;
  }

  // 3.6 综合查询业务-质量统计-确认废品与交验
  public ConfirmScrapResult confirmScrap(Map<String, Object> parameterMap, boolean export) {
    List<JSONObject> jsonList = null;
    ConfirmScrapResult confirmScrapResult = null;
    JSONObject total = null;
    if(export){
      jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
              "3.6-confirm-scrap",
              parameterMap, JSONObject.class);

    } else {
       jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
              "3.6-confirm-scrap-total",
              parameterMap, JSONObject.class);

    }

    for (JSONObject json : jsonList) {
      String scrapDate = json.getString("scrapDate");
      if (scrapDate.equals("total")) {
        total = json;
      }

      String ss = json.getString("ss");
      for (Entry entry : json.entrySet()) {
        String key = ((String) entry.getKey());
        // 把Pct结尾的百分比值拼接到不带Pct结尾的值后面，格式为xxxx xx.xx%
        if (!key.equals("ss") && !key.equals("scrapDate")) {
          String value = json.getString(key);
          String pctValue = DigitalUtil.percentage(Integer.parseInt(value), Integer.parseInt(ss));
          json.put(key, value + " - " + pctValue);
        }
      }
    }

    if(export) {
      jsonList.remove(total);
      confirmScrapResult = ConfirmScrapResult.builder()
              .resultList(jsonList)
              .total(total)
              .build();
    } else {
      confirmScrapResult = ConfirmScrapResult.builder()
              .total(total)
              .build();
    }
    return confirmScrapResult;
  }

  // 3.7 综合查询业务-质量统计-预检废品
  public JSONObject preScrap(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "3.7-pre-scrap",
      parameterMap,
      JSONObject.class);
    if (jsonList.size() == 1) {
      return null;
    }

    JSONObject total = null;
    List<JSONObject> level2List = new ArrayList<>();
    Map<String, List<JSONObject>> level3Map = new HashMap<>();
    for (JSONObject json : jsonList) {
      String castDate = json.getString("castDate");
      if (castDate.equals("total")) {
        json.fluentRemove("castDate").fluentRemove("design");
        total = json;
      } else {
        String design = json.getString("design");
        if (design.equals("total")) {
          json.remove("design");
          level2List.add(json);
        } else {
          if (level3Map.get(castDate) == null) {
            level3Map.put(castDate, new ArrayList<>());
          }
          List<JSONObject> level3List = level3Map.get(castDate);
          json.remove("castDate");
          level3List.add(json);
        }
      }
    }
    level2List.forEach(json -> {
      String castDate = json.getString("castDate");
      json.put("data", level3Map.get(castDate));
    });
    JSONObject result = new JSONObject();
    result.put("total", total);
    result.put("resultList", level2List);
    return result;
  }

  // 3.8 综合查询业务-质量统计-在制品统计
  public MachiningStat machiningStatResult() {
    JSONArray machiningStat = machiningStat();
    JSONObject xMachiningStat = xMachiningStat();
    if (machiningStat == null || xMachiningStat == null) {
      return null;
    }
    MachiningStat result = MachiningStat.builder()
      .machiningStat(machiningStat)
      .xMachiningStat(xMachiningStat)
      .build();
    return result;
  }

  // 3.8 综合查询业务-质量统计-在制品统计-在制品
  public JSONArray machiningStat() {
    List<Map<String, String>> resultList = new ArrayList<>();
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "3.8-machining-stat",
      new HashMap<>(), JSONObject.class);
    if (jsonList.size() == 1) {
      return null;
    }
    Map<String, Map<String, String>> resultMap = new TreeMap<>();

    // 生成所有轮型名称集合
    Set<String> designSet = new TreeSet<>();
    for (JSONObject json : jsonList) {
      String design = json.getString("design");
      designSet.add(design);
    }

    // 生成<日期, 数据>的Map，数据中的轮型先用0填充
    for (JSONObject json : jsonList) {
      String castDate = json.getString("castDate");
      Map<String, String> data = resultMap.get("castDate");
      if (data == null) {
        data = new LinkedHashMap<>();
        data.put("castDate", castDate);
        data.put("total", "0");
        for (String design : designSet) {
          if (!design.equals("total")) {
            data.put(design, "0");
          }
        }
        resultMap.put(castDate, data);
      }
    }

    // 将数据塞入Map中
    for (JSONObject json : jsonList) {
      String castDate = json.getString("castDate");
      Map<String, String> data = resultMap.get(castDate);
      String design = json.getString("design");
      String cnt = json.getString("cnt");
      data.put(design, cnt);
    }

    resultMap.entrySet().forEach(resultEntry -> {
      resultList.add(resultEntry.getValue());
    });
    return (JSONArray) JSONArray.toJSON(resultList);
  }

  // 3.8 综合查询业务-质量统计-在制品统计-X光在制品
  public JSONObject xMachiningStat() {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "3.8-x-machining-stat",
      new HashMap<>(), JSONObject.class);
    if (jsonList.size() == 1) {
      return null;
    }

    List<String> keyList = new ArrayList<>();
    keyList.add("design");
    keyList.add("castDate");
    JSONObject result = assembler.generateProdLevelResult(jsonList, keyList);
    return result;
  }

  // 3.9 综合查询业务-质量统计-在制品统计-全部确认废品统计
  public AllConfirmScrapResult allConfirmScrapStat(Map<String, Object> parameterMap) {
    List<AllConfirmScrap> allConfirmScrapList = (List<AllConfirmScrap>) reportSqlService.queryResultList(REPORT_NAME,
      "3.9-all-confirm-scrap-stat",
      parameterMap,
      AllConfirmScrap.class);
    if (allConfirmScrapList.size() == 1) {
      return null;
    }
    AllConfirmScrap total = null;
    for (AllConfirmScrap allConfirmScrap : allConfirmScrapList) {
      if (allConfirmScrap.getScrapCode().equals("total")) {
        total = allConfirmScrap;
        break;
      }
    }
    allConfirmScrapList.remove(total);
    AllConfirmScrapResult allConfirmScrapResult = AllConfirmScrapResult.builder()
      .total(total)
      .resultList(allConfirmScrapList)
      .build();
    return allConfirmScrapResult;
  }
}
