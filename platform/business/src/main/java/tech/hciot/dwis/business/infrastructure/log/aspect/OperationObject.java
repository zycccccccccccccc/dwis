package tech.hciot.dwis.business.infrastructure.log.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OperationObject {

  /**
   * 操作对象名称
   */
  String value() default "";
}
