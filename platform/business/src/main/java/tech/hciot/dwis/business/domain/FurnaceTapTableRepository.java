package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.FurnaceTapTable;

public interface FurnaceTapTableRepository extends JpaRepository<FurnaceTapTable, Integer>, JpaSpecificationExecutor<FurnaceTapTable> {

  @Query(value = "SELECT top 1 * FROM furnace_tap_table WHERE furnace_no = :furnaceNo AND status = :status " +
    "ORDER BY id DESC", nativeQuery = true)
  Optional<FurnaceTapTable> findLatest(@Param("furnaceNo") Integer furnaceNo, @Param("status") Integer status);

  @Query(value = "SELECT top 1 * FROM furnace_tap_table WHERE furnace_no = :furnaceNo AND furnace_seq = :furnaceSeq AND status = :status AND DATEPART(yyyy, cast_date) = :castYear "  +
          "ORDER BY id DESC", nativeQuery = true)
  Optional<FurnaceTapTable> findSame(@Param("furnaceNo") Integer furnaceNo, @Param("furnaceSeq") Integer furnaceSeq, @Param("status") Integer status, @Param("castYear") Integer castYear);

  @Query(value = "SELECT ISNULL(MAX(furnace_seq), 0) FROM furnace_tap_table WHERE furnace_no = :furnaceNo " +
    "AND status = 2 AND cast_date BETWEEN :beginDate AND :endDate", nativeQuery = true)
  Integer findMaxSeq(@Param("furnaceNo") Integer furnaceNo,
                     @Param("beginDate") String beginDate,
                     @Param("endDate") String endDate);

  Optional<FurnaceTapTable> findByIdAndStatus(Integer id, Integer status);
}
