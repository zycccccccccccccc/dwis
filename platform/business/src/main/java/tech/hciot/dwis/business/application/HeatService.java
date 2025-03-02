package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_CUT_ID_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_IN_DATE_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_IN_ID_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_IN_OPERATOR_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_IN_SHIFT_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_IN_TIME_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_LINE_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_NOT_EXIST;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_OUT_DATE_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_OUT_ID_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_OUT_OPERATOR_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_OUT_SHIFT_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.HEAT_OUT_TIME_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_SERIAL_1_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_SERIAL_2_CAN_NOT_EMPTY;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.HeatParamsRepository;
import tech.hciot.dwis.business.domain.HeatRepository;
import tech.hciot.dwis.business.domain.HiHeatPreworkRecordRepository;
import tech.hciot.dwis.business.domain.model.Heat;
import tech.hciot.dwis.business.domain.model.HeatParams;
import tech.hciot.dwis.business.domain.model.HiHeatPreworkRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
@Slf4j
public class HeatService {

  @Autowired
  private HeatRepository heatRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private HiHeatPreworkRecordRepository hiHeatPreworkRecordRepository;

  @Autowired
  private HeatParamsRepository heatParamsRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Transactional
  public void save(Heat heat) {
    Heat saved = null;
    if (heat.getId() == null) {  //输完两个轮号，保存新记录
      String mecSerial = generateMecSerial(heat.getHeatLine());
      heat.setMecSerial(mecSerial);
      saved = saveStep1(heat);
    } else {
      if (heat.getHiHeatInDate() == null || heat.getHiHeatInTime() == null) {
        saved = updateWheelSerials(heat); //非正常顺序操作，修改轮号
      } else if (heat.getHiHeatOutDate() == null || heat.getHiHeatOutTime() == null) {
        saved = saveStep2(heat);  //输完高温进炉日期时间，进行保存（有可能修改轮号，在此进行保存）
      } else {
        saved = saveStep3(heat);  //输完高温出炉日期时间，进行保存（有可能修改轮号，在此进行保存）
      }
    }
    updateWheelHeatId(saved.getWheelSerial1(), saved);
    updateWheelHeatId(saved.getWheelSerial2(), saved);
  }

  private Heat updateWheelSerials(Heat heat) {
    return heatRepository.findById(heat.getId()).map(old -> {
      if (!old.getHeatCode1().isEmpty() && heat.getHeatCode1().isEmpty()) { // 如果删除已有的热处理代码，则不予保存
        if (old.getWheelSerial1().equals(heat.getWheelSerial1())) {
          heat.setHeatCode1(old.getHeatCode1());
        }
      }
      if (!old.getHeatCode2().isEmpty() && heat.getHeatCode2().isEmpty()) {
        if (old.getWheelSerial2().equals(heat.getWheelSerial2())) {
          heat.setHeatCode2(old.getHeatCode2());
        }
      }
      String oldWheelSerial1 = old.getWheelSerial1(); //通过同一id值获取已存的轮号
      String oldWheelSerial2 = old.getWheelSerial2();
      Heat ret = heatRepository.save(heat); //必须先保存heat,然后更新wheel_record表，顺序不能乱
      if (!oldWheelSerial1.equals(heat.getWheelSerial1()) || !oldWheelSerial2.equals(heat.getWheelSerial2())) { //轮1、轮2存在轮号改变情况
        if (!oldWheelSerial1.equals(heat.getWheelSerial1())) {
          modifyWheel(oldWheelSerial1);
          log.info("updateheatoldwheelserial1: {}, updateheatnewwheelserial1: {}", oldWheelSerial1, heat.getWheelSerial1());
        }
        if (!oldWheelSerial2.equals(heat.getWheelSerial2())) {
          modifyWheel(oldWheelSerial2);
          log.info("updateheatoldwheelserial2: {}, updateheatnewwheelserial2: {}", oldWheelSerial2, heat.getWheelSerial2());
        }
      }
      return ret;
    }).orElseThrow(HEAT_NOT_EXIST::getPlatformException);
  }

