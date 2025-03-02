package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseDate;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_IS_FINISHED;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_IS_SCRAP;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.XRAY_PARAM_ERROR;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.business.domain.InspectionRecordRepository;
import tech.hciot.dwis.business.domain.OperatingTimeCtrRepository;
import tech.hciot.dwis.business.domain.PourRecordRepository;
import tech.hciot.dwis.business.domain.model.InspectionRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.interfaces.dto.XRayCheckData;
import tech.hciot.dwis.business.interfaces.dto.XRayCheckResponse;

@Service
public class InspectionRecordService {

  @Autowired
  private InspectionRecordRepository inspectionRecordRepository;

  @Autowired
  private OperatingTimeCtrRepository operatingTimeCtrRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  public Page<InspectionRecord> find(String inspectorId, String wheelSerial, String testCode, String holdCode, String heatCode,
      String scrapCode, String reworkCode, Integer xrayReq, String opeTime, Integer currentPage, Integer pageSize) {
    Specification<InspectionRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(testCode)) {
        list.add(criteriaBuilder.equal(root.get("testCode"), testCode));
      }
      if (StringUtils.isNotEmpty(holdCode)) {
        list.add(criteriaBuilder.equal(root.get("holdCode"), holdCode));
      }
      if (StringUtils.isNotEmpty(heatCode)) {
        list.add(criteriaBuilder.equal(root.get("heatCode"), heatCode));
      }
      if (StringUtils.isNotEmpty(scrapCode)) {
        list.add(criteriaBuilder.equal(root.get("scrapCode"), scrapCode));
      }
      if (StringUtils.isNotEmpty(reworkCode)) {
        list.add(criteriaBuilder.equal(root.get("reworkCode"), reworkCode));
      }
      if (xrayReq != null) {
        list.add(criteriaBuilder.equal(root.get("xrayReq"), xrayReq));
      }
      if (StringUtils.isNotBlank(opeTime)) {
        Date opeDate = parseDate(opeTime);
        list.add(criteriaBuilder.equal(root.get("createDate"), opeDate));
      }
      if (StringUtils.isAllBlank(wheelSerial, testCode, holdCode, heatCode, scrapCode, reworkCode, opeTime) && xrayReq == null) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return inspectionRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void add(InspectionRecord inspectionRecord) {
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      inspectionRecord.setOpeDT(ope);
    });
    Integer maxTs = inspectionRecordRepository.getMaxTs(inspectionRecord.getWheelSerial());
    inspectionRecord.setTs(maxTs == null ? 1 : maxTs + 1);
    inspectionRecord.setCreateDate(new Date());
    inspectionRecordRepository.save(inspectionRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(inspectionRecord.getWheelSerial());
    wheelRecord.setHeatCode(inspectionRecord.getHeatCode());
    wheelRecord.setTestCode(inspectionRecord.getTestCode());
    wheelRecord.setHoldCode(inspectionRecord.getHoldCode());
    wheelRecord.setScrapCode(inspectionRecord.getScrapCode());
    wheelRecord.setReworkCode(inspectionRecord.getReworkCode());
    wheelRecord.setBrinReq(inspectionRecord.getBrinReq());
    wheelRecord.setSpecialUltra(inspectionRecord.getSpecialUltra());
    wheelRecord.setXrayReq(inspectionRecord.getXrayReq());
    wheelRecord.setSpecialMt(inspectionRecord.getSpecialMt());
    wheelRecordService.save(wheelRecord);
  }

  public Page<InspectionRecord> findXRay(String inspectorId, String wheelSerial, String xrayResult, String scrapResult,
      Integer currentPage, Integer pageSize) {
    Specification<InspectionRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(inspectorId)) {
        list.add(criteriaBuilder.equal(root.get("inspectorId"), inspectorId));
      }
      if (StringUtils.isNotEmpty(wheelSerial)) {
        list.add(criteriaBuilder.equal(root.get("wheelSerial"), wheelSerial));
      }
      if (StringUtils.isNotEmpty(xrayResult)) {
        list.add(criteriaBuilder.equal(root.get("xrayResult"), xrayResult));
      }
      if (StringUtils.isNotEmpty(scrapResult)) {
        list.add(criteriaBuilder.equal(root.get("scrapResult"), scrapResult));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createDate")));
      return query.getRestriction();
    };
    return inspectionRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void addXRay(InspectionRecord inspectionRecord) {
    if ((inspectionRecord.getXrayResult().equals("1A") || inspectionRecord.getXrayResult().equals("2A")) && StringUtils
        .isNotEmpty(inspectionRecord.getScrapResult())) {
      throw XRAY_PARAM_ERROR.getPlatformException();
    }
    if (inspectionRecord.getXrayResult().equals("3A")) {
      inspectionRecord.setXrayReq(1);
      inspectionRecord.setScrapCode("7");
    } else {
      inspectionRecord.setXrayReq(0);
    }
    operatingTimeCtrRepository.findByDep("QA").ifPresent(operatingTimeCtr -> {
      int minute = operatingTimeCtr.getOperatingTime();
      Date ope = DateUtils.addMinutes(new Date(), -minute);
      inspectionRecord.setOpeDT(ope);
    });
    inspectionRecord.setCreateDate(new Date());
    inspectionRecordRepository.save(inspectionRecord);

    WheelRecord wheelRecord = wheelRecordService.findWheel(inspectionRecord.getWheelSerial());
    wheelRecord.setXrayReq(inspectionRecord.getXrayReq());
    if ("7".equals(inspectionRecord.getScrapCode())) {
      wheelRecord.setScrapCode("7");
    }
    wheelRecordService.save(wheelRecord);
  }

  public Page<XRayCheckResponse> findXRayCheckList(String castDate, Integer currentPage, Integer pageSize) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    params.put("date", castDate.substring(0, 10));

    String sql = "SELECT t1.id AS ladle_id,t1.cast_date,t1.furnace_no,t1.heat_seq,t1.tap_seq,t1.ladle_seq,"
        + "t1.ladle_temp,t1.pour_time,chemistry_detail.c "
        + "FROM (SELECT heat_record.cast_date,heat_record.furnace_no,heat_record.heat_seq,heat_record.tap_seq,"
        + "ladle_record.id,ladle_record.ladle_seq,ladle_record.ladle_temp,"
        + "DATEDIFF(MINUTE,MIN(Pour_Record.pour_d_t),MAX(Pour_Record.pour_d_t)) AS pour_time "
        + "FROM pour_record INNER JOIN ladle_record ON pour_record.ladle_id = ladle_record.id "
        + "INNER JOIN heat_record ON heat_record.id = ladle_record.heat_record_id "
        + "WHERE heat_record.cast_date = :date "
        + "GROUP BY "
        + "heat_record.cast_date,heat_record.furnace_no,heat_record.heat_seq,heat_record.tap_seq,"
        + "ladle_record.id,ladle_record.ladle_seq,ladle_record.ladle_temp) t1 "
        + "INNER JOIN chemistry_detail ON t1.id = chemistry_detail.ladle_id "
        + "ORDER BY t1.tap_seq ASC,t1.ladle_seq ASC";
    List<XRayCheckResponse> xRayCheckResponseList = template
        .query(sql, params, BeanPropertyRowMapper.newInstance(XRayCheckResponse.class));
    xRayCheckResponseList.forEach(xRayCheckResponse -> {
      xRayCheckResponse.setDesign(StringUtils.join(
          pourRecordRepository.findByLadleId(xRayCheckResponse.getLadleId()).stream().map(pourRecord -> pourRecord.getDesign())
              .distinct().collect(Collectors.toList()), ","));
    });
    PagedListHolder pagedListHolder = new PagedListHolder(xRayCheckResponseList);
    pagedListHolder.setPageSize(pageSize);
    pagedListHolder.setPage(currentPage);
    return new PageImpl<XRayCheckResponse>(pagedListHolder.getPageList(), PageRequest.of(currentPage, pageSize),
        xRayCheckResponseList.size());
  }

  public List<XRayCheckData> findXRayCheckData(Integer ladleId) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    params.put("ladleId", ladleId);

    String sql =
        "SELECT ladle_record_key,wheel_record.wheel_serial,wheel_record.design,confirmed_scrap,finished,wheel_record.xray_req "
            + "FROM wheel_record "
            + "INNER JOIN ladle_record ON wheel_record.ladle_id = ladle_record.id "
            + "INNER JOIN pour_record ON pour_record.wheel_serial = wheel_record.wheel_serial "
            + "WHERE ladle_record.id = :ladleId "
            + "ORDER BY pour_record.record_created ASC";
    List<XRayCheckData> xRayCheckDataList = template
        .query(sql, params, BeanPropertyRowMapper.newInstance(XRayCheckData.class));
    return xRayCheckDataList;
  }

  @Transactional
  public void xRayCheck(XRayCheckData xRayCheckData) {
    wheelRecordService.findByWheelSerial(xRayCheckData.getWheelSerial()).ifPresent(wheelRecord -> {
      if (wheelRecord.getConfirmedScrap() == 1) {
        throw WHEEL_IS_SCRAP.getPlatformException();
      }
      if (wheelRecord.getFinished() == 1) {
        throw WHEEL_IS_FINISHED.getPlatformException();
      }
      int xrayReq = xRayCheckData.getXrayReq() == 0 ? 1 : 0;
      wheelRecord.setXrayReq(xrayReq);
      wheelRecordService.save(wheelRecord);
      Date opeDT = operatingTimeCtrService.getQAOperatingTime();

      inspectionRecordRepository
          .save(InspectionRecord.builder().inspectorId(xRayCheckData.getInspectorId()).wheelSerial(xRayCheckData.getWheelSerial())
              .xrayReq(xrayReq).ts(inspectionRecordRepository.getMaxTs(xRayCheckData.getWheelSerial()) + 1)
              .opeDT(opeDT).build());
    });
  }
}
