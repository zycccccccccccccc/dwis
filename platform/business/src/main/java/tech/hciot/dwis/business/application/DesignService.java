package tech.hciot.dwis.business.application;

import static tech.hciot.dwis.business.infrastructure.exception.ErrorEnum.DESIGNS_NOT_EXISTS;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.DesignRepository;
import tech.hciot.dwis.business.domain.model.Design;
import tech.hciot.dwis.business.infrastructure.exception.ErrorEnum;

@Service
@Slf4j
public class DesignService {

  @Autowired
  private DesignRepository designRepository;

  public Page<Design> findDesign(String design, Integer currentPage, Integer pageSize) {
    Specification<Design> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (StringUtils.isNotBlank(design)) {
        list.add(criteriaBuilder.like(root.get("design"), "%" + design + "%"));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    Pageable pageable =
        pageSize == null ? Pageable.unpaged() : PageRequest.of(currentPage, pageSize);
    return designRepository.findAll(specification, pageable);
  }

  public List<String> findDesignList() {return designRepository.findDesignList();}

  public List<String> findDesignListForReport() {return designRepository.findDesignListForReport();}

  public List<String> findAllDesignList() {
    return designRepository.findAllDesignList();
  }

  public void addDesign(Design design) {
    designRepository.findByDesign(design.getDesign()).ifPresent(d -> {
      throw ErrorEnum.DESIGNS_EXISTS.getPlatformException();
    });
    design.setCreateTime(new Date());
    designRepository.save(design);
  }

  public void editDesign(Integer id, Design modifiedDesign) {
    designRepository.findById(id).map(design -> {
      BeanUtil.copyProperties(modifiedDesign, design, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
      design.setId(id);
      designRepository.save(design);
      return Optional.empty();
    }).orElseThrow(DESIGNS_NOT_EXISTS::getPlatformException);
  }

  public void deleteDesign(Integer id) {
    designRepository.findById(id).ifPresent(design -> {
      designRepository.deleteById(id);
    });
  }

  public boolean isInternal(String design) {
    return designRepository.findByDesign(design).map(design1 ->
        design1.getInternal() == Design.IS_INTERNAL_YES ? true : false).orElse(false);
  }

  public Design getDesign(String design) {
    return designRepository.findByDesign(design).orElseThrow(DESIGNS_NOT_EXISTS::getPlatformException);
  }
}
