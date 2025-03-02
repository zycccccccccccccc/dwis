package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.BALANCE_FLAG_CAN_NOT_EMPTY;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.BORE_SIZE_CAN_NOT_EMPTY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.DesignRepository;
import tech.hciot.dwis.business.domain.TransportRecordRepository;
import tech.hciot.dwis.business.domain.model.TransportRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.TransportRecordResponse;

@Service
public class TransportRecordService {

  private static final int OPE_TYPE_X_RAY = 81;
  private static final int OPE_TYPE_DE_WEIGHT = 82;
  private static final int OPE_TYPE_BORE = 203;

  @Autowired
  private TransportRecordRepository transportRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private DesignService designService;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private DesignRepository designRepository;

  @Autowired
  private BalanceService balanceService;

  public Page<TransportRecordResponse> find(String wheelSerial, String design, String inspectorId, Integer opeType,
      Integer currentPage, Integer pageSize) {
    Specification<TransportRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(design)) {
        list.add(criteriaBuilder.equal(root.get("design"), design));
      }
      if (StringUtils.isNotEmpty(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (opeType != null) {
        list.add(criteriaBuilder.equal(root.get("opeType"), opeType));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return transportRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize))
        .map(transportRecord -> TransportRecordResponse.builder().wheelSerial(transportRecord.getWheelSerial())
            .design(transportRecord.getDesign()).boreSize(transportRecord.getBoreSize()).tapeSize(transportRecord.getWheelRecord()
                .getTapeSize()).brinnelReading(transportRecord.getWheelRecord().getBrinnelReading())
            .wheelW(transportRecord.getWheelRecord().getWheelW()).balanceV(transportRecord.getWheelRecord().getBalanceV())
            .balanceA(transportRecord.getWheelRecord().getBalanceA()).opeDT(transportRecord.getOpeDT())
            .build());
  }

  public List<String> findXrayWheelSerialList(String keyword, Integer limit) {
    return wheelRecordService.findXRayTransportWheelSerialList(keyword, limit);
  }

  public List<String> findDeWeightWheelSerialList(String keyword, Integer limit) {
    return wheelRecordService.findDeWeightWheelSerialList(keyword, limit);
  }

  public List<String> findBoreWheelSerialList(String keyword, Integer limit) {
    return wheelRecordService.findBoreWheelSerialList(keyword, limit);
  }

  @Transactional
  public WheelRecord add(TransportRecord transportRecord) {
    WheelRecord wheelRecord = wheelRecordService.findWheel(transportRecord.getWheelSerial());
    if (transportRecord.getOpeType() == OPE_TYPE_X_RAY || transportRecord.getOpeType() == OPE_TYPE_DE_WEIGHT) {
      if (designService.isInternal(wheelRecord.getDesign()) && StringUtils.isBlank(transportRecord.getBalanceS())) {
        throw BALANCE_FLAG_CAN_NOT_EMPTY.getPlatformException();
      }
    } else {
      if (StringUtils.isBlank(transportRecord.getBalanceS())) {
        throw BALANCE_FLAG_CAN_NOT_EMPTY.getPlatformException();
      }
      if (StringUtils.isBlank(transportRecord.getBoreSize())) {
        throw BORE_SIZE_CAN_NOT_EMPTY.getPlatformException();
      }
    }

    transportRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    transportRecord.setCreateTime(new Date());
    Integer maxTimes =
        transportRecordRepository.findMaxTransportTimes(transportRecord.getWheelSerial(), transportRecord.getOpeType())
            .orElse(new BigDecimal(0)).intValue() + 1;
    transportRecord.setTs(maxTimes);
    return updateWheel(transportRecordRepository.save(transportRecord), wheelRecord);
  }

  private WheelRecord updateWheel(TransportRecord transportRecord, WheelRecord wheelRecord) {
    Integer result = 0;
    switch (transportRecord.getOpeType()) {
      case OPE_TYPE_X_RAY:
        result = updateXray(transportRecord, wheelRecord);
        break;
      case OPE_TYPE_DE_WEIGHT:
        result = updateDeWeight(transportRecord, wheelRecord);
        break;
      case OPE_TYPE_BORE:
        result = updateBore(transportRecord, wheelRecord);
        break;
      default:
    }
    if (result == 1) {
      wheelRecordService.save(wheelRecord);
      wheelRecord.setInternal(
          designRepository.findByDesign(wheelRecord.getDesign()).map(design -> design.getInternal()).orElse(null));
      if (wheelRecord.getInternal() == 1) {
        wheelRecord.setDataMatrix(balanceService.generateInternalDataMatrix(wheelRecord));
      } else if (wheelRecord.getInternal() == 0) {
        wheelRecord.setDataMatrix(balanceService.generateExternalDataMatrix(wheelRecord));
      }
      /*if (wheelRecord.getFinished() == 1) {
        if (wheelRecord.getDesign().equals("IR33") || wheelRecord.getDesign().equals("CJ33") || wheelRecord.getDesign()
            .equals("CG33") || wheelRecord.getDesign().equals("PAK950") || wheelRecord.getDesign().equals("GEZ")) {
          wheelRecord.setDataMatrix(balanceService.generateExternalDataMatrix(wheelRecord));
        } else if (wheelRecord.getInternal() == 1 && ((wheelRecord.getWheelW() < 137
            || wheelRecord.getTapeSize().doubleValue() < 840)
            || wheelRecord.getWheelW() == 137 && wheelRecord.getTapeSize().doubleValue() >= 840)) {
          wheelRecord.setDataMatrix(balanceService.generateInternalDataMatrix(wheelRecord));
        }
      }*/
      return wheelRecord;
    }
    return null;
  }

