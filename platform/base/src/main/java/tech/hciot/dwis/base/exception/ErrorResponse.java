package tech.hciot.dwis.base.exception;

public class ErrorResponse {

  private int errorCode;
  private String errorDesc;

  public ErrorResponse() {
  }

  public ErrorResponse(int errorCode, String errorDesc) {
    this.errorCode = errorCode;
    this.errorDesc = errorDesc;
  }

  public void setPlatformException(PlatformException e) {
    this.errorCode = e.getErrorCode();
    this.errorDesc = e.getMessage();
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorDesc() {
    return errorDesc;
  }

  public void setErrorDesc(String errorDesc) {
    this.errorDesc = errorDesc;
  }
}
