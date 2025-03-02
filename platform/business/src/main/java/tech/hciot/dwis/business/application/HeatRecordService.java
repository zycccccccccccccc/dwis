package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.*;
import tech.hciot.dwis.business.interfaces.dto.HElementHeatResponse;
import tech.hciot.dwis.business.interfaces.dto.LadleAddRequest;
import tech.hciot.dwis.business.interfaces.dto.LadleAddsResponse;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.LADLE_EXIST;

@Service
@Slf4j
public class HeatRecordService {

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
  private SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy");

  @Autowired
  private HeatRecordRepository heatRecordRepository;

  @Autowired
  private LadleRecordRepository ladleRecordRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private ChemistryDetailRepository chemistryDetailRepository;

  @Autowired
  private PourParamsRepository pourParamsRepository;

  @Autowired
  private LadleAdditionRecordRepository ladleAdditionRecordRepository;

  @Autowired
  private LadleRecordService ladleRecordService;

  public Page<HeatRecord> find(Integer furnaceNo,
      Integer heatSeq,
      String castDateStr,
      Integer currentPage,
      Integer pageSize) {
    Date last = DateUtils.addHours(new Date(), -12);
    Specification<HeatRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (furnaceNo != null) {
        list.add(criteriaBuilder.equal(root.get("furnaceNo"), furnaceNo));
      }
      if (heatSeq != null) {
        list.add(criteriaBuilder.equal(root.get("heatSeq"), heatSeq));
      }
      if (StringUtils.isNotEmpty(castDateStr)) {
        Date castDate = parseDate(castDateStr);
        list.add(criteriaBuilder.equal(root.get("castDate"), castDate));
      }
      if (ObjectUtil.isAllEmpty(furnaceNo, heatSeq, castDateStr)) {
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("recordCreated"), last));
      }

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      if (castDateStr == null) {
        query.orderBy(criteriaBuilder.desc(root.get("recordCreated")));
      } else {
        query.orderBy(criteriaBuilder.asc(root.get("tapSeq")));
      }
      return query.getRestriction();
    };
    return heatRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize))
        .map(heatRecord -> {
          heatRecord.setEditable(heatRecord.getRecordCreated().after(last));
          return heatRecord;
        });
  }

  public Integer findCurrentHeatSeq(String dateStr, Integer furnaceNo) {
    try {
      Date date = dateFormat.parse(dateStr);
      String year = String.format("%tY", date);
      String beginDate = year + "-01-01";
      String endDate = year + "-12-31";
      return heatRecordRepository.findMaxSeq(furnaceNo, beginDate, endDate) + 1;
    } catch (ParseException e) {
      log.error("date parse failed: {}", dateStr);
      return 1;
    }
  }

  public Integer add(HeatRecord heatRecord) {
    String heatRecordKey = generateHeatRecordKey(heatRecord);
    heatRecordRepository.findByHeatRecordKey(heatRecordKey).ifPresent(heatRecord1 -> {
      throw PlatformException.badRequestException("大包关键字已存在");
    });
    heatRecord.setHeatRecordKey(heatRecordKey);
    heatRecord.setRecordCreated(new Date());
    heatRecordRepository.save(heatRecord);
    return heatRecord.getId();
  }

  public void modify(Integer id, HeatRecord newHeatRecord) {
    HeatRecord existedHeatRecord = heatRecordRepository.findById(id)
        .orElseThrow(() -> PlatformException.badRequestException("炉信息不存在"));

    String newHeatRecordKey = generateHeatRecordKey(newHeatRecord);
    heatRecordRepository.findByHeatRecordKey(newHeatRecordKey).ifPresent(heatRecord -> {
      if (heatRecord.getId() != id) {
        throw PlatformException.badRequestException("大包关键字已存在");
      }
    });

    BeanUtil.copyProperties(newHeatRecord, existedHeatRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    if (!existedHeatRecord.getHeatRecordKey().equals(newHeatRecordKey)) {
      existedHeatRecord.setHeatRecordKey(newHeatRecordKey);
      ladleRecordService.updateLadleRecordKey(existedHeatRecord);
    }
    heatRecordRepository.save(existedHeatRecord);
  }

  @Transactional
  public void delete(Integer id) {
    heatRecordRepository.findById(id).ifPresent(heatRecord -> {
      if (ladleRecordRepository.countByHeatRecordId(id) > 0) {
        throw LADLE_EXIST.getPlatformException();
      }
      List<ChemistryDetail> list = chemistryDetailRepository.findByHeatRecordId(id);
      if (list.size() > 0) {
        Iterator iterator = list.iterator();
        while(iterator.hasNext()) {
          ChemistryDetail cd = (ChemistryDetail) iterator.next();
          cd.setHeatRecordId(null);
          chemistryDetailRepository.save(cd);
        }
      }
      heatRecordRepository.deleteById(id);
    });
  }

  private String generateHeatRecordKey(HeatRecord heatRecord) {
    String datePre = outputFormat.format(heatRecord.getCastDate());
    return datePre + "_" + heatRecord.getFurnaceNo() + "_" + heatRecord.getHeatSeq();
  }

  public List<HElementHeatResponse> findHElementHeatRecord(String date) {
    List<Map<String, Object>> resultMap = heatRecordRepository.findHElementHeatRecord(date);
    List<HElementHeatResponse> resultList = new ArrayList<>();
    resultMap.forEach(row -> {
      JSONObject result = new JSONObject(row);
      resultList.add(result.toJavaObject(HElementHeatResponse.class));
    });
    return resultList;
  }

  public List<Integer> findTapSeqList(String date) {
    return heatRecordRepository.findTapSeqListByCastDate(date);
  }

  public List<PourParams> findPourParams(String type) {
    return pourParamsRepository.findByTypeAndEnabled(type,1);
  }

  public LadleAddsResponse findLadleAddsForPour(Integer heatRecordId, String furnaceNo, String heatSeq) {
    LadleAddsResponse response = new LadleAddsResponse();
    response.setHeatRecordId(heatRecordId);
    Map<String, Map<String, BigDecimal>> res = new HashMap<>();
    for (int i = 1; i <= 4; i++) { // 初始化传送值
      Map<String, BigDecimal> ladleAdd = new HashMap<>();
      ladleAdd.put("cCon", null);
      ladleAdd.put("siCon", null);
      ladleAdd.put("mnCon", null);
      ladleAdd.put("alCal", null);
      ladleAdd.put("cAct", null);
      ladleAdd.put("siAct", null);
      ladleAdd.put("mnAct", null);
      ladleAdd.put("alAct", null);
      res.put("L" + i, ladleAdd);
    }
    Map<String, BigDecimal> temp = null;
    for (int i = 1; i <= 4; i++) {
      LadleAdditionRecord repeat = ladleAdditionRecordRepository.findByHeatRecordIdAndLaddleSeq(heatRecordId, i);
      if (repeat == null) { //laddle_addition_record表中无对应底注包加料记录
        //获取对应底注包加料计算值
        temp = calLadleAdds(furnaceNo, heatSeq, i);
        if (temp != null) {
          //将对应底注包计算值添加至页面response中
          res.put("L" + i, temp);
          //将对应底注包计算值保存至laddle_addition_record表中
          LadleAdditionRecord ladleAdditionRecord = LadleAdditionRecord.builder().heatRecordId(heatRecordId).build();
          ladleAdditionRecord.setLadleSeq(i);
          ladleAdditionRecord.setCCal(temp.get("C").multiply(HeatRecord.C_UNIT).setScale(2, RoundingMode.HALF_UP));
          if (i == 1) {
            ladleAdditionRecord.setSiCal(temp.get("Si").multiply(HeatRecord.Si_UNIT_L1).setScale(2, RoundingMode.HALF_UP));
          } else {
            ladleAdditionRecord.setSiCal(temp.get("Si").multiply(HeatRecord.Si_UNIT_L234).setScale(2, RoundingMode.HALF_UP));
          }
          ladleAdditionRecord.setMnCal(temp.get("Mn").multiply(HeatRecord.Mn_UNIT).setScale(2, RoundingMode.HALF_UP));
          ladleAdditionRecord.setAlCal(temp.get("Al"));
          ladleAdditionRecord.setCreateTime(new Date());
          ladleAdditionRecordRepository.save(ladleAdditionRecord);
        }
      } else { //laddle_addition_record表中存在对应底注包加料记录
        temp = new HashMap<>();
        if (repeat.getPourdirectTime() != null) { //浇注指导已确认对应底注包加料值，则返回已确认的值
          temp.put("C", repeat.getCCon().divide(HeatRecord.C_UNIT).setScale(0, RoundingMode.HALF_UP));
          if (i == 1) {
            temp.put("Si", repeat.getSiCon().divide(HeatRecord.Si_UNIT_L1).setScale(0, RoundingMode.HALF_UP));
          } else {
            temp.put("Si", repeat.getSiCon().divide(HeatRecord.Si_UNIT_L234).setScale(0, RoundingMode.HALF_UP));
          }
          temp.put("Mn", repeat.getMnCon().divide(HeatRecord.Mn_UNIT).setScale(0, RoundingMode.HALF_UP));
        } else { //浇注指导未确认对应底注包加料值，则返回计算值
          if (repeat.getCCal() != null) {
            temp.put("C", repeat.getCCal().divide(HeatRecord.C_UNIT).setScale(0, RoundingMode.HALF_UP));
          }
          if (repeat.getSiCal() != null) {
            if (i == 1) {
              temp.put("Si", repeat.getSiCal().divide(HeatRecord.Si_UNIT_L1).setScale(0, RoundingMode.HALF_UP));
            } else {
              temp.put("Si", repeat.getSiCal().divide(HeatRecord.Si_UNIT_L234).setScale(0, RoundingMode.HALF_UP));
            }
          }
          if (repeat.getMnCal() != null) {
            temp.put("Mn", repeat.getMnCal().divide(HeatRecord.Mn_UNIT).setScale(0, RoundingMode.HALF_UP));
          }
        }
        res.put("L" + i, temp);
      }
    }
    response.setLadleAdds(res);
    return response;
  }

  // 根据茶壶包化学元素值计算小包添加料
  public Map<String, BigDecimal> calLadleAdds(String furnaceNo, String heatSeq, Integer ladleSeq) {
    String furnaceSeq = furnaceNo + '-' + heatSeq;
    //如果chemistry_detail表中对应记录创建时间早于heat_record表中对应记录创建时间，则chemistry_detail表中heat_record_id字段更新不及时，
    //造成通过heat_record_id字段无法找到对应的茶壶包化学成分记录；
    //因此，通过furnaceSeq字段查找最新茶壶包化学成分记录；
    Optional<ChemistryDetail> chemistryDetailOpt = chemistryDetailRepository.findTPotByFurnaceSeq(furnaceSeq);
    Map<String, BigDecimal> ladleAdd = new HashMap<>();
    return chemistryDetailOpt.map(chemistryDetail -> {
      // 计算C元素的添加值（多少个0.01%）
      if (HeatRecord.C_TARGET.compareTo(chemistryDetail.getC()) > 0) {
        BigDecimal c_diff = HeatRecord.C_TARGET.subtract(chemistryDetail.getC());
        // 除法运算，使用HALF_UP四舍五入模式
        BigDecimal c_num = c_diff.divide(BigDecimal.valueOf(0.01), 0, RoundingMode.HALF_UP);
        ladleAdd.put("C", c_num);
      } else {
        ladleAdd.put("C", BigDecimal.valueOf(0));
      }
      // 计算Si元素的添加值（多少个0.01%）
      if (HeatRecord.Si_TARGET.compareTo(chemistryDetail.getSi()) > 0) {
        BigDecimal si_diff = HeatRecord.Si_TARGET.subtract(chemistryDetail.getSi());
        BigDecimal si_num = si_diff.divide(BigDecimal.valueOf(0.01), 0, RoundingMode.HALF_UP);
        ladleAdd.put("Si", si_num);
      } else {
        ladleAdd.put("Si", BigDecimal.valueOf(0));
      }
      // 计算Mn元素的添加值（多少个0.01%）
      if (HeatRecord.Mn_TARGET.compareTo(chemistryDetail.getMn()) > 0) {
        BigDecimal mn_diff = HeatRecord.Mn_TARGET.subtract(chemistryDetail.getMn());
        BigDecimal mn_num = mn_diff.divide(BigDecimal.valueOf(0.01), 0, RoundingMode.HALF_UP);
        ladleAdd.put("Mn", mn_num);
      } else {
        ladleAdd.put("Mn", BigDecimal.valueOf(0));
      }
      // 计算Al元素的添加值
      if (HeatRecord.Al_TARGET.compareTo(chemistryDetail.getAl()) > 0) {
        if (ladleSeq == 1) {
          ladleAdd.put("Al", HeatRecord.Al_UNIT_L1);
        } else {
          ladleAdd.put("Al", HeatRecord.Al_UNIT_L234);
        }
      } else {
        ladleAdd.put("Al", BigDecimal.valueOf(0));
      }
      return ladleAdd;
    }).orElse(null);
  }

  @Transactional
  public Integer addLadleAdds(LadleAddRequest request) {
    LadleAdditionRecord ladleAdditionRecord = ladleAdditionRecordRepository.findByHeatRecordIdAndLaddleSeq(request.getHeatRecordId(), request.getLadleSeq());
    if (ladleAdditionRecord != null) { //浇注指导发送确认值，表中已存在对应底注包计算加料记录
      if (request.getType().equals("pourdirect")) { //浇注指导确认加料值
        if (ladleAdditionRecord.getPourdirectTime() != null) {
          throw PlatformException.badRequestException("不能重复发送提交！");
        } else {
          ladleAdditionRecord.setPourdirectTime(new Date());
          ladleAdditionRecord.setPourdirectId(request.getOperatorId());
          ladleAdditionRecord.setCCon(request.getC().multiply(HeatRecord.C_UNIT).setScale(2, RoundingMode.HALF_UP));
          if (request.getLadleSeq() == 1) {
            ladleAdditionRecord.setSiCon(request.getSi().multiply(HeatRecord.Si_UNIT_L1).setScale(2, RoundingMode.HALF_UP));
          } else {
            ladleAdditionRecord.setSiCon(request.getSi().multiply(HeatRecord.Si_UNIT_L234).setScale(2, RoundingMode.HALF_UP));
          }
          ladleAdditionRecord.setMnCon(request.getMn().multiply(HeatRecord.Mn_UNIT).setScale(2, RoundingMode.HALF_UP));
        }
      }
      if (request.getType().equals("materialpre")) { //备料工实际加料值
        if (ladleAdditionRecord.getMaterialpreTime() != null) {
          throw PlatformException.badRequestException("不能重复发送提交！");
        } else {
          ladleAdditionRecord.setMaterialpreTime(new Date());
          ladleAdditionRecord.setMaterialpreId(request.getOperatorId());
          ladleAdditionRecord.setCAct(request.getC());
          ladleAdditionRecord.setSiAct(request.getSi());
          ladleAdditionRecord.setMnAct(request.getMn());
          ladleAdditionRecord.setAlAct(request.getAl());
        }
      }
    } else {  //浇注指导发送确认值，表中不存在对应底注包计算加料记录,则创建新记录
      if (request.getType().equals("pourdirect")) {
        ladleAdditionRecord = new LadleAdditionRecord();
        ladleAdditionRecord.setHeatRecordId(request.getHeatRecordId());
        ladleAdditionRecord.setLadleSeq(request.getLadleSeq());
        ladleAdditionRecord.setCCon(request.getC().multiply(HeatRecord.C_UNIT).setScale(2, RoundingMode.HALF_UP));
        if (request.getLadleSeq() == 1) {
          ladleAdditionRecord.setSiCon(request.getSi().multiply(HeatRecord.Si_UNIT_L1).setScale(2, RoundingMode.HALF_UP));
          ladleAdditionRecord.setAlCal( HeatRecord.Al_UNIT_L1);
        } else {
          ladleAdditionRecord.setSiCon(request.getSi().multiply(HeatRecord.Si_UNIT_L234).setScale(2, RoundingMode.HALF_UP));
          ladleAdditionRecord.setAlCal( HeatRecord.Al_UNIT_L234);
        }
        ladleAdditionRecord.setMnCon(request.getMn().multiply(HeatRecord.Mn_UNIT).setScale(2, RoundingMode.HALF_UP));
        ladleAdditionRecord.setPourdirectTime(new Date());
        ladleAdditionRecord.setCreateTime(new Date());
        ladleAdditionRecord.setPourdirectId(request.getOperatorId());
      }
     // throw PlatformException.badRequestException("发送数据失败！");
    }
    ladleAdditionRecordRepository.save(ladleAdditionRecord);
    return ladleAdditionRecord.getId();
  }

  public Object findIdsNewest() {
    Object res = ladleAdditionRecordRepository.findIdsNewest();
    return res == null ? null : res;
  }

  // 将浇注指导确认后的添加值发送到备料工显示
  public LadleAddsResponse findLadleAddsForDisplay(Integer heatRecordId) {
    Map<String, Map<String, BigDecimal>> res = new HashMap<>();
    for (int i = 1; i <= 4; i++) { // 初始化传送值
      Map<String, BigDecimal> ladleAdd = new HashMap<>();
      ladleAdd.put("cCon", null);
      ladleAdd.put("siCon", null);
      ladleAdd.put("mnCon", null);
      ladleAdd.put("alCal", null);
      ladleAdd.put("cAct", null);
      ladleAdd.put("siAct", null);
      ladleAdd.put("mnAct", null);
      ladleAdd.put("alAct", null);
      res.put("L" + i, ladleAdd);
    }
    LadleAddsResponse response = LadleAddsResponse.builder().heatRecordId(heatRecordId).build();
    List<LadleAdditionRecord> list = ladleAdditionRecordRepository.findByHeatRecordId(heatRecordId);
    if (!list.isEmpty()) {
      for (int i = 0; i < list.size(); i++) {
        Map<String, BigDecimal> temp = new HashMap<>();
        temp.put("cCon", list.get(i).getCCon());
        temp.put("siCon", list.get(i).getSiCon());
        temp.put("mnCon", list.get(i).getMnCon());
        temp.put("alCal", list.get(i).getAlCal());
        temp.put("cAct", list.get(i).getCAct());
        temp.put("siAct", list.get(i).getSiAct());
        temp.put("mnAct", list.get(i).getMnAct());
        temp.put("alAct", list.get(i).getAlAct());
        res.put("L" + list.get(i).getLadleSeq(), temp);
      }
    }
    response.setLadleAdds(res);
    return response;
  }

  public ChemistryDetail findChemiNewest(Integer heatRecordId) {
    Optional<ChemistryDetail> chemistryDetailOpt = chemistryDetailRepository.findChemiByHeatRecordId(heatRecordId);
    return chemistryDetailOpt.map(chemistryDetail -> {
      chemistryDetail.computeOtherChemistry();
      return chemistryDetail;
    }).orElse(null);
  }
}