  private Integer updateBore(TransportRecord transportRecord, WheelRecord wheelRecord) {
    Integer finished = 0;
    wheelRecord.setBalanceS(transportRecord.getBalanceS());
    wheelRecord.setReworkCode("");
    wheelRecord.setBoreSize(Integer.valueOf(transportRecord.getBoreSize()));
    if (wheelRecord.getWheelW() != null && wheelRecord.getBoreSize() != null && wheelRecord.getTapeSize() != null
        && wheelRecord.getXrayReq() == 0 && wheelRecord.getBrinReq() == 0 && wheelRecord.getSpecialMt() == 0 && isCihenOk(
        wheelRecord) && isDesignOkBore(wheelRecord)) {
      finished = 1;
    }
    if (finished == 1) {
      wheelRecord.setFinished(1);
      if (wheelRecord.getBalanceS().equals("E3") && wheelRecord.getBalance() == 1) {
        wheelRecord.setBalanceFlag(2);
      }
      wheelRecord.setKFinishedId(transportRecord.getId());
      wheelRecord.setBarcode(wheelRecord.getBarcode() + 1);
      wheelRecord.setLastBarcode(transportRecord.getOpeDT());
      wheelRecord.setFinishPrint(wheelRecord.getFinished());
    }
    return finished;
  }

  private Integer updateDeWeight(TransportRecord transportRecord, WheelRecord wheelRecord) {
    Integer finished = 0;
    wheelRecord.setBalanceS(transportRecord.getBalanceS());
    wheelRecord.setHoldCode("");
    if (wheelRecord.getWheelW() != null && wheelRecord.getBoreSize() != null && wheelRecord.getTapeSize() != null
        && wheelRecord.getXrayReq() == 0 && wheelRecord.getBrinReq() == 0 && wheelRecord.getSpecialMt() == 0 && isCihenOk(
        wheelRecord) && isDesignOkDeWeight(wheelRecord)) {
      finished = 1;
    }
    if (finished == 1) {
      wheelRecord.setFinished(1);
      if (wheelRecord.getBalanceS().equals("E3") && wheelRecord.getBalance() == 1) {
        wheelRecord.setBalanceFlag(2);
      }
      wheelRecord.setReWeightId(transportRecord.getId());
      wheelRecord.setBarcode(wheelRecord.getBarcode() + 1);
      wheelRecord.setLastBarcode(transportRecord.getOpeDT());
      wheelRecord.setFinishPrint(wheelRecord.getFinished());
    }
    return finished;
  }

  private Integer updateXray(TransportRecord transportRecord, WheelRecord wheelRecord) {
    Integer finished = 0;
    wheelRecord.setBalanceS(transportRecord.getBalanceS());
    if (wheelRecord.getWheelW() != null && wheelRecord.getBoreSize() != null && wheelRecord.getTapeSize() != null
        && wheelRecord.getXrayReq() == 0 && wheelRecord.getBrinReq() == 0 && wheelRecord.getSpecialMt() == 0 && StringUtils
        .isAllBlank(wheelRecord.getReworkCode(), wheelRecord.getScrapCode()) && isCihenOk(wheelRecord) && isDesignOk(
        wheelRecord)) {
      finished = 1;
    }
    if (finished == 1) {
      wheelRecord.setFinished(1);
      if (wheelRecord.getBalanceS().equals("E3") && wheelRecord.getBalance() == 1) {
        wheelRecord.setBalanceFlag(2);
      }
      wheelRecord.setXFinishedId(transportRecord.getId());
      wheelRecord.setBarcode(wheelRecord.getBarcode() + 1);
      wheelRecord.setLastBarcode(transportRecord.getOpeDT());
      wheelRecord.setFinishPrint(wheelRecord.getFinished());
    }
    return finished;
  }

  private boolean isCihenOk(WheelRecord wheelRecord) {
    boolean result = false;
    if (wheelRecord.getDesign().equals("CJ33") || wheelRecord.getDesign().equals("CP33")) {
      if (wheelRecord.getCihenCode().equals("OK")) {
        result = true;
      }
    } else {
      result = true;
    }
    return result;
  }

  private boolean isDesignOk(WheelRecord wheelRecord) {
    boolean result = false;
    if (designService.isInternal(wheelRecord.getDesign())) {
      if (wheelRecord.getBalanceV() <= 125 && wheelRecord.getBalanceS().equals("E3")) {
        result = true;
      }
    } else {
      result = true;
    }
    return result;
  }

  private boolean isDesignOkDeWeight(WheelRecord wheelRecord) {
    boolean result = false;
    if (designService.isInternal(wheelRecord.getDesign())) { //国内车轮
      if (wheelRecord.getDesign().equals("HFZ915")) { //HFZ915轮型
        if (wheelRecord.getBalanceV() >= 125 && wheelRecord.getBalanceV() <= 240 && wheelRecord.getBalanceS().equals("E3")) {
          result = true;
        }
      } else { //其他国内轮型
        if (wheelRecord.getBalanceV() >= 125 && wheelRecord.getBalanceV() <= 220 && wheelRecord.getBalanceS().equals("E3")) {
          result = true;
        }
      }
    } else { //不是国内轮
      result = true;
    }
    return result;
  }

  private boolean isDesignOkBore(WheelRecord wheelRecord) {
    boolean result = false;
    if (designService.isInternal(wheelRecord.getDesign())) {
      if (wheelRecord.getBalanceV() > 0 && wheelRecord.getBalanceV() <= 220 && wheelRecord.getBalanceS().equals("E3")) {
        result = true;
      }
    } else {
      result = true;
    }
    return result;
  }
}
