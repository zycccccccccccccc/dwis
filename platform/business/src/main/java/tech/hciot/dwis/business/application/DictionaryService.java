package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.domain.DictionaryRepository;
import tech.hciot.dwis.business.domain.TableInfoRepository;
import tech.hciot.dwis.business.domain.model.ColumnInfo;
import tech.hciot.dwis.business.domain.model.Dictionary;
import tech.hciot.dwis.business.domain.model.Option;
import tech.hciot.dwis.business.domain.model.TableInfo;

@Service
@Slf4j
public class DictionaryService {

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private TableInfoRepository tableInfoRepository;

  @Autowired
  private DictionaryRepository dictionaryRepository;

  @Autowired
  private EntityManager entityManager;

  public List<Dictionary> findDictionaryList(String name) {
    Map<String, Map<String, ColumnInfo>> tableInfoMap = getTableInfoMap();

    List<Dictionary> dictionaryList = dictionaryRepository.findListByName(name);

    return dictionaryList.stream().map(dictionary -> {
      Dictionary returnDict = Dictionary.builder().build();
      BeanUtil.copyProperties(dictionary, returnDict, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      List<ColumnInfo> columnInfoList = JSON.parseArray(dictionary.getColumns(), ColumnInfo.class);
      Map<String, ColumnInfo> columnInfoMap = tableInfoMap.get(dictionary.getTableName());
      if (columnInfoMap != null && columnInfoList != null) {
        columnInfoList.forEach(columnInfo -> {
          ColumnInfo mergedColumnInfo = columnInfoMap.get(columnInfo.getProp());
          if (columnInfo.getLabel() != null) {
            mergedColumnInfo.setLabel(columnInfo.getLabel());
          }
          if (columnInfo.getType() != null) {
            mergedColumnInfo.setType(columnInfo.getType());
          }
          if (columnInfo.getRequired() != null) {
            mergedColumnInfo.setRequired(columnInfo.getRequired());
          }
          if (columnInfo.getKey() != null) {
            mergedColumnInfo.setKey(columnInfo.getKey());
          }
          if (columnInfo.getOptions() != null) {
            mergedColumnInfo.setOptions(columnInfo.getOptions());
          }
          if (columnInfo.getValidate() != null) {
            mergedColumnInfo.setValidate(columnInfo.getValidate());
          }
        });
      }
      List<ColumnInfo> mergedColumnInfoList = columnInfoMap.values().stream().sorted((x, y) -> {
        if (x.getColumnId() == null) {
          return -1;
        } else if (y.getColumnId() == null) {
          return 1;
        } else {
          return x.getColumnId() - y.getColumnId();
        }
      }).collect(Collectors.toList());
      returnDict.setColumnInfo(mergedColumnInfoList);
      returnDict.setColumns(null);
      return returnDict;
    }).collect(Collectors.toList());
  }

  private Map<String, Map<String, ColumnInfo>> getTableInfoMap() {
    Map<String, Map<String, ColumnInfo>> tableInfoMap = new HashMap<>();
    List<TableInfo> tableInfoList = tableInfoRepository.findAll();

    List<Option> enabledOptions = new ArrayList<>();
    enabledOptions.add(Option.builder().value(1).label("是").build());
    enabledOptions.add(Option.builder().value(0).label("否").build());

    for (TableInfo tableInfo : tableInfoList) {
      if ("create_time".equals(tableInfo.getColumnName())) {
        continue;
      }
      String tableName = tableInfo.getTableName();
      if (!tableInfoMap.containsKey(tableName)) {
        tableInfoMap.put(tableName, new HashMap<>());
      }
      Map<String, ColumnInfo> columnInfoMap = tableInfoMap.get(tableName);

      if ("id".equals(tableInfo.getColumnName())) {
        continue;
      }
      if ("enabled".equals(tableInfo.getColumnName())) {
        ColumnInfo enabledColumnInfo = ColumnInfo.builder()
          .prop("enabled")
          .label("是否可用")
          .type("select")
          .required(true)
          .options(enabledOptions)
          .columnId(tableInfo.getColumnId())
          .build();
        columnInfoMap.put(tableInfo.getColumnName(), enabledColumnInfo);
      } else {
        columnInfoMap.put(tableInfo.getColumnName(), ColumnInfo.builder()
          .prop(tableInfo.getColumnName())
          .label(tableInfo.getColumnDesc())
          .required(!tableInfo.getIsNullable())
          .columnId(tableInfo.getColumnId())
          .build());
      }
    }
    return tableInfoMap;
  }

  public List find(String tableName, String code, String design, String typeKxsj, Integer enabled) {
    List dataList = dictionaryRepository.findByTableName(tableName).map(dictionary -> {
      String querySql = "SELECT * FROM " + tableName + " WHERE ";
      String enabledSql = (enabled == null) ? " 1 = 1" : " enabled = " + enabled;
      querySql += enabledSql;
      if (StringUtils.isNotBlank(code)) {
        querySql += " AND code LIKE ?";
      }
      if (StringUtils.isNotBlank(design)) {
        querySql += " AND design LIKE ?";
      }
      if (StringUtils.isNotBlank(typeKxsj)) {
        querySql += " AND type_kxsj = ?";
      }
      Query query = entityManager.createNativeQuery(querySql);
      query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
      if (StringUtils.isNotBlank(code)) {
        query.setParameter(1, "%" + code + "%");
      }
      if (StringUtils.isNotBlank(design)) {
        query.setParameter(1, "%" + design + "%");
      }
      if (StringUtils.isNotBlank(typeKxsj)) {
        query.setParameter(1, typeKxsj);
      }
      return query.getResultList();
    }).orElseThrow(() -> PlatformException.badRequestException("字典表不存在"));

    dataList.forEach(data -> {
      Map<String, Object> dictMap = (Map) data;
      for (Entry<String, Object> entry : dictMap.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof Timestamp) {
          value = StandardTimeUtil.toTimeStr(((Timestamp) value).getTime());
        } else if (key.equals("location")) {
          value = strToList((String) value);
        } else if (tableName.equals("product_type") && key.equals("dep_id")) {
          value = strToNumberList((String) value);
        } else if (tableName.equals("manufacturer") && key.equals("product_type_id")) {
          value = strToNumberList((String) value);
        }
        entry.setValue(value);
      }
    });
    return dataList;
  }

