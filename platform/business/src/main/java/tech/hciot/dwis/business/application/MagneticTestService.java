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
import tech.hciot.dwis.business.domain.MaterialRecordRepository;
import tech.hciot.dwis.business.domain.MtTestRecordRepository;
import tech.hciot.dwis.business.domain.model.MtTestRecord;

@Service
@Slf4j
public class MagneticTestService {

  private static final String DATE_FORMAT_STR = "yyyy-MM-dd";

  @Autowired
  private MtTestRecordRepository mtTestRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private MaterialRecordRepository materialRecordRepository;

  public Page<MtTestRecord> find(String inspectorId,
                                 String shiftNo,
                                 String beginDateStr,
                                 String endDateStr,
                                 Integer currentPage,
                                 Integer pageSize) {
    Specification<MtTestRecord> specification = (root, query, criteriaBuilder) -> {
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
    return mtTestRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<String> findBatchNoList(Integer limit) {
    return materialRecordRepository.findBatchNoByNameAndDept("荧光磁粉", "QA", limit);
  }

  public Integer add(MtTestRecord record) {
    record.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxTimes(record);
    record.setCreateTime(new Date());
    mtTestRecordRepository.save(record);
    return record.getId();
  }

  // Ts的值为MT_Test_Record表中当前当班工长号、班次，探伤日期(同一天)的数据的ts（试验次数）最大值+1
  private void updateMaxTimes(MtTestRecord record) {
    String beginDate = DateUtil.format(record.getOpeDT(), DATE_FORMAT_STR);
    String endDate = DateUtil.format(DateUtils.addDays(record.getOpeDT(), 1), DATE_FORMAT_STR);
    Integer maxTimes = mtTestRecordRepository
      .findMaxTimes(record.getInspectorId(), record.getShiftNo(), beginDate, endDate)
      .orElse(new BigDecimal(0)).intValue() + 1;
    record.setTs(maxTimes);
  }

  public static void main(String[] args) {
    Date date = new Date();
    String beginDate = DateUtil.format(date, DATE_FORMAT_STR);
    String endDate = DateUtil.format(DateUtils.addDays(date, 1), DATE_FORMAT_STR);
    System.out.println(beginDate);
    System.out.println(endDate);
  }
}
