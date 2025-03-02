package tech.hciot.dwis.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardTimeUtil {

  private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private static DateFormat slashDateFormat = new SimpleDateFormat("yyyy/MM/dd");
  private static DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
  private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");

  public static Date parseTime(String timeStr) {
    if (timeStr != null) {
      try {
        return dateTimeFormat.parse(timeStr);
      } catch (Exception e) {
        log.error(" time string {} parsed failed: {}", timeStr, e.getMessage());
      }
    }
    return null;
  }

  public static Date parseDate(String dateStr) {
    if (dateStr != null) {
      try {
        return dateFormat.parse(dateStr);
      } catch (Exception e) {
        log.error(" date string {} parsed failed: {}", dateStr, e.getMessage());
      }
    }
    return null;
  }

  public static String dateTimeStr(Date date) {
    if (date != null) {
      return dateTimeFormat.format(date);
    }
    return "";
  }

  public static String dateStr(Date date) {
    if (date != null) {
      return dateFormat.format(date);
    }
    return "";
  }

  public static String simpleDateStr(Date date) {
    if (date != null) {
      return simpleDateFormat.format(date);
    }
    return "";
  }

  public static String dateStr(long time) {

    return dateStr(new Date(time));
  }

  public static String timeStr(Date date) {
    if (date != null) {
      return timeFormat.format(date);
    }
    return "";
  }

  public static String toTimeStr(long timestamp) {
    return dateTimeFormat.format(new Date(timestamp));
  }

  // yyyy/M/d格式字符串
  public static String shippedDate(String originShippedDate) {
    try {
      DateFormat originTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = originTimeFormat.parse(originShippedDate);
      DateFormat newTimeFormat = new SimpleDateFormat("yyyy/M/d");
      return newTimeFormat.format(date);
    } catch (ParseException e) {
      log.error("Certificate shipped date parse failed: {}", originShippedDate);
      return "";
    }
  }

  // yyyy/MM/dd格式字符串
  public static String slashDate(String dateStr) {
    try {
      DateFormat originTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = originTimeFormat.parse(dateStr);
      DateFormat newTimeFormat = new SimpleDateFormat("yyyy/MM/dd");
      return newTimeFormat.format(date);
    } catch (ParseException e) {
      return "";
    }
  }

  // yyyy年M月d日格式字符串
  public static String shippedDateCH(String originShippedDate) {
    try {
      DateFormat originTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = originTimeFormat.parse(originShippedDate);
      DateFormat newTimeFormat = new SimpleDateFormat("yyyy年M月d日");
      return newTimeFormat.format(date);
    } catch (ParseException e) {
      log.error("Certificate shipped date parse failed: {}", originShippedDate);
      return "";
    }
  }

  // 生成开始日期到结束日期之间的所有日期
  // 输入日期格式：yyyy-MM-dd
  // 返回日期格式：yyyy/MM/dd
  public static List<String> generateDateList(String beginDate, String endDate) {
    beginDate = convertDateFormat(beginDate);
    endDate = convertDateFormat(endDate);
    List<String> dateList = new ArrayList<>();
    try {
      String dateStr = beginDate;
      SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
      while (dateStr.compareTo(endDate) <= 0) {
        dateList.add(dateStr);
        Date date = format.parse(dateStr);
        Date nextDate = new Date(date.getTime() + 1000 * 60 * 60 * 24); // 加1天
        dateStr = format.format(nextDate);
      }
    } catch (ParseException e) {
      log.error(e.getMessage());
    }
    return dateList;
  }

  // yyyy-MM-dd格式日期转换成yyyy/MM/dd日期
  private static String convertDateFormat(String date) {
    try {
      return new SimpleDateFormat("yyyy/MM/dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(date));
    } catch (ParseException e) {
      log.error(e.getMessage());
    }
    return "";
  }

  // 获取前一天
  public static String beforeDay(String dateStr) {
    try {
      Date currentDate = dateFormat.parse(dateStr);
      Calendar c = Calendar.getInstance();
      c.setTime(currentDate);
      c.add(Calendar.DAY_OF_MONTH, -1);
      Date nextDate = c.getTime();
      return dateFormat.format(nextDate);
    } catch (ParseException e) {
      log.error(e.getMessage());
    }
    return "";
  }

  // 获取下一天
  public static String nextDay(String dateStr) {
    try {
      Date currentDate = dateFormat.parse(dateStr);
      Calendar c = Calendar.getInstance();
      c.setTime(currentDate);
      c.add(Calendar.DAY_OF_MONTH, 1);
      Date nextDate = c.getTime();
      return dateFormat.format(nextDate);
    } catch (ParseException e) {
      log.error(e.getMessage());
    }
    return "";
  }
}
