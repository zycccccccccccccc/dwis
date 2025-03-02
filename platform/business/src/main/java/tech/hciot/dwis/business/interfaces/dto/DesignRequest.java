package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.Design;

@Data
public class DesignRequest {

  @NotEmpty
  private String design;
  private String typeKxsj;
  @NotEmpty
  private String steelClass;
  private Integer balanceCheck;
  @NotEmpty
  private Integer internal;
  private String drawingNo;
  private String approbationNo;
  private BigDecimal weight;
  @NotEmpty
  private Integer enabled;
  private String memo;

  public Design convert2Model() {
    Design target = new Design();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
