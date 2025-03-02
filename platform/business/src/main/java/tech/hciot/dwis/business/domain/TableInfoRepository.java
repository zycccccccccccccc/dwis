package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.TableInfo;

public interface TableInfoRepository extends JpaRepository<TableInfo, Integer>, JpaSpecificationExecutor<TableInfo> {

  @Query(value = "SELECT * FROM v_table_info WHERE table_name LIKE :tableName% ", nativeQuery = true)
  List<TableInfo> findByTableName(@Param("tableName") String tableName);
}
