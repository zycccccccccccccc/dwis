package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.ProductType;

public interface ProductTypeRepository extends JpaRepository<ProductType, Integer>, JpaSpecificationExecutor<ProductType> {

  @Query(value = "SELECT * FROM product_type WHERE ',' + dep_id + ',' LIKE %:depId% AND enabled = 1", nativeQuery = true)
  List<ProductType> findProductTypeList(@Param("depId") String depId);
}
