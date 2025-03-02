package tech.hciot.dwis.business.infrastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * int列表类型字段转换类
 */
public class IntegerListAttributeConverter implements AttributeConverter<List<Integer>, String> {
  @Override
  public String convertToDatabaseColumn(List<Integer> depList) {
    if (depList == null || depList.isEmpty()) {
      return "0";
    }
    return "0," + StringUtils.join(depList, ",");
  }

  @Override
  public List<Integer> convertToEntityAttribute(String department) {
    List<Integer> depList = new ArrayList<>();
    if (department == null || department.equals("")) {
      return depList;
    }
    String[] values = department.split(",");
    Arrays.stream(values).forEach(v -> {
      if (!v.equals("0")) {
        depList.add(Integer.parseInt(v));
      }
    });
    return depList;
  }
}
