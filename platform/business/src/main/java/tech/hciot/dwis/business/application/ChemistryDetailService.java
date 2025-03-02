package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.CommonUtil.getDoubleValue;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.ExcelUtil.createCell;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.CHEMISTRY_EXPORT_FAILED;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.domain.ChemicalControlRepository;
import tech.hciot.dwis.business.domain.ChemistryDetailRepository;
import tech.hciot.dwis.business.domain.HeatRecordRepository;
import tech.hciot.dwis.business.domain.LabKeepaliveRepository;
import tech.hciot.dwis.business.domain.LadleRecordRepository;
import tech.hciot.dwis.business.domain.model.ChemicalControl;
import tech.hciot.dwis.business.domain.model.ChemistryDetail;
import tech.hciot.dwis.business.domain.model.LabKeepalive;
import tech.hciot.dwis.business.domain.model.RollTip;
import tech.hciot.dwis.business.infrastructure.ExcelUtil;
import tech.hciot.dwis.business.interfaces.dto.ChemistryDetailResponse;

@Service
@Slf4j
public class ChemistryDetailService {

  private static final Integer LAB_STATUS_UP = 1; // 本地化验室程序状态正常
  private static final Integer LAB_STATUS_DOWN = 0; // 本地化验室程序状态异常

  private Long lastKeepAliveTimestamp = System.currentTimeMillis();

  private List<SseEmitter> sseEmitterList = new ArrayList<>();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private ChemicalControlRepository chemicalControlRepository;

  @Autowired
  private ChemistryDetailRepository chemistryDetailRepository;

  @Autowired
  private HeatRecordRepository heatRecordRepository;

  @Autowired
  private LadleRecordRepository ladleRecordRepository;

  @Autowired
  private LabKeepaliveRepository labKeepaliveRepository;

  private Map<String, List<ChemicalControl>> cache = new HashMap<>();

