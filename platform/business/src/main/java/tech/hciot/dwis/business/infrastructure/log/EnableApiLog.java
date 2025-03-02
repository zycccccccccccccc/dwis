package tech.hciot.dwis.business.infrastructure.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tech.hciot.dwis.business.application.OperationLogService;
import tech.hciot.dwis.business.infrastructure.log.aspect.OperationLogAspect;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({OperationLogAspect.class, OperationLogService.class})
@EnableJpaRepositories(basePackages = "tech.hciot.dwis")
public @interface EnableApiLog {

}
