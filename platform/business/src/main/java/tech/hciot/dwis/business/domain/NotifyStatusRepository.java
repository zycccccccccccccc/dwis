package tech.hciot.dwis.business.domain;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.hciot.dwis.business.domain.model.NotifyStatus;

public interface NotifyStatusRepository extends JpaRepository<NotifyStatus, Integer>, JpaSpecificationExecutor<NotifyStatus> {

  @Query(value = "SELECT COUNT(1) FROM notify_status WHERE read_status = 1 AND account_id = :accountId", nativeQuery = true)
  BigDecimal unreadCount(@Param("accountId") String accountId);

  @Query(value = "SELECT id FROM notification n WHERE publish_status = 2 AND ',' + department + ',' LIKE %:department% "
        + "AND NOT EXISTS (SELECT 1 FROM notify_status ns WHERE n.id = ns.notify_id AND ns.notify_type = 1 AND ns.account_id = :accountId) ",
      nativeQuery = true)
  List<Integer> getNewNotificationIdList(@Param("accountId") String accountId, @Param("department") String department);

  @Query(value = "SELECT id FROM technical_document t WHERE publish_status = 2 AND ',' + department + ',' LIKE %:department% "
    + "AND NOT EXISTS (SELECT 1 FROM notify_status ns WHERE t.id = ns.notify_id AND ns.notify_type = 2 AND ns.account_id = :accountId) ",
    nativeQuery = true)
  List<Integer> getNewTechnicalDocumentIdList(@Param("accountId") String accountId, @Param("department") String department);

  @Query(value = "UPDATE notify_status SET read_status = 2 WHERE notify_id = :id " +
    "AND notify_type = :notifyType AND account_id = :accountId", nativeQuery = true)
  @Modifying
  void read(@Param("id") Integer id,
                  @Param("notifyType") Integer notifyType,
                  @Param("accountId") String accountId);

  @Query(value = "DELETE FROM notify_status WHERE notify_id = :id AND notify_type = :notifyType", nativeQuery = true)
  @Modifying
  void deleteByNotifyIdAndNotifyType(@Param("id") Integer id, @Param("notifyType") Integer notifyType);
}
