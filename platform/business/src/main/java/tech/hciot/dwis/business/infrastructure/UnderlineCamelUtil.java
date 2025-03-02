package tech.hciot.dwis.business.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * 下划线和驼峰互转工具
 */
public class UnderlineCamelUtil {

  private static Pattern humpPattern = Pattern.compile("[A-Z]");

  // 驼峰格式转下划线分割
  public static String humpToUnderline(String str) {
    Matcher matcher = humpPattern.matcher(str);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  // 下划线转驼峰（首字母小写）
  public static String underlineToHump(String str) {
    if (StringUtils.isEmpty(str)) {
      return "";
    }
    int len = str.length();
    StringBuilder strb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char c = str.charAt(i);
      if (c == '_' && (++i) < len) {
        c = str.charAt(i);
        strb.append(Character.toUpperCase(c));
      } else {
        strb.append(c);
      }
    }
    return strb.toString();
  }

  // 下划线key的Map转为驼峰key（首字母小写）的Map
  public static Map<String, Object> underlineMapToHumpMap(Map<String, Object> map) {
    Map<String, Object> newMap = new HashMap<>();
    map.entrySet().forEach(entry -> {
      String key = underlineToHump(entry.getKey());
      Object value = entry.getValue();
      newMap.put(key, value);
    });
    return newMap;
  }
}
