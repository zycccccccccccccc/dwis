package tech.hciot.dwis.business.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.business.infrastructure.log.domain.OperationLogRepository;
import tech.hciot.dwis.business.infrastructure.log.domain.model.OperationLog;


@Service
@Slf4j
public class OperationLogService {

  @Autowired
  private OperationLogRepository operationLogRepository;

  public Page<OperationLog> findAllOperationLog(String username,
      String operationName,
      Long operationTimeStart,
      Long operationTimeEnd,
      Boolean success,
      String errorDesc,
      String operationType,
      Integer currentPage,
      Integer pageSize, JwtTokenUser user) {
    Specification<OperationLog> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (user.getDepId() == 0) {
        if (StringUtils.isNotBlank(username)) {
          list.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
        }
      } else {
        list.add(criteriaBuilder.equal(root.get("username"), user.getUsername()));
      }
      if (StringUtils.isNotBlank(operationName)) {
        list.add(criteriaBuilder.like(root.get("operationName"), "%" + operationName + "%"));
      }
      if (operationTimeStart != null) {
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("operationTime"), new Date(operationTimeStart)));
      }
      if (operationTimeEnd != null) {
        list.add(criteriaBuilder.lessThanOrEqualTo(root.get("operationTime"), new Date(operationTimeEnd)));
      }
      if (success != null) {
        if (success) {
          list.add(criteriaBuilder.equal(root.get("errorCode"), 0));
        } else {
          list.add(criteriaBuilder.notEqual(root.get("errorCode"), 0));
        }
      }
      if (StringUtils.isNotBlank(errorDesc)) {
        list.add(criteriaBuilder.like(root.get("errorDesc"), "%" + errorDesc + "%"));
      }
      if (StringUtils.isNotBlank(operationType)) {
        list.add(criteriaBuilder.equal(root.get("operationType"), operationType));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("operationTime")));
      return query.getRestriction();
    };
    Page<OperationLog> operationLogs = operationLogRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
    return operationLogs;
  }

  //根据id查询操作信息..
  public Optional<OperationLog> findById(String id) {
    return operationLogRepository.findById(id);
  }

  public void add(OperationLog operationLog) {
    operationLogRepository.save(operationLog);
  }
}
