package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.base.util.StandardTimeUtil;
import tech.hciot.dwis.business.domain.GraphiteFirmRepository;
import tech.hciot.dwis.business.domain.ManufacturerRepository;
import tech.hciot.dwis.business.domain.model.GraphiteFirm;
import tech.hciot.dwis.business.domain.model.Manufacturer;

@Service
@Slf4j
public class GraphiteFirmService {

  private DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private GraphiteFirmRepository graphiteFirmRepository;

  @Autowired
  private ManufacturerRepository manufacturerRepository;

  public Page<GraphiteFirm> find(String graphiteKey,
                                 Integer diameter,
                                 Integer status,
                                 String receiveDateStr,
                                 String graphiteOpeId,
                                 Integer currentPage,
                                 Integer pageSize) {
    Specification<GraphiteFirm> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (graphiteKey != null) {
        list.add(criteriaBuilder.equal(root.get("graphiteKey"), graphiteKey));
      }
      if (diameter != null) {
        list.add(criteriaBuilder.equal(root.get("diameter"), diameter));
      }
      if (status != null) {
        list.add(criteriaBuilder.equal(root.get("status"), status));
      }

      Date receiveDate = StandardTimeUtil.parseDate(receiveDateStr);
      if (receiveDate != null) {
        list.add(criteriaBuilder.equal(root.get("receiveDate"), receiveDate));
      }

      if (ObjectUtil.isAllEmpty(graphiteKey, diameter, status, receiveDateStr)) {
        Date last = DateUtils.addHours(new Date(), -12);
        list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), last));
      }

      if (graphiteOpeId != null) {
        list.add(criteriaBuilder.equal(root.get("graphiteOpeId"), graphiteOpeId));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return graphiteFirmRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Page<Map<String, Object>> findMaxGraphite(Integer currentPage, Integer pageSize) {
    Page<Map<String, Object>> page = graphiteFirmRepository.findMaxGraphite(PageRequest.of(currentPage, pageSize));
    return page;
  }

  public List<String> findGraphiteFirmList(String graphiteKey, Integer limit) {
    return graphiteFirmRepository.findGraphiteFirmList(graphiteKey, limit);
  }

  public Integer add(GraphiteFirm graphiteFirm) {
    graphiteFirmRepository.findByGraphiteKey(graphiteFirm.getGraphiteKey()).ifPresent(gf -> {
      throw PlatformException.badRequestException("原始石墨号已存在");
    });
    Manufacturer manufacturer = manufacturerRepository.findById(graphiteFirm.getManufacturerId())
      .orElseThrow(() -> PlatformException.badRequestException("供应商不存在"));
    graphiteFirm.setManufacturerName(manufacturer.getName());
    graphiteFirm.setCreateTime(new Date());
    graphiteFirm.setStatus(GraphiteFirm.STATUS_UNPROCESS);
    graphiteFirmRepository.save(graphiteFirm);
    return graphiteFirm.getId();
  }

  public void modify(Integer id, GraphiteFirm newGraphiteFirm) {
    GraphiteFirm graphiteFirm = graphiteFirmRepository.findById(id)
      .orElseThrow(() -> PlatformException.badRequestException("原始石墨不存在"));
    graphiteFirmRepository.findByGraphiteKey(newGraphiteFirm.getGraphiteKey()).ifPresent(existedGraphiteFirm -> {
      if (existedGraphiteFirm.getId() != graphiteFirm.getId()) {
        throw PlatformException.badRequestException("原始石墨号已存在");
      }
    });
    BeanUtil.copyProperties(newGraphiteFirm, graphiteFirm, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    Manufacturer manufacturer = manufacturerRepository.findById(graphiteFirm.getManufacturerId())
      .orElseThrow(() -> PlatformException.badRequestException("供应商不存在"));
    graphiteFirm.setManufacturerName(manufacturer.getName());
    graphiteFirmRepository.save(graphiteFirm);
  }

  public void delete(Integer id) {
    graphiteFirmRepository.findById(id).ifPresent(graphiteFirm -> {
      graphiteFirmRepository.deleteById(id);
    });
  }

  public GraphiteFirm findByGraphiteKey(String graphiteKey) {
    return graphiteFirmRepository.findByGraphiteKey(graphiteKey).orElse(null);
  }
}