  private Heat saveStep3(Heat heat) {
    if (heat.getHeatLine() == null) {
      throw HEAT_LINE_CAN_NOT_EMPTY.getPlatformException();
    }
    if (heat.getHiHeatInTime() == null) {
      String err = "进炉时间不能为空,如若已输入，回车至下一行保存！";
      HEAT_IN_TIME_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_IN_TIME_CAN_NOT_EMPTY.getPlatformException();
    }

    if (heat.getHiHeatInDate() == null) {
      String err = "进炉日期不能为空,如若已输入，回车至下一行保存！";
      HEAT_IN_DATE_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_IN_DATE_CAN_NOT_EMPTY.getPlatformException();
    }
    if (heat.getHiHeatOutTime() == null) {
      String err = "出炉时间不能为空,如若已输入，回车至下一行保存！";
      HEAT_OUT_TIME_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_OUT_TIME_CAN_NOT_EMPTY.getPlatformException();
    }

    if (heat.getHiHeatOutDate() == null) {
      String err = "出炉日期不能为空,如若已输入，回车至下一行保存！";
      HEAT_OUT_DATE_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_OUT_DATE_CAN_NOT_EMPTY.getPlatformException();
    }

    if (StringUtils.isEmpty(heat.getHiHeatOutId())) {
      throw HEAT_OUT_ID_CAN_NOT_EMPTY.getPlatformException();
    }
    if (StringUtils.isEmpty(heat.getHiHeatOutOperator())) {
      throw HEAT_OUT_OPERATOR_CAN_NOT_EMPTY.getPlatformException();
    }
    if (heat.getHiHeatOutShift() == null) {
      throw HEAT_OUT_SHIFT_CAN_NOT_EMPTY.getPlatformException();
    }
    return heatRepository.findById(heat.getId()).map(old -> {
      if (!old.getHeatCode1().isEmpty() && heat.getHeatCode1().isEmpty()) { // 如果删除已有的热处理代码，则不予保存
        if (old.getWheelSerial1().equals(heat.getWheelSerial1())) {
          heat.setHeatCode1(old.getHeatCode1());
        }
      }
      if (!old.getHeatCode2().isEmpty() && heat.getHeatCode2().isEmpty()) {
        if (old.getWheelSerial2().equals(heat.getWheelSerial2())) {
          heat.setHeatCode2(old.getHeatCode2());
        }
      }
      if (!old.getWheelSerial1().equals(heat.getWheelSerial1()) || !old.getWheelSerial2().equals(heat.getWheelSerial2())) {
        String oldWheelSerial1 = old.getWheelSerial1();
        String oldWheelSerial2 = old.getWheelSerial2();
        heat.setXh(1);
        heat.setCreateDateTime(old.getCreateDateTime());
        Heat ret = heatRepository.save(heat); //必须先保存heat,然后更新wheel_record表，顺序不能乱
        if (!oldWheelSerial1.equals(heat.getWheelSerial1())) {
          modifyWheel(oldWheelSerial1);
          log.info("updateheatstep3oldwheelserial1: {}, updateheatstep3newwheelserial1: {}", oldWheelSerial1, heat.getWheelSerial1());
        }
        if (!oldWheelSerial2.equals(heat.getWheelSerial2())) {
          modifyWheel(oldWheelSerial2);
          log.info("updateheatstep3oldwheelserial2: {}, updateheatstep3newwheelserial2: {}", oldWheelSerial2, heat.getWheelSerial2());
        }
        return ret;
      } else {
        old.setHiHeatOutDate(heat.getHiHeatOutDate());
        old.setHiHeatOutTime(heat.getHiHeatOutTime());
        old.setHiHeatOutId(heat.getHiHeatOutId());
        old.setHiHeatOutOperator(heat.getHiHeatOutOperator());
        old.setHiHeatOutShift(heat.getHiHeatOutShift());
        old.setHeatCode1(heat.getHeatCode1());
        old.setHeatCode2(heat.getHeatCode2());
        old.setXh(1);
        old.setHid(heat.getHid());
        log.info("heatstep3wheelserial1: {}, heatstep3wheelserial2: {}", heat.getWheelSerial1(), heat.getWheelSerial2());
        return heatRepository.save(old);
      }
    }).orElseThrow(HEAT_NOT_EXIST::getPlatformException);
  }

