package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.O2blowingTable;

public interface O2blowingTableRepository extends JpaRepository<O2blowingTable, Integer>, JpaSpecificationExecutor<O2blowingTable> {

  @Query(value = "SELECT ISNULL(MAX(times), 0) FROM o2blowing_table WHERE furnace_tap_id = :furnaceTapId ", nativeQuery = true)
  Integer findMaxTimes(@Param("furnaceTapId") Integer furnaceTapId);

  Optional<O2blowingTable> findTop1ByFurnaceTapIdOrderByIdDesc(Integer furnaceTapId);
}
