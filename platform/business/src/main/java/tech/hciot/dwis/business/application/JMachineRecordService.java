package tech.hciot.dwis.business.application;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.CalibraWheelRepository;
import tech.hciot.dwis.business.domain.JMachineRecordRepository;
import tech.hciot.dwis.business.domain.MachineRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.CalibraWheel;
import tech.hciot.dwis.business.domain.model.JMachineRecord;
import tech.hciot.dwis.business.domain.model.MachineRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_IS_FINISHED;

@Service
@Slf4j
public class JMachineRecordService {

  @Autowired
  private JMachineRecordRepository jMachineRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private CalibraWheelRepository calibraWheelRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<JMachineRecord> find(String machineNo,
                                   String operator,
                                   Boolean isInspector,
                                   String wheelSerial,
                                   Integer jS1,
                                   Integer jS2,
                                   Integer isCheck,
                                   Integer isRework,
                                   Integer currentPage,
                                   Integer pageSize) {
    Specification<JMachineRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (machineNo != null) {
        list.add(criteriaBuilder.equal(root.get("machineNo"), machineNo));
      }
      if (operator != null) {
        if(isInspector){
          list.add(criteriaBuilder.equal(root.get("inspectorId"), operator));
        } else {
          list.add(criteriaBuilder.equal(root.get("operator"), operator));
        }
      }
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (jS1 != null) {
        list.add(criteriaBuilder.equal(root.get("jS1"), jS1));
      }
      if (jS2 != null) {
        list.add(criteriaBuilder.equal(root.get("jS2"), jS2));
      }
      if (isCheck != null) {
        list.add(criteriaBuilder.equal(root.get("isCheck"), isCheck));
      }
      if (isRework != null) {
        if (isRework == 1) {
          list.add(criteriaBuilder.notEqual(root.get("reworkCode"), ""));
        } else {
          list.add(criteriaBuilder.equal(root.get("reworkCode"), ""));
        }
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    Page<JMachineRecord> page = jMachineRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
    page.getContent().forEach(jMachineRecord -> {
      jMachineRecord.setRework(StringUtils.isBlank(jMachineRecord.getReworkCode()) ? 0 : 1);
    });
    return page;
  }

  public List<Integer> machineNoList() {
    return jMachineRecordRepository.machineNoList();
  }

  public Integer machineCount(Integer machineNo, String operator) {
    Date last12Hour = DateUtils.addHours(new Date(), -12);
    return jMachineRecordRepository.totalMachineCount(machineNo, operator, last12Hour);
  }

  public JMachineRecord add(JMachineRecord jMachineRecord) {
    jMachineRecord.setOpeDT(operatingTimeCtrService.getMachineOperatingTime());
    jMachineRecord.setCreateTime(new Date());
    jMachineRecordRepository.save(jMachineRecord);
    updateMachineRecord(jMachineRecord);
    updateCalibraWheel(jMachineRecord);
    return jMachineRecord;
  }

  // 级联更新机加工表
  private void updateMachineRecord(JMachineRecord jMachineRecord) {
    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(jMachineRecord.getWheelSerial())
      .orElseGet(() -> {
        WheelRecord wheelRecord = wheelRecordRepository.findByWheelSerial(jMachineRecord.getWheelSerial())
          .orElseThrow(() -> PlatformException.badRequestException("车轮号不存在"));
        return MachineRecord.builder()
          .wheelSerial(wheelRecord.getWheelSerial())
          .design(wheelRecord.getDesign())
          .build();
      });
    machineRecord.setJS1(jMachineRecord.getJS1());
    machineRecord.setJS2(jMachineRecord.getJS2());
    if (machineRecord.getJId() == null || machineRecord.getJId() == 0) {
      machineRecord.setJId(jMachineRecord.getId());
    }
    machineRecord.setJIdLast(jMachineRecord.getId());
    machineRecord.setJCounts(machineRecord.getJCounts() == null ? 1 : machineRecord.getJCounts() + 1);
    if (StringUtils.isNotBlank(jMachineRecord.getReworkCode())) {
      machineRecord.setJIdRe(jMachineRecord.getId());
    }
    machineRecordRepository.save(machineRecord);
  }

  // 级联更新标准轮表
  private void updateCalibraWheel(JMachineRecord jMachineRecord) {
    CalibraWheel calibraWheel = calibraWheelRepository.findById(jMachineRecord.getCaliWheelId())
      .orElseThrow(() -> PlatformException.badRequestException("标准轮记录不存在"));
    calibraWheel.setMachineCount(calibraWheel.getMachineCount() == null ? 1 : calibraWheel.getMachineCount() + 1);
    calibraWheelRepository.save(calibraWheel);
  }

  //根据机床号、轮号获取基面加工记录
  public List<JMachineRecord> findByWheelSerial(String machineNo, String wheelSerial) {
    Optional<WheelRecord> opt = wheelRecordRepository.findByWheelSerial(wheelSerial);
    if (opt.isPresent()) {
      if (opt.get().getFinished() == 0) {
        Specification<JMachineRecord> specification = (root, query, criteriaBuilder) -> {
          List<Predicate> list = new ArrayList<>();
          if (machineNo != null) {
            list.add(criteriaBuilder.equal(root.get("machineNo"), machineNo));
          }
          list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
          query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
          query.orderBy(criteriaBuilder.desc(root.get("createTime")));
          return query.getRestriction();
        };
        return jMachineRecordRepository.findAll(specification);
      } else {
        throw WHEEL_IS_FINISHED.getPlatformException();
      }
    } else {
      throw PlatformException.badRequestException("轮号不存在");
    }
  }

  @Transactional
  public void modifyRecord(Integer id, JMachineRecord record) {
    jMachineRecordRepository.findById(id).ifPresent(old -> {
      old.setJS1(record.getJS1());
      old.setJS2(record.getJS2());
      old.setF(record.getF());
      old.setD2Dia(record.getD2Dia());
      old.setD2Cir(record.getD2Cir());
      jMachineRecordRepository.save(old);
    });
    //如若是最新的基面加工记录，级联更新machine_record表的j_s1及j_s2
    machineRecordRepository.findByWheelSerial(record.getWheelSerial()).ifPresent(old -> {
      if(old.getJIdLast().equals(id)) {
        old.setJS1(record.getJS1());
        old.setJS2(record.getJS2());
        machineRecordRepository.save(old);
      }
    });
  }
}
