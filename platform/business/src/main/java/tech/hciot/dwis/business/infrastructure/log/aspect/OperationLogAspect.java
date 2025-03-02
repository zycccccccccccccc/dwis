package tech.hciot.dwis.business.infrastructure.log.aspect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.hciot.dwis.base.exception.PlatformException;
import tech.hciot.dwis.base.jwt.JwtTokenUser;
import tech.hciot.dwis.business.application.OperationLogService;
import tech.hciot.dwis.business.application.TableInfoService;
import tech.hciot.dwis.business.infrastructure.UnderlineCamelUtil;
import tech.hciot.dwis.business.infrastructure.log.domain.model.OperationLog;

@Component
@Aspect
@Slf4j
public class OperationLogAspect {

  private final static String SENSITIVE_DATA_URI = "/dwis/user/resetpass,/dwis/user/forgetpass";
  private static DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private OperationLogService operationLogService;

  @Autowired
  private TableInfoService tableInfoService;

  @Pointcut("@annotation(tech.hciot.dwis.business.infrastructure.log.aspect.Log)")
  private void pointcut() {
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {

    long beginTime = System.currentTimeMillis();
    Object result;
    Integer errorCode = 0;
    String errorDesc = null;
    Integer httpStatus = 200;
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    Method method = getMethod(point);
    OperationLog operationLog = OperationLog.builder()
      .username(getUsername())
      .operationName(getOperationName(method))
      .uri(request.getRequestURI())
      .parameter(request.getQueryString())
      .httpMethod(getHttpMethod(method))
      .requestBody(getRequestBody(request, request.getRequestURI()))
      .operationType(getOperationType(method))
      .operationTime(new Date(beginTime))
      .build();
    updateOldAndNewValue(operationLog, point);
    try {
      result = point.proceed();
    } catch (PlatformException e) {
      errorCode = e.getErrorCode();
      errorDesc = e.getMessage();
      httpStatus = e.getHttpStatus().value();
      throw e;
    } finally {
      long consuming = System.currentTimeMillis() - beginTime;
      operationLog.setErrorCode(errorCode);
      operationLog.setErrorDesc(errorDesc);
      operationLog.setHttpStatus(httpStatus);
      operationLog.setConsuming(consuming);
      operationLogService.add(operationLog);
    }
    return result;
  }

  private String getUsername() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof JwtTokenUser) {
      return ((JwtTokenUser) principal).getUsername();
    } else {
      return principal.toString();
    }
  }

  private Method getMethod(ProceedingJoinPoint point) {
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();
    return method;
  }

  private String getHttpMethod(Method method) {
    Annotation[] annotations = method.getAnnotations();
    try {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().getName().endsWith("GetMapping")) {
        return "GET";
      } else if (annotation.annotationType().getName().endsWith("PostMapping")) {
        return "POST";
      } else if (annotation.annotationType().getName().endsWith("PutMapping")) {
        return "PUT";
      } else if (annotation.annotationType().getName().endsWith("DeleteMapping")) {
        return "DELETE";
      } else if (annotation.annotationType().getName().endsWith("RequestMapping")) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        RequestMethod requestMethod = requestMapping.method()[0];
        return requestMethod.toString();
      }
    }
    } catch (Exception e) {
      log.error("getHttpMethod failed: {}", e.getMessage());
    }
    return "GET";
  }

  private String getOperationName(Method method) {
    String operationName = "";
    Log log = method.getAnnotation(Log.class);
    operationName = log.name();
    if (StringUtils.isEmpty(operationName)) {
      ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
      if (apiOperation != null) {
        operationName = apiOperation.value();
      }
    }
    return operationName;
  }

  private String getOperationType(Method method) {
    String operationType = "";
    Log log = method.getAnnotation(Log.class);
    operationType = log.type();
    return operationType;
  }

  private String getRequestBody(HttpServletRequest request, String requestURI) {
    String requestBody = null;
    if (!SENSITIVE_DATA_URI.contains(requestURI)) {
      requestBody = (String) request.getAttribute("request-body");
    }
    return requestBody;
  }

  // 从表中查询更新前或删除前的值oldValue，从接口中获取更新后或添加后的值newValue，都添加到操作日志中
  private void updateOldAndNewValue(OperationLog operationLog, ProceedingJoinPoint point) {
    try {
    MethodSignature signature = (MethodSignature) point.getSignature();
    Method method = signature.getMethod();
    String tableName = getOperationObject(signature);
    Parameter[] parameters = method.getParameters();
    Object id = null;
    Object newObject = null;
    for (int i = 0; i < parameters.length; i++) {
      for (Annotation annotation : parameters[i].getAnnotations()) {
        if (annotation.annotationType() == Id.class) {
          Object[] paramId = point.getArgs();
          id = paramId[i];
        } else if (annotation.annotationType() == Request.class) {
          Object[] paramRequest = point.getArgs();
          newObject = paramRequest[i];
        }
      }
    }
    //由于之前开发时候，有的controller命名不规范，导致保存操作记录时解析表名称不正确。在此，进行修复如下;
    if (point.toString().contains("LowController.modifyHeat")) { tableName = "heat"; }
    if (point.toString().contains("LabController.modify")) { tableName = "chemistry_detail"; }
    if (point.toString().contains("AccountController.modify")) { id = "'" + id.toString() + "'"; }

    Map<String, Object> oldObject = tableInfoService.findByTableNameAndId(tableName, id);
    if (oldObject != null) {
      updateTimeFormat(oldObject);
      operationLog.setOldValue(new JSONObject(oldObject));
    }
    if (newObject != null) {
      if (newObject instanceof List) {
        JSONArray newObjectJsonArray = (JSONArray) JSONArray.toJSON(newObject);
        newObjectJsonArray.forEach(newObjectJson -> {
          updateTimeFormat((Map) newObjectJson);
        });
        operationLog.setNewValue(newObjectJsonArray);
      } else {
          JSONObject newObjectJson = (JSONObject) JSONObject.toJSON(newObject);
          updateTimeFormat(newObjectJson);
        operationLog.setNewValue(newObjectJson);
      }
    }
//    log.info("old_value: {}", operationLog.getOldValue());
//    log.info("new_value: {}", operationLog.getNewValue());
    } catch (Exception e) {
      log.error("parse old_value and new_value failed");
      log.error(e.getMessage(), e);
    }
  }

  private JSONObject parseOldObjectToJson(Map<String, Object> map, String tableName) {
    if (map == null) {
      log.info("no old object");
      return null;
    }
    Map<String, Object> objectMap = new HashMap<>();
    Map<String, String> columnMap = tableInfoService.getColumnMap(tableName);
    map.entrySet().forEach(o -> {
      String columnDesc = columnMap.get(o.getKey());
      if (columnDesc != null && o.getValue() != null) {
        objectMap.put(columnDesc, o.getValue());
      }
    });
    JSONObject jsonObj = new JSONObject(objectMap);
    return jsonObj;
  }

  private JSONObject parseNewObjectToJson(Object request, String tableName) {
    if (request == null) {
      log.info("no request");
      return null;
    }
    Map<String, Object> objectMap = new HashMap<>();
    Map<String, String> columnMap = tableInfoService.getColumnMap(tableName);
    if (columnMap == null) {
      return null;
    }
    Field[] fields = request.getClass().getDeclaredFields();
    for (Field field : fields) {
      String fieldName = field.getName();
      Object fieldValue = null;
      try {
        field.setAccessible(true);
        fieldValue = field.get(request);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      String columnDesc = columnMap.get(UnderlineCamelUtil.humpToUnderline(fieldName));
      if (columnDesc != null && fieldValue != null) {
        columnDesc = columnDesc.split(",")[0];
        objectMap.put(columnDesc, fieldValue);
      }
    }
    JSONObject jsonObj = new JSONObject(objectMap);
    System.out.println(jsonObj.toJSONString());
    return jsonObj;
  }

  private String getOperationObject(MethodSignature signature) {
    OperationObject object = (OperationObject) signature.getDeclaringType().getAnnotation(OperationObject.class);
    String objectStr;
    if (object != null) {
      objectStr = object.value();
    } else {
      objectStr = signature.getDeclaringType().getSimpleName().replace("Controller", "");
    }
    objectStr = UnderlineCamelUtil.humpToUnderline(objectStr).substring(1);
//    log.info("OperationObject: {}", objectStr);
    return objectStr;
  }

  // 修改时间格式，时间戳改成时间格式
  private void updateTimeFormat(Map<String, Object> object) {
    object.entrySet().forEach(entry -> {
      Object value = entry.getValue();
      if (value instanceof Date) {
        object.put(entry.getKey(), timeFormat.format((Date) value));
      }
    });
  }
}
