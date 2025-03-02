package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.Date;
import lombok.Data;

@Data
public class FinalCheckReworkLineTotalRecord {

  private Date opeDT;
  private String xh;
  private Integer finalCheckTimes;
}
