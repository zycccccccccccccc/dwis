package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.PitRecords;

@Data
public class PitRecordsOutRequest {

  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date outPitDTAct;
  @NotEmpty
  private String craneOutId;


  public PitRecords convert2Model() {
    PitRecords target = new PitRecords();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
