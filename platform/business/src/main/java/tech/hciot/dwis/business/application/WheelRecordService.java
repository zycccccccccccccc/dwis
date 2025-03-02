package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.*;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_SERIAL_EXIST;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_SERIAL_NOT_EXIST;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.*;
import tech.hciot.dwis.business.interfaces.dto.MachineResponse;
import tech.hciot.dwis.business.interfaces.dto.TestWheelResponse;

@Service
@Slf4j
public class WheelRecordService {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private DesignRepository designRepository;

  @Autowired
  private HeatCodeRepository heatCodeRepository;

  @Autowired
  private ReworkCodeRepository reworkCodeRepository;

  @Autowired
  private BoreSizeRepository boreSizeRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private KMachineRecordRepository kMachineRecordRepository;

  @Autowired
  private MachiningCodeRepository machiningCodeRepository;

  @Autowired
  private HeatRepository heatRepository;

  public void addWheelRecord(WheelRecord wheelRecord) {
    assertWheelSerialNotExist(wheelRecord.getWheelSerial());
    wheelRecordRepository.save(wheelRecord);
  }

  private void assertWheelSerialNotExist(String wheelSerial) {
    if (wheelRecordRepository.countByWheelSerial(wheelSerial) > 0) {
      throw WHEEL_SERIAL_EXIST.getPlatformException();
    }
  }

  public Optional<WheelRecord> findByWheelSerial(String wheelSerial) {
    return wheelRecordRepository.findByWheelSerial(wheelSerial);
  }

  public void save(WheelRecord wheelRecord) {
    wheelRecordRepository.save(wheelRecord);
  }

