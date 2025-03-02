package tech.hciot.dwis.business.application;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.*;
import tech.hciot.dwis.business.infrastructure.UnderlineCamelUtil;
import tech.hciot.dwis.business.infrastructure.templatesql.SqlTemplateParser;
import tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc.FinalCheckPercentRecord;
import tech.hciot.dwis.business.interfaces.dto.SPCChartData;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static tech.hciot.dwis.base.util.StandardTimeUtil.*;

@Service
@Slf4j
public class MoldService {

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

  private static final String PRE_THICKNESS_LIMITS = "pre_spray_thickness";
  private static final String SAND_BREATHABILITY_LIMITS = "sand_breathability";
  private static final String SAND_TEMP_LIMITS = "sand_temp";
  private static final String WATER_GLASS_TEMP_LIMITS = "water_glass_temp";

  @Autowired
  private MoldPreShiftRecordRepository moldPreShiftRecordRepository;

  @Autowired
  private MetalMoldRepository metalMoldRepository;

  @Autowired
  private SandJetRecordRepository sandJetRecordRepository;

  @Autowired
  private GraphiteRepository graphiteRepository;

  @Autowired
  private SandMixRecordRepository sandMixRecordRepository;

  @Autowired
  private MoldParamsRepository moldParamsRepository;

  @Autowired
  private PreSprayRecordRepository preSprayRecordRepository;

  @Autowired
  private FinalSprayRecordRepository finalSprayRecordRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private EntityManager entityManager;

  @Resource
  SqlTemplateParser sqlTemplateParser;

  private List queryResultList(String templateName, Map<String, Object> parameterMap, Class z) {
    log.info("mold spc query - {} begin", templateName);
    String sql = sqlTemplateParser.parseSqlTemplate("mold-spc", templateName, parameterMap);
    Query query = entityManager.createNativeQuery(sql);
    parameterMap.entrySet().forEach(entry -> {
      if (!entry.getKey().startsWith("shift")) {
        query.setParameter(entry.getKey(), entry.getValue());
      }
    });
    query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    try {
      List<Map<String, Object>> resultList = query.getResultList();
      List objectList = new ArrayList<>();
      resultList.forEach(data -> {
        Map<String, Object> humpData = UnderlineCamelUtil.underlineMapToHumpMap(data);
        objectList.add(new JSONObject(humpData).toJavaObject(z));
      });
      return objectList;
    } catch (NoResultException e) {
    } catch (Exception e) {
      log.error("mold spc query error: " + e.getMessage(), e);
    }
    return new ArrayList<>();
  }

  public Integer addPreShiftRecord(MoldPreShiftRecord moldPreShiftRecord) {
    moldPreShiftRecord.setCreateTime(new Date());
    return moldPreShiftRecordRepository.save(moldPreShiftRecord).getId();
  }

