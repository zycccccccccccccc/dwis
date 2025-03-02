package tech.hciot.dwis.business.application;

import static java.util.stream.Collectors.toList;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.SHELF_NUMBER_INVALID;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.START_PREFIX_NOT_EQUAL_END_PREFIX;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.PourRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.InternalBarcodeResponse;
import tech.hciot.dwis.business.interfaces.dto.InternalSerialResponse;
import tech.hciot.dwis.business.interfaces.dto.SouthAfricaBarcodeResponse;
import tech.hciot.dwis.business.interfaces.dto.SouthAfricaSerialResponse;
import tech.hciot.dwis.business.interfaces.dto.UsaBarcodeResponse;
import tech.hciot.dwis.business.interfaces.dto.UsaSerialResponse;

@Service
public class PrintSerialService {

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private DesignService designService;

  @Autowired
  private BalanceService balanceService;

  private static final String SHELF_NUMBER_PATTERN = "^[a-zA-Z]{2}\\d+$";

  public List<InternalSerialResponse> internalSerial(String startSerial, String endSerial) {
    String prefix = getPrefix(startSerial, endSerial);
    int start = Integer.valueOf(startSerial.substring(2));
    int end = Integer.valueOf(endSerial.substring(2));
    List<InternalSerialResponse> internalSerialResponseList = new ArrayList<>();
    for (int i = start; i <= end; i++) {
      InternalSerialResponse internalSerialResponse = getInternalSerialByShelfNumber(prefix + i);
      if (internalSerialResponse != null) {
        internalSerialResponseList.add(internalSerialResponse);
      }
    }
    return internalSerialResponseList;
  }

  private InternalSerialResponse getInternalSerialByShelfNumber(String shelfNumber) {
    List<WheelRecord> wheelRecordList = wheelRecordRepository.findWheelSerialByShelfNumber(shelfNumber);
    if (!wheelRecordList.isEmpty()) {
      InternalSerialResponse internalSerialResponse =
          InternalSerialResponse.builder().shelfNumber(shelfNumber).manufacturer("DCACC").build();

      wheelRecordList.forEach(wheelRecord -> {
        if (StringUtils.isBlank(internalSerialResponse.getDesign())) {
          internalSerialResponse.setDesign(wheelRecord.getDesign());
        }
        if (StringUtils.isBlank(internalSerialResponse.getShippedNo())) {
          internalSerialResponse.setShippedNo(wheelRecord.getShippedNo());
        }
        if (internalSerialResponse.getMaxBoreSize() == null) {
          internalSerialResponse.setMaxBoreSize(wheelRecord.getBoreSize());
        } else {
          if (wheelRecord.getBoreSize() > internalSerialResponse.getMaxBoreSize()) {
            internalSerialResponse.setMaxBoreSize(wheelRecord.getBoreSize());
          }
        }
        if (internalSerialResponse.getMinBoreSize() == null) {
          internalSerialResponse.setMinBoreSize(wheelRecord.getBoreSize());
        } else {
          if (wheelRecord.getBoreSize() < internalSerialResponse.getMinBoreSize()) {
            internalSerialResponse.setMinBoreSize(wheelRecord.getBoreSize());
          }
        }
      });

      StringBuilder sb = new StringBuilder();
      sb.append("<wheel>").append(generateDataMatrixField("idNumber", shelfNumber))
          .append(generateDataMatrixField("C401", "DCACC"))
          .append(generateDataMatrixField("C402", internalSerialResponse.getDesign())).append(generateDataMatrixField("C403",
          internalSerialResponse.getMaxBoreSize())).append(generateDataMatrixField("C404",
          internalSerialResponse.getMinBoreSize()));

      for (int i = 0; i < 5; i++) {
        if (wheelRecordList.size() > i) {
          WheelRecord wheelRecord = wheelRecordList.get(i);
          String[] yearAndMonth = getPourYearAndMonth(wheelRecord.getWheelSerial());
          sb.append(generateDataMatrixField("C" + (405 + (i * 5)), yearAndMonth[0]));
          sb.append(generateDataMatrixField("C" + (406 + (i * 5)), yearAndMonth[1]));
          int wheelSerialLength = wheelRecord.getWheelSerial().length();
          int start = wheelSerialLength - 6;
          sb.append(generateDataMatrixField("C" + (408 + (i * 5)), wheelRecord.getWheelSerial().substring(start)));
          sb.append(generateDataMatrixField("C" + (409 + (i * 5)), wheelRecord.getBoreSize()));
        } else {
          sb.append(generateDataMatrixField("C" + (405 + (i * 5)), ""));
          sb.append(generateDataMatrixField("C" + (406 + (i * 5)), ""));
          sb.append(generateDataMatrixField("C" + (408 + (i * 5)), ""));
          sb.append(generateDataMatrixField("C" + (409 + (i * 5)), ""));
        }
      }

      sb.append("</wheel>");
      internalSerialResponse.setDataMatrix(sb.toString());
      return internalSerialResponse;
    }
    return null;
  }

