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
import tech.hciot.dwis.business.domain.ScrapRecordRepository;
import tech.hciot.dwis.business.domain.model.ScrapRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
public class ScrapRecordService {

  @Autowired
  private ScrapRecordRepository scrapRecordRepository;

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  public Page<ScrapRecord> find(String inspectorId, String wheelSerial, String design, String scrapCode, Integer confirmedScrap,
      Integer currentPage, Integer pageSize) {
    Specification<ScrapRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(design)) {
        list.add(criteriaBuilder.equal(root.get("design"), design));
      }
      if (StringUtils.isNotEmpty(scrapCode)) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (confirmedScrap != null) {
        list.add(criteriaBuilder.equal(root.get("confirmedScrap"), confirmedScrap));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return scrapRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void add(ScrapRecord scrapRecord) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      scrapRecord.setOpeDT(ope);
    });

    scrapRecord.setConfirmedScrap(1);
    scrapRecord.setCreateDate(new Date());
    scrapRecordRepository.save(scrapRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(scrapRecord.getWheelSerial());

    wheelRecord.setConfirmedScrap(1);
    wheelRecord.setScrapDate(new Date());
    wheelRecordService.save(wheelRecord);
  }

  @Transactional
  public void correct(ScrapRecord scrapRecord) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      scrapRecord.setOpeDT(ope);
    });

    scrapRecord.setConfirmedScrap(0);
    scrapRecord.setFormerScrapCode(StringUtils.defaultString(scrapRecord.getScrapCode(), ""));
    scrapRecord.setScrapCode("");
    scrapRecord.setCreateDate(new Date());
    scrapRecordRepository.save(scrapRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(scrapRecord.getWheelSerial());

    wheelRecord.setConfirmedScrap(0);
    wheelRecord.setScrapDate(null);
    wheelRecordService.save(wheelRecord);
  }
}
