package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.CaseUnpackTimeCtl;

public interface CaseUnpackTimeCtlRepository extends JpaRepository<CaseUnpackTimeCtl, Integer>, JpaSpecificationExecutor<CaseUnpackTimeCtl> {

  @Query(value = "SELECT fminite FROM case_unpack_time_ctl WHERE type_kxsj = :typeKxsj AND temp_min <= :temp AND temp_max > :temp", nativeQuery = true)
  Optional<Integer> computeUnpackDelayMinute(@Param("typeKxsj") String typeKxsj, @Param("temp") Double temp);

}
