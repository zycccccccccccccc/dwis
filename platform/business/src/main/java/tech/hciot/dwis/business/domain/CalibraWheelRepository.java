package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.CalibraWheel;

public interface CalibraWheelRepository extends JpaRepository<CalibraWheel, Integer>, JpaSpecificationExecutor<CalibraWheel> {

}
