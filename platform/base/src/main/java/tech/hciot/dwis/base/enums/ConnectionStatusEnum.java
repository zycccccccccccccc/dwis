package tech.hciot.dwis.base.enums;

public enum ConnectionStatusEnum {

  ONLINE(0), OFFLINE(1), INSTALLED(2), FAULT(3);

  private int code;

  ConnectionStatusEnum(int code) {
    this.code = code;
  }

  public int getCode() {
    return this.code;
  }
}
