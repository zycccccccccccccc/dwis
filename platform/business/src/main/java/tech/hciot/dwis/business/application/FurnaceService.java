package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.hamcrest.core.IsNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.AdditionMaterialTableRepository;
import tech.hciot.dwis.business.domain.ChargeMaterialTableRepository;
import tech.hciot.dwis.business.domain.DipelectrodeTableRepository;
import tech.hciot.dwis.business.domain.FurnaceTapCurrentRepository;
import tech.hciot.dwis.business.domain.FurnaceTapDetailRepository;
import tech.hciot.dwis.business.domain.FurnaceTapTableRepository;
import tech.hciot.dwis.business.domain.O2blowingTableRepository;
import tech.hciot.dwis.business.domain.TempmeasureTableRepository;
import tech.hciot.dwis.business.domain.VoltChangeTableRepository;
import tech.hciot.dwis.business.domain.model.AdditionMaterialTable;
import tech.hciot.dwis.business.domain.model.ChargeMaterialTable;
import tech.hciot.dwis.business.domain.model.ChemistryDetail;
import tech.hciot.dwis.business.domain.model.DipelectrodeTable;
import tech.hciot.dwis.business.domain.model.FurnaceTapCurrent;
import tech.hciot.dwis.business.domain.model.FurnaceTapDetail;
import tech.hciot.dwis.business.domain.model.FurnaceTapTable;
import tech.hciot.dwis.business.domain.model.O2blowingTable;
import tech.hciot.dwis.business.domain.model.TempmeasureTable;
import tech.hciot.dwis.business.domain.model.VoltChangeTable;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValueOfDate;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;

@Service
@Slf4j
public class FurnaceService {

  private static final String DATE_FORMAT_STR = "yyyy-MM-dd";

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

  private SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy");

  @Autowired
  private FurnaceTapTableRepository furnaceTapTableRepository;

  @Autowired
  private FurnaceTapDetailRepository furnaceTapDetailRepository;

  @Autowired
  private FurnaceTapCurrentRepository furnaceTapCurrentRepository;

  @Autowired
  private ChargeMaterialTableRepository chargeMaterialTableRepository;

  @Autowired
  private AdditionMaterialTableRepository additionMaterialTableRepository;

  @Autowired
  private O2blowingTableRepository o2blowingTableRepository;

  @Autowired
  private DipelectrodeTableRepository dipelectrodeTableRepository;

  @Autowired
  private VoltChangeTableRepository voltChangeTableRepository;

  @Autowired
  private TempmeasureTableRepository tempmeasureTableRepository;

  @Autowired
  private ChemistryDetailService chemistryDetailService;

