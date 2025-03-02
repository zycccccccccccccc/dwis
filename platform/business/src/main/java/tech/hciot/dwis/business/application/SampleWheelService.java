package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_SERIAL_EXIST;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.SampleWheelRecordRepository;
import tech.hciot.dwis.business.domain.model.SampleWheelRecord;

@Service
public class SampleWheelService {

  @Autowired
  private SampleWheelRecordRepository sampleWheelRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<SampleWheelRecord> find(String inspectorId, Integer currentPage, Integer pageSize) {
    Specification<SampleWheelRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("opeDT")));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return sampleWheelRecordRepository.findAll(specification, pageable);
  }

  public void add(SampleWheelRecord sampleWheelRecord) {
    if (sampleWheelRecordRepository.countByWheelSerial(sampleWheelRecord.getWheelSerial()) > 0) {
      throw WHEEL_SERIAL_EXIST.getPlatformException();
    }
    sampleWheelRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    sampleWheelRecord.setCreateTime(new Date());
    sampleWheelRecordRepository.save(sampleWheelRecord);
  }

  public void delete(Integer id) {
    sampleWheelRecordRepository.deleteById(id);
  }
}
