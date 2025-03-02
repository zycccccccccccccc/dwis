package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.DepartmentRepository;
import tech.hciot.dwis.business.domain.model.Department;

@Service
@Slf4j
public class DepartmentService {

  @Autowired
  private DepartmentRepository departmentRepository;

  public Page<Department> find(Integer currentPage, Integer pageSize) {
    Specification<Department> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      return query.getRestriction();
    };
    return departmentRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Integer add(Department department) {
    Department savedDepartment = departmentRepository.save(department);
    return savedDepartment.getId();
  }

  public void modify(Integer id, Department newDepartment) {
    Department department = departmentRepository.findById(id).get();
    BeanUtil.copyProperties(newDepartment, department, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    departmentRepository.save(department);
  }

  public void delete(Integer id) {
    // TODO 检查部门下有没有员工
    departmentRepository.findById(id).ifPresent(department -> {
      departmentRepository.deleteById(id);
    });
  }

  public Department findById(Integer id) {
    return departmentRepository.findById(id).get();
  }

  private void assertDepartmentExists(Integer id) {
    // departmentRepository.findById(id)
    //     .orElseThrow(FLOOR_NOT_FOUND::getPlatformException);
  }
}
