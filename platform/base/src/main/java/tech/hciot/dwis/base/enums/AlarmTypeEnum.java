package tech.hciot.dwis.base.enums;

public enum AlarmTypeEnum {
  BATTERY_ALARM("低电告警"), TAMPER_ALARM("防撬告警"),
  LOCKED_ALARM("连续输入错误锁定告警"), HIJACKING_ALARM("挟持开门");

  private String desc;

  AlarmTypeEnum(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return this.desc;
  }
}
