package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CORRECT_WHEEL_PARAM_CAN_NOT_ALL_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CORRECT_WHEEL_RETURN_PARAM_CAN_NOT_ALL_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CORRECT_WHEEL_SCRAP_CODE_CAN_NOT_EMPTY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.business.domain.CorrectWheelRecordRepository;
import tech.hciot.dwis.business.domain.OperatingTimeCtrRepository;
import tech.hciot.dwis.business.domain.model.CorrectWheelRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
public class CorrectWheelRecordService {

  public static final int RECALL_TYPE_FINISH = 1;
  public static final int RECALL_TYPE_STOCK = 2;
  public static final int RECALL_TYPE_RETURN = 3;

  @Autowired
  private CorrectWheelRecordRepository correctWheelRecordRepository;

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  public Page<CorrectWheelRecord> find(String inspectorId, String wheelSerial, String holdCode, String reworkCode,
      String scrapCode, String cihenCode, String formerStockDate, String formerShippedNo, Integer recallType,
      Integer currentPage, Integer pageSize) {
    Specification<CorrectWheelRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(holdCode)) {
        list.add(criteriaBuilder.equal(root.get("holdCode"), holdCode));
      }
      if (StringUtils.isNotEmpty(reworkCode)) {
        list.add(criteriaBuilder.equal(root.get("reworkCode"), reworkCode));
      }
      if (StringUtils.isNotEmpty(scrapCode)) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (StringUtils.isNotEmpty(cihenCode)) {
        list.add(criteriaBuilder.equal(root.get("cihenCode"), cihenCode));
      }
      if (StringUtils.isNotEmpty(formerStockDate)) {
        Date date = parseDate(formerStockDate);
        list.add(criteriaBuilder.equal(root.get("formerStockDate"), date));
      }
      if (StringUtils.isNotEmpty(formerShippedNo)) {
        list.add(criteriaBuilder.equal(root.get("formerShippedNo"), formerShippedNo));
      }
      if (recallType != null) {
        list.add(criteriaBuilder.equal(root.get("recallType"), recallType));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return correctWheelRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void finish(CorrectWheelRecord correctWheelRecord) {
    if (StringUtils.isAllEmpty(correctWheelRecord.getHoldCode(), correctWheelRecord.getReworkCode(),
        correctWheelRecord.getScrapCode(), correctWheelRecord.getCihenCode())) {
      throw CORRECT_WHEEL_PARAM_CAN_NOT_ALL_EMPTY.getPlatformException();
    }
    correctWheelRecord.setRecallType(RECALL_TYPE_FINISH);
    CorrectWheelRecord saved = save(correctWheelRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(correctWheelRecord.getWheelSerial());
    wheelRecord.setHoldCode(correctWheelRecord.getHoldCode());
    wheelRecord.setScrapCode(correctWheelRecord.getScrapCode());
    wheelRecord.setReworkCode(correctWheelRecord.getReworkCode());
    wheelRecord.setCihenCode(correctWheelRecord.getCihenCode());
    wheelRecord.setFinished(0);
	wheelRecord.setXFinishedId(null);
    wheelRecord.setReWeightId(null);
    wheelRecord.setKFinishedId(null);
    wheelRecord.setFinRecallId(saved.getLogId());
    wheelRecordService.save(wheelRecord);
  }

  private CorrectWheelRecord save(CorrectWheelRecord correctWheelRecord) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      correctWheelRecord.setOpeDT(ope);
    });
    correctWheelRecord.setCreateDate(new Date());
    return correctWheelRecordRepository.save(correctWheelRecord);
  }

  @Transactional
  public void stock(CorrectWheelRecord correctWheelRecord) {
    if (StringUtils.isAllEmpty(correctWheelRecord.getHoldCode(), correctWheelRecord.getReworkCode(),
        correctWheelRecord.getScrapCode(), correctWheelRecord.getCihenCode())) {
      throw CORRECT_WHEEL_PARAM_CAN_NOT_ALL_EMPTY.getPlatformException();
    }
    correctWheelRecord.setRecallType(RECALL_TYPE_STOCK);
    CorrectWheelRecord saved = save(correctWheelRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(correctWheelRecord.getWheelSerial());
    wheelRecord.setHoldCode(correctWheelRecord.getHoldCode());
    wheelRecord.setScrapCode(correctWheelRecord.getScrapCode());
    wheelRecord.setReworkCode(correctWheelRecord.getReworkCode());
    wheelRecord.setCihenCode(correctWheelRecord.getCihenCode());
    wheelRecord.setFinished(0);
	wheelRecord.setXFinishedId(null);
    wheelRecord.setReWeightId(null);
    wheelRecord.setKFinishedId(null);
    wheelRecord.setStockRecallId(saved.getLogId());
    wheelRecord.setCheckCode("");
    wheelRecord.setStockDate(null);
    wheelRecordService.save(wheelRecord);
  }

  @Transactional
  public void returnCorrect(CorrectWheelRecord correctWheelRecord) {
    if (correctWheelRecord.getConfirmedScrap() == 1 && StringUtils.isEmpty(correctWheelRecord.getScrapCode())) {
      throw CORRECT_WHEEL_SCRAP_CODE_CAN_NOT_EMPTY.getPlatformException();
    }
    if (StringUtils.isAllEmpty(correctWheelRecord.getReworkCode(), correctWheelRecord.getScrapCode())) {
      throw CORRECT_WHEEL_RETURN_PARAM_CAN_NOT_ALL_EMPTY.getPlatformException();
    }

    correctWheelRecord.setRecallType(RECALL_TYPE_RETURN);
    if (correctWheelRecord.getConfirmedScrap() == 1) {
      correctWheelRecord.setScrapDate(new Date());
    }
    CorrectWheelRecord saved = save(correctWheelRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(correctWheelRecord.getWheelSerial());
    wheelRecord.setScrapCode(correctWheelRecord.getScrapCode());
    wheelRecord.setReworkCode(correctWheelRecord.getReworkCode());
    wheelRecord.setFinished(0);
	wheelRecord.setXFinishedId(null);
    wheelRecord.setReWeightId(null);
    wheelRecord.setKFinishedId(null);
    wheelRecord.setOutRecallId(saved.getLogId());
    wheelRecord.setCheckCode("");
    wheelRecord.setStockDate(null);
    wheelRecord.setShippedNo(null);
    if (correctWheelRecord.getConfirmedScrap() == 1) {
      wheelRecord.setConfirmedScrap(1);
      wheelRecord.setScrapDate(new Date());
    }
    wheelRecordService.save(wheelRecord);
  }
}
