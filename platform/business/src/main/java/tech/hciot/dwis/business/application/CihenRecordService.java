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
import tech.hciot.dwis.business.domain.CihenRecordPreRepository;
import tech.hciot.dwis.business.domain.CihenRecordRepository;
import tech.hciot.dwis.business.domain.OperatingTimeCtrRepository;
import tech.hciot.dwis.business.domain.model.CihenRecord;
import tech.hciot.dwis.business.domain.model.CihenRecordPre;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
public class CihenRecordService {

  @Autowired
  private CihenRecordRepository cihenRecordRepository;

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private CihenRecordPreRepository cihenRecordPreRepository;

  public Page<CihenRecord> find(String wheelSerial, String foreCihenCode, String cihenCode, String scrapCode, String inspectorId,
      Integer currentPage, Integer pageSize) {
    Specification<CihenRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(foreCihenCode)) {
        list.add(criteriaBuilder.equal(root.get("foreCihenCode"), foreCihenCode));
      }
      if (StringUtils.isNotEmpty(cihenCode)) {
        list.add(criteriaBuilder.equal(root.get("cihenCode"), cihenCode));
      }
      if (StringUtils.isNotEmpty(scrapCode)) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
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
    return cihenRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void add(CihenRecord cihenRecord) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      cihenRecord.setOpeDT(ope);
    });
    Integer maxTs = cihenRecordRepository.getMaxTs(cihenRecord.getWheelSerial());
    cihenRecord.setTs(maxTs == null ? 1 : maxTs + 1);
    cihenRecord.setCreateDate(new Date());
    cihenRecordRepository.save(cihenRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(cihenRecord.getWheelSerial());
    wheelRecord.setCihenCode(cihenRecord.getCihenCode());
    wheelRecordService.save(wheelRecord);
  }

  public Page<CihenRecordPre> findPre(String wheelSerial, String inspectorId, Integer currentPage, Integer pageSize) {
    Specification<CihenRecordPre> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
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
    return cihenRecordPreRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void addPre(CihenRecordPre cihenRecordPre) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      cihenRecordPre.setOpeDT(ope);
    });
    Integer maxTs = cihenRecordPreRepository.getMaxTs(cihenRecordPre.getWheelSerial());
    cihenRecordPre.setTs(maxTs == null ? 1 : maxTs + 1);
    cihenRecordPre.setCreateDate(new Date());
    cihenRecordPreRepository.save(cihenRecordPre);

    if (StringUtils.isNotEmpty(cihenRecordPre.getScrapCode())) {
      WheelRecord wheelRecord = wheelRecordService.findWheel(cihenRecordPre.getWheelSerial());
      wheelRecord.setScrapCode(cihenRecordPre.getScrapCode());
      wheelRecordService.save(wheelRecord);
    }
  }
}
