package tech.hciot.dwis.business.application;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hciot.dwis.business.domain.NotifyStatusRepository;
import tech.hciot.dwis.business.domain.model.NotifyStatus;

@Service
@Slf4j
public class DashboardService {

  @Autowired
  private NotifyStatusRepository notifyStatusRepository;

  public Integer unreadCount(String accountId, Integer depId) {
    List<NotifyStatus> notifyStatusList = new ArrayList<>();
    String department = "," + depId + ",";
    List<Integer> newNotificationIdList = notifyStatusRepository.getNewNotificationIdList(accountId, department);
    newNotificationIdList.forEach(id -> {
      NotifyStatus notifyStatus = NotifyStatus.builder()
        .notifyId(id)
        .notifyType(NotifyStatus.TYPE_NOTIFICATION)
        .build();
      notifyStatusList.add(notifyStatus);
    });

    List<Integer> newTechnicalDocumentIdList = notifyStatusRepository.getNewTechnicalDocumentIdList(accountId,department);
    newTechnicalDocumentIdList.forEach(id -> {
      NotifyStatus notifyStatus = NotifyStatus.builder()
        .notifyId(id)
        .notifyType(NotifyStatus.TYPE_TECHNOLIGY_DOCUMENT)

        .build();
      notifyStatusList.add(notifyStatus);
    });

    notifyStatusList.forEach(notifyStatus -> notifyStatus.setAccountId(accountId));
    notifyStatusRepository.saveAll(notifyStatusList);
    return notifyStatusRepository.unreadCount(accountId).intValue();
  }
}
