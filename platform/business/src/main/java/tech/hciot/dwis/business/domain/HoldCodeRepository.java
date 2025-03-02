package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.HoldCode;

public interface HoldCodeRepository extends JpaRepository<HoldCode, Integer>, JpaSpecificationExecutor<HoldCode> {

  List<HoldCode> findByEnabled(Integer enabled);

  Optional<HoldCode> findByCodeAndEnabled(String code, Integer enabled);

  Optional<HoldCode> findByCode(String code);
}