  public Page<ChemistryDetailResponse> find(String startDate, String endDate, Integer currentPage, Integer pageSize) {
    Date start = parseDate(startDate);
    Date end = parseDate(endDate);

    List<ChemistryDetailResponse> showList = new ArrayList<>();
    Map<String, ChemistryDetailResponse> tempMap = new LinkedHashMap<>();

    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    String sql = "SELECT DISTINCT t1.ladle_id,t2.design,t1.c,t1.si,t1.mn,t1.p,t1.s,t1.cr,t1.ni,t1.w,t1.v,t1.mo,t1.ti,"
            + "t1.cu,t1.al,t1.b,t1.co,t1.sn,t1.pb,t1.[as],t1.sb,t1.bi,t1.nb,t1.ca,t1.mg,t1.ce,t1.n,t1.zr,"
            + "t1.bs,t1.ns,t1.nt,t1.fe,t1.create_date "
            + "FROM chemistry_detail t1 "
            + "INNER JOIN wheel_record t2 on t2.ladle_id = t1.ladle_id "
            + "WHERE t1.create_date >= :startDate AND t1.create_date < :endDate AND t1.ladle_id IS NOT NULL "
            + "ORDER BY t1.create_date ASC";

    String sql1 = "SELECT t1.ladle_id,t3.ladle_record_key,t2.wheel_serial,t2.design,t2.scrap_code,t1.c,t1.si,t1.mn,t1.p,t1.s,t1.cr,"
            + "t1.ni,t1.w,t1.v,t1.mo,t1.ti,t1.cu,t1.al,t1.b,t1.co,t1.sn,t1.pb,t1.[as],t1.sb,t1.bi,t1.nb,t1.ca,t1.mg,t1.ce,t1.n,t1.zr,"
            + "t1.bs,t1.ns,t1.nt,t1.fe "
            + "FROM chemistry_detail t1 "
            + "INNER JOIN ladle_record t3 ON t3.id = t1.ladle_id "
            + "INNER JOIN wheel_record t2 ON t3.id = t2.ladle_id WHERE t2.confirmed_scrap = 0 AND t1.ladle_id IN (:ladleId) "
            + "ORDER BY t1.create_date ASC";
    while (!start.after(end)) {
      Map<String, Object> params = new HashMap<>();
      params.put("startDate", start);
      params.put("endDate", DateUtils.addDays(start, 1));
      List<Integer> ladleIdList = template.queryForList(sql, params).stream().map(
          row -> {
            ChemistryDetailResponse chemistryDetailResponse =
                ChemistryDetailResponse.builder()
                    .ladleId((Integer) row.get("ladle_id"))
                    .design((String) row.get("design"))
                    .c((BigDecimal) row.get("c"))
                    .si((BigDecimal) row.get("si"))
                    .mn((BigDecimal) row.get("mn"))
                    .p((BigDecimal) row.get("p"))
                    .s((BigDecimal) row.get("s"))
                    .cr((BigDecimal) row.get("cr"))
                    .ni((BigDecimal) row.get("ni"))
                    .w((BigDecimal) row.get("w"))
                    .v((BigDecimal) row.get("v"))
                    .mo((BigDecimal) row.get("mo"))
                    .ti((BigDecimal) row.get("ti"))
                    .cu((BigDecimal) row.get("cu"))
                    .al((BigDecimal) row.get("al"))
                    .b((BigDecimal) row.get("b"))
                    .co((BigDecimal) row.get("co"))
                    .sn((BigDecimal) row.get("sn"))
                    .pb((BigDecimal) row.get("pb"))
                    .as((BigDecimal) row.get("as"))
                    .sb((BigDecimal) row.get("sb"))
                    .bi((BigDecimal) row.get("bi"))
                    .nb((BigDecimal) row.get("nb"))
                    .ca((BigDecimal) row.get("ca"))
                    .mg((BigDecimal) row.get("mg"))
                    .ce((BigDecimal) row.get("ce"))
                    .n((BigDecimal) row.get("n"))
                    .zr((BigDecimal) row.get("zr"))
                    .bs((BigDecimal) row.get("bs"))
                    .ns((BigDecimal) row.get("ns"))
                    .nt((BigDecimal) row.get("nt"))
                    .fe((BigDecimal) row.get("fe"))
                    .build();
            return chemistryDetailResponse;
          }).filter(
          chemistryDetailResponse -> !isChemistryStandard(chemistryDetailResponse) || isChemistryControl(chemistryDetailResponse))
          .map(chemistryDetailResponse -> chemistryDetailResponse.getLadleId()).distinct()
          .collect(Collectors.toList());

      if (ladleIdList.size() == 0) {
        start = DateUtils.addDays(start, 1);
        continue;
      }

      params.put("ladleId", ladleIdList);
      template.queryForList(sql1, params).forEach(
          row -> {
            ChemistryDetailResponse chemistryDetailResponse =
                ChemistryDetailResponse.builder()
                    .ladleId((Integer) row.get("ladle_id"))
                    .ladleRecordKey((String) row.get("ladle_record_key"))
                    .wheelSerial((String) row.get("wheel_serial"))
                    .design((String) row.get("design"))
                    .scrapCode((String) row.get("scrap_code"))
                    .c((BigDecimal) row.get("c"))
                    .si((BigDecimal) row.get("si"))
                    .mn((BigDecimal) row.get("mn"))
                    .p((BigDecimal) row.get("p"))
                    .s((BigDecimal) row.get("s"))
                    .cr((BigDecimal) row.get("cr"))
                    .ni((BigDecimal) row.get("ni"))
                    .w((BigDecimal) row.get("w"))
                    .v((BigDecimal) row.get("v"))
                    .mo((BigDecimal) row.get("mo"))
                    .ti((BigDecimal) row.get("ti"))
                    .cu((BigDecimal) row.get("cu"))
                    .al((BigDecimal) row.get("al"))
                    .b((BigDecimal) row.get("b"))
                    .co((BigDecimal) row.get("co"))
                    .sn((BigDecimal) row.get("sn"))
                    .pb((BigDecimal) row.get("pb"))
                    .as((BigDecimal) row.get("as"))
                    .sb((BigDecimal) row.get("sb"))
                    .bi((BigDecimal) row.get("bi"))
                    .nb((BigDecimal) row.get("nb"))
                    .ca((BigDecimal) row.get("ca"))
                    .mg((BigDecimal) row.get("mg"))
                    .ce((BigDecimal) row.get("ce"))
                    .n((BigDecimal) row.get("n"))
                    .zr((BigDecimal) row.get("zr"))
                    .bs((BigDecimal) row.get("bs"))
                    .ns((BigDecimal) row.get("ns"))
                    .nt((BigDecimal) row.get("nt"))
                    .fe((BigDecimal) row.get("fe"))
                    .build();
            chemistryDetailResponse.computeOtherChemistry();
            chemistryDetailResponse.setHighlight(getHighlightElement(chemistryDetailResponse));
            chemistryDetailResponse.setYellowLight(getYellowElement(chemistryDetailResponse));
            tempMap
                .put(chemistryDetailResponse.getLadleRecordKey() + chemistryDetailResponse.getWheelSerial(),
                    chemistryDetailResponse);
          });
      start = DateUtils.addDays(start, 1);
    }

    showList.addAll(tempMap.values());
    PagedListHolder pagedListHolder = new PagedListHolder(showList);
    if (pageSize != null) {
      pagedListHolder.setPage(currentPage);
      pagedListHolder.setPageSize(pageSize);
    }
    return new PageImpl<>(
        pageSize == null ? showList : pagedListHolder.getPageList(),
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize),
        showList.size());
  }

  private List<String> getYellowElement(ChemistryDetailResponse chemistryDetailResponse) {
    List<String> result = new ArrayList<>();
    Map<String, BigDecimal> labValueMap = getValueMap(chemistryDetailResponse);
    String design = chemistryDetailResponse.getDesign();
    List<ChemicalControl> chemicalControlList = getChemicalControls(design);

    for (ChemicalControl control : chemicalControlList) {
      String element = control.getElementAbbr().toLowerCase();
      if ("c".equals(element) || "si".equals(element) || "mn".equals(element)) {
        BigDecimal c = labValueMap.get(element);
        if (c.compareTo(control.getCinValueMin()) >= 0
          && c.compareTo(control.getCinValueMax()) <= 0) {
          result.add(element);
        }
      }
    }
    return result;
  }

  public Page<ChemistryDetail> findLab(String furnaceSeq,
      String sampleNo,
      Integer currentPage,
      Integer pageSize) {
    Specification<ChemistryDetail> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (furnaceSeq != null) {
        list.add(criteriaBuilder.equal(root.get("furnaceSeq"), furnaceSeq));
      }
      if (sampleNo != null) {
        list.add(criteriaBuilder.equal(root.get("sampleNo"), sampleNo));
      }
      if (ObjectUtil.isAllEmpty(furnaceSeq, sampleNo)) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return chemistryDetailRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  // 熔炼加料页的化学元素信息
  public List<ChemistryDetail> findLabForFurnace(Integer furnaceNo, Integer furnaceSeq, String year) {
    Specification<ChemistryDetail> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      list.add(criteriaBuilder.equal(root.get("furnaceSeq"), furnaceNo + "-" + furnaceSeq));

      Date beginDate = StandardTimeUtil.parseDate(year + "-01-01");
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), beginDate));

      Date endDate = StandardTimeUtil.parseDate((Integer.valueOf(year) + 1) + "-01-01");
      list.add(criteriaBuilder.lessThan(root.get("createDate"), endDate));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.asc(root.get("createDate")));
      return query.getRestriction();
    };
    List<ChemistryDetail> chemistryDetailList = chemistryDetailRepository.findAll(specification);
    chemistryDetailList.forEach(ChemistryDetail::computeOtherChemistry);
    return chemistryDetailList;
  }

  public Optional<ChemistryDetail> findByLadleId(Integer ladleId) {
    return chemistryDetailRepository.findByLadleId(ladleId);
  }

  /*
     AAR1的值为Cr+Ni+Mo+Cu的总和，总和值大于0.5时，显示红色字体
     TB1的值为Cr+Ni+Mo的总和，总和值大于0.5时，显示红色字体
     TB2的值为Cr+Ni+Mo+Cu+Sn+V+Ti+Nb的总和，总和值大于1时，显示红色字体
     AAR2的值为计算得出，计算公式为
     930-570*[C]/100-80*[Mn]/100-20*[Si]/100-50*[Cr]/100-30*[Ni]/100-20*([Mo]/100+[V]/100)
     简化成以下公式：
     930 - (570 * C + 80 * Mn + 50 * Cr + 30 * Ni + 20 * Si + 20 * Mo + 20 * V) / 100
     公式有变化，不除100了
   */
  public ChemistryDetail findNewest(Integer furnaceNo, String location) {
    Optional<ChemistryDetail> chemistryDetailOpt;
    if ("furnace".equals(location)) {
      chemistryDetailOpt = chemistryDetailRepository.findNewestFurnaceLab(furnaceNo + "-");
    } else {
      chemistryDetailOpt = chemistryDetailRepository.findNewestPourLab(furnaceNo + "-");
    }
    return chemistryDetailOpt.map(chemistryDetail -> {
      chemistryDetail.computeOtherChemistry();
      return chemistryDetail;
    }).orElse(null);
  }

  public List<ChemistryDetail> findHistory(String furnaceSeq, String location) {
    List<ChemistryDetail> chemistryDetailList;
    if ("furnace".equals(location)) {
      chemistryDetailList = chemistryDetailRepository.findFurnaceLabHis(furnaceSeq);
    } else {
      chemistryDetailList = chemistryDetailRepository.findPourLabHis(furnaceSeq);
    }
    chemistryDetailList.forEach(chemistryDetail -> {
      chemistryDetail.computeOtherChemistry();
    });
    return chemistryDetailList;
  }

  // 化学成分是否标准，如果是则返回true，否则返回false
  public boolean isChemistryStandard(String design, ChemistryDetail chemistryDetail) {
    Map<String, BigDecimal> labValueMap = getValueMap(chemistryDetail);
    List<ChemicalControl> chemicalControlList = getChemicalControls(design);
    for (ChemicalControl control : chemicalControlList) {
      String element = control.getElementAbbr().toLowerCase();
      if ("cr,mo,ni".equals(element)) {
        BigDecimal crMoNi = chemistryDetail.getCr().add(chemistryDetail.getMo()).add(chemistryDetail.getNi());
        if (crMoNi.compareTo(control.getCstValueMin()) < 0
          || crMoNi.compareTo(control.getCstValueMax()) > 0) {
          return false;
        }
      }
      if ("cr,mo,ni,cu,v".equals(element)) {
        BigDecimal crMoNiCuV = chemistryDetail.getCr().add(chemistryDetail.getMo())
          .add(chemistryDetail.getNi()).add(chemistryDetail.getCu()).add(chemistryDetail.getV());
        if (crMoNiCuV.compareTo(control.getCstValueMin()) < 0
          || crMoNiCuV.compareTo(control.getCstValueMax()) > 0) {
          return false;
        }
      }
      BigDecimal value = labValueMap.get(element);
      if (value != null) {
        if (value.compareTo(control.getCstValueMin()) < 0
          || value.compareTo(control.getCstValueMax()) > 0) {
          return false;
        }
      }
    }
    return true;
  }

  // 化学成分C、Si、Mn是否落在内控值里，如果是，则返回true，否则返回false
  public boolean isInChemistryControl(String design, ChemistryDetail chemistryDetail) {
    Map<String, BigDecimal> labValueMap = getValueMap(chemistryDetail);
    List<ChemicalControl> chemicalControlList = getChemicalControls(design);
    for (ChemicalControl control : chemicalControlList) {
      String element = control.getElementAbbr().toLowerCase();
      if ("c".equals(element) || "si".equals(element) || "mn".equals(element)) {
        BigDecimal c = labValueMap.get(element);
        if (c.compareTo(control.getCinValueMin()) >= 0
            && c.compareTo(control.getCinValueMax()) <= 0) {
          return true;
        }
      }
    }
    return false;
  }

  private List<String> getHighlightElement(ChemistryDetailResponse chemistryDetailResponse) {
    List<String> result = new ArrayList<>();
    Map<String, BigDecimal> labValueMap = getValueMap(chemistryDetailResponse);
    String design = chemistryDetailResponse.getDesign();
    List<ChemicalControl> chemicalControlList = getChemicalControls(design);

    for (ChemicalControl control : chemicalControlList) {
      String element = control.getElementAbbr().toLowerCase();
      if ("cr,mo,ni".equals(element)) {
        element = "tb1";
      } else if ("cr,mo,ni,cu,v".equals(element)) {
        element = "tb3";
      }
      BigDecimal value = labValueMap.get(element);
      if (value != null) {
        if (value.compareTo(control.getCstValueMin()) < 0
          || value.compareTo(control.getCstValueMax()) > 0) {
          result.add(element);
        }
      }
    }
    BigDecimal tb2 = labValueMap.get("tb2");
    if (tb2.compareTo(new BigDecimal(0.5)) > 0) {
      result.add("tb2");
    }
    BigDecimal aar1 = labValueMap.get("aar1");
    if (aar1.compareTo(new BigDecimal(0.5)) > 0) {
      result.add("aar1");
    }
    BigDecimal aar2 = labValueMap.get("aar2");
    if (aar2.compareTo(new BigDecimal(390)) <= 0) {
      result.add("aar2");
    }
    return result;
  }

  private List<ChemicalControl> getChemicalControls(String design) {
    List<ChemicalControl> chemicalControlList;
    if (!cache.containsKey(design)) {
      chemicalControlList = chemicalControlRepository.findByDesign(design);
      cache.put(design, chemicalControlList);
    } else {
      chemicalControlList = cache.get(design);
    }
    return chemicalControlList;
  }

  private <T> Map<String, BigDecimal> getValueMap(T obj) {
    Map<String, BigDecimal> labValueMap = new HashMap<>();
    Field[] fields = obj.getClass().getDeclaredFields();
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        if (field.getType().getName().equals("java.math.BigDecimal")) {
          String name = field.getName();
          BigDecimal value = (BigDecimal) field.get(obj);
          if (value != null) {
            labValueMap.put(name, value);
          } else {
            labValueMap.put(name, new BigDecimal(0));
          }
        }
      } catch (IllegalAccessException e) {
        log.error(e.getMessage());
      }
    }
    return labValueMap;
  }

  private boolean isChemistryStandard(ChemistryDetailResponse chemistryDetailResponse) {
    ChemistryDetail chemistryDetail = new ChemistryDetail();
    BeanUtils.copyProperties(chemistryDetailResponse, chemistryDetail);
    return isChemistryStandard(chemistryDetailResponse.getDesign(), chemistryDetail);
  }

  private boolean isChemistryControl(ChemistryDetailResponse chemistryDetailResponse) {
    ChemistryDetail chemistryDetail = new ChemistryDetail();
    BeanUtils.copyProperties(chemistryDetailResponse, chemistryDetail);
    return isInChemistryControl(chemistryDetailResponse.getDesign(), chemistryDetail);
  }

  public void export(String startDate, String endDate, HttpServletResponse response) {
    List<ChemistryDetailResponse> chemistryDetailResponseList =
        find(startDate, endDate, null, null).getContent();
    Workbook workbook = null;
    try {
      workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet(startDate + "-" + endDate);

      String fileName = "化学成分超标" + ".xlsx";

      String[] headers = {
          "小包关键字",
          "车轮序列号",
          "轮型",
          "废品代码",
          "c",
          "si",
          "mn",
          "p",
          "s",
          "cr",
          "ni",
          "w",
          "v",
          "mo",
          "ti",
          "cu",
          "al",
          "b",
          "co",
          "sn",
          "pb",
          "as",
          "sb",
          "bi",
          "nb",
          "ca",
          "mg",
          "ce",
          "n",
          "zr",
          "bs",
          "ns",
          "nt",
          "fe",
          "aar1",
          "aar2",
          "tb1",
          "tb2",
          "内控"
      };

      // 列宽数组
      Integer[] lengthArray = ExcelUtil.createColumnWidthArray(headers);

      // 标题
      ExcelUtil.createTitleRow(sheet, headers);

      // 内容
      int rowNum = 1;
      for (ChemistryDetailResponse chemistryDetailResponse : chemistryDetailResponseList) {
        Row row1 = sheet.createRow(rowNum);
        int columnNum = 0;
        createCell(row1, columnNum++, chemistryDetailResponse.getLadleRecordKey(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getWheelSerial(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getDesign(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getScrapCode(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getC(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getSi(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getMn(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getP(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getS(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getCr(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getNi(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getW(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getV(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getMo(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getTi(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getCu(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getAl(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getB(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getCo(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getSn(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getPb(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getAs(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getSb(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getBi(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getNb(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getCa(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getMg(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getCe(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getN(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getZr(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getBs(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getNs(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getNt(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getFe(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getAar1(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getAar2(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getTb1(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getTb2(), lengthArray);
        createCell(row1, columnNum++, chemistryDetailResponse.getTb3(), lengthArray);
        rowNum++;
      }

      // 自动调整列宽
      ExcelUtil.autoSizeColumnWidth(sheet, lengthArray);

      response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      response.setHeader("Content-disposition", "attachment;filename=" + fileName);
      response.flushBuffer();

      workbook.write(response.getOutputStream());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw CHEMISTRY_EXPORT_FAILED.getPlatformException();
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
        }
      }
    }
  }

  @Transactional
  public ChemistryDetail add(ChemistryDetail request) {
    ChemistryDetail chemistryDetail = ChemistryDetail.builder().build();
    BeanUtil.copyProperties(request, chemistryDetail, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

    if (!Pattern.compile("[0-9]+-[0-9]+").matcher(chemistryDetail.getFurnaceSeq()).matches()) {
      throw PlatformException.badRequestException("furnaceSeq format error: " + chemistryDetail.getFurnaceSeq());
    }
    if (!Pattern.compile("[0-9]+-.+").matcher(chemistryDetail.getSampleNo()).matches()) {
      throw PlatformException.badRequestException("sampleNo format error: " + chemistryDetail.getSampleNo());
    }
    // 如果相同labId的记录已经存在，则可能是重复上传，忽略这条记录
    if (chemistryDetailRepository.findByLabId(chemistryDetail.getLabId()).isPresent()) {
      return chemistryDetail;
    }
    chemistryDetail.setCreateDate(new Date());
    chemistryDetail.setLabDate(StandardTimeUtil.dateStr(chemistryDetail.getCreateDate()));
    chemistryDetail.setLabTime(StandardTimeUtil.timeStr(chemistryDetail.getCreateDate()));
    updateHeatRecordId(chemistryDetail);
    updateLadleId(chemistryDetail);
    chemistryDetailRepository.save(chemistryDetail);
    checkSeqNoRepeat(chemistryDetail.getFurnaceSeq(), chemistryDetail.getSampleNo());
    log.info("receive chemistryDetail: {}", chemistryDetail);
    for (Iterator<SseEmitter> iterator = sseEmitterList.iterator(); iterator.hasNext(); ) {
      SseEmitter sseEmitter = iterator.next();
      try {
        sseEmitter.send(JSONObject.toJSON(chemistryDetail));
      } catch (IOException e) {
        sseEmitter.completeWithError(e);
        iterator.remove();
        log.error("send sse failed: {}", e.getMessage());
      }
    }
    return chemistryDetail;
  }

  // 根据FurnaceSeq炉号字段的值，如：FurnaceSeq=1-125  1表示炉号，125表示炉次，
  // 在heat_record表中查询24小时内的，Furnace_No（炉号）等于1的、Heat_Seq#（炉次）等于125的数据，
  // 获取该数据的Heat_record_id，更新到chemistry_detail（服务器化学成分表）的Heat_record_id字段
  @Transactional
  private void updateHeatRecordId(ChemistryDetail chemistry) {
    String furnaceSeq = chemistry.getFurnaceSeq();
    String[] furnaceNoAndHeatSeq = furnaceSeq.split("-");
    try {
      Integer furnaceNo = Integer.parseInt(furnaceNoAndHeatSeq[0]);
      Integer heatSeq = Integer.parseInt(furnaceNoAndHeatSeq[1]);
      Date last24 = DateUtils.addHours(new Date(), -24);
      String last24Str = DateUtil.format(last24, "yyyy-MM-dd HH:mm:ss");
      heatRecordRepository.findHeatRecordIdForLab(furnaceNo, heatSeq, last24Str).ifPresent(chemistry::setHeatRecordId);
      if (chemistry.getHeatRecordId() != null) {
        Date last3Day = DateUtils.addHours(new Date(), -72);
        String last3DayStr = DateUtil.format(last3Day, "yyyy-MM-dd HH:mm:ss");
        chemistryDetailRepository.updateFurnaceSeqByHeatRecordId(furnaceSeq, chemistry.getHeatRecordId(), last3DayStr);
      }
    } catch (Exception e) {
      log.error("parse furnaceNo and heatSeq error: {}", e.getMessage());
    }
  }

  // 依次解析这些数据的SampleNo样号字段的值，如：SampleNo等于3-L2或2-P1，
  // 当SampleNo的值带有L时，截取L后的数字作为Ladle_Seq#（小包序号），
  // 根据该数据的Heat_record_id的值查询Ladle_Record表中Heat_record_id相同的，
  // 且L后的数字与Ladle_Record表的Ladle_Seq#字段相同的Ladle_Record表的数据，
  // 将该数据的Ladle_id的值更新到chemistry_detail服务器化学成分表 的Ladle_id字段
  private void updateLadleId(ChemistryDetail chemistry) {
    if (chemistry.getHeatRecordId() == null) {
      return;
    }
    String sampleNo = chemistry.getSampleNo();
    if (!sampleNo.contains("-L")) {
      return;
    }
    String[] sampleAndLadleSeq = sampleNo.split("-L");
    if (sampleAndLadleSeq.length == 2) {
      try {
        Integer ladleSeq = Integer.parseInt(sampleAndLadleSeq[1]);
        ladleRecordRepository.findByHeatRecordIdAndLadleSeq(chemistry.getHeatRecordId(), ladleSeq)
            .ifPresent(ladleRecord -> chemistry.setLadleId(ladleRecord.getId()));
      } catch (Exception e) {
        log.error("parse ladleSeq error: {}", e.getMessage());
      }
    }
  }

  // 根据炉号和样号检查3天内是否有重复的，如果有，就把seqNoRepeat更新为1，否则更新为0
  private void checkSeqNoRepeat(String furnaceSeq, String sampleNo) {
    Date last3Day = DateUtils.addHours(new Date(), -2);
    String last3DayStr = StandardTimeUtil.dateStr(last3Day);
    List<ChemistryDetail> chemistryDetailList = chemistryDetailRepository.findLatestBySeqNo(furnaceSeq,
        sampleNo, last3DayStr);
    if (chemistryDetailList.isEmpty()) {
      return;
    }
    if (chemistryDetailList.size() == 1) {
      chemistryDetailList.get(0).setSeqNoRepeat(0);
    } else {
      chemistryDetailList.forEach(chemistryDetail -> {
        chemistryDetail.setSeqNoRepeat(1);
      });
    }
    chemistryDetailRepository.saveAll(chemistryDetailList);
  }

  public void modify(Integer id, ChemistryDetail request) {
    ChemistryDetail chemistryDetail = chemistryDetailRepository.findById(id)
        .orElseThrow(() -> PlatformException.badRequestException("化学成分不存在"));
    String oldFurnaceSeq = chemistryDetail.getFurnaceSeq();
    String oldSampleNo = chemistryDetail.getSampleNo();
    BeanUtil.copyProperties(request, chemistryDetail, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    updateHeatRecordId(chemistryDetail);
    updateLadleId(chemistryDetail);
    chemistryDetailRepository.save(chemistryDetail);
    checkSeqNoRepeat(oldFurnaceSeq, oldSampleNo);
    checkSeqNoRepeat(chemistryDetail.getFurnaceSeq(), chemistryDetail.getSampleNo());
  }

  /**
   * 本地化验室发送保活心跳信息
   */
  public void keepAlive() {
    LabKeepalive labKeepalive = labKeepaliveRepository.findById(1).orElse(LabKeepalive.builder().build());
    labKeepalive.setLastKeepaliveTime(System.currentTimeMillis());
    labKeepaliveRepository.save(labKeepalive);
  }

  /**
   * 如果本地化验室程序最后同步时间是3分钟以前，则判断本地化验室程序运行异常，否则认为是正常
   */
  public Integer labStatus() {
    long lastKeepAliveTimestamp = labKeepaliveRepository.findById(1)
      .orElse(LabKeepalive.builder().build()).getLastKeepaliveTime();
    if (System.currentTimeMillis() - lastKeepAliveTimestamp > 3 * 60 * 1000) {
      String lastSyncTimeStr = DateUtil.format(new Date(lastKeepAliveTimestamp), "yyyy-MM-dd HH:mm:ss");
      log.info("Last keep alive time: {}", lastSyncTimeStr);
      return LAB_STATUS_DOWN;
    }
    return LAB_STATUS_UP;
  }

  public void addSseEmitters(SseEmitter sseEmitter) {
    try {
      sseEmitter.send("");
      log.info("add emitter");
      sseEmitterList.add(sseEmitter);
      sseEmitter.onCompletion(() -> {
        log.info("emitter completion");
        sseEmitterList.remove(sseEmitter);
      });
      sseEmitter.onTimeout(() -> {
        log.error("emitter timeout");
        sseEmitterList.remove(sseEmitter);
      });
      sseEmitter.onError(errorCallBack(sseEmitter));
      log.info("sseEmitterList size: {}", sseEmitterList.size());
    } catch (IOException e) {
      log.error("init sse emitter failed: {}", e.getMessage());
    }
  }

  private Consumer<Throwable> errorCallBack(SseEmitter emitter) {
    return throwable -> {
      log.error("emitter error");
      sseEmitterList.remove(emitter);
    };
  }

  public static void main(String[] args) {
    String str = "1-T";
    //String pattern = "[0-9]+-[0-9]+";
    String pattern = "[0-9]+-.+";
    boolean match = Pattern.compile(pattern).matcher(str).matches();
    log.info("{} match pattern {}: {}", str, pattern, match);
  }

  public List<ChemistryDetail> findByHeatRecordId(Integer heatRecordId) {
    return chemistryDetailRepository.findByHeatRecordId(heatRecordId);
  }

  public void save(ChemistryDetail chemistryDetail) {
    chemistryDetailRepository.save(chemistryDetail);
  }

  public Optional<ChemistryDetail> findById(Integer id) {
    return chemistryDetailRepository.findById(id);
  }

  public ChemistryDetail findRepeatRecord(Integer id) {
    ChemistryDetail detail = chemistryDetailRepository.findById(id).get();
    Date lastDay = DateUtils.addHours(new Date(), -365);
    String lastDayStr = StandardTimeUtil.dateStr(lastDay);
    List<ChemistryDetail> chemistryDetailList = chemistryDetailRepository.findLatestBySeqNo(detail.getFurnaceSeq(),
        detail.getSampleNo(), lastDayStr);
    if (chemistryDetailList.isEmpty()) {
      return null;
    }
    for (ChemistryDetail chemistryDetail : chemistryDetailList) {
      if (chemistryDetail.getId() != id) {
        return chemistryDetail;
      }
    }
    return null;
  }
}
