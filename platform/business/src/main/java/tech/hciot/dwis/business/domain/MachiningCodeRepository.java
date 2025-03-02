package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.MachiningCode;

import java.util.List;

public interface MachiningCodeRepository extends JpaRepository<MachiningCode, Integer>, JpaSpecificationExecutor<MachiningCode> {

  @Query(value = "SELECT machining_code FROM machining_code WHERE [procedure] = :procedure AND parameter = :parameter AND location = :location " +
          "AND enabled = :enabled AND large_bore != 1 ORDER BY id ASC ", nativeQuery = true)
  List<String> findBasicCodeList(@Param("procedure") String procedure,
                                        @Param("parameter") String parameter,
                                        @Param("location") String location,
                                        @Param("enabled") Integer enabled);
}
