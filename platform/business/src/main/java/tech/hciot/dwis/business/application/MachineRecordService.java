package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.MachineParamsRepository;
import tech.hciot.dwis.business.domain.MachineRecordRepository;
import tech.hciot.dwis.business.domain.model.MachineParams;
import tech.hciot.dwis.business.domain.model.MachineRecord;

@Service
@Slf4j
public class MachineRecordService {

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private MachineParamsRepository machineParamsRepository;

  public Page<MachineRecord> find(Integer currentPage, Integer pageSize) {
    Specification<MachineRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    return machineRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Integer add(MachineRecord machineRecord) {
    machineRecord.setCreateTime(new Date());
    MachineRecord savedMachineRecord = machineRecordRepository.save(machineRecord);
    return savedMachineRecord.getId();
  }

  public void modify(Integer id, MachineRecord newMachineRecord) {
    MachineRecord machineRecord = machineRecordRepository.findById(id).get();
    BeanUtil.copyProperties(newMachineRecord, machineRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    machineRecordRepository.save(machineRecord);
  }

  public void delete(Integer id) {
    machineRecordRepository.findById(id).ifPresent(machineRecord -> {
      machineRecordRepository.deleteById(id);
    });
  }

  public MachineRecord findById(Integer id) {
    return machineRecordRepository.findById(id).get();
  }

  public List<MachineParams> findProcessParams(String process) {
    return machineParamsRepository.findByProcessAndEnabled(process, 1);
  }

  private void assertMachineRecordExists(Integer id) {
    // machineRecordRepository.findById(id)
    //     .orElseThrow(FLOOR_NOT_FOUND::getPlatformException);
  }
}
