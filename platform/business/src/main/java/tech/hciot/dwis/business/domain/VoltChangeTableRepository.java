package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.VoltChangeTable;

public interface VoltChangeTableRepository extends JpaRepository<VoltChangeTable, Integer>, JpaSpecificationExecutor<VoltChangeTable> {

  @Query(value = "SELECT ISNULL(MAX(times), 0) FROM volt_change_table WHERE furnace_tap_id = :furnaceTapId ", nativeQuery = true)
  Integer findMaxTimes(@Param("furnaceTapId") Integer furnaceTapId);

  Optional<VoltChangeTable> findTop1ByFurnaceTapIdOrderByIdDesc(Integer furnaceTapId);
}
