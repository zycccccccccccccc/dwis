package tech.hciot.dwis.base.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

public class CommonUtil {

  public static Double getDoubleValue(BigDecimal decimal) {
    return decimal == null ? 0 : decimal.doubleValue();
  }

  public static String getStringValueWithScale(BigDecimal decimal, int scale) {
    return decimal == null ? "" : decimal.setScale(scale, RoundingMode.HALF_UP).toPlainString();
  }

  public static String getStringValueOfDecimal(BigDecimal decimal) {
    return getStringValueWithScale(decimal, 3);
  }

  public static String getStringValue(Object obj) {
    return obj == null ? null : obj.toString();
  }

  public static String getStringValueOfDate(Date date, String pattern) {
    return date == null ? null : DateFormatUtils.format(date, pattern);
  }
}
