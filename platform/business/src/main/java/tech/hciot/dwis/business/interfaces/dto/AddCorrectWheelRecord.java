package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.CorrectWheelRecord;

@Data
public class AddCorrectWheelRecord {

  @NotEmpty
  private String inspectorId;
  @NotEmpty
  private String wheelSerial;

  private String holdCode;
  private String reworkCode;
  private String scrapCode;
  private String cihenCode;
  private Integer confirmedScrap;
  private String formerCheckCode;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date formerStockDate;
  private String formerShippedNo;
  private String memo;

  public CorrectWheelRecord convert2Model() {
    CorrectWheelRecord target = new CorrectWheelRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
