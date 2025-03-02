package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.Graphite;

@Data
public class GraphiteRequest {

  @NotNull
  private Integer graphiteKey;
  @NotEmpty
  private String supplierId;
  private BigDecimal height;


  public Graphite convert2Model() {
    Graphite target = new Graphite();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
