package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.PourParams;

import java.util.List;
import java.util.Optional;

public interface PourParamsRepository extends JpaRepository<PourParams, Integer>, JpaSpecificationExecutor<PourParams> {

  //Optional<PourParams> findByTypeAndEnabled(String type, Integer enabled);

  @Query(value = "SELECT * FROM pour_params WHERE type = :type AND enabled = :enabled", nativeQuery = true)
  List<PourParams> findByTypeAndEnabled(@Param("type") String type, @Param("enabled") Integer enabled);
}
