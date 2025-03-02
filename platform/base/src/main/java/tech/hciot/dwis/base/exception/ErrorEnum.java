package tech.hciot.dwis.base.exception;

import org.springframework.http.HttpStatus;

public enum ErrorEnum {
  INTERNAL_SERVER_ERROR(500, "服务暂时不可用，请稍后再试", HttpStatus.INTERNAL_SERVER_ERROR),
  PARAM_ILLEGAL(900, "参数不合法", HttpStatus.BAD_REQUEST),
  ALI_API_ERROR(999, "Ali Api 调用错误", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_GRANT(1103, "用户名或密码错误", HttpStatus.BAD_REQUEST),
  INVALID_TOKEN(1104, "Token无效", HttpStatus.UNAUTHORIZED),
  ACCESS_DENIED(1105, "禁止访问", HttpStatus.FORBIDDEN),
  INVALID_CLIENT(1106, "客户端身份无效", HttpStatus.UNAUTHORIZED),
  UNAUTHORIZED(1107, "未获得授权", HttpStatus.UNAUTHORIZED),
  UNSUPPORTED_GRANT_TYPE(1108, "不支持的授权类型", HttpStatus.BAD_REQUEST),
  INVALID_REQUEST(1109, "无效请求", HttpStatus.BAD_REQUEST),
  ACCOUNT_DISABLED(1110, "账号已禁用", HttpStatus.BAD_REQUEST),
  WX_GET_SESSION_FAILED(1111, "获取微信用户登录状态失败", HttpStatus.BAD_REQUEST),
  WX_NEED_MOBILE(1112, "手机号或者微信账号OpenID无效", HttpStatus.BAD_REQUEST),
  USER_VERIFY_IS_INVALID(1384, "验证码无效", HttpStatus.BAD_REQUEST),
  REQUEST_PARAM_IS_INVALID(400, "请求参数不合法", HttpStatus.BAD_REQUEST);

  private int errorcode;
  private String errordesc;
  private HttpStatus httpstatus;

  ErrorEnum(int errorcode, String errordesc, HttpStatus httpstatus) {
    this.errorcode = errorcode;
    this.errordesc = errordesc;
    this.httpstatus = httpstatus;
  }

  public int getErrorcode() {
    return errorcode;
  }

  public String getErrordesc() {
    return errordesc;
  }

  public HttpStatus getHttpstatus() {
    return httpstatus;
  }

  public PlatformException getPlatformException() {
    return new PlatformException(errorcode, errordesc, httpstatus);
  }
}