  @Transactional
  public void createDictionary(String tableName, Map<String, Object> valueMap) {
    assertDictionaryNotExists(tableName, null, valueMap);

    StringBuilder columnSql = new StringBuilder();
    StringBuilder valueSql = new StringBuilder();
    for (Entry<String, Object> valueEntry : valueMap.entrySet()) {
      String column = valueEntry.getKey();
      if ("id".equals(column)) {
        continue;
      }
      Object value = valueEntry.getValue();
      if (column.equals("location")) {
        value = StringUtils.join((List<?>) value, ",");
      } else if (tableName.equals("product_type") && column.equals("dep_id")) {
        value = StringUtils.join((List<?>) value, ",");
      } else if (tableName.equals("manufacturer") && column.equals("product_type_id")) {
        value = StringUtils.join((List<?>) value, ",");
      }
      columnSql.append(column).append(", ");
      if (value == null || value instanceof Integer) {
        valueSql.append(value).append(", ");
      } else {
        valueSql.append("'").append(value).append("', ");
      }
    }
    columnSql.append("create_time");
    valueSql.append("'").append(timeFormat.format(new Date())).append("'");
    StringBuilder addSql = new StringBuilder("INSERT INTO ");
    addSql.append(tableName).append(" (" + columnSql + ") ")
      .append(" VALUES(").append(valueSql).append(")");
    try {
      entityManager.createNativeQuery(addSql.toString()).executeUpdate();
    } catch (Exception e) {
      log.error("create dictionary {} failed, failed info: {}", tableName, e.getMessage());
      throw PlatformException.badRequestException("添加字典表数据失败");
    }
  }

