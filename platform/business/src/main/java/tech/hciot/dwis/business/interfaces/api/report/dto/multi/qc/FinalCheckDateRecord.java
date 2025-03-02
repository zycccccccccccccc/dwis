package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class FinalCheckDateRecord {

  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  private Date date;
  private FinalCheckTotalData total;
  private List<FinalCheckLineTotalData> line;
}
