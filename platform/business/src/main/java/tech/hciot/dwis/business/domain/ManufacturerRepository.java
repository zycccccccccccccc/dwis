package tech.hciot.dwis.business.domain;

import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Integer>, JpaSpecificationExecutor<Manufacturer> {

  @Query(value = "SELECT top (:limit) id, name FROM manufacturer WHERE name LIKE :name% ORDER BY name", nativeQuery = true)
  List<Map<String, Object>> findManufacturerIdNameList(@Param("name") String name, @Param("limit") Integer limit);

  @Query(value = "SELECT * FROM manufacturer WHERE ',' + product_type_id + ',' LIKE %:productTypeId% AND enabled = 1", nativeQuery = true)
  List<Manufacturer> findManufacturerList(@Param("productTypeId") String productTypeId);
}