  @Transactional
  public void updateDictionary(String tableName, Integer id, Map<String, Object> valueMap) {
    assertDictionaryNotExists(tableName, id, valueMap);

    StringBuilder updateSql = new StringBuilder();
    updateSql.append("UPDATE ").append(tableName).append(" SET ");
    for (Entry<String, Object> valueEntry : valueMap.entrySet()) {
      String column = valueEntry.getKey();
      Object value = valueEntry.getValue();
      if ("id".equals(column)) {
        continue;
      }
      if (column.equals("location")) {
        value = StringUtils.join((List<?>) value, ",");
      } else if (tableName.equals("product_type") && column.equals("dep_id")) {
        value = StringUtils.join((List<?>) value, ",");
      } else if (tableName.equals("manufacturer") && column.equals("product_type_id")) {
        value = StringUtils.join((List<?>) value, ",");
      }
      updateSql.append(column).append(" = ");
      if (value == null || value instanceof Integer) {
        updateSql.append(value).append(", ");
      } else {
        updateSql.append("'").append(value).append("', ");
      }
    }
    updateSql.delete(updateSql.length() - 2, updateSql.length());
    updateSql.append(" WHERE id = ").append(id);
    try {
      entityManager.createNativeQuery(updateSql.toString()).executeUpdate();
    } catch(Exception e) {
      log.error("update dictionary {} failed, failed info: {}", tableName, e.getMessage());
      throw PlatformException.badRequestException("更新字典表失败");
    }
  }

  @Transactional
  public void deleteDictionary(String tableName, Integer id) {
    StringBuilder deleteSql = new StringBuilder();
    deleteSql.append("DELETE FROM ").append(tableName).append(" WHERE id = ").append(id);
    try {
      entityManager.createNativeQuery(deleteSql.toString()).executeUpdate();
    } catch(Exception e) {
      log.error("delete dictionary {} failed, failed info: {}", tableName, e.getMessage());
      throw PlatformException.badRequestException("删除字典表数据失败");
    }
  }

  // 返回所有列名
  List<String> getColumns(String columnInfo) {
    List<String> columnList = new ArrayList<>();
    JSONArray columnJson = JSONArray.parseArray(columnInfo);
    for (int i = 0; i < columnJson.size(); i++) {
      String id = columnJson.getJSONObject(i).getString("prop");
      columnList.add(id);
    }
    return columnList;
  }

  private void assertDictionaryNotExists(String tableName, Integer id, Map<String, Object> valueMap) {
    List<String> keyList = getKeyList(tableName);
    if (keyList.isEmpty()) {
      return;
    }
    StringBuilder keySql = new StringBuilder("SELECT 1 FROM ");
    keySql.append(tableName).append(" WHERE ");
    for (String key : keyList) {
      keySql.append(key).append(" = '").append(valueMap.get(key)).append("' AND ");
    }
    keySql.delete(keySql.length() - 5, keySql.length());
    if (id != null) {
      keySql.append(" AND ").append("id").append(" != ").append(id);
    }
    List<Object> resultList = entityManager.createNativeQuery(keySql.toString()).getResultList();
    if (!resultList.isEmpty()) {
      throw PlatformException.badRequestException("字典数据已存在");
    }
  }

  private List<String> getKeyList(String tableName) {
    List<Dictionary> dictionaryList = findDictionaryList(tableName);
    List<String> keyList = new ArrayList<>();
    if (!dictionaryList.isEmpty()) {
      for (ColumnInfo o : dictionaryList.get(0).getColumnInfo()) {
        Boolean key = o.getKey();
        if (key) {
          keyList.add(o.getProp());
        }
      }
    }
    return keyList;
  }

  // 字符串转成列表
  private List<String> strToList(String value) {
    if (value == null || value.equals("")) {
      return new ArrayList<>();
    } else {
      return Arrays.asList(value.split(","));
    }
  }

  // 字符串转成数字列表
  private List<Integer> strToNumberList(String value) {
    List<Integer> numberList = new ArrayList<>();
    if (value == null || value.equals("")) {
      return numberList;
    }
    String[] values = value.split(",");
    Arrays.stream(values).forEach(v -> {
      numberList.add(Integer.parseInt(v));
    });
    return numberList;
  }

  public static void main(String[] args) {
    List<String> list = new ArrayList<>();
    list.add("a");
    list.add("b");
    System.out.println(StringUtils.join(list, ","));
  }
}
