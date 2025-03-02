package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.MoldPreShiftRecord;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MoldPreShiftRequest {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @NotNull
  private Date moldDate;
  @NotNull
  private Integer cd;
  @NotNull
  private Integer shift;
  @NotEmpty
  private String inspectorId;
  @NotNull
  private BigDecimal siteTemp;
  @NotNull
  private BigDecimal siteHumidity;

  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date jetBarrelCleanTime1;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date jetBarrelCleanTime2;

  public MoldPreShiftRecord convert2Model() {
    MoldPreShiftRecord target = new MoldPreShiftRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
