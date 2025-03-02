package tech.hciot.dwis.business.infrastructure;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("default")
public class DBKeepAliveScheduledTask {

  public final static long REFRESH_PERIOD = 60 * 1000;

  @Autowired
  private EntityManager entityManager;

  @Scheduled(fixedDelay = REFRESH_PERIOD)
  public void keepAlive() {
    try {
      List<Object> list = entityManager.createNativeQuery("SELECT top 1 id FROM department").getResultList();
      log.info("SELECT for db keep alive");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
