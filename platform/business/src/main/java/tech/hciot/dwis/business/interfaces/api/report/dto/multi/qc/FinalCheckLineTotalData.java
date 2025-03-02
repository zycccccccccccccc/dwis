package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class FinalCheckLineTotalData {

  private String xh;
  private Integer checkTimes;
  private List<String> detail;
}
