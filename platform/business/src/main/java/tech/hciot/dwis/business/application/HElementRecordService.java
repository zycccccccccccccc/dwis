package tech.hciot.dwis.business.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.HElementRecordRepository;
import tech.hciot.dwis.business.domain.model.HElementRecord;
import tech.hciot.dwis.business.interfaces.dto.HElementRequest;

@Service
public class HElementRecordService {

  @Autowired
  private HElementRecordRepository hElementRecordRepository;

  @Autowired
  private ChemistryDetailService chemistryDetailService;

  @Transactional
  public void add(HElementRequest hElementRequest) {
    chemistryDetailService.findByHeatRecordId(hElementRequest.getHeatRecordId()).forEach(chemistryDetail -> {
      BigDecimal h = new BigDecimal(hElementRequest.getH() / 100d);
      chemistryDetail.setB(h);
      chemistryDetailService.save(chemistryDetail);
      HElementRecord hElementRecord =
          HElementRecord.builder().createTime(new Date()).castDate(hElementRequest.getCastDate()).h(h)
              .fid(chemistryDetail.getId()).furnaceNo(hElementRequest.getFurnaceNo()).heatSeq(hElementRequest.getHeatSeq())
              .tapSeq(hElementRequest.getTapSeq()).opeId(hElementRequest.getOpeId())
              .build();
      hElementRecordRepository.save(hElementRecord);
    });
  }

  public Page<HElementRecord> find(String opeId, Integer currentPage, Integer pageSize) {
    Specification<HElementRecord> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotEmpty(opeId)) {
        list.add(criteriaBuilder.equal(root.get("opeId"), opeId));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return hElementRecordRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  @Transactional
  public void modify(Integer id, HElementRequest hElementRequest) {
    hElementRecordRepository.findById(id).ifPresent(hElementRecord -> {
      Integer fid = hElementRecord.getFid();
      BigDecimal h = new BigDecimal(hElementRequest.getH() / 100d);
      chemistryDetailService.findById(fid).ifPresent(chemistryDetail -> {
        chemistryDetail.setB(h);
        chemistryDetailService.save(chemistryDetail);
      });
      hElementRecord.setH(h);
      hElementRecordRepository.save(hElementRecord);
    });
  }
}
