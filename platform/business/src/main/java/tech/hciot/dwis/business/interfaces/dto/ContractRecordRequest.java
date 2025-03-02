package tech.hciot.dwis.business.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.ContractRecord;

@Data
public class ContractRecordRequest {

  @NotEmpty
  private String contractNo;
  @NotEmpty
  private String design;
  @NotEmpty
  private String customerId;
  @NotNull
  private Integer contractSum;
  @NotNull
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date startDate;
  @NotEmpty
  private String operator;


  public ContractRecord convert2Model() {
    ContractRecord target = new ContractRecord();
    BeanUtils.copyProperties(this, target);
    return target;
  }
}
