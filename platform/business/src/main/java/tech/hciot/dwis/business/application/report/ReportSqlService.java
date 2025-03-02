package tech.hciot.dwis.business.application.report;

import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.infrastructure.UnderlineCamelUtil;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;

@Service
@Slf4j
public class ReportSqlService {

  @Resource
  SqlTemplateParser sqlTemplateParser;

  @Autowired
  private EntityManager entityManager;

  public Object querySingleResult(String reportName,
                                  String templateName,
                                  Map<String, Object> parameterMap,
                                  Class z) {
    log.info("report {} - {} begin", reportName, templateName);
    String sql = sqlTemplateParser.parseSqlTemplate(reportName, templateName, parameterMap);
    Query query = entityManager.createNativeQuery(sql);
    parameterMap.entrySet().forEach(entry -> {
      if (entry.getValue() != null) {
        query.setParameter(entry.getKey(), entry.getValue());
      }
    });
    query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    try {
      Map<String, Object> resultMap = (Map<String, Object>) query.getSingleResult();
      Map<String, Object> humpMap = UnderlineCamelUtil.underlineMapToHumpMap(resultMap);
      log.info("report {} - {} finish", reportName, templateName);
      return new JSONObject(humpMap).toJavaObject(z);
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error("report " + reportName + " error: " + e.getMessage(), e);
    }
    return null;
  }

  public List queryResultList(String reportName,
                              String templateName,
                              Map<String, Object> parameterMap,
                              Class z) {
    log.info("report {} - {} begin", reportName, templateName);
    String sql = sqlTemplateParser.parseSqlTemplate(reportName, templateName, parameterMap);
    Query query = entityManager.createNativeQuery(sql);
    parameterMap.entrySet().forEach(entry -> {
      query.setParameter(entry.getKey(), entry.getValue());
    });
    query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    try {
      List<Map<String, Object>> resultList = query.getResultList();
      List objectList = new ArrayList<>();
      resultList.forEach(data -> {
        Map<String, Object> humpData = UnderlineCamelUtil.underlineMapToHumpMap(data);
        objectList.add(new JSONObject(humpData).toJavaObject(z));
      });
      log.info("report {} - {} finish", reportName, templateName);
      return objectList;
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error("report " + reportName + " error: " + e.getMessage(), e);
    }
    return new ArrayList<>();
  }

  public static void main(String[] args) {
    BigDecimal value = null;
    value = new BigDecimal(100.0 * 9 / 11).setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    System.out.println(value.toPlainString() + "%");
  }
}
