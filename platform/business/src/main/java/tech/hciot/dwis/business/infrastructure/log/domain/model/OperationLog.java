package tech.hciot.dwis.business.infrastructure.log.domain.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;

  private String username;
  private String operationName;
  private String uri;
  private String parameter;
  private String httpMethod;
  private String requestBody;
  private Integer errorCode;
  private String errorDesc;
  private Integer httpStatus;
  private String operationType;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date operationTime;
  private Long consuming;
  @Convert(converter = JSONObjectAttributeConverter.class)
  private JSON oldValue;
  @Convert(converter = JSONObjectAttributeConverter.class)
  private JSON newValue;

  private static class JSONObjectAttributeConverter implements
    AttributeConverter<JSON, String> {

    @Override
    public String convertToDatabaseColumn(JSON attribute) {
      return attribute == null ? null : attribute.toJSONString();
    }

    @Override
    public JSON convertToEntityAttribute(String dbData) {
      return (JSON) JSON.parse(dbData);
    }
  }
}
