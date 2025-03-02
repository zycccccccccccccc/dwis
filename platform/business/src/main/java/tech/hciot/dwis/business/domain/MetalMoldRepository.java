package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tech.hciot.dwis.business.domain.model.MetalMold;

import java.util.List;

public interface MetalMoldRepository extends JpaRepository<MetalMold, Integer>,
    JpaSpecificationExecutor<MetalMold> {

    @Query(value = "SELECT mold_no FROM metal_mold WHERE cd = :cdType AND enabled = :enabled ", nativeQuery = true)
    List<String> findByCdAndEnabled(Integer cdType, Integer enabled);

}
