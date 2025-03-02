package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.CommonUtil.getStringValue;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_SERIAL_NOT_EXIST;

import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.HeatRepository;
import tech.hciot.dwis.business.domain.MecRecordRepository;
import tech.hciot.dwis.business.domain.WheelRecordRepository;
import tech.hciot.dwis.business.domain.XNReleaseRecordRepository;
import tech.hciot.dwis.business.domain.model.MecRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;
import tech.hciot.dwis.business.domain.model.XNReleaseRecord;
import tech.hciot.dwis.business.interfaces.dto.WheelRecordMecResponse;
import tech.hciot.dwis.business.interfaces.dto.XNWheelResponse;

@Service
@Slf4j
public class MecRecordService {

  @Autowired
  private WheelRecordRepository wheelRecordRepository;

  @Autowired
  private MecRecordRepository mecRecordRepository;

  @Autowired
  private XNReleaseRecordRepository xnReleaseRecordRepository;

  @Autowired
  private HeatRepository heatRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<String> getMecSerialList(String type) {
    List<String> mecSerialList = null;
    switch (type) {
      case "check":
        mecSerialList = wheelRecordRepository.findMecSerialForCheck();
        break;
      case "correct":
        mecSerialList = wheelRecordRepository.findMecSerialForCorrectANDRelease();
        break;
      case "release":
        mecSerialList = wheelRecordRepository.findMecSerialForCorrectANDRelease();
        break;
      case "XNrelease":
        mecSerialList = wheelRecordRepository.findXNForRelease();
    }
    return mecSerialList.stream().map(mecSerial -> mecSerial.substring(0, mecSerial.lastIndexOf("-"))).distinct()
        .collect(Collectors.toList());
  }

  public List<String> getXNWheelList() {
    List<String> xnWheelList = null;
    xnWheelList = wheelRecordRepository.findXNForRelease();
    return xnWheelList.stream().collect(Collectors.toList());
  }

