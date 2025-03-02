package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.LowHeatPreworkRecord;

@Data
public class LowPreworkRequest {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @NotNull
  private Date lowDate;
  @NotNull
  private Integer shift;
  @NotEmpty
  private String supervisor;
  @NotEmpty
  private String operator;
  @NotEmpty
  private String targetTemp;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date actualCycle;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date timeChecked;

  public LowHeatPreworkRecord convert2Model() {
    LowHeatPreworkRecord target = new LowHeatPreworkRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
