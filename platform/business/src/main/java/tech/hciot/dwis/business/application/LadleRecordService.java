package tech.hciot.dwis.business.application;


import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.POUR_EXIST;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.ChemistryDetailRepository;
import tech.hciot.dwis.business.domain.HeatRecordRepository;
import tech.hciot.dwis.business.domain.LadleRecordRepository;
import tech.hciot.dwis.business.domain.PourRecordRepository;
import tech.hciot.dwis.business.domain.model.HeatRecord;
import tech.hciot.dwis.business.domain.model.LadleRecord;
import tech.hciot.dwis.business.domain.model.PourRecord;

@Service
@Slf4j
public class LadleRecordService {

  private DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
  private SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy");

  @Autowired
  private LadleRecordRepository ladleRecordRepository;

  @Autowired
  private HeatRecordRepository heatRecordRepository;

  @Autowired
  private PourRecordRepository pourRecordRepository;

  @Autowired
  private ChemistryDetailRepository chemistryDetailRepository;

  @Autowired
  private PourRecordService pourRecordService;

  public Page<LadleRecord> find(Integer heatRecordId, Integer currentPage, Integer pageSize) {
    Specification<LadleRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (heatRecordId != null) {
        list.add(criteriaBuilder.equal(root.get("heatRecordId"), heatRecordId));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.asc(root.get("ladleSeq")));
      return query.getRestriction();
    };
    return ladleRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public LadleRecord add(LadleRecord ladleRecord) {
    ladleRecordRepository.findByHeatRecordIdAndLadleSeq(ladleRecord.getHeatRecordId(), ladleRecord.getLadleSeq())
        .ifPresent(ladleRecord1 -> {
          throw PlatformException.badRequestException("小包序号已存在");
        });
    HeatRecord heatRecord = heatRecordRepository.findById(ladleRecord.getHeatRecordId())
        .orElseThrow(() -> PlatformException.badRequestException("炉信息不存在"));

    ladleRecord.setLadleRecordKey(generateLadleRecordKey(heatRecord, ladleRecord.getLadleSeq()));
    ladleRecord.setRecordCreated(new Date());
    ladleRecordRepository.save(ladleRecord);
    updateChemistry(ladleRecord);
    return ladleRecord;
  }

  private void updateChemistry(LadleRecord ladleRecord) {
    chemistryDetailRepository.updateLadleId(ladleRecord.getId(), ladleRecord.getHeatRecordId(),
        ladleRecord.getLadleSeq().toString());
  }

  @Transactional
  public void modify(Integer id, LadleRecord newLadleRecord) {
    LadleRecord existedLadleRecord = ladleRecordRepository.findById(id)
        .orElseThrow(() -> PlatformException.badRequestException("小包信息不存在"));

    HeatRecord heatRecord = heatRecordRepository.findById(existedLadleRecord.getHeatRecordId())
            .orElseThrow(() -> PlatformException.badRequestException("炉信息不存在"));

    ladleRecordRepository.findByHeatRecordIdAndLadleSeq(existedLadleRecord.getHeatRecordId(), newLadleRecord.getLadleSeq())
            .ifPresent(ladleRecord -> {
              if (!ladleRecord.getId().equals(id)) {
                throw PlatformException.badRequestException("小包序号已存在");
              }
            });

    // 修改温度时，需要同步修改当前小包下所有浇注记录的计算开箱时间
    if (newLadleRecord.getLadleTemp().compareTo(existedLadleRecord.getLadleTemp()) != 0
            && !pourRecordRepository.findByLadleId(existedLadleRecord.getId()).isEmpty()) {
      updateTemp(existedLadleRecord);
    }

    Integer oldLadleSeq = existedLadleRecord.getLadleSeq();
    BeanUtil
        .copyProperties(newLadleRecord, existedLadleRecord, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    String newLadleRecordKey = generateLadleRecordKey(heatRecord, existedLadleRecord.getLadleSeq());
    existedLadleRecord.setLadleRecordKey(newLadleRecordKey);
    ladleRecordRepository.save(existedLadleRecord);
    if (oldLadleSeq != newLadleRecord.getLadleSeq()) {
      heatRecordRepository.findById(existedLadleRecord.getHeatRecordId()).ifPresent(heatRecord1 -> {
        pourRecordService
            .sendSseMsg("modify",
                "出钢号" + heatRecord1.getTapSeq() + "的小包号由" + oldLadleSeq + "修改为" + newLadleRecord
                    .getLadleSeq());
      });
    }
  }

  private void updateTemp(LadleRecord lr) {
    List<PourRecord> pourRecordList = pourRecordRepository.findByLadleId(lr.getId());
    for (PourRecord pourRecord : pourRecordList) {
      String castDateStr = DateFormatUtils.format(pourRecord.getCastDate(), "yyyy-MM-dd");
      String pourTimeStr = DateFormatUtils.format(pourRecord.getPourTime(), "HH:mm");
      String unpackTimeStr = pourRecordService.computeUnpackTime(pourRecord.getDesign(),
        lr.getLadleTemp().doubleValue(), castDateStr + " " + pourTimeStr);
      log.info("old {}, new {}", pourRecord.getOpenTimeCal(), unpackTimeStr);
      Date unpackTime = null;
      try {
        unpackTime = DateUtils.parseDate(unpackTimeStr, "yyyy-MM-dd HH:mm");
      } catch (ParseException e) {

      }
      pourRecord.setOpenTimeCal(unpackTime);
    }
    pourRecordRepository.saveAll(pourRecordList);
  }

  @Transactional
  public void updateLadleRecordKey(HeatRecord heatRecord) {
    ladleRecordRepository.findByHeatRecordId(heatRecord.getId()).forEach(ladleRecord -> {
      String newLadleRecordKey = generateLadleRecordKey(heatRecord, ladleRecord.getLadleSeq());
      ladleRecord.setLadleRecordKey(newLadleRecordKey);
      ladleRecordRepository.save(ladleRecord);
    });
  }

  @Transactional
  public void delete(Integer id) {
    ladleRecordRepository.findById(id).ifPresent(ladleRecord -> {
      if (pourRecordRepository.countByLadleId(id) > 0) {
        throw POUR_EXIST.getPlatformException();
      }
      chemistryDetailRepository.findByLadleId(id).ifPresent(chemistryDetail -> {
        chemistryDetail.setLadleId(null);
        chemistryDetailRepository.save(chemistryDetail);
      });
      ladleRecordRepository.deleteById(id);
    });
  }

  private String generateLadleRecordKey(HeatRecord heatRecord, Integer ladleSeq) {
    String datePre = outputFormat.format(heatRecord.getCastDate());
    return datePre + "_" + heatRecord.getFurnaceNo() + "_" + heatRecord.getHeatSeq() + "_" + ladleSeq;
  }
}
