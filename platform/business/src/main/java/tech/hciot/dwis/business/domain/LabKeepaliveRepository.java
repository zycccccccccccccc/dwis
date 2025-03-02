package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.LabKeepalive;
import tech.hciot.dwis.business.domain.model.RollTip;

public interface LabKeepaliveRepository extends JpaRepository<LabKeepalive, Integer>, JpaSpecificationExecutor<LabKeepalive> {

}
