package tech.hciot.dwis.business.interfaces.api.report;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.hciot.dwis.business.interfaces.api.report.dto.techqa.QAStat;

@Component
@Slf4j
public class ReportAssembler {

  // 根据开始日期、结束日期、轮型生成参数Map
  public Map<String, Object> parameterMap(String beginDate, String endDate, List<String> design) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    if (!CollectionUtils.isEmpty(design)) {
      parameterMap.put("design", design);
    }
    return parameterMap;
  }

  public Map<String, Object> parameterMapForMachine(String beginDate, String endDate, String staffId) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    if (!StringUtils.isEmpty(staffId)) {
      parameterMap.put("staffId", staffId);
    }
    return parameterMap;
  }

  public Map<String, Object> parameterMapForQc(String beginDate, String endDate, String staffId, String shift) {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("beginDate", beginDate);
    parameterMap.put("endDate", endDate);
    if (!StringUtils.isEmpty(staffId)) {
      parameterMap.put("staffId", staffId);
    }
    if (!StringUtils.isEmpty(shift)) {
      parameterMap.put("shift", Integer.parseInt(shift));
    }
    return parameterMap;
  }

  // 根据日期生成当前日期所在月的第1天和下个月第1天组成的参数Map
  public Map<String, Object> generateCurrentMonthMap(String dateStr,
                                                     List<String> design) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    try {
      String beginDateStr = dateStr.substring(0, 7) + "-01";
      Date beginDate = dateFormat.parse(beginDateStr);
      Calendar cd = Calendar.getInstance();
      cd.setTime(beginDate);
      cd.add(Calendar.MONTH, 1);
      String endDateStr = dateFormat.format(cd.getTime());

      Map<String, Object> parameterMap = parameterMap(beginDateStr, endDateStr, design);
      return parameterMap;
    } catch (Exception e) {
      log.error(e.getMessage());
      return new HashMap<>();
    }
  }

  // 根据日期生成当前日期所在年的第1天和最后1天组成的参数Map
  public Map<String, Object> generateCurrentYearMap(String dateStr,
                                                    List<String> design) {
    String year = dateStr.substring(0, 4);
    String nextYear = String.valueOf(Integer.parseInt(year) + 1);
    Map<String, Object> parameterMap = parameterMap(year + "-01-01", nextYear + "-01-01", design);
    return parameterMap;
  }

  // 生成层级结构的返回结果，maxDataSize为数据部分最多个数
  public JSON generateLevelResult(List<QAStat> statList, int maxDataSize) {
    Map<String, JSONObject> level1Map = new TreeMap<>();
    Map<String, JSONObject> level2Map = new TreeMap<>();
    JSONArray level3List = new JSONArray();
    JSONObject level2Object = new JSONObject();
    statList.forEach(stat -> {
      JSONObject json = (JSONObject) JSONObject.toJSON(stat);
      json.remove("statKey");
      String key1 = json.getString("majorTitle");
      String key2 = json.getString("minorTitle");
      String key3 = json.getString("subTitle");
      if (key2 != null && key2.equals("total")) { // 生成第一层Map
        json.fluentRemove("data")
            .fluentRemove("minorTitle")
            .fluentRemove("subTitle");
        json = removeNullValue(json);
        level1Map.put(key1, json);
      } else if (key3.equals("total")) { // 生成第二层Map
        json.fluentRemove("data")
            .fluentRemove("subTitle");
        json = removeNullValue(json);
        if (key1 == null && key2 == null) { // 只有一层的情况
          BeanUtil.copyProperties(json, level2Object, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        } else {
          level2Map.put(key1 + "---" + key2, json);
        }
      } else { // 生成第三层List
        json = removeNullValue(json);
        JSONArray newData = new JSONArray();
        JSONArray jsonArray = json.getJSONArray("data");
        if (!jsonArray.isEmpty()) {
          jsonArray.subList(0, Math.min(jsonArray.size(), maxDataSize)).forEach(data -> {
            JSONObject newJson = removeNullValue((JSONObject) data);
            ((JSONObject) data).remove("statKey");
            newData.add(newJson);
          });
        }
        json.put("data", newData);
        level3List.add(json);
      }
    });

    if (level1Map.isEmpty() && level2Map.isEmpty()) { // 一层Map和二层Map都为空，则为一层
      if (level2Object.isEmpty()) { // 一层并且没有数据的情况
        return null;
      }
      level2Object.put("data", level3List);
      return level2Object;
    }

    // 将第三层List依次放入第二层Map中
    for (int i = 0; i < level3List.size(); i++) {
      JSONObject o = level3List.getJSONObject(i);
      String key1 = o.getString("majorTitle");
      String key2 = o.getString("minorTitle");
      JSONObject json = level2Map.get(key1 + "---" + key2);
      o.fluentRemove("majorTitle").fluentRemove("minorTitle");
      if (!json.containsKey("data")) {
        json.fluentPut("data", new JSONArray());
      }
      json.getJSONArray("data").add(o);
    }

    // 将第二层Map转化成List
    JSONArray level2List = new JSONArray();
    level2Map.entrySet().forEach(entry -> {
      level2List.add(entry.getValue());
    });

    if (level1Map.isEmpty()) { // 一层Map为空，则为两层Map
      return level2List;
    }

    // 将第二层List依次放入第一层Map中
    for (int i = 0; i < level2List.size(); i++) {
      JSONObject o = level2List.getJSONObject(i);
      String key1 = o.getString("majorTitle");
      JSONObject json = level1Map.get(key1);
      o.remove("majorTitle");
      if (!json.containsKey("data")) {
        json.fluentPut("data", new JSONArray());
      }
      json.getJSONArray("data").add(o);
    }

    // 将第一层Map转化成List
    JSONArray level1List = new JSONArray();
    level1Map.entrySet().forEach(entry -> {
      level1List.add(entry.getValue());
    });
    return level1List;
  }

  private JSONObject removeNullValue(JSONObject json) {
    JSONObject newJson = new JSONObject();
    json.entrySet().forEach(entry -> {
      if (entry.getValue() != null) {
        newJson.put(entry.getKey(), entry.getValue());
      }
    });
    return newJson;
  }

  // 年/月度产量统计-生成通用的三层结构的返回结果，即总->resultList->data
  public JSONObject generateProdLevelResult(List<JSONObject> jsonList, List<String> keyList) {
    if (jsonList.size() == 1) {
      return null;
    }
    // 找到total结点
    JSONObject total = null;
    for (JSONObject json : jsonList) {
      boolean isTotal = true;
      for (String key : keyList) {
        if (!"total".equals(json.getString(key))) {
          isTotal = false;
          break;
        }
      }
      if (isTotal) {
        total = json;
        break;
      }
    }
    jsonList.remove(total);

    // 每一层的数据分别放在每一层的Map中
    // 先生成每一层的空Map
    List<Map<String, JSONObject>> mapList = new ArrayList<>();
    for (int i = 0; i < keyList.size(); i++) {
      mapList.add(new TreeMap<>());
    }

    // 将数据分别放到每一层的Map中
    for (JSONObject json : jsonList) {
      int totalCnt = 0;
      String mapKey = "";
      for (String key : keyList) {
        String keyValue = json.getString(key);
        if (keyValue.equals("total")) {
          totalCnt++;
        } else {
          mapKey = mapKey + keyValue + "--";
        }
      }
      int level = keyList.size() - totalCnt;
      mapKey = mapKey.substring(0, mapKey.length() - 2);
      Map<String, JSONObject> map = mapList.get(level - 1);
      map.put(mapKey, json);
    }

    // 将每一层数据塞进上一层的data结点中
    for (int i = keyList.size() - 1; i > 0; i--) {
      Map<String, JSONObject> map = mapList.get(i);
      for (Entry<String, JSONObject> entry : map.entrySet()) {
        String mapKey = entry.getKey();
        JSONObject json = entry.getValue();
        String highLevelMapKey = mapKey.substring(0, mapKey.lastIndexOf("--"));
        Map<String, JSONObject> highLevelMap = mapList.get(i - 1);
        JSONObject highLevelJson = highLevelMap.get(highLevelMapKey);
        if (highLevelJson.getJSONArray("data") == null) {
          highLevelJson.put("data", new JSONArray());
        }
        highLevelJson.getJSONArray("data").add(json);
      }
    }
    JSONObject result = new JSONObject();
    result.put("total", total);
    result.put("resultList", mapList.get(0).values());
    return result;
  }
}
