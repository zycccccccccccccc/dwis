package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ChemistryDetail;

public interface ChemistryDetailRepository extends JpaRepository<ChemistryDetail, Integer>,
    JpaSpecificationExecutor<ChemistryDetail> {

  Optional<ChemistryDetail> findByLadleId(Integer ladleId);

  @Query(value = "SELECT top 1 * FROM chemistry_detail WHERE furnace_seq LIKE :furnaceNo% " +
    "AND (sample_no LIKE '%T%' OR sample_no LIKE '%P%') ORDER BY id DESC", nativeQuery = true)
  Optional<ChemistryDetail> findNewestFurnaceLab(@Param("furnaceNo") String furnaceNo);

  @Query(value = "SELECT top 1 * FROM chemistry_detail WHERE furnace_seq LIKE :furnaceNo% " +
    "AND (sample_no LIKE '%T%' OR sample_no LIKE '%L%') ORDER BY id DESC", nativeQuery = true)
  Optional<ChemistryDetail> findNewestPourLab(@Param("furnaceNo") String furnaceNo);

  @Query(value = "SELECT * FROM chemistry_detail WHERE furnace_seq = :furnaceSeq " +
    "AND (sample_no LIKE '%T%' OR sample_no LIKE '%P%') AND create_date > GETDATE() - 1 " +
    "ORDER BY id DESC", nativeQuery = true)
  List<ChemistryDetail> findFurnaceLabHis(@Param("furnaceSeq") String furnaceSeq);

  @Query(value = "SELECT * FROM chemistry_detail WHERE furnace_seq = :furnaceSeq " +
    "AND (sample_no LIKE '%T%' OR sample_no LIKE '%L%') AND create_date > GETDATE() - 1 " +
    "ORDER BY id DESC", nativeQuery = true)
  List<ChemistryDetail> findPourLabHis(@Param("furnaceSeq") String furnaceSeq);

  Optional<ChemistryDetail> findByLabId(Integer labId);

  List<ChemistryDetail> findByHeatRecordId(Integer heatRecordId);

  // 按炉号和样号查询最近n天的记录
  @Query(value = "SELECT * FROM chemistry_detail " +
    "WHERE furnace_seq = :furnaceSeq AND sample_no = :sampleNo AND create_date > :lastDay ORDER BY id DESC", nativeQuery = true)
  List<ChemistryDetail> findLatestBySeqNo(@Param("furnaceSeq") String furnaceSeq,
                                          @Param("sampleNo") String sampleNo,
                                          @Param("lastDay") String lastDay);

  @Query(value = "UPDATE chemistry_detail SET heat_record_id = :heatRecordId " +
    "WHERE furnace_seq = :furnaceSeq AND create_date > :createTime", nativeQuery = true)
  @Modifying
  void updateFurnaceSeqByHeatRecordId(@Param("furnaceSeq") String furnaceSeq,
                                      @Param("heatRecordId") Integer heatRecordId,
                                      @Param("createTime") String createTime);

  @Query(value = "UPDATE chemistry_detail SET ladle_id = :ladleId " +
    "WHERE heat_record_id = :heatRecordId AND ladle_id IS NULL AND sample_no LIKE '%L' + :ladleSeq", nativeQuery = true)
  @Modifying
  void updateLadleId(@Param("ladleId") Integer ladleId,
                     @Param("heatRecordId") Integer heatRecordId,
                     @Param("ladleSeq") String ladleSeq);

  @Query(value = "SELECT top 1 * FROM chemistry_detail WHERE furnace_seq = :furnaceSeq AND create_date > GETDATE() - 1 " +
          "AND (sample_no LIKE '%T%') ORDER BY id DESC", nativeQuery = true)
  Optional<ChemistryDetail> findTPotByFurnaceSeq(@Param("furnaceSeq") String furnaceSeq);

  @Query(value = "SELECT top 1 * FROM chemistry_detail WHERE heat_record_id = :heatRecordId " +
          "AND (sample_no LIKE '%T%' OR sample_no LIKE '%L%') ORDER BY id DESC", nativeQuery = true)
  Optional<ChemistryDetail> findChemiByHeatRecordId(Integer heatRecordId);
}
