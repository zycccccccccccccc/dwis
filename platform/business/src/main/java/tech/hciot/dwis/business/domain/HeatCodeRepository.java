package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.HeatCode;

public interface HeatCodeRepository extends JpaRepository<HeatCode, Integer>, JpaSpecificationExecutor<HeatCode> {

  List<HeatCode> findByEnabled(Integer enabled);

  Optional<HeatCode> findByCodeAndEnabled(String code, Integer enabled);

  Optional<HeatCode> findByCode(String code);
}
