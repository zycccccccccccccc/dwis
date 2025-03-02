package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.ChemicalControl;

public interface ChemicalControlRepository extends JpaRepository<ChemicalControl, Integer>, JpaSpecificationExecutor<ChemicalControl> {

  List<ChemicalControl> findByDesign(String design);
}
