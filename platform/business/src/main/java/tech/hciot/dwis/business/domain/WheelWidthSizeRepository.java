package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.WheelWidthSize;

public interface WheelWidthSizeRepository extends JpaRepository<WheelWidthSize, Integer>, JpaSpecificationExecutor<WheelWidthSize> {

  List<WheelWidthSize> findByDesignAndEnabled(String design, Integer enabled);
}
