package tech.hciot.dwis.business.domain.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CustomerDoubleSerialize extends JsonSerializer<BigDecimal> {

  private DecimalFormat df = new DecimalFormat("0.000");

  @Override
  public void serialize(BigDecimal arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
    if (arg0 != null) {
      arg1.writeNumber(df.format(arg0.doubleValue()));
    }
  }
}
