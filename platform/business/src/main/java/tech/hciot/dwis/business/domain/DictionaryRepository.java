package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Dictionary;

public interface DictionaryRepository extends JpaRepository<Dictionary, Integer>, JpaSpecificationExecutor<Dictionary> {
    Optional<Dictionary> findByTableName(String tableName);

    @Query(value = "SELECT * FROM dictionary WHERE table_name LIKE %:tableName% OR memo LIKE %:tableName%", nativeQuery = true)
    List<Dictionary> findListByName(@Param("tableName") String tableName);
}
