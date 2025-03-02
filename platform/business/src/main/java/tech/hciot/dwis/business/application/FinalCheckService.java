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
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.FinalCheckRecord;
import tech.hciot.dwis.business.domain.model.MachineRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.FinalCheckResponse;

@Service
@Slf4j
public class FinalCheckService {

  private static final int ENABLED = 1;
  private static final int DISABLED = 0;

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private FinalCheckRecordRepository finalCheckRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private ReleaseRecordRepository releaseRecordRepository;

  @Autowired
  private BoreSizeRepository boreSizeRepository;

  @Autowired
  private WheelWidthSizeRepository wheelWidthSizeRepository;

  @Autowired
  private KMachineRecordRepository kMachineRecordRepository;

  @Autowired
  private DesignRepository designRepository;

  @Autowired
  private HardnessControlService hardnessControlService;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<FinalCheckRecord> find(String wheelSerial,
                                     String scrapCode,
                                     String reworkCode,
                                     String heatCode,
                                     String inspectorId,
                                     Integer currentPage,
                                     Integer pageSize) {
    Specification<FinalCheckRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (scrapCode != null) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (reworkCode != null) {
        list.add(criteriaBuilder.equal(root.get("reworkCode"), reworkCode));
      }
      if (heatCode != null) {
        list.add(criteriaBuilder.equal(root.get("heatCode"), heatCode));
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
    return finalCheckRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  /**
   * 获取车轮信息
   * @param wheelSerial
   * @return
   */
  public FinalCheckResponse findWheel(String wheelSerial) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("pre", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(wheelSerial, parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    FinalCheckResponse finalCheck = FinalCheckResponse.builder().build();
    BeanUtil.copyProperties(wheelRecord, finalCheck, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(wheelSerial)
      .orElse(MachineRecord.builder().build());
    finalCheck.setTS1(machineRecord.getTS1());
    finalCheck.setJS2(machineRecord.getJS2());
    finalCheck.setTS2(machineRecord.getTS2());
    finalCheck.setKS2(machineRecord.getKS2());

    if (releaseRecordRepository.findNewestWheelSerial(wheelSerial).isPresent()) {
      finalCheck.setRelease(true);
    };

    List<Integer> boreSizeList = finalCheck.getBoreSizeList();
    boreSizeRepository.findByDesignAndEnabled(wheelRecord.getDesign(), ENABLED).forEach(boreSize -> {
      boreSizeList.add(boreSize.getSizeParameter());
    });

    List<BigDecimal> wheelWidthSizeList = finalCheck.getWheelWidthSizeList();
    wheelWidthSizeRepository.findByDesignAndEnabled(wheelRecord.getDesign(), ENABLED).forEach(wheelWidthSize -> {
      wheelWidthSizeList.add(wheelWidthSize.getSizeParameter());
    });

    // 判断是否国内轮型&&有无镗孔S2=40的记录
    Integer internal = designRepository.findByDesign(wheelRecord.getDesign()).map(design -> design.getInternal()).orElse(null);
    if (internal != null && internal == 1) {
      Integer kCounts = kMachineRecordRepository.findKCountsByWheelSerial(wheelSerial, 40);
      if (kCounts != null && kCounts >= 1) {
        finalCheck.setIsKMachined(true);
      } else {
        finalCheck.setIsKMachined(false);
      }
    }
    return finalCheck;
  }

  /**
   * 添加终检记录
   * @param finalCheckRecord
   * @return
   */
  public Integer add(FinalCheckRecord finalCheckRecord) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("pre", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(finalCheckRecord.getWheelSerial(), parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    finalCheckRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxFinalCheckTimes(finalCheckRecord);
    finalCheckRecord.setFlangeTreadProfile("H3".equals(finalCheckRecord.getReworkCode()) ? 0 : 1);
    finalCheckRecord.setCreateTime(new Date());
    finalCheckRecordRepository.save(finalCheckRecord);
    updateWheelRecord(finalCheckRecord, wheelRecord);
    return finalCheckRecord.getId();
  }

  private void updateMaxFinalCheckTimes(FinalCheckRecord finalCheckRecord) {
    Integer maxFinalCheckTimes = finalCheckRecordRepository.findMaxFinalCheckTimes(finalCheckRecord.getWheelSerial()) + 1;
    finalCheckRecord.setTs(maxFinalCheckTimes);
  }

  private void updateWheelRecord(FinalCheckRecord finalCheckRecord, WheelRecord wheelRecord) {
    wheelRecord.setFinalId(finalCheckRecord.getId());
    wheelRecord.setBoreSize(finalCheckRecord.getBoreSize());
    wheelRecord.setWheelW(finalCheckRecord.getWheelW());
    wheelRecord.setBrinnelReading(finalCheckRecord.getBrinnelReading());
    wheelRecord.setFinalCount(finalCheckRecord.getTs());
    wheelRecord.setLastFinal(finalCheckRecord.getOpeDT());
    wheelRecord.setScrapCode(finalCheckRecord.getScrapCode());
    wheelRecord.setReworkCode(finalCheckRecord.getReworkCode());
    wheelRecord.setGrindDepth(finalCheckRecord.getGrindDepth());
    wheelRecord.setNGrind(finalCheckRecord.getNgrind());
    wheelRecord.setHeatCode(finalCheckRecord.getHeatCode());
    wheelRecord.setCihenCode(finalCheckRecord.getCihenCode());
    wheelRecord.setBrinReq(finalCheckRecord.getBrinReq());
    wheelRecordRepository.save(wheelRecord);
  }

  /**
   * 是否硬度检测，如果在硬度范围内，则不需要检查，否则需要检查
   * @param design
   * @param brinnelReading
   * @return
   */
  public boolean isBrinReq(String design, Integer brinnelReading) {
    return !hardnessControlService.isHardnessInScope(design, brinnelReading);
  }
}
