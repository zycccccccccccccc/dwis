package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.Date;
import lombok.Data;

@Data
public class FinalCheckReworkDateTotalRecord {

  private Date opeDT;
  private Integer finalCheckTimes;
}
