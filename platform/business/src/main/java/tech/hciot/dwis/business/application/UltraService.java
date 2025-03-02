package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.MachineRecordRepository;
import tech.hciot.dwis.business.domain.ReleaseRecordRepository;
import tech.hciot.dwis.business.domain.TapeRepository;
import tech.hciot.dwis.business.domain.UltraRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.Heat;
import tech.hciot.dwis.business.domain.model.MachineRecord;
import tech.hciot.dwis.business.domain.model.UltraRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.UltraResponse;

@Service
@Slf4j
public class UltraService {

  private static final int ENABLED = 1;
  private static final int DISABLED = 0;
  private static final String HOLDFOR8C = "C230";

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private UltraRecordRepository ultraRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private ReleaseRecordRepository releaseRecordRepository;

  @Autowired
  private TapeRepository tapeRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<UltraRecord> find(String wheelSerial,
                                BigDecimal tapeSize,
                                String reworkCode,
                                String scrapCode,
                                String inspectorId,
                                Integer currentPage,
                                Integer pageSize) {
    Specification<UltraRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (tapeSize != null) {
        list.add(criteriaBuilder.equal(root.get("tapeSize"), tapeSize));
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
    return ultraRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  /**
   * 获取车轮信息
   * @param wheelSerial
   * @return
   */
  public UltraResponse findWheel(String wheelSerial) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("finalCount", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(wheelSerial, parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    UltraResponse ultraRecord = UltraResponse.builder().build();
    BeanUtil.copyProperties(wheelRecord, ultraRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

    ultraRecord.setHfs("<0.18μSv/h");
    updateTS1(ultraRecord);
    updateRelease(ultraRecord);
    updateTapeSizeList(ultraRecord);
    return ultraRecord;
  }

  // Machine_Record机加工状态表的T_S1（踏面S1参数）
  private void updateTS1(UltraResponse ultraRecord) {
    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(ultraRecord.getWheelSerial())
      .orElse(MachineRecord.builder().build());
    ultraRecord.setTS1(machineRecord.getTS1());
  }

  // 根据车轮序列号查询专员检查放行记录表，按Ope_D_T操作时间降序排序取第一条数据，
  // 如果能查到数据，则弹出提示框：“前返工代码为Rework_Code，已被Inspector_ID查验，准许放行！”
  private void updateRelease(UltraResponse ultraRecord) {
    if (releaseRecordRepository.findNewestWheelSerial(ultraRecord.getWheelSerial()).isPresent()) {
      ultraRecord.setRelease(true);
    }
  }

  // 带尺尺寸下拉框
  private void updateTapeSizeList(UltraResponse ultraRecord) {
    List<BigDecimal> tapeSizeList = ultraRecord.getTapeSizeList();
    tapeRepository.findByDesignAndEnabled(ultraRecord.getDesign(), ENABLED).forEach(tape -> {
      tapeSizeList.add(tape.getTapesize());
    });
    ultraRecord.setTapeSizeList(tapeSizeList);
  }

  /**
   * 添加超探记录
   * @param ultraRecord
   * @return
   */
  @Transactional
  public Boolean add(UltraRecord ultraRecord) {
    Integer num_8CS = 0;
    Boolean alart_8CS = false;
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("finalCount", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(ultraRecord.getWheelSerial(), parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    ultraRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxUltraTimes(ultraRecord);
    ultraRecord.setSpecialMt(wheelRecord.getSpecialMt());
    ultraRecord.setRemanence("<=0.7mT");
    ultraRecord.setCreateTime(new Date());
    ultraRecordRepository.save(ultraRecord);
    updateWheelRecord(ultraRecord, wheelRecord);
    // 计算该炉车轮8CS或8C的个数
    if (ultraRecord.getReworkCode().equals("8C") || ultraRecord.getScrapCode().equals("8CS")) {
      num_8CS = ultraRecordRepository.find8CSNumByWheelSerial(ultraRecord.getWheelSerial());
      if (num_8CS >= 4) {
        alart_8CS = true;
        // 将该炉车轮中已成品&&未发运的test_code赋值C230
        List<WheelRecord> wheelRecordListList = new ArrayList<>();
        wheelRecordRepository.findFinishedAndNotShippedByWheelSerial(ultraRecord.getWheelSerial()).stream().forEach(wr -> {
          wr.setTestCode(HOLDFOR8C);
          wheelRecordListList.add(wr);
        });
        wheelRecordRepository.saveAll(wheelRecordListList);
      }
    }
    return alart_8CS;
  }

  // 超探次数取值为 超探表相同车轮序列号的ts（超探次数）最大值+1，存入超探表的ts字段
  private void updateMaxUltraTimes(UltraRecord ultraRecord) {
    Integer maxFinalCheckTimes = ultraRecordRepository
      .findMaxUltraTimes(ultraRecord.getWheelSerial()).orElse(new BigDecimal(0)).intValue() + 1;
    ultraRecord.setTs(maxFinalCheckTimes);
  }

  // 级联更新Wheel_Record表
  private void updateWheelRecord(UltraRecord ultraRecord, WheelRecord wheelRecord) {
    wheelRecord.setUltraId(ultraRecord.getId());
    wheelRecord.setTapeSize(ultraRecord.getTapeSize());
    wheelRecord.setUltra(ultraRecord.getTs());
    wheelRecord.setLastUltra(ultraRecord.getOpeDT());
    wheelRecord.setScrapCode(ultraRecord.getScrapCode());
    wheelRecord.setReworkCode(ultraRecord.getReworkCode());
    wheelRecord.setSpecialUltra(0);
    wheelRecord.setHfs("<0.18μSv/h");
    wheelRecordRepository.save(wheelRecord);
  }
}
