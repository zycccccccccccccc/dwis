package tech.hciot.dwis.lab.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.lab.domain.model.Measurements;

import java.util.Optional;

public interface MearurementsRepository extends JpaRepository<Measurements, String>, JpaSpecificationExecutor<Measurements> {

    @Query(value = "SELECT TOP 1 * FROM Measurements WHERE Status = 0 AND (ID4 LIKE 'P%' OR ID4 LIKE 'L%') ORDER BY ID DESC", nativeQuery = true)
    Optional<Measurements> findNewest();
}