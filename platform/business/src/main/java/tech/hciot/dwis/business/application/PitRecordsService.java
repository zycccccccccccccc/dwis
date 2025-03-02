package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.base.util.StandardTimeUtil.parseTime;
import static tech.hciot.dwis.business.application.PourRecordService.STATUS_PIT_COMMIT;
import static tech.hciot.dwis.business.application.PourRecordService.STATUS_PIT_SAVE;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.COMMIT_WHEEL_LESS_THAN_THREE;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.PIT_RECORD_NOT_EXISTS;
import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.POUR_NOT_EXISTS;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.PitRecordsRepository;
import tech.hciot.dwis.business.domain.PourRecordRepository;
import tech.hciot.dwis.business.domain.SandJetRecordRepository;
import tech.hciot.dwis.business.domain.model.PitRecords;
import tech.hciot.dwis.business.domain.model.PourRecord;
import tech.hciot.dwis.business.domain.model.SandJetRecord;
import tech.hciot.dwis.business.domain.model.WheelRecord;

@Service
public class PitRecordsService {

  @Autowired
  private PitRecordsRepository pitRecordsRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private SandJetRecordRepository sandJetRecordRepository;

  @Autowired
  private WheelRecordService wheelRecordService;


  @Transactional
  public int addPitRecords(PitRecords pitRecords) {
    pitRecords.setRecordCreated(new Date());
    return pitRecordsRepository.save(pitRecords).getPitSeq();
  }

  public void editPitRecords(Integer id, PitRecords editPitRecords) {
    pitRecordsRepository.findById(id).ifPresent(pitRecords -> {
      pitRecords.setPitNo(editPitRecords.getPitNo());
      pitRecords.setCraneInId(editPitRecords.getCraneInId());
      pitRecords.setOpenId(editPitRecords.getOpenId());
      pitRecordsRepository.save(pitRecords);
    });
  }

  public void savePitRecords(Integer id, PourRecord editPourRecord) {
    pourRecordRepository.findById(editPourRecord.getPourId()).map(pourRecord -> {
      pourRecord.setPitSeq(id);
      pourRecord.setScrapCode(editPourRecord.getScrapCode());
      pourRecord.setInPitDateTime(editPourRecord.getInPitDateTime());
      pourRecord.setOpenTimeAct(editPourRecord.getOpenTimeAct());
      pourRecord.setBz(STATUS_PIT_SAVE);
      if (editPourRecord.getXrayReq() != null && editPourRecord.getXrayReq() != 0) {
        pourRecord.setXrayReq(editPourRecord.getXrayReq());
      }
      if (editPourRecord.getOffPants() != null && editPourRecord.getOffPants() != 0) {
        pourRecord.setOffPants(editPourRecord.getOffPants());
      }
      if (editPourRecord.getVibrateWheel() != null && editPourRecord.getVibrateWheel() != 0) {
        pourRecord.setVibrateWheel(editPourRecord.getVibrateWheel());
      }
      pourRecordRepository.save(pourRecord);
      //更新下箱状态
      sandJetRecordRepository.findByGraphiteAndStatus(pourRecord.getDragNo(), SandJetRecord.STATUS_POURED).ifPresent(sandJetRecord -> {
        sandJetRecord.setStatus(SandJetRecord.STATUS_UNBOX);
        sandJetRecordRepository.save(sandJetRecord);
      });
      //更新上箱状态
      sandJetRecordRepository.findByGraphiteAndStatus(pourRecord.getCopeNo(), SandJetRecord.STATUS_POURED).ifPresent(sandJetRecord -> {
        sandJetRecord.setStatus(SandJetRecord.STATUS_UNBOX);
        sandJetRecordRepository.save(sandJetRecord);
      });
      return Optional.empty();
    }).orElseThrow(POUR_NOT_EXISTS::getPlatformException);
  }

