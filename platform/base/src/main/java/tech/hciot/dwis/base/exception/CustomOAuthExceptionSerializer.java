package tech.hciot.dwis.base.exception;


import static tech.hciot.dwis.base.exception.ErrorEnum.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class CustomOAuthExceptionSerializer extends StdSerializer<CustomOAuth2Exception> {

  private static final long serialVersionUID = -2805650858094650403L;

  public CustomOAuthExceptionSerializer() {
    super(CustomOAuth2Exception.class);
  }

  @Override
  public void serialize(CustomOAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeStartObject();
    ErrorEnum errorEnum;
    try {
      errorEnum = ErrorEnum.valueOf(value.getOAuth2ErrorCode().toUpperCase());
    } catch (IllegalArgumentException e) {
      errorEnum = INTERNAL_SERVER_ERROR;
    }

    gen.writeNumberField("errorCode", errorEnum.getErrorcode());
    gen.writeStringField("errorDesc", errorEnum.getErrordesc());
    gen.writeEndObject();
  }
}