  public Page<FurnaceTapTable> find(String furnaceNo,
                                    String castDate,
                                    String furnaceSeq,
                                    Integer currentPage,
                                    Integer pageSize) {
    Specification<FurnaceTapTable> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (furnaceNo != null) {
        list.add(criteriaBuilder.equal(root.get("furnaceNo"), furnaceNo));
      }
      if (castDate != null) {
        list.add(criteriaBuilder.equal(root.get("castDate"), castDate));
      }
      if (furnaceSeq != null) {
        list.add(criteriaBuilder.equal(root.get("furnaceSeq"), furnaceSeq));
      }
      list.add(criteriaBuilder.equal(root.get("status"), FurnaceTapTable.STATUS_COMMITTED));

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return furnaceTapTableRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public FurnaceTapCurrent findCurrent(Integer furnaceNo) {
    FurnaceTapCurrent furnaceTapCurrent =
      furnaceTapCurrentRepository.findLatest(furnaceNo, FurnaceTapTable.STATUS_SAVED).orElse(null);
    if (furnaceTapCurrent == null) {
      return furnaceTapCurrent;
    }
    furnaceTapCurrent.setChargeTs(chargeMaterialTableRepository.findMaxTimes(furnaceTapCurrent.getId()));
    furnaceTapCurrent.setAddTs(additionMaterialTableRepository.findMaxTimes(furnaceTapCurrent.getId()));
    furnaceTapCurrent.setO2Ts(o2blowingTableRepository.findMaxTimes(furnaceTapCurrent.getId()));
    furnaceTapCurrent.setDipTs(dipelectrodeTableRepository.findMaxTimes(furnaceTapCurrent.getId()));
    furnaceTapCurrent.setVoltTs(voltChangeTableRepository.findMaxTimes(furnaceTapCurrent.getId()));
    furnaceTapCurrent.setTempTs(tempmeasureTableRepository.findMaxTimes(furnaceTapCurrent.getId()));
    return furnaceTapCurrent;
  }

  public FurnaceTapTable findLast(Integer furnaceNo) {
    return furnaceTapTableRepository.findLatest(furnaceNo, FurnaceTapTable.STATUS_COMMITTED).orElse(null);
  }

  /**
   * 根据所选炉号、浇注日期 查询FurnaceTap_Table表中浇注日期所在年份相同炉号的最大炉次+1回显到页面
   * @param dateStr
   * @param furnaceNo
   * @return
   */
  public Integer findCurrentSeq(String dateStr, Integer furnaceNo) {
    try {
      Date date = dateFormat.parse(dateStr);
      String year = String.format("%tY", date);
      String beginDate = year + "-01-01";
      String endDate = year + "-12-31";
      return furnaceTapTableRepository.findMaxSeq(furnaceNo, beginDate, endDate) + 1;
    } catch (ParseException e) {
      log.error("date parse failed: {}", dateStr);
      return 1;
    }
  }

  public FurnaceTapTable save(FurnaceTapTable request) {
    FurnaceTapTable furnaceTapTable
      = furnaceTapTableRepository.findLatest(request.getFurnaceNo(), FurnaceTapTable.STATUS_SAVED)
      .orElse(FurnaceTapTable.builder().build());
    FurnaceTapTable sameRecord = furnaceTapTableRepository.findSame(request.getFurnaceNo(), request.getFurnaceSeq(), FurnaceTapTable.STATUS_COMMITTED,
                    Integer.parseInt(getStringValueOfDate(request.getCastDate(), "yyyy"))).orElse(FurnaceTapTable.builder().build());
    if(sameRecord.getId() == null) { //判断是否与已提交的记录重复
      BeanUtil.copyProperties(request, furnaceTapTable,
              CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      furnaceTapTable.setFurnaceKey(furnaceTapTable.getFurnaceNo()
              + "_" + furnaceTapTable.getFurnaceSeq()
              + "_" + furnaceTapTable.getTapNo());
      if(furnaceTapTable.getId() == null) { //判断是否首次添加该记录
        furnaceTapTable.setStatus(FurnaceTapTable.STATUS_SAVED);
        furnaceTapTable.setCreateTime(new Date());
      }
        return furnaceTapTableRepository.save(furnaceTapTable);
      }
    throw PlatformException.badRequestException("炉前熔炼记录重复，不能保存");
  }

  public FurnaceTapTable commit(FurnaceTapTable request) {
    FurnaceTapTable furnaceTapTable
      = furnaceTapTableRepository.findLatest(request.getFurnaceNo(), FurnaceTapTable.STATUS_SAVED)
      .orElseThrow(() -> PlatformException.badRequestException("提交之前请先保存记录"));
    BeanUtil.copyProperties(request, furnaceTapTable,
      CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    computeMtotalWeight(furnaceTapTable);
    computeLastCommitted(furnaceTapTable);
    furnaceTapTable.setStatus(FurnaceTapTable.STATUS_COMMITTED);
    return furnaceTapTableRepository.save(furnaceTapTable);
  }

  // 与上一次提交的记录进行计算
  private void computeLastCommitted(FurnaceTapTable furnaceTapTable) {
    furnaceTapTableRepository.findLatest(furnaceTapTable.getFurnaceNo(), FurnaceTapTable.STATUS_COMMITTED)
        .ifPresent(lastFurnaceTapTable -> {
      // 如果不是跨月，电极本月使用数量、电极本月损害数量、接头本月使用数量、接头本月损坏数量这4个字段值需要加上上一条从记录的值
      if (!isSpanMonth(furnaceTapTable, lastFurnaceTapTable)) {
        furnaceTapTable.setElectrodeUseQuantity(furnaceTapTable.getElectrodeUseQuantity()
          + lastFurnaceTapTable.getElectrodeUseQuantity());
        furnaceTapTable.setElectrodeBrokenQuantity(furnaceTapTable.getElectrodeBrokenQuantity()
          + lastFurnaceTapTable.getElectrodeBrokenQuantity());
        furnaceTapTable.setPlugUseQuantity(furnaceTapTable.getPlugUseQuantity()
          + lastFurnaceTapTable.getPlugUseQuantity());
        furnaceTapTable.setPlugBrokenQuantity(furnaceTapTable.getPlugBrokenQuantity()
          + lastFurnaceTapTable.getPlugBrokenQuantity());
      }
      furnaceTapTable.setThistimeEconsumption(furnaceTapTable.getEmeterReading().subtract(lastFurnaceTapTable.getEmeterReading()));
      furnaceTapTable.setThistimeO2UseQuantity(furnaceTapTable.getO2Flow() - lastFurnaceTapTable.getO2Flow());
    });
  }

  // 计算加料合计值
  private void computeMtotalWeight(FurnaceTapTable furnaceTapTable) {
    List<ChargeMaterialTable> chargeMaterialTableList
      = chargeMaterialTableRepository.findByFurnaceTapId(furnaceTapTable.getId());
    if (chargeMaterialTableList.isEmpty()) {
      throw PlatformException.badRequestException("请填写加料信息");
    }
    furnaceTapTable.setMtotalWeight(new BigDecimal(0));
    chargeMaterialTableList.forEach(chargeMaterialTable -> {
      furnaceTapTable.setMtotalWeight(new BigDecimal(furnaceTapTable.getMtotalWeight().doubleValue()
        + chargeMaterialTable.getMtotalWeight().doubleValue()));
    });
  }

  // 跨月的记录
  private boolean isSpanMonth(FurnaceTapTable furnaceTapTable, FurnaceTapTable lastFurnaceTapTable) {
    DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
    if (!monthFormat.format(furnaceTapTable.getCastDate()).equals(monthFormat.format(lastFurnaceTapTable.getCastDate()))) {
      return true;
    }
    return false;
  }

  // 保存加料信息
  public ChargeMaterialTable saveChargeMaterial(ChargeMaterialTable request) {
    assertFurnaceTapExists(request.getFurnaceTapId());
    FurnaceTapTable furnaceTapTable = furnaceTapTableRepository
      .findById(request.getFurnaceTapId()).get();

    if (request.getId() == null) { // id为空表示新增记录
      request.setTimes(chargeMaterialTableRepository.findMaxTimes(request.getFurnaceTapId()) + 1);
    }
    chargeMaterialTableRepository.save(request);

    if (request.getTimes() == 1) { // 第一次加料时，将送电时间保存到主表
      furnaceTapTable.setFirstPoweronTime(request.getPoweronTime());
      furnaceTapTableRepository.save(furnaceTapTable);
    }

    if (request.getId() != null) { // 修改时计算加料总量
      computeMtotalWeight(furnaceTapTable);
      furnaceTapTableRepository.save(furnaceTapTable);
    }
    return request;
  }

  // 保存添加剂信息
  public AdditionMaterialTable saveAdditionMaterial(AdditionMaterialTable request) {
    assertFurnaceTapExists(request.getFurnaceTapId());
    if (request.getId() == null) {
      request.setTimes(additionMaterialTableRepository.findMaxTimes(request.getFurnaceTapId()) + 1);
    }
    return additionMaterialTableRepository.save(request);
  }

  // 保存吹氧信息
  public O2blowingTable saveO2blowing(O2blowingTable request) {
    assertFurnaceTapExists(request.getFurnaceTapId());
    if (request.getId() == null) {
      request.setTimes(o2blowingTableRepository.findMaxTimes(request.getFurnaceTapId()) + 1);
    }
    return o2blowingTableRepository.save(request);
  }

  // 保存浸电极信息
  public DipelectrodeTable saveDipelectrode(DipelectrodeTable request) {
    assertFurnaceTapExists(request.getFurnaceTapId());
    if (request.getId() == null) {
      request.setTimes(dipelectrodeTableRepository.findMaxTimes(request.getFurnaceTapId()) + 1);
    }
    return dipelectrodeTableRepository.save(request);
  }

  // 保存电压变化信息
  public VoltChangeTable saveVoltChange(VoltChangeTable request) {
    assertFurnaceTapExists(request.getFurnaceTapId());
    if (request.getId() == null) {
      request.setTimes(voltChangeTableRepository.findMaxTimes(request.getFurnaceTapId()) + 1);
    }
    return voltChangeTableRepository.save(request);
  }

  // 保存测温信息
  public TempmeasureTable saveTempmeasure(TempmeasureTable request) {
    assertFurnaceTapExists(request.getFurnaceTapId());
    if (request.getId() == null) {
      request.setTimes(tempmeasureTableRepository.findMaxTimes(request.getFurnaceTapId()) + 1);
    }
    return tempmeasureTableRepository.save(request);
  }

  // 录入副表的数据之前，需要存在一条已保存的熔炼主表信息
  private void assertFurnaceTapExists(Integer id) {
    furnaceTapTableRepository.findById(id).orElseThrow(()
      -> PlatformException.badRequestException("请先录入熔炼信息"));
  }

  public Optional<FurnaceTapDetail> findDetail(Integer id) {
    Optional<FurnaceTapDetail> furnaceTapDetailOpt = furnaceTapDetailRepository.findById(id);
    furnaceTapDetailOpt.ifPresent(furnaceTapDetail -> {
      String year = new SimpleDateFormat("yyyy").format(new Date());
      List<ChemistryDetail> chemistryDetailList = chemistryDetailService.findLabForFurnace(furnaceTapDetail.getFurnaceNo(),
        furnaceTapDetail.getFurnaceSeq(), year);
      furnaceTapDetail.setChemistryDetailList(chemistryDetailList);
    });
    return furnaceTapDetailOpt;
  }
}
