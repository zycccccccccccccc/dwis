package tech.hciot.dwis.business.application;

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
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.business.domain.OperatingTimeCtrRepository;
import tech.hciot.dwis.business.domain.ReleaseRecordRepository;
import tech.hciot.dwis.business.domain.model.ReleaseRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
public class ReleaseRecordService {

  @Autowired
  private ReleaseRecordRepository releaseRecordRepository;

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  public Page<ReleaseRecord> find(String wheelSerial, String reworkCode, String inspectorId,
      Integer currentPage, Integer pageSize) {
    Specification<ReleaseRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(reworkCode)) {
        list.add(criteriaBuilder.equal(root.get("reworkCode"), reworkCode));
      }
      if (StringUtils.isNotEmpty(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return releaseRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void add(ReleaseRecord releaseRecord) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      releaseRecord.setOpeDT(ope);
    });
    releaseRecord.setCreateDate(new Date());
    releaseRecordRepository.save(releaseRecord);
    /*WheelRecord wheelRecord = wheelRecordService.findWheel(releaseRecord.getWheelSerial());
    wheelRecord.setReworkCode("");
    wheelRecordService.save(wheelRecord);*/
  }
}