  private String generateDataMatrixField(String fieldName, Object fieldValue) {
    return "<" + fieldName + ">" + fieldValue + "</" + fieldName + ">";
  }

  private String[] getPourYearAndMonth(String wheelSerial) {
    String[] result = new String[]{"", ""};
    PourRecord pourRecord = pourRecordRepository.findByWheelSerial(wheelSerial);
    if (pourRecord != null) {
      Date pourDate = pourRecord.getPourDT();
      if (pourDate != null) {
        result[0] = DateFormatUtils.format(pourDate, "yy");
        result[1] = DateFormatUtils.format(pourDate, "MM");
      }
    }
    return result;
  }

  public List<UsaSerialResponse> usaSerial(String startSerial, String endSerial, String design, String orderNo,
      String netWeight, String grossWeight, String date) {
    String prefix = getPrefix(startSerial, endSerial);
    int start = Integer.valueOf(startSerial.substring(2));
    int end = Integer.valueOf(endSerial.substring(2));
    List<UsaSerialResponse> usaSerialResponseList = new ArrayList<>();
    for (int i = start; i <= end; i++) {
      UsaSerialResponse usaSerialResponse = getUsaSerialByShelfNumber(prefix + i, design, orderNo, netWeight,
          grossWeight, date);
      if (usaSerialResponse != null) {
        usaSerialResponseList.add(usaSerialResponse);
      }
    }
    return usaSerialResponseList;
  }

  private UsaSerialResponse getUsaSerialByShelfNumber(String shelfNumber, String design, String orderNo,
      String netWeight, String grossWeight, String date) {
    UsaSerialResponse usaSerialResponse = null;
    if (!wheelRecordRepository.findWheelSerialByShelfNumber(shelfNumber).isEmpty()) {
      usaSerialResponse =
          UsaSerialResponse.builder().design(design).orderNo(orderNo).spindleNo(shelfNumber).netWeight(netWeight)
              .grossWeight(grossWeight).date(parseDate(date)).build();
    }
    return usaSerialResponse;
  }

  public List<SouthAfricaSerialResponse> southAfricaSerial(String startSerial, String endSerial, String design, String orderNo,
      String netWeight, String grossWeight, String boreSize) {
    String prefix = getPrefix(startSerial, endSerial);
    int start = Integer.valueOf(startSerial.substring(2));
    int end = Integer.valueOf(endSerial.substring(2));
    List<SouthAfricaSerialResponse> southAfricaSerialResponseList = new ArrayList<>();
    for (int i = start; i <= end; i++) {
      SouthAfricaSerialResponse southAfricaSerialResponse = getSouthAfricaSerialByShelfNumber(prefix + i, design,
          orderNo, netWeight, grossWeight, boreSize);
      if (southAfricaSerialResponse != null) {
        southAfricaSerialResponseList.add(southAfricaSerialResponse);
      }
    }
    return southAfricaSerialResponseList;
  }

  private SouthAfricaSerialResponse getSouthAfricaSerialByShelfNumber(String shelfNumber, String design, String orderNo,
      String netWeight, String grossWeight, String boreSize) {
    SouthAfricaSerialResponse southAfricaSerialResponse = null;
    List<WheelRecord> wheelRecordList = wheelRecordRepository.findWheelSerialByShelfNumber(shelfNumber);
    if (!wheelRecordList.isEmpty()) {
      List<String> wheelSerialList = new ArrayList<>();
      wheelRecordList.forEach(wheelRecord -> wheelSerialList.add(wheelRecord.getWheelSerial()));
      southAfricaSerialResponse =
          SouthAfricaSerialResponse.builder().design(design).orderNo(orderNo).spindleNo(shelfNumber).netWeight(netWeight)
              .grossWeight(grossWeight).boreSize(boreSize).wheelSerial(wheelSerialList).build();
    }
    return southAfricaSerialResponse;
  }

