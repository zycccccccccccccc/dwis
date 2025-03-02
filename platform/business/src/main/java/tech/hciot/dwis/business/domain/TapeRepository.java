package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.Tape;

public interface TapeRepository extends JpaRepository<Tape, Integer>, JpaSpecificationExecutor<Tape> {
  List<Tape> findByEnabled(Integer enabled);
  List<Tape> findByDesignAndEnabled(String design, Integer enabled);
}
