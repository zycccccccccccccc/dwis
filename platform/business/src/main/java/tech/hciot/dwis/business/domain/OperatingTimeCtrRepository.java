package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.OperatingTimeCtr;

public interface OperatingTimeCtrRepository extends JpaRepository<OperatingTimeCtr, Integer>,
    JpaSpecificationExecutor<OperatingTimeCtr> {

  Optional<OperatingTimeCtr> findByDep(String dep);
}
