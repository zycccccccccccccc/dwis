package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.dto.PageDataResponse;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.domain.GraphiteFirmRepository;
import tech.hciot.dwis.business.domain.GraphiteProcessRepository;
import tech.hciot.dwis.business.domain.GraphiteRecordRepository;
import tech.hciot.dwis.business.domain.GraphiteRepository;
import tech.hciot.dwis.business.domain.GraphiteScrapRepository;
import tech.hciot.dwis.business.domain.model.Graphite;
import tech.hciot.dwis.business.domain.model.GraphiteFirm;
import tech.hciot.dwis.business.domain.model.GraphitePour;
import tech.hciot.dwis.business.domain.model.GraphiteProcess;
import tech.hciot.dwis.business.domain.model.GraphiteRecord;
import tech.hciot.dwis.business.domain.model.GraphiteScrap;

@Service
@Slf4j
public class GraphiteService {

  @Autowired
  private GraphiteRepository graphiteRepository;

  @Autowired
  private GraphiteRecordRepository graphiteRecordRepository;

  @Autowired
  private GraphiteFirmRepository graphiteFirmRepository;

  @Autowired
  private GraphiteScrapRepository graphiteScrapRepository;

  @Autowired
  private GraphiteProcessRepository graphiteProcessRepository;

  @Autowired
  private EntityManager entityManager;

  public Page<Map<String, Object>> findMaxGraphite(Integer cd, Integer currentPage, Integer pageSize) {
    Page<Map<String, Object>> page = graphiteRepository.findMaxGraphite(cd, PageRequest.of(currentPage, pageSize));
    return page;
  }

  public List<Graphite> findGraphiteList(String graphite, String opType, Integer limit) {
    List<Integer> statusList = new ArrayList<>();

    switch (opType) {
      case Graphite.GRAPHITE_NO_REWORK:
        statusList.add(Graphite.STATUS_PROCESSED);
        statusList.add(Graphite.STATUS_DOWN);
        break;
      case Graphite.GRAPHITE_NO_MODIFY_GRAPHITE_NO:
        statusList.add(Graphite.STATUS_PROCESSED);
        statusList.add(Graphite.STATUS_DOWN);
        statusList.add(Graphite.STATUS_REWORK);
        break;
      case Graphite.GRAPHITE_NO_UP:
        statusList.add(Graphite.STATUS_PROCESSED);
        statusList.add(Graphite.STATUS_DOWN);
        statusList.add(Graphite.STATUS_REWORK);
        break;
      case Graphite.GRAPHITE_NO_DOWN:
        statusList.add(Graphite.STATUS_UP);
        break;
      case Graphite.GRAPHITE_NO_SCRAP:
        statusList.add(Graphite.STATUS_PROCESSED);
        statusList.add(Graphite.STATUS_DOWN);
        statusList.add(Graphite.STATUS_REWORK);
        break;
    }
    return graphiteRepository.findGraphiteList(graphite, statusList, limit);
  }

