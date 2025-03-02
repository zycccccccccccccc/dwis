package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.sql.Time;

public class CustomerMinuteSecondDeserialize extends JsonDeserializer<Time> {

  @Override
  public Time deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String value = p.getText();
    String[] timeStr = value.split(":");
    return new Time(0, Integer.parseInt(timeStr[0]), Integer.parseInt(timeStr[1]));
  }
}
