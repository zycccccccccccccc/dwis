package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.TempmeasureTable;

public interface TempmeasureTableRepository extends JpaRepository<TempmeasureTable, Integer>, JpaSpecificationExecutor<TempmeasureTable> {

  @Query(value = "SELECT ISNULL(MAX(times), 0) FROM tempmeasure_table WHERE furnace_tap_id = :furnaceTapId ", nativeQuery = true)
  Integer findMaxTimes(@Param("furnaceTapId") Integer furnaceTapId);

  Optional<TempmeasureTable> findTop1ByFurnaceTapIdOrderByIdDesc(Integer furnaceTapId);
}