  private Heat saveStep2(Heat heat) {
    if (heat.getHiHeatInTime() == null) {
      String err = "进炉时间不能为空,如若已输入，回车至下一行保存！";
      HEAT_IN_TIME_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_IN_TIME_CAN_NOT_EMPTY.getPlatformException();
    }

    if (heat.getHiHeatInDate() == null) {
      String err = "进炉日期不能为空,如若已输入，回车至下一行保存！";
      HEAT_IN_DATE_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_IN_DATE_CAN_NOT_EMPTY.getPlatformException();
    }
    if (heat.getHeatLine() == null) {
      throw HEAT_LINE_CAN_NOT_EMPTY.getPlatformException();
    }
    if (StringUtils.isEmpty(heat.getHiHeatInId())) {
      throw HEAT_IN_ID_CAN_NOT_EMPTY.getPlatformException();
    }
    if (StringUtils.isEmpty(heat.getHiHeatInOperator())) {
      throw HEAT_IN_OPERATOR_CAN_NOT_EMPTY.getPlatformException();
    }
    if (heat.getHiHeatInShift() == null) {
      throw HEAT_IN_SHIFT_CAN_NOT_EMPTY.getPlatformException();
    }
    if (StringUtils.isEmpty(heat.getCutId())) {
      throw HEAT_CUT_ID_CAN_NOT_EMPTY.getPlatformException();
    }
    return heatRepository.findById(heat.getId()).map(old -> {
      if (!old.getHeatCode1().isEmpty() && heat.getHeatCode1().isEmpty()) { // 如果删除已有的热处理代码，则不予保存
        if (old.getWheelSerial1().equals(heat.getWheelSerial1())) {
          heat.setHeatCode1(old.getHeatCode1());
        }
      }
      if (!old.getHeatCode2().isEmpty() && heat.getHeatCode2().isEmpty()) {
        if (old.getWheelSerial2().equals(heat.getWheelSerial2())) {
          heat.setHeatCode2(old.getHeatCode2());
        }
      }
      if (!old.getWheelSerial1().equals(heat.getWheelSerial1()) || !old.getWheelSerial2().equals(heat.getWheelSerial2())) {
        String oldWheelSerial1 = old.getWheelSerial1();
        String oldWheelSerial2 = old.getWheelSerial2();
        heat.setXh(0);
        heat.setCreateDateTime(old.getCreateDateTime());
        Heat ret = heatRepository.save(heat); //必须先保存heat,然后更新wheel_record表，顺序不能乱
        if (!oldWheelSerial1.equals(heat.getWheelSerial1())) {
          modifyWheel(oldWheelSerial1);
          log.info("updateheatstep2oldwheelserial1: {}, updateheatstep2newwheelserial1: {}", oldWheelSerial1, heat.getWheelSerial1());
        }
        if (!oldWheelSerial2.equals(heat.getWheelSerial2())) {
          modifyWheel(oldWheelSerial2);
          log.info("updateheatstep2oldwheelserial2: {}, updateheatstep2newwheelserial2: {}", oldWheelSerial2, heat.getWheelSerial2());
        }
        return ret;
      } else {
        old.setHeatLine(heat.getHeatLine());
        old.setHiHeatInDate(heat.getHiHeatInDate());
        old.setHiHeatInTime(heat.getHiHeatInTime());
        old.setHeatCode1(heat.getHeatCode1());
        old.setHeatCode2(heat.getHeatCode2());
        old.setHiHeatInOperator(heat.getHiHeatInOperator());
        old.setHiHeatInId(heat.getHiHeatInId());
        old.setHiHeatInShift(heat.getHiHeatInShift());
        old.setCutId(heat.getCutId());
        old.setXh(0);
        log.info("heatstep2wheelserial1: {}, heatstep2wheelserial2: {}", heat.getWheelSerial1(), heat.getWheelSerial2());
        return heatRepository.save(old);
      }
    }).orElseThrow(HEAT_NOT_EXIST::getPlatformException);
  }

