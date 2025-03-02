package tech.hciot.dwis.business.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
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
import tech.hciot.dwis.business.domain.WMachineRecordRepository;
import tech.hciot.dwis.business.domain.model.MachineRecord;
import tech.hciot.dwis.business.domain.model.WMachineRecord;

@Service
@Slf4j
public class WMachineRecordService {

  @Autowired
  private WMachineRecordRepository wMachineRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<WMachineRecord> find(String machineNo,
                                   String operator,
                                   Boolean isInspector,
                                   String wheelSerial,
                                   Integer wS1,
                                   Integer wS2,
                                   Integer isRework,
                                   Integer currentPage,
                                   Integer pageSize) {
    Specification<WMachineRecord> specification = (root, query, criteriaBuilder) -> {
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
      if (wS1 != null) {
        list.add(criteriaBuilder.equal(root.get("wS1"), wS1));
      }
      if (wS2 != null) {
        list.add(criteriaBuilder.equal(root.get("wS2"), wS2));
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
    Page<WMachineRecord> page = wMachineRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
    page.getContent().forEach(wMachineRecord -> {
      wMachineRecord.setRework(StringUtils.isBlank(wMachineRecord.getReworkCode()) ? 0 : 1);
    });
    return page;
  }

  public List<Integer> machineNoList() {
    return wMachineRecordRepository.machineNoList();
  }

  public Integer machineCount(Integer machineNo, String operator) {
    Date last12Hour = DateUtils.addHours(new Date(), -12);
    return wMachineRecordRepository.totalMachineCount(machineNo, operator, last12Hour);
  }

  public WMachineRecord add(WMachineRecord wMachineRecord) {
    wMachineRecord.setOpeDT(operatingTimeCtrService.getMachineOperatingTime());
    wMachineRecord.setCreateTime(new Date());
    wMachineRecordRepository.save(wMachineRecord);
    updateMachineRecord(wMachineRecord);
    return wMachineRecord;
  }

  // 级联更新机加工表
  private void updateMachineRecord(WMachineRecord wMachineRecord) {
    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(wMachineRecord.getWheelSerial())
      .orElseThrow(() -> PlatformException.badRequestException("机加工状态记录不存在"));
    machineRecord.setWS1(wMachineRecord.getWS1());
    machineRecord.setWS2(wMachineRecord.getWS2());
    if (machineRecord.getWId() == null || machineRecord.getWId() == 0) {
      machineRecord.setWId(wMachineRecord.getId());
    }
    machineRecord.setWIdLast(wMachineRecord.getId());
    machineRecord.setWCounts(machineRecord.getWCounts() == null ? 1 : machineRecord.getWCounts() + 1);
    if (StringUtils.isNotBlank(wMachineRecord.getReworkCode())) {
      machineRecord.setWIdRe(wMachineRecord.getId());
    }
    machineRecordRepository.save(machineRecord);
  }
}
