package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.BalanceRecord;
import tech.hciot.dwis.business.domain.model.BarcodePrintRecord;
import tech.hciot.dwis.business.domain.model.Design;
import tech.hciot.dwis.business.domain.model.HeatRecord;
import tech.hciot.dwis.business.domain.model.LadleRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.BalanceResponse;

@Service
@Slf4j
public class BalanceService {

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  @Autowired
  private BalanceRecordRepository balanceRecordRepository;

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private DesignRepository designRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private BasicDataService basicDataService;

  @Autowired
  private BarcodePrintRecordRepository barcodePrintRecordRepository;

  @Autowired
  private ChemistryDetailRepository chemistryDetailRepository;

  @Autowired
  private LadleRecordRepository ladleRecordRepository;

  @Autowired
  private HeatRecordRepository heatRecordRepository;

  @Autowired
  private TMachineRecordRepository tMachineRecordRepository;

  @Autowired
  private FinalCheckRecordRepository finalCheckRecordRepository;

  public Page<BalanceRecord> find(String wheelSerial,
      String holdCode,
      String reworkCode,
      String scrapCode,
      String balanceS,
      String inspectorId,
      Integer currentPage,
      Integer pageSize) {
    Specification<BalanceRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (wheelSerial != null) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (holdCode != null) {
        list.add(criteriaBuilder.equal(root.get("holdCode"), holdCode));
      }
      if (reworkCode != null) {
        list.add(criteriaBuilder.equal(root.get("reworkCode"), reworkCode));
      }
      if (scrapCode != null) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (balanceS != null) {
        list.add(criteriaBuilder.equal(root.get("balanceS"), balanceS));
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
    return balanceRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  /**
   * 获取车轮信息
   *
   * @param wheelSerial
   * @return
   */
  public BalanceResponse findWheel(String wheelSerial) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("pre", true);
    parameterMap.put("finalCount", true);
    parameterMap.put("ultra", true);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(wheelSerial, parameterMap)
        .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    BalanceResponse balanceRecord = BalanceResponse.builder().build();
    BeanUtil.copyProperties(wheelRecord, balanceRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

    Design design = designRepository.findByDesign(wheelRecord.getDesign())
        .orElseThrow(() -> PlatformException.badRequestException("轮型不存在"));
    balanceRecord.setSteelClass(design.getSteelClass());

    //判断是否弹框“不打磨车轮”
    if (Objects.equals(wheelRecord.getNGrind(), "NG")) {
      balanceRecord.setTip_NG(true);
    }

    // 判断是否提示“圆度复检”
    // 车轮带尺小于845 && 第一次踏面加工记录出现t_s2=51的时间要早于第一次终检记录时间
    Date firstTTime = tMachineRecordRepository.findFirst51RecordTime(balanceRecord.getWheelSerial(), 51);
    if (firstTTime != null) {
      Date firstFTime = finalCheckRecordRepository.findFirstRecordTime(balanceRecord.getWheelSerial());
      if (firstFTime != null) {
        if (firstFTime.after(firstTTime) && balanceRecord.getTapeSize().compareTo(BigDecimal.valueOf(845)) < 0) {
          balanceRecord.setTip_RoundnessCheck(true);
        }
      }
    }

    // 根据车轮序列号获取轮型，如果轮型等于CJ33，则查询Wheel_Record表的Ladle_id，
    // 根据Ladle_id查询 chemistry_detail化学成分表，获得该轮的各项化学成分值，
    // 如果P大于等于0.025或S大于等于0.03，弹出提示框：“BNSF不发运！”；
    // 如果Al小于0.025，弹出提示框：“单独作外观检查！”
    if ("CJ33".equals(design.getDesign())) {
      Integer ladleId = wheelRecord.getLadleId();
      if (ladleId != null) {
        chemistryDetailRepository.findByLadleId(ladleId).ifPresent(chemistryDetail -> {
          if ((chemistryDetail.getP() != null && (chemistryDetail.getP().doubleValue() >= 0.025))
              || (chemistryDetail.getS() != null && (chemistryDetail.getS().doubleValue() >= 0.03))) {
            balanceRecord.setTip_BNSF(true);
          }
          if ((chemistryDetail.getAl() != null && (chemistryDetail.getAl().doubleValue() < 0.025))) {
            balanceRecord.setTip_exterior_insp(true);
          }
        });
      }
    }

    balanceRecord.setInternal(getDesignInternal(balanceRecord.getDesign()));
    return balanceRecord;
  }

  private String getDataMatrix(WheelRecord wheelRecord) {
    if (isInternalWheel(wheelRecord)) {
      return generateInternalDataMatrix(wheelRecord);
    } else {
      return generateExternalDataMatrix(wheelRecord);
    }
  }

  // 是否是国内轮
  private boolean isInternalWheel(WheelRecord wheelRecord) {
    Design design = designRepository.findByDesign(wheelRecord.getDesign())
        .orElseThrow(() -> PlatformException.badRequestException("轮型不存在"));
    return design.getInternal() == Design.IS_INTERNAL_YES;
  }

  // 国内轮二维码
  public String generateInternalDataMatrix(WheelRecord wheelRecord) {
    Design design = designRepository.findByDesign(wheelRecord.getDesign())
        .orElseThrow(() -> PlatformException.badRequestException("轮型不存在"));
    StringBuilder code = new StringBuilder();
    code.append(generateDataMatrixField("idNumber", wheelRecord.getWheelSerial()));
    code.append(generateDataMatrixField("C103", "DCACC"));
    code.append(generateDataMatrixField("C104",
        wheelRecord.getLastBarcode() == null ? "" : dateFormat.format(wheelRecord.getLastBarcode())));
    code.append(generateDataMatrixField("C105", wheelRecord.getWheelSerial().substring(0, 2)));
    code.append(generateDataMatrixField("C106", wheelRecord.getWheelSerial().substring(2, 4)));
    code.append(generateDataMatrixField("C107", "CO"));
    code.append(generateDataMatrixField("C108", design.getSteelClass()));
    code.append(generateDataMatrixField("C109", wheelRecord.getWheelW()));
    code.append(generateDataMatrixField("C110", ""));
    code.append(generateDataMatrixField("C111", wheelRecord.getWheelSerial().substring(4)));
    code.append(generateDataMatrixField("C112", generateC112(wheelRecord)));
    code.append(generateDataMatrixField("C113", ""));
    code.append(generateDataMatrixField("C114", ""));
    code.append(generateDataMatrixField("C115", "CP"));
    code.append(generateDataMatrixField("C116", wheelRecord.getTapeSize().doubleValue()));
    code.append(generateDataMatrixField("C117", ""));
    code.append(generateDataMatrixField("C118", ""));
    code.append(generateDataMatrixField("C119", ""));
    code.append(generateDataMatrixField("C120", ""));
    code.append(generateDataMatrixField("C121", "20" + wheelRecord.getWheelSerial().substring(0, 2)));
    code.append(generateDataMatrixField("C122", "0"));
    code.append(generateDataMatrixField("C123", wheelRecord.getDesign().substring(3)));
    code.append(generateDataMatrixField("C126", generateC126(wheelRecord.getWheelSerial())));
    code.append(generateDataMatrixField("C130", "01"));
    code.append(generateDataMatrixField("C131", "M"));
    code.append(generateDataMatrixField("C133", wheelRecord.getBoreSize()));
    return "<wheel>" + code + "</wheel>";
  }

  // 国外轮二维码
  public String generateExternalDataMatrix(WheelRecord wheelRecord) {
    Design design = designRepository.findByDesign(wheelRecord.getDesign())
        .orElseThrow(() -> PlatformException.badRequestException("轮型不存在"));
    StringBuilder code = new StringBuilder();
    code.append(generateDataMatrixField("companyCode", "8AMS"));
    code.append(generateDataMatrixField("idNumber", generateExternalIdNumber(wheelRecord.getWheelSerial())));
    code.append(generateDataMatrixField("C103", "ABCD"));
    code.append(generateDataMatrixField("C104",
        wheelRecord.getLastBarcode() == null ? "" : dateFormat.format(wheelRecord.getLastBarcode())));
    code.append(generateDataMatrixField("C105", wheelRecord.getWheelSerial().substring(0, 2)));
    code.append(generateDataMatrixField("C106", wheelRecord.getWheelSerial().substring(2, 4)));
    code.append(generateDataMatrixField("C107", "CO"));
    code.append(generateDataMatrixField("C108", design.getSteelClass()));
    code.append(generateDataMatrixField("C109", "24"));
    code.append(generateDataMatrixField("C110", "0"));
    code.append(generateDataMatrixField("C111", wheelRecord.getWheelSerial().substring(4)));
    code.append(generateDataMatrixField("C112", generateC112(wheelRecord)));
    code.append(generateDataMatrixField("C113", ""));
    code.append(generateDataMatrixField("C114", wheelRecord.getDesign()));
    code.append(generateDataMatrixField("C115", "CP"));
    code.append(generateDataMatrixField("C116", wheelRecord.getTapeSize().intValue()));
    code.append(generateDataMatrixField("C117", "N"));
    code.append(generateDataMatrixField("C118", ""));
    code.append(generateDataMatrixField("C119", ""));
    code.append(generateDataMatrixField("C120", "7.875"));
    return "<wheel>" + code + "</wheel>";
  }

  private String generateC112(WheelRecord balanceRecord) {
    LadleRecord ladleRecord = ladleRecordRepository.findById(balanceRecord.getLadleId())
        .orElseThrow(() -> PlatformException.badRequestException("小包信息不存在"));
    HeatRecord heatRecord = heatRecordRepository.findById(ladleRecord.getHeatRecordId())
        .orElseThrow(() -> PlatformException.badRequestException("炉信息不存在"));
    return DateFormatUtils.format(heatRecord.getCastDate(), "yyyy").substring(2, 4)
        + heatRecord.getFurnaceNo()
        + String.format("%04d", heatRecord.getHeatSeq())
        + ladleRecord.getLadleSeq();
  }

  private String generateC126(String wheelSerial) {
    return "20" + wheelSerial.substring(0, 2)
        + "-" + wheelSerial.substring(2, 4)
        + "-" + wheelSerial.substring(5, 7);
  }

  private String generateExternalIdNumber(String wheelSerial) {
    return "8" + wheelSerial.substring(2, 4) + wheelSerial.substring(0, 2) + wheelSerial.substring(5);
  }

  private String generateDataMatrixField(String fieldName, Object fieldValue) {
    return "<" + fieldName + ">" + fieldValue + "</" + fieldName + ">";
  }

  /**
   * 添加平衡机记录 平衡机业务流程： 1 下拉框选择轮号 2 后台返回车轮详情 3 提交平衡机记录，同时更新车轮表和打印记录表 4 返回车轮的finished状态 5 界面打印车轮信息
   *
   * @param balanceRecord
   * @return
   */
  public BalanceResponse add(BalanceRecord balanceRecord) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("pre", true);
    parameterMap.put("finalCount", true);
    parameterMap.put("ultra", true);
    parameterMap.put("finished", false);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(balanceRecord.getWheelSerial(), parameterMap)
        .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));
    if (balanceRecord.getBalanceV() != null && balanceRecord.getBalanceV() > 0) {
    }
    // 如果不平衡角度小于等于285，则不平衡角度等于285-不平衡角度
    // 如果不平衡角度大于285，则不平衡角度等于645-不平衡角度
    if (balanceRecord.getBalanceA() != null) {
      balanceRecord.setBalanceA(balanceRecord.getBalanceA() <= 285 ?
          285 - balanceRecord.getBalanceA() : 645 - balanceRecord.getBalanceA());
    }
    balanceRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    updateMaxBarcodeTimes(balanceRecord);
    balanceRecord.setCreateTime(new Date());
    balanceRecordRepository.save(balanceRecord);
    updateWheelRecord(balanceRecord, wheelRecord);
    saveBarcodePrintRecord(balanceRecord, wheelRecord);
    String dataMatrix = getDataMatrix(wheelRecord);
    balanceRecord.setDataMatrix(dataMatrix);
    balanceRecord.setFinished(wheelRecord.getFinished());
    BalanceResponse balanceResponse = BalanceResponse.builder().build();
    BeanUtil.copyProperties(wheelRecord, balanceResponse, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    BeanUtil.copyProperties(balanceRecord, balanceResponse, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    return balanceResponse;
  }

  // 级联更新Wheel_Record表
  private void updateWheelRecord(BalanceRecord balanceRecord,
      WheelRecord wheelRecord) {
    wheelRecord.setBalanceId(balanceRecord.getId());
    wheelRecord.setBalance(balanceRecord.getTs());
    wheelRecord.setLastBalance(balanceRecord.getOpeDT());
    wheelRecord.setScrapCode(balanceRecord.getScrapCode());
    wheelRecord.setReworkCode(balanceRecord.getReworkCode());
    wheelRecord.setHoldCode(balanceRecord.getHoldCode());
    wheelRecord.setBalanceS(balanceRecord.getBalanceS());
    wheelRecord.setBalanceA(balanceRecord.getBalanceA());
    wheelRecord.setBalanceV(balanceRecord.getBalanceV());
    wheelRecord.setSpecialMt(balanceRecord.getSpecialMt());
    if (balanceRecord.getTs() == 1 && "E3".equals(balanceRecord.getBalanceS())) {
      wheelRecord.setBalanceFlag(1);
    }
    updateFinishedStatus(balanceRecord, wheelRecord);
    wheelRecord.setFinishPrint(wheelRecord.getFinished());
    wheelRecordRepository.save(wheelRecord);
  }

  private void updateFinishedStatus(BalanceRecord balanceRecord, WheelRecord wheelRecord) {
    log.info("checking finished status, wheelSerial: {}", wheelRecord.getWheelSerial());

    if (ObjectUtil.isEmpty(wheelRecord.getWheelW())) {
      log.info("wheelW is empty");
      return;
    }
    if (ObjectUtil.isEmpty(wheelRecord.getBoreSize())) {
      log.info("boreSize is empty");
      return;
    }
    if (ObjectUtil.isEmpty(wheelRecord.getTapeSize())) {
      log.info("tapeSize is empty");
      return;
    }
    if (wheelRecord.getXrayReq() != 0) {
      log.info("xrayReq != 0");
      return;
    }
    if (wheelRecord.getBrinReq() != 0) {
      log.info("brinReq != 0");
      return;
    }
    if (!checkCihenCode(wheelRecord)) {
      log.info("cihenCode check failed");
      return;
    }
    if (!isHoldCodeEmptyOrRelease(balanceRecord.getHoldCode())) {
      log.info("holdCode is not empty, and type is not release");
      return;
    }
    if (!isTestCodeEmptyOrRelease(balanceRecord.getTestCode())) {
      log.info("testCode is not empty, and type is not release");
      return;
    }
    if (!isHeatCodeEmptyOrRelease(balanceRecord.getHeatCode())) {
      log.info("heatCode is not empty, and type is not release");
      return;
    }
    if (StringUtils.isNotBlank(balanceRecord.getScrapCode())) {
      log.info("scrapCode is not empty");
      return;
    }
    if (StringUtils.isNotBlank(balanceRecord.getReworkCode())) {
      log.info("reworkCode is not empty");
      return;
    }
    if (balanceRecord.getSpecialMt() != 0) {
      log.info("specialMT != 0");
      return;
    }
    if (!checkBalanceAndE3(balanceRecord)) {
      log.info("balance or E3 check failed");
      return;
    }
    log.info("set finished to 1");
    wheelRecord.setFinished(1);
    wheelRecord.setXh(balanceRecord.getXh());
  }

  // 如果是CJ33或者CP33，检查CihenCode，如果CihenCode不是OK，返回false，其它情况都返回true
  private boolean checkCihenCode(WheelRecord wheelRecord) {

    if (wheelRecord.getDesign().equals("CJ33") || wheelRecord.getDesign().equals("CP33")) {
      if (!"OK".equals(wheelRecord.getCihenCode())) {
        return false;
      }
    }
    return true;
  }

  // 根据DWIS_9表的Design，查询Designs-轮型表的Internal（是否国内轮）字段，
  // 如果Internal=1, 并且不平衡量的值小于等于125且标注的值等于E3，返回true，否则返回false
  private boolean checkBalanceAndE3(BalanceRecord balanceRecord) {
    int internal = designRepository.findByDesign(balanceRecord.getDesign())
        .map(d -> d.getInternal()).orElse(1);
    if (internal == 1) {
      return balanceRecord.getBalanceV() <= 125 && "E3".equals(balanceRecord.getBalanceS());
    }
    return true;
  }

  private Integer getDesignInternal(String design) {
    return designRepository.findByDesign(design).map(d -> d.getInternal()).orElse(1);
  }

  private boolean isHoldCodeEmptyOrRelease(String code) {
    if (StringUtils.isBlank(code)) {
      return true;
    }
    return basicDataService.findHoldCodeByCode(code)
        .filter(d -> "Release".equals(d.getCodeType())).isPresent();
  }

  private boolean isTestCodeEmptyOrRelease(String code) {
    if (StringUtils.isBlank(code)) {
      return true;
    }
    return basicDataService.findTestCodeByCode(code)
        .filter(d -> "Release".equals(d.getCodeType())).isPresent();
  }

  private boolean isHeatCodeEmptyOrRelease(String code) {
    if (StringUtils.isBlank(code)) {
      return true;
    }
    return basicDataService.findHeatCodeByCode(code)
        .filter(d -> "Release".equals(d.getCodeType())).isPresent();
  }

  private void saveBarcodePrintRecord(BalanceRecord balanceRecord, WheelRecord wheelRecord) {
    BarcodePrintRecord barcodePrintRecord = BarcodePrintRecord.builder()
        .barcodeInspectorId(balanceRecord.getMarkInspectorId())
        .inspectorId(balanceRecord.getInspectorId())
        .wheelSerial(balanceRecord.getWheelSerial())
        .xh(balanceRecord.getXh())
        .design(balanceRecord.getDesign())
        .finished(wheelRecord.getFinished())
        .finishPrint(wheelRecord.getFinishPrint())
        .opeDT(operatingTimeCtrService.getQAOperatingTime())
        .createTime(new Date())
        .build();
    updateMaxBarcodeTimes(barcodePrintRecord);
    barcodePrintRecordRepository.save(barcodePrintRecord);
    updateWheelRecordForBarcodePrint(barcodePrintRecord, wheelRecord);
  }

  // 条码打印次数取值为 条码打印表相同车轮序列号的ts（条码打印次数）最大值+1
  private void updateMaxBarcodeTimes(BalanceRecord balanceRecord) {
    Integer maxBalanceTimes = balanceRecordRepository
        .findMaxBalanceTimes(balanceRecord.getWheelSerial()) + 1;
    balanceRecord.setTs(maxBalanceTimes);
  }

  public BarcodePrintRecord printBarcode(BarcodePrintRecord barcodePrintRecord) {
    Map<String, Boolean> parameterMap = new HashMap<>();
    parameterMap.put("balance", true);
    parameterMap.put("finishPrint", true);
    parameterMap.put("confirmedScrap", false);
    WheelRecord wheelRecord = wheelRecordService.findWheel(barcodePrintRecord.getWheelSerial(), parameterMap)
        .orElseThrow(() -> PlatformException.badRequestException("请检查轮号是否正确"));

    barcodePrintRecord.setOpeDT(operatingTimeCtrService.getQAOperatingTime());
    barcodePrintRecord.setReprintCode(1);
    barcodePrintRecord.setCreateTime(new Date());
    updateMaxBarcodeTimes(barcodePrintRecord);
    barcodePrintRecordRepository.save(barcodePrintRecord);
    updateWheelRecordForBarcodeRePrint(barcodePrintRecord, wheelRecord);
    String dataMatrix = getDataMatrix(wheelRecord);
    barcodePrintRecord.setDataMatrix(dataMatrix);
    return barcodePrintRecord;
  }

  // 条码打印次数取值为 条码打印表相同车轮序列号的ts（条码打印次数）最大值+1
  public void updateMaxBarcodeTimes(BarcodePrintRecord barcodePrintRecord) {
    Integer maxPrintTimes = barcodePrintRecordRepository.findMaxPrintTimes(barcodePrintRecord.getWheelSerial()) + 1;
    barcodePrintRecord.setTs(maxPrintTimes);
  }

  // 打印完成之后更新wheel_record表
  private void updateWheelRecordForBarcodePrint(BarcodePrintRecord barcodePrintRecord, WheelRecord wheelRecord) {
    wheelRecord.setBarcodeId(barcodePrintRecord.getId());
    wheelRecord.setBarcode(wheelRecord.getBarcode() == null ? 1 : wheelRecord.getBarcode() + 1);
    wheelRecord.setLastBarcode(barcodePrintRecord.getOpeDT());
    wheelRecord.setFinishPrint(wheelRecord.getFinished());
    wheelRecordRepository.save(wheelRecord);
  }

  // 补打完成之后更新wheel_record表
  private void updateWheelRecordForBarcodeRePrint(BarcodePrintRecord barcodePrintRecord, WheelRecord wheelRecord) {
    wheelRecord.setBarcodeId(barcodePrintRecord.getId());
    wheelRecord.setBarcode(wheelRecord.getBarcode() == null ? 1 : wheelRecord.getBarcode() + 1);
    wheelRecord.setFinishPrint(wheelRecord.getFinished());
    wheelRecordRepository.save(wheelRecord);
  }
}
