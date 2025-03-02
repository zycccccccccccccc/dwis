package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.DipelectrodeTable;

public interface DipelectrodeTableRepository extends JpaRepository<DipelectrodeTable, Integer>, JpaSpecificationExecutor<DipelectrodeTable> {

  @Query(value = "SELECT ISNULL(MAX(times), 0) FROM dipelectrode_table WHERE furnace_tap_id = :furnaceTapId ", nativeQuery = true)
  Integer findMaxTimes(@Param("furnaceTapId") Integer furnaceTapId);

  Optional<DipelectrodeTable> findTop1ByFurnaceTapIdOrderByIdDesc(Integer furnaceTapId);
}
