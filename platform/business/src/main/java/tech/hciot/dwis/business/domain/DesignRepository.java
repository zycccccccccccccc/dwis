package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.business.domain.model.Design;

public interface DesignRepository extends JpaRepository<Design, Integer>, JpaSpecificationExecutor<Design> {

  Optional<Design> findByDesign(String design);

  @Query(value = "SELECT design FROM design where enabled = 1", nativeQuery = true)
  List<String> findDesignList();

  @Query(value = "SELECT design FROM design where enabled = 0 OR enabled = 1", nativeQuery = true)
  List<String> findDesignListForReport();

  @Query(value = "SELECT DISTINCT base_design FROM design", nativeQuery = true)
  List<String> findAllDesignList();
}
