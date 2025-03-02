package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.PourRecord;

@Data
public class PourRecordForPitRequest {

  @NotNull
  private Integer pourId;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date openTimeAct;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
  private Date inPitDateTime;
  private String scrapCode;
  private Integer xrayReq;
  private Integer vibrateWheel;
  private Integer offPants;

  public PourRecord convert2Model() {
    PourRecord target = new PourRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
