package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.*;
import static tech.hciot.dwis.base.util.StandardTimeUtil.timeStr;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.*;


import java.util.*;
import java.util.function.Consumer;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.HeatRepository;
import tech.hciot.dwis.business.domain.LowHeatPreworkRecordRepository;
import tech.hciot.dwis.business.domain.PourRecordRepository;
import tech.hciot.dwis.business.domain.model.Heat;
import tech.hciot.dwis.business.domain.model.LowHeatPreworkRecord;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
@Slf4j
public class LowService {

  @Autowired
  private HeatRepository heatRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private LowHeatPreworkRecordRepository lowHeatPreworkRecordRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  public List<Heat> find(String inOperator, String outOperator, String wheelSerial, String inDate, String outDate) {
    Specification<Heat> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(inOperator)) {
        list.add(criteriaBuilder.equal(root.get("lowHeatInOperator"), inOperator));
      }
      if (StringUtils.isNotEmpty(outOperator)) {
        list.add(criteriaBuilder.equal(root.get("lowHeatOutOperator"), outOperator));
      }
      if (StringUtils.isNotEmpty(wheelSerial)) {
        Predicate p1 = criteriaBuilder.equal(root.get("wheelSerial1"), wheelSerial);
        Predicate p2 = criteriaBuilder.equal(root.get("wheelSerial2"), wheelSerial);
        list.add(criteriaBuilder.or(p1, p2));
      }
      if (StringUtils.isNotEmpty(inDate)) {
        Date date = parseDate(inDate);
        list.add(criteriaBuilder.equal(root.get("lowHeatInDate"), date));
      }
      if (StringUtils.isNotEmpty(outDate)) {
        Date date = parseDate(outDate);
        list.add(criteriaBuilder.equal(root.get("lowHeatOutDate"), date));
      }
      if (StringUtils.isAllBlank(inOperator, outOperator, wheelSerial, inDate, outDate)) {
        Predicate p1 = criteriaBuilder.equal(root.get("xh"), 2);
        Predicate p2 = criteriaBuilder.equal(root.get("xh"), 3);
        Predicate p3 = criteriaBuilder.equal(root.get("xh"), 4);
        list.add(criteriaBuilder.or(p1, p2, p3));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.asc(root.get("hiHeatOutDate")), criteriaBuilder.asc(root.get("hiHeatOutTime")),
          criteriaBuilder.asc(root.get("heatLine")));
      return query.getRestriction();
    };
    List<Heat> result = heatRepository.findAll(specification);
    if(!result.isEmpty()){
      for (Heat heat: result ) {
        if(heat.getHeatLine() == 1) { heat.setYellowLight(Arrays.asList("heatLine"));}
      }
    }
    return result;