  @Transactional
  public void modify(Integer id, GraphiteRecord newGraphiteRecord) {
    graphiteRecordRepository.findById(id).map(graphiteRecord -> {
      Integer preHeight = graphiteRecord.getHeight() + graphiteRecord.getProcessSize();
      BeanUtil.copyProperties(newGraphiteRecord, graphiteRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      Integer processSize = preHeight - graphiteRecord.getHeight();
      graphiteRecord.setProcessSize(processSize);
      graphiteRecordRepository.save(graphiteRecord);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("石墨记录不存在"));

    newGraphiteRecord.setId(null);
    graphiteRepository.findByGrId(id).map(graphite -> {
      BeanUtil.copyProperties(newGraphiteRecord, graphite, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      graphiteRepository.save(graphite);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("石墨不存在"));
  }

  @Transactional
  public void delete(Integer id) {
    graphiteRecordRepository.findById(id).ifPresent(graphiteRecord -> {
      graphiteRepository.findByGrId(id).ifPresent(graphite -> {
        graphiteRepository.deleteById(graphite.getId());
        graphiteRecordRepository.deleteById(id);
      });
    });
  }

  @Transactional
  public void process(GraphiteRecord request) {
    checkGraphiteFirm(request.getGraphiteKey());
    assertGraphiteNotExists(request.getGraphite());
    graphiteFirmRepository.findByGraphiteKey(request.getGraphiteKey()).ifPresent(graphiteFirm -> {
      graphiteFirm.setStatus(GraphiteFirm.STATUS_PROCESSED);
      graphiteFirmRepository.save(graphiteFirm);
    });

    request.setStatus(Graphite.STATUS_PROCESSED);
    graphiteRecordRepository.save(request);

    Graphite graphite = Graphite.builder().build();
    BeanUtil.copyProperties(request, graphite, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    graphite.setId(null);
    graphite.setGrId(request.getId());
    graphiteRepository.save(graphite);
  }

  @Transactional
  public void rework(GraphiteRecord request) {
    graphiteRepository.findByGraphite(request.getGraphite()).map(graphite -> {
      request.setGraphiteKey(graphite.getGraphiteKey());
      request.setDesign(graphite.getDesign());
      request.setCd(graphite.getCd());
      request.setProcessSize(graphite.getHeight() - request.getHeight());
      request.setStatus(Graphite.STATUS_REWORK);
      graphiteRecordRepository.save(request);

      graphite.setGrId(request.getId());
      graphite.setProcessDate(request.getProcessDate());
      graphite.setHeight(request.getHeight());
      graphite.setStatus(Graphite.STATUS_REWORK);
      graphite.setRepairTimes(graphite.getRepairTimes() == null ? 1 : graphite.getRepairTimes() + 1);
      graphiteRepository.save(graphite);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("石墨号不存在"));
  }

  @Transactional
  public void modifyNo(GraphiteRecord request) {
    assertGraphiteNotExists(request.getGraphite());
    graphiteRepository.findByGraphite(request.getReGraphite()).map(reGraphite -> {
      // 原石墨加工记录
      GraphiteRecord reGraphiteRecord = GraphiteRecord.builder()
        .graphiteOpeId(request.getGraphiteOpeId())
        .graphiteKey(reGraphite.getGraphiteKey())
        .reDesign(reGraphite.getDesign())
        .reGraphite(request.getReGraphite())
        .cd(reGraphite.getCd())
        .processId(request.getProcessId())
        .height(reGraphite.getHeight())
        .status(Graphite.STATUS_MODIFY_GRAPHITE_NO)
        .processDate(request.getProcessDate())
        .build();
      graphiteRecordRepository.save(reGraphiteRecord);

      // 新石墨加工记录
      GraphiteRecord newGraphiteRecord = GraphiteRecord.builder().build();
      BeanUtil.copyProperties(reGraphiteRecord, newGraphiteRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      newGraphiteRecord.setId(null);
      newGraphiteRecord.setGraphite(request.getGraphite());
      newGraphiteRecord.setDesign(request.getDesign());
      newGraphiteRecord.setHeight(request.getHeight());
      newGraphiteRecord.setReworkCode(request.getReworkCode());
      newGraphiteRecord.setProcessSize(reGraphite.getHeight() - request.getHeight());
      newGraphiteRecord.setStatus(Graphite.STATUS_PROCESSED);
      graphiteRecordRepository.save(newGraphiteRecord);

      // 原石墨
      reGraphite.setGrId(reGraphiteRecord.getId());
      reGraphite.setProcessDate(reGraphiteRecord.getProcessDate());
      reGraphite.setStatus(Graphite.STATUS_MODIFY_GRAPHITE_NO);
      reGraphite.setRedesignTimes(Optional.ofNullable(reGraphite.getRedesignTimes()).orElse(0) + 1);
      graphiteRepository.save(reGraphite);

      // 新石墨
      Graphite graphite = Graphite.builder()
        .grId(newGraphiteRecord.getId())
        .graphiteKey(newGraphiteRecord.getGraphiteKey())
        .graphite(newGraphiteRecord.getGraphite())
        .design(newGraphiteRecord.getDesign())
        .cd(newGraphiteRecord.getCd())
        .height(newGraphiteRecord.getHeight())
        .processDate(newGraphiteRecord.getProcessDate())
        .redesignTimes(reGraphite.getRedesignTimes())
        .status(Graphite.STATUS_PROCESSED)
        .build();
      graphiteRepository.save(graphite);

      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("原石墨号不存在"));
  }

  @Transactional
  public void up(GraphiteRecord request) {
    graphiteRepository.findByGraphite(request.getGraphite()).map(graphite -> {
      if (graphite.getStatus() == Graphite.STATUS_UP) {
        throw PlatformException.badRequestException("石墨已上线");
      }

      GraphiteRecord graphiteRecord = GraphiteRecord.builder()
        .graphiteOpeId(request.getGraphiteOpeId())
        .graphite(request.getGraphite())
        .graphiteKey(graphite.getGraphiteKey())
        .design(graphite.getDesign())
        .cd(graphite.getCd())
        .processId(request.getProcessId())
        .processDate(request.getProcessDate())
        .height(graphite.getHeight())
        .status(Graphite.STATUS_UP)
        .build();
      graphiteRecordRepository.save(graphiteRecord);

      graphite.setGrId(graphiteRecord.getId());
      graphite.setStatus(Graphite.STATUS_UP);
      graphite.setProcessDate(request.getProcessDate());
      graphite.setUpTimes(Optional.ofNullable(graphite.getUpTimes()).orElse(0) + 1);
      graphiteRepository.save(graphite);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("石墨号不存在"));
  }

  @Transactional
  public void down(GraphiteRecord request) {
    graphiteRepository.findByGraphite(request.getGraphite()).map(graphite -> {
      if (graphite.getStatus() == Graphite.STATUS_DOWN) {
        throw PlatformException.badRequestException("石墨已下线");
      }

      GraphiteRecord graphiteRecord = GraphiteRecord.builder()
        .graphiteOpeId(request.getGraphiteOpeId())
        .graphite(request.getGraphite())
        .graphiteKey(graphite.getGraphiteKey())
        .design(graphite.getDesign())
        .cd(graphite.getCd())
        .processId(request.getProcessId())
        .processDate(request.getProcessDate())
        .height(graphite.getHeight())
        .status(Graphite.STATUS_DOWN)
        .build();
      graphiteRecordRepository.save(graphiteRecord);

      graphite.setGrId(graphiteRecord.getId());
      graphite.setStatus(Graphite.STATUS_DOWN);
      graphite.setProcessDate(request.getProcessDate());
      graphite.setDownTimes(Optional.ofNullable(graphite.getDownTimes()).orElse(0) + 1);
      graphiteRepository.save(graphite);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("石墨号不存在"));
  }

  @Transactional
  public void scrap(GraphiteRecord request) {
    graphiteRepository.findByGraphite(request.getGraphite()).map(graphite -> {
      if (graphite.getStatus() == Graphite.STATUS_SCRAP) {
        throw PlatformException.badRequestException("石墨已报废");
      }

      GraphiteRecord graphiteRecord = GraphiteRecord.builder()
        .graphiteOpeId(request.getGraphiteOpeId())
        .graphite(request.getGraphite())
        .graphiteKey(graphite.getGraphiteKey())
        .design(graphite.getDesign())
        .cd(graphite.getCd())
        .processId(request.getProcessId())
        .processDate(request.getProcessDate())
        .height(graphite.getHeight())
        .scrapCode(request.getScrapCode())
        .status(Graphite.STATUS_SCRAP)
        .build();
      graphiteRecordRepository.save(graphiteRecord);

      graphite.setGrId(graphiteRecord.getId());
      graphite.setStatus(Graphite.STATUS_SCRAP);
      graphite.setProcessDate(request.getProcessDate());
      graphite.setScrapCode(request.getScrapCode());
      graphiteRepository.save(graphite);

      graphiteFirmRepository.findByGraphiteKey(graphite.getGraphiteKey()).map(graphiteFirm -> {
        graphiteFirm.setStatus(GraphiteFirm.STATUS_SCRAP);
        graphiteFirm.setScrapCode(request.getScrapCode());
        graphiteFirm.setScrapDate(request.getProcessDate());
        graphiteFirmRepository.save(graphiteFirm);
        return Optional.empty();
      }).orElseThrow(() -> PlatformException.badRequestException("原始石墨号不存在"));

      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("石墨号不存在"));
  }

  public List<String> findGraphiteCDList(Integer cdType) {
    return graphiteRepository.findGraphiteCDList(cdType);
  }


  private void checkGraphiteFirm(String graphiteKey) {
    graphiteFirmRepository.findByGraphiteKey(graphiteKey).map(graphiteFirm -> {
      if (graphiteFirm.getStatus() != GraphiteFirm.STATUS_UNPROCESS) {
        throw PlatformException.badRequestException("原始石墨已加工");
      }
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("原始石墨号不存在"));
  }

  private void assertGraphiteNotExists(String graphite) {
    graphiteRepository.findByGraphite(graphite).ifPresent(graphite1 -> {
      throw PlatformException.badRequestException("石墨号已存在");
    });
  }

  public PageDataResponse<GraphitePour> getPourInfo(String graphite,
                                                    String beginDate,
                                                    String endDate,
                                                    Integer currentPage,
                                                    Integer pageSize) {
    String parameter = "";
    if (StringUtils.isNotBlank(graphite)) {
      parameter = parameter + "AND graphite = '" + graphite + "' ";
    }
    if (StringUtils.isNotBlank(beginDate)) {
      parameter = parameter + "AND cast_date >= '" + beginDate + "' ";
    }
    if (StringUtils.isNotBlank(endDate)) {
      parameter = parameter + "AND cast_date <= '" + endDate + "' ";
    }

    String sql = "SELECT g.graphite, g.cd, COUNT(1) AS cnt, g.status "
               + "FROM graphite g JOIN pour_record p ON p.drag_no = g.graphite "
               + "WHERE 1 = 1 " + parameter
               + "GROUP BY g.graphite, g.cd, g.status "
               + "UNION ALL"
               + " SELECT g.graphite, g.cd, COUNT(1) AS cnt, g.status "
               + "FROM graphite g JOIN pour_record p ON p.cope_no = g.graphite "
               + "WHERE 1 = 1 " + parameter
               + "GROUP BY g.graphite, g.cd, g.status "
               + "ORDER BY graphite";

    String countSql = "SELECT COUNT(1) FROM ("
                    + "SELECT g.graphite, g.cd, COUNT(1) AS cnt, g.status "
                    + "FROM graphite g JOIN pour_record p ON p.drag_no = g.graphite "
                    + "WHERE 1 = 1 " + parameter
                    + "GROUP BY g.graphite, g.cd, g.status "
                    + "UNION ALL "
                    + "SELECT g.graphite, g.cd, COUNT(1) AS cnt, g.status "
                    + "FROM graphite g JOIN pour_record p ON p.cope_no = g.graphite "
                    + "WHERE 1 = 1 " + parameter
                    + "GROUP BY g.graphite, g.cd, g.status"
                    + ") t";

    try {
      Query query = entityManager.createNativeQuery(sql);
      query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
      query.setFirstResult(currentPage * pageSize);
      query.setMaxResults(pageSize);
      List<GraphitePour> resultList = query.getResultList();

      query = entityManager.createNativeQuery(countSql);
      int total = (int) query.getSingleResult();
      PageDataResponse<GraphitePour> page = new PageDataResponse(total, total / pageSize,
        pageSize, currentPage, resultList);
      return page;
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return new PageDataResponse<>(0, 0,
      pageSize, currentPage, null);
  }

  public Page<GraphiteScrap> getGraphiteScrap(String graphiteKey,
                                              String beginDate,
                                              String endDate,
                                              String scrapCode,
                                              Integer currentPage,
                                              Integer pageSize) {
    Specification<GraphiteScrap> specification =
      (root, query, criteriaBuilder) -> {
        List<Predicate> list = new ArrayList<>();
        if (graphiteKey != null) {
          list.add(criteriaBuilder.equal(root.get("graphiteKey"), graphiteKey));
        }
        if (scrapCode != null) {
          list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
        }
        if (beginDate != null) {
          list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("scrapDate"), beginDate));
        }
        if (endDate != null) {
          list.add(criteriaBuilder.lessThanOrEqualTo(root.get("scrapDate"), endDate));
        }
        query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
        query.orderBy(criteriaBuilder.desc(root.get("scrapDate")));
        return query.getRestriction();
      };
    return graphiteScrapRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Page<GraphiteProcess> processListForGraphiteProcess(String graphiteKey,
                                                             String beginDateStr,
                                                             String endDateStr) {
    Specification<GraphiteProcess> specification =
      (root, query, criteriaBuilder) -> {
        List<Predicate> list = new ArrayList<>();
        if (graphiteKey != null) {
          list.add(criteriaBuilder.equal(root.get("graphiteKey"), graphiteKey));
        }
        if (beginDateStr != null) {
          Date beginDate = StandardTimeUtil.parseDate(beginDateStr);
          list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("processDate"), beginDate));
        }
        if (endDateStr != null) {
          Date endDate = StandardTimeUtil.parseDate(endDateStr);
          list.add(criteriaBuilder.lessThanOrEqualTo(root.get("processDate"), endDate));
        }
        query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
        query.orderBy(criteriaBuilder.desc(root.get("processDate")));
        return query.getRestriction();
      };
    return graphiteProcessRepository.findAll(specification, PageRequest.of(0, 1));
  }

  public Page<GraphiteRecord> processListForGraphiteRecord(String beginDateStr,
                                                           String endDateStr,
                                                           Integer currentPage,
                                                           Integer pageSize) {
    Specification<GraphiteRecord> specification =
      (root, query, criteriaBuilder) -> {
        List<Predicate> list = new ArrayList<>();
        if (beginDateStr != null) {
          Date beginDate = StandardTimeUtil.parseDate(beginDateStr);
          list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("processDate"), beginDate));
        }
        if (endDateStr != null) {
          Date endDate = StandardTimeUtil.parseDate(endDateStr);
          list.add(criteriaBuilder.lessThanOrEqualTo(root.get("processDate"), endDate));
        }
        query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
        query.orderBy(criteriaBuilder.desc(root.get("processDate")));
        return query.getRestriction();
      };
    return graphiteRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }
}
