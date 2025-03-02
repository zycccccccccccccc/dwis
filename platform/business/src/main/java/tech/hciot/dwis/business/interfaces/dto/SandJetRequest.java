package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.SandJetRecord;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SandJetRequest {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @NotNull
  private Integer preShiftId;
  @NotNull
  private String operatorId;
  @NotNull
  private String graphite;
  private String wheelSerial;
  @NotNull
  private Integer lineNo;
  @NotNull
  private String metalMold;
  @NotNull
  private BigDecimal metalTemp;
  @NotNull
  private Integer co2HardeningTime;

  public SandJetRecord convert2Model() {
    SandJetRecord target = new SandJetRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