  public Page<MecRecord> find(String type, Integer currentPage, Integer pageSize) {
    int temp = 0;
    switch (type) {
      case "check":
        temp = 1;
        break;
      case "correct":
        temp = 2;
        break;
      case "release":
        temp = 3;
        break;
    }
    int status = temp;
    Specification<MecRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      list.add(criteriaBuilder.equal(root.get("status"), status));
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDate"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    return mecRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Page<WheelRecordMecResponse> getWheel(String mecSerial, Integer currentPage, Integer pageSize) {
    Specification<WheelRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();

      if (StringUtils.isNotEmpty(mecSerial)) {
        list.add(criteriaBuilder.like(root.get("mecSerial"), mecSerial + "-%"));
        if (mecSerial.startsWith("C")) {
          list.add(criteriaBuilder.equal(root.get("confirmedScrap"), 1));
        } else {
          list.add(criteriaBuilder.equal(root.get("mecConfirm"), 1));
        }
      }

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    return wheelRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize))
        .map(wheelRecord -> WheelRecordMecResponse.builder().wheelSerial(wheelRecord.getWheelSerial()).scrapCode(
            wheelRecord.getScrapCode()).mecSerial(wheelRecord.getMecSerial())
            .build());
  }

  @Transactional
  public void check(MecRecord mecRecord) {
    mecRecord.setStatus(1);
    mecRecord.setCreateDate(new Date());
    mecRecordRepository.save(mecRecord);
    wheelRecordRepository.findByWheelSerial(mecRecord.getWheelSerial()).ifPresent(wheelRecord -> {
      wheelRecord.setMecConfirm(1);
      wheelRecordRepository.save(wheelRecord);
    });
    printMecAndWheelBeforeUpdate(mecRecord.getMecSerial(), "T");
    String mecBatch = mecRecord.getMecSerial().substring(0, mecRecord.getMecSerial().lastIndexOf("-") + 1);
    wheelRecordRepository.updateMecSerial("T", mecBatch);
    heatRepository.updateMecSerial("T", mecBatch);
    printMecAndWheelAfterUpdate(mecRecord.getMecSerial(), "T");
  }

  @Transactional
  public void correct(MecRecord mecRecord) {
    mecRecord.setStatus(2);
    mecRecord.setCreateDate(new Date());
    mecRecordRepository.save(mecRecord);
    wheelRecordRepository.findByWheelSerial(mecRecord.getWheelSerial()).ifPresent(wheelRecord -> {
      wheelRecord.setMecConfirm(0);
      wheelRecordRepository.save(wheelRecord);
    });
    String mecBatch = mecRecord.getMecSerial().substring(0, mecRecord.getMecSerial().lastIndexOf("-") + 1);
    wheelRecordRepository.updateMecSerial("C", mecBatch);
    heatRepository.updateMecSerial("C", mecBatch);
  }

  @Transactional
  public void release(MecRecord mecRecord) {
    mecRecord.setStatus(3);
    mecRecord.setCreateDate(new Date());
    mecRecordRepository.save(mecRecord);
    String mecBatch = mecRecord.getMecSerial().substring(0, mecRecord.getMecSerial().lastIndexOf("-") + 1);
    wheelRecordRepository.updateMecSerial("P", mecBatch);
    heatRepository.updateMecSerial("P", mecBatch);
  }

  @Transactional
  public void xnRelease(List<XNReleaseRecord> list) {
    if(!list.isEmpty()){
      list.forEach(item -> {
        xnReleaseRecordRepository.save(item);
        wheelRecordRepository.updateXNTestCode(item.getLadleId());
      });
    }
  }

  public WheelRecordMecResponse findWheel(String wheelSerial) {
    return wheelRecordRepository.findByWheelSerial(wheelSerial)
        .map(wheelRecord -> WheelRecordMecResponse.builder().wheelSerial(wheelRecord.getWheelSerial()).scrapCode(
            wheelRecord.getScrapCode()).mecSerial(wheelRecord.getMecSerial()).confirmedScrap(wheelRecord.getConfirmedScrap())
            .scrapDate(wheelRecord.getScrapDate())
            .build()).orElseThrow(WHEEL_SERIAL_NOT_EXIST::getPlatformException);
  }

  public List<XNWheelResponse> findxnWheel(String wheel_serial) {
    NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
    Map<String, Object> params = new HashMap<>();
    String sql = "WITH s1 AS ( SELECT ladle_record.heat_record_id FROM wheel_record INNER JOIN ladle_record ON wheel_record.ladle_id = ladle_record.id WHERE wheel_record.wheel_serial = :xnwheelserial ";
    params.put ("xnwheelserial",wheel_serial);
    sql += " GROUP BY heat_record_id), ";

    sql += "s2 AS ( SELECT ladle_id, CASE test_code WHEN 'XNF' THEN 1 ELSE 0 END AS isCheckPass FROM wheel_record INNER JOIN ladle_record ON wheel_record.ladle_id = ladle_record.id ";
    sql += "WHERE ladle_record.heat_record_id IN ( SELECT s1.heat_record_id FROM s1 ) GROUP BY ladle_id,test_code ), ";

    sql += "s3 AS ( SELECT ladle_id,isCheckPass FROM s2 GROUP BY s2.ladle_id,isCheckPass ) ";

    sql += "SELECT ladle_record.id, ladle_record.ladle_record_key, chemistry_detail.c, chemistry_detail.si, chemistry_detail.mn, COUNT(wheel_record.wheel_serial) AS quantity, s3.isCheckPass ";
    sql += "FROM s3 INNER JOIN ladle_record ON ladle_record.id = s3.ladle_id INNER JOIN chemistry_detail ON ladle_record.id = chemistry_detail.ladle_id ";
    sql += "INNER JOIN wheel_record ON wheel_record.ladle_id = ladle_record.id WHERE s3.isCheckPass = 0 GROUP BY ladle_record.id, ladle_record.ladle_record_key, chemistry_detail.c, ";
    sql += "chemistry_detail.si,chemistry_detail.mn,s3.isCheckPass ORDER BY ladle_record.ladle_record_key ASC";
    List<Map<String, Object>> list = template.queryForList(sql, params);
    List<XNWheelResponse> XNWheelResponseList = new ArrayList<>();
    list.forEach(l ->
            XNWheelResponseList.add(
                    XNWheelResponse.builder().ladleId(((Number) l.get("id")).intValue())
                            .ladleKey(getStringValue(l.get("ladle_record_key")))
                            .C( getStringValue(l.get("c")))
                            .Si(getStringValue(l.get("si")))
                            .Mn(getStringValue(l.get("mn")))
                            .quantity(((Number) l.get("quantity")).intValue())
                            .isCheckPass(((Number) l.get("isCheckPass")).intValue()).build()
            )
                   );

    return XNWheelResponseList;
  }

  private void printMecAndWheelBeforeUpdate(String mecSerial, String type) {
    String mecBatch = mecSerial.substring(0, mecSerial.lastIndexOf("-") + 1);
    log.info("before update: ");
    log.info("mecSerial: {}, mecBatch: {}, modify to: {}", mecSerial, mecBatch, type);
    List<String> wheelSerialList = wheelRecordRepository.findWheelSerialByMecSerial(mecBatch);
    log.info(StringUtils.join(wheelSerialList, ","));
  }

  private void printMecAndWheelAfterUpdate(String mecSerial, String type) {
    String mecBatch = mecSerial.substring(0, mecSerial.lastIndexOf("-") + 1);
    log.info("after update:");
    List<String> wheelSerialList = wheelRecordRepository.findWheelSerialByMecSerial(mecBatch);
    log.info("unmodified list: " + StringUtils.join(wheelSerialList, ","));
    String modifiedMecBatch = type + mecSerial.substring(1, mecSerial.lastIndexOf("-") + 1);
    List<String> modifiedWheelSerialList = wheelRecordRepository.findWheelSerialByMecSerial(modifiedMecBatch);
    log.info("modified list: " + StringUtils.join(modifiedWheelSerialList, ","));
  }
}
