package tech.hciot.dwis.business.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.TechnicalDocument;

public interface TechnicalDocumentRepository extends JpaRepository<TechnicalDocument, Integer>, JpaSpecificationExecutor<TechnicalDocument> {

  @Query(value = "UPDATE technical_document SET publish_status = 2 WHERE id = :id", nativeQuery = true)
  @Modifying
  void publish(@Param("id") Integer id);

  Optional<TechnicalDocument> findByFilename(String filename);
}