  public List<InternalBarcodeResponse> internalBarcode(String startSerial, String endSerial) {
    String prefix = getPrefix(startSerial, endSerial);
    int start = Integer.valueOf(startSerial.substring(2));
    int end = Integer.valueOf(endSerial.substring(2));
    List<InternalBarcodeResponse> internalBarcodeResponseList = new ArrayList<>();
    for (int i = start; i <= end; i++) {
      internalBarcodeResponseList.addAll(getInternalBarcodeByShelfNumber(prefix + i));
    }
    return internalBarcodeResponseList;
  }

  private List<InternalBarcodeResponse> getInternalBarcodeByShelfNumber(String shelfNumber) {
    List<WheelRecord> wheelRecordList = wheelRecordRepository.findWheelSerialByShelfNumber(shelfNumber);
    return wheelRecordList.stream().filter(wheelRecord -> StringUtils.isNotBlank(wheelRecord.getWheelSerial())).map(wheelRecord ->
        InternalBarcodeResponse.builder().manufacturer("DCACC").lastBarcode(wheelRecord.getLastBarcode()).wheelSerial(
            wheelRecord.getWheelSerial()).wheelW(wheelRecord.getWheelW()).balanceS(wheelRecord.getBalanceS())
            .boreSize(wheelRecord.getBoreSize()).tapeSize(wheelRecord.getTapeSize()).design(wheelRecord.getDesign())
            .dataMatrix(balanceService.generateInternalDataMatrix(wheelRecord))
            .build()
    ).collect(toList());
  }

  public List<UsaBarcodeResponse> usaBarcode(String startSerial, String endSerial) {
    String prefix = getPrefix(startSerial, endSerial);
    int start = Integer.valueOf(startSerial.substring(2));
    int end = Integer.valueOf(endSerial.substring(2));
    List<UsaBarcodeResponse> usaBarcodeResponseList = new ArrayList<>();
    for (int i = start; i <= end; i++) {
      usaBarcodeResponseList.addAll(getUsaBarcodeByShelfNumber(prefix + i));
    }
    return usaBarcodeResponseList;
  }

  private List<UsaBarcodeResponse> getUsaBarcodeByShelfNumber(String shelfNumber) {
    return wheelRecordRepository.findWheelSerialByShelfNumber(shelfNumber).stream().map(wheelRecord -> {
      String steelClass = designService.getDesign(wheelRecord.getDesign()).getSteelClass();
      return UsaBarcodeResponse.builder().steelClass(steelClass).design(wheelRecord.getDesign())
          .wheelSerial(wheelRecord.getWheelSerial()).boreSize(wheelRecord.getBoreSize()).tapeSize(wheelRecord.getTapeSize())
          .lastBarcode(wheelRecord.getLastBarcode()).dataMatrix(balanceService.generateExternalDataMatrix(wheelRecord)).build();
    }).collect(toList());
  }

  public List<SouthAfricaBarcodeResponse> southAfricaBarcode(String startSerial, String endSerial) {
    String prefix = getPrefix(startSerial, endSerial);
    int start = Integer.valueOf(startSerial.substring(2));
    int end = Integer.valueOf(endSerial.substring(2));
    List<SouthAfricaBarcodeResponse> southAfricaBarcodeResponseList = new ArrayList<>();
    for (int i = start; i <= end; i++) {
      southAfricaBarcodeResponseList.addAll(getSouthAfricaBarcodeByShelfNumber(prefix + i));
    }
    return southAfricaBarcodeResponseList;
  }

  private List<SouthAfricaBarcodeResponse> getSouthAfricaBarcodeByShelfNumber(String shelfNumber) {
    return wheelRecordRepository.findWheelSerialByShelfNumber(shelfNumber).stream().map(wheelRecord ->
        SouthAfricaBarcodeResponse.builder().Xh(wheelRecord.getXh()).design(wheelRecord.getDesign())
            .wheelSerial(wheelRecord.getWheelSerial()).boreSize(wheelRecord.getBoreSize()).tapeSize(wheelRecord.getTapeSize())
            .lastBarcode(wheelRecord.getLastBarcode()).wheelW(wheelRecord.getWheelW()).brinnelReading(
            wheelRecord.getBrinnelReading()).shelfNumber(shelfNumber).build()).collect(toList());
  }

  private String getPrefix(String startSerial, String endSerial) {
    if (!startSerial.matches(SHELF_NUMBER_PATTERN) || !endSerial.matches(SHELF_NUMBER_PATTERN)) {
      throw SHELF_NUMBER_INVALID.getPlatformException();
    }
    String prefix = startSerial.substring(0, 2);
    if (!prefix.equals(endSerial.substring(0, 2))) {
      throw START_PREFIX_NOT_EQUAL_END_PREFIX.getPlatformException();
    }
    return prefix;
  }
}
