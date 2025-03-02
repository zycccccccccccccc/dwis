package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.ManufacturerRepository;
import tech.hciot.dwis.business.domain.model.Manufacturer;
import tech.hciot.dwis.business.interfaces.dto.ManufacturerListResponse;

@Service
@Slf4j
public class ManufacturerService {

  @Autowired
  private ManufacturerRepository manufacturerRepository;

  public Page<Manufacturer> find(Integer status, Integer currentPage, Integer pageSize) {
    Specification<Manufacturer> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (status != null) {
        list.add(criteriaBuilder.equal(root.get("status"), status));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    return manufacturerRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public List<ManufacturerListResponse> findManufacturerIdNameList(String keyword, Integer limit) {
    return manufacturerRepository.findManufacturerIdNameList(keyword, limit).stream().map(o ->
        ManufacturerListResponse.builder()
          .id((Integer) o.get("id"))
          .name((String) o.get("name"))
          .build())
      .collect(Collectors.toList());
  }

  public Integer add(Manufacturer manufacturer) {
    manufacturer.setCreateTime(new Date());
    Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);
    return savedManufacturer.getId();
  }

  public void modify(Integer id, Manufacturer newManufacturer) {
    Manufacturer manufacturer = manufacturerRepository.findById(id).get();
    BeanUtil.copyProperties(newManufacturer, manufacturer, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    manufacturerRepository.save(manufacturer);
  }

  public void delete(Integer id) {
    manufacturerRepository.findById(id).ifPresent(manufacturer -> {
      manufacturerRepository.deleteById(id);
    });
  }

  public Manufacturer findById(Integer id) {
    return manufacturerRepository.findById(id).get();
  }
}
