package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.BoreSize;

public interface BoreSizeRepository extends JpaRepository<BoreSize, Integer>, JpaSpecificationExecutor<BoreSize> {

  List<BoreSize> findByDesignAndEnabled(String design, Integer enabled);
}
