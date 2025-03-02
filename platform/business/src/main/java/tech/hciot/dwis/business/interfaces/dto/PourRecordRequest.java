package tech.hciot.dwis.business.interfaces.dto;

import java.util.Date;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.PourRecord;

@Data
public class PourRecordRequest {

  private Date castDate;
  private String design;
  @NotEmpty
  private String wheelSerial;
  @NotEmpty
  private String dragNo;
  private String coreSetterId1;
  private String batchNo;


  public PourRecord convert2Model() {
    PourRecord target = new PourRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
