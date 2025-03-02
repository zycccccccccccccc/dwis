package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.GraphiteScrap;

public interface GraphiteScrapRepository extends JpaRepository<GraphiteScrap, Integer>, JpaSpecificationExecutor<GraphiteScrap> {

}
