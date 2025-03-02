package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.math.BigDecimal;
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
import tech.hciot.dwis.business.domain.DesignRepository;
import tech.hciot.dwis.business.domain.RawWheelPrintRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.Design;
import tech.hciot.dwis.business.domain.model.RawWheelPrintRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.RawWheelPrintResponse;

@Service
@Slf4j
public class RawWheelPrintRecordService {

  @Autowired
  private RawWheelPrintRecordRepository rawWheelPrintRecordRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private DesignRepository designRepository;

  public Page<RawWheelPrintRecord> find(String wheelSerial,
      String design,
      Integer boreSize,
      String scrapCode,
      String operator,
      Integer currentPage,
      Integer pageSize) {
    Specification<RawWheelPrintRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (design != null) {
        list.add(criteriaBuilder.equal(root.get("design"), design));
      }
      if (boreSize != null) {
        list.add(criteriaBuilder.equal(root.get("boreSize"), boreSize));
      }
      if (scrapCode != null) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (operator != null) {
        list.add(criteriaBuilder.equal(root.get("operator"), operator));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return rawWheelPrintRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  /**
   * 获取车轮信息
   *
   * @param wheelSerial
   * @return
   */
  public RawWheelPrintResponse findWheel(String wheelSerial) {
    WheelRecord wheelRecord = wheelRecordService.findRawWheelPrintWheel(wheelSerial)
        .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    RawWheelPrintResponse rawWheelPrintResponse = RawWheelPrintResponse.builder().build();
    BeanUtil
        .copyProperties(wheelRecord, rawWheelPrintResponse, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

    Design design = designRepository.findByDesign(wheelRecord.getDesign())
        .orElseThrow(() -> PlatformException.badRequestException("轮型不存在"));
    rawWheelPrintResponse.setSteelClass(design.getSteelClass());
    return rawWheelPrintResponse;
  }

  public RawWheelPrintResponse add(RawWheelPrintRecord rawWheelPrintRecord) {
    WheelRecord wheelRecord = wheelRecordRepository.findByWheelSerial(rawWheelPrintRecord.getWheelSerial())
        .orElseThrow(() -> PlatformException.badRequestException("车轮记录不存在"));
    rawWheelPrintRecord.setCreateTime(new Date());
    rawWheelPrintRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxTimes(rawWheelPrintRecord);
    if (StringUtils.isBlank(rawWheelPrintRecord.getScrapCode())) {
      rawWheelPrintRecord.setFinished(1);
    } else {
      rawWheelPrintRecord.setFinished(0);
    }
    rawWheelPrintRecordRepository.save(rawWheelPrintRecord);
    updateWheelRecord(rawWheelPrintRecord, wheelRecord);
    RawWheelPrintResponse rawWheelPrintResponse = RawWheelPrintResponse.builder().build();
    BeanUtil.copyProperties(wheelRecord, rawWheelPrintResponse,
        CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    BeanUtil.copyProperties(rawWheelPrintRecord, rawWheelPrintResponse,
        CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    Design design = designRepository.findByDesign(wheelRecord.getDesign())
        .orElseThrow(() -> PlatformException.badRequestException("轮型不存在"));
    rawWheelPrintResponse.setSteelClass(design.getSteelClass());
    return rawWheelPrintResponse;
  }

  private void updateMaxTimes(RawWheelPrintRecord rawWheelPrintRecord) {
    Integer maxPreCheckTimes = rawWheelPrintRecordRepository
        .findMaxTimes(rawWheelPrintRecord.getWheelSerial()).orElse(new BigDecimal(0)).intValue() + 1;
    rawWheelPrintRecord.setTs(maxPreCheckTimes);
  }

  private void updateWheelRecord(RawWheelPrintRecord rawWheelPrintRecord, WheelRecord wheelRecord) {
    wheelRecord.setScrapCode(rawWheelPrintRecord.getScrapCode());
    wheelRecord.setBarcode(rawWheelPrintRecord.getTs());
    wheelRecord.setLastBarcode(rawWheelPrintRecord.getOpeDT());
    wheelRecord.setBoreSize(rawWheelPrintRecord.getBoreSize());
    wheelRecord.setFinished(rawWheelPrintRecord.getFinished());
    if (rawWheelPrintRecord.getFinished() == 1) {
      wheelRecord.setFinishPrint(1);
    }
    wheelRecordRepository.save(wheelRecord);
  }
}
