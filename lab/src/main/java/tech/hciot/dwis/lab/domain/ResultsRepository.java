package tech.hciot.dwis.lab.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.lab.domain.model.Results;
import java.util.List;

public interface ResultsRepository extends JpaRepository<Results, String>, JpaSpecificationExecutor<Results> {

    @Query(value = "SELECT * FROM Results WHERE Measurement = :measurementId ORDER BY Row DESC", nativeQuery = true)
    List<Results> findByMeasurement(@Param("measurementId") Integer measurementId);
}
