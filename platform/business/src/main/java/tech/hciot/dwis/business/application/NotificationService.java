package tech.hciot.dwis.business.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.hciot.dwis.business.domain.NotificationInfoRepository;
import tech.hciot.dwis.business.domain.NotificationRepository;
import tech.hciot.dwis.business.domain.NotifyStatusRepository;
import tech.hciot.dwis.business.domain.model.Notification;
import tech.hciot.dwis.business.domain.model.NotificationInfo;
import tech.hciot.dwis.business.domain.model.NotifyStatus;

@Service
@Slf4j
public class NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private NotificationInfoRepository notificationInfoRepository;

  @Autowired
  private NotifyStatusRepository notifyStatusRepository;

  public Page<NotificationInfo> find(String accountId, Integer currentPage, Integer pageSize) {
    Specification<NotificationInfo> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      if (accountId != null) {
        list.add(criteriaBuilder.equal(root.get("accountId"), accountId));
      }
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return notificationInfoRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Page<Notification> findForMgr(Integer currentPage, Integer pageSize) {
    Specification<Notification> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> list = new ArrayList<>();
      query.where(criteriaBuilder.and(list.toArray(new Predicate[0])));
      query.orderBy(criteriaBuilder.desc(root.get("createTime")));
      return query.getRestriction();
    };
    return notificationRepository.findAll(specification, PageRequest.of(currentPage, pageSize));
  }

  public Notification add(Notification notification) {
    notification.setCreateTime(new Date());
    notification.setPublishStatus(Notification.STATUS_UNPUBLISH);
    return notificationRepository.save(notification);
  }

  @Transactional
  public Notification modify(Integer id, Notification newNotification) {
    Notification notification = notificationRepository.findById(id).get();
    if (notification.getPublishStatus() == Notification.STATUS_PUBLISHED) {
      List<Integer> newDepList = newNotification.getDepartment();
      List<Integer> oldDepList = notification.getDepartment();
      List<Integer> removedDepList = new ArrayList<>();
      oldDepList.forEach(dep -> {
        if (!newDepList.contains(dep)) {
          removedDepList.add(dep);
        }
      });
      notificationRepository.deleteNotifyStatusByDep(id, NotifyStatus.TYPE_NOTIFICATION, removedDepList);
    }
    BeanUtil.copyProperties(newNotification, notification, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
    return notificationRepository.save(notification);
  }

  @Transactional
  public void delete(Integer id) {
    notifyStatusRepository.deleteByNotifyIdAndNotifyType(id, 1);
    notificationRepository.deleteById(id);
  }

  @Transactional
  public void publish(Integer id) {
    notificationRepository.publish(id);
  }

  @Transactional
  public Notification findById(Integer id, String accountId) {
    notifyStatusRepository.read(id, NotifyStatus.TYPE_NOTIFICATION, accountId);
    return notificationRepository.findById(id).get();
  }
}
