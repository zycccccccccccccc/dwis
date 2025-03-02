package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.LadleRecord;

public interface LadleRecordRepository extends JpaRepository<LadleRecord, Integer>, JpaSpecificationExecutor<LadleRecord> {

  List<LadleRecord> findByHeatRecordId(Integer id);

  int countByHeatRecordId(Integer id);

  Optional<LadleRecord> findByHeatRecordIdAndLadleSeq(Integer heatRecordId, Integer ladleSeq);

  Optional<LadleRecord> findByLadleRecordKey(String ladleRecordKey);
}
