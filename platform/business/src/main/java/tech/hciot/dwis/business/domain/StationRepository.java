package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.Station;

public interface StationRepository extends JpaRepository<Station, Integer>, JpaSpecificationExecutor<Station> {

}
