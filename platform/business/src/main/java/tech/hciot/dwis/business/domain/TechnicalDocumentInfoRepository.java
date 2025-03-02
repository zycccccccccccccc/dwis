package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.TechnicalDocumentInfo;

public interface TechnicalDocumentInfoRepository extends JpaRepository<TechnicalDocumentInfo, Integer>, JpaSpecificationExecutor<TechnicalDocumentInfo> {

}
