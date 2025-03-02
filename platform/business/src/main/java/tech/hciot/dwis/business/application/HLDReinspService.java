package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.HLDReinspRecord;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.domain.model.PreCheckRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

import javax.persistence.criteria.Predicate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static tech.hciot.dwis.base.util.StandardTimeUtil.*;

@Service
@Slf4j
public class HLDReinspService {

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private HeatRepository heatRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private PitRecordsRepository pitRecordsRepository;

  @Autowired
  private HLDReinspRecordRepository hldReinspRecordRepository;

  /**
   * 获取车轮信息
   *
   * @param wheelSerial
   * @return
   */
  public HLDReinspRecord findWheel(String wheelSerial) {
    WheelRecord wheelRecord = wheelRecordService.findByWheelSerial(wheelSerial)
            .orElseThrow(() -> PlatformException.badRequestException("轮号输入有误！"));
    PourRecord pourRecord = pourRecordRepository.findByWheelSerial(wheelRecord.getWheelSerial());
    HLDReinspRecord response = HLDReinspRecord.builder().build();
    response.setWheelSerial(wheelRecord.getWheelSerial());
    response.setScrapCode(wheelRecord.getScrapCode());
    if (pourRecord.getInPitDateTime() != null) {
      response.setActOpenDateTime(pourRecord.getOpenTimeAct());
      response.setInPitDateTime(pourRecord.getInPitDateTime());
    }
    pitRecordsRepository.findByPitSeq(pourRecord.getPitSeq()).ifPresent(pitRecord -> {
      response.setOpenId(pitRecord.getOpenId());
      response.setOutPitDateTime(pitRecord.getOutPitDTAct());
      response.setCraneOutId(pitRecord.getCraneOutId());
    });
    heatRepository.findById(wheelRecord.getHeatId()).ifPresent(heat -> {
      if (heat.getHiHeatInDate() != null && heat.getHiHeatInTime() != null) {
        Date inHiDateTime = parseTime(dateStr(heat.getHiHeatInDate()) + " " + timeStr(heat.getHiHeatInTime()));
        response.setInHiDateTime(inHiDateTime);
        response.setInHiId(heat.getHiHeatInOperator());
      }
    });
    return response;
  }

  /**
   * 添加HLD废码检查记录信息
   * @param HLDReinspRecord
   * @return
   */
  public HLDReinspRecord add(HLDReinspRecord hldReinspRecord) {
    HLDReinspRecord oldData = findWheel(hldReinspRecord.getWheelSerial());
    HLDReinspRecord record = new HLDReinspRecord();
    BeanUtil.copyProperties(oldData, record, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    record.setScrapCode(hldReinspRecord.getScrapCode());
    record.setActOpenDateTimeFormer(oldData.getActOpenDateTime());
    record.setActOpenDateTime(hldReinspRecord.getActOpenDateTime());
    record.setInPitDateTimeFormer(oldData.getInPitDateTime());
    record.setInPitDateTime(hldReinspRecord.getInPitDateTime());
    record.setOutPitDateTimeFormer(oldData.getOutPitDateTime());
    record.setOutPitDateTime(hldReinspRecord.getOutPitDateTime());
    record.setInHiDateTimeFormer(oldData.getInHiDateTime());
    record.setInHiDateTime(hldReinspRecord.getInHiDateTime());
    record.setCreateDateTime(new Date());
    record.setInspectorId(hldReinspRecord.getInspectorId());
    hldReinspRecordRepository.save(record); //保存操作修改前的记录，修改后最新的数据更新至各相关数据表中
    updateWheelRecord(hldReinspRecord);
    updatePitRecord(hldReinspRecord);
    updateHeat(hldReinspRecord);
    HLDReinspRecord current = findWheel(hldReinspRecord.getWheelSerial()); //返回修改后的数据
    return current;
  }

  private void updateWheelRecord(HLDReinspRecord hldReinspRecord) {
    WheelRecord wheelRecord = wheelRecordService.findByWheelSerial(hldReinspRecord.getWheelSerial())
            .orElseThrow(() -> PlatformException.badRequestException("轮号输入有误！"));
    wheelRecord.setScrapCode(hldReinspRecord.getScrapCode());
    wheelRecordRepository.save(wheelRecord);
  }

  private void updatePitRecord(HLDReinspRecord hldReinspRecord) {
    PourRecord pourRecord = pourRecordRepository.findByWheelSerial(hldReinspRecord.getWheelSerial());
    pitRecordsRepository.findByPitSeq(pourRecord.getPitSeq()).ifPresent(pitRecord -> {
      if (hldReinspRecord.getActOpenDateTime() != null && hldReinspRecord.getInPitDateTime() != null) {
        pourRecord.setOpenTimeAct(hldReinspRecord.getActOpenDateTime());
        pourRecord.setInPitDateTime(hldReinspRecord.getInPitDateTime());
        pitRecord.setInPitDT(hldReinspRecord.getInPitDateTime());
        pourRecordRepository.save(pourRecord);
      }
      if (hldReinspRecord.getOutPitDateTime() != null) {
        pitRecord.setOutPitDTAct(hldReinspRecord.getOutPitDateTime());
        pitRecordsRepository.save(pitRecord);
      }
    });
  }

  private void updateHeat(HLDReinspRecord hldReinspRecord) {
    wheelRecordRepository.findByWheelSerial(hldReinspRecord.getWheelSerial()).ifPresent(wheelRecord -> {
      heatRepository.findById(wheelRecord.getHeatId()).ifPresent(heat -> {
        SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        if (hldReinspRecord.getInHiDateTime() != null) {
          heat.setHiHeatInDate(parseDate(fd.format(hldReinspRecord.getInHiDateTime())));
          heat.setHiHeatInTime(parseTime("1970-01-01 " + ft.format(hldReinspRecord.getInHiDateTime())));
          if (wheelRecord.getWheelSerial().equals(heat.getWheelSerial1())) {
            heat.setScrapCode1(wheelRecord.getScrapCode());
          }
          if (wheelRecord.getWheelSerial().equals(heat.getWheelSerial2())) {
            heat.setScrapCode2(wheelRecord.getScrapCode());
          }
          heatRepository.save(heat);
        }
      });
    });
  }

  public Page<HLDReinspRecord> findRecords(String wheelSerial, String inspectorId, Integer currentPage, Integer pageSize) {
    Specification<HLDReinspRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDateTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDateTime")));
      return query.getRestriction();
    };
    return hldReinspRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

}


