package tech.hciot.dwis.business.application.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.infrastructure.UnderlineCamelUtil;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;
import tech.hciot.dwis.business.interfaces.api.report.ReportAssembler;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Slf4j
public class MultiReportMachineStatService {

  public static final String MACHINE_STAT_STAFF_DETAIL = "5.1-machine-stat-staff-detail";
  public static final String MACHINE_STAT_STAFF_QUANTITY_J_MACHINE = "5.2.1-machine-stat-staff-quantity";
  public static final String MACHINE_STAT_STAFF_QUANTITY_T_MACHINE = "5.2.2-machine-stat-staff-quantity";
  public static final String MACHINE_STAT_STAFF_QUANTITY_K_MACHINE = "5.2.3-machine-stat-staff-quantity";
  public static final String MACHINE_STAT_STAFF_QUANTITY_W_MACHINE = "5.2.4-machine-stat-staff-quantity";
  public static final String MACHINE_STAT_STAFF_QUANTITY_Q_MACHINE = "5.2.5-machine-stat-staff-quantity";
  public static final String MACHINE_STAT_MACHINE_REWORK = "5.4-machine-stat-rework";
  public static final String MACHINE_STAT_MACHINE_STAFF_JMACHINE = "5.5-machine-stat-machine-staff-jmachine";
  public static final String MACHINE_STAT_MACHINE_STAFF_KMACHINE = "5.5-machine-stat-machine-staff-kmachine";
  public static final String MACHINE_STAT_MACHINE_STAFF_TMACHINE = "5.5-machine-stat-machine-staff-tmachine";
  public static final String MACHINE_STAT_MACHINE_TOOL_JMACHINE = "5.6-machine-stat-machine-tool-jmachine";
  public static final String MACHINE_STAT_MACHINE_TOOL_KMACHINE = "5.6-machine-stat-machine-tool-kmachine";
  public static final String MACHINE_STAT_MACHINE_TOOL_TMACHINE = "5.6-machine-stat-machine-tool-tmachine";
  public static final String MACHINE_STAT_JMACHINE8_DATE = "5.7-machine-stat-jmachine8-date";
  public static final String MACHINE_STAT_JMACHINE8_OPERATOR = "5.7-machine-stat-jmachine8-operator";
  public static final String MACHINE_STAT_MACHINE_REWORKSTAT = "5.8-machine-stat-reworkstat";
  public static final String MACHINE_STAT_RECHECK_DETAIL = "5.9-machine-roundness-recheck-detail";

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
    String sql = sqlTemplateParser.parseSqlTemplate("composite-report/machine-stat", templateName, parameterMap);
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
      log.info("technology quality query - {} finish", templateName);
      return objectList;
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error("technology quality query error: " + e.getMessage(), e);
    }
    return new ArrayList<>();
  }

  public List<StaffDetailData> staffDetail(Map<String, Object> parameterMap) {
    List<StaffDetailData> staffDetailDataList = queryResultList(MACHINE_STAT_STAFF_DETAIL, parameterMap, StaffDetailData.class);
    return staffDetailDataList.isEmpty() ? null : staffDetailDataList;
  }

  public StaffQuantityData staffQuantity(Map<String, Object> parameterMap) {
    StaffQuantityData staffQuantityData = new StaffQuantityData();

    List<JMachineQuantity> jMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_J_MACHINE, parameterMap,
        JMachineQuantity.class);
    List<JMachineQuantityStat> jMachineQuantityStatList = genQuantityStat(jMachineQuantityList, JMachineQuantityStat.class);
    JMachineQuantityTotal jMachineQuantityTotal = genTotal(jMachineQuantityStatList, JMachineQuantityTotal.class);
    QuantityData<JMachineQuantityTotal, JMachineQuantityStat> jQuantityData = new QuantityData();
    jQuantityData.setTotal(jMachineQuantityTotal);
    jQuantityData.setRecord(jMachineQuantityStatList);
    staffQuantityData.setJMachine(jQuantityData);

    List<TMachineQuantity> tMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_T_MACHINE, parameterMap,
        TMachineQuantity.class);
    List<TMachineQuantityStat> tMachineQuantityStatList = genQuantityStat(tMachineQuantityList, TMachineQuantityStat.class);
    TMachineQuantityTotal tMachineQuantityTotal = genTotal(tMachineQuantityStatList, TMachineQuantityTotal.class);
    QuantityData<TMachineQuantityTotal, TMachineQuantityStat> tQuantityData = new QuantityData();
    tQuantityData.setTotal(tMachineQuantityTotal);
    tQuantityData.setRecord(tMachineQuantityStatList);
    staffQuantityData.setTMachine(tQuantityData);

    List<KMachineQuantity> kMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_K_MACHINE, parameterMap,
        KMachineQuantity.class);
    List<KMachineQuantityStat> kMachineQuantityStatList = genQuantityStat(kMachineQuantityList, KMachineQuantityStat.class);
    KMachineQuantityTotal kMachineQuantityTotal = genTotal(kMachineQuantityStatList, KMachineQuantityTotal.class);
    QuantityData<KMachineQuantityTotal, KMachineQuantityStat> kQuantityData = new QuantityData();
    kQuantityData.setTotal(kMachineQuantityTotal);
    kQuantityData.setRecord(kMachineQuantityStatList);
    staffQuantityData.setKMachine(kQuantityData);

    List<WMachineQuantity> wMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_W_MACHINE, parameterMap,
        WMachineQuantity.class);
    List<WMachineQuantityStat> wMachineQuantityStatList = genQuantityStat(wMachineQuantityList, WMachineQuantityStat.class);
    WMachineQuantityTotal wMachineQuantityTotal = genTotal(wMachineQuantityStatList, WMachineQuantityTotal.class);
    QuantityData<WMachineQuantityTotal, WMachineQuantityStat> wQuantityData = new QuantityData();
    wQuantityData.setTotal(wMachineQuantityTotal);
    wQuantityData.setRecord(wMachineQuantityStatList);
    staffQuantityData.setWMachine(wQuantityData);

    List<QMachineQuantity> qMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_Q_MACHINE, parameterMap,
        QMachineQuantity.class);
    List<QMachineQuantityStat> qMachineQuantityStatList = genQuantityStat(qMachineQuantityList, QMachineQuantityStat.class);
    QMachineQuantityTotal qMachineQuantityTotal = genTotal(qMachineQuantityStatList, QMachineQuantityTotal.class);
    QuantityData<QMachineQuantityTotal, QMachineQuantityStat> qQuantityData = new QuantityData();
    qQuantityData.setTotal(qMachineQuantityTotal);
    qQuantityData.setRecord(qMachineQuantityStatList);
    staffQuantityData.setQMachine(qQuantityData);

    return staffQuantityData;
  }

  private <T, R> List<T> genQuantityStat(List<R> quantityList, Class<T> type) {
    return genQuantityStat(quantityList, type, false);
  }

  private <T, R> List<T> genQuantityStat(List<R> quantityList, Class<T> type, boolean isMachine) {
    List<T> quantityStatList = new ArrayList<>();

    Map<String, T> map = new LinkedHashMap<>();
    quantityList.forEach(quantity -> {
      Class clazz = quantity.getClass();
      try {
        Method method = clazz.getDeclaredMethod(isMachine ? "getMachineNo" : "getOperator");
        String key = method.invoke(quantity).toString();
        if (map.containsKey(key)) {
          T quantityStat = map.get(key);
          Class clazzStat = quantityStat.getClass();
          Field fields[] = clazzStat.getDeclaredFields();
          for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (fieldName.equals("operator") || fieldName.equals("machineNo")) {
              continue;
            }
            if (fieldName.equals("detail")) {
              List<R> detailList = (List<R>) clazzStat.getDeclaredMethod("getDetail").invoke(quantityStat);
              detailList.add(quantity);
              clazzStat.getDeclaredMethod("setDetail", List.class).invoke(quantityStat, detailList);
              continue;
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + fieldName.substring(1);
            String setMethodName = "set" + firstLetter + fieldName.substring(1);
            Integer oldValue = (Integer) clazzStat.getDeclaredMethod(getMethodName).invoke(quantityStat);
            Integer addedValue = (Integer) clazz.getDeclaredMethod(getMethodName).invoke(quantity);
            int newValue = oldValue.intValue() + addedValue.intValue();
            Method quantityStatSet = clazzStat.getDeclaredMethod(setMethodName, new Class[]{field.getType()});
            quantityStatSet.invoke(quantityStat, newValue);
          }
        } else {
          T quantityStat = type.newInstance();
          Class clazzStat = quantityStat.getClass();
          Field fields[] = clazzStat.getDeclaredFields();
          for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (isMachine) {
              if (fieldName.equals("operator")) {
                continue;
              }
            } else {
              if (fieldName.equals("machineNo")) {
                continue;
              }
            }
            if (fieldName.equals("detail")) {
              List<R> detailList = new ArrayList();
              detailList.add(quantity);
              clazzStat.getDeclaredMethod("setDetail", List.class).invoke(quantityStat, detailList);
              continue;
            }
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + fieldName.substring(1);
            String setMethodName = "set" + firstLetter + fieldName.substring(1);

            Method quantityStatSet = clazzStat.getDeclaredMethod(setMethodName, new Class[]{field.getType()});
            Object value = clazz.getDeclaredMethod(getMethodName).invoke(quantity);
            quantityStatSet.invoke(quantityStat, value);
          }
          map.put(key, quantityStat);
        }
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    });
    quantityStatList.addAll(map.values());
    return quantityStatList;
  }

  private <T, R> T genTotal(List<R> statList, Class<T> type) {
    final T total;
    try {
      total = type.newInstance();
      Class clazz = total.getClass();
      statList.forEach(stat -> {
        Class classStat = stat.getClass();
        Field fields[] = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
          Field field = fields[i];
          String fieldName = field.getName();
          String firstLetter = fieldName.substring(0, 1).toUpperCase();
          String getMethodName = "get" + firstLetter + fieldName.substring(1);
          String setMethodName = "set" + firstLetter + fieldName.substring(1);
          try {
            Integer oldValue = (Integer) clazz.getDeclaredMethod(getMethodName).invoke(total);
            Integer addedValue = (Integer) classStat.getDeclaredMethod(getMethodName).invoke(stat);
            int newValue = oldValue.intValue() + addedValue.intValue();
            Method totalSet = clazz.getDeclaredMethod(setMethodName, new Class[]{field.getType()});
            totalSet.invoke(total, newValue);
          } catch (Exception e) {
            log.error(e.getMessage());
          }
        }
      });
      return total;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }

  private void changeParameterMap(Map<String, Object> parameterMap) {
    if (parameterMap.containsKey("shift")) {
      Integer shift = (Integer) parameterMap.get("shift");
      String condition = null;

      if (shift == 1) {
        condition = "CONVERT(INT, CONVERT(varchar(2),ope_d_t, 108)) < 8 ";
      } else if (shift == 2) {
        condition = "CONVERT(INT, CONVERT(varchar(2),ope_d_t, 108)) BETWEEN 8 AND 15 ";
      } else if (shift == 3) {
        condition = "CONVERT(INT, CONVERT(varchar(2),ope_d_t, 108)) > 15 ";
      }
      parameterMap.put("shift", condition);
    }
  }

  public StaffQuantityData machineQuantity(Map<String, Object> parameterMap) {
    changeParameterMap(parameterMap);
    StaffQuantityData staffQuantityData = new StaffQuantityData();

    List<JMachineQuantity> jMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_J_MACHINE, parameterMap,
        JMachineQuantity.class);
    List<JMachineQuantityStat> jMachineQuantityStatList = genQuantityStat(jMachineQuantityList, JMachineQuantityStat.class, true);
    JMachineQuantityTotal jMachineQuantityTotal = genTotal(jMachineQuantityStatList, JMachineQuantityTotal.class);
    QuantityData<JMachineQuantityTotal, JMachineQuantityStat> jQuantityData = new QuantityData();
    jQuantityData.setTotal(jMachineQuantityTotal);
    jQuantityData.setRecord(jMachineQuantityStatList);
    staffQuantityData.setJMachine(jQuantityData);

    List<TMachineQuantity> tMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_T_MACHINE, parameterMap,
        TMachineQuantity.class);
    List<TMachineQuantityStat> tMachineQuantityStatList = genQuantityStat(tMachineQuantityList, TMachineQuantityStat.class, true);
    TMachineQuantityTotal tMachineQuantityTotal = genTotal(tMachineQuantityStatList, TMachineQuantityTotal.class);
    QuantityData<TMachineQuantityTotal, TMachineQuantityStat> tQuantityData = new QuantityData();
    tQuantityData.setTotal(tMachineQuantityTotal);
    tQuantityData.setRecord(tMachineQuantityStatList);
    staffQuantityData.setTMachine(tQuantityData);

    List<KMachineQuantity> kMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_K_MACHINE, parameterMap,
        KMachineQuantity.class);
    List<KMachineQuantityStat> kMachineQuantityStatList = genQuantityStat(kMachineQuantityList, KMachineQuantityStat.class, true);
    KMachineQuantityTotal kMachineQuantityTotal = genTotal(kMachineQuantityStatList, KMachineQuantityTotal.class);
    QuantityData<KMachineQuantityTotal, KMachineQuantityStat> kQuantityData = new QuantityData();
    kQuantityData.setTotal(kMachineQuantityTotal);
    kQuantityData.setRecord(kMachineQuantityStatList);
    staffQuantityData.setKMachine(kQuantityData);

    List<WMachineQuantity> wMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_W_MACHINE, parameterMap,
        WMachineQuantity.class);
    List<WMachineQuantityStat> wMachineQuantityStatList = genQuantityStat(wMachineQuantityList, WMachineQuantityStat.class, true);
    WMachineQuantityTotal wMachineQuantityTotal = genTotal(wMachineQuantityStatList, WMachineQuantityTotal.class);
    QuantityData<WMachineQuantityTotal, WMachineQuantityStat> wQuantityData = new QuantityData();
    wQuantityData.setTotal(wMachineQuantityTotal);
    wQuantityData.setRecord(wMachineQuantityStatList);
    staffQuantityData.setWMachine(wQuantityData);

    List<QMachineQuantity> qMachineQuantityList = queryResultList(MACHINE_STAT_STAFF_QUANTITY_Q_MACHINE, parameterMap,
        QMachineQuantity.class);
    List<QMachineQuantityStat> qMachineQuantityStatList = genQuantityStat(qMachineQuantityList, QMachineQuantityStat.class, true);
    QMachineQuantityTotal qMachineQuantityTotal = genTotal(qMachineQuantityStatList, QMachineQuantityTotal.class);
    QuantityData<QMachineQuantityTotal, QMachineQuantityStat> qQuantityData = new QuantityData();
    qQuantityData.setTotal(qMachineQuantityTotal);
    qQuantityData.setRecord(qMachineQuantityStatList);
    staffQuantityData.setQMachine(qQuantityData);

    return staffQuantityData;
  }

  public List<ReworkData> machineRework(Map<String, Object> parameterMap) {
    List<ReworkData> reworkDataList = queryResultList(MACHINE_STAT_MACHINE_REWORK, parameterMap, ReworkData.class);
    fillJMachine(reworkDataList, false);
    fillTMachine(reworkDataList, false);
    fillKMachine(reworkDataList, false);
    fillWMachine(reworkDataList, false);
    fillJMachine(reworkDataList, true);
    fillTMachine(reworkDataList, true);
    fillKMachine(reworkDataList, true);
    fillWMachine(reworkDataList, true);
    fillFinalCheck(reworkDataList);
    fillMagCheck(reworkDataList);
    return reworkDataList.isEmpty() ? null : reworkDataList;
  }

  private void fillMagCheck(List<ReworkData> reworkDataList) {
    reworkDataList.forEach(reworkData -> {
      String sql = "SELECT TOP 1 magnetic_record.ope_d_t,magnetic_record.mag_drag_inspector_id "
          + "FROM magnetic_record "
          + "WHERE magnetic_record.rework_code = '" + reworkData.getReworkCode() + "' AND magnetic_record.wheel_serial = '"
          + reworkData.getWheelSerial() + "' "
          + "ORDER BY magnetic_record.ope_d_t ASC";
      try {
        Map result = jdbcTemplate.queryForMap(sql);
        reworkData.setFinalCheckDT((Date) result.get("ope_d_t"));
        reworkData.setDragInspectorId((String) result.get("mag_drag_inspector_id"));
      } catch (EmptyResultDataAccessException e) {
        log.info("no data");
      }
    });
  }

  private void fillFinalCheck(List<ReworkData> reworkDataList) {
    reworkDataList.forEach(reworkData -> {
      String sql = "SELECT TOP 1 final_check_record.ope_d_t,final_check_record.drag_inspector_id "
          + "FROM final_check_record "
          + "WHERE final_check_record.rework_code = '" + reworkData.getReworkCode() + "' AND final_check_record.wheel_serial = '"
          + reworkData.getWheelSerial() + "' "
          + "ORDER BY final_check_record.ope_d_t ASC";
      try {
        Map result = jdbcTemplate.queryForMap(sql);
        reworkData.setFinalCheckDT((Date) result.get("ope_d_t"));
        reworkData.setDragInspectorId((String) result.get("drag_inspector_id"));
      } catch (EmptyResultDataAccessException e) {
        log.info("no data");
      }
    });
  }

  private void fillWMachine(List<ReworkData> reworkDataList, boolean isFirst) {
    String columnName = isFirst ? "w_id" : "w_id_re";
    reworkDataList.forEach(reworkData -> {
      String sql = "SELECT w_machine_record.machine_no,w_machine_record.ope_d_t,w_machine_record.operator "
          + "FROM w_machine_record INNER JOIN machine_record ON w_machine_record.id = machine_record." + columnName + " "
          + "WHERE machine_record.wheel_serial = '" + reworkData.getWheelSerial() + "'";
      try {
        Map result = jdbcTemplate.queryForMap(sql);
        if (isFirst) {
          reworkData.setFirstWMachineNo((Integer) result.get("machine_no"));
          reworkData.setFirstWMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setFirstWMachineOperator((String) result.get("operator"));
        } else {
          reworkData.setWMachineNo((Integer) result.get("machine_no"));
          reworkData.setWMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setWMachineOperator((String) result.get("operator"));
        }
      } catch (EmptyResultDataAccessException e) {
        log.info("no data");
      }
    });
  }

  private void fillKMachine(List<ReworkData> reworkDataList, boolean isFirst) {
    String columnName = isFirst ? "k_id" : "k_id_re";
    reworkDataList.forEach(reworkData -> {
      String sql = "SELECT k_machine_record.machine_no,k_machine_record.ope_d_t,k_machine_record.operator "
          + "FROM k_machine_record INNER JOIN machine_record ON k_machine_record.id = machine_record." + columnName + " "
          + "WHERE machine_record.wheel_serial = '" + reworkData.getWheelSerial() + "'";
      try {
        Map result = jdbcTemplate.queryForMap(sql);
        if (isFirst) {
          reworkData.setFirstKMachineNo((Integer) result.get("machine_no"));
          reworkData.setFirstKMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setFirstKMachineOperator((String) result.get("operator"));
        } else {
          reworkData.setKMachineNo((Integer) result.get("machine_no"));
          reworkData.setKMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setKMachineOperator((String) result.get("operator"));
        }
      } catch (EmptyResultDataAccessException e) {
        log.info("no data");
      }
    });
  }

  private void fillTMachine(List<ReworkData> reworkDataList, boolean isFirst) {
    String columnName = isFirst ? "t_id" : "t_id_re";
    reworkDataList.forEach(reworkData -> {
      String sql = "SELECT t_machine_record.machine_no,t_machine_record.ope_d_t,t_machine_record.operator "
          + "FROM t_machine_record INNER JOIN machine_record ON t_machine_record.id = machine_record." + columnName + " "
          + "WHERE machine_record.wheel_serial = '" + reworkData.getWheelSerial() + "'";
      try {
        Map result = jdbcTemplate.queryForMap(sql);
        if (isFirst) {
          reworkData.setFirstTMachineNo((Integer) result.get("machine_no"));
          reworkData.setFirstTMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setFirstTMachineOperator((String) result.get("operator"));
        } else {
          reworkData.setTMachineNo((Integer) result.get("machine_no"));
          reworkData.setTMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setTMachineOperator((String) result.get("operator"));
        }
      } catch (EmptyResultDataAccessException e) {
        log.info("no data");
      }
    });
  }

  private void fillJMachine(List<ReworkData> reworkDataList, boolean isFirst) {
    String columnName = isFirst ? "j_id" : "j_id_re";
    reworkDataList.forEach(reworkData -> {
      String sql = "SELECT j_machine_record.machine_no,j_machine_record.ope_d_t,j_machine_record.operator "
          + "FROM j_machine_record INNER JOIN machine_record ON j_machine_record.id = machine_record." + columnName + " "
          + "WHERE machine_record.wheel_serial = '" + reworkData.getWheelSerial() + "'";
      try {
        Map result = jdbcTemplate.queryForMap(sql);
        if (isFirst) {
          reworkData.setFirstJMachineNo((Integer) result.get("machine_no"));
          reworkData.setFirstJMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setFirstJMachineOperator((String) result.get("operator"));
        } else {
          reworkData.setJMachineNo((Integer) result.get("machine_no"));
          reworkData.setJMachineOpeDT((Date) result.get("ope_d_t"));
          reworkData.setJMachineOperator((String) result.get("operator"));
        }
      } catch (EmptyResultDataAccessException e) {
        log.info("no data");
      }
    });
  }

  public JSON machineStaff(Map<String, Object> parameterMap) {
    List<String> keyList = new ArrayList<>();
    keyList.add("teamLeaderId");
    keyList.add("operator");
    keyList.add("reworkCode");

    List<JSONObject> jMachineResultList =
      queryResultList(MACHINE_STAT_MACHINE_STAFF_JMACHINE, parameterMap, JSONObject.class);
    computeReworkMachine(jMachineResultList);
    JSONObject jMachineResult = assembler.generateProdLevelResult(jMachineResultList, keyList);

    List<JSONObject> tMachineResultList =
            queryResultList(MACHINE_STAT_MACHINE_STAFF_TMACHINE, parameterMap, JSONObject.class);
    computeReworkMachine(tMachineResultList);
    JSONObject tMachineResult = assembler.generateProdLevelResult(tMachineResultList, keyList);

    List<JSONObject> kMachineResultList =
      queryResultList(MACHINE_STAT_MACHINE_STAFF_KMACHINE, parameterMap, JSONObject.class);
    computeReworkMachine(kMachineResultList);
    JSONObject kMachineResult = assembler.generateProdLevelResult(kMachineResultList, keyList);

    JSONObject result = new JSONObject();
    result.put("jMachine", jMachineResult.getJSONArray("resultList"));
    result.put("tMachine", tMachineResult.getJSONArray("resultList"));
    result.put("kMachine", kMachineResult.getJSONArray("resultList"));

    return result;
  }

  public JSON machineTool(Map<String, Object> parameterMap) {
    List<String> keyList = new ArrayList<>();
    keyList.add("machineNo");
    keyList.add("reworkCode");

    List<JSONObject> jMachineResultList =
      queryResultList(MACHINE_STAT_MACHINE_TOOL_JMACHINE, parameterMap, JSONObject.class);
    computeReworkMachine(jMachineResultList);
    JSONObject jMachineResult = assembler.generateProdLevelResult(jMachineResultList, keyList);

    List<JSONObject> tMachineResultList =
      queryResultList(MACHINE_STAT_MACHINE_TOOL_TMACHINE, parameterMap, JSONObject.class);
    computeReworkMachine(tMachineResultList);
    JSONObject tMachineResult = assembler.generateProdLevelResult(tMachineResultList, keyList);

    List<JSONObject> kMachineResultList =
            queryResultList(MACHINE_STAT_MACHINE_TOOL_KMACHINE, parameterMap, JSONObject.class);
    computeReworkMachine(kMachineResultList);
    JSONObject kMachineResult = assembler.generateProdLevelResult(kMachineResultList, keyList);

    JSONObject result = new JSONObject();
    result.put("jMachine", jMachineResult.getJSONArray("resultList"));
    result.getJSONArray("jMachine").sort(Comparator.comparing(st -> ((JSONObject)st).getInteger("machineNo")));
    result.put("tMachine", tMachineResult.getJSONArray("resultList"));
    result.getJSONArray("tMachine").sort(Comparator.comparing(st -> ((JSONObject)st).getInteger("machineNo")));
    result.put("kMachine", kMachineResult.getJSONArray("resultList"));
    result.getJSONArray("kMachine").sort(Comparator.comparing(st -> ((JSONObject)st).getInteger("machineNo")));

    return result;
  }

  private void computeReworkMachine(List<JSONObject> resultList) {
    if (ObjectUtils.isEmpty(resultList)) {
      return;
    }
    for (JSONObject result : resultList) {
      int reworkCount = result.getInteger("reworkCount");
      int machineCount = result.getInteger("machineCount");
      double reworkMachine = reworkCount * 100.0 / machineCount;
      DecimalFormat df = new DecimalFormat("0.00");
      String reworkMachineStr = df.format(reworkMachine) + "%";
      result.put("reworkMachine", reworkMachineStr);
    }
  }

  // 机加工统计-生成通用的三层结构的返回结果
  public JSONObject generateMachineLevelResult(List<MachineStaff> machineStaffJmachineList) {
    if (machineStaffJmachineList.size() == 0) {
      return null;
    }

    Map<String, Object> level1Map = new TreeMap<>();
    for (MachineStaff machineStaff : machineStaffJmachineList) {
      if ("".equals(machineStaff.getOperator())) {
        level1Map.put(machineStaff.getTeamLeaderId(), JSONObject.toJSON(machineStaff));
      }
    }

    for (MachineStaff machineStaff : machineStaffJmachineList) {
      if ("".equals(machineStaff.getReworkCode()) && !"".equals(machineStaff.getOperator())) {
        JSONObject level1 = (JSONObject) level1Map.get(machineStaff.getTeamLeaderId());
        level1.put(machineStaff.getOperator(), JSONObject.toJSON(machineStaff));
      }
    }

    for (MachineStaff machineStaff : machineStaffJmachineList) {
      if (!"".equals(machineStaff.getReworkCode()) && !"".equals(machineStaff.getOperator())) {
        JSONObject level1 = (JSONObject) level1Map.get(machineStaff.getTeamLeaderId());
        JSONObject level2 = (JSONObject) level1.get(machineStaff.getOperator());
        level2.put(machineStaff.getReworkCode(), JSONObject.toJSON(machineStaff));
      }
    }

    return null;
  }

  public Map<String, List<JMachine8>> jMachine8(Map<String, Object> parameterMap) {
    List<JMachine8> jMachine8DateList = queryResultList(MACHINE_STAT_JMACHINE8_DATE, parameterMap, JMachine8.class);
    List<JMachine8> jMachine8OperatorList = queryResultList(MACHINE_STAT_JMACHINE8_OPERATOR, parameterMap, JMachine8.class);
    Map<String, List<JMachine8>> result = new HashMap<>();
    result.put("jMachine8Date", jMachine8DateList.isEmpty() ? null : jMachine8DateList);
    result.put("jMachine8Operator", jMachine8OperatorList.isEmpty() ? null : jMachine8OperatorList);
    return result;
  }


  public QuantityData<ReworkStatTotal, ReworkStatData> machineReworkStatTotal(Map<String, Object> parameterMap) {
    changeParameterMap(parameterMap);
    ReworkStatTotal reworkStatTotal;

    List<ReworkStatData> list = queryResultList(MACHINE_STAT_MACHINE_REWORKSTAT, parameterMap, ReworkStatData.class);
    reworkStatTotal = genTotal(list, ReworkStatTotal.class);
    QuantityData<ReworkStatTotal, ReworkStatData> quantityData = new QuantityData();
    quantityData.setTotal(reworkStatTotal);
    quantityData.setRecord(list);
    return quantityData;
  }

  public List<ReworkStatData> machineReworkStat(Map<String, Object> parameterMap) {
    List<ReworkStatData> list = queryResultList(MACHINE_STAT_MACHINE_REWORKSTAT, parameterMap, ReworkStatData.class);
    return  list.isEmpty() ? null : list;
  }

  public List<RoundnessRecheckDetail> machineRoundnessRecheck(Map<String, Object> parameterMap) {
    List<RoundnessRecheckDetail> list = queryResultList(MACHINE_STAT_RECHECK_DETAIL, parameterMap, RoundnessRecheckDetail.class);
    if (!list.isEmpty()) {
      return list;
    } else {
      throw PlatformException.badRequestException("查询结果为空，检查日期是否正确！");
    }
  }
}
