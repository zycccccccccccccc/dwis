package tech.hciot.dwis.business.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import tech.hciot.dwis.business.domain.MachineRecordRepository;
import tech.hciot.dwis.business.domain.TMachineRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.*;

import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_IS_FINISHED;

@Service
@Slf4j
public class TMachineRecordService {

  @Autowired
  private TMachineRecordRepository tMachineRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<TMachineRecord> find(String machineNo,
                                   String operator,
                                   Boolean isInspector,
                                   String wheelSerial,
                                   Integer tS1,
                                   Integer tS2,
                                   Integer isRollingDiaCheck,
                                   Integer isCheck,
                                   Integer isRework,
                                   Integer currentPage,
                                   Integer pageSize) {
    Specification<TMachineRecord> specification = (root, query, criteriaBuilder) -> {
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
      if (tS1 != null) {
        list.add(criteriaBuilder.equal(root.get("tS1"), tS1));
      }
      if (tS2 != null) {
        list.add(criteriaBuilder.equal(root.get("tS2"), tS2));
      }
      if (isRollingDiaCheck != null) {
        list.add(criteriaBuilder.equal(root.get("isRollingDiaCheck"), isRollingDiaCheck));
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
    return tMachineRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<Integer> machineNoList() {
    return tMachineRecordRepository.machineNoList();
  }

  public Integer machineCount(Integer machineNo, String operator) {
    Date last12Hour = DateUtils.addHours(new Date(), -12);
    return tMachineRecordRepository.totalMachineCount(machineNo, operator, last12Hour);
  }

  public TMachineRecord add(TMachineRecord tMachineRecord) {
    tMachineRecord.setOpeDT(operatingTimeCtrService.getMachineOperatingTime());
    tMachineRecord.setCreateTime(new Date());
    tMachineRecordRepository.save(tMachineRecord);
    updateMachineRecord(tMachineRecord);
    return tMachineRecord;
  }

  // 级联更新机加工表
  private void updateMachineRecord(TMachineRecord tMachineRecord) {
    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(tMachineRecord.getWheelSerial())
      .orElseThrow(() -> PlatformException.badRequestException("机加工状态记录不存在"));
    if (tMachineRecord.getTS1().doubleValue() > 100 && tMachineRecord.getTS1().doubleValue() < 1000) {
      machineRecord.setTS1(tMachineRecord.getTS1());
    }
    machineRecord.setTS2(tMachineRecord.getTS2());
    if (machineRecord.getTId() == null || machineRecord.getTId() == 0) {
      machineRecord.setTId(tMachineRecord.getId());
    }
    machineRecord.setTIdLast(tMachineRecord.getId());
    machineRecord.setTCounts(machineRecord.getTCounts() == null ? 1 : machineRecord.getTCounts() + 1);
    if (StringUtils.isNotBlank(tMachineRecord.getReworkCode())) {
      machineRecord.setTIdRe(tMachineRecord.getId());
    }
    machineRecordRepository.save(machineRecord);
  }

  //根据机床号、轮号获取踏面加工记录
  public List<TMachineRecord> findByWheelSerial(String machineNo, String wheelSerial) {
    Optional<WheelRecord> opt = wheelRecordRepository.findByWheelSerial(wheelSerial);
    if (opt.isPresent()) {
      if (opt.get().getFinished() == 0) {
        Specification<TMachineRecord> specification = (root, query, criteriaBuilder) -> {
          List<Predicate> list = new ArrayList<>();
          if (machineNo != null) {
            list.add(criteriaBuilder.equal(root.get("machineNo"), machineNo));
          }
          list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
          query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
          query.orderBy(criteriaBuilder.desc(root.get("createTime")));
          return query.getRestriction();
        };
        return tMachineRecordRepository.findAll(specification);
      } else {
        throw WHEEL_IS_FINISHED.getPlatformException();
      }
    } else {
      throw PlatformException.badRequestException("轮号不存在");
    }
  }

  @Transactional
  public void modifyRecord(Integer id, TMachineRecord record) {
    tMachineRecordRepository.findById(id).ifPresent(old -> {
      old.setTS1(record.getTS1());
      old.setTS2(record.getTS2());
      old.setRimWidth(record.getRimWidth());
      old.setHubLength(record.getHubLength());
      old.setRollingCircleDia(record.getRollingCircleDia());
      old.setRimdev1(record.getRimdev1());
      old.setRimdev2(record.getRimdev2());
      old.setRimdev3(record.getRimdev3());
      tMachineRecordRepository.save(old);
    });
    //如若是最新的踏面加工记录，级联更新machine_record表的t_s1及t_s2
    machineRecordRepository.findByWheelSerial(record.getWheelSerial()).ifPresent(old -> {
      if(old.getTIdLast().equals(id)) {
        old.setTS1(record.getTS1());
        old.setTS2(record.getTS2());
        machineRecordRepository.save(old);
      }
    });
  }
}
