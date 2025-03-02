package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.HeatCode;
import tech.hciot.dwis.business.domain.model.HeatParams;

import java.util.List;
import java.util.Optional;

public interface HeatParamsRepository extends JpaRepository<HeatParams, Integer>, JpaSpecificationExecutor<HeatParams> {

  Optional<HeatParams> findByTypeAndEnabled(String type, Integer enabled);
}
