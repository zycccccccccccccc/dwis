package tech.hciot.dwis.base.util;

import com.alibaba.fastjson.JSONObject;
import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class JsonObjectAttributeConverter implements AttributeConverter<JSONObject, String> {

  @Override
  public String convertToDatabaseColumn(JSONObject attribute) {
    return attribute != null ? attribute.toJSONString() : null;
  }

  @Override
  public JSONObject convertToEntityAttribute(String dbData) {
    return StringUtils.isEmpty(dbData) ? new JSONObject() : JSONObject.parseObject(dbData);
  }
}
