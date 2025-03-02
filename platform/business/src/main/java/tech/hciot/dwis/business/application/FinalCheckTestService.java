package tech.hciot.dwis.business.application;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.HbtestRecordRepository;
import tech.hciot.dwis.business.domain.TapeTestingRecordRepository;
import tech.hciot.dwis.business.domain.TestWheelRepository;
import tech.hciot.dwis.business.domain.ThreehbRecordRepository;
import tech.hciot.dwis.business.domain.TroundRecordRepository;
import tech.hciot.dwis.business.domain.WheelDevRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.HbtestRecord;
import tech.hciot.dwis.business.domain.model.TapeTestingRecord;
import tech.hciot.dwis.business.domain.model.TestWheel;
import tech.hciot.dwis.business.domain.model.ThreehbRecord;
import tech.hciot.dwis.business.domain.model.TroundRecord;
import tech.hciot.dwis.business.domain.model.WheelDevRecord;

@Service
@Slf4j
public class FinalCheckTestService {

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private HbtestRecordRepository hbtestRecordRepository;

  @Autowired
  private ThreehbRecordRepository threehbRecordRepository;

  @Autowired
  private TroundRecordRepository troundRecordRepository;

  @Autowired
  private WheelDevRecordRepository wheelDevRecordRepository;

  @Autowired
  private TapeTestingRecordRepository tapeTestingRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private TestWheelRepository testWheelRepository;

  //布氏硬度机试验记录
  public Page<HbtestRecord> findHbtestRecord(String inspectorId, String shiftNo,
                                             Integer currentPage, Integer pageSize) {
    Specification<HbtestRecord> specification = (root, query, criteriaBuilder) -> {
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
    return hbtestRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public HbtestRecord add(HbtestRecord hbtestRecord) {
    hbtestRecord.setDevIndentaDia(hbtestRecord.getMIndentaDia().subtract(hbtestRecord.getIndentaDia()));
    hbtestRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    Integer maxIs = hbtestRecordRepository.findByMaxTs(hbtestRecord.getInspectorId(),
      hbtestRecord.getShiftNo(), hbtestRecord.getTestDate());
    hbtestRecord.setTs(maxIs + 1);
    hbtestRecord.setCreateTime(new Date());
    return hbtestRecordRepository.save(hbtestRecord);
  }


  // 轮辋外侧三点硬度检测
  public Page<ThreehbRecord> findThreehbRecord(String inspectorId, String shiftNo, Integer reTest,
                                               Integer currentPage, Integer pageSize) {
    Specification<ThreehbRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (shiftNo != null) {
        list.add(criteriaBuilder.equal(root.get("shiftNo"), shiftNo));
      }
      if (reTest != null) {
        list.add(criteriaBuilder.equal(root.get("reTest"), reTest));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return threehbRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public ThreehbRecord add(ThreehbRecord threehbRecord) {
    Integer min = ObjectUtils.min(threehbRecord.getBrinnel1(), threehbRecord.getBrinnel2(), threehbRecord.getBrinnel3());
    Integer max = ObjectUtils.max(threehbRecord.getBrinnel1(), threehbRecord.getBrinnel2(), threehbRecord.getBrinnel3());
    threehbRecord.setDifference(max - min);
    threehbRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    Integer maxIs = threehbRecordRepository.findByMaxTs(threehbRecord.getInspectorId(), threehbRecord.getWheelSerial());
    threehbRecord.setTs(maxIs + 1);
    threehbRecord.setCreateTime(new Date());
    return threehbRecordRepository.save(threehbRecord);
  }


  // 踏面机床首件圆度
  public Page<TroundRecord> findTroundRecord(String inspectorId, String shiftNo,
                                             Integer currentPage, Integer pageSize) {
    Specification<TroundRecord> specification = (root, query, criteriaBuilder) -> {
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
    return troundRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public TroundRecord add(TroundRecord troundRecord) {
    BigDecimal min = ObjectUtils.min(troundRecord.getBrinnel1(), troundRecord.getBrinnel2(),
      troundRecord.getBrinnel3(), troundRecord.getBrinnel4());
    BigDecimal max = ObjectUtils.max(troundRecord.getBrinnel1(), troundRecord.getBrinnel2(),
      troundRecord.getBrinnel3(), troundRecord.getBrinnel4());
    troundRecord.setRoundDiffer(max.subtract(min));
    troundRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    Integer maxIs = troundRecordRepository.findByMaxTs(troundRecord.getInspectorId(),troundRecord.getWheelSerial());
    troundRecord.setTs(maxIs + 1);
    troundRecord.setCreateTime(new Date());
    return troundRecordRepository.save(troundRecord);
  }


  // 车轮尺寸偏差检查
  public Page<WheelDevRecord> findWheelDevRecord(String inspectorId, String shiftNo,
                                                 Integer currentPage, Integer pageSize) {
    Specification<WheelDevRecord> specification = (root, query, criteriaBuilder) -> {
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
    return wheelDevRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public WheelDevRecord add(WheelDevRecord wheelDevRecord) {
    wheelDevRecord.setDiffRim(Math.abs(wheelDevRecord.getFrontRim() - wheelDevRecord.getBackRim()));
    wheelDevRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    Integer maxIs = wheelDevRecordRepository.findByMaxTs(wheelDevRecord.getInspectorId(), wheelDevRecord.getWheelSerial());
    wheelDevRecord.setTs(maxIs + 1);
    wheelDevRecord.setCreateTime(new Date());
    return wheelDevRecordRepository.save(wheelDevRecord);
  }


  // 带尺标准轮检测记录
  public Page<TapeTestingRecord> findTapeTestingRecord(String inspectorId, String shiftNo,
                                                       Integer currentPage, Integer pageSize) {
    Specification<TapeTestingRecord> specification = (root, query, criteriaBuilder) -> {
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
    return tapeTestingRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<TestWheel> findTestWheelList(String wheelSerial, Integer limit) {
    return testWheelRepository.findByWheelSerial(wheelSerial, "finalchecktest", limit);
  }

  public TapeTestingRecord add(TapeTestingRecord tapeTestingRecord) {
    tapeTestingRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    Integer maxIs = tapeTestingRecordRepository.findByMaxTs(tapeTestingRecord.getInspectorId(),
      tapeTestingRecord.getShiftNo(), tapeTestingRecord.getTestDate());
    tapeTestingRecord.setTs(maxIs + 1);
    tapeTestingRecord.setCreateTime(new Date());
    return tapeTestingRecordRepository.save(tapeTestingRecord);
  }
}
