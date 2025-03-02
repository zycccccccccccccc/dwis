package tech.hciot.dwis.business.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer>, JpaSpecificationExecutor<Notification> {

  @Query(value = "UPDATE notification SET publish_status = 2 WHERE id = :id", nativeQuery = true)
  @Modifying
  void publish(@Param("id") Integer id);

  @Query(value = "DELETE FROM notify_status WHERE notify_id = :notifyId AND notify_type = :notifyType AND account_id IN "
  + "(SELECT id FROM account WHERE dep_id IN (:depId))", nativeQuery = true)
  @Modifying
  void deleteNotifyStatusByDep(@Param("notifyId") Integer notifyId,
                               @Param("notifyType") Integer notifyType,
                               @Param("depId") List<Integer> depId);
}
