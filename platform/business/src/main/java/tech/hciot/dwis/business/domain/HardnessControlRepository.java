package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.HardnessControl;

public interface HardnessControlRepository extends JpaRepository<HardnessControl, Integer>, JpaSpecificationExecutor<HardnessControl> {

  Optional<HardnessControl> findByDesign(@Param("design") String design);
}
