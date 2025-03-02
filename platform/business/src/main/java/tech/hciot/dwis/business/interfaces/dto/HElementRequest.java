package tech.hciot.dwis.business.interfaces.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import tech.hciot.dwis.business.domain.model.HElementRecord;

@Data
public class HElementRequest {

  private String opeId;
  private Date castDate;
  private Integer heatRecordId;
  private Integer furnaceNo;
  private Integer heatSeq;
  private Integer tapSeq;
  private Integer h;


  public HElementRecord convert2Model() {
    HElementRecord target = new HElementRecord();
    BeanUtils.copyProperties(this, target, "h");
    target.setH(new BigDecimal(h / 100d));
    return target;
  }
}
