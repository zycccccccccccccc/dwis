package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ChargeMaterialTable;

public interface ChargeMaterialTableRepository extends JpaRepository<ChargeMaterialTable, Integer>, JpaSpecificationExecutor<ChargeMaterialTable> {

  @Query(value = "SELECT ISNULL(MAX(times), 0) FROM charge_material_table WHERE furnace_tap_id = :furnaceTapId ", nativeQuery = true)
  Integer findMaxTimes(@Param("furnaceTapId") Integer furnaceTapId);

  Optional<ChargeMaterialTable> findTop1ByFurnaceTapIdOrderByIdDesc(Integer furnaceTapId);

  List<ChargeMaterialTable> findByFurnaceTapId(Integer furnaceTapId);
}
