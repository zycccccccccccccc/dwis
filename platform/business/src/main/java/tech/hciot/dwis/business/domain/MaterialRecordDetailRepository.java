package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.MaterialRecordDetail;

public interface MaterialRecordDetailRepository extends JpaRepository<MaterialRecordDetail, Integer>, JpaSpecificationExecutor<MaterialRecordDetail> {

}
