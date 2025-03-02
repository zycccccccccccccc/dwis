package tech.hciot.dwis.business.infrastructure;

import java.math.BigDecimal;

public class DigitalUtil {

  // 两数相除的百分比
  public static String percentage(Integer dividend, Integer divider) {
    if (divider == null || divider == 0 || dividend == null || dividend == 0) {
      return "0%";
    }
    BigDecimal value = new BigDecimal(100.0 * dividend / divider)
      .setScale(2, BigDecimal.ROUND_HALF_UP)
      .stripTrailingZeros();
    return value.toPlainString() + "%";
  }
}
