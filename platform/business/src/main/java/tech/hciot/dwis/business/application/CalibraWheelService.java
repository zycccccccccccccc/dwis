package tech.hciot.dwis.business.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.business.domain.CalibraWheelRepository;
import tech.hciot.dwis.business.domain.model.CalibraWheel;

@Service
@Slf4j
public class CalibraWheelService {

  @Autowired
  private CalibraWheelRepository calibraWheelRepository;

  @Autowired
  private OperatingTimeCtrService operatingTimeCtrService;

  public Page<CalibraWheel> find(String machineNo,
                                 String operator,
                                 Integer currentPage,
                                 Integer pageSize) {
    Specification<CalibraWheel> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (machineNo != null) {
        list.add(criteriaBuilder.equal(root.get("machineNo"), machineNo));
      }
      if (operator != null) {
        list.add(criteriaBuilder.equal(root.get("operator"), operator));
      }
      Date last = DateUtils.addHours(new Date(), -12);
      list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return calibraWheelRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public CalibraWheel add(CalibraWheel calibraWheel) {
    calibraWheel.setMachineCount(0);
    calibraWheel.setOpeDT(operatingTimeCtrService.getMachineOperatingTime());
    calibraWheel.setCreateTime(new Date());
    return calibraWheelRepository.save(calibraWheel);
  }

  public CalibraWheel findById(Integer id) {
    return calibraWheelRepository.findById(id)
      .orElseThrow(() -> PlatformException.badRequestException("没有找到标准轮数据"));
  }
}
