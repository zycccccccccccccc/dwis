package tech.hciot.dwis.business.application;

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
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.application.controlledRecord.impl.BalanceExporter;
import tech.hciot.dwis.business.domain.CalibraWheelRepository;
import tech.hciot.dwis.business.domain.MachineRecordRepository;
import tech.hciot.dwis.business.domain.QMachineRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.CalibraWheel;
import tech.hciot.dwis.business.domain.model.MachineRecord;
import tech.hciot.dwis.business.domain.model.QMachineRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
@Slf4j
public class QMachineRecordService {

  @Autowired
  private QMachineRecordRepository qMachineRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private CalibraWheelRepository calibraWheelRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<QMachineRecord> find(String machineNo,
                                   String operator,
                                   Boolean isInspector,
                                   String wheelSerial,
                                   String holdCode,
                                   Integer currentPage,
                                   Integer pageSize) {
    Specification<QMachineRecord> specification = (root, query, criteriaBuilder) -> {
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
      if (holdCode != null) {
        list.add(criteriaBuilder.equal(root.get("holdCode"), holdCode));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return qMachineRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<Integer> machineNoList() {
    return qMachineRecordRepository.machineNoList();
  }

  public Integer machineCount(Integer machineNo, String operator) {
    Date last12Hour = DateUtils.addHours(new Date(), -12);
    return qMachineRecordRepository.totalMachineCount(machineNo, operator, last12Hour);
  }

  public QMachineRecord add(QMachineRecord qMachineRecord) {
    qMachineRecord.setOpeDT(operatingTimeCtrService.getMachineOperatingTime());
    qMachineRecord.setCreateTime(new Date());
    qMachineRecordRepository.save(qMachineRecord);
    updateMachineRecord(qMachineRecord);
    updateCalibraWheel(qMachineRecord);
    updateWheelRecord(qMachineRecord);
    return qMachineRecord;
  }

  // 级联更新机加工表
  private void updateMachineRecord(QMachineRecord qMachineRecord) {
    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(qMachineRecord.getWheelSerial())
      .orElseThrow(() -> PlatformException.badRequestException("机加工状态记录不存在"));
    if (machineRecord.getQId() == null || machineRecord.getQId() == 0) {
      machineRecord.setQId(qMachineRecord.getId());
    }
    machineRecord.setQCounts(machineRecord.getQCounts() == null ? 1 : machineRecord.getQCounts() + 1);
    machineRecordRepository.save(machineRecord);
  }

  // 级联更新标准轮表
  private void updateCalibraWheel(QMachineRecord qMachineRecord) {
    CalibraWheel calibraWheel = calibraWheelRepository.findById(qMachineRecord.getCaliWheelId())
      .orElseThrow(() -> PlatformException.badRequestException("标准轮记录不存在"));
    calibraWheel.setMachineCount(calibraWheel.getMachineCount() == null ? 1 : calibraWheel.getMachineCount() + 1);
    calibraWheelRepository.save(calibraWheel);
  }

  // 级联更新车轮表
  private void updateWheelRecord(QMachineRecord qMachineRecord) {
    WheelRecord wheelRecord = wheelRecordRepository.findByWheelSerial(qMachineRecord.getWheelSerial())
      .orElseThrow(() -> PlatformException.badRequestException("车轮记录不存在"));
    if ("Q2".equals(qMachineRecord.getHoldCode())) {
      wheelRecord.setHoldCode("Q3");
      wheelRecordRepository.save(wheelRecord);
    }
  }
}
