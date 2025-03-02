package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.HiHeatPreworkRecord;

@Data
public class HeatPreworkRequest {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @NotNull
  private Date highDate;
  @NotNull
  private Integer furNo;
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
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date treadQuenchDelay;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date treadTimeChecked;
  @JsonFormat(pattern = "mm:ss", timezone = "GMT+8")
  private Date sprayTime;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date sprayTimeChecked;
  private BigDecimal waterPressure;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date waterPressureTimeChecked;
  private Integer waterTemp;
  @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
  private Date waterTempTimeChecked;

  public HiHeatPreworkRecord convert2Model() {
    HiHeatPreworkRecord target = new HiHeatPreworkRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
