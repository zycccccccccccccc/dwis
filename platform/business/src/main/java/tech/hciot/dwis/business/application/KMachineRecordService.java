package tech.hciot.dwis.business.application;

import java.util.*;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.KMachineRecordRepository;
import tech.hciot.dwis.business.domain.MachineRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.*;

import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_IS_FINISHED;

@Service
@Slf4j
public class KMachineRecordService {

  @Autowired
  private KMachineRecordRepository kMachineRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<KMachineRecord> find(String machineNo,
                                   String operator,
                                   Boolean isInspector,
                                   String wheelSerial,
                                   String location,
                                   Integer kS1,
                                   Integer kS2,
                                   Integer isCheck,
                                   Integer isRework,
                                   Integer currentPage,
                                   Integer pageSize) {
    Specification<KMachineRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (machineNo != null) {
        list.add(criteriaBuilder.equal(root.get("machineNo"), machineNo));
      }
      if (operator != null) {
        if (isInspector) {
          list.add(criteriaBuilder.equal(root.get("inspectorId"), operator));
        } else {
          list.add(criteriaBuilder.equal(root.get("operator"), operator));
        }
      }
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (location != null) {
        list.add(criteriaBuilder.equal(root.get("location"), location));
      }
      if (kS1 != null) {
        list.add(criteriaBuilder.equal(root.get("kS1"), kS1));
      }
      if (kS2 != null) {
        list.add(criteriaBuilder.equal(root.get("kS2"), kS2));
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
    return kMachineRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<Integer> machineNoList() {
    return kMachineRecordRepository.machineNoList();
  }

  public Map<String, Integer> machineCount(Integer machineNo, String operator) {
    Date last12Hour = DateUtils.addHours(new Date(), -12);
    List<Object[]> array = kMachineRecordRepository.totalMachineCount(machineNo, operator, last12Hour);
    Map<String, Integer> countMap = new HashMap<>();
    for (Object[] o : array) {
      countMap.put((String) o[0], (Integer) o[1]);
    }
    return countMap;
  }

  public KMachineRecord add(KMachineRecord kMachineRecord) {
    kMachineRecord.setOpeDT(operatingTimeCtrService.getMachineOperatingTime());
    kMachineRecord.setCreateTime(new Date());
    kMachineRecordRepository.save(kMachineRecord);
    updateMachineRecord(kMachineRecord);
    return kMachineRecord;
  }

  // 级联更新机加工表
  private void updateMachineRecord(KMachineRecord kMachineRecord) {
    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(kMachineRecord.getWheelSerial())
      .orElseThrow(() -> PlatformException.badRequestException("机加工状态记录不存在"));
    machineRecord.setKS1(kMachineRecord.getKS1());
    machineRecord.setKS2(kMachineRecord.getKS2());
    if (machineRecord.getKId() == null || machineRecord.getKId() == 0) {
      machineRecord.setKId(kMachineRecord.getId());
    }
    machineRecord.setKIdLast(kMachineRecord.getId());
    machineRecord.setKCounts(machineRecord.getKCounts() == null ? 1 : machineRecord.getKCounts() + 1);
    if (StringUtils.isNotBlank(kMachineRecord.getReworkCode())) {
      machineRecord.setKIdRe(kMachineRecord.getId());
    }
    machineRecordRepository.save(machineRecord);
  }

  //根据机床号、轮号获取镗孔加工记录
  public List<KMachineRecord> findByWheelSerial(String machineNo, String wheelSerial) {
    Optional<WheelRecord> opt = wheelRecordRepository.findByWheelSerial(wheelSerial);
    if (opt.isPresent()) {
      if (opt.get().getFinished() == 0) {
        Specification<KMachineRecord> specification = (root, query, criteriaBuilder) -> {
          List<Predicate> list = new ArrayList<>();
          if (machineNo != null) {
            list.add(criteriaBuilder.equal(root.get("machineNo"), machineNo));
          }
          list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
          query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
          query.orderBy(criteriaBuilder.desc(root.get("createTime")));
          return query.getRestriction();
        };
        return kMachineRecordRepository.findAll(specification);
      } else {
        throw WHEEL_IS_FINISHED.getPlatformException();
      }
    } else {
      throw PlatformException.badRequestException("轮号不存在");
    }
  }

  @Transactional
  public void modifyRecord(Integer id, KMachineRecord record) {
    kMachineRecordRepository.findById(id).ifPresent(old -> {
      old.setKS1(record.getKS1());
      old.setKS2(record.getKS2());
      old.setConcentricity(record.getConcentricity());
      old.setBoreDia(record.getBoreDia());
      kMachineRecordRepository.save(old);
    });
    //如若是最新的镗孔加工记录，级联更新machine_record表的k_s1及k_s2
    machineRecordRepository.findByWheelSerial(record.getWheelSerial()).ifPresent(old -> {
      if(old.getKIdLast().equals(id)) {
        old.setKS1(record.getKS1());
        old.setKS2(record.getKS2());
        machineRecordRepository.save(old);
      }
    });
  }
}
