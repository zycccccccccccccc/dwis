package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.MaterialRecord;

public interface MaterialRecordRepository extends JpaRepository<MaterialRecord, Integer>, JpaSpecificationExecutor<MaterialRecord> {

  @Query(value = "SELECT top (:limit) batch_no FROM material_record mr JOIN department d ON mr.dept = d.id " +
    "WHERE mr.material_name LIKE %:materialName% AND d.dep_key = :depKey AND mr.status = 1", nativeQuery = true)
  List<String> findBatchNoByNameAndDept(@Param("materialName") String materialName,
                                        @Param("depKey") String depKey,
                                        @Param("limit") Integer limit);

  List<MaterialRecord> findByIdIn(List<Integer> idList);

  @Query(value = "SELECT DISTINCT material_name FROM material_record WHERE dept = :depId OR :depId = 0", nativeQuery = true)
  List<String> findMaterialNameList(@Param("depId") Integer depId);
}
