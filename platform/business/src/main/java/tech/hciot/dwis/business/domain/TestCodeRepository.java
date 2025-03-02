package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.TestCode;

public interface TestCodeRepository extends JpaRepository<TestCode, Integer>, JpaSpecificationExecutor<TestCode> {

  List<TestCode> findByEnabled(Integer enabled);

  Optional<TestCode> findByCodeAndEnabled(String code, Integer enabled);

  Optional<TestCode> findByCode(String code);
}
