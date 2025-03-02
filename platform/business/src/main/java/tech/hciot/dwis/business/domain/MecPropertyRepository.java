package tech.hciot.dwis.business.domain;

import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.business.domain.model.MecProperty;

public interface MecPropertyRepository extends JpaRepository<MecProperty, Integer>, JpaSpecificationExecutor<MecProperty> {

  @Query(value = "SELECT mec_property.* FROM mec_property,pour_record WHERE mec_property.wheel_serial = "
      + "pour_record.wheel_serial AND pour_record.cast_date BETWEEN ?1 AND ?2",
      countQuery = "SELECT COUNT(1) FROM mec_property,pour_record WHERE mec_property.wheel_serial = pour_record.wheel_serial AND "
          + "pour_record.cast_date BETWEEN ?1 AND ?2", nativeQuery = true)
  Page<MecProperty> findSummary(Date startDate, Date endDate, Pageable pageable);
}
