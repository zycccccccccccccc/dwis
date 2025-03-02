package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.RollTip;

public interface RollTipRepository extends JpaRepository<RollTip, Integer>, JpaSpecificationExecutor<RollTip> {

}
