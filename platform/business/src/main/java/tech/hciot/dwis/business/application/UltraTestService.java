package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import java.math.BigDecimal;
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
import tech.hciot.dwis.business.domain.TestWheelRepository;
import tech.hciot.dwis.business.domain.UtTestRecordRepository;
import tech.hciot.dwis.business.domain.model.UtTestRecord;
import tech.hciot.dwis.business.interfaces.dto.TestWheelResponse;

@Service
@Slf4j
public class UltraTestService {

  private static final String DATE_FORMAT_STR = "yyyy-MM-dd";

  @Autowired
  private UtTestRecordRepository utTestRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private TestWheelRepository testWheelRepository;

  public Page<UtTestRecord> find(String inspectorId, String shiftNo,
                                 Integer currentPage, Integer pageSize) {
    Specification<UtTestRecord> specification = (root, query, criteriaBuilder) -> {
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
    return utTestRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<TestWheelResponse> findWheelList(String wheelSerial, Integer limit) {
    return testWheelRepository.findByWheelSerial(wheelSerial, "ultratest", limit).stream()
      .map(testWheel -> TestWheelResponse.builder()
        .wheelSerial(testWheel.getWheelSerial())
        .design(testWheel.getDesign())
        .build())
      .collect(Collectors.toList());
  }

  public Integer add(UtTestRecord record) {
    record.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    record.setCreateTime(new Date());
    updateMaxTimes(record);
    if (record.getWheelCheck() == 1) {
      UtTestRecord sampleRecord = UtTestRecord.builder().build();
      BeanUtil.copyProperties(sampleRecord, record, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    }
    utTestRecordRepository.save(record);
    return record.getId();
  }

  // Ts的值为UT_Test_Record表中当前当班工长号、班次、试验日期的数据的ts（试验次数）最大值+1
  private void updateMaxTimes(UtTestRecord record) {
    String beginDate = DateUtil.format(record.getOpeDT(), DATE_FORMAT_STR);
    String endDate = DateUtil.format(DateUtils.addDays(record.getOpeDT(), 1), DATE_FORMAT_STR);
    Integer maxTimes = utTestRecordRepository
      .findMaxTimes(record.getInspectorId(), record.getShiftNo(), beginDate, endDate)
      .orElse(new BigDecimal(0)).intValue() + 1;
    record.setTs(maxTimes);
  }
}
