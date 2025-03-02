package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.ReworkCode;
import tech.hciot.dwis.business.domain.model.TestCode;

public interface ReworkCodeRepository extends JpaRepository<ReworkCode, Integer>, JpaSpecificationExecutor<ReworkCode> {

  Optional<ReworkCode> findByCodeAndEnabled(String code, Integer enabled);
}
