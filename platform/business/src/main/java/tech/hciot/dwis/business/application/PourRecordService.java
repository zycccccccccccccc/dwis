package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.dateStr;
import static tech.hciot.dwis.base.util.StandardTimeUtil.parseTime;
import static tech.hciot.dwis.base.util.StandardTimeUtil.timeStr;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.LADLE_SEQ_INVALID;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.WHEEL_SERIAL_EXIST;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.*;
import tech.hciot.dwis.business.domain.model.*;
import tech.hciot.dwis.business.interfaces.dto.PourDelayStatRequest;
import tech.hciot.dwis.business.interfaces.dto.PourDelayStatResponse;

@Service
@Slf4j
public class PourRecordService {

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

  public static final int STATUS_INIT = 0;
  public static final int STATUS_PIT_SAVE = 3;
  public static final int STATUS_PIT_COMMIT = 4;

  private List<SseEmitter> sseEmitterList = new ArrayList<>();

  @Autowired
  private HeatRecordRepository heatRecordRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private PourDelayStatRepository pourDelayStatRepository;

  @Autowired
  private DesignRepository designRepository;

  @Autowired
  private CaseUnpackTimeCtlRepository caseUnpackTimeCtlRepository;

  @Autowired
  private LadleRecordRepository ladleRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;

  @Autowired
  private PitRecordsRepository pitRecordsRepository;

  @Autowired
  private SandJetRecordRepository sandJetRecordRepository;

  public void addPourRecord(PourRecord pourRecord) {
    if (pourRecordRepository.countByWheelSerial(pourRecord.getWheelSerial()) > 0) {
      throw WHEEL_SERIAL_EXIST.getPlatformException();
    }
    pourRecord.setBz(STATUS_INIT);
    pourRecord.setRecordCreated(new Date());
    pourRecordRepository.save(pourRecord);
  }

