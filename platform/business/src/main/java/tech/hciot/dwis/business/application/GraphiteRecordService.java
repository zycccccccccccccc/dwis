package tech.hciot.dwis.business.application;

import cn.hutool.core.util.ObjectUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.domain.GraphiteRecordRepository;
import tech.hciot.dwis.business.domain.GraphiteRepository;
import tech.hciot.dwis.business.domain.model.Graphite;
import tech.hciot.dwis.business.domain.model.GraphiteRecord;

@Service
@Slf4j
public class GraphiteRecordService {

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private GraphiteRepository graphiteRepository;

  @Autowired
  private GraphiteRecordRepository graphiteRecordRepository;

  public Page<GraphiteRecord> find(String graphiteKey,
                                   String graphite,
                                   String design,
                                   Integer cd,
                                   String reworkCode,
                                   String scrapCode,
                                   String processDateStr,
                                   String opType,
                                   Integer currentPage,
                                   Integer pageSize) {
    Specification<GraphiteRecord> specification =
        (root, query, criteriaBuilder) -> {
          List<Predicate> list = new ArrayList<>();
          if (graphiteKey != null) {
            list.add(criteriaBuilder.equal(root.get("graphiteKey"), graphiteKey));
          }
          if (graphite != null) {
            list.add(criteriaBuilder.equal(root.get("graphite"), graphite));
          }
          if (design != null) {
            list.add(criteriaBuilder.equal(root.get("design"), design));
          }
          if (cd != null) {
            list.add(criteriaBuilder.equal(root.get("cd"), cd));
          }
          if (reworkCode != null) {
            list.add(criteriaBuilder.equal(root.get("reworkCode"), reworkCode));
          }
          if (scrapCode != null) {
            list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
          }

          Date processDate = StandardTimeUtil.parseDate(processDateStr);
          if (processDate != null) {
            list.add(criteriaBuilder.equal(root.get("processDate"), processDate));
          }

          if (ObjectUtil.isAllEmpty(graphite, design, cd, reworkCode, scrapCode, processDateStr)) {
            Date last = DateUtils.addHours(new Date(), -12);
            list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
          }

          switch (opType) {
            case Graphite.GRAPHITE_NO_PROCESS:
              list.add(criteriaBuilder.equal(root.get("status"), Graphite.STATUS_PROCESSED));
              list.add(criteriaBuilder.isNull(root.get("reGraphite")));
              break;
            case Graphite.GRAPHITE_NO_REWORK:
              list.add(criteriaBuilder.equal(root.get("status"), Graphite.STATUS_REWORK));
              break;
            case Graphite.GRAPHITE_NO_MODIFY_GRAPHITE_NO:
              list.add(criteriaBuilder.equal(root.get("status"), Graphite.STATUS_PROCESSED));
              list.add(criteriaBuilder.isNotNull(root.get("reGraphite")));
              break;
            case Graphite.GRAPHITE_NO_UP:
              list.add(criteriaBuilder.equal(root.get("status"), Graphite.STATUS_UP));
              break;
            case Graphite.GRAPHITE_NO_DOWN:
              list.add(criteriaBuilder.equal(root.get("status"), Graphite.STATUS_DOWN));
              break;
            case Graphite.GRAPHITE_NO_SCRAP:
              list.add(criteriaBuilder.equal(root.get("status"), Graphite.STATUS_SCRAP));
              break;
          }
          query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
          query.orderBy(criteriaBuilder.desc(root.get("createTime")));
          return query.getRestriction();
        };
    return graphiteRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<String> findGraphiteList(String graphite, String opType, Integer limit) {
    return graphiteRecordRepository.findGraphiteList(graphite, limit);
  }

  public Page<GraphiteRecord> processListDetail(String graphiteKey,
                                                Integer currentPage,
                                                Integer pageSize) {
    Specification<GraphiteRecord> specification =
      (root, query, criteriaBuilder) -> {
        List<Predicate> list = new ArrayList<>();
        if (graphiteKey != null) {
          list.add(criteriaBuilder.equal(root.get("graphiteKey"), graphiteKey));
        }
        query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
        query.orderBy(criteriaBuilder.asc(root.get("processDate")));
        return query.getRestriction();
      };
    return graphiteRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }
}
