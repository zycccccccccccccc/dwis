package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.business.domain.model.MoldParams;

public interface MoldParamsRepository extends JpaRepository<MoldParams, Integer>, JpaSpecificationExecutor<MoldParams> {

  @Query(value = "SELECT TOP 1 id FROM mold_params WHERE type = :type AND enabled = :enabled ORDER BY id DESC ", nativeQuery = true)
  Integer findIdByTypeAndEnabled(String type, Integer enabled);
}
