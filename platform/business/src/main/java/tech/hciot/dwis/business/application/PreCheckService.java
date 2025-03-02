package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
import tech.hciot.dwis.business.domain.model.HeatCode;
import tech.hciot.dwis.business.domain.model.MachineRecord;
import tech.hciot.dwis.business.domain.model.PreCheckRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.PreCheckResponse;

@Service
@Slf4j
public class PreCheckService {

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private PreCheckRecordRepository preCheckRepository;

  @Autowired
  private HeatRepository heatRepository;

  @Autowired
  private HeatCodeRepository heatCodeRepository;

  @Autowired
  private ChemistryDetailService chemistryDetailService;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private MachineRecordRepository machineRecordRepository;

  @Autowired
  private HardnessControlService hardnessControlService;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<PreCheckRecord> find(String wheelSerial,
      String scrapCode,
      String reworkCode,
      String inspectorId,
      Integer currentPage,
      Integer pageSize) {
    Specification<PreCheckRecord> specification = (root, query, criteriaBuilder) -> {
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
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return preCheckRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Page<PreCheckRecord> findForRawWheel(String wheelSerial,
                                              String design,
                                              String copeInspectorId,
                                              Integer currentPage,
                                              Integer pageSize) {
    Specification<PreCheckRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (design != null) {
        list.add(criteriaBuilder.equal(root.get("design"), design));
      }
      if (copeInspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("copeInspectorId"), copeInspectorId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("opeDT")));
      return query.getRestriction();
    };
    return preCheckRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  /**
   * 获取车轮信息
   * @param wheelSerial
   * @return
   */
  public PreCheckResponse findWheel(String wheelSerial) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(wheelSerial, parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("轮号输入有误！"));
    PreCheckResponse response = PreCheckResponse.builder().build();
    BeanUtil.copyProperties(wheelRecord, response, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    updateHeatCode(wheelRecord, response);
    updateScrapCode(wheelRecord, response);
    response.setInChemistryControl(isInChemistryControl(wheelRecord));
    return response;
  }

  private void updateHeatCode(WheelRecord wheelRecord, PreCheckResponse response) {
    Integer heatId = wheelRecord.getHeatId();
    if (heatId != null) { //有热处理记录
      heatRepository.findById(heatId).ifPresent(heat -> {
        if (heat.getXh() == 5) { //回火炉已提交
          Optional<HeatCode> heatCode = heatCodeRepository.findByCodeAndEnabled(wheelRecord.getHeatCode(), 1);
          if (!heatCode.isPresent()) { //没有热处理代码
            response.setAlreadHeat(true);
          } else { //有热处理代码
            if (Objects.equals(heatCode.get().getCodeType(), "Hold")) { //热处理代码为Hold类型
              response.setAlreadHeat(false);
            } else { //热处理代码不是Hold类型
              response.setAlreadHeat(true);
            }
          }
        } else { //回火炉未提交
          response.setAlreadHeat(false);
          response.setHeatCode("ARHT");
        }
      });
    } else {  //如若没有热处理记录，赋值ARHT
      response.setAlreadHeat(false);
      response.setHeatCode("ARHT");
    }
  }

  private void updateScrapCode(WheelRecord wheelRecord, PreCheckResponse response) {
    response.setScrapCode("77");
    Integer ladleId = wheelRecord.getLadleId();
    if (ladleId != null) {
      chemistryDetailService.findByLadleId(ladleId).ifPresent(chemistryDetail -> {
        if (chemistryDetailService.isChemistryStandard(wheelRecord.getDesign(), chemistryDetail)) {
          response.setScrapCode(wheelRecord.getScrapCode());
        }
      });
    }
  }

  private boolean isInChemistryControl(WheelRecord wheelRecord) {
    if ("XNF".equals(wheelRecord.getTestCode())) {
      return false;
    }
    AtomicBoolean inChemistryControl = new AtomicBoolean(false);
    Integer ladleId = wheelRecord.getLadleId();
    if (ladleId != null) {
      chemistryDetailService.findByLadleId(ladleId).ifPresent(chemistryDetail -> {
        if (chemistryDetailService.isInChemistryControl(wheelRecord.getDesign(), chemistryDetail)) {
          inChemistryControl.set(true);
        }
      });
    }
    return inChemistryControl.get();
  }

  /**
   * 添加预检信息
   * @param preCheckRecord
   * @return
   */
  public PreCheckRecord add(PreCheckRecord preCheckRecord) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(preCheckRecord.getWheelSerial(), parameterMap)
      .orElseThrow(() -> PlatformException.badRequestException("轮号输入有误！"));
    preCheckRecord.setCreateTime(new Date());
    preCheckRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxPreCheckTimes(preCheckRecord);
    updateBrinReq(preCheckRecord, wheelRecord);
    checkInChemistryControl(preCheckRecord, wheelRecord);
    preCheckRepository.save(preCheckRecord);
    updateWheelRecord(preCheckRecord, wheelRecord);
    return preCheckRecord;
  }

  private void updateWheelRecord(PreCheckRecord preCheckRecord, WheelRecord wheelRecord) {
    wheelRecord.setPreId(preCheckRecord.getId());
    wheelRecord.setPre(preCheckRecord.getTs());
    wheelRecord.setLastPre(preCheckRecord.getOpeDT());
    wheelRecord.setScrapCode(preCheckRecord.getScrapCode());
    if (preCheckRecord.getReworkCode() != null) {
      wheelRecord.setReworkCode(preCheckRecord.getReworkCode());
    }
    if (preCheckRecord.getGrindDepth() != null) {
      wheelRecord.setGrindDepth(preCheckRecord.getGrindDepth());
    }
    if (preCheckRecord.getHeatCode() != null) {
      wheelRecord.setHeatCode(preCheckRecord.getHeatCode());
    }
    if (preCheckRecord.getHfs() != null) {
      wheelRecord.setHfs(preCheckRecord.getHfs());
    }
    if (preCheckRecord.getTs() == 1) {
      wheelRecord.setPreDate(preCheckRecord.getOpeDT());
      MachineRecord machineRecord = MachineRecord.builder()
        .wheelSerial(preCheckRecord.getWheelSerial())
        .design(preCheckRecord.getDesign())
        .createTime(new Date())
        .build();
      machineRecordRepository.save(machineRecord);
    }
    wheelRecordRepository.save(wheelRecord);
  }

  private void updateBrinReq(PreCheckRecord preCheckRecord, WheelRecord wheelRecord) {
    int brinReq = 1;
    Integer brinnelReading = wheelRecord.getBrinnelReading();
    if (brinnelReading != null) {
      boolean inScope = hardnessControlService.isHardnessInScope(preCheckRecord.getDesign(), brinnelReading);
      if (inScope) {
        brinReq = 0;
      }
    }
    wheelRecord.setBrinReq(brinReq);
    preCheckRecord.setBrinReq(brinReq);
  }

  private void checkInChemistryControl(PreCheckRecord preCheckRecord, WheelRecord wheelRecord) {
    boolean inChemistryControl = isInChemistryControl(wheelRecord);
    if (inChemistryControl) {
      preCheckRecord.setTestCode("XN");
      wheelRecord.setTestCode("XN");
    }
  }

  private void updateMaxPreCheckTimes(PreCheckRecord preCheckRecord) {
    Integer maxPreCheckTimes = preCheckRepository.findMaxPreCheckTimes(preCheckRecord.getWheelSerial()) + 1;
    preCheckRecord.setTs(maxPreCheckTimes);
  }
}