  private Heat saveStep1(Heat heat) {
    assertWheelSerial1NotEmpty(heat.getWheelSerial1());
    assertWheelSerial2NotEmpty(heat.getWheelSerial2());
    Heat newHeat = new Heat();
    newHeat.setWheelSerial1(heat.getWheelSerial1());
    newHeat.setWheelSerial2(heat.getWheelSerial2());
    newHeat.setDesign1(heat.getDesign1());
    newHeat.setDesign2(heat.getDesign2());
    newHeat.setTestCode1(heat.getTestCode1());
    newHeat.setTestCode2(heat.getTestCode2());
    if (heat.getHeatCode1() != null && !heat.getHeatCode1().isEmpty()) { // 只有heatCode非空，heat表才进行保存该字段，这样达到只能增加/修改，不能删除已有值
      newHeat.setHeatCode1(heat.getHeatCode1());
    }
    if (heat.getHeatCode2() != null && !heat.getHeatCode2().isEmpty()) {
      newHeat.setHeatCode2(heat.getHeatCode2());
    }
    newHeat.setScrapCode1(heat.getScrapCode1());
    newHeat.setScrapCode2(heat.getScrapCode2());
    newHeat.setXh(0);
    newHeat.setHeatLine(heat.getHeatLine());
    newHeat.setMecSerial(heat.getMecSerial());
    newHeat.setCreateDateTime(new Date());
    heat = heatRepository.save(newHeat);
    log.info("heatstep1wheelserial1: {}, heatstep1wheelserial2: {}", heat.getWheelSerial1(), heat.getWheelSerial2());
    return heat;
  }

  private void assertWheelSerial1NotEmpty(String wheelSerial1) {
    if (StringUtils.isEmpty(wheelSerial1)) {
      throw WHEEL_SERIAL_1_CAN_NOT_EMPTY.getPlatformException();
    }
  }

  private void assertWheelSerial2NotEmpty(String wheelSerial2) {
    if (StringUtils.isEmpty(wheelSerial2)) {
      throw WHEEL_SERIAL_2_CAN_NOT_EMPTY.getPlatformException();
    }
  }

  private void updateWheelHeatId(String wheelSerial, Heat heat) {
    wheelRecordService.findByWheelSerial(wheelSerial).ifPresent(wheelRecord -> {
      if (wheelRecord.getFinished() != 1 && wheelRecord.getConfirmedScrap() != 1) {
        wheelRecord.setHeatId(heat.getId());
        log.info("update mecSerial: {}, wheelSerial: {}", heat.getMecSerial(), wheelRecord.getWheelSerial());
        wheelRecord.setMecSerial(heat.getMecSerial());
        wheelRecord.setMecConfirm(0);
        wheelRecordService.save(wheelRecord);
      }
    });
  }

  public void refreshHeatLine() {
    List<Heat> heatList = new ArrayList<>();
    heatRepository.findByXh(1).stream().forEach(heat -> {
      if (ObjectUtils
          .allNotNull(heat.getHiHeatInDate(), heat.getHiHeatInTime(), heat.getHiHeatOutDate(), heat.getHiHeatOutTime())) {
        heat.setXh(2);
        heatList.add(heat);
        if (StringUtils.isNotBlank(heat.getScrapCode1()) || StringUtils.isNotBlank(heat.getScrapCode2())) { //如果刷新时，存在废码非空的情况，则更新至wheelrecord
          if (StringUtils.isNotBlank(heat.getScrapCode1())) {
            wheelRecordService.findByWheelSerial(heat.getWheelSerial1()).ifPresent(wheelRecord -> {
              if (wheelRecord.getFinished() != 1 && wheelRecord.getConfirmedScrap() != 1) {
                wheelRecord.setScrapCode(heat.getScrapCode1());
                wheelRecordService.save(wheelRecord);
              }
            });
          }
          if (StringUtils.isNotBlank(heat.getScrapCode2())) {
            wheelRecordService.findByWheelSerial(heat.getWheelSerial2()).ifPresent(wheelRecord -> {
              if (wheelRecord.getFinished() != 1 && wheelRecord.getConfirmedScrap() != 1) {
                wheelRecord.setScrapCode(heat.getScrapCode2());
                wheelRecordService.save(wheelRecord);
              }
            });
          }
        }
      }
    });
    heatRepository.saveAll(heatList);
  }

