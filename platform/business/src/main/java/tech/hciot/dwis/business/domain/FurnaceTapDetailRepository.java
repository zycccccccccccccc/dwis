package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.FurnaceTapDetail;

public interface FurnaceTapDetailRepository extends JpaRepository<FurnaceTapDetail, Integer>, JpaSpecificationExecutor<FurnaceTapDetail> {

  @Query(value = "SELECT top 1 * FROM furnace_tap_table WHERE furnace_no = :furnaceNo AND status = :status " +
    "ORDER BY id DESC", nativeQuery = true)
  Optional<FurnaceTapDetail> findLatest(@Param("furnaceNo") Integer furnaceNo, @Param("status") Integer status);

}
