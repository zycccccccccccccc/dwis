package tech.hciot.dwis.business.application.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.prod.RepositoryReportResult;

@Service
@Slf4j
public class MultiReportProdStatService {

  private static final String REPORT_NAME = "composite-report/prod-stat";

  @Autowired
  private ReportAssembler assembler;

  @Autowired
  private ReportSqlService reportSqlService;

  // 2.1 综合查询业务-年/月度产量统计-库存报告
  public RepositoryReportResult repositoryReport(List<String> design) {
    Map<String, Object> parameterMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(design)) {
      parameterMap.put("design", design);
    }
    List<JSONObject> finishList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "2.1-repository-report-finish",
      parameterMap,
      JSONObject.class);

    if (finishList.size() == 1) {
      return null;
    }
    List<String> finishKeyList = new ArrayList<>();
    finishKeyList.add("design");
    finishKeyList.add("wheelW");
    finishKeyList.add("boreSize");
    JSONObject finishResult = assembler.generateProdLevelResult(finishList, finishKeyList);
    int finishTotal = finishResult.getJSONObject("total").getInteger("cnt");

    List<JSONObject> machiningList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "2.1-repository-report-machining",
      parameterMap,
      JSONObject.class);

    List<String> machiningKeyList = new ArrayList<>();
    machiningKeyList.add("design");
    machiningKeyList.add("process");
    JSONObject machiningResult = assembler.generateProdLevelResult(machiningList, machiningKeyList);
    int machiningTotal = machiningResult.getJSONObject("total").getInteger("cnt");

    RepositoryReportResult repositoryReportResult = RepositoryReportResult.builder()
      .finish(finishResult)
      .machining(machiningResult)
      .total(finishTotal + machiningTotal)
      .build();
    return repositoryReportResult;
  }

  // 2.2 综合查询业务-年/月度产量统计-浇注报告
  public JSONObject pourReport(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "2.2-pour-report",
      parameterMap,
      JSONObject.class);

    if (jsonList.size() == 1) {
      return null;
    }

    List<String> keyList = new ArrayList<>();
    keyList.add("design");
    keyList.add("castDate");
    JSONObject result = assembler.generateProdLevelResult(jsonList, keyList);
    return result;
  }

  // 2.3 综合查询业务-年/月度产量统计-成品报告
  public JSONObject finishReport(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "2.3-finish-report",
      parameterMap,
      JSONObject.class);

    if (jsonList.size() == 1) {
      return null;
    }

    List<String> keyList = new ArrayList<>();
    keyList.add("design");
    keyList.add("castDate");
    keyList.add("boreSize");
    JSONObject result = assembler.generateProdLevelResult(jsonList, keyList);
    return result;
  }

  // 2.4 综合查询业务-年/月度产量统计-入库报告
  public JSONObject stockReport(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "2.4-stock-report",
      parameterMap,
      JSONObject.class);

    if (jsonList.size() == 1) {
      return null;
    }

    List<String> keyList = new ArrayList<>();
    keyList.add("design");
    keyList.add("castDate");
    JSONObject result = assembler.generateProdLevelResult(jsonList, keyList);
    splitCastDateAndCheckCode(result);
    return result;
  }

  private void splitCastDateAndCheckCode(JSONObject result) {
    result.getJSONObject("total").put("checkCode", "total");
    JSONArray resultList = result.getJSONArray("resultList");
    for (Object data : resultList) {
      ((JSONObject) data).put("checkCode", "total");
      JSONArray dataList = ((JSONObject) data).getJSONArray("data");
      for (Object subData : dataList) {
        String castDate = (String) ((JSONObject) subData).get("castDate");
        ((JSONObject) subData).put("checkCode", castDate.split(" ")[1]);
        ((JSONObject) subData).put("castDate", castDate.split(" ")[0]);
      }
    }
  }

  // 2.5 综合查询业务-年/月度产量统计-发运报告
  public JSONObject shipReport(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "2.5-ship-report",
      parameterMap,
      JSONObject.class);

    if (jsonList.size() == 1) {
      return null;
    }

    List<String> keyList = new ArrayList<>();
    keyList.add("design");
    keyList.add("shippedDate");
    keyList.add("shippedNo");
    JSONObject result = assembler.generateProdLevelResult(jsonList, keyList);
    return result;
  }

  // 2.6 综合查询业务-年/月度产量统计-年度生产汇总报告
  public JSONObject yearProdReport(Map<String, Object> parameterMap) {
    List<JSONObject> jsonList = (List<JSONObject>) reportSqlService.queryResultList(REPORT_NAME,
      "2.6-year-prod-report",
      parameterMap,
      JSONObject.class);

    if (jsonList.size() == 1) {
      return null;
    }

    List<String> keyList = new ArrayList<>();
    keyList.add("dd");
    JSONObject result = assembler.generateProdLevelResult(jsonList, keyList);
    return result;
  }
}
