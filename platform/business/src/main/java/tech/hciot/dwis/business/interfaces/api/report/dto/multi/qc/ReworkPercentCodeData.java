package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.List;
import lombok.Data;

@Data
public class ReworkPercentCodeData {

  private String reworkCode;
  private List<ReworkPercentRecord> detail;
}
