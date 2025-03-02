package tech.hciot.dwis.business.interfaces.api.report.dto.multi.qc;

import java.util.Date;
import lombok.Data;

@Data
public class ScrapLadleSeqTemp {

  private Integer xh;
  private String ladleRecordKey;
  private Date pourDT;
  private String wheelSerial;
}