  public List<String> findWheelList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findWheelSerialList(wheelSerial, limit);
  }

  public WheelRecord findWheel(String wheelSerial) {
    return findByWheelSerial(wheelSerial).map(wheelRecord -> {
      wheelRecord.setInternal(
              designRepository.findByDesign(wheelRecord.getDesign()).map(design -> design.getInternal()).orElse(null));
      wheelRecord.setBalanceCheck(
              designRepository.findByDesign(wheelRecord.getDesign()).map(design -> design.getBalanceCheck()).orElse(-1));
      wheelRecord.setHeatCodeType(
              heatCodeRepository.findByCodeAndEnabled(wheelRecord.getHeatCode(), 1).map(heatCode -> heatCode.getCodeType()).orElse(null));
      List<Integer> boreSizeList = wheelRecord.getBoreSizeList();
      boreSizeRepository.findByDesignAndEnabled(wheelRecord.getDesign(), 1).forEach(boreSize -> {
        boreSizeList.add(boreSize.getSizeParameter());
      });
      //判断车轮缓冷时长是否满足
      PourRecord pourRecord = pourRecordRepository.findByWheelSerial(wheelRecord.getWheelSerial());
      if (pourRecord != null) {
        if (pourRecord.getInPitDateTime() != null) { //有入缓冷桶时间
          Integer heatStatus = null;
          if (wheelRecord.getHeatId() != null) {
            heatStatus = heatRepository.findMaxHeat(wheelRecord.getWheelSerial()).getXh();
          }
          if (heatStatus == null || heatStatus == 0 || heatStatus == 1) { //高温炉未刷新前，以当前系统时间作为判断标准
            Date nowDate = new Date();
            Date OutPitDTCal1 = DateUtils.addHours(pourRecord.getInPitDateTime(), 12);
            if (nowDate.getTime() - OutPitDTCal1.getTime() < 0) { //进环时缓冷时长小于12h
              wheelRecord.setPitFinished(false);
            } else { //进环时缓冷时长不小于12h
              if (wheelRecord.getScrapCode().equals("HLD")) {
                wheelRecord.setPitFinished(false);
              } else {
                wheelRecord.setPitFinished(true);
              }
            }
          } else { //高温炉刷新后， 以进环时间作为判断标准
            if (wheelRecord.getFinished() != 1 && wheelRecord.getConfirmedScrap() != 1) {
              Heat heat = heatRepository.findMaxHeat(wheelRecord.getWheelSerial());
              Date inHiDT = parseTime(dateStr(heat.getHiHeatInDate()) + " " + timeStr(heat.getHiHeatInTime()));
              Date OutPitDTCal2 = DateUtils.addHours(pourRecord.getInPitDateTime(), 12);
              if (inHiDT.getTime() - OutPitDTCal2.getTime() < 0) {
                wheelRecord.setPitFinished(false);
              } else {
                if (wheelRecord.getScrapCode().equals("HLD")) {
                  wheelRecord.setPitFinished(false);
                } else {
                  wheelRecord.setPitFinished(true);
                }
              }
            }
          }
        } else { //没有入缓冷桶时间
          wheelRecord.setPitFinished(false);
        }
      }
      return wheelRecord;
    }).orElseThrow(WHEEL_SERIAL_NOT_EXIST::getPlatformException);
  }

  public WheelRecord findWheelWithoutThrowException(String wheelSerial) {
    return findByWheelSerial(wheelSerial).orElse(null);
  }

  public Integer findHeatTimes(String wheelSerial) {
    return wheelRecordRepository.findHeatTimes(wheelSerial);
  }

  public List<String> findRawWheelPrintWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findRawWheelPrintWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findReleaseWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findReleaseWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findNotFinishNotScrapWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findNotFinishNotScrapWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findScrapWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findScrapWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findNeedScrapWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findNeedScrapWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findCorrectScrapWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findCorrectScrapWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findCorrectFinishWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findCorrectFinishWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findCorrectStockWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findCorrectStockWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findCorrectReturnWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findCorrectReturnWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findXRayWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findXRayWheelSerialList(wheelSerial, limit);
  }

  public List<WheelRecord> findCihenWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findCihenWheelSerialList(wheelSerial, limit);
  }

  public List<TestWheelResponse> findUtTestWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findUtTestWheelSerialList(wheelSerial, limit).stream()
        .map(wheel -> TestWheelResponse.builder()
            .wheelSerial(wheel.getWheelSerial())
            .design(wheel.getDesign())
            .build())
        .collect(Collectors.toList());
  }

  public List<WheelRecord> findPerformanceWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findPerformanceWheelSerialList(wheelSerial, limit);
  }

  public List<String> findHLDReinspWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findHLDReinspWheelSerialList(wheelSerial, limit);
  }

  public Optional<WheelRecord> findRawWheelPrintWheel(String wheelSerial) {
    return wheelRecordRepository.findRawWheelPrintWheel(wheelSerial);
  }

  public List<String> findMachineWheelSerialList(String wheelSerial, Integer limit, String location) {
    if (location.equals("jmachine")) {
      return wheelRecordRepository.findJMachineWheelSerialList(wheelSerial, limit);
    } else if (location.equals("tmachine") || location.equals("wmachine")) {
      return wheelRecordRepository.findTWMachineWheelSerialList(wheelSerial, limit);
    } else if (location.equals("kmachine")) {
      return wheelRecordRepository.findKMachineWheelSerialList(wheelSerial, limit);
    } else if (location.equals("qmachine")) {
      return wheelRecordRepository.findQMachineWheelSerialList(wheelSerial, limit);
    }
    return new ArrayList<>();
  }

  /**
   * 按车轮号，以及车轮各个状态查询车轮，其中，parameterMap中的key为状态字段，value为true表示不等于0，为false表示等于0
   *
   * @param wheelSerial
   * @param parameterMap
   * @param limit
   * @return
   */
  public List<String> findWheelSerialList(String wheelSerial, Map<String, Boolean> parameterMap, Integer limit) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> q = cb.createQuery(String.class);
    Root<WheelRecord> r = q.from(WheelRecord.class);

    List<Predicate> list = new ArrayList<>();
    parameterMap.entrySet().forEach(entry -> {
      String parameter = entry.getKey();
      // map中的值如果是false，则等于0，为true，则不等于0
      list.add(entry.getValue()
          ? cb.notEqual(r.get(parameter), 0)
          : cb.equal(r.get(parameter), 0));
    });
    list.add(cb.like(r.get("wheelSerial"), wheelSerial + "%"));
    q.select(r.get("wheelSerial"));
    q.orderBy(cb.asc(r.get("wheelSerial")));
    q.where(cb.and(list.toArray(new Predicate[0])));
    TypedQuery<String> query = entityManager.createQuery(q);
    List<String> resultList = query.getResultList();
    return resultList.size() <= limit ? resultList : resultList.subList(0, limit);
  }

  public MachineResponse findMachineWheel(String wheelSerial) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("pre", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = findWheel(wheelSerial, parameterMap).get();
    MachineRecord machineRecord = machineRecordRepository.findByWheelSerial(wheelSerial).orElse(MachineRecord.builder().build());
    if (ObjectUtils.anyNull(wheelRecord, machineRecord)) {
      return null;
    }
    MachineResponse machineResponse = MachineResponse.builder().wheelSerial(wheelSerial).build();
    BeanUtil.copyProperties(wheelRecord, machineResponse, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    BeanUtil.copyProperties(machineRecord, machineResponse, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

    Integer internal = designRepository.findByDesign(wheelRecord.getDesign()).map(design -> design.getInternal()).orElse(null);
    List<String> codeListForKMachine = machiningCodeRepository.findBasicCodeList("K", "S2", "kmachine", 1);
    if (machineResponse.getTCounts() >= 1 && internal != null && internal == 1) {
      // 已有踏面加工记录&&轮型是国内的
      Integer kCounts = kMachineRecordRepository.findKCountsByWheelSerial(wheelSerial, 40);
      // 判断有无镗孔S2=40的记录，动态加载镗孔S2参数下拉框值
      if (kCounts != null && kCounts >= 1) {
        if (machineResponse.getDesign().equals("840HEZD-C")) {
          codeListForKMachine.add("203");
          codeListForKMachine.add("206");
        }
        if (machineResponse.getDesign().equals("840HDZD-C")) {
          codeListForKMachine.add("187");
        }
      }
    }
    machineResponse.setMachiningCodeList(codeListForKMachine);

    if (isRework(wheelRecord, machineRecord)) {
      machineResponse.setRework(1);
    }
    return machineResponse;
  }

  private boolean isRework(WheelRecord wheelRecord, MachineRecord machineRecord) {
    // 如果：
    // 1 输入的车轮序列号在Wheel_Record表的Rework_Code不为空；
    // 2 且该返工代码在Rework_Code表的Rework_flag字段是以“F”开始的；
    // 3 且在Machine_Record机加工状态表中J_id或T_id或K_id不为空
    // 则为返修车轮
    if (StringUtils.isNotBlank(wheelRecord.getReworkCode())) {
      ReworkCode reworkCode = reworkCodeRepository.findByCodeAndEnabled(wheelRecord.getReworkCode(), 1).get();
      if (reworkCode.getReworkFlag() != null && reworkCode.getReworkFlag().startsWith("F")
          && ObjectUtils.anyNotNull(machineRecord.getJId(), machineRecord.getTId(), machineRecord.getKId())) {
        return true;
      }
    }
    return false;
  }

  public Optional<WheelRecord> findWheel(String wheelSerial, Map<String, Boolean> parameterMap) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<WheelRecord> q = cb.createQuery(WheelRecord.class);
    Root<WheelRecord> r = q.from(WheelRecord.class);

    List<Predicate> list = new ArrayList<>();
    parameterMap.entrySet().forEach(entry -> {
      String parameter = entry.getKey();
      list.add(entry.getValue()
          ? cb.notEqual(r.get(parameter), 0)
          : cb.equal(r.get(parameter), 0));
    });
    list.add(cb.equal(r.get("wheelSerial"), wheelSerial));
    q.where(cb.and(list.toArray(new Predicate[0])));
    TypedQuery<WheelRecord> query = entityManager.createQuery(q);
    try {
      return Optional.of(query.getSingleResult());
    } catch (Exception e) {
      log.error(e.getMessage());
      return Optional.empty();
    }
  }

  public List<String> findSampleWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findSampleWheelSerialList(wheelSerial, limit);
  }

  public List<String> findXRayTransportWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findXRayTransportWheelSerialList(wheelSerial, limit);
  }

  public List<String> findDeWeightWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findDeWeightWheelSerialList(wheelSerial, limit);
  }

  public List<String> findBoreWheelSerialList(String wheelSerial, Integer limit) {
    return wheelRecordRepository.findBoreWheelSerialList(wheelSerial, limit);
  }

  public void deleteByWheelSerial(String wheelSerial) {
    wheelRecordRepository.deleteByWheelSerial(wheelSerial);
  }
}
