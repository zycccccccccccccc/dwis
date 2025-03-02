package tech.hciot.dwis.base.exception;


import static tech.hciot.dwis.base.exception.ErrorEnum.ACCESS_DENIED;
import static tech.hciot.dwis.base.exception.ErrorEnum.INTERNAL_SERVER_ERROR;
import static tech.hciot.dwis.base.exception.ErrorEnum.PARAM_ILLEGAL;
import static tech.hciot.dwis.base.exception.ErrorEnum.REQUEST_PARAM_IS_INVALID;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler
  @SuppressWarnings("unchecked")
  ResponseEntity handlePlatformException(PlatformException e) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setPlatformException(e);
    return new ResponseEntity(errorResponse, e.getHttpStatus());
  }

  @ExceptionHandler
  ResponseEntity handleValidateException(MethodArgumentNotValidException e) {
    ErrorResponse errorResponse = new ErrorResponse(PARAM_ILLEGAL.getErrorcode(),
        StringUtils.join(e.getBindingResult().getFieldError().getField(), " ",
            e.getBindingResult().getFieldError().getDefaultMessage()));
    return new ResponseEntity(errorResponse, PARAM_ILLEGAL.getHttpstatus());
  }

  @ExceptionHandler
  ResponseEntity handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
    ErrorResponse errorResponse = new ErrorResponse(PARAM_ILLEGAL.getErrorcode(),
        StringUtils.join("缺少请求参数：" + e.getParameterName()));
    return new ResponseEntity(errorResponse, PARAM_ILLEGAL.getHttpstatus());
  }

  @ExceptionHandler
  ResponseEntity handleAccessDeniedException(AccessDeniedException e) {
    ErrorResponse errorResponse = new ErrorResponse(ACCESS_DENIED.getErrorcode(),
        ACCESS_DENIED.getErrordesc());
    return new ResponseEntity(errorResponse, ACCESS_DENIED.getHttpstatus());
  }

  @ExceptionHandler
  ResponseEntity handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
    ErrorResponse errorResponse = new ErrorResponse(REQUEST_PARAM_IS_INVALID.getErrorcode(),
        REQUEST_PARAM_IS_INVALID.getErrordesc());
    return new ResponseEntity(errorResponse, REQUEST_PARAM_IS_INVALID.getHttpstatus());
  }

  @ExceptionHandler
  @SuppressWarnings("unchecked")
  ResponseEntity handleException(Exception e) {
    log.error(e.getMessage(), e);
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setPlatformException(INTERNAL_SERVER_ERROR.getPlatformException());
    return new ResponseEntity(errorResponse, INTERNAL_SERVER_ERROR.getHttpstatus());
  }
}
