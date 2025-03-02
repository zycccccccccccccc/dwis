package tech.hciot.dwis.business.infrastructure;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    Method[] methods = BatchProperties.Job.class.getMethods();
    int defaultPoolSize = 3;
    int corePoolSize = 0;
    if (methods != null && methods.length > 0) {
      for (Method method : methods) {
        Scheduled annotation = method.getAnnotation(Scheduled.class);
        if (annotation != null) {
          corePoolSize++;
        }
      }
      if (defaultPoolSize > corePoolSize)
        corePoolSize = defaultPoolSize;
    }
    taskRegistrar.setScheduler(Executors.newScheduledThreadPool(corePoolSize));
  }
}