  @Transactional
  public void commitPitRecords(Integer id, List<PourRecord> editPourRecordList) {
    if (editPourRecordList.size() < 3) {
      throw COMMIT_WHEEL_LESS_THAN_THREE.getPlatformException();
    }
    List<Date> dateList = new ArrayList<>();
    editPourRecordList.forEach(pourRecord -> {
      pourRecordRepository.findById(pourRecord.getPourId()).ifPresent(pour -> {
        pour.setPitSeq(id);
        pour.setBz(STATUS_PIT_COMMIT);
        if (!pour.getScrapCode().equals("1")) {
          WheelRecord wheelRecord =
              WheelRecord.builder().wheelSerial(pour.getWheelSerial()).ladleId(pour.getLadleId()).design(pour.getDesign())
                  .testCode(pour.getTestCode()).scrapCode(pour.getScrapCode()).build();
          wheelRecordService.addWheelRecord(wheelRecord);
        } else {
          pour.setDragScrap(1);
          pour.setCopeScrap(1);
        }
        pourRecordRepository.save(pour);
        dateList.add(pour.getInPitDateTime());
      });
    });
    Date max = dateList.stream().max(Date::compareTo).get();
    Date maxOut = DateUtils.addHours(max, 12);
    pitRecordsRepository.findById(id).ifPresent(pitRecords -> {
      pitRecords.setInPitDT(max);
      pitRecords.setOutPitDTCal(maxOut);
      pitRecordsRepository.save(pitRecords);
    });
  }

  public List<PitRecords> findOutRecord() {
    return pitRecordsRepository.findByOutPitDTActIsNull();
  }

  public List<Integer> findOutRecordSeq() {
    return pitRecordsRepository.findByOutPitDTCalLessThanAndOutPitDTActIsNull(new Date()).stream()
        .map(pitRecords -> pitRecords.getPitSeq()).collect(Collectors.toList());
  }

  public PitRecords findPitRecords(Integer id) {
    return pitRecordsRepository.findById(id).orElseThrow(PIT_RECORD_NOT_EXISTS::getPlatformException);
  }

  @Transactional
  public void outPitRecords(Integer id, PitRecords editPitRecords) {
    pitRecordsRepository.findById(id).map(pitRecords -> {
      pitRecords.setOutPitDTAct(editPitRecords.getOutPitDTAct());
      pitRecords.setCraneOutId(editPitRecords.getCraneOutId());
      pitRecordsRepository.save(pitRecords);

      if (editPitRecords.getOutPitDTAct().getTime() - pitRecords.getOutPitDTCal().getTime() < 0) {
        pourRecordRepository.findByPitSeq(pitRecords.getPitSeq()).forEach(pourRecord -> {
          if (StringUtils.isEmpty(pourRecord.getScrapCode())) {
            pourRecord.setScrapCode("8");
            pourRecordRepository.save(pourRecord);
          }
          wheelRecordService.findByWheelSerial(pourRecord.getWheelSerial()).ifPresent(wheelRecord -> {
            if (StringUtils.isEmpty(wheelRecord.getScrapCode())) {
              wheelRecord.setScrapCode("8");
              wheelRecordService.save(wheelRecord);
            }
          });
        });
      } else {
        pourRecordRepository.findByPitSeq(pitRecords.getPitSeq()).forEach(pourRecord -> {
          if ("8".equals(pourRecord.getScrapCode())) {
            pourRecord.setScrapCode("");
            pourRecordRepository.save(pourRecord);
          }
          wheelRecordService.findByWheelSerial(pourRecord.getWheelSerial()).ifPresent(wheelRecord -> {
            if ("8".equals(wheelRecord.getScrapCode())) {
              wheelRecord.setScrapCode("");
              wheelRecordService.save(wheelRecord);
            }
          });
        });
      }
      return Optional.empty();
    }).orElseThrow(PIT_RECORD_NOT_EXISTS::getPlatformException);
  }

  public Page<PitRecords> listPitRecords(Integer pitSeq, String outDTCal, String outDTAct, Integer currentPage,
      Integer pageSize) {
    Specification<PitRecords> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (pitSeq != null) {
        list.add(criteriaBuilder.equal(root.get("pitSeq"), pitSeq));
      } else {
        list.add(criteriaBuilder.equal(root.get("pitSeq"), 0));
      }
      if (StringUtils.isNotEmpty(outDTCal)) {
        Date date = parseTime(outDTCal);
        list.add(criteriaBuilder.equal(root.get("outPitDTCal"), date));
      }
      if (StringUtils.isNotEmpty(outDTAct)) {
        Date date = parseTime(outDTAct);
        list.add(criteriaBuilder.equal(root.get("outPitDTAct"), date));
      }
      return query.where(criteriaBuilder.and(list.toArray(new Predicate[0]))).getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return pitRecordsRepository.findAll(specification, pageable);
  }

  @Transactional
  public void modify(Integer id, PitRecords newPitRecord) {
    pitRecordsRepository.findById(id).map(pitRecord -> {
      BeanUtil.copyProperties(newPitRecord, pitRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      pitRecordsRepository.save(pitRecord);
      return Optional.empty();
    }).orElseThrow(() -> PlatformException.badRequestException("缓冷桶信息不存在"));
  }
}
