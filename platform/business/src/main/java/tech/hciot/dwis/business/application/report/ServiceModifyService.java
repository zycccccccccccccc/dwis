package tech.hciot.dwis.business.application.report;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.PourRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;

@Service
@Slf4j
public class ServiceModifyService {

  @Autowired
  private SqlTemplateParser sqlTemplateParser;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  // 热处理超时
  public PageDataResponse<Object> findHeatTimeout(String beginDate,
                                                  String endDate,
                                                  Integer currentPage,
                                                  Integer pageSize) {
    Query query = createQuery("service-modify-heat-timeout");
    query.setParameter("beginDate", beginDate);
    query.setParameter("endDate", endDate);
    int total = total(query);
    return queryPageData(query, total, currentPage, pageSize);
  }

  // 热处理代码
  public PageDataResponse<Object> findHeatCode(String beginDate,
                                               String endDate,
                                               Integer currentPage,
                                               Integer pageSize) {
    Query query = createQuery("service-modify-heat-code");
    query.setParameter("beginDate", beginDate);
    query.setParameter("endDate", endDate);
    int total = total(query);
    return queryPageData(query, total, currentPage, pageSize);
  }

  // 浇注
  public PageDataResponse<Object> findPour(String beginDate,
                                           Integer currentPage,
                                           Integer pageSize) {
    Query query = createQuery("service-modify-pour");
    query.setParameter("beginDate", beginDate);
    int total = total(query);
    return queryPageData(query, total, currentPage, pageSize);
  }

  // 开箱入桶时间
  public PageDataResponse<Object> findPitTime(String beginDate,
                                              String endDate,
                                              Integer currentPage,
                                              Integer pageSize) {
    Query query = createQuery("service-modify-pittime");
    query.setParameter("beginDate", beginDate);
    query.setParameter("endDate", endDate);
    int total = total(query);
    return queryPageData(query, total, currentPage, pageSize);
  }

  // 废品代码8
  public PageDataResponse<Object> findScrapCode8(String beginDate,
                                                String endDate,
                                                Integer currentPage,
                                                Integer pageSize) {
    Query query = createQuery("service-modify-scrapcode8");
    query.setParameter("beginDate", beginDate);
    query.setParameter("endDate", endDate);
    int total = total(query);
    return queryPageData(query, total, currentPage, pageSize);
  }

  @Transactional
  public void modifyScrapCode8(String wheelSerial) {
    pourRecordRepository.clearScrapCode(wheelSerial);
    wheelRecordRepository.clearScrapCode(wheelSerial);
  }

  // 轮型石墨
  public PageDataResponse<Object> findGraphite(String beginDate,
                                               String endDate,
                                               Integer currentPage,
                                               Integer pageSize) {
    Query query = createQuery("service-modify-graphite");
    query.setParameter("beginDate", beginDate);
    query.setParameter("endDate", endDate);
    int total = total(query);
    return queryPageData(query, total, currentPage, pageSize);
  }

  private Query createQuery(String sqlTemplateName) {
    String sql = sqlTemplateParser.parseSqlTemplate("service-modify", sqlTemplateName);
    Query query = entityManager.createNativeQuery(sql);
    query.unwrap(org.hibernate.SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    return query;
  }

  // 统计总数
  private int total(Query query) {
    try {
      List<Object> allList = query.getResultList();
      return allList.size();
    } catch (NoResultException e) {
      return 0;
    } catch (Exception e) {
      log.error("query total error: {}", e.getMessage());
      throw PlatformException.badRequestException("查询数据失败");
    }
  }

  // 分页返回数据
  private PageDataResponse<Object> queryPageData(Query query,
                                                 int total,
                                                 int currentPage,
                                                 int pageSize) {
    try {
      query.setFirstResult(currentPage * pageSize);
      query.setMaxResults(pageSize);
      List<Object> pageList = query.getResultList();
      return new PageDataResponse<>(total, total / pageSize + 1,
        pageSize, currentPage, pageList);
    } catch (NoResultException e) {
      return new PageDataResponse<>(0, 0, pageSize, 0, new ArrayList<>());
    } catch (Exception e) {
      log.error("query data error: {}", e.getMessage());
      throw PlatformException.badRequestException("查询数据失败");
    }
  }
}
