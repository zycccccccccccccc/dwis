package tech.hciot.dwis.business.application.report;

import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;

@Service
@Slf4j
public class SingleReportService {

  @Resource
  SqlTemplateParser sqlTemplateParser;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  public Map<String, Object> find(String wheelSerial) {
    wheelRecordRepository.findByWheelSerial(wheelSerial)
      .orElseThrow(() -> PlatformException.badRequestException("车轮记录不存在"));
    Map<String, Object> resultMap = new HashMap<>();

    Object wheel = querySingleResult("single-report-wheel", wheelSerial);
    resultMap.put("wheel", wheel);

    Object pour = querySingleResult("single-report-pour", wheelSerial);
    resultMap.put("pour", pour);

    Object chemistry = querySingleResult("single-report-chemistry", wheelSerial);
    resultMap.put("chemistry", chemistry);

    Object heat = querySingleResult("single-report-heat", wheelSerial);
    resultMap.put("heat", heat);

    Object stockAndShip = querySingleResult("single-report-stock-and-ship", wheelSerial);
    resultMap.put("stockAndShip", stockAndShip);

    Object machine = queryResultList("single-report-machine", wheelSerial);
    resultMap.put("machine", machine);

    Object quality = queryResultList("single-report-quality", wheelSerial);
    resultMap.put("quality", quality);
    return resultMap;
  }

  private Object querySingleResult(String templateName, String wheelSerial) {
    String sql = sqlTemplateParser.parseSqlTemplate("single-report", templateName);
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("wheelSerial", wheelSerial);
    query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error("single-report-wheel query error: {}", e.getMessage());
    }
    return new JSONObject();
  }

  private Object queryResultList(String templateName, String wheelSerial) {
    String sql = sqlTemplateParser.parseSqlTemplate("single-report", templateName);
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("wheelSerial", wheelSerial);
    query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error("single-report-wheel query error: {}", e.getMessage());
    }
    return new JSONObject();
  }
}
