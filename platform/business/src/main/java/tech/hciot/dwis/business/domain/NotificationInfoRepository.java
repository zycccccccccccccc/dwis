package tech.hciot.dwis.business.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.hciot.dwis.business.domain.model.NotificationInfo;

public interface NotificationInfoRepository extends JpaRepository<NotificationInfo, Integer>, JpaSpecificationExecutor<NotificationInfo> {

}
