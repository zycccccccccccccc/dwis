package tech.hciot.dwis.business.infrastructure.log.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import tech.hciot.dwis.business.infrastructure.log.OperationType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Log {

  /**
   * 操作名称
   */
  String name() default "";

  /**
   * 操作类型，包括查询、添加、修改、删除等
   * @see OperationType
   */
  String type() default "";

  /**
   * 操作对象，对应数据库的表名
   */
  String operationObject() default "";
}