  public List<Heat> find(Integer heatLine, String inOperator, String outOperator, String wheelSerial, String inDate,
      String outDate) {
    log.info("begin find heat");
    Specification<Heat> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(inOperator)) {
        list.add(criteriaBuilder.equal(root.get("hiHeatInOperator"), inOperator));
      }
      if (StringUtils.isNotEmpty(outOperator)) {
        list.add(criteriaBuilder.equal(root.get("hiHeatOutOperator"), outOperator));
      }
      if (StringUtils.isNotEmpty(wheelSerial)) {
        Predicate p1 = criteriaBuilder.equal(root.get("wheelSerial1"), wheelSerial);
        Predicate p2 = criteriaBuilder.equal(root.get("wheelSerial2"), wheelSerial);
        list.add(criteriaBuilder.or(p1, p2));
      } else {
        if (heatLine != null) {
          list.add(criteriaBuilder.equal(root.get("heatLine"), heatLine));
        }
      }
      if (StringUtils.isNotEmpty(inDate)) {
        Date date = parseDate(inDate);
        list.add(criteriaBuilder.equal(root.get("hiHeatInDate"), date));
      }
      if (StringUtils.isNotEmpty(outDate)) {
        Date date = parseDate(outDate);
        list.add(criteriaBuilder.equal(root.get("hiHeatOutDate"), date));
      }
      if (StringUtils.isAllBlank(inOperator, inDate, outOperator, outDate, wheelSerial)) {
        Predicate p1 = criteriaBuilder.equal(root.get("xh"), 0);
        Predicate p2 = criteriaBuilder.equal(root.get("xh"), 1);
        list.add(criteriaBuilder.or(p1, p2));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.asc(root.get("id")));
      return query.getRestriction();
    };
    return heatRepository.findAll(specification).stream().map(heat -> {
      heat.setHeatCount1(wheelRecordService.findHeatTimes(heat.getWheelSerial1()));
      heat.setHeatCount2(wheelRecordService.findHeatTimes(heat.getWheelSerial2()));

      if (heat.getMecSerial() != null) {
        Integer startPos = heat.getMecSerial().lastIndexOf("-");
        if (startPos > 0) { // 找出这对车轮在该批次中是第几对的
          heat.setMecCount(heat.getMecSerial().substring(startPos + 1));
        }
      }
      return heat;
    }).collect(Collectors.toList());
  }

  private void modifyWheel(String wheelSerial) {
    Heat maxHeat = heatRepository.findMaxHeat(wheelSerial);
    wheelRecordService.findByWheelSerial(wheelSerial).ifPresent(wheelRecord -> {
      if (maxHeat != null) { //找到最近的热处理记录
        if (wheelRecord.getFinished() != 1 && wheelRecord.getConfirmedScrap() != 1) {
          wheelRecord.setHeatId(maxHeat.getId());
          if (maxHeat.getWheelSerial1().equals(wheelRecord.getWheelSerial())) {
            wheelRecord.setHeatCode(maxHeat.getHeatCode1());
          }
          if (maxHeat.getWheelSerial2().equals(wheelRecord.getWheelSerial())) {
            wheelRecord.setHeatCode(maxHeat.getHeatCode2());
          }
          log.info("update mecSerial: {}, wheelSerial: {}", maxHeat.getMecSerial(), wheelRecord.getWheelSerial());
          wheelRecord.setMecSerial(maxHeat.getMecSerial());
        }
      } else { //没有热处理记录
        if (wheelRecord.getFinished() != 1 && wheelRecord.getConfirmedScrap() != 1) {
          wheelRecord.setHeatId(null);
          wheelRecord.setHeatCode("");
          wheelRecord.setMecSerial(null);
        }
      }
      wheelRecordService.save(wheelRecord);
    });
  }

  public Optional<Heat> findById(Integer id) {
    return heatRepository.findById(id);
  }

  @Transactional
  public void modifyHeatTimeout(Integer id, Heat newHeat) {
    heatRepository.findById(id).map(heat -> {
      BeanUtil.copyProperties(newHeat, heat, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      heatRepository.save(heat);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("热处理信息不存在"));
  }

  public Integer addPrework(HiHeatPreworkRecord hiHeatPreworkRecord) {
    hiHeatPreworkRecord.setCreateTime(new Date());
    return hiHeatPreworkRecordRepository.save(hiHeatPreworkRecord).getId();
  }

  public Page<HiHeatPreworkRecord> getPrework(Integer furNo, Integer currentPage, Integer pageSize) {
    Specification<HiHeatPreworkRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (furNo != null) {
        list.add(criteriaBuilder.equal(root.get("furNo"), furNo));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return hiHeatPreworkRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public void editPrework(Integer id, HiHeatPreworkRecord hiHeatPreworkRecord) {
    hiHeatPreworkRecordRepository.findById(id).ifPresent(old -> {
      hiHeatPreworkRecord.setId(old.getId());
      hiHeatPreworkRecord.setCreateTime(old.getCreateTime());
      hiHeatPreworkRecordRepository.save(hiHeatPreworkRecord);
    });
  }

  private String initMecSerial(Integer heatLine) { //取最新热处理记录的炉号，系统当前时间的年份，生成新的性能批次号，如C2023-H2-1-1
    return StringUtils.join("C", DateFormatUtils.format(new Date(), "yyyy"), "-", "H", heatLine, "-", "1", "-", "1");
  }

  private String generateMecSerial(Integer heatLine) {
    String mecSerial = null;
    Optional<Heat> optionalHeat = heatRepository.findMaxHeatByHeatLine(heatLine);
    if (optionalHeat.isPresent()) { //最新的热处理记录存在
      Heat lastHeat = optionalHeat.get();
      Date createDate = lastHeat.getCreateDateTime();
      Date now = new Date();
      if (DateUtils.truncatedEquals(createDate, now, Calendar.YEAR)) { //最新的热处理记录创建日期的年份与系统当前时间的年份相同
        if (StringUtils.isNotEmpty(lastHeat.getMecSerial())) { //最新的热处理记录的性能批次号不为空
          String[] mecSerialArray = StringUtils.split(lastHeat.getMecSerial(), "-");
          if (DateUtils.addDays(createDate, 1).after(now)) {  //最新的热处理记录创建日期与当前系统时间相差不超过24小时
            int count = Integer.parseInt(mecSerialArray[3]);
            if (count >= operatingTimeCtrService.getOperatingTimeNumber("HT")) { //最新热处理记录的性能批次号的轮对数大于等于125
              mecSerialArray[2] = String.valueOf(Integer.parseInt(mecSerialArray[2]) + 1); //重新开始下一个批次
              mecSerialArray[3] = "1";
            } else { //最新热处理记录的性能批次号的轮对数小于125
              mecSerialArray[3] = String.valueOf(count + 1); //在最新的热处理记录的性能批次数上加1
            }
          } else { //最新的热处理记录创建日期与当前系统时间相差超过24小时
            mecSerialArray[2] = String.valueOf(Integer.parseInt(mecSerialArray[2]) + 1); //重新开始下一个批次
            mecSerialArray[3] = "1";
          }
          mecSerial = StringUtils.join(mecSerialArray, "-");
        } else { //最新的热处理记录的性能批次号为空
          mecSerial = initMecSerial(lastHeat.getHeatLine());
        }
      } else { //最新的热处理记录创建日期的年份与系统当前时间的年份不相同
        mecSerial = initMecSerial(lastHeat.getHeatLine());
      }
    } else { //最新的热处理记录不存在
      mecSerial = initMecSerial(heatLine);
    }
    return mecSerial;
  }

  public Optional<HeatParams> findProcParams(String type) {
    return heatParamsRepository.findByTypeAndEnabled(type,1);
  }
}
