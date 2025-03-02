package tech.hciot.dwis.base.exception;

import org.springframework.http.HttpStatus;

public class PlatformException extends RuntimeException {

  private static final long serialVersionUID = 5923290794010137622L;
  protected int errorCode = -1;
  protected HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

  static public PlatformException badRequestException(String errorMsg) {
    return new PlatformException(HttpStatus.BAD_REQUEST.value(), errorMsg, HttpStatus.BAD_REQUEST);
  }

  public PlatformException() {
    super();
  }

  /**
   * @param errorCode
   * @param errorMsg
   */
  public PlatformException(int errorCode, String errorMsg) {
    super(errorMsg);
    this.errorCode = errorCode;
  }

  public PlatformException(int errorCode, String errorMsg, HttpStatus httpStatus) {
    super(errorMsg);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
