package tech.hciot.dwis.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tech.hciot.dwis.base.configurations.ResourceServerConfiguration;
import tech.hciot.dwis.base.exception.GlobalExceptionHandler;
import tech.hciot.dwis.base.filters.MyCorsFilter;
import tech.hciot.dwis.business.infrastructure.log.EnableApiLog;

@SpringBootApplication
@Import({GlobalExceptionHandler.class, MyCorsFilter.class, ResourceServerConfiguration.class})
@EnableFeignClients
@EnableScheduling
@EntityScan(value = {"tech.hciot.dwis.business.infrastructure", "tech.hciot.dwis.business.domain"})
@EnableApiLog
@EnableAsync
public class BusinessPlatformApplication {

  public static void main(String[] args) {
    SpringApplication.run(BusinessPlatformApplication.class, args);
  }
}
