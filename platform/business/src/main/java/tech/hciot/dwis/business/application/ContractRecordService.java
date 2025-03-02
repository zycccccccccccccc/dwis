package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.ContractRecordRepository;
import tech.hciot.dwis.business.domain.model.ContractRecord;

@Service
public class ContractRecordService {

  @Autowired
  private ContractRecordRepository contractRecordRepository;

  public void addContractRecord(ContractRecord contractRecord) {
    contractRecord.setEnabled(1);
    contractRecord.setShippedSum(0);
    contractRecord.setSurplusSum(contractRecord.getContractSum());
    contractRecord.setCreateTime(new Date());
    contractRecordRepository.save(contractRecord);
  }

  public Page<ContractRecord> find(String contractNo, Integer enabled, String createDate, Integer currentPage,
      Integer pageSize) {
    Specification<ContractRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(contractNo)) {
        list.add(criteriaBuilder.equal(root.get("contractNo"), contractNo));
      }
      if (enabled != null) {
        list.add(criteriaBuilder.equal(root.get("enabled"), enabled));
      }
      if (StringUtils.isNotBlank(createDate)) {
        Date date = parseDate(createDate);
        Date nextDay = DateUtils.addDays(date, 1);
        list.add(criteriaBuilder.between(root.get("createTime"), date, nextDay));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return contractRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public void closeContractRecord(Integer id) {
    contractRecordRepository.findById(id).ifPresent(contractRecord -> {
      contractRecord.setEnabled(0);
      contractRecord.setEndDate(new Date());
      contractRecordRepository.save(contractRecord);
    });
  }

  public List<ContractRecord> getList() {
    return contractRecordRepository.findByEnabled(1);
  }
}
