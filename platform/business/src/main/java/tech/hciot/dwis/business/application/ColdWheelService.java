package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseTime;

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
import tech.hciot.dwis.business.domain.ColdWheelRepository;
import tech.hciot.dwis.business.domain.OperatingTimeCtrRepository;
import tech.hciot.dwis.business.domain.model.ColdWheel;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.BucketResponse;
import tech.hciot.dwis.business.interfaces.dto.MarkColdWheelRequest;

@Service
public class ColdWheelService {

  @Autowired
  private ColdWheelRepository coldWheelRepository;

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private PourRecordService pourRecordService;

  public Page<ColdWheel> find(String inspectorId, String startDateTime, Integer currentPage, Integer pageSize) {
    Specification<ColdWheel> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (StringUtils.isNotEmpty(startDateTime)) {
        Date start = parseTime(startDateTime);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), start));
      } else {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      }

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return coldWheelRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void add(ColdWheel coldWheel) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      coldWheel.setOpeDT(ope);
    });

    coldWheel.setCreateDate(new Date());
    coldWheelRepository.save(coldWheel);

    WheelRecord wheelRecord = wheelRecordService.findWheel(coldWheel.getWheelSerial());
    wheelRecord.setSpecialMt(1);
    wheelRecordService.save(wheelRecord);
  }

  public Page<BucketResponse> findBucket(String castDate, Integer tapSeq, Integer currentPage, Integer pageSize) {
    return coldWheelRepository.findBuckets(castDate, tapSeq, PageRequest.of(currentPage, pageSize))
        .map(row -> BucketResponse.builder().castDate(row.get("cast_date").toString()).pitNo((Integer) row.get("pit_no"))
            .tapSeq((Integer) row.get("tap_seq")).pitSeq((Integer) row.get("pit_seq"))
            .build()
        );
  }

  @Transactional
  public void mark(MarkColdWheelRequest markColdWheelRequest) {
    Integer minute =
        operatingTimeCtrRepository.findByDep("QA").map(operatingTimeCtr -> operatingTimeCtr.getOperatingTime()).orElse(0);
    Date ope = DateUtils.addMinutes(new Date(), -minute);
    Date createDate = new Date();
    pourRecordService.findByPitSeq(markColdWheelRequest.getPitSeq()).forEach(pourRecord -> {
      String wheelSerial = pourRecord.getWheelSerial();
      WheelRecord wheelRecord = wheelRecordService.findWheel(wheelSerial);
      if (wheelRecord != null && wheelRecord.getFinished() == 0 && wheelRecord.getConfirmedScrap() == 0) {
        ColdWheel coldWheel = new ColdWheel();
        coldWheel.setWheelSerial(wheelSerial);
        coldWheel.setCastDate(markColdWheelRequest.getCastDate());
        coldWheel.setPitSeq(markColdWheelRequest.getPitSeq());
        coldWheel.setPitNo(markColdWheelRequest.getPitNo());
        coldWheel.setInspectorId(markColdWheelRequest.getInspectorId());
        coldWheel.setTapSeq(markColdWheelRequest.getTapSeq());
        coldWheel.setOpeDT(ope);
        coldWheel.setCreateDate(createDate);
        coldWheelRepository.save(coldWheel);

        wheelRecord.setSpecialMt(1);
        wheelRecordService.save(wheelRecord);
      }
    });
  }
}
