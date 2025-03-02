package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.sql.Time;

public class CustomerMinuteSecondSerialize extends JsonSerializer<Time> {

  @Override
  public void serialize(Time arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
    if (arg0 != null) {
      arg1.writeString(arg0.toString().substring(3));
    }
  }
}
