package tech.hciot.dwis.business.interfaces.api.report.dto.multi.machine;

import java.util.List;
import lombok.Data;

@Data
public class QuantityData<T, R> {

  private T total;
  private List<R> record;
}