//        .map(heat -> {
//          WheelRecord wheelRecord1 = wheelRecordService.findWheelWithoutThrowException(heat.getWheelSerial1());
//          WheelRecord wheelRecord2 = wheelRecordService.findWheelWithoutThrowException(heat.getWheelSerial2());
//          heat.setHeatCount1(wheelRecord1 == null ? 0 : wheelRecord1.getHeatTimes());
//          heat.setHeatCount2(wheelRecord2 == null ? 0 : wheelRecord2.getHeatTimes());
//          return heat;
//        });
  }

  @Transactional
  public void save(Heat heat) {
    if (heat.getLowHeatInTime() == null) {
      String err = "进炉时间不能为空,如若已输入，回车至下一行保存！";
      HEAT_IN_TIME_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_IN_TIME_CAN_NOT_EMPTY.getPlatformException();
    }

    if (heat.getLowHeatInDate() == null) {
      String err = "进炉日期不能为空,如若已输入，回车至下一行保存！";
      HEAT_IN_DATE_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
      throw HEAT_IN_DATE_CAN_NOT_EMPTY.getPlatformException();
    }

      heatRepository.findById(heat.getId()).map(old -> {
      int xh = 3;
      if (ObjectUtils.allNotNull(heat.getLowHeatOutDate(), heat.getLowHeatOutTime())) {
        xh = 4;
        old.setHeatCode1(heat.getHeatCode1());
        old.setHeatCode2(heat.getHeatCode2());
        old.setLowHeatOutDate(heat.getLowHeatOutDate());
        old.setLowHeatOutTime(heat.getLowHeatOutTime());
        old.setLowHeatOutOperator(heat.getLowHeatOutOperator());
        old.setLowHeatOutId(heat.getLowHeatOutId());
        old.setLowHeatOutShift(heat.getLowHeatOutShift());
        old.setLid(heat.getLid());
      } else {
        old.setHeatCode1(heat.getHeatCode1());
        old.setHeatCode2(heat.getHeatCode2());
        old.setLowHeatInDate(heat.getLowHeatInDate());
        old.setLowHeatInTime(heat.getLowHeatInTime());
        old.setLowHeatInOperator(heat.getLowHeatInOperator());
        old.setLowHeatInId(heat.getLowHeatInId());
        old.setLowHeatInShift(heat.getLowHeatInShift());
      }
      old.setXh(xh);
      return heatRepository.save(old);
    }).orElseThrow(HEAT_NOT_EXIST::getPlatformException);
  }

  @Transactional
  public void refreshLow() {
    heatRepository.findByXh(4).forEach(heat -> {
      if (heat.getLowHeatInDate() == null) {
        String err = "进炉日期不能为空,如若已输入，回车至下一行保存！";
        HEAT_IN_DATE_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
        throw HEAT_IN_DATE_CAN_NOT_EMPTY.getPlatformException();
      }
      if (heat.getLowHeatInTime() == null) {
        String err = "进炉时间不能为空,如若已输入，回车至下一行保存！";
        HEAT_IN_TIME_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
        throw HEAT_IN_TIME_CAN_NOT_EMPTY.getPlatformException();
      }
      if (heat.getLowHeatOutDate() == null) {
        String err = "出炉日期不能为空,如若已输入，回车至下一行保存！";
        HEAT_OUT_DATE_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
        throw HEAT_OUT_DATE_CAN_NOT_EMPTY.getPlatformException();
      }
      if (heat.getLowHeatOutTime() == null) {
        String err = "出炉时间不能为空,如若已输入，回车至下一行保存！";
        HEAT_OUT_TIME_CAN_NOT_EMPTY.setErrordesc(heat.getWheelSerial1() + "/" + heat.getWheelSerial2() + err);
        throw HEAT_OUT_TIME_CAN_NOT_EMPTY.getPlatformException();
      }
      heat.setXh(5);
      heatRepository.save(heat);
      // 修改热处理记录时，不更新wheel_record表中heat_code,在此处提交数据时进行更新
      wheelRecordService.findByWheelSerial(heat.getWheelSerial1()).ifPresent(modifyWheelRecord(heat.getHeatCode1()));
      wheelRecordService.findByWheelSerial(heat.getWheelSerial2()).ifPresent(modifyWheelRecord(heat.getHeatCode2()));
    });
  }

  private Consumer<WheelRecord> modifyWheelRecord(String code) {
    return (wheelRecord -> {
      if (wheelRecord.getConfirmedScrap() != 1 && wheelRecord.getFinished() != 1) {
        wheelRecord.setHeatCode(code);
        wheelRecord.setHeatTimes(wheelRecord.getHeatTimes() + 1);
        wheelRecord.setBrinReq(1);
        wheelRecordService.save(wheelRecord);
      }
    });
  }

  public Heat setEmptyHeat(String type, Heat h1, Heat h2) {
    if (type == "Hi_Heat_In") {
      h1.setXh(0);
      h1.setHeatLine(h2.getHeatLine());
      h1.setHiHeatInDate(null);
      h1.setHiHeatInTime(null);
      h1.setCutId("");
      h1.setHiHeatInId("");
      h1.setHiHeatInOperator("");
      h1.setHiHeatInShift(null);

      h1.setHiHeatOutDate(null);
      h1.setHiHeatOutTime(null);
      h1.setHiHeatOutId("");
      h1.setHiHeatOutOperator("");
      h1.setHiHeatOutShift(null);
      
      h1.setHeatCode1("");
      h1.setHeatCode2("");

      h1.setLowHeatInOperator("");
      h1.setLowHeatInId("");
      h1.setLowHeatInShift(null);
      h1.setLowHeatInDate(null);
      h1.setLowHeatInTime(null);

      h1.setLowHeatOutOperator("");
      h1.setLowHeatOutId("");
      h1.setLowHeatOutShift(null);
      h1.setLowHeatOutDate(null);
      h1.setLowHeatOutTime(null);
    }
    else if (type == "Hi_Heat_Out") {
      h1.setXh(0);
      h1.setHeatLine(h2.getHeatLine());
      h1.setHiHeatInDate(h2.getHiHeatInDate());
      h1.setHiHeatInTime(h2.getHiHeatInTime());
      h1.setCutId(h2.getCutId());
      h1.setHiHeatInId(h2.getHiHeatInId());
      h1.setHiHeatInOperator(h2.getHiHeatInOperator());
      h1.setHiHeatInShift(h2.getHiHeatInShift());

      h1.setHeatCode1(h2.getHeatCode1());
      h1.setHeatCode2(h2.getHeatCode2());

      h1.setHiHeatOutDate(null);
      h1.setHiHeatOutTime(null);
      h1.setHiHeatOutId("");
      h1.setHiHeatOutOperator("");
      h1.setHiHeatOutShift(null);

      h1.setLowHeatInOperator("");
      h1.setLowHeatInId("");
      h1.setLowHeatInShift(null);
      h1.setLowHeatInDate(null);
      h1.setLowHeatInTime(null);

      h1.setLowHeatOutOperator("");
      h1.setLowHeatOutId("");
      h1.setLowHeatOutShift(null);
      h1.setLowHeatOutDate(null);
      h1.setLowHeatOutTime(null);
    }
    else if (type == "Low_Heat_In") {
      h1.setXh(2);
      h1.setHeatLine(h2.getHeatLine());
      h1.setHiHeatInDate(h2.getHiHeatInDate());
      h1.setHiHeatInTime(h2.getHiHeatInTime());
      h1.setCutId(h2.getCutId());
      h1.setHiHeatInId(h2.getHiHeatInId());
      h1.setHiHeatInOperator(h2.getHiHeatInOperator());
      h1.setHiHeatInShift(h2.getHiHeatInShift());

      h1.setHeatCode1(h2.getHeatCode1());
      h1.setHeatCode2(h2.getHeatCode2());

      h1.setHiHeatOutDate(h2.getHiHeatOutDate());
      h1.setHiHeatOutTime(h2.getHiHeatOutTime());
      h1.setHiHeatOutId(h2.getHiHeatOutId());
      h1.setHiHeatOutOperator(h2.getHiHeatOutOperator());
      h1.setHiHeatOutShift(h2.getHiHeatOutShift());

      h1.setLowHeatInOperator("");
      h1.setLowHeatInId("");
      h1.setLowHeatInShift(null);
      h1.setLowHeatInDate(null);
      h1.setLowHeatInTime(null);

      h1.setLowHeatOutOperator("");
      h1.setLowHeatOutId("");
      h1.setLowHeatOutShift(null);
      h1.setLowHeatOutDate(null);
      h1.setLowHeatOutTime(null);
    }
    else if (type == "Low_Heat_Out") {
      h1.setXh(3);
      h1.setHeatLine(h2.getHeatLine());
      h1.setHiHeatInDate(h2.getHiHeatInDate());
      h1.setHiHeatInTime(h2.getHiHeatInTime());
      h1.setCutId(h2.getCutId());
      h1.setHiHeatInId(h2.getHiHeatInId());
      h1.setHiHeatInOperator(h2.getHiHeatInOperator());
      h1.setHiHeatInShift(h2.getHiHeatInShift());

      h1.setHeatCode1(h2.getHeatCode1());
      h1.setHeatCode2(h2.getHeatCode2());

      h1.setHiHeatOutDate(h2.getHiHeatOutDate());
      h1.setHiHeatOutTime(h2.getHiHeatOutTime());
      h1.setHiHeatOutId(h2.getHiHeatOutId());
      h1.setHiHeatOutOperator(h2.getHiHeatOutOperator());
      h1.setHiHeatOutShift(h2.getHiHeatOutShift());

      h1.setLowHeatInOperator(h2.getLowHeatInOperator());
      h1.setLowHeatInId(h2.getLowHeatInId());
      h1.setLowHeatInShift(h2.getLowHeatInShift());
      h1.setLowHeatInDate(h2.getLowHeatInDate());
      h1.setLowHeatInTime(h2.getLowHeatInTime());

      h1.setLowHeatOutOperator("");
      h1.setLowHeatOutId("");
      h1.setLowHeatOutShift(null);
      h1.setLowHeatOutDate(null);
      h1.setLowHeatOutTime(null);
    } else if (type == "None") {
      h1.setXh(4);
      h1.setHeatLine(h2.getHeatLine());
      h1.setHiHeatInDate(h2.getHiHeatInDate());
      h1.setHiHeatInTime(h2.getHiHeatInTime());
      h1.setCutId(h2.getCutId());
      h1.setHiHeatInId(h2.getHiHeatInId());
      h1.setHiHeatInOperator(h2.getHiHeatInOperator());
      h1.setHiHeatInShift(h2.getHiHeatInShift());

      h1.setHeatCode1(h2.getHeatCode1());
      h1.setHeatCode2(h2.getHeatCode2());

      h1.setHiHeatOutDate(h2.getHiHeatOutDate());
      h1.setHiHeatOutTime(h2.getHiHeatOutTime());
      h1.setHiHeatOutId(h2.getHiHeatOutId());
      h1.setHiHeatOutOperator(h2.getHiHeatOutOperator());
      h1.setHiHeatOutShift(h2.getHiHeatOutShift());

      h1.setLowHeatInOperator(h2.getLowHeatInOperator());
      h1.setLowHeatInId(h2.getLowHeatInId());
      h1.setLowHeatInShift(h2.getLowHeatInShift());
      h1.setLowHeatInDate(h2.getLowHeatInDate());
      h1.setLowHeatInTime(h2.getLowHeatInTime());

      h1.setLowHeatOutOperator(h2.getLowHeatOutOperator());
      h1.setLowHeatOutId(h2.getLowHeatOutId());
      h1.setLowHeatOutShift(h2.getLowHeatOutShift());
      h1.setLowHeatOutDate(h2.getLowHeatOutDate());
      h1.setLowHeatOutTime(h2.getLowHeatOutTime());
    }
    return h1;
  }

  @Transactional
  public void modifyHeat(String id, Heat heat) {
      Heat old = heatRepository.findById(Integer.valueOf(id)).orElseThrow(
              () -> PlatformException.badRequestException("热处理信息不存在: " + heat.getWheelSerial1() + heat.getWheelSerial2()));
      heat.setCreateDateTime(old.getCreateDateTime());
      String oldWheelSerial1 = old.getWheelSerial1();
      String oldWheelSerial2 = old.getWheelSerial2();
      if (!oldWheelSerial1.equals(heat.getWheelSerial1()) || !oldWheelSerial2.equals(heat.getWheelSerial2())) {
        heatRepository.save(heat); //必须先保存heat,然后更新wheel_record表，顺序不能乱
        //修改轮号时，wheel_record表中的HeatId、MecSerial都进行更新
        if (!oldWheelSerial1.equals(heat.getWheelSerial1())) {
          modifyWheel(oldWheelSerial1, 1);
          modifyNewWheel(heat, 1);
        }
        if (!oldWheelSerial2.equals(heat.getWheelSerial2())) {
          modifyWheel(oldWheelSerial2, 2);
          modifyNewWheel(heat, 2);
        }
      } else {
        if (heat.getHiHeatInDate() == null || heat.getHiHeatInTime() == null) {
          //高进时间或高进日期有一个为空
          setEmptyHeat("Hi_Heat_In", old, heat);
        } else { //高进日期和高进时间都不为空
          if (heat.getHeatLine() == null) {
            throw HEAT_LINE_CAN_NOT_EMPTY.getPlatformException();
          }
          if (!ObjectUtils.isNotEmpty(heat.getCutId())) {
            throw HEAT_CUT_ID_CAN_NOT_EMPTY.getPlatformException();
          }
          if (!ObjectUtils.isNotEmpty(heat.getHiHeatInOperator())) {
            throw HEAT_IN_OPERATOR_CAN_NOT_EMPTY.getPlatformException();
          }
          if (!ObjectUtils.isNotEmpty(heat.getHiHeatInId())) {
            throw HEAT_IN_ID_CAN_NOT_EMPTY.getPlatformException();
          }
          if (heat.getHiHeatInShift() == null) {
            throw HEAT_IN_SHIFT_CAN_NOT_EMPTY.getPlatformException();
          }

          if (heat.getHiHeatOutDate() == null || heat.getHiHeatOutTime() == null) {
            //高出时间或高出日期有一个为空
            setEmptyHeat("Hi_Heat_Out", old, heat);
          } else { //高出时间和高出日期都不为空
            if (!ObjectUtils.isNotEmpty(heat.getHiHeatOutOperator())) {
              throw HEAT_OUT_OPERATOR_CAN_NOT_EMPTY.getPlatformException();
            }
            if (!ObjectUtils.isNotEmpty(heat.getHiHeatOutId())) {
              throw HEAT_OUT_ID_CAN_NOT_EMPTY.getPlatformException();
            }
            if (heat.getHiHeatOutShift() == null) {
              throw HEAT_OUT_SHIFT_CAN_NOT_EMPTY.getPlatformException();
            }
            if (heat.getLowHeatInDate() == null || heat.getLowHeatInTime() == null) {
              //低进时间或低进日期有一个为空
              setEmptyHeat("Low_Heat_In", old, heat);
            } else { //低进时间或低进日期都不为空
              if (!ObjectUtils.isNotEmpty(heat.getLowHeatInOperator())) {
                throw HEAT_IN_OPERATOR_CAN_NOT_EMPTY.getPlatformException();
              }
              if (!ObjectUtils.isNotEmpty(heat.getLowHeatInId())) {
                throw HEAT_IN_ID_CAN_NOT_EMPTY.getPlatformException();
              }
              if (heat.getLowHeatInShift() == null) {
                throw HEAT_IN_SHIFT_CAN_NOT_EMPTY.getPlatformException();
              }

              if (heat.getLowHeatOutDate() == null || heat.getLowHeatOutTime() == null) {
                // 低出时间或低出日期有一个为空
                setEmptyHeat("Low_Heat_Out", old, heat);
              } else { //低出时间或低出日期都不为空
                if (!ObjectUtils.isNotEmpty(heat.getLowHeatOutOperator())) {
                  throw HEAT_OUT_OPERATOR_CAN_NOT_EMPTY.getPlatformException();
                }
                if (!ObjectUtils.isNotEmpty(heat.getLowHeatOutId())) {
                  throw HEAT_OUT_ID_CAN_NOT_EMPTY.getPlatformException();
                }
                if (heat.getLowHeatOutShift() == null) {
                  throw HEAT_OUT_SHIFT_CAN_NOT_EMPTY.getPlatformException();
                }
                setEmptyHeat("None", old, heat);
              }
            }
          }
        }
        heatRepository.save(old);
      }
  }

  private void modifyNewWheel(Heat heat, int index) {
    wheelRecordService.findByWheelSerial(index == 1 ? heat.getWheelSerial1() : heat.getWheelSerial2()).ifPresent(wheelRecord -> {
      if (wheelRecord.getFinished() == 0 && wheelRecord.getConfirmedScrap() == 0) {
        wheelRecord.setHeatId(heat.getId());
        log.info("update mecSerial: {}, wheelSerial: {}", heat.getMecSerial(), wheelRecord.getWheelSerial());
        wheelRecord.setMecSerial(heat.getMecSerial());
        if (index == 1) {
          if (StringUtils.isNotBlank(heat.getScrapCode1()) && wheelRecord.getWheelSerial().equals(heat.getWheelSerial1())) {
            wheelRecord.setScrapCode(heat.getScrapCode1());
          }
        }
        if (index == 2) {
          if (StringUtils.isNotBlank(heat.getScrapCode1()) && wheelRecord.getWheelSerial().equals(heat.getWheelSerial2())) {
            wheelRecord.setScrapCode(heat.getScrapCode2());
          }
        }
        wheelRecordService.save(wheelRecord);
      }
    });
  }

  private void modifyWheel(String wheelSerial, int index) {
    WheelRecord wheelRecord = wheelRecordService.findWheelWithoutThrowException(wheelSerial);
    if (wheelRecord != null) {
      Heat maxHeat = heatRepository.findMaxHeat(wheelSerial);
      PourRecord pourRecord = pourRecordRepository.findByWheelSerial(wheelSerial);
      if (maxHeat != null) {
        if (wheelRecord.getFinished() != 1 && wheelRecord.getConfirmedScrap() != 1) { //非成品&&非确废的情况下才进行wheelRecord更新
          wheelRecord.setHeatId(maxHeat.getId());
          wheelRecord.setHeatCode(index == 1 ? maxHeat.getHeatCode1() : maxHeat.getHeatCode2());
          log.info("update mecSerial: {}, wheelSerial: {}", maxHeat.getMecSerial(), wheelRecord.getWheelSerial());
          wheelRecord.setMecSerial(maxHeat.getMecSerial());

          if (pourRecord.getInPitDateTime() != null) {
            Date dt;
            if (maxHeat.getXh() == 0 || maxHeat.getXh() == 1) { //高温炉未刷新前，以热处理记录生成时间作为判断标准
              dt = maxHeat.getCreateDateTime();
            } else { //高温炉刷新后， 以进环时间作为判断标准
              dt = parseTime(dateStr(maxHeat.getHiHeatInDate()) + " " + timeStr(maxHeat.getHiHeatInTime()));
            }
            Date OutPitDTCal = DateUtils.addHours(pourRecord.getInPitDateTime(), 12);
            if (dt.getTime() - OutPitDTCal.getTime() < 0) {
              wheelRecord.setScrapCode("HLD");
            }
          } else {
            wheelRecord.setScrapCode("HLD");
          }
        }
      } else {
        wheelRecord.setHeatId(null);
        wheelRecord.setHeatCode("");
        wheelRecord.setMecSerial(null);
        if (wheelRecord.getScrapCode().equals("HLD")) {
          wheelRecord.setScrapCode("");
        }
      }
      wheelRecordService.save(wheelRecord);
    }
  }

  public Integer addPrework(LowHeatPreworkRecord lowHeatPreworkRecord) {
    lowHeatPreworkRecord.setCreateTime(new Date());
    return lowHeatPreworkRecordRepository.save(lowHeatPreworkRecord).getId();
  }

  public Page<LowHeatPreworkRecord> getPrework(Integer currentPage, Integer pageSize) {
    Specification<LowHeatPreworkRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return lowHeatPreworkRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public void editPrework(Integer id, LowHeatPreworkRecord lowHeatPreworkRecord) {
    lowHeatPreworkRecordRepository.findById(id).ifPresent(old -> {
      lowHeatPreworkRecord.setId(old.getId());
      lowHeatPreworkRecord.setCreateTime(old.getCreateTime());
      lowHeatPreworkRecordRepository.save(lowHeatPreworkRecord);
    });
  }
}
