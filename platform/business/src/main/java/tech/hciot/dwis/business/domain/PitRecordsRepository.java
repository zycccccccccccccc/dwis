package tech.hciot.dwis.business.domain;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.PitRecords;

public interface PitRecordsRepository extends JpaRepository<PitRecords, Integer>, JpaSpecificationExecutor<PitRecords> {

  List<PitRecords> findByOutPitDTCalLessThanAndOutPitDTActIsNull(Date current);

  List<PitRecords> findByOutPitDTActIsNull();

  Optional<PitRecords> findByPitSeq(Integer pitSeq);
}
