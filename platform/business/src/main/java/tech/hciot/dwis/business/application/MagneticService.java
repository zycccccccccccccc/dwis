package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.MagneticRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.MagneticRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.MagneticResponse;

@Service
@Slf4j
public class MagneticService {

  @Autowired
  private MagneticRecordRepository magneticRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<MagneticRecord> find(String wheelSerial,
                                String reworkCode,
                                String scrapCode,
                                String inspectorId,
                                Integer currentPage,
                                Integer pageSize) {
    Specification<MagneticRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (reworkCode != null) {
        list.add(criteriaBuilder.equal(root.get("reworkCode"), reworkCode));
      }
      if (scrapCode != null) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return magneticRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  /**
   * 获取车轮信息
   * @param wheelSerial
   * @return
   */
  public MagneticResponse findWheel(String wheelSerial) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("ultra", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(wheelSerial, parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    MagneticResponse magneticRecord = MagneticResponse.builder().build();
    BeanUtil.copyProperties(wheelRecord, magneticRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

    return magneticRecord;
  }

  /**
   * 添加磁探记录
   * @param magneticRecord
   * @return
   */
  public Integer add(MagneticRecord magneticRecord) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("ultra", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(magneticRecord.getWheelSerial(), parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    magneticRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxMagneticTimes(magneticRecord);
    magneticRecord.setCreateTime(new Date());
    magneticRecordRepository.save(magneticRecord);
    updateWheelRecord(magneticRecord, wheelRecord);
    return magneticRecord.getId();
  }

  // 磁探次数取值为磁探表相同车轮序列号的ts（磁探次数）最大值+1，存入磁探表的ts字段
  private void updateMaxMagneticTimes(MagneticRecord magneticRecord) {
    Integer maxFinalCheckTimes = magneticRecordRepository
      .findMaxMagneticTimes(magneticRecord.getWheelSerial()).orElse(new BigDecimal(0)).intValue() + 1;
    magneticRecord.setTs(maxFinalCheckTimes);
  }

  // 级联更新Wheel_Record表
  private void updateWheelRecord(MagneticRecord magneticRecord, WheelRecord wheelRecord) {
    wheelRecord.setMtId(magneticRecord.getId());
    wheelRecord.setMt(magneticRecord.getTs());
    wheelRecord.setLastMt(magneticRecord.getOpeDT());
    wheelRecord.setScrapCode(magneticRecord.getScrapCode());
    wheelRecord.setReworkCode(magneticRecord.getReworkCode());
    wheelRecordRepository.save(wheelRecord);
  }
}
