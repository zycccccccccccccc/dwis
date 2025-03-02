package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.GraphiteProcess;
import tech.hciot.dwis.business.domain.model.GraphiteScrap;

public interface GraphiteProcessRepository extends JpaRepository<GraphiteProcess, Integer>, JpaSpecificationExecutor<GraphiteProcess> {

}
