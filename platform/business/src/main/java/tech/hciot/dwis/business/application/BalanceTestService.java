package tech.hciot.dwis.business.application;

import cn.hutool.core.date.DateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.BalanceTestRecordRepository;
import tech.hciot.dwis.business.domain.TestWheelRepository;
import tech.hciot.dwis.business.domain.model.BalanceTestRecord;
import tech.hciot.dwis.business.interfaces.dto.TestWheelResponse;

@Service
@Slf4j
public class BalanceTestService {

  private static final String DATE_FORMAT_STR = "yyyy-MM-dd";

  @Autowired
  private BalanceTestRecordRepository balanceTestRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private TestWheelRepository testWheelRepository;

  public Page<BalanceTestRecord> find(String inspectorId, String shiftNo,
                                 Integer currentPage, Integer pageSize) {
    Specification<BalanceTestRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (shiftNo != null) {
        list.add(criteriaBuilder.equal(root.get("shiftNo"), shiftNo));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return balanceTestRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<TestWheelResponse> findWheelList(String wheelSerial, Integer limit) {
    return testWheelRepository.findByWheelSerial(wheelSerial, "balancetest", limit).stream()
      .map(testWheel -> TestWheelResponse.builder()
        .wheelSerial(testWheel.getWheelSerial())
        .design(testWheel.getDesign())
        .build())
      .collect(Collectors.toList());
  }

  public Integer add(BalanceTestRecord record) {
    record.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxTimes(record);
    record.setCreateTime(new Date());
    balanceTestRecordRepository.save(record);
    return record.getId();
  }

  // Ts的值为BALANCE_Test_Record表中当前当班工长号、班次、试验日期的数据的ts（试验次数）最大值+1
  private void updateMaxTimes(BalanceTestRecord record) {
    String beginDate = DateUtil.format(record.getOpeDT(), DATE_FORMAT_STR);
    String endDate = DateUtil.format(DateUtils.addDays(record.getOpeDT(), 1), DATE_FORMAT_STR);
    Integer maxTimes = balanceTestRecordRepository
      .findMaxTimes(record.getInspectorId(), record.getShiftNo(), beginDate, endDate) + 1;
    record.setTs(maxTimes);
  }
}