  public Page<PourRecord> findPourRecord(String operatorId, String recordDate, String batchNo, Integer ladleId,
      Integer currentPage, Integer pageSize) {
    Specification<PourRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(operatorId)) {
        list.add(criteriaBuilder.equal(root.get("coreSetterId1"), operatorId));
      }
      if (StringUtils.isNotBlank(recordDate)) {
        try {
          String start = recordDate + " 00:00:00";
          Date startDate = DateUtils.parseDate(start, "yyyy-MM-dd HH:mm:ss");
          Date endDate = DateUtils.addDays(startDate, 1);
          list.add(criteriaBuilder.between(root.get("recordCreated"), startDate, endDate));
        } catch (ParseException parseException) {
          log.error(parseException.getMessage());
        }
      }
      if (StringUtils.isNotEmpty(batchNo)) {
        list.add(criteriaBuilder.equal(root.get("batchNo"), batchNo));
      }
      if (ladleId != null) {
        list.add(criteriaBuilder.equal(root.get("ladleId"), ladleId));
      }
      if (StringUtils.isAllBlank(recordDate, batchNo) && ladleId == null) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("recordCreated"), last));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("recordCreated")));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return pourRecordRepository.findAll(specification, pageable).map(addProp());
  }

  public Page<PourRecord> findPourRecord(Integer ladleId, Integer currentPage, Integer pageSize) {
    Specification<PourRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();

      if (ladleId != null) {
        list.add(criteriaBuilder.equal(root.get("ladleId"), ladleId));
      }
      if (ladleId == null) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("recordCreated"), last));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.asc(root.get("inPitDateTime")));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return pourRecordRepository.findAll(specification, pageable).map(addProp());
  }

  public List<PourRecord> getForPit() {
    Specification<PourRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      Predicate p1 = criteriaBuilder.equal(root.get("bz"), 2);
      Predicate p2 = criteriaBuilder.notEqual(root.get("scrapCode"), "1");
      Predicate p3 = criteriaBuilder.equal(root.get("dragScrap"), 0);
      Predicate p4 = criteriaBuilder.equal(root.get("copeScrap"), 0);
      Predicate p5 = criteriaBuilder.equal(root.get("bz"), 3);
      list.add(criteriaBuilder.or(criteriaBuilder.and(p1, p2, p3, p4), p5));

      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.asc(root.get("recordCreated")));
      return query.getRestriction();
    };
    return pourRecordRepository.findAll(specification)
        .stream().map(addProp()).collect(Collectors.toList());
  }

  private Function<PourRecord, PourRecord> addProp() {
    return (pourRecord -> {
      if (pourRecord.getLadleId() != null) {
        ladleRecordRepository.findById(pourRecord.getLadleId())
            .ifPresent(ladleRecord -> {
              pourRecord.setLadleSeq(ladleRecord.getLadleSeq());
              Integer heatRecordId = ladleRecord.getHeatRecordId();
              heatRecordRepository.findById(heatRecordId).ifPresent(heatRecord -> pourRecord.setTapSeq(heatRecord.getTapSeq()));
            });
      }
      pitRecordsRepository.findByPitSeq(pourRecord.getPitSeq()).ifPresent(pit -> pourRecord.setPitNo(pit.getPitNo()));
      return pourRecord;
    });
  }

  public Page<PourRecord> getForPour(Integer currentPage, Integer pageSize) {
    Specification<PourRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      Predicate p1 = criteriaBuilder.equal(root.get("bz"), 0);
      Predicate p2 = criteriaBuilder.equal(root.get("bz"), 1);
      list.add(criteriaBuilder.or(p1, p2));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return pourRecordRepository.findAll(specification, pageable);
  }

  public void modify(Integer id, PourRecord newPourRecord) {
    PourRecord existedPourRecord = pourRecordRepository.findById(id)
        .orElseThrow(() -> PlatformException.badRequestException("浇注信息不存在"));

    BeanUtil
        .copyProperties(newPourRecord, existedPourRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    pourRecordRepository.save(existedPourRecord);
  }

  public void modifyList(List<PourRecord> pourRecordList, Integer bz) {
    pourRecordList.forEach(pourRecord -> {
      Date pourDT = generatePourDT(pourRecord.getPourTime());
      pourRecord.setPourDT(pourDT);
      pourRecord.setBz(bz);
      //计算模龄
      if (bz.equals(PourRecord.BZ_POUR_UNCOMMIT)) {
        //计算下箱, 石墨号&&轮号都相等
        sandJetRecordRepository.findByGraphiteAndStatus(pourRecord.getDragNo(), SandJetRecord.STATUS_JETTED).ifPresent(sandJetRecord -> {
          if (pourRecord.getWheelSerial().equals(sandJetRecord.getWheelSerial())) {
            Date dragTimeDiff = generateTimeDiff(sandJetRecord.getJetTime(), pourDT);
            if (dragTimeDiff != null) {
              sandJetRecord.setMoldAge(dragTimeDiff);
              sandJetRecord.setStatus(SandJetRecord.STATUS_POURED);
              sandJetRecordRepository.save(sandJetRecord);
            }
          }
        });
        //计算上箱，石墨号相等
        sandJetRecordRepository.findByGraphiteAndStatus(pourRecord.getCopeNo(), SandJetRecord.STATUS_JETTED).ifPresent(sandJetRecord -> {
          Date copeTimeDiff = generateTimeDiff(sandJetRecord.getJetTime(), pourDT);
          if (copeTimeDiff != null) {
            sandJetRecord.setWheelSerial(pourRecord.getWheelSerial());
            sandJetRecord.setMoldAge(copeTimeDiff);
            sandJetRecord.setStatus(SandJetRecord.STATUS_POURED);
            sandJetRecordRepository.save(sandJetRecord);
          }
        });
      }
    });
    log.info("begin save pourRecordList");
    pourRecordRepository.saveAll(pourRecordList);
    log.info("finish save pourRecordList");
  }

  //计算时间差
  private Date generateTimeDiff(Date start, Date end) {
    if (end.getTime() - start.getTime() > 0) {
      long timeDiffInMinis = (end.getTime() - start.getTime()) / 1000; //计算时间差（秒）
      long hours = timeDiffInMinis / 3600; //取小时数
      long minutes = (timeDiffInMinis / 60) % 60; //取分钟数
      long seconds = timeDiffInMinis % 60; //取秒数
      String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
      try {
        return DateUtils.parseDate(timeString, "HH:mm:ss");
      } catch (ParseException e) {
        log.error("moldAge time parse failed: {}", timeString);
        return null;
      }
    } else {
      return null;
    }
  }

  // 根据pourTime，生成pourDT，需要考虑跨天的情况
  private Date generatePourDT(Date pourTime) {
    Date currentTime = new Date();
    Date pourDT = parseTime(dateStr(currentTime) + " " + timeStr(pourTime));
    if ((pourDT.getTime() - currentTime.getTime()) > 7200 * 1000) { // 浇注时间比当前时间大2小时，就认为是跨天，需要将日期减1天
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(pourDT);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      pourDT = calendar.getTime();
    }
    return pourDT;
  }

  public PourDelayStatResponse delayStat(Integer heatRecordId) {
    Optional<HeatRecord> heatRecordOpt = heatRecordRepository.findById(heatRecordId);
    if (!heatRecordOpt.isPresent()) {
      throw PlatformException.badRequestException("炉信息不存在");
    }
    HeatRecord heatRecord = heatRecordOpt.get();
    PourDelayStat pourDelayStat = pourDelayStatRepository.findById(heatRecordId).get();

    Date lastPourEndDate;
    Optional<Integer> lastHeatRecordIdOpt = heatRecordRepository.findLastHeatRecordId(heatRecordId);
    if (lastHeatRecordIdOpt.isPresent()) {
      Integer lastHeatRecordId = lastHeatRecordIdOpt.get();
      PourDelayStat lastPourDelayStat = pourDelayStatRepository.findById(lastHeatRecordId).get();
      lastPourEndDate = lastPourDelayStat.getPourEndTime();
    } else {
      lastPourEndDate = pourDelayStat.getPourEndTime();
    }
    Integer interval = (int) ((pourDelayStat.getPourBeginTime().getTime() - lastPourEndDate.getTime()) / 1000 / 60);

    PourDelayStatResponse pourDelayStatResponse = PourDelayStatResponse.builder()
        .heatRecordId(heatRecordId)
        .pourDate(pourDelayStat.getPourDate())
        .heatSeq(pourDelayStat.getHeatSeq())
        .pourStartDate(pourDelayStat.getPourBeginTime())
        .pourEndDate(pourDelayStat.getPourEndTime())
        .pourDuration(pourDelayStat.getPourDuration())
        .lastPourEndDate(lastPourEndDate)
        .pourInterval(interval)
        .pourWheelNum(pourDelayStat.getPourWheelNum())
        .scrapNum(heatRecord.getScrapNum())
        .delayCode(heatRecord.getDelayCode())
        .memo(heatRecord.getMemo())
        .build();
    return pourDelayStatResponse;
  }

  public void modifyDelayStat(Integer heatRecordId, PourDelayStatRequest request) {
    Optional<HeatRecord> heatRecordOpt = heatRecordRepository.findById(heatRecordId);
    if (!heatRecordOpt.isPresent()) {
      throw PlatformException.badRequestException("炉信息不存在");
    }
    HeatRecord heatRecord = heatRecordOpt.get();
    heatRecord.setScrapNum(request.getScrapNum());
    heatRecord.setDelayCode(request.getDelayCode());
    heatRecord.setMemo(request.getMemo());
    heatRecordRepository.save(heatRecord);
  }

  public String computeUnpackTime(String design, Double pourTemp, String pourTimeStr) {
    Optional<Design> designOpt = designRepository.findByDesign(design);
    if (!designOpt.isPresent()) {
      log.error("can not find design: {}", design);
      return null;
    }
    String typeKxsj = designOpt.get().getTypeKxsj();
    Date pourTime;
    try {
      pourTime = DateUtils.parseDate(pourTimeStr, "yyyy-MM-dd HH:mm");
    } catch (ParseException e) {
      log.error("pour time parse failed: {}", pourTimeStr);
      return null;
    }
    Optional<Integer> unpackMinuteOpt = caseUnpackTimeCtlRepository.computeUnpackDelayMinute(typeKxsj, pourTemp);
    if (unpackMinuteOpt.isPresent()) {
      Date unpackTime = new Date(pourTime.getTime() + unpackMinuteOpt.get() * 60 * 1000);
      return DateFormatUtils.format(unpackTime, "yyyy-MM-dd HH:mm");
    } else {
      return null;
    }
  }

  public Page<PourRecord> findCommittedPitPourRecords(Integer pitSeq, Integer pageSize, Integer currentPage) {
    Specification<PourRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();

      if (pitSeq != null) {
        list.add(criteriaBuilder.equal(root.get("pitSeq"), pitSeq));
      }
      list.add(criteriaBuilder.equal(root.get("bz"), 4));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return pourRecordRepository.findAll(specification, pageable);
  }

  public List<PourRecord> findByPitSeq(Integer pitSeq) {
    return pourRecordRepository.findByPitSeq(pitSeq);
  }

  @Transactional
  public void delete(Integer id) {
    pourRecordRepository.findById(id).ifPresent(pourRecord -> {
      pourRecordRepository.delete(pourRecord);
      if (pourRecord.getBz() == STATUS_PIT_COMMIT) {
        wheelRecordService.deleteByWheelSerial(pourRecord.getWheelSerial());
      } else {
        sendSseMsg("delete", "轮号" + pourRecord.getWheelSerial() + "的数据已删除");
      }
    });
  }

  public void sendSseMsg(String action, String msg) {
    for (Iterator<SseEmitter> iterator = sseEmitterList.iterator(); iterator.hasNext(); ) {
      SseEmitter sseEmitter = iterator.next();
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.fluentPut("action", action).fluentPut("msg", msg);
        sseEmitter.send(jsonObject);
      } catch (IOException e) {
        sseEmitter.completeWithError(e);
        iterator.remove();
        log.error("send sse failed: {}", e.getMessage());
      }
    }
  }

  @Transactional
  public void modifyRecord(Integer id, PourRecord pourRecord) {
    pourRecordRepository.findById(id).ifPresent(old -> {
      String action = "modify";
      String msg = null;
      if (!old.getWheelSerial().equals(pourRecord.getWheelSerial())) {
        if (pourRecordRepository.countByWheelSerial(pourRecord.getWheelSerial()) > 0) {
          throw WHEEL_SERIAL_EXIST.getPlatformException();
        }
        msg = "轮号" + old.getWheelSerial() + "已修改为" + pourRecord.getWheelSerial();
      }

      old.setWheelSerial(pourRecord.getWheelSerial());
      old.setDesign(pourRecord.getDesign());
      old.setCopeNo(pourRecord.getCopeNo());
      old.setDragNo(pourRecord.getDragNo());
      old.setPourTime(pourRecord.getPourTime());
      old.setOpenTimeCal(pourRecord.getOpenTimeCal());
      old.setTestCode(pourRecord.getTestCode());
      old.setCopeScrap(pourRecord.getCopeScrap());
      old.setDragScrap(pourRecord.getDragScrap());
      old.setScrapCode(pourRecord.getScrapCode());
      String pourDate = dateFormat.format(new Date());
      String pourTime = timeFormat.format(pourRecord.getPourTime());
      String pourDT = pourDate + " " + pourTime;
      old.setPourDT(parseTime(pourDT));

      ladleRecordRepository.findById(pourRecord.getLadleId()).ifPresent(ladleRecord -> {
        if (ladleRecord.getLadleSeq() != pourRecord.getLadleSeq()) {
          String newKey = ladleRecord.getLadleRecordKey().substring(0, ladleRecord.getLadleRecordKey().length() - 1) + pourRecord
              .getLadleSeq();
          LadleRecord record = ladleRecordRepository.findByLadleRecordKey(newKey).orElseThrow(
              LADLE_SEQ_INVALID::getPlatformException);
          old.setLadleId(record.getId());
          wheelRecordService.findByWheelSerial(pourRecord.getWheelSerial()).ifPresent(wheelRecord -> {
            wheelRecord.setLadleId(record.getId());
            wheelRecordService.save(wheelRecord);
          });
        }
      });
      pourRecordRepository.save(old);
      if (pourRecord.getBz() == STATUS_PIT_COMMIT) {
        if (pourRecord.getScrapCode().equals("1")) {
          wheelRecordService.deleteByWheelSerial(pourRecord.getWheelSerial());
        }
        wheelRecordService.findByWheelSerial(pourRecord.getWheelSerial()).ifPresent(wheelRecord -> {
          wheelRecord.setDesign(pourRecord.getDesign());
          wheelRecord.setScrapCode(pourRecord.getScrapCode());
          wheelRecord.setTestCode(pourRecord.getTestCode());
          wheelRecordService.save(wheelRecord);
        });
      } else {
        if (msg == null) {
          msg = "车轮" + pourRecord.getWheelSerial() + "的浇注信息已修改";
        }
        sendSseMsg(action, msg);
      }
    });
  }

  public Page<PourRecord> findPourRecordLadle(Integer ladleId, Integer currentPage, Integer pageSize) {
    Specification<PourRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      Predicate p1 = criteriaBuilder.equal(root.get("bz"), 2);
      Predicate p2 = criteriaBuilder.equal(root.get("bz"), 3);
      Predicate p3 = criteriaBuilder.equal(root.get("bz"), 4);
      list.add(criteriaBuilder.or(p1, p2, p3));
      if (ladleId != null) {
        list.add(criteriaBuilder.equal(root.get("ladleId"), ladleId));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return pourRecordRepository.findAll(specification, pageable).map(addProp());
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
}