  public Page<MoldPreShiftRecord> getPreShift(Integer cd, Integer currentPage, Integer pageSize) {
    Specification<MoldPreShiftRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (cd != null) {
        list.add(criteriaBuilder.equal(root.get("cd"), cd));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return moldPreShiftRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public void editPreShift(Integer id, MoldPreShiftRecord moldPreShiftRecord) {
    moldPreShiftRecordRepository.findById(id).ifPresent(old -> {
      moldPreShiftRecord.setId(old.getId());
      moldPreShiftRecord.setCreateTime(old.getCreateTime());
      moldPreShiftRecordRepository.save(moldPreShiftRecord);
    });
  }

  public MoldPreShiftRecord findPreShiftByIdAndType(Integer preShiftId, Integer type) {
    MoldPreShiftRecord moldPreShiftRecord = moldPreShiftRecordRepository.findById(preShiftId)
            .orElseThrow(() -> PlatformException.badRequestException("没有找到造型射砂开班信息"));
    Integer lineOdd, lineEven;
    if (moldPreShiftRecord.getCd() == 0) {
      lineOdd = 3;
      lineEven = 4;
    } else {
      lineOdd = 1;
      lineEven = 2;
    }
    // type: 1-预喷；2-射砂；3-终喷
    if (type == 2) {
      sandJetRecordRepository.findNewestByPreShiftIdAndLineNo(preShiftId, lineOdd).ifPresent(sandJetRecord -> {
        moldPreShiftRecord.setOperatorId(sandJetRecord.getOperatorId());
        moldPreShiftRecord.setMetalMoldOdd(sandJetRecord.getMetalMold());
        moldPreShiftRecord.setMetalTempOdd(sandJetRecord.getMetalTemp());
        moldPreShiftRecord.setCo2HardeningTimeOdd(sandJetRecord.getCo2HardeningTime());
      });
      sandJetRecordRepository.findNewestByPreShiftIdAndLineNo(preShiftId, lineEven).ifPresent(sandJetRecord -> {
        moldPreShiftRecord.setOperatorId(sandJetRecord.getOperatorId());
        moldPreShiftRecord.setMetalMoldEven(sandJetRecord.getMetalMold());
        moldPreShiftRecord.setMetalTempEven(sandJetRecord.getMetalTemp());
        moldPreShiftRecord.setCo2HardeningTimeEven(sandJetRecord.getCo2HardeningTime());
      });
    } else if (type == 1) {
      preSprayRecordRepository.findNewestByPreShiftId(preShiftId).ifPresent(preSprayRecord -> {
        moldPreShiftRecord.setOperatorId(preSprayRecord.getOperatorId());
      });
    } else if (type == 3) {
      finalSprayRecordRepository.findNewestByPreShiftId(preShiftId).ifPresent(finalSprayRecord -> {
        moldPreShiftRecord.setOperatorId(finalSprayRecord.getOperatorId());
      });
    }
    return moldPreShiftRecord;
  }

  public List<String> getMetalMoldList (Integer cdType) {
    return metalMoldRepository.findByCdAndEnabled(cdType, 1);
  }

  public Integer addSandJetRecord(SandJetRecord sandJetRecord) {
    sandJetRecord.setCreateTime(new Date());
    sandJetRecord.setJetTime(new Date());
    sandJetRecord.setStatus(SandJetRecord.STATUS_JETTED);
    return sandJetRecordRepository.save(sandJetRecord).getId();
  }

  public void editSandJet(Integer id, SandJetRecord sandJetRecord) {
    sandJetRecordRepository.findById(id).ifPresent(old -> {
      if (old.getStatus() != null && old.getStatus() > 1 && !old.getGraphite().equals(sandJetRecord.getGraphite())) {
        throw PlatformException.badRequestException("该石墨号已浇注，不能修改石墨号！");
      }
      sandJetRecord.setId(old.getId());
      sandJetRecord.setJetTime(old.getJetTime());
      sandJetRecord.setCreateTime(old.getCreateTime());
      sandJetRecord.setStatus(old.getStatus());
      sandJetRecordRepository.save(sandJetRecord);
    });
  }

  @Transactional
  public void deleteSandJet(Integer id) {
    sandJetRecordRepository.findById(id).ifPresent(sandJetRecord -> {
      if (sandJetRecord.getStatus() != null && sandJetRecord.getStatus() > 1) {
        throw PlatformException.badRequestException("该石墨号已浇注，不能删除！");
      }
      sandJetRecordRepository.deleteById(id);
    });
  }

  public Page<SandJetRecord> findSandJetList( Integer preShiftId, Integer lineNo, String graphite,
                                              Integer currentPage, Integer pageSize) {
    Specification<SandJetRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      list.add(criteriaBuilder.equal(root.get("preShiftId"), preShiftId));
      if (graphite != null) {
        list.add(criteriaBuilder.equal(root.get("graphite"), graphite));
      }
      if (lineNo != null) {
        list.add(criteriaBuilder.equal(root.get("lineNo"), lineNo));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return sandJetRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Integer addSandMixRecord(SandMixRecord sandMixRecord) {
    Integer breathability = moldParamsRepository.findIdByTypeAndEnabled(SAND_BREATHABILITY_LIMITS, 1);
    Integer sandTemp = moldParamsRepository.findIdByTypeAndEnabled(SAND_TEMP_LIMITS, 1);
    Integer waterGlassTemp = moldParamsRepository.findIdByTypeAndEnabled(WATER_GLASS_TEMP_LIMITS, 1);
    if (breathability != null) {
      sandMixRecord.setSandBreathabilityLimits(breathability);
    };
    if (sandTemp != null) {
      sandMixRecord.setSandTempLimits(sandTemp);
    };
    if (waterGlassTemp != null) {
      sandMixRecord.setWaterGlassTempLimits(waterGlassTemp);
    };
    sandMixRecord.setCreateTime(new Date());
    // 计算水玻璃加入量百分比（保留2位小数，四舍五入）
    sandMixRecord.setWaterGlassPercent(sandMixRecord.getWaterGlass().divide(sandMixRecord.getQuartzSand().multiply(BigDecimal.valueOf(10)), 2, RoundingMode.HALF_UP));
    // 计算粉煤灰加入量百分比（保留2位小数，四舍五入）
    sandMixRecord.setCoalAshPercent(sandMixRecord.getCoalAsh().divide(sandMixRecord.getQuartzSand().multiply(BigDecimal.valueOf(10)), 2, RoundingMode.HALF_UP));
    return sandMixRecordRepository.save(sandMixRecord).getId();
  }

  public Page<SandMixRecord> findSandMixList(String inspectorId,
                                             Integer currentPage,
                                             Integer pageSize) {
    Specification<SandMixRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (inspectorId != null) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    Page<SandMixRecord> page = sandMixRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
    // 如果粉煤灰/水玻璃加入量百分比超出5±0.1%，则标红显示
    if (!page.isEmpty()) {
      for (SandMixRecord record : page) {
        List<String> light = new ArrayList<>();
        if (record.getCoalAshPercent().compareTo(BigDecimal.valueOf(4.9)) < 0 || record.getCoalAshPercent().compareTo(BigDecimal.valueOf(5.1)) > 0) {
          light.add("coalAshPercent");
        }
        if (record.getWaterGlassPercent().compareTo(BigDecimal.valueOf(4.9)) < 0 || record.getWaterGlassPercent().compareTo(BigDecimal.valueOf(5.1)) > 0) {
          light.add("waterGlassPercent");
        }
        if (light.size() != 0) { record.setHighlight(light); }
      }
    }
    return page;
  }

  public void editSandMix(Integer id, SandMixRecord sandMixRecord) {
    sandMixRecordRepository.findById(id).ifPresent(old -> {
      sandMixRecord.setId(old.getId());
      sandMixRecord.setCreateTime(old.getCreateTime());
      // 计算水玻璃加入量百分比（保留2位小数，四舍五入）
      sandMixRecord.setWaterGlassPercent(sandMixRecord.getWaterGlass().divide(sandMixRecord.getQuartzSand().multiply(BigDecimal.valueOf(10)), 2, RoundingMode.HALF_UP));
      // 计算粉煤灰加入量百分比（保留2位小数，四舍五入）
      sandMixRecord.setCoalAshPercent(sandMixRecord.getCoalAsh().divide(sandMixRecord.getQuartzSand().multiply(BigDecimal.valueOf(10)), 2, RoundingMode.HALF_UP));
      sandMixRecordRepository.save(sandMixRecord);
    });
  }

  public String findNextGraphite(Integer lineNo) {
    String nextGraphite = null;
    //先找出此线号下最新石墨号在数据表中的使用次数
    Integer rows = sandJetRecordRepository.findGraphiteRows(lineNo);
    if (rows!= null && rows > 1) { //该石墨号并不是第一次在此线号下使用
      nextGraphite = sandJetRecordRepository.findNextGraphite(lineNo);
    }
    return nextGraphite;
  }

  public String findNextWheelNo(String moldDate, Integer lineNo) {
    String nextWheelNo = "001";
    String[] md = moldDate.split("-");
    String year = md[0].substring(2);
    String month = md[1];
    String day = md[2];
    String currentWheelNo = sandJetRecordRepository.findCurrentWheelNo(year, month, day, lineNo);
    if (currentWheelNo != null) { //在当前日期&&线号条件下查询到记录, 序列号加1计算
      String str = "1" + currentWheelNo; //例如， 先将“002”变成“1002”
      Integer temp = Integer.parseInt(str) + 1;
      nextWheelNo = Integer.toString(temp).substring(1); //截取后三位
    }
    return nextWheelNo;
  }

  public Integer addPreSprayRecord(PreSprayRecord preSprayRecord) {
    Integer limitId = moldParamsRepository.findIdByTypeAndEnabled(PRE_THICKNESS_LIMITS, 1);
    if (limitId != null) {
      preSprayRecord.setThicknessLimits(limitId);
    };
    preSprayRecord.setCreateTime(new Date());
    return preSprayRecordRepository.save(preSprayRecord).getId();
  }

  public Page<PreSprayRecord> findPreSprayList(Integer preShiftId,
                                             Integer currentPage,
                                             Integer pageSize) {
    Specification<PreSprayRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (preShiftId != null) {
        list.add(criteriaBuilder.equal(root.get("preShiftId"), preShiftId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    Page<PreSprayRecord> page = preSprayRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
    return page;
  }

  public void editPreSpray(Integer id, PreSprayRecord preSprayRecord) {
    preSprayRecordRepository.findById(id).ifPresent(old -> {
      preSprayRecord.setId(old.getId());
      preSprayRecord.setCreateTime(old.getCreateTime());
      preSprayRecordRepository.save(preSprayRecord);
    });
  }

  public Integer addFinalSprayRecord(FinalSprayRecord finalSprayRecord) {
    finalSprayRecord.setCreateTime(new Date());
    return finalSprayRecordRepository.save(finalSprayRecord).getId();
  }

  public Page<FinalSprayRecord> findFinalSprayList(Integer preShiftId,
                                               Integer currentPage,
                                               Integer pageSize) {
    Specification<FinalSprayRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (preShiftId != null) {
        list.add(criteriaBuilder.equal(root.get("preShiftId"), preShiftId));
      }

      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    Page<FinalSprayRecord> page = finalSprayRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
    return page;
  }

  public void editFinalSpray(Integer id, FinalSprayRecord finalSprayRecord) {
    finalSprayRecordRepository.findById(id).ifPresent(old -> {
      finalSprayRecord.setId(old.getId());
      finalSprayRecord.setCreateTime(old.getCreateTime());
      finalSprayRecordRepository.save(finalSprayRecord);
    });
  }

  public List<SPCChartData> findSPCChartList(String type, Map<String, Object> parameterMap) {
    String sql = null;
    switch (type) {
      case PRE_THICKNESS_LIMITS:
        sql = "1-pre-spray-thickness";
        break;
      case SAND_BREATHABILITY_LIMITS:
        sql = "2-sand-breathability";
        break;
      case SAND_TEMP_LIMITS:
        sql = "3-sand-temp";
        break;
      case WATER_GLASS_TEMP_LIMITS:
        sql = "4-water-glass-temp";
        break;
      default:
        break;
    }
    return sql == null ? null : queryResultList(sql, parameterMap, SPCChartData.class);
  }
}
