package tech.hciot.dwis.base.util;

import com.alibaba.fastjson.JSONArray;
import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class JsonArrayAttributeConverter implements AttributeConverter<JSONArray, String> {

  @Override
  public String convertToDatabaseColumn(JSONArray attribute) {
    return attribute != null ? attribute.toJSONString() : null;
  }

  @Override
  public JSONArray convertToEntityAttribute(String dbData) {
    return StringUtils.isEmpty(dbData) ? new JSONArray() : JSONArray.parseArray(dbData);
  }

}
