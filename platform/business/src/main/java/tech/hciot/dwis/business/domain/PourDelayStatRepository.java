package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.PourDelayStat;

public interface PourDelayStatRepository extends JpaRepository<PourDelayStat, Integer>, JpaSpecificationExecutor<PourDelayStat> {

}
