package tech.hciot.dwis.business.application;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import java.math.BigDecimal;
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
import tech.hciot.dwis.business.domain.ShotTestRecordRepository;
import tech.hciot.dwis.business.domain.model.ShotTestRecord;

@Service
@Slf4j
public class ShotTestService {

  private static final String DATE_FORMAT_STR = "yyyy-MM-dd";

  @Autowired
  private ShotTestRecordRepository shotTestRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<ShotTestRecord> find(String inspectorId,
                                   String shiftNo,
                                   String beginDateStr,
                                   String endDateStr,
                                   Integer currentPage,
                                   Integer pageSize) {
    Specification<ShotTestRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (shiftNo != null) {
        list.add(criteriaBuilder.equal(root.get("shiftNo"), shiftNo));
      }
      if (beginDateStr != null) {
        Date beginDate = DateUtil.parseDate(beginDateStr);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("opeDT"), beginDate));
      }
      if (endDateStr != null) {
        Date endDate = DateUtils.addDays(DateUtil.parseDate(endDateStr), 1);
        list.add(criteriaBuilder.lessThanOrEqualTo(root.get("opeDT"), endDate));
      }
      if (ObjectUtil.isAllEmpty(beginDateStr, endDateStr)) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return shotTestRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Integer add(ShotTestRecord record) {
    record.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxTimes(record);
    record.setCreateTime(new Date());
    shotTestRecordRepository.save(record);
    return record.getId();
  }

  public void modify(ShotTestRecord record) {
    shotTestRecordRepository.save(record);
  }

  // ts的值为ShotTest_Record表中当前当班工长号、班次、试验日期（同一天）的数据的ts（试验次数）最大值+1
  private void updateMaxTimes(ShotTestRecord record) {
    String beginDate = DateUtil.format(record.getOpeDT(), DATE_FORMAT_STR);
    String endDate = DateUtil.format(DateUtils.addDays(record.getOpeDT(), 1), DATE_FORMAT_STR);
    Integer maxTimes = shotTestRecordRepository
      .findMaxTimes(record.getInspectorId(), record.getShiftNo(), beginDate, endDate)
      .orElse(new BigDecimal(0)).intValue() + 1;
    record.setTs(maxTimes);
  }
}
